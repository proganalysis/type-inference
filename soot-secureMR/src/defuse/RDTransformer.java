package defuse;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import soot.Body;
import soot.BodyTransformer;
import soot.BooleanType;
import soot.IntType;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.internal.JimpleLocalBox;
import soot.jimple.internal.VariableBox;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.LocalUses;
import soot.toolkits.scalar.SimpleLocalDefs;
import soot.toolkits.scalar.SimpleLocalUses;
import soot.toolkits.scalar.UnitValueBoxPair;

public class RDTransformer extends BodyTransformer {

	private HashSet<String> sources;
	private LocalUses uses;
	private Map<Value, Set<Unit>> defUseChains;
	private Map<Value, Reference> sensitiveValues;
	private Set<String> keyBucket, detBucket, opeBucket, ignoreBucket;
	private String detIgnore;

	public RDTransformer(HashSet<String> sources) {
		this.sources = sources;
		defUseChains = new HashMap<>();
		sensitiveValues = new HashMap<>();
		keyBucket = new HashSet<>();
		detBucket = new HashSet<>();
		opeBucket = new HashSet<>();
		ignoreBucket = new HashSet<>();
		detIgnore = "<java.lang.String: boolean equals(java.lang.Object)>(\"\")";
		
		keyBucket.add("<org.apache.hadoop.mapred.OutputCollector: void collect(java.lang.Object,java.lang.Object)>");
		keyBucket.add("<org.apache.hadoop.mapreduce.Mapper$Context: void write"); //TODO
		
		detBucket.add("<java.lang.String: boolean equals(java.lang.Object)>");
		detBucket.add("<java.util.List: boolean contains(java.lang.Object)>");
		
		opeBucket.add("<java.util.Collections: void sort(java.util.List)>");
		
		ignoreBucket.add("<java.util.List: int size()>");
		ignoreBucket.add("<java.lang.String: int length()>");
		ignoreBucket.add("<java.lang.String: int indexOf(java.lang.String)>");
	}

	@Override
	protected void internalTransform(Body body, String arg1, @SuppressWarnings("rawtypes") Map arg2) {
		UnitGraph cfg = new BriefUnitGraph(body);
		SimpleLocalDefs defs = new SimpleLocalDefs(cfg);
		uses = new SimpleLocalUses(body, defs);
		String methodName = body.getMethod().getName();
		// 4161 means the modifier is volatile which should be skipped
		if (body.getMethod().getModifiers() == 4161)
			return;
		//if (methodName.equals("map")) {
		if (methodName.equals("reduce")) {
			for (Unit unit : body.getUnits()) {
				for (Object unitValue : uses.getUsesOf(unit)) {
					UnitValueBoxPair usePair = (UnitValueBoxPair) unitValue;
					Value defValue = usePair.getValueBox().getValue();
					if (sources.contains(defValue.toString()))
						sensitiveValues.put(defValue, sensitiveValues.getOrDefault(defValue, new Reference(defValue)));
					//Reference defReference = getReference(defValue);
					//Set<Reference> useSet = defUseChains.getOrDefault(defReference, new HashSet<>());
					Set<Unit> useSet = defUseChains.getOrDefault(defValue, new HashSet<>());
					useSet.add(usePair.getUnit());
//					for (Object valueBox : usePair.getUnit().getUseAndDefBoxes()) {
//						Value value = ((ValueBox) valueBox).getValue();
//						if (value instanceof Local && defValue != value) {
//							Reference useRef = getReference(value);
//							//if (!defUseChains.getOrDefault(useRef, new HashSet<>()).contains(defReference))
//								useSet.add(useRef);
//						}
//					}
					defUseChains.put(defValue, useSet);
					//defUseChains.put(defReference, useSet);
				}
			}
			//for (Reference key : defUseChains.keySet()) {
			// debug info; delete later
			for (Value key : defUseChains.keySet()) {
				System.out.println("def: " + key);
				//for (Reference use : defUseChains.get(key)) {
				for (Unit use : defUseChains.get(key)) {
					System.out.println("use: " + use);
					for (Object valueBox : use.getUseAndDefBoxes()) {
						if (valueBox instanceof VariableBox || valueBox instanceof JimpleLocalBox)
							System.out.println(valueBox);
					}
				}
				System.out.println();
			}
			// propagate sensitivity; BFS; add children
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
							queue.add(value);
							if (value != defValue)
								defRef.addChild(value);
						}
					}
				}
				visited.add(defValue);
			}
			
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
//					if (useUnit instanceof InvokeStmt) {
//						InvokeExpr invokeExpr = ((InvokeStmt) useUnit).getInvokeExpr();
//						SootMethod method = invokeExpr.getMethod();
//						String className = method.getDeclaringClass().getName();
//						if (detContainers.containsKey(className) &&
//								detContainers.get(className).contains(method.getName())) {
//							Set<Operation> operations = ref.getOperations();
//							if (operations.contains(Operation.AH) || operations.contains(Operation.MH)) {
//								
//								System.out.println("Conversion: " + operations.iterator().next() + " -> DET");
//							}
//							ref.addOperation(Operation.DET);
//						}
//						if (useUnit.toString().contains("<org.apache.hadoop.mapred.OutputCollector: void collect(java.lang.Object,java.lang.Object)>")
//								&& (invokeExpr.getArg(0) == defValue)) {
//							ref.addOperation(Operation.DET);
//						}
//					}
				}
			}
			
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
			
			for (Value defValue : sensitiveValues.keySet()) {
				System.out.println(sensitiveValues.get(defValue));
			}
			
			//if (methodName.equals("map") || methodName.equals("reduce")) {
			//buildGraph(body, sourceNodes);
		}
	}

}
