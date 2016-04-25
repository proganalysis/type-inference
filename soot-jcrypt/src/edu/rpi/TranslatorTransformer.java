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

	private Set<String> polyElements = new HashSet<>();

	public Set<String> getPolyElements() {
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
				polyElements.add(line);
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
		if (polyElements.contains(field.getSignature())) {
			SootField newfield = new SootField(newName, field.getType(), field.getModifiers());
			sc.addField(newfield);
			return Jimple.v().newStaticFieldRef(newfield.makeRef());
		} else return null;
	}

	private FieldRef getInstanceFieldForSen(SootField field, FieldRef fieldRef) {
		SootClass sc = field.getDeclaringClass();
		String newName = field.getName() + "_Sen";
		if (sc.declaresFieldByName(newName))
			return Jimple.v().newInstanceFieldRef(((InstanceFieldRef) fieldRef).getBase(),
					sc.getFieldByName(newName).makeRef());
		if (polyElements.contains(field.getSignature())) {
			SootField newfield = new SootField(newName, field.getType(), field.getModifiers());
			sc.addField(newfield);
			return Jimple.v().newInstanceFieldRef(((InstanceFieldRef) fieldRef).getBase(), newfield.makeRef());
		} else return null;
	}

	private FieldRef getInstanceFieldForClear(SootField field, FieldRef fieldRef, SootMethod sm) {
		Value base = (( InstanceFieldRef) fieldRef).getBase();
		if (polyElements.contains(sm.getSignature() + "@" + base.toString()))
			return getInstanceFieldForSen(field, fieldRef);
		else return null;
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
		if (polyElements.contains(sm.getSignature() + "@" + base.toString()))
			return getInvokeForSen(expr);
		else return null;
	}

	private InvokeExpr getStaticInvoke(StaticInvokeExpr expr, SootMethod sm) {
		for (Value arg : expr.getArgs()) {
			if (polyElements.contains(sm.getSignature() + "@" + arg.toString()))
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
		if (senMethod.isConstructor()) {
			List<Value> args = new ArrayList<>(expr.getArgs());
			args.add(NullConstant.v());
			return Jimple.v().newSpecialInvokeExpr((Local) expr.getBase(), senMethod.makeRef(), args);
		} else
			return Jimple.v().newSpecialInvokeExpr((Local) expr.getBase(), senMethod.makeRef(), expr.getArgs());
	}

	private SootMethod copyMethod(Body b) {
		return generateSenMethod(b.getMethod());
	}

	private SootMethod generateSenMethod(SootMethod sm) {
		if (!polyElements.contains(sm.getSignature())) return null;
		if (sm.isConstructor())
			return generateSenConstructor(sm);
		String name = sm.getName();
		String senName = name + "_Sen";
		String senSignature = sm.getSubSignature().replace(name + "(", senName + "(");
		SootClass sc = sm.getDeclaringClass();
		if (sc.declaresMethod(senSignature))
			return sc.getMethod(senSignature);
		else {
			SootMethod senMethod = new SootMethod(senName, sm.getParameterTypes(), sm.getReturnType(),
					sm.getModifiers(), sm.getExceptions());
			sc.addMethod(senMethod);
			if (sm.isAbstract()) return senMethod;
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
