/**
 * Copyright (C) 2013 Rohan Padhye
 * 
 * This library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 2.1 of the 
 * License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package vasco.soot.examples;

import java.io.File;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import soot.Local;
import soot.SceneTransformer;
import soot.SootField;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.BinopExpr;
import soot.jimple.FieldRef;
import soot.jimple.IfStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import vasco.DataFlowSolution;

/**
 * A Soot {@link SceneTransformer} for performing {@link JCryptAnalysis}.
 * 
 * @author Rohan Padhye
 */
public class AETransformer extends SceneTransformer {

	public AETransformer(String outputDir, String mrExt) {
		super();
		this.outputDir = outputDir;
		this.mrExt = mrExt;
	}

	private String outputDir;
	private String mrExt;
	public Map<String, Byte> aeResults = new HashMap<>();

	public Map<String, Byte> getAeResults() {
		return aeResults;
	}

	@Override
	protected void internalTransform(String arg0, @SuppressWarnings("rawtypes") Map arg1) {
		AEAnalysis analysis = new AEAnalysis();
		analysis.doAnalysis();
		DataFlowSolution<Unit, Map<Object, Byte>> solution = analysis.getMeetOverValidPathsSolution();
		try {
			PrintStream out = new PrintStream(outputDir + File.separator + "analysis-result-" + mrExt + ".txt");
			out.println("================================================================");
			for (SootMethod sootMethod : analysis.getMethods()) {
				if (!sootMethod.hasActiveBody())
					continue;
				out.println(sootMethod);
				for (Unit unit : sootMethod.getActiveBody().getUnits()) {
					out.println("----------------------------------------------------------------");
					out.println(unit);
					out.println("IN:  " + formatResults(solution.getValueBefore(unit)));
					out.println("OUT: " + formatResults(solution.getValueAfter(unit)));
					storeAeResults(solution.getValueBefore(unit), unit, sootMethod);
				}
				out.println("================================================================");
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addResult(Value v, SootMethod sm, Byte type) {
		if (type == null)
			return;
		String id = "";
		if (v instanceof FieldRef) {
			SootField sf = ((FieldRef) v).getField();
			id = sf.getSignature();
		} else if (v instanceof Local) {
			id = sm.getSignature() + "@" + v.toString();
		}
		aeResults.put(id, type);
	}

	private void storeAeResults(Map<Object, Byte> map, Unit unit, SootMethod sm) {
		if (unit instanceof AssignStmt) {
			// Get operands
			Value rhsOp = ((AssignStmt) unit).getRightOp();
			if (rhsOp instanceof BinopExpr) {
				Value op1 = ((BinopExpr) rhsOp).getOp1();
				Value op2 = ((BinopExpr) rhsOp).getOp2();
				addResult(op1, sm, map.get(op1));
				addResult(op2, sm, map.get(op2));
			}
		} else if (unit instanceof IfStmt) {
			Value condition = ((IfStmt) unit).getCondition();
			if (condition instanceof BinopExpr) {
				Value op1 = ((BinopExpr) condition).getOp1();
				Value op2 = ((BinopExpr) condition).getOp2();
				addResult(op1, sm, map.get(op1));
				addResult(op2, sm, map.get(op2));
			}
		} else if (unit instanceof InvokeStmt) {
			InvokeExpr ie = ((InvokeStmt) unit).getInvokeExpr();
			if (ie instanceof InstanceInvokeExpr) {
				// receiver
				InstanceInvokeExpr iv = (InstanceInvokeExpr) ie;
				Value base = iv.getBase();
				addResult(base, sm, map.get(base));
			}
			if (ie.getArgCount() == 0) return;
			Value arg0 = ie.getArg(0);
			addResult(arg0, sm, map.get(arg0));
		}
	}

	public String formatResults(Map<? extends Object, Byte> map) {
		if (map == null) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for (Entry<? extends Object, Byte> entry : map.entrySet()) {
			Object local = entry.getKey();
			byte typeSet = entry.getValue();
			sb.append("(").append(local).append("=[ ");
			if ((0b1000 & typeSet) != 0)
				sb.append("RND").append(" ");
			if ((0b100 & typeSet) != 0)
				sb.append("AH").append(" ");
			if ((0b10 & typeSet) != 0)
				sb.append("DET").append(" ");
			if ((0b1 & typeSet) != 0)
				sb.append("OPE").append(" ");
			sb.append("]) ");
		}
		return sb.toString();
	}

}
