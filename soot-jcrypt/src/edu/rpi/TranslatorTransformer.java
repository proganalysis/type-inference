package edu.rpi;

/* JCryptTranslator extends the abstract class BodyTransformer,
 * and implements <pre>internalTransform</pre> method.
 */
import soot.*;
import soot.jimple.AssignStmt;
import soot.jimple.FieldRef;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.VirtualInvokeExpr;

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
			processClearMethod(b);
		} else {
			processSenMethod(senMethod.getActiveBody());
		}
	}

	private void processClearMethod(Body b) {
		PatchingChain<Unit> units = b.getUnits();
		for (Iterator<Unit> iter = units.snapshotIterator(); iter.hasNext();) {
			Unit u = iter.next();
			if (u instanceof AssignStmt) {
				AssignStmt as = ( AssignStmt) u;
				Value right = as.getRightOp();
				if (right instanceof VirtualInvokeExpr) {
					processInvokeForClear(right, b.getMethod());
				}
			} else if (u instanceof InvokeStmt) {
				InvokeExpr expr = ((InvokeStmt) u).getInvokeExpr();
				if (expr instanceof VirtualInvokeExpr) {
					processInvokeForClear(expr, b.getMethod());
				}
			}
		}
	}

	private void processSenMethod(Body b) {
		PatchingChain<Unit> units = b.getUnits();
		for (Iterator<Unit> iter = units.snapshotIterator(); iter.hasNext();) {
			Unit u = iter.next();
			if (u instanceof AssignStmt) {
				AssignStmt as = ( AssignStmt) u;
				Value left = as.getLeftOp();
				if (left instanceof InstanceFieldRef) {
					SootField newf = processField(((FieldRef) left).getField());
					if (newf != null) {
						FieldRef newFieldRef = Jimple.v().newInstanceFieldRef(((InstanceFieldRef) left).getBase(),
								newf.makeRef());
						as.setLeftOp(newFieldRef);
					}
				}
				Value right = as.getRightOp();
				if (right instanceof InstanceFieldRef) {
					SootField newf = processField(((FieldRef) right).getField());
					if (newf != null) {
						FieldRef newFieldRef = Jimple.v().newInstanceFieldRef(((InstanceFieldRef) right).getBase(),
								newf.makeRef());
						as.setRightOp(newFieldRef);
					}
				} else if (right instanceof VirtualInvokeExpr) {
					processInvokeForSen(right);
				}
			} else if (u instanceof InvokeStmt) {
				InvokeExpr expr = ((InvokeStmt) u).getInvokeExpr();
				if (expr instanceof VirtualInvokeExpr) {
					processInvokeForSen(expr);
				}
			}
		}
	}

	private void processInvokeForSen(Value v) {
		VirtualInvokeExpr expr = (VirtualInvokeExpr) v;
		SootMethod method = expr.getMethod();
		if (method.isJavaLibraryMethod())
			return;
		SootMethod senMethod = generateSenMethod(method);
		if (senMethod == null)
			return;
		expr.setMethodRef(senMethod.makeRef());
	}

	private void processInvokeForClear(Value v, SootMethod sm) {
		VirtualInvokeExpr expr = (VirtualInvokeExpr) v;
		Value base = expr.getBase();
		String kind = polyElements.get(sm.getSignature() + "@" + base.toString());
		if (kind == null)
			return;
		if (!kind.equals("@Clear")) {
			processInvokeForSen(v);
		}
	}

	private SootMethod copyMethod(Body b) {
		return generateSenMethod(b.getMethod());
	}

	private SootMethod generateSenMethod(SootMethod sm) {
		String kind = polyElements.get(sm.toString());
		if (kind == null || kind.equals("0"))
			return null;
		String senName = sm.getName() + "_Sen";
		SootClass sc = sm.getDeclaringClass();
		if (sc.declaresMethodByName(senName))
			return sc.getMethodByName(senName);
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

	public SootField processField(SootField field) {
		SootClass sc = field.getDeclaringClass();
		String newName = field.getName() + "_Sen";
		if (sc.declaresFieldByName(newName))
			return sc.getFieldByName(newName);
		String kind = polyElements.get(field.getSignature());
		if (kind == null || kind.equals("@Clear"))
			return null;
		else if (kind.equals("@Poly")) {
			SootField newfield = new SootField(newName, field.getType(), field.getModifiers());
			sc.addField(newfield);
			return newfield;
		}
		return null;
	}
}
