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
package vasco.soot;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
 * <p>This analysis uses a mapping of {@link Local}s to {@link Constant}s as
 * data flow values. The flow functions consider assignments of constants
 * to locals (immediate operands) as well as assignments of locals to locals
 * where the operand has a constant value. This type of analysis is commonly referred
 * to as copy constant propagation.</p>
 * 
 * 
 * @author Rohan Padhye
 *
 */
public class JCryptAnalysis extends ForwardInterProceduralAnalysis<SootMethod, Unit, Map<Object, Set<String>>> {
	
	// An artificial local representing returned value of a procedure (used because a method can have multiple return statements).
	private static final Local RETURN_LOCAL = new JimpleLocal("@return", null);
	public static int count = 0;
	//private Set<String> senElements = new HashSet<>();
	//private Set<Object> visited = new HashSet<>();
	//private String dir = "/Users/yaodong/Documents/Projects/type-inference/trunk/soot-jcrypt/sootOutput";
	
	// Simply constructs a forward flow inter-procedural analysis with the VERBOSE option set.
	public JCryptAnalysis() {
		super();
		verbose = true;
		//readFile(dir + File.separator + "poly-result.txt");
	}
	
//	private void readFile(String fileName) {
//		String line = null;
//		try {
//			BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
//
//			while ((line = bufferedReader.readLine()) != null) {
//				String type = bufferedReader.readLine();
//				if (type.equals("@Sensitive") || type.equals("@Poly"))
//					senElements.add(line);
//			}
//
//			bufferedReader.close();
//		} catch (FileNotFoundException ex) {
//			System.out.println("Unable to open file '" + fileName + "'");
//		} catch (IOException ex) {
//			System.out.println("Error reading file '" + fileName + "'");
//		}
//	}

	
	// Private utility method to assign the constant value of the RHS (if any) from the input map to  the LHS in the output map.
//	private void assign(Local lhs, Value rhs, Map<Object, Set<String>> input, Map<Object, Set<String>> output) {
//		// First remove casts, if any.
//		if (rhs instanceof CastExpr) {
//			rhs = ((CastExpr) rhs).getOp();
//		}
//		// Then check if the RHS operand is a constant or local
//		if (rhs instanceof Constant) {
//			// If RHS is a constant, it is a direct gen
//			output.put(lhs, (Constant) rhs);
//		} else if (rhs instanceof Local) {
//			// Copy constant-status of RHS to LHS (indirect gen), if exists
//			if(input.containsKey(rhs)) {
//				output.put(lhs, input.get(rhs));
//			}
//		} else {
//			// RHS is some compound expression, then LHS is non-constant (only kill)
//			output.put(lhs, null);
//		}			
//	}
	
	private void checkUsage(Map<Object, Set<String>> inValue, BinopExpr expr, String type) {
		//if (!sm.getName().endsWith("_Sen") && !sm.isMain()) return;
		Value leftOp = expr.getOp1();
		Value rightOp = expr.getOp2();
		if (!getTypeSet(inValue, leftOp).contains(type)) {
			System.out.println("Need Conversion for " + leftOp.toString() + " " + type);
			count++;
		}
		if (!getTypeSet(inValue, rightOp).contains(type)) {
			System.out.println("Need Conversion for " + rightOp.toString() + " " + type);
			count++;
		}
	}
	
	private Set<String> getTypeSet(Map<Object, Set<String>> fromSet, Object key) {
		if (fromSet.containsKey(key))
			return fromSet.get(key);
		else {
			Set<String> typeSet = new HashSet<>();
			typeSet.add("AH");
			typeSet.add("OPE");
			typeSet.add("DET");
			fromSet.put(key, typeSet);
			return typeSet;
		}
	}

