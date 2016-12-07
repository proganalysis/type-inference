package edu.rpi.jcrypt;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.rpi.AnnotatedValue;
import soot.Body;
import soot.BodyTransformer;
import soot.BooleanType;
import soot.Local;
import soot.PrimType;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.IdentityStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.NewExpr;
import soot.jimple.ParameterRef;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.internal.JIdentityStmt;
import soot.tagkit.AbstractHost;
import soot.tagkit.SignatureTag;

public class TransformerTransformer extends BodyTransformer {

	private static Set<SootClass> visited = new HashSet<>();
	private JCryptTransformer jt;
	private Set<String> polyValues;
	public TransformerTransformer(JCryptTransformer jcryptTransformer, Set<String> polyValues) {
		jt = jcryptTransformer;
		this.polyValues = polyValues;
	}

	@Override
	protected void internalTransform(Body body, String arg1, Map<String, String> arg2) {
		SootMethod sm = body.getMethod();
		if (soot.Modifier.isVolatile(sm.getModifiers()))
			return;
		String methodName = sm.getName();
		if (methodName.equals("map")) {
			modifyClass(true);
			modifyMapMethod(sm);
			modifyMethodStmts(body, sm, true);
			modifyMethodLocals(body, sm);
		} else if (methodName.equals("reduce")) {
			modifyClass(false);
			modifyReduceMethod(sm);
			modifyMethodSignatures(sm);
			modifyMethodStmts(body, sm, false);
			modifyMethodLocals(body, sm);
		}

	}
	
	private void modifyMethodSignatures(SootMethod sm) {
		if (shouldModify(jt.mapKey)) {
			List<Type> list = new ArrayList<>(sm.getParameterTypes());
			if (list.isEmpty()) return;
			list.remove(0);
			list.add(0, RefType.v("org.apache.hadoop.io.Text"));
			sm.setParameterTypes(list);
			Local para = sm.getActiveBody().getParameterLocal(0);
			para.setType(RefType.v("org.apache.hadoop.io.Text"));
		}
	}

	private void modifyMethodLocals(Body body, SootMethod sm) {
		for (Local local : body.getLocals()) {
			if (shouldModify(local, sm)) {
				local.setType(RefType.v("org.apache.hadoop.io.Text"));
			}
			if (polyValues.contains(TransUtils.getIdenfication(local, sm))
					&& local.getType() instanceof PrimType
					&& !(local.getType() instanceof BooleanType)) {
				local.setType(RefType.v("java.lang.String"));
			}
		}
	}

	private void modifyMethodStmts(Body body, SootMethod sm, boolean isMap) {
		for (Unit unit : body.getUnits()) {
			if (unit instanceof AssignStmt) {
				Value rightOp = ((AssignStmt) unit).getRightOp();
				Value leftOp = ((AssignStmt) unit).getLeftOp();
				if (rightOp instanceof NewExpr) {
					// $r6 = new org.apache.hadoop.io.IntWritable;
					// -> $r6 = new org.apache.hadoop.io.Text;
					if (shouldModify(leftOp, sm)) {
						((NewExpr) rightOp).setBaseType(RefType.v("org.apache.hadoop.io.Text"));
					}
				} else if (rightOp instanceof StaticInvokeExpr) {
					// $i3 = staticinvoke <java.lang.Integer: int parseInt(java.lang.String)>($r8);
					// -> $i3 = $r8;
					if (((StaticInvokeExpr) rightOp).getMethod().getName().equals("parseInt")
							&& polyValues.contains(TransUtils.getIdenfication(leftOp, sm))) {
						((AssignStmt) unit).setRightOp(((StaticInvokeExpr) rightOp).getArg(0));
					}
				}
			} else if (unit instanceof InvokeStmt) {
				// specialinvoke $r6.<org.apache.hadoop.io.IntWritable: void <init>(int)>($i3);
				// -> specialinvoke $r6.<org.apache.hadoop.io.Text: void <init>(java.lang.String)>($i3);
				InvokeExpr invoke = ((InvokeStmt) unit).getInvokeExpr();
				if (invoke instanceof SpecialInvokeExpr) {
					Value receiver = ((SpecialInvokeExpr) invoke).getBase();
					if (shouldModify(receiver, sm)) {
						SootMethod toCall = Scene.v()
								.getMethod("<org.apache.hadoop.io.Text: void <init>(java.lang.String)>");
						invoke.setMethodRef(toCall.makeRef());
					}
				}
			} else if (!isMap && unit instanceof IdentityStmt) {
				Value rightOp = ((IdentityStmt) unit).getRightOp();
				if (shouldModify(jt.mapKey) && rightOp instanceof ParameterRef) {
					int index = ((ParameterRef) rightOp).getIndex();
					if (index == 0)
						((JIdentityStmt) unit).setRightOp(new ParameterRef(RefType.v("org.apache.hadoop.io.Text"), 0));
				}
			}
		}
	}

