package defuse;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import soot.Body;
import soot.BodyTransformer;
import soot.Unit;
import soot.Value;
import soot.jimple.Stmt;
import soot.jimple.internal.JimpleLocalBox;
import soot.jimple.internal.VariableBox;
import soot.jimple.toolkits.annotation.logic.Loop;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.LoopNestTree;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.LocalUses;
import soot.toolkits.scalar.SimpleLocalDefs;
import soot.toolkits.scalar.SimpleLocalUses;
import soot.toolkits.scalar.UnitValueBoxPair;

public class RDTransformer extends BodyTransformer {

	private Map<Value, Set<Unit>> mapDefUseChains, reduceDefUseChains;
	private Value reduceKey, reduceValue;

	public RDTransformer() {
		setMapDefUseChains(new LinkedHashMap<>());
		setReduceDefUseChains(new LinkedHashMap<>());
	}

	@Override
	protected void internalTransform(Body body, String arg1, @SuppressWarnings("rawtypes") Map arg2) {
		String methodName = body.getMethod().getName();
		// 4161 means the modifier is volatile which should be skipped
		if (body.getMethod().getModifiers() == 4161)
			return;
		if (methodName.equals("reduce")) {
			setReduceKey(body.getParameterLocal(0));
			setReduceValue(body.getParameterLocal(1));
			//System.out.println("Reduce Key: " + body.getParameterLocal(0));
			//System.out.println("Reduce Value: " + body.getParameterLocal(1));
		}
		if (methodName.equals("map") || methodName.equals("reduce")) {
			UnitGraph cfg = new BriefUnitGraph(body);
			SimpleLocalDefs defs = new SimpleLocalDefs(cfg);
			LocalUses uses = new SimpleLocalUses(body, defs);
			
			Map<Value, Set<Unit>> defUseChains = methodName.equals("map") ? mapDefUseChains : reduceDefUseChains;
			for (Unit unit : body.getUnits()) {
				for (Object unitValue : uses.getUsesOf(unit)) {
					UnitValueBoxPair usePair = (UnitValueBoxPair) unitValue;
					Value defValue = usePair.getValueBox().getValue();
					Set<Unit> useSet = defUseChains.getOrDefault(defValue, new LinkedHashSet<>());
					useSet.add(usePair.getUnit());
					defUseChains.put(defValue, useSet);
				}
			}
			// debug info; delete later
			for (Value key : defUseChains.keySet()) {
				System.out.println("def: " + key);
				for (Unit use : defUseChains.get(key)) {
					System.out.println("use: " + use);
					for (Object valueBox : use.getUseAndDefBoxes()) {
						if (valueBox instanceof VariableBox || valueBox instanceof JimpleLocalBox)
							System.out.println(valueBox);
					}
				}
				System.out.println();
			}
			LoopNestTree loopNestTree = new LoopNestTree(body);
			for (Loop loop : loopNestTree) {
	            System.out.print("Found a loop: " + loop.getHead().getJavaSourceStartLineNumber() + "-");
	            //System.out.println(loop.getBackJumpStmt().getJavaSourceStartLineNumber());
	            for (Stmt exit : loop.getLoopExits()) {
	            	for (Stmt target : loop.targetsOfLoopExit(exit)) {
	            		System.out.println("target line: " + target.getJavaSourceStartLineNumber());
	            	}
	            	//System.out.print(exit.getJavaSourceStartLineNumber() + ",");
	            }
	            System.out.println();
	        }
		}
	}

	public Map<Value, Set<Unit>> getMapDefUseChains() {
		return mapDefUseChains;
	}

	public void setMapDefUseChains(Map<Value, Set<Unit>> mapDefUseChains) {
		this.mapDefUseChains = mapDefUseChains;
	}

	public Map<Value, Set<Unit>> getReduceDefUseChains() {
		return reduceDefUseChains;
	}

	public void setReduceDefUseChains(Map<Value, Set<Unit>> reduceDefUseChains) {
		this.reduceDefUseChains = reduceDefUseChains;
	}

	public Value getReduceKey() {
		return reduceKey;
	}

	public void setReduceKey(Value reduceKey) {
		this.reduceKey = reduceKey;
	}

	public Value getReduceValue() {
		return reduceValue;
	}

	public void setReduceValue(Value reduceValue) {
		this.reduceValue = reduceValue;
	}
	
}
