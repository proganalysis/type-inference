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

import java.util.HashMap;
import java.util.Map;

import soot.Local;
import soot.SootField;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.BinopExpr;
import soot.jimple.CastExpr;
import soot.jimple.FieldRef;
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
 * This analysis uses a mapping of variables to types as data flow values. The
 * types are expressed as a byte. The last 3 bits of the byte represents the 3
 * types: AH, DET, OPE.
 * </p>
 * 
 * 
 * @author Yao Dong
 *
 */
public class AEAnalysis extends ForwardInterProceduralAnalysis<SootMethod, Unit, Map<Object, Byte>> {

	// An artificial local representing returned value of a procedure (used
	// because a method can have multiple return statements).
	private static final Local RETURN_LOCAL = new JimpleLocal("@return", null);

	// Simply constructs a forward flow inter-procedural analysis with the
	// VERBOSE option set.
	public AEAnalysis() {
		super();
		verbose = false;
	}

	// Initially, all of the three types are available.
	// The byte is 00001111
	private byte initialSet() {
		return 0b1111;
	}

	@Override
	public Map<Object, Byte> normalFlowFunction(Context<SootMethod, Unit, Map<Object, Byte>> context, Unit unit,
			Map<Object, Byte> inValue) {
		// Initialize result to input
		Map<Object, Byte> outValue = copy(inValue);
		// Only statements assigning locals matter
		if (unit instanceof AssignStmt) {
			// Get operands
			Value lhsOp = ((AssignStmt) unit).getLeftOp();
			Value rhsOp = ((AssignStmt) unit).getRightOp();
			Byte set = outValue.get(rhsOp);
			if (set == null)
				set = initialSet();
			if (lhsOp instanceof FieldRef) { // x.f = y
				SootField sf = ((FieldRef) lhsOp).getField();
				Byte b = outValue.get(sf);
				outValue.put(sf, b == null ? initialSet() : (byte) (b & set));
			} else if (lhsOp instanceof ArrayRef) { // x[y] = z
				Value base = (( ArrayRef) lhsOp).getBase();
				outValue.put(base, (byte) (outValue.get(base) & set));
			} else if (lhsOp instanceof Local) {
				if (rhsOp instanceof FieldRef) { // x = y.f
					SootField sf = ((FieldRef) rhsOp).getField();
					Byte b = outValue.get(sf);
					outValue.put(lhsOp, b == null ? initialSet() : b);
				} else if (rhsOp instanceof ArrayRef) { // x = y[z]
					Value base = (( ArrayRef) rhsOp).getBase();
					outValue.put(lhsOp, outValue.get(base));
				} else if (rhsOp instanceof BinopExpr) { // x = y + z
					String symbol = ((BinopExpr) rhsOp).getSymbol();
					switch (symbol) {
					case " + ":
					case " - ":
						outValue.put(lhsOp, (byte) 0b100);
						break;
					case " * ":
					case " << ":
						outValue.put(lhsOp, (byte) 0b10);
					}
				} else if (rhsOp instanceof Local) { // x = y
					outValue.put(lhsOp, set);
				} else if (rhsOp instanceof CastExpr) { // x = (int) y
					outValue.put(lhsOp, outValue.get(((CastExpr) rhsOp).getOp()));
				}
			}
		} else if (unit instanceof ReturnStmt) {
			// Get operand
			Value rhsOp = ((ReturnStmt) unit).getOp();
			outValue.put(RETURN_LOCAL, outValue.get(rhsOp));
		}
		// Return the data flow value at the OUT of the statement
		return outValue;
	}

	@Override
	public Map<Object, Byte> callEntryFlowFunction(Context<SootMethod, Unit, Map<Object, Byte>> context,
			SootMethod calledMethod, Unit unit, Map<Object, Byte> inValue) {
		// Initialise result
		Map<Object, Byte> entryValue = initialValue(calledMethod);
		// Map arguments to parameters
		if (calledMethod.hasActiveBody()) {
			InvokeExpr ie = ((Stmt) unit).getInvokeExpr();
			for (int i = 0; i < ie.getArgCount(); i++) {
				Value arg = ie.getArg(i);
				Local param = calledMethod.getActiveBody().getParameterLocal(i);
				entryValue.put(param, inValue.get(arg));
			}
		}
		// Return the entry value at the called method
		return entryValue;
	}

	@Override
	public Map<Object, Byte> callExitFlowFunction(Context<SootMethod, Unit, Map<Object, Byte>> context,
			SootMethod calledMethod, Unit unit, Map<Object, Byte> exitValue) {
		// Initialise result to an empty value
		Map<Object, Byte> afterCallValue = topValue();
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
	public Map<Object, Byte> callLocalFlowFunction(Context<SootMethod, Unit, Map<Object, Byte>> context, Unit unit,
			Map<Object, Byte> inValue) {
		// Initialise result to the input
		Map<Object, Byte> afterCallValue = copy(inValue);
		
		return afterCallValue;
	}

	@Override
	public Map<Object, Byte> boundaryValue(SootMethod method) {
		return initialValue(method);
	}

	private Map<Object, Byte> initialValue(SootMethod method) {
		Map<Object, Byte> value = topValue();
		if (method.hasActiveBody()) {
			for (Local local : method.getActiveBody().getLocals()) {
				value.put(local, initialSet());
			}
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
		return new HashMap<Object, Byte>();
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
	public boolean isLibMethod(SootMethod sm) {
		if (sm.isPhantom() || sm.isJavaLibraryMethod() || !sm.hasActiveBody())
			return true;
		return false;
	}
}