	private boolean shouldModify(Value value, SootMethod sm) {
		if (!polyValues.contains(TransUtils.getIdenfication(value, sm)))
			return false;
		String type = value.getType().toString();
		return type.startsWith("org.apache.hadoop.io.") && !type.equals("org.apache.hadoop.io.Text");
	}

	private void modifyMapMethod(SootMethod sm) {
		Set<Integer> index = new HashSet<>();
		if (shouldModify(jt.mapKey)) {
			index.add(0);
		}
		if (shouldModify(jt.mapValue)) {
			index.add(1);
		}
		modifyGenericType(sm, index);
	}

	private void modifyReduceMethod(SootMethod sm) {
		Set<Integer> index = new HashSet<>();
		if (shouldModify(jt.reduceKey)) {
			index.add(0);
		}
		if (shouldModify(jt.reduceValue)) {
			index.add(1);
		}
		modifyGenericType(sm, index);
	}

	private void modifyClass(boolean isMap) {
		for (SootClass sc : Scene.v().getApplicationClasses()) {
			if (visited.contains(sc))
				continue;
			else
				visited.add(sc);
			Set<Integer> mapIndex = new HashSet<>();
			Set<Integer> reduceIndex = new HashSet<>();
			if (shouldModify(jt.mapKey)) {
				mapIndex.add(2);
				reduceIndex.add(0);
			}
			if (shouldModify(jt.mapValue)) {
				mapIndex.add(3);
				reduceIndex.add(1);
			}
			if (shouldModify(jt.reduceKey)) {
				reduceIndex.add(2);
			}
			if (shouldModify(jt.reduceValue)) {
				reduceIndex.add(3);
			}
			if (isMap)
				modifyGenericType(sc, mapIndex);
			else
				modifyGenericType(sc, reduceIndex);
		}
	}

	private boolean shouldModify(AnnotatedValue value) {
		return !value.containsAnno(jt.CLEAR) && !value.getType().toString().equals("org.apache.hadoop.io.Text")
				&& value.getType().toString().startsWith("org.apache.hadoop.io.");
	}

	private void modifyGenericType(AbstractHost sc, Set<Integer> index) {
		SignatureTag sigTag = (SignatureTag) sc.getTag("SignatureTag");
		if (sigTag == null)
			return;
		sc.removeTag("SignatureTag");
		String signature = sigTag.getSignature();
		int start = signature.indexOf('<') + 1;
		int end = signature.indexOf('>');
		String[] genericTypes = signature.substring(start, end).split(";");
		if (genericTypes.length == 1) { // iterator<Text>
			if (shouldModify(jt.mapValue)) {
				signature = signature.substring(0, start) + "Lorg/apache/hadoop/io/Text;" + signature.substring(end);
			}
			start = signature.indexOf('<', start) + 1;
			end = signature.indexOf('>', start);
			genericTypes = signature.substring(start, end).split(";");
		}
		for (int i : index) {
			genericTypes[i] = "Lorg/apache/hadoop/io/Text";
		}
		StringBuilder builder = new StringBuilder();
		for (String s : genericTypes) {
			builder.append(s + ";");
		}
		SignatureTag tag = new SignatureTag(
				signature.substring(0, start) + builder.toString() + signature.substring(end));
		sc.addTag(tag);
	}

}
