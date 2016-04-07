package edu.rpi;

/* JCryptTranslator extends the abstract class BodyTransformer,
 * and implements <pre>internalTransform</pre> method.
 */
import soot.*;
import soot.jimple.AssignStmt;
import soot.jimple.FieldRef;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InterfaceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.NullConstant;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.StaticInvokeExpr;
import soot.util.Chain;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class TranslatorTransformer extends BodyTransformer {

	private Map<String, String> polyElements = new HashMap<>();

	public Map<String, String> getPolyElements() {
		return polyElements;
	}

	public TranslatorTransformer(String dir) {
		readFile(dir + File.separator + "poly-result.txt");
	}

	private void readFile(String fileName) {
		String line = null;
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));

			while ((line = bufferedReader.readLine()) != null) {
				polyElements.put(line, bufferedReader.readLine());
			}

			bufferedReader.close();
		} catch (FileNotFoundException ex) {
			System.out.println("Unable to open file '" + fileName + "'");
		} catch (IOException ex) {
			System.out.println("Error reading file '" + fileName + "'");
		}
	}

	@Override
	protected void internalTransform(Body b, String phase, @SuppressWarnings("rawtypes") Map options) {
		SootMethod senMethod = copyMethod(b);
		if (senMethod == null) {
			processMethod(b, true);
		} else {
			// if (senMethod.isStatic())
			// processMethod(senMethod.getActiveBody(), true);
			processMethod(senMethod.getActiveBody(), false);
		}
	}

	// private void processClearMethod(Body b) {
	// Chain<Unit> units = b.getUnits();
	// for (Iterator<Unit> iter = units.snapshotIterator(); iter.hasNext();) {
	// Unit u = iter.next();
	// InvokeExpr invoke = null;
	// if (u instanceof AssignStmt) {
	// AssignStmt as = (AssignStmt) u;
	// Value right = as.getRightOp();
	// if (right instanceof InstanceInvokeExpr)
	// invoke = getInstanceInvokeForClear((InstanceInvokeExpr) right,
	// b.getMethod());
	// else if (right instanceof StaticInvokeExpr)
	// invoke = getStaticInvoke((StaticInvokeExpr) right, b.getMethod());
	// if (invoke != null)
	// as.setRightOp(invoke);
	// } else if (u instanceof InvokeStmt) {
	// InvokeStmt is = (InvokeStmt) u;
	// InvokeExpr expr = is.getInvokeExpr();
	// if (expr instanceof InstanceInvokeExpr)
	// invoke = getInstanceInvokeForClear((InstanceInvokeExpr) expr,
	// b.getMethod());
	// else if (expr instanceof StaticInvokeExpr)
	// invoke = getStaticInvoke((StaticInvokeExpr) expr, b.getMethod());
	// if (invoke != null)
	// is.setInvokeExpr(invoke);
	// }
	// }
	// }

	private FieldRef processField(FieldRef fieldRef, SootMethod sm, boolean isClear) {
		SootField field = fieldRef.getField();
		if (field.isStatic())
			return getStaticFieldRef(field);
		else if (isClear)
			return getInstanceFieldForClear(field, fieldRef, sm);
		else
			return getInstanceFieldForSen(field, fieldRef);
	}

	private FieldRef getStaticFieldRef(SootField field) {
		SootClass sc = field.getDeclaringClass();
		String newName = field.getName() + "_Sen";
		if (sc.declaresFieldByName(newName))
			return Jimple.v().newStaticFieldRef(sc.getFieldByName(newName).makeRef());
		String kind = polyElements.get(field.getSignature());
		if (kind == null || kind.equals("@Clear"))
			return null;
		else {
			SootField newfield = new SootField(newName, field.getType(), field.getModifiers());
			sc.addField(newfield);
			return Jimple.v().newStaticFieldRef(newfield.makeRef());
		}
	}

	private FieldRef getInstanceFieldForSen(SootField field, FieldRef fieldRef) {
		SootClass sc = field.getDeclaringClass();
		String newName = field.getName() + "_Sen";
		if (sc.declaresFieldByName(newName))
			return Jimple.v().newInstanceFieldRef(((InstanceFieldRef) fieldRef).getBase(),
					sc.getFieldByName(newName).makeRef());
		String kind = polyElements.get(field.getSignature());
		if (kind == null || kind.equals("@Clear"))
			return null;
		else {
			SootField newfield = new SootField(newName, field.getType(), field.getModifiers());
			sc.addField(newfield);
			return Jimple.v().newInstanceFieldRef(((InstanceFieldRef) fieldRef).getBase(), newfield.makeRef());
		}
	}

	private FieldRef getInstanceFieldForClear(SootField field, FieldRef fieldRef, SootMethod sm) {
		Value base = (( InstanceFieldRef) fieldRef).getBase();
		String kind = polyElements.get(sm.getSignature() + "@" + base.toString());
		if (kind == null || kind.equals("@Clear"))
			return null;
		else {
			return getInstanceFieldForSen(field, fieldRef);
		}
	}

	private void processMethod(Body b, Boolean isClear) {
		PatchingChain<Unit> units = b.getUnits();
		for (Iterator<Unit> iter = units.snapshotIterator(); iter.hasNext();) {
			Unit u = iter.next();
			if (u instanceof AssignStmt) {
				AssignStmt as = ( AssignStmt) u;
				Value left = as.getLeftOp();
				if (left instanceof FieldRef) {
					FieldRef newFieldRef = processField((FieldRef) left, b.getMethod(), isClear);
					if (newFieldRef != null)
						as.setLeftOp(newFieldRef);
				}
				Value right = as.getRightOp();
				if (right instanceof FieldRef) {
					FieldRef newFieldRef = processField((FieldRef) right, b.getMethod(), isClear);
					if (newFieldRef != null)
						as.setRightOp(newFieldRef);
				} else if (right instanceof InvokeExpr) {
					InvokeExpr invoke = processInvoke(b, isClear, (InvokeExpr) right);
					if (invoke != null)
						as.setRightOp(invoke);
				}
			} else if (u instanceof InvokeStmt) {
				InvokeStmt is = (InvokeStmt) u;
				InvokeExpr invoke = processInvoke(b, isClear, is.getInvokeExpr());
				if (invoke != null)
					is.setInvokeExpr(invoke);
			}
		}
	}

	private InvokeExpr processInvoke(Body b, Boolean isClear, InvokeExpr expr) {
		InvokeExpr invoke = null;
		if (expr instanceof InstanceInvokeExpr)
			invoke = isClear ? getInstanceInvokeForClear((InstanceInvokeExpr) expr, b.getMethod())
					: getInvokeForSen((InstanceInvokeExpr) expr);
		else if (expr instanceof StaticInvokeExpr)
			invoke = getStaticInvoke((StaticInvokeExpr) expr, b.getMethod());
		return invoke;
	}

	private InvokeExpr getInstanceInvokeForClear(InstanceInvokeExpr expr, SootMethod sm) {
		Value base = expr.getBase();
		String kind = polyElements.get(sm.getSignature() + "@" + base.toString());
		if (kind == null || kind.equals("@Clear"))
			return null;
		return getInvokeForSen(expr);
	}

	private InvokeExpr getStaticInvoke(StaticInvokeExpr expr, SootMethod sm) {
		for (Value arg : expr.getArgs()) {
			String kind = polyElements.get(sm.getSignature() + "@" + arg.toString());
			if (kind == null || kind.equals("@Clear"))
				continue;
			return getInvokeForSen(expr);
		}
		return null;
	}

	private InvokeExpr getInvokeForSen(InvokeExpr expr) {
		SootMethod method = expr.getMethod();
		if (method.isJavaLibraryMethod())
			return null;
		SootMethod senMethod = generateSenMethod(method);
		if (senMethod == null)
			return null;
		if (expr instanceof SpecialInvokeExpr)
			return getSpecialInvoke((InstanceInvokeExpr) expr, senMethod);
		else if (expr instanceof InterfaceInvokeExpr)
			return Jimple.v().newInterfaceInvokeExpr((Local) ((InstanceInvokeExpr) expr).getBase(), senMethod.makeRef(),
					expr.getArgs());
		else if (expr instanceof StaticInvokeExpr)
			return Jimple.v().newStaticInvokeExpr(senMethod.makeRef(), expr.getArgs());
		else
			return Jimple.v().newVirtualInvokeExpr((Local) ((InstanceInvokeExpr) expr).getBase(), senMethod.makeRef(),
					expr.getArgs());
	}

	private InvokeExpr getSpecialInvoke(InstanceInvokeExpr expr, SootMethod senMethod) {
		List<Value> args = new ArrayList<>(expr.getArgs());
		args.add(NullConstant.v());
		return Jimple.v().newSpecialInvokeExpr((Local) expr.getBase(), senMethod.makeRef(), args);
	}

	private SootMethod copyMethod(Body b) {
		return generateSenMethod(b.getMethod());
	}

	private SootMethod generateSenMethod(SootMethod sm) {
		String kind = polyElements.get(sm.toString());
		if (kind == null || kind.equals("0"))
			return null;
		if (sm.isConstructor())
			return generateSenConstructor(sm);
		String name = sm.getName();
		String senName = name + "_Sen";
		String senSignature = sm.getSubSignature().replace(name, senName);
		SootClass sc = sm.getDeclaringClass();
		if (sc.declaresMethod(senSignature))
			return sc.getMethod(senSignature);
		else {
			SootMethod senMethod = new SootMethod(senName, sm.getParameterTypes(), sm.getReturnType(),
					sm.getModifiers(), sm.getExceptions());
			sc.addMethod(senMethod);
			JimpleBody body = Jimple.v().newBody(senMethod);
			body.importBodyContentsFrom(sm.retrieveActiveBody());
			senMethod.setActiveBody(body);
			return senMethod;
		}
	}

	private SootMethod generateSenConstructor(SootMethod sm) {
		String name = sm.getName();
		List<Type> senParameterTypes = new ArrayList<>(sm.getParameterTypes());
		int numOfPar = senParameterTypes.size();
		RefType refType = RefType.v("java.lang.Object");
		senParameterTypes.add(refType);
		SootClass sc = sm.getDeclaringClass();
		if (sc.declaresMethod(name, senParameterTypes))
			return sc.getMethod(name, senParameterTypes);
		else {
			SootMethod senMethod = new SootMethod(name, senParameterTypes, sm.getReturnType(), sm.getModifiers(),
					sm.getExceptions());
			sc.addMethod(senMethod);
			JimpleBody body = Jimple.v().newBody(senMethod);
			body.importBodyContentsFrom(sm.retrieveActiveBody());
			senMethod.setActiveBody(body);
			Chain<Unit> units = body.getUnits();
			Local arg = Jimple.v().newLocal("l0", refType);
			body.getLocals().add(arg);
			units.insertBefore(Jimple.v().newIdentityStmt(arg, Jimple.v().newParameterRef(refType, numOfPar)), units.getFirst());
			return senMethod;
		}
	}

}
