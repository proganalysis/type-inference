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
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
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
	//private Map<Reference, Set<Reference>> defUseChains;
	private Map<Value, Set<Unit>> defUseChains;
	private Map<Value, Reference> sensitiveValues;
	private Map<String, Set<String>> detContainers;

	public RDTransformer(HashSet<String> sources) {
		this.sources = sources;
		defUseChains = new HashMap<>();
		sensitiveValues = new HashMap<>();
		detContainers = new HashMap<>();
		
		Set<String> methods = new HashSet<>();
		methods.add("contains");
		detContainers.put("java.util.ArrayList", methods);
		methods = new HashSet<>();
		methods.add("contains");
		methods.add("add");
		detContainers.put("java.util.HashSet", methods);
		detContainers.put("java.util.TreeSet", methods);
		methods = new HashSet<>();
		methods.add("containsKey");
		methods.add("put");
		detContainers.put("java.util.HashMap", methods);
		detContainers.put("java.util.LinkedHashMap", methods);
		methods = new HashSet<>();
		methods.add("equals");
		detContainers.put("java.lang.String", methods);
		methods = new HashSet<>();
		methods.add("write");
		detContainers.put("org.apache.hadoop.mapreduce.Mapper$Context", methods);
		methods = new HashSet<>();
		methods.add("collect");
		detContainers.put("org.apache.hadoop.mapred.OutputCollector", methods);
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
				for (Unit useUnit : defUseChains.get(defValue)) {
					if (useUnit instanceof InvokeStmt) {
						InvokeExpr invokeExpr = ((InvokeStmt) useUnit).getInvokeExpr();
						SootMethod method = invokeExpr.getMethod();
						String className = method.getDeclaringClass().getName();
						if (detContainers.containsKey(className) &&
								detContainers.get(className).contains(method.getName())) {
							Reference ref = sensitiveValues.get(defValue);
							Set<Operation> operations = ref.getOperations();
							if (operations.contains(Operation.AH) || operations.contains(Operation.MH)) {
								
								System.out.println("Conversion: " + operations.iterator().next() + " -> DET");
							}
							ref.addOperation(Operation.DET);
						}
					}
				}
			}
			
			// propagate operations bottom up
			boolean changed = true;
			while (changed) {
				changed = false;
				for (Value defValue : sensitiveValues.keySet()) {
					Reference defRef = sensitiveValues.get(defValue);
					for (Value child : defRef.getChildren()) {
						for (Operation ope : sensitiveValues.get(child).getOperations()) {
							changed = defRef.addOperation(ope);
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
