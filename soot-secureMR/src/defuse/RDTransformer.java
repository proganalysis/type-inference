package defuse;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import soot.Body;
import soot.BodyTransformer;
import soot.Unit;
import soot.Value;
import soot.jimple.Stmt;
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
	private ArrayList<Integer> mapLoops, reduceLoops;
	private String mapClass, reduceClass;

	public RDTransformer() {
		setMapDefUseChains(new LinkedHashMap<>());
		setReduceDefUseChains(new LinkedHashMap<>());
		setMapLoops(new ArrayList<>());
		setReduceLoops(new ArrayList<>());
	}

	@Override
	protected synchronized void internalTransform(Body body, String arg1, @SuppressWarnings("rawtypes") Map arg2) {
		String methodName = body.getMethod().getName();
		// 4161 means the modifier is volatile which should be skipped
		if (body.getMethod().getModifiers() == 4161)
			return;
		if (!methodName.equals("map") && !methodName.equals("reduce") && !methodName.equals("getPartition"))
			return;
		Map<Value, Set<Unit>> defUseChains;
		ArrayList<Integer> loops;
		if (methodName.equals("reduce") || methodName.equals("getPartition")) {
			setReduceKey(body.getParameterLocal(0));
			setReduceValue(body.getParameterLocal(1));
			setReduceClass(body.getMethod().getDeclaringClass().getName());
			defUseChains = reduceDefUseChains;
			loops = reduceLoops;
		} else {
			setMapClass(body.getMethod().getDeclaringClass().getName());
			defUseChains = mapDefUseChains;
			loops = mapLoops;
		}
		UnitGraph cfg = new BriefUnitGraph(body);
		SimpleLocalDefs defs = new SimpleLocalDefs(cfg);
		LocalUses uses = new SimpleLocalUses(body, defs);

		for (Unit unit : body.getUnits()) {
			for (Object unitValue : uses.getUsesOf(unit)) {
				UnitValueBoxPair usePair = (UnitValueBoxPair) unitValue;
				Value defValue = usePair.getValueBox().getValue();
				Set<Unit> useSet = defUseChains.getOrDefault(defValue, new LinkedHashSet<>());
				useSet.add(usePair.getUnit());
				defUseChains.put(defValue, useSet);
			}
		}
		LoopNestTree loopNestTree = new LoopNestTree(body);
		for (Loop loop : loopNestTree) {
			int start = loop.getHead().getJavaSourceStartLineNumber();
			for (Stmt exit : loop.getLoopExits()) {
				for (Stmt target : loop.targetsOfLoopExit(exit)) {
					int end = target.getJavaSourceStartLineNumber();
					if (end > start) {
						loops.add(start);
						loops.add(end);
					}
				}
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

	public ArrayList<Integer> getMapLoops() {
		return mapLoops;
	}

	public void setMapLoops(ArrayList<Integer> loops) {
		this.mapLoops = loops;
	}

	public ArrayList<Integer> getReduceLoops() {
		return reduceLoops;
	}

	public void setReduceLoops(ArrayList<Integer> reduceLoops) {
		this.reduceLoops = reduceLoops;
	}

	public String getMapClass() {
		return mapClass;
	}

	public void setMapClass(String mapClass) {
		this.mapClass = mapClass;
	}

	public String getReduceClass() {
		return reduceClass;
	}

	public void setReduceClass(String reduceClass) {
		this.reduceClass = reduceClass;
	}
	
}
