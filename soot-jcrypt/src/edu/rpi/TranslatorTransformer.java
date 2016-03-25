package edu.rpi;

/* JCryptTranslator extends the abstract class BodyTransformer,
 * and implements <pre>internalTransform</pre> method.
 */
import soot.*;
import soot.jimple.AssignStmt;
import soot.jimple.FieldRef;
import soot.jimple.InstanceFieldRef;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.Stmt;

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
		SootMethod sm = b.getMethod();
		SootClass sc = (sm == null ? null : sm.getDeclaringClass());
		processMethods(sm, sc);
		// visitorState.setSootMethod(sm);
		// visitorState.setSootClass(sc);
		// TranslatorVisitor visitor = new TranslatorVisitor(this);
		//
		// final PatchingChain<Unit> units = b.getUnits();
		// for (Iterator<Unit> iter = units.snapshotIterator(); iter.hasNext();)
		// {
		// final Unit u = iter.next();
		// visitorState.setUnit(u);
		// u.apply(visitor);
		// }
		// visitorState.setSootMethod(null);
		// visitorState.setSootClass(null);
		// visitorState.setUnit(null);
	}

	private void processMethods(SootMethod sm, SootClass sc) {
		String kind = polyElements.get(sm.toString());
		if (kind == null || kind.equals("0"))
			return;
		else {
			String name = sm.getName();
			String newName = name + "_Sen";
			if (kind.equals("2")) { // @Poly: need two versions
				SootMethod method = new SootMethod(newName, sm.getParameterTypes(), sm.getReturnType(),
						sm.getModifiers(), sm.getExceptions());
				sc.addMethod(method);
				JimpleBody body = Jimple.v().newBody(method);
				body.importBodyContentsFrom(sm.retrieveActiveBody());
				changeFieldsInMethods(body);
				method.setActiveBody(body);
			} else { // @Sensitive: change to sensitive version
				sm.setName(newName);
				changeFieldsInMethods((JimpleBody) sm.retrieveActiveBody());
			}
			//polyElements.put(name, "0");
		}
		//processInvokes(sm.retrieveActiveBody());
	}

	private void processInvokes(SootMethod sm) {
		
	}

	private void changeFieldsInMethods(JimpleBody body) {
		Iterator<Unit> stmtIt = body.getUnits().snapshotIterator();
		while (stmtIt.hasNext()) {
			Stmt s = (Stmt) stmtIt.next();
			if (s instanceof AssignStmt && s.containsFieldRef()) {
				AssignStmt as = ( AssignStmt) s;
				FieldRef fr = as.getFieldRef();
				if (fr instanceof InstanceFieldRef) {
					SootField newf = processField(fr.getField());
					if (newf == null)
						continue;
					FieldRef newFieldRef = Jimple.v().newInstanceFieldRef(((InstanceFieldRef) fr).getBase(),
							newf.makeRef());
					if (as.getLeftOp() instanceof FieldRef)
						as.setLeftOp(newFieldRef);
					else
						as.setRightOp(newFieldRef);
				}
			}
		}
	}

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
