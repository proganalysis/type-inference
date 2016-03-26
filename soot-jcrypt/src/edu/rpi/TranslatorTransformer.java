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

	private VisitorState visitorState = new VisitorState();
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
		// SootMethod sm = b.getMethod();
		// SootClass sc = (sm == null ? null : sm.getDeclaringClass());
		// processMethods(sm, sc);
		// // visitorState.setSootMethod(sm);
		// visitorState.setSootClass(sc);
		// TranslatorVisitor visitor = new TranslatorVisitor(this);
		//
		SootMethod senMethod = copyMethod(b);
		if (senMethod == null) {
			// the current method is clear; only original/clear version
			// need to process invokes inside
			processClearMethod(b);
		} else {
			// the current method needs two versions
			// need to change fields/invokes inside the sensitive one
			processSenMethod(senMethod.getActiveBody());
		}
		
		// visitorState.setSootMethod(null);
		// visitorState.setSootClass(null);
		// visitorState.setUnit(null);
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
	
	private void processInvokeForSen(Value v) {
		VirtualInvokeExpr expr = (VirtualInvokeExpr) v;
		SootMethod method = expr.getMethod();
		method.setName(method.getName()+ "_Sen");
	}

	private void processInvokeForClear(Value v, SootMethod sm) {
		VirtualInvokeExpr expr = (VirtualInvokeExpr) v;
		Value base = expr.getBase();
		String kind = polyElements.get(sm.getSignature() + "@" + base.toString());
		if (kind == null) return;
		if (!kind.equals("@Clear")) {
			SootMethod method = expr.getMethod();
			method.setName(method.getName()+ "_Sen");
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

	private SootMethod copyMethod(Body b) {
		SootMethod sm = b.getMethod();
		String kind = polyElements.get(sm.toString());
		if (kind == null || kind.equals("0"))
			return null;
		else {
			String newName = sm.getName() + "_Sen";
			// if (kind.equals("2")) { // @Poly: need two versions
			SootMethod method = new SootMethod(newName, sm.getParameterTypes(), sm.getReturnType(), sm.getModifiers(),
					sm.getExceptions());
			SootClass sc = sm.getDeclaringClass();
			sc.addMethod(method);
			JimpleBody body = Jimple.v().newBody(method);
			body.importBodyContentsFrom(sm.retrieveActiveBody());
			// changeFieldsInMethods(body);
			method.setActiveBody(body);
			return method;
			// } else { // @Sensitive: change to sensitive version
			// sm.setName(newName);
			// changeFieldsInMethods((JimpleBody) sm.retrieveActiveBody());
			// }
			// polyElements.put(name, "0");
		}
		// processInvokes(sm.retrieveActiveBody());
	}

//	private void changeFieldsInMethods(JimpleBody body) {
//		Iterator<Unit> stmtIt = body.getUnits().snapshotIterator();
//		while (stmtIt.hasNext()) {
//			Stmt s = (Stmt) stmtIt.next();
//			if (s instanceof AssignStmt && s.containsFieldRef()) {
//				AssignStmt as = ( AssignStmt) s;
//				FieldRef fr = as.getFieldRef();
//				if (fr instanceof InstanceFieldRef) {
//					SootField newf = processField(fr.getField());
//					if (newf == null)
//						continue;
//					FieldRef newFieldRef = Jimple.v().newInstanceFieldRef(((InstanceFieldRef) fr).getBase(),
//							newf.makeRef());
//					if (as.getLeftOp() instanceof FieldRef)
//						as.setLeftOp(newFieldRef);
//					else
//						as.setRightOp(newFieldRef);
//				}
//			}
//		}
//	}

	public VisitorState getVisitorState() {
		return visitorState;
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
