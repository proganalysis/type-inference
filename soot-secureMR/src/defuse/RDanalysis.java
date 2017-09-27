package defuse;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import soot.BooleanType;
import soot.IntType;
import soot.PackManager;
import soot.Transform;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.internal.JimpleLocalBox;
import soot.jimple.internal.VariableBox;

public class RDanalysis {
	
	private Set<Reference> sources;
	private Map<Value, Set<Unit>> defUseChains, mapDefUseChains, reduceDefUseChains;
	private Map<Value, Reference> sensitiveValues;
	private Set<String> keyBucket, detBucket, opeBucket, ignoreBucket;
	private String detIgnore;
	private RDTransformer transformer;
	
	public RDanalysis(RDTransformer transformer) {
		this.transformer = transformer;
		mapDefUseChains = transformer.getMapDefUseChains();
		reduceDefUseChains = transformer.getReduceDefUseChains();
		defUseChains = Stream.of(mapDefUseChains, reduceDefUseChains)
				.flatMap(map -> map.entrySet().stream())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
						(v1, v2) -> new HashSet<>()));
		sensitiveValues = new HashMap<>();
		sources = new HashSet<>();
		keyBucket = new HashSet<>();
		detBucket = new HashSet<>();
		opeBucket = new HashSet<>();
		ignoreBucket = new HashSet<>();
		detIgnore = "<java.lang.String: boolean equals(java.lang.Object)>(\"\")";
		
		keyBucket.add("<org.apache.hadoop.mapred.OutputCollector: void collect(java.lang.Object,java.lang.Object)>");
		keyBucket.add("<org.apache.hadoop.mapreduce.TaskInputOutputContext: void write(java.lang.Object,java.lang.Object)");
		
		detBucket.add("<java.lang.String: boolean equals(java.lang.Object)>");
		detBucket.add("<java.util.List: boolean contains(java.lang.Object)>");
		
		opeBucket.add("<java.util.Collections: void sort(java.util.List)>");
		
		ignoreBucket.add("<java.util.List: int size()>");
		ignoreBucket.add("<java.lang.String: int length()>");
		ignoreBucket.add("<java.lang.String: int indexOf(java.lang.String)>");
	}
	
	public void propagate(Set<String> mSources, Set<String> rSources) {
		// propagate sensitivity; BFS; add children
		for (Value defValue : mapDefUseChains.keySet()) {
			if (mSources.contains(defValue.toString())) {
				Reference ref = sensitiveValues.getOrDefault(defValue, new Reference(defValue));
				sensitiveValues.put(defValue, ref);
				ref.setInMap(true);
				sources.add(ref);
			}
		}
		for (Value defValue : reduceDefUseChains.keySet()) {
			if (rSources.contains(defValue.toString()))
				sensitiveValues.put(defValue, sensitiveValues.getOrDefault(defValue, new Reference(defValue)));
		}
		
		Queue<Value> queue = new LinkedList<>();
		Set<Value> visited = new HashSet<>();
		for (Value source : sensitiveValues.keySet())
			queue.add(source);
		while (!queue.isEmpty()) {
			Value defValue = queue.remove();
			if (visited.contains(defValue))
				continue;
			Reference defRef = sensitiveValues.get(defValue);
			for (Unit useUnit : defUseChains.get(defValue)) {
				boolean shouldIgnore = false;
				for (String ignore : ignoreBucket)
					if (useUnit.toString().contains(ignore)) {
						shouldIgnore = true;
						break;
					}
				if (shouldIgnore) continue;
				for (Object valueBox : useUnit.getUseAndDefBoxes()) {
					if (valueBox instanceof VariableBox || valueBox instanceof JimpleLocalBox) {
						Value value = ((ValueBox) valueBox).getValue();
						if (value.getType() instanceof BooleanType) continue;
						sensitiveValues.put(value, sensitiveValues.getOrDefault(value, new Reference(value)));
						sensitiveValues.get(value).setInMap(defRef.isInMap());
						queue.add(value);
						if (value != defValue)
							defRef.addChild(value);
					}
				}
			}
			visited.add(defValue);
		}
	}
	
	public void linkKey() {
		Value reduceKey = transformer.getReduceKey();
		for (Unit useUnit : reduceDefUseChains.get(reduceKey)) {
			for (Object valueBox : useUnit.getUseAndDefBoxes()) {
				Value value = ((ValueBox) valueBox).getValue();
				if (sensitiveValues.containsKey(value)) {
					for (Reference mapKey : getMapKeys()) {
						mapKey.addChild(value);
					}
				}
			}
		}
	}
	
	public Set<Reference> getMapKeys() {
		Set<Reference> mapKeys = new HashSet<>();
		for (Value defValue : sensitiveValues.keySet()) {
			for (Unit useUnit : mapDefUseChains.getOrDefault(defValue, new HashSet<>())) {
				for (String keyUnitStr : keyBucket) {
					if (useUnit.toString().contains(keyUnitStr)) {
						String useUnitStr = useUnit.toString();
						int indexBegin = useUnitStr.lastIndexOf('(') + 1;
						int indexEnd = useUnitStr.lastIndexOf(',');
						if (useUnitStr.substring(indexBegin, indexEnd).equals(defValue.toString())) {
							Reference ref = sensitiveValues.get(defValue);
							mapKeys.add(ref);
						}
					}
				}
			}
		}
		return mapKeys;
	}
	
	public void linkValue() {
		Value reduceValue = transformer.getReduceValue();
		for (Unit useUnit : reduceDefUseChains.get(reduceValue)) {
			for (Object valueBox : useUnit.getUseAndDefBoxes()) {
				Value value = ((ValueBox) valueBox).getValue();
				if (sensitiveValues.containsKey(value)) {
					for (Reference mapValue : getMapValues()) {
						mapValue.addChild(value);
					}
				}
			}
		}
	}
	
	public Set<Reference> getMapValues() {
		Set<Reference> mapValues = new HashSet<>();
		for (Value defValue : sensitiveValues.keySet()) {
			for (Unit useUnit : mapDefUseChains.getOrDefault(defValue, new HashSet<>())) {
				for (String keyUnitStr : keyBucket) {
					if (useUnit.toString().contains(keyUnitStr)) {
						String useUnitStr = useUnit.toString();
						int indexBegin = useUnitStr.lastIndexOf(',') + 2;
						int indexEnd = useUnitStr.lastIndexOf(')');
						if (useUnitStr.substring(indexBegin, indexEnd).equals(defValue.toString())) {
							Reference ref = sensitiveValues.get(defValue);
							mapValues.add(ref);
						}
					}
				}
			}
		}
		return mapValues;
	}
	
	public void addOperations() {
		// Add operations and check conversions
		for (Value defValue : sensitiveValues.keySet()) {
			Reference ref = sensitiveValues.get(defValue);
			for (Unit useUnit : defUseChains.get(defValue)) {
				for (String detUnitStr : detBucket) {
					if (useUnit.toString().contains(detUnitStr))
						if (!useUnit.toString().contains(detIgnore))
							ref.addOperation(Operation.DET);
				}
				for (String keyUnitStr : keyBucket) {
					if (useUnit.toString().contains(keyUnitStr)) {
						String useUnitStr = useUnit.toString();
						int indexBegin = useUnitStr.lastIndexOf('(') + 1;
						int indexEnd = useUnitStr.lastIndexOf(',');
						if (useUnitStr.substring(indexBegin, indexEnd).equals(defValue.toString()))
							ref.addOperation(Operation.DET);
					}
				}
				for (String opeUnitStr : opeBucket) {
					if (useUnit.toString().contains(opeUnitStr) && defValue.getType() instanceof IntType)
						ref.addOperation(Operation.OPE);
				}
			}
		}
	}
	
	public void propagateOperations() {
		// propagate operations bottom up
		boolean changed = true;
		while (changed) {
			changed = false;
			for (Value defValue : sensitiveValues.keySet()) {
				Reference defRef = sensitiveValues.get(defValue);
				for (Value child : defRef.getChildren()) {
					for (Operation operation : sensitiveValues.get(child).getOperations()) {
						if (defRef.addOperation(operation))
							changed = true;
					}
				}
			}
		}
	}

	public Map<Value, Reference> getSensitiveValues() {
		return sensitiveValues;
	}
	
	public Set<Reference> getSources() {
		return sources;
	}

	public void analyze(Set<String> mSources, Set<String> rSources) {
		propagate(mSources, rSources);
		linkKey();
		linkValue();
		addOperations();
		propagateOperations();
	}

	public static void main(String[] args) {

		args = new String[] {"-cp", "adjList.jar", "AdjList$MapClass", "AdjList$Reduce", "-allow-phantom-refs",
				"-p", "jb", "use-original-names:true", "-f", "jimple"};
		String[] mSources = new String[] {"outEdge", "inEdge"};
		String[] rSources = new String[] {"vertex"};
		RDTransformer transformer = new RDTransformer();
		PackManager.v().getPack("jtp").add(new Transform("jtp.rd", transformer));
		soot.Main.main(args);
		RDanalysis analysis = new RDanalysis(transformer);
		analysis.analyze(new HashSet<>(Arrays.asList(mSources)), new HashSet<>(Arrays.asList(rSources)));
		for (Value defValue : analysis.getSensitiveValues().keySet()) {
			System.out.println(analysis.getSensitiveValues().get(defValue));
		}
		System.out.println();
		for (Reference source : analysis.getSources()) {
			System.out.println(source.getValue() + ": " + source.getOperations());
		}
	}

}
