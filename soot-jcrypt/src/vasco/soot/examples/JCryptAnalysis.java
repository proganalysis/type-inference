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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import soot.Local;
import soot.SootField;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.BinopExpr;
import soot.jimple.Constant;
import soot.jimple.FieldRef;
import soot.jimple.IfStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.ReturnStmt;
import soot.jimple.Stmt;
import soot.jimple.internal.JimpleLocal;
import vasco.Context;
import vasco.ForwardInterProceduralAnalysis;
import vasco.ProgramRepresentation;
import vasco.soot.DefaultJimpleRepresentation;

/**
 * An inter-procedural copy constant propagation analysis.
 * 
 * <p>
 * This analysis uses a mapping of {@link Local}s to {@link Constant}s as data
 * flow values. The flow functions consider assignments of constants to locals
 * (immediate operands) as well as assignments of locals to locals where the
 * operand has a constant value. This type of analysis is commonly referred to
 * as copy constant propagation.
 * </p>
 * 
 * 
 * @author Rohan Padhye
 *
 */
public class JCryptAnalysis
		extends ForwardInterProceduralAnalysis<SootMethod, Unit, Map<Object, Set<JCryptAnalysis.EnType>>> {

	// An artificial local representing returned value of a procedure (used
	// because a method can have multiple return statements).
	private static final Local RETURN_LOCAL = new JimpleLocal("@return", null);
	public static int count = 0;
	public static Map<Object, Set<EnType>> fieldValue = new HashMap<>();
	private Set<String> senElements = new HashSet<>();

	public enum EnType {
		AH, OPE, DET
	}

	// Simply constructs a forward flow inter-procedural analysis with the
	// VERBOSE option set.
	public JCryptAnalysis(String dir) {
		super();
		verbose = true;
		readFile(dir + File.separator + "poly-result.txt");
	}

	private void readFile(String fileName) {
		String line = null;
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
			while ((line = bufferedReader.readLine()) != null) {
				String type = bufferedReader.readLine();
				if (type.equals("@Sensitive") || type.equals("@Poly"))
					senElements.add(line);
			}
			bufferedReader.close();
		} catch (FileNotFoundException ex) {
			System.out.println("Unable to open file '" + fileName + "'");
		} catch (IOException ex) {
			System.out.println("Error reading file '" + fileName + "'");
		}
	}

	private void checkUsage(Map<Object, Set<EnType>> outValue, BinopExpr expr, EnType type) {
		Value leftOp = expr.getOp1();
		Value rightOp = expr.getOp2();
		if (!getValueSet(outValue, leftOp).contains(type)) {
			System.out.println("Need Conversion for " + leftOp.toString() + " " + type + " at " + expr);
			count++;
		}
		if (!getValueSet(outValue, rightOp).contains(type)) {
			System.out.println("Need Conversion for " + rightOp.toString() + " " + type + " at " + expr);
			count++;
		}
	}

	private Set<EnType> initialSet() {
		Set<EnType> typeSet = new HashSet<>();
		typeSet.add(EnType.AH);
		typeSet.add(EnType.OPE);
		typeSet.add(EnType.DET);
		return typeSet;
	}

	public Set<EnType> getValueSet(Map<Object, Set<EnType>> outValue, Object op) {
		if (!outValue.containsKey(op))
			outValue.put(op, initialSet());
		return outValue.get(op);
	}

	@Override
	public Map<Object, Set<EnType>> normalFlowFunction(Context<SootMethod, Unit, Map<Object, Set<EnType>>> context,
			Unit unit, Map<Object, Set<EnType>> inValue) {
		// Initialize result to input
		Map<Object, Set<EnType>> outValue = copy(inValue);
		SootMethod sm = context.getMethod();
		if (!sm.getName().endsWith("_Sen") && !sm.isMain())
			return outValue;
		String smOriginalSig = sm.getSignature().replace("_Sen", "");
		// Only statements assigning locals matter
		if (unit instanceof AssignStmt) {
			// Get operands
			Value lhsOp = ((AssignStmt) unit).getLeftOp();
			Value rhsOp = ((AssignStmt) unit).getRightOp();
			if (lhsOp instanceof FieldRef) { // x.f = y
				SootField sf = ((FieldRef) lhsOp).getField();
				Set<EnType> fieldSet = getValueSet(outValue, sf);
				Set<EnType> rhsSet = getValueSet(outValue, rhsOp);
				fieldSet.retainAll(rhsSet);
				outValue.put(sf, new HashSet<>(fieldSet));
			} else if (lhsOp instanceof Local) {
				if (!senElements.contains(smOriginalSig + "@" + lhsOp.toString()))
					return outValue;
				if (rhsOp instanceof FieldRef) { // x = y.f
					SootField sf = ((FieldRef) rhsOp).getField();
					Set<EnType> fieldSet = getValueSet(outValue, sf);
					outValue.put(lhsOp, new HashSet<>(fieldSet));
				} else if (rhsOp instanceof BinopExpr) { // x = y + z
					String symbol = ((BinopExpr) rhsOp).getSymbol();
					switch (symbol) {
					case " + ":
					case " - ":
						Set<EnType> typeSet = new HashSet<>();
						typeSet.add(EnType.AH);
						outValue.put(lhsOp, typeSet);
						checkUsage(outValue, (BinopExpr) rhsOp, EnType.AH);
						break;
					case " * ":
					case " / ":
					case " % ":
					case " << ":
					case " >> ":
					case " >>> ":
						typeSet = new HashSet<>();
						typeSet.add(EnType.DET);
						outValue.put(lhsOp, typeSet);
						checkUsage(outValue, (BinopExpr) rhsOp, EnType.DET);
						break;
					case " == ":
					case " != ":
						checkUsage(outValue, (BinopExpr) rhsOp, EnType.DET);
						break;
					case " cmp ":
					case " cmpg ":
					case " cmpl ":
					case " < ":
					case " > ":
					case " >= ":
					case " <= ":
						checkUsage(outValue, (BinopExpr) rhsOp, EnType.OPE);
					}
				} else if (rhsOp instanceof Local) { // x = y
					outValue.put(lhsOp, new HashSet<>(getValueSet(outValue, rhsOp)));
				}
			}
		} else if (unit instanceof IfStmt) {
			Value condition = ((IfStmt) unit).getCondition();
			if (condition instanceof BinopExpr) {
				Value lhsOp = ((BinopExpr) condition).getOp1();
				if (!senElements.contains(smOriginalSig + "@" + lhsOp.toString()))
					return outValue;
				switch (((BinopExpr) condition).getSymbol()) {
				case " == ":
				case " != ":
					checkUsage(outValue, (BinopExpr) condition, EnType.DET);
					break;
				case " cmp ":
				case " cmpg ":
				case " cmpl ":
				case " < ":
				case " > ":
				case " >= ":
				case " <= ":
					checkUsage(outValue, (BinopExpr) condition, EnType.OPE);
				}
			}
		} else if (unit instanceof ReturnStmt) {
			// Get operand
			Value rhsOp = ((ReturnStmt) unit).getOp();
			Set<EnType> typeSet = new HashSet<>(getValueSet(outValue, rhsOp));
			outValue.put(RETURN_LOCAL, typeSet);
		}
		// Return the data flow value at the OUT of the statement
		return outValue;
	}

	@Override
	public Map<Object, Set<EnType>> callEntryFlowFunction(Context<SootMethod, Unit, Map<Object, Set<EnType>>> context,
			SootMethod calledMethod, Unit unit, Map<Object, Set<EnType>> inValue) {
		// Initialise result to empty map
		Map<Object, Set<EnType>> entryValue = copy(inValue);
		for (Local local : calledMethod.getActiveBody().getLocals()) {
			getValueSet(entryValue, local);
		}
		if (!calledMethod.getName().endsWith("_Sen"))
			return entryValue;
		// Map arguments to parameters
		InvokeExpr ie = ((Stmt) unit).getInvokeExpr();
		for (int i = 0; i < ie.getArgCount(); i++) {
			Value arg = ie.getArg(i);
			Local param = calledMethod.getActiveBody().getParameterLocal(i);
			Set<EnType> typeSet = getValueSet(entryValue, param);
			typeSet.retainAll(getValueSet(entryValue, arg));
			entryValue.put(param, new HashSet<>(typeSet));
		}
		// Return the entry value at the called method
		return entryValue;
	}

	@Override
	public Map<Object, Set<EnType>> callExitFlowFunction(Context<SootMethod, Unit, Map<Object, Set<EnType>>> context,
			SootMethod calledMethod, Unit unit, Map<Object, Set<EnType>> exitValue) {
		// Initialise result to an empty value
		Map<Object, Set<EnType>> afterCallValue = copy(exitValue);
		if (!calledMethod.getName().endsWith("_Sen"))
			return afterCallValue;
		// Only propagate constants for return values
		if (unit instanceof AssignStmt) {
			Value lhsOp = ((AssignStmt) unit).getLeftOp();
			Set<EnType> typeSet = new HashSet<>(exitValue.get(RETURN_LOCAL));
			afterCallValue.put(lhsOp, typeSet);
		}
		// Return the map with the returned value's constant
		return afterCallValue;
	}

	@Override
	public Map<Object, Set<EnType>> callLocalFlowFunction(Context<SootMethod, Unit, Map<Object, Set<EnType>>> context,
			Unit unit, Map<Object, Set<EnType>> inValue) {
		// Initialise result to the input
		Map<Object, Set<EnType>> afterCallValue = copy(inValue);
		return afterCallValue;
	}

	@Override
	public Map<Object, Set<EnType>> boundaryValue(SootMethod method) {
		Map<Object, Set<EnType>> value = topValue();
		for (Local local : method.getActiveBody().getLocals()) {
			value.put(local, initialSet());
		}
		for (SootField sf : method.getDeclaringClass().getFields()) {
			value.put(sf, initialSet());
		}
		return value;
	}

	@Override
	public Map<Object, Set<EnType>> copy(Map<Object, Set<EnType>> src) {
		Map<Object, Set<EnType>> copy = new HashMap<>();
		for (Entry<Object, Set<EnType>> entry : src.entrySet()) {
			Set<EnType> c = new HashSet<>();
			for (EnType e : entry.getValue())
				c.add(e);
			copy.put(entry.getKey(), c);
		}
		return copy;
	}

	@Override
	public Map<Object, Set<EnType>> meet(Map<Object, Set<EnType>> op1, Map<Object, Set<EnType>> op2) {
		Map<Object, Set<EnType>> result;
		// First add everything in the first operand
		result = new HashMap<Object, Set<EnType>>(op1);
		// Then add everything in the second operand, bottoming out the common
		// keys with different values
		for (Object x : op2.keySet()) {
			if (op1.containsKey(x)) {
				// Check the values in both operands
				Set<EnType> c1 = op1.get(x);
				Set<EnType> c2 = op2.get(x);
				if (!c1.isEmpty()) {
					c1.retainAll(c2);
					// Set to non-constant
					result.put(x, c1);
				}
			} else {
				// Only in second operand, so add as-is
				result.put(x, op2.get(x));
			}
		}
		return result;
	}

	/**
	 * Returns an empty map.
	 */
	@Override
	public Map<Object, Set<EnType>> topValue() {
		return new HashMap<Object, Set<EnType>>();
	}

	/**
	 * Returns a default jimple representation.
	 * 
	 * @see DefaultJimpleRepresentation
	 */
	@Override
	public ProgramRepresentation<SootMethod, Unit> programRepresentation() {
		return DefaultJimpleRepresentation.v();
	}

}
