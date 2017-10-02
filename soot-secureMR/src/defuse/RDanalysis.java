package defuse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import soot.BooleanType;
import soot.IntType;
import soot.PackManager;
import soot.Transform;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.AssignStmt;
import soot.jimple.BinopExpr;
import soot.jimple.IfStmt;
import soot.jimple.NeExpr;
import soot.jimple.internal.JimpleLocalBox;
import soot.jimple.internal.VariableBox;

public class RDanalysis {
	
	private Set<Reference> sources;
	private Map<Value, Set<Unit>> mapDefUseChains, reduceDefUseChains;
	private Map<Value, Set<Unit>> defUseChains;
	private Map<Value, Reference> sensitiveValues;
	private Set<String> keyBucket, detBucket, opeBucket, ignoreBucket, mathBucket;
	private String detIgnore;
	private RDTransformer transformer;
	private Set<Value> convertedValues;
	
	public RDanalysis(RDTransformer transformer) {
		this.transformer = transformer;
		mapDefUseChains = transformer.getMapDefUseChains();
		reduceDefUseChains = transformer.getReduceDefUseChains();
//		defUseChains = Stream.of(mapDefUseChains, reduceDefUseChains)
//				.flatMap(map -> map.entrySet().stream())
//				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
//						(v1, v2) -> new HashSet<>()));
		defUseChains = new LinkedHashMap<>();
		defUseChains.putAll(mapDefUseChains);
		defUseChains.putAll(reduceDefUseChains);
		
		sensitiveValues = new LinkedHashMap<>();
		convertedValues = new HashSet<>();
		sources = new HashSet<>();
		keyBucket = new HashSet<>();
		detBucket = new HashSet<>();
		opeBucket = new HashSet<>();
		ignoreBucket = new HashSet<>();
		mathBucket = new HashSet<>();
		
		mathBucket.add("<java.lang.Math: double sqrt(double)>");
		
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
	
	public void propagateMap(Set<String> mSources) {
		// propagate sensitivity; BFS; add children
		for (Value defValue : mapDefUseChains.keySet()) {
			if (mSources.contains(defValue.toString())) {
				Reference ref = sensitiveValues.getOrDefault(defValue, new Reference(defValue));
				sensitiveValues.put(defValue, ref);
				ref.setInMap(true);
				sources.add(ref);
			}
		}
		scanDefUseChains(mapDefUseChains);
	}

	private void scanDefUseChains(Map<Value, Set<Unit>> defUseChains) {
		Queue<Value> queue = new LinkedList<>();
		Set<Value> visited = new HashSet<>();
		for (Value source : sensitiveValues.keySet())
			queue.add(source);
		while (!queue.isEmpty()) {
			Value defValue = queue.remove();
			if (visited.contains(defValue))
				continue;
			Reference defRef = sensitiveValues.get(defValue);
			for (Unit useUnit : defUseChains.getOrDefault(defValue, new HashSet<>())) {
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

	public void propagateReduce(Set<String> rSources) {
		// propagate sensitivity; BFS; add children
		for (Value defValue : reduceDefUseChains.keySet()) {
			if (rSources.contains(defValue.toString()))
				sensitiveValues.put(defValue, sensitiveValues.getOrDefault(defValue, new Reference(defValue)));
		}
		scanDefUseChains(reduceDefUseChains);
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
			for (Unit useUnit : defUseChains.getOrDefault(defValue, new HashSet<>())) {
				for (String detUnitStr : detBucket) {
					if (useUnit.toString().contains(detUnitStr))
						if (!useUnit.toString().contains(detIgnore))
							ref.addOperation("DET");
				}
				for (String keyUnitStr : keyBucket) {
					if (useUnit.toString().contains(keyUnitStr)) {
						String useUnitStr = useUnit.toString();
						int indexBegin = useUnitStr.lastIndexOf('(') + 1;
						int indexEnd = useUnitStr.lastIndexOf(',');
						if (useUnitStr.substring(indexBegin, indexEnd).equals(defValue.toString()))
							ref.addOperation("DET");
					}
				}
				for (String opeUnitStr : opeBucket) {
					if (useUnit.toString().contains(opeUnitStr) && defValue.getType() instanceof IntType) {
						ref.addOperation("OPE");
					}
				}
				for (String opeUnitStr : mathBucket) {
					if (useUnit.toString().contains(opeUnitStr)) {
						System.out.println("Conversion: " + useUnit.getJavaSourceStartLineNumber() + "-" + useUnit);
						convertedValues.add(defValue);
						ref.clearOperations();
						clearOperations(ref);
						
					}
				}
				if (useUnit instanceof IfStmt) {
					Value condition = ((IfStmt) useUnit).getCondition();
					if (condition instanceof NeExpr) {
						if (((NeExpr) condition).getOp1() == defValue || ((NeExpr) condition).getOp2() == defValue) {
							ref.addOperation("DET");
						}
					}
				} else if (useUnit instanceof AssignStmt) {
						Value rhsOp = ((AssignStmt) useUnit).getRightOp();
						if (rhsOp instanceof BinopExpr) {
							String symbol = ((BinopExpr) rhsOp).getSymbol();
							Value op1 = ((BinopExpr) rhsOp).getOp1();
							Value op2 = ((BinopExpr) rhsOp).getOp2();
							switch (symbol) {
							case " + ":
							case " - ":
								if (ref.contains("MH")) {
									System.out.println("Conversion: " + useUnit.getJavaSourceStartLineNumber() + "-" + useUnit);
									convertedValues.add(defValue);
									ref.removeOperation("MH");
									removeOperations(ref, "MH");
								}
								ref.addOperation("AH");
								addOperations(ref, "AH");
								break;
							case " * ":
								if (sensitiveValues.containsKey(op1) && sensitiveValues.containsKey(op2)) {
									if (ref.contains("AH")) {
										System.out.println("Conversion: " + useUnit.getJavaSourceStartLineNumber() + "-" + useUnit);
										convertedValues.add(defValue);
										ref.removeOperation("AH");
										removeOperations(ref, "AH");
									}
									ref.addOperation("MH");
									addOperations(ref, "MH");
								} else {
									if (ref.contains("MH")) {
										System.out.println("Conversion: " + useUnit.getJavaSourceStartLineNumber() + "-" + useUnit);
										convertedValues.add(defValue);
										ref.removeOperation("MH");
										removeOperations(ref, "MH");
									}
									ref.addOperation("AH");
									addOperations(ref, "AH");
								}
								break;
							case " / ":
								if (sensitiveValues.containsKey(op1) && sensitiveValues.containsKey(op2)) {
									System.out.println("Conversion: " + useUnit.getJavaSourceStartLineNumber() + "-" + useUnit);
									convertedValues.add(defValue);
									ref.clearOperations();
									clearOperations(ref);
								} else {
									if (ref.contains("MH")) {
										System.out.println("Conversion: " + useUnit.getJavaSourceStartLineNumber() + "-" + useUnit);
										convertedValues.add(defValue);
										ref.removeOperation("MH");
										removeOperations(ref, "MH");
									}
									ref.addOperation("AH");
									addOperations(ref, "AH");
								}
								break;
							case " cmpl ":
								if (ref.contains("MH")) {
									System.out.println("Conversion: " + useUnit.getJavaSourceStartLineNumber() + "-" + useUnit);
									convertedValues.add(defValue);
									ref.addOperation("OPE");
									ref.removeOperation("MH");
									removeOperations(ref, "MH");
								}
								if (ref.contains("AH")) {
									System.out.println("Conversion: " + useUnit.getJavaSourceStartLineNumber() + "-" + useUnit);
									convertedValues.add(defValue);
									ref.addOperation("OPE");
									ref.removeOperation("AH");
									removeOperations(ref, "AH");
								}
							case " >> ":
							case " >>> ":
							}
						}
				}
			}
		}
	}

	private void clearOperations(Reference ref) {
		for (Value child : ref.getChildren()) {
			if (sensitiveValues.get(child).clearOperations())
				clearOperations(sensitiveValues.get(child));
		}
	}
	
	public void addOperations(Reference ref, String ope) {
		for (Value child : ref.getChildren()) {
			if (sensitiveValues.get(child).addOperation(ope))
				addOperations(sensitiveValues.get(child), ope);
		}
	}
	
	public void removeOperations(Reference ref, String ope) {
		//if (!ref.getOperations().contains(ope)) return;
		for (Value child : ref.getChildren()) {
			if (sensitiveValues.get(child).contains(ope)) {
				sensitiveValues.get(child).removeOperation(ope);
				removeOperations(sensitiveValues.get(child), ope);
			}
		}
//		Queue<Value> q = new LinkedList<>();
//		q.addAll(ref.getChildren());
//		Set<Value> visited = new HashSet<>();
//		visited.addAll(ref.getChildren());
//		while (!q.isEmpty()) {
//			Value headValue = q.poll();
//			if (sensitiveValues.get(headValue).removeOperation(ope)) {
//				
//			}
//			
//		}
	}
	
	public void propagateOperations() {
		// propagate operations bottom up
		boolean changed = true;
		while (changed) {
			changed = false;
			for (Value defValue : sensitiveValues.keySet()) {
				Reference defRef = sensitiveValues.get(defValue);
				for (Value child : defRef.getChildren()) {
					//if (convertedValues.contains(child)) continue;
					for (String operation : sensitiveValues.get(child).getOperations()) {
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
		propagateMap(mSources);
		propagateReduce(rSources);
		linkKey();
		linkValue();
		addOperations();
		propagateOperations();
	}

	public static void main(String[] args) {
		ArrayList<String> arguments = new ArrayList<>();
		String mapSources = "", reduceSources = "";
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-mapSources")) {
				mapSources = args[++i];
			} else if (args[i].equals("-reduceSources")) {
				reduceSources = args[++i];
			} else {
				arguments.add(args[i]);
			}
		}
//		String[] sootArgs = new String[] {"-cp", "adjList.jar", "AdjList$MapClass", "AdjList$Reduce", "-allow-phantom-refs",
//				"-p", "jb", "use-original-names:true", "-f", "jimple"};
		String[] sootArgs = new String[] {"-allow-phantom-refs", "--keep-line-number",
				"-p", "jb", "use-original-names:true", "-f", "jimple"};
//		String[] sootArgs = new String[] {"-allow-phantom-refs",
//				"-p", "jb", "use-original-names:true", "-f", "jimple"};
		Collections.addAll(arguments, sootArgs);
		//System.out.println(arguments);
		RDTransformer transformer = new RDTransformer();
		PackManager.v().getPack("jtp").add(new Transform("jtp.rd", transformer));
		soot.Main.main(arguments.toArray(new String[0]));
		
//		String[] mSources = new String[] {"outEdge", "inEdge"};
//		String[] rSources = new String[] {"vertex"};
		String[] mSources = mapSources.split(":");
		String[] rSources = reduceSources.split(":");
		RDanalysis analysis = new RDanalysis(transformer);
//		try {
//			Thread.sleep(20000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		analysis.analyze(new HashSet<>(Arrays.asList(mSources)), new HashSet<>(Arrays.asList(rSources)));
		for (Value defValue : analysis.getSensitiveValues().keySet()) {
			System.out.println(analysis.getSensitiveValues().get(defValue));
		}
		System.out.println();
		for (Reference source : analysis.getSources()) {
			System.out.println(source.getValue() + ": " + source.getOperations());
		}
		System.out.println();
	}

}