	@Override
	public Map<Object, Set<String>> normalFlowFunction(Context<SootMethod, Unit, Map<Object, Set<String>>> context, Unit unit, Map<Object, Set<String>> inValue) {
		// Initialize result to input
		Map<Object, Set<String>> outValue = copy(inValue);
		SootMethod sm = context.getMethod();
		if (!sm.getName().endsWith("_Sen")) return outValue;
		// Only statements assigning locals matter
		if (unit instanceof AssignStmt) {
			// Get operands
			Value lhsOp = ((AssignStmt) unit).getLeftOp();
			Value rhsOp = ((AssignStmt) unit).getRightOp();
//			if (!senElements.contains(methodSignature + "@" + lhsOp.toString()))
//				return outValue;
			if (lhsOp instanceof FieldRef) { // x.f = y
				SootField sf = ((FieldRef) lhsOp).getField();
//				if (!senElements.contains(sf.getSignature()))
//					return outValue;
				Set<String> typeSet = new HashSet<>(getTypeSet(inValue, sf));
				typeSet.retainAll(getTypeSet(inValue, rhsOp));
				outValue.put(sf, typeSet);
			} else if (lhsOp instanceof Local) {
				if (rhsOp instanceof FieldRef) { // x = y.f
					SootField sf = ((FieldRef) rhsOp).getField();
					Set<String> typeSet = new HashSet<>(getTypeSet(inValue, sf));
					outValue.put(lhsOp, typeSet);
				} else if (rhsOp instanceof BinopExpr) { // x = y + z
					String symbol = ((BinopExpr) rhsOp).getSymbol();
					switch (symbol) {
					case " + ":
					case " - ":
						Set<String> typeSet = new HashSet<>();
						typeSet.add("AH");
						outValue.put(lhsOp, typeSet);
						checkUsage(inValue, (BinopExpr) rhsOp, "AH");
						break;
					case " * ":
					case " / ":
					case " % ":
					case " << ":
					case " >> ":
					case " >>> ":
//					case " ^ ":
//					case " & ":
//					case " | ":
						typeSet = new HashSet<>();
						typeSet.add("DET");
						outValue.put(lhsOp, typeSet);
						checkUsage(inValue, (BinopExpr) rhsOp, "DET");
						break;
					case " == ":
					case " != ":
						checkUsage(inValue, (BinopExpr) rhsOp, "DET");
						break;
					case " cmp ":
					case " cmpg ":
					case " cmpl ":
					case " < ":
					case " > ":
					case " >= ":
					case " <= ":
						checkUsage(inValue, (BinopExpr) rhsOp, "OPE");
					}
				} else if (rhsOp instanceof Local) { // x = y
					outValue.put(lhsOp, new HashSet<>(getTypeSet(inValue, rhsOp)));
				}
				//assign((Local) lhsOp, rhsOp, inValue, outValue);		
			}
		} else if (unit instanceof IfStmt) {
			Value condition = ((IfStmt) unit).getCondition();
			if (condition instanceof BinopExpr) {
				//Value lhsOp = ((BinopExpr) condition).getOp1();
				//if (senElements.contains(methodSignature + "@" + lhsOp.toString())) {
					switch (((BinopExpr) condition).getSymbol()) {
					case " == ":
					case " != ":
						checkUsage(inValue, (BinopExpr) condition, "DET");
						break;
					case " cmp ":
					case " cmpg ":
					case " cmpl ":
					case " < ":
					case " > ":
					case " >= ":
					case " <= ":
						checkUsage(inValue, (BinopExpr) condition, "OPE");
					}
				//}
			}
		} else if (unit instanceof ReturnStmt) {
			// Get operand
			Value rhsOp = ((ReturnStmt) unit).getOp();
			Set<String> typeSet = new HashSet<>(getTypeSet(inValue, rhsOp));
			outValue.put(RETURN_LOCAL, typeSet);
			//assign(RETURN_LOCAL, rhsOp, inValue, outValue);
		}
		// Return the data flow value at the OUT of the statement
		return outValue;
	}

