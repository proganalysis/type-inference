package defuse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import soot.Body;
import soot.BodyTransformer;
import soot.Local;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.AssignStmt;
import soot.jimple.DefinitionStmt;
import soot.jimple.IfStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.VirtualInvokeExpr;
import soot.jimple.internal.JimpleLocalBox;
import soot.jimple.internal.VariableBox;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.LocalDefs;
import soot.toolkits.scalar.LocalUses;
import soot.toolkits.scalar.SimpleLocalDefs;
import soot.toolkits.scalar.SimpleLocalUses;
import soot.toolkits.scalar.UnitValueBoxPair;

public class RDTransformer extends BodyTransformer {

	private HashSet<String> sources;
	private LocalUses uses;
	//private Map<Reference, Set<Reference>> defUseChains;
	private Map<Value, Set<Unit>> defUseChains;
	private Map<Value, Reference> referenceMap;

	public RDTransformer(HashSet<String> sources) {
		this.sources = sources;
		defUseChains = new HashMap<>();
		referenceMap = new HashMap<>();
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
		if (methodName.equals("map")) {
			for (Unit unit : body.getUnits()) {
				for (Object unitValue : uses.getUsesOf(unit)) {
					UnitValueBoxPair usePair = (UnitValueBoxPair) unitValue;
					Value defValue = usePair.getValueBox().getValue();
					if (sources.contains(defValue.toString()))
						referenceMap.put(defValue, referenceMap.getOrDefault(defValue, new Reference(defValue)));
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
			// propagate; BFS
			Queue<Value> queue = new LinkedList<>();
			Set<Value> visited = new HashSet<>();
			for (Value source : referenceMap.keySet())
				queue.add(source);
			while (!queue.isEmpty()) {
				Value defValue = queue.remove();
				if (visited.contains(defValue))
					continue;
				for (Unit useUnit : defUseChains.get(defValue)) {
					for (Object valueBox : useUnit.getUseAndDefBoxes()) {
						if (valueBox instanceof VariableBox || valueBox instanceof JimpleLocalBox) {
							Value value = ((ValueBox) valueBox).getValue();
							referenceMap.put(value, referenceMap.getOrDefault(value, new Reference(value)));
							queue.add(value);
						}
					}
				}
				visited.add(defValue);
			}
			
			for (Reference ref : referenceMap.values()) {
				System.out.println(ref);
			}
			
			//if (methodName.equals("map") || methodName.equals("reduce")) {
			//buildGraph(body, sourceNodes);
		}
	}
	
	private Reference getReference(Value value) {
		Reference ref = referenceMap.getOrDefault(value, new Reference(value));
//		if (sources.contains(value.toString()))
//			ref.setSensitive(true);
		referenceMap.put(value, ref);
		return ref;
	}

//	private void buildGraph(Body body, List<Reference> nodes) {
//		//List<TaintNode> nodeList = new ArrayList<>();
//		for (Unit unit : body.getUnits()) {
//			for (ValueBox defValueBox : unit.getDefBoxes()) {
//				Value defValue = defValueBox.getValue();
//				for (Reference defNode : nodes) {
//					if (defNode.getValue() == defValue) {
//						System.out.println(defValue);
//						for (Object use : uses.getUsesOf(unit)) {
//							UnitValueBoxPair usePair = (UnitValueBoxPair) use;
//							Unit useUnit = usePair.getUnit();
//							System.out.println(useUnit);
//							if (unit instanceof AssignStmt) {
//								Value rightValue = ((AssignStmt) unit).getRightOp();
//								if (rightValue instanceof VirtualInvokeExpr) {
//									Reference node = new Reference(((AssignStmt) unit).getLeftOp());
//									defNode.getChildren().add(node);
//									//nodeList.add(node);
//								}
//							} else if (unit instanceof InvokeStmt) {
//								InvokeExpr invokeExpr = ((InvokeStmt) unit).getInvokeExpr();
//								if (invokeExpr instanceof SpecialInvokeExpr) {
//									Value baseValue = ((SpecialInvokeExpr) invokeExpr).getBase();
//									Reference node = new Reference(baseValue);
//									defNode.getChildren().add(node);
//									//nodeList.add(node);
//								}
//							}
//						}
//					}
//				}
//			}
//		}
//	}

}
