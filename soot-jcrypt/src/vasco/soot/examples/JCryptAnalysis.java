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
import java.util.Set;

import soot.Local;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.BinopExpr;
import soot.jimple.CastExpr;
import soot.jimple.FieldRef;
import soot.jimple.IfStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.ReturnStmt;
import soot.jimple.Stmt;
import soot.jimple.internal.JimpleLocal;
import vasco.Context;
import vasco.ForwardInterProceduralAnalysis;
import vasco.ProgramRepresentation;
import vasco.soot.DefaultJimpleRepresentation;

/**
 * An inter-procedural available expression analysis.
 * 
 * <p>
 * This analysis uses a mapping of variables to types as data
 * flow values. The types are expressed as a byte. The last 3
 * bits of the byte represents the 3 types: AH, DET, OPE.
 * </p>
 * 
 * 
 * @author Yao Dong
 *
 */
public class JCryptAnalysis
		extends ForwardInterProceduralAnalysis<SootMethod, Unit, Map<Object, Byte>> {

	// An artificial local representing returned value of a procedure (used
	// because a method can have multiple return statements).
	private static final Local RETURN_LOCAL = new JimpleLocal("@return", null);
	protected static Set<String> conversions = new HashSet<>();
	//public static Map<Object, Byte> fieldValue = new HashMap<>();
	private Set<String> senElements = new HashSet<>();
	private Map<String, Set<String>> detContainers = new HashMap<>();
	private Map<String, Set<String>> opeContainers = new HashMap<>();

	// Simply constructs a forward flow inter-procedural analysis with the
	// VERBOSE option set.
	public JCryptAnalysis(String dir) {
		super();
		Set<String> methods = new HashSet<>();
		methods.add("contains");
		detContainers.put("java.util.ArrayList", methods);
		methods = new HashSet<>();
		methods.add("containsKey");
		methods.add("put");
		detContainers.put("java.util.HashMap", methods);
		detContainers.put("java.util.LinkedHashMap", methods);
		methods = new HashSet<>();
		methods.add("equals");
		detContainers.put("java.lang.String", methods);
		methods = new HashSet<>();
		methods.add("sort");
		opeContainers.put("java.util.Collections", methods);
		verbose = true;
		readFile(dir + File.separator + "poly-result.txt");
	}

	private void readFile(String fileName) {
		String line = null;
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
			while ((line = bufferedReader.readLine()) != null)
				senElements.add(line);
			bufferedReader.close();
		} catch (FileNotFoundException ex) {
			System.out.println("Unable to open file '" + fileName + "'");
		} catch (IOException ex) {
			System.out.println("Error reading file '" + fileName + "'");
		}
	}

	private void checkUsage(Map<Object, Byte> outValue, BinopExpr expr, Byte type, String method) {
		Value leftOp = expr.getOp1();
		Value rightOp = expr.getOp2();
		if ((getValueSet(outValue, leftOp) & type) == 0) {
			conversions.add(leftOp + ": " + expr + " at " + method);
		}
		if ((getValueSet(outValue, rightOp) & type) == 0) {
			conversions.add(rightOp + ": " + expr + " at " + method);
		}
	}

	// Initially, all of the three types are available.
	// The byte is 00000111
	private byte initialSet() {
		return 0b111;
	}

	public byte getValueSet(Map<Object, Byte> outValue, Object op) {
		if (!outValue.containsKey(op))
			outValue.put(op, initialSet());
		return outValue.get(op);
	}

	@Override
	public Map<Object, Byte> normalFlowFunction(Context<SootMethod, Unit, Map<Object, Byte>> context,
			Unit unit, Map<Object, Byte> inValue) {
		// Initialize result to input
		Map<Object, Byte> outValue = copy(inValue);
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
				byte fieldSet = getValueSet(outValue, sf);
				byte rhsSet = getValueSet(outValue, rhsOp);
				outValue.put(sf, (byte) (fieldSet & rhsSet));
			} else if (lhsOp instanceof Local) {
				if (!senElements.contains(smOriginalSig + "@" + lhsOp.toString())) {
					// x = a < b, if a is sensitive, we have to check if OPE is available
					if (rhsOp instanceof BinopExpr) {
						Value lhs = ((BinopExpr) rhsOp).getOp1();
						if (!senElements.contains(smOriginalSig + "@" + lhs.toString()))
							return outValue;
						checkUsageForCondition(outValue, sm, rhsOp);
					} else return outValue;
				}
				if (rhsOp instanceof FieldRef) { // x = y.f
					SootField sf = ((FieldRef) rhsOp).getField();
					byte fieldSet = getValueSet(outValue, sf);
					outValue.put(lhsOp, fieldSet);
				} else if (rhsOp instanceof BinopExpr) { // x = y + z
					String symbol = ((BinopExpr) rhsOp).getSymbol();
					switch (symbol) {
					case " + ":
					case " - ":
						outValue.put(lhsOp, (byte) 0b100);
						checkUsage(outValue, (BinopExpr) rhsOp, (byte) 0b100, sm.getSignature());
						break;
					case " * ":
					case " / ":
					case " % ":
					case " << ":
					case " >> ":
					case " >>> ":
						outValue.put(lhsOp, (byte) 0b10);
						checkUsage(outValue, (BinopExpr) rhsOp, (byte) 0b10, sm.getSignature());
						break;
					case " == ":
					case " != ":
						checkUsage(outValue, (BinopExpr) rhsOp, (byte) 0b10, sm.getSignature());
						break;
					case " cmp ":
					case " cmpg ":
					case " cmpl ":
					case " < ":
					case " > ":
					case " >= ":
					case " <= ":
						checkUsage(outValue, (BinopExpr) rhsOp, (byte) 0b1, sm.getSignature());
					}
				} else if (rhsOp instanceof Local) { // x = y
					outValue.put(lhsOp, getValueSet(outValue, rhsOp));
				} else if (rhsOp instanceof CastExpr) { // x = (int) y
					outValue.put(lhsOp, getValueSet(outValue, ((CastExpr) rhsOp).getOp()));
				}
			}
		} else if (unit instanceof IfStmt) {
			Value condition = ((IfStmt) unit).getCondition();
			if (condition instanceof BinopExpr) {
				Value lhsOp = ((BinopExpr) condition).getOp1();
				if (!senElements.contains(smOriginalSig + "@" + lhsOp.toString()))
					return outValue;
				checkUsageForCondition(outValue, sm, condition);
			}
		} else if (unit instanceof ReturnStmt) {
			// Get operand
			Value rhsOp = ((ReturnStmt) unit).getOp();
			byte typeSet = getValueSet(outValue, rhsOp);
			outValue.put(RETURN_LOCAL, typeSet);
		}
		// Return the data flow value at the OUT of the statement
		return outValue;
	}

	private void checkUsageForCondition(Map<Object, Byte> outValue, SootMethod sm, Value condition) {
		switch (((BinopExpr) condition).getSymbol()) {
		case " == ":
		case " != ":
			checkUsage(outValue, (BinopExpr) condition, (byte) 0b10, sm.getSignature());
			break;
		case " cmp ":
		case " cmpg ":
		case " cmpl ":
		case " < ":
		case " > ":
		case " >= ":
		case " <= ":
			checkUsage(outValue, (BinopExpr) condition, (byte) 0b1, sm.getSignature());
		}
	}

	@Override
	public Map<Object, Byte> callEntryFlowFunction(Context<SootMethod, Unit, Map<Object, Byte>> context,
			SootMethod calledMethod, Unit unit, Map<Object, Byte> inValue) {
		// Initialise result to empty map
		Map<Object, Byte> entryValue = topValue();
		SootClass sc = calledMethod.getDeclaringClass();
		for (SootField sf : sc.getFields()) {
			if (sf.getName().endsWith("_Sen")) {
				getValueSet(inValue, sf);
				entryValue.put(sf, inValue.get(sf));
			}
		}
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
			byte typeSet = getValueSet(entryValue, param);
			entryValue.put(param, (byte) (typeSet & getValueSet(inValue, arg)));
		}
		// Return the entry value at the called method
		return entryValue;
	}

	@Override
	public Map<Object, Byte> callExitFlowFunction(Context<SootMethod, Unit, Map<Object, Byte>> context,
			SootMethod calledMethod, Unit unit, Map<Object, Byte> exitValue) {
		// Initialise result to an empty value
		Map<Object, Byte> afterCallValue = copy(exitValue);
		if (!calledMethod.getName().endsWith("_Sen"))
			return afterCallValue;
		// Only propagate constants for return values
		if (unit instanceof AssignStmt) {
			Value lhsOp = ((AssignStmt) unit).getLeftOp();
			byte typeSet = exitValue.get(RETURN_LOCAL);
			afterCallValue.put(lhsOp, typeSet);
		}
		// Return the map with the returned value's constant
		return afterCallValue;
	}

	@Override
	public Map<Object, Byte> callLocalFlowFunction(Context<SootMethod, Unit, Map<Object, Byte>> context,
			Unit unit, Map<Object, Byte> inValue) {
		// Initialise result to the input
		Map<Object, Byte> afterCallValue = copy(inValue);
		return afterCallValue;
	}

	@Override
	public Map<Object, Byte> boundaryValue(SootMethod method) {
		Map<Object, Byte> value = topValue();
		for (Local local : method.getActiveBody().getLocals()) {
			value.put(local, initialSet());
		}
		for (SootField sf : method.getDeclaringClass().getFields()) {
			value.put(sf, initialSet());
		}
		return value;
	}

	@Override
	public Map<Object, Byte> copy(Map<Object, Byte> src) {
		return new HashMap<>(src);
	}

	@Override
	public Map<Object, Byte> meet(Map<Object, Byte> op1, Map<Object, Byte> op2) {
		Map<Object, Byte> result;
		// First add everything in the first operand
		result = new HashMap<Object, Byte>(op1);
		// Then add everything in the second operand, bottoming out the common
		// keys with different values
		for (Object x : op2.keySet()) {
			if (op1.containsKey(x)) {
				// Check the values in both operands
				byte c1 = op1.get(x);
				byte c2 = op2.get(x);
				result.put(x, (byte) (c1 & c2));
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
	public Map<Object, Byte> topValue() {
		return new HashMap<>();
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
	
	@Override
	public boolean isLibMethod(SootMethod sm, Unit unit, Map<Object, Byte> inValue) {
		if (sm.isPhantom()) return true;
		if (sm.isJavaLibraryMethod()) {
			// check implicit equality
			String className = sm.getDeclaringClass().toString();
			if (detContainers.containsKey(className)) {
				if (detContainers.get(className).contains(sm.getName())) {
					InvokeExpr ie = ((Stmt) unit).getInvokeExpr();
					if (ie instanceof InstanceInvokeExpr) {
			            // receiver
			            InstanceInvokeExpr iv = (InstanceInvokeExpr) ie;
			            Value base = iv.getBase();
			            System.out.println("Equality Check: " + base + " at " + unit + "(" + className + ")");
			            if ((inValue.get(base) & 0b10) == 0) {
							conversions.add(base + ": " + unit);
						}
					}
					Value arg = ie.getArg(0);
					System.out.println("Equality Check: " + arg + " at " + unit + "(" + className + ")");
					if (inValue.containsKey(arg) && (inValue.get(arg) & 0b10) == 0) {
						conversions.add(arg + ": " + unit);
					}
				}
			}
			if (opeContainers.containsKey(className)) {
				if (opeContainers.get(className).contains(sm.getName())) {
					InvokeExpr ie = ((Stmt) unit).getInvokeExpr();
					Value arg = ie.getArg(0);
					System.out.println("Comparison Check: " + arg + " at " + unit + "(" + className + ")");
					if ((inValue.get(arg) & 0b1) == 0) {
						conversions.add(arg + ": " + unit);
					}
				}
			}
			return true;
		}
		return false;
	}

}