	@Override
	public Map<Object, Set<String>> callEntryFlowFunction(Context<SootMethod, Unit, Map<Object, Set<String>>> context, SootMethod calledMethod, Unit unit, Map<Object, Set<String>> inValue) {
		// Initialise result to empty map
		Map<Object, Set<String>> entryValue = topValue();
		if (!calledMethod.getName().endsWith("_Sen")) return entryValue;
//		if (calledMethod.isJavaLibraryMethod())
//			return entryValue;
		// Map arguments to parameters
		InvokeExpr ie = ((Stmt) unit).getInvokeExpr();
//		if (!senElements.contains(ie.getMethod().getSignature()))
//			return entryValue;
		for (int i = 0; i < ie.getArgCount(); i++) {
			Value arg = ie.getArg(i);
			Local param = calledMethod.getActiveBody().getParameterLocal(i);
			Set<String> typeSet = new HashSet<>(getTypeSet(inValue, param));
			typeSet.retainAll(getTypeSet(inValue, arg));
			entryValue.put(param, typeSet);
			//assign(param, arg, inValue, entryValue);
		}
		// And instance of the this local
//		if (ie instanceof InstanceInvokeExpr) {
//			Value instance = ((InstanceInvokeExpr) ie).getBase();
//			Local thisLocal = calledMethod.getActiveBody().getThisLocal();
//			assign(thisLocal, instance, inValue, entryValue);
//		}
		// Return the entry value at the called method
		return entryValue;
	}
	
	@Override
	public Map<Object, Set<String>> callExitFlowFunction(Context<SootMethod, Unit, Map<Object, Set<String>>> context, SootMethod calledMethod, Unit unit, Map<Object, Set<String>> exitValue) {
		// Initialise result to an empty value
		Map<Object, Set<String>> afterCallValue = topValue();
		if (!calledMethod.getName().endsWith("_Sen")) return afterCallValue;
		// Only propagate constants for return values
		if (unit instanceof AssignStmt) {
			Value lhsOp = ((AssignStmt) unit).getLeftOp();
			Set<String> typeSet = new HashSet<>(exitValue.get(RETURN_LOCAL));
			afterCallValue.put(lhsOp, typeSet);
			//assign((Local) lhsOp, RETURN_LOCAL, exitValue, afterCallValue);
		}
		// Return the map with the returned value's constant
		return afterCallValue;
	}

	@Override
	public Map<Object, Set<String>> callLocalFlowFunction(Context<SootMethod, Unit, Map<Object, Set<String>>> context, Unit unit, Map<Object, Set<String>> inValue) {
		// Initialise result to the input
		Map<Object, Set<String>> afterCallValue = copy(inValue);
		SootMethod sm = context.getMethod();
		if (!sm.getName().endsWith("_Sen")) return afterCallValue;
		// Remove information for return value (as it's value will flow from the call)
		if (unit instanceof AssignStmt) {
			Value lhsOp = ((AssignStmt) unit).getLeftOp();
			afterCallValue.remove(lhsOp);
		}
		// Rest of the map remains the same
		return afterCallValue;
	}
	
	@Override
	public Map<Object, Set<String>> boundaryValue(SootMethod method) {
		return topValue();
	}

	@Override
	public Map<Object, Set<String>> copy(Map<Object, Set<String>> src) {
		return new HashMap<Object, Set<String>>(src);
	}



	@Override
	public Map<Object, Set<String>> meet(Map<Object, Set<String>> op1, Map<Object, Set<String>> op2) {
		Map<Object, Set<String>> result;
		// First add everything in the first operand
		result = new HashMap<Object, Set<String>>(op1);
		// Then add everything in the second operand, bottoming out the common keys with different values
		for (Object x : op2.keySet()) {
			if (op1.containsKey(x)) {
				// Check the values in both operands
				Set<String> c1 = op1.get(x);
				Set<String> c2 = op2.get(x);
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
	public Map<Object, Set<String>> topValue() {
		return new HashMap<Object, Set<String>>();
	}

	/**
	 * Returns a default jimple representation.
	 * @see DefaultJimpleRepresentation
	 */
	@Override
	public ProgramRepresentation<SootMethod, Unit> programRepresentation() {
		return DefaultJimpleRepresentation.v();
	}

}
