package edu.rpi.jcrypt;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.rpi.AnnotatedValue;
import edu.rpi.InferenceUtils;
import soot.Body;
import soot.BodyTransformer;
import soot.BooleanType;
import soot.CharType;
import soot.Local;
import soot.PrimType;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootFieldRef;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.javaToJimple.LocalGenerator;
import soot.jimple.AddExpr;
import soot.jimple.AssignStmt;
import soot.jimple.BinopExpr;
import soot.jimple.CastExpr;
import soot.jimple.ClassConstant;
import soot.jimple.DefinitionStmt;
import soot.jimple.FieldRef;
import soot.jimple.IdentityStmt;
import soot.jimple.IfStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.NewExpr;
import soot.jimple.NumericConstant;
import soot.jimple.ParameterRef;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.VirtualInvokeExpr;
import soot.jimple.internal.JIdentityStmt;
import soot.tagkit.AbstractHost;
import soot.tagkit.SignatureTag;
import soot.util.Chain;

import static com.esotericsoftware.minlog.Log.*;

public class TransformerTransformer extends BodyTransformer {

	// record classes whose generic types have been modified
	private static Set<SootClass> visited = new HashSet<>();
	private JCryptTransformer jt;
	private Set<String> polyValues;
	private Map<SootClass, SootMethod> modifiedMethod = new HashMap<>();
	private static SootClass encryptionClass;
	private Set<String> mapreducePrimTypes;
	private Set<String> parseMethods;
	private Map<String, Byte> encryptions;
	private SootMethod sm;
	private List<Type> paramTypes;
	
	public TransformerTransformer(JCryptTransformer jcryptTransformer, Set<String> polyValues, Map<String, Byte> map) {
		info(this.getClass().getSimpleName(), "Transforming ...");
		jt = jcryptTransformer;
		this.polyValues = polyValues;
		encryptions = map;
		Scene.v().addBasicClass("encryptUtil.EncryptUtil", SootClass.SIGNATURES);
		mapreducePrimTypes = new HashSet<>();
		mapreducePrimTypes.add("org.apache.hadoop.io.IntWritable");
		mapreducePrimTypes.add("org.apache.hadoop.io.LongWritable");
		mapreducePrimTypes.add("org.apache.hadoop.io.ShortWritable");
		mapreducePrimTypes.add("org.apache.hadoop.io.DoubleWritable");
		mapreducePrimTypes.add("org.apache.hadoop.io.ByteWritable");
		mapreducePrimTypes.add("org.apache.hadoop.io.BytesWritable");
		mapreducePrimTypes.add("org.apache.hadoop.io.FloatWritable");
		parseMethods = new HashSet<>();
		parseMethods.add("parseInt");
		parseMethods.add("parseDouble");
		parseMethods.add("parseLong");
		parseMethods.add("parseShort");
		parseMethods.add("parseByte");
		parseMethods.add("parseFloat");
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected synchronized void internalTransform(Body body, String arg1, Map arg2) {
		sm = body.getMethod();
		paramTypes = new LinkedList<>();
		String methodName = sm.getName();
		//System.out.println("*****" + sm.getSignature());
		if (methodName.equals("map") || methodName.equals("reduce")) {
			Chain<Unit> units = body.getUnits();
			Iterator<Unit> stmtIt = units.snapshotIterator();
			while (stmtIt.hasNext()) {
				Unit unit = stmtIt.next();
				if (unit instanceof DefinitionStmt) {
					modifyDefinitionStmt((DefinitionStmt) unit);
				} else if (unit instanceof IfStmt){
					modifyIfStmt((IfStmt) unit);
				} else if (unit instanceof InvokeStmt) {
					// only consider two cases here:
					// specialinvoke and virtualinvoke in volatile methods
					getInvokeExpr(((InvokeStmt) unit).getInvokeExpr());
				}
			}
			sm.setParameterTypes(paramTypes);
			modifyMethodLocals(body);
			modifiedMethod.put(sm.getDeclaringClass(), body.getMethod());
			//System.out.println("modifiedMethod: " + modifiedMethod.getSignature());
		}
		//System.out.println(sm.getName());
//		if (methodName.equals("map")) {
//			modifyClass(true);
//			modifyMapMethod(sm);
//			modifyMethodStmts(body, sm);
//			modifyMethodLocals(body);
//		} else if (methodName.equals("reduce")) {
//			modifyClass(false);
//			modifyReduceMethod(sm);
//			modifyMethodStmts(body, sm);
//			modifyMethodLocals(body);
//			modifyMethodSignatures(sm);
//		} else if (methodName.equals("run") || methodName.equals("main")) {
//			modifyRunMethod(body);
//		}
	}
	
	private void modifyDefinitionStmt(DefinitionStmt unit) {
		Value leftValue = unit.getLeftOp();
		Value rightValue = unit.getRightOp();
		String leftType = modifyTo(leftValue);
		if (unit instanceof AssignStmt) {
			if (leftType != null) {
				Value rightOp = getRightOpOfAssignStmt(rightValue, leftValue, leftType);
				if (rightOp != null)
					((AssignStmt) unit).setRightOp(rightOp);
			}
		} else if (rightValue instanceof ParameterRef) {
			if (leftType != null) {
				unit.getRightOpBox().setValue(new ParameterRef(RefType.v(leftType),
						((ParameterRef) rightValue).getIndex()));
				paramTypes.add(RefType.v(leftType));
			} else paramTypes.add(rightValue.getType());
		}
	}

	private Value getRightOpOfAssignStmt(Value rightValue, Value leftValue, String leftType) {
		if (rightValue instanceof NewExpr) {
			// $r6 = new org.apache.hadoop.io.IntWritable;
			// -> $r6 = new org.apache.hadoop.io.Text;
			((NewExpr) rightValue).setBaseType(RefType.v(leftType));
			return rightValue;
		} else if (rightValue instanceof InvokeExpr) {
			return getInvokeExpr((InvokeExpr) rightValue);
		} else if (rightValue instanceof AddExpr) {
			return getAddExpr(rightValue);
		} else if (rightValue instanceof NumericConstant) {
			return getNumericConstant(rightValue, leftValue);
		} else if (rightValue instanceof CastExpr) {
			// $r5 = (org.apache.hadoop.io.IntWritable) r1;
			((CastExpr) rightValue).setCastType(RefType.v(leftType));
			return rightValue;
		}
		return null;
	}

	private Value getNumericConstant(Value rightValue, Value leftValue) {
		// d1 = 0.0 ->
		// d1 = staticinvoke <encryptUtil.EncryptUtil: java.lang.String
		// getAH(double)>(0.0);
		String libMethodName = "";
		byte typeSet = encryptions.get(TransUtils.getIdenfication(leftValue, sm));
		if ((0b100 & typeSet) != 0) {
			libMethodName = "getAH";
		}
		encryptionClass = Scene.v().getSootClass("encryptUtil.EncryptUtil");
		SootMethod libMethod = encryptionClass
				.getMethod("java.lang.String " + libMethodName + "(" + rightValue.getType() + ")");
		return Jimple.v().newStaticInvokeExpr(libMethod.makeRef(), rightValue);
	}

	private Value getInvokeExpr(InvokeExpr expr) {
		if (expr instanceof StaticInvokeExpr) {
			// $i3 = staticinvoke <java.lang.Integer: int parseInt(java.lang.String)>($r8);
			// -> $i3 = $r8;
			if (parseMethods.contains(expr.getMethod().getName()))
				return expr.getArg(0);
		} else if (expr instanceof SpecialInvokeExpr) {
			// specialinvoke $r6.<org.apache.hadoop.io.IntWritable: void <init>(int)>($i3);
			// -> specialinvoke $r6.<org.apache.hadoop.io.Text: void <init>(java.lang.String)>($i3);
			Value receiver = ((SpecialInvokeExpr) expr).getBase();
			String type = modifyTo(receiver);
			if (type != null) {
				SootMethod toCall = Scene.v().getMethod("<org.apache.hadoop.io.Text: void <init>(java.lang.String)>");
				expr.setMethodRef(toCall.makeRef());
			}
		} else if (expr instanceof VirtualInvokeExpr) {
			if (Modifier.isVolatile(sm.getModifiers())) {
				// call map/reduce method in volatile map/reduce
				expr.setMethodRef(modifiedMethod.get(sm.getDeclaringClass()).makeRef());
				//System.out.println("Volatile: " + expr);
			} else {
				// $i0 = virtualinvoke r5.<org.apache.hadoop.io.IntWritable: int get()>();
				// -> $i0 = virtualinvoke r5.<org.apache.hadoop.io.Text: String toString()>();
				SootMethod invokeMethod = expr.getMethod();
				if (mapreducePrimTypes.contains(invokeMethod.getDeclaringClass().getName())
						&& invokeMethod.getName().equals("get")) {
					SootMethod toCall = Scene.v()
							.getMethod("<org.apache.hadoop.io.Text: java.lang.String toString()>");
					expr.setMethodRef(toCall.makeRef());
					return expr;
				}
			}
		}
		return null;
	}

	private void modifyRunMethod(Body body) {
		for (Unit unit : body.getUnits()) {
			if (unit instanceof InvokeStmt) {
				InvokeExpr invoke = ((InvokeStmt) unit).getInvokeExpr();
				if (invoke instanceof VirtualInvokeExpr) {
					String methodName = invoke.getMethod().getName();
					if ( (methodName.equals("setOutputKeyClass") 
							&& ( (jt.reduceKey == null && shouldModify(jt.mapOutKeys.get(0)))
									|| shouldModify(jt.reduceKey) ) )
							|| (methodName.equals("setOutputValueClass")
									&& ( (jt.reduceValue == null && shouldModify(jt.mapOutValues.get(0)))
											|| shouldModify(jt.reduceValue) ) ) ) {
						invoke.setArg(0, ClassConstant.v("org/apache/hadoop/io/Text"));
					}
				}
			}
		}
	}

	// only used to modify reduce method
//	private void modifyMethodSignatures(SootMethod sm) {
//		if (shouldModify(jt.mapOutKeys.get(0)) && shouldModify(sm)) {
//			List<Type> list = new ArrayList<>(sm.getParameterTypes());
//			list.remove(0);
//			list.add(0, RefType.v("org.apache.hadoop.io.Text"));
//			sm.setParameterTypes(list);
//			Local para = sm.getActiveBody().getParameterLocal(0);
//			para.setType(RefType.v("org.apache.hadoop.io.Text"));
//
//			SignatureTag sigTag = (SignatureTag) sm.getTag("SignatureTag");
//			if (sigTag == null)
//				return;
//			sm.removeTag("SignatureTag");
//			String signature = sigTag.getSignature();
//			int index = signature.indexOf(';');
//			SignatureTag tag = new SignatureTag("(Lorg/apache/hadoop/io/Text" + signature.substring(index));
//			sm.addTag(tag);
//			modifiedReduceMethod = sm;
//		}
//	}

//	private void modifyMethodStmts(Body body, SootMethod sm) {
//		Chain<Unit> units = body.getUnits();
//		Iterator<Unit> stmtIt = units.snapshotIterator();
//		while (stmtIt.hasNext()) {
//			Unit unit = stmtIt.next();
//			if (unit instanceof AssignStmt) {
//				modifyAssignStmt(sm, (AssignStmt) unit);
//			} else if (unit instanceof InvokeStmt) {
//				modifyInvokeStmt(sm, (InvokeStmt) unit);
//			} else if (unit instanceof IdentityStmt) {
//				// r1 := @parameter0: org.apache.hadoop.io.IntWritable;
//				Value rightOp = ((IdentityStmt) unit).getRightOp();
//				String type = modifyTo(((IdentityStmt) unit).getLeftOp());
//				if (type != null && rightOp instanceof ParameterRef) {
//					int index = ((ParameterRef) rightOp).getIndex();
//					if (index == 0)
//						((JIdentityStmt) unit).setRightOp(new ParameterRef(RefType.v(type), 0));
//				}
//			} else if (unit instanceof IfStmt) {
//				modifyIfStmt((IfStmt) unit);
//			}
//		}
//	}

	private void modifyIfStmt(IfStmt unit) {
		// if i0 <= $i1 =>
		// $z0 = staticinvoke <encryption.EncryptUtil: boolean isGt(java.lang.String, int)>(i0, $i1);
		// if $z0 == 0
		Value condition = unit.getCondition();
		if (condition instanceof BinopExpr) {
			Value leftOp = ((BinopExpr) condition).getOp1();
			Value rightOp = ((BinopExpr) condition).getOp2();
			String leftType = modifyTo(leftOp);
			String rightType = modifyTo(rightOp);
			if (leftType == null && rightType == null) return;
			if (leftType == null) leftType = leftOp.getType().toString();
			else if (rightType == null) rightType = rightOp.getType().toString();
			
			// make a new invoke statement and insert it into the chain
			String op = ((BinopExpr) condition).getSymbol();
			String libMethodName = "";
			switch (op) {
			case " <= ": libMethodName = "isGt";
			}
			if (libMethodName.isEmpty()) return;
			encryptionClass = Scene.v().getSootClass("encryptUtil.EncryptUtil");
			SootMethod libMethod = encryptionClass.getMethod("boolean "
					+ libMethodName + "(" + leftType + "," + rightType + ")");
			InvokeExpr comExpr = Jimple.v().newStaticInvokeExpr(libMethod.makeRef(), leftOp, rightOp);
			
			// add a local $z0
			Body body = sm.getActiveBody();
			LocalGenerator lg = new LocalGenerator(body);
			Local tmpLocal = lg.generateLocal(BooleanType.v());
						
			AssignStmt toAdd = Jimple.v().newAssignStmt(tmpLocal, comExpr);
			body.getUnits().insertBefore(toAdd, unit);
			
			// make $z0 == 0 and replace if condition with it
			BinopExpr biExpr = Jimple.v().newEqExpr(tmpLocal, IntConstant.v(0));
			unit.setCondition(biExpr);
		}
	}

//	private void modifyInvokeStmt(SootMethod sm, InvokeStmt unit) {
//		// specialinvoke $r6.<org.apache.hadoop.io.IntWritable: void
//		// <init>(int)>($i3);
//		// -> specialinvoke $r6.<org.apache.hadoop.io.Text: void
//		// <init>(java.lang.String)>($i3);
//		InvokeExpr invoke = unit.getInvokeExpr();
//		if (invoke instanceof InstanceInvokeExpr) {
//			Value receiver = ((SpecialInvokeExpr) invoke).getBase();
//			String type = modifyTo(receiver);
//			if (type != null) {
//				SootMethod toCall = Scene.v().getMethod("<org.apache.hadoop.io.Text: void <init>(java.lang.String)>");
//				invoke.setMethodRef(toCall.makeRef());
//			} else {
//				SootMethodRef methodRef = invoke.getMethodRef();
//				List<Type> newParameterTypes = new LinkedList<>();
//				for (Value argument : invoke.getArgs()) {
//					String argType = modifyTo(argument);
//					if (argType != null) {
//						modifyCustomerMethod(invoke.getMethod());
//						newParameterTypes.add(RefType.v(argType));
//					} else newParameterTypes.add(argument.getType());
//				}
//				methodRef = Scene.v().makeMethodRef(methodRef.declaringClass(), methodRef.name(),
//						newParameterTypes, methodRef.returnType(), methodRef.isStatic());
//				invoke.setMethodRef(methodRef);
//			}
//		} else if (invoke instanceof VirtualInvokeExpr && soot.Modifier.isVolatile(sm.getModifiers())) {
//			if (modifiedReduceMethod != null) {
//				invoke.setMethodRef(modifiedReduceMethod.makeRef());
//			}
//		}
//	}

	private void modifyAssignStmt(SootMethod sm, AssignStmt unit) {
		Value leftOp = unit.getLeftOp();
 		String type = modifyTo(leftOp);
		if (type == null) return;
		Value rightOp = unit.getRightOp();
		if (rightOp instanceof NewExpr) {
			// $r6 = new org.apache.hadoop.io.IntWritable;
			// -> $r6 = new org.apache.hadoop.io.Text;
			((NewExpr) rightOp).setBaseType(RefType.v(type));
		} else if (rightOp instanceof StaticInvokeExpr) {
			// $i3 = staticinvoke <java.lang.Integer: int
			// parseInt(java.lang.String)>($r8);
			// -> $i3 = $r8;
			// $r24 = staticinvoke <java.lang.Double: java.lang.Double valueOf(java.lang.String)>(r9)
			// -> $r24 = r9;
			String parseMethod = ((StaticInvokeExpr) rightOp).getMethod().getName();
			if (parseMethods.contains(parseMethod)) { // || parseMethod.equals("valueOf")) {
				unit.setRightOp(((StaticInvokeExpr) rightOp).getArg(0));
			}
		} else if (rightOp instanceof VirtualInvokeExpr) {
			// $i0 = virtualinvoke r5.<org.apache.hadoop.io.IntWritable: int get()>();
			// -> $i0 = virtualinvoke r5.<org.apache.hadoop.io.Text: String toString()>();
			SootMethod invokeMethod = ((VirtualInvokeExpr) rightOp).getMethod();
			SootClass sc = invokeMethod.getDeclaringClass();
			if (mapreducePrimTypes.contains(sc.getName())
					&& invokeMethod.getName().equals("get")) {
				SootMethod toCall = Scene.v()
						.getMethod("<org.apache.hadoop.io.Text: java.lang.String toString()>");
				((VirtualInvokeExpr) rightOp).setMethodRef(toCall.makeRef());
			}
		} else if (rightOp instanceof CastExpr) {
			// $r5 = (org.apache.hadoop.io.IntWritable) r1;
			((CastExpr) rightOp).setCastType(RefType.v(type));
		} else if (rightOp instanceof AddExpr) {
			//modifyAddExpr(rightOp);
		} else if (rightOp instanceof NumericConstant) {
			// d1 = 0.0 -> 
			// d1 = staticinvoke <encryptUtil.EncryptUtil: java.lang.String getAH(double)>(0.0);
			String libMethodName = "";
			byte typeSet = encryptions.get(TransUtils.getIdenfication(leftOp, sm));
			if ((0b100 & typeSet) != 0) {
				libMethodName = "getAH";
			}
			encryptionClass = Scene.v().getSootClass("encryptUtil.EncryptUtil");
			SootMethod libMethod = encryptionClass.getMethod("java.lang.String "
					+ libMethodName + "(" + rightOp.getType() + ")");
			InvokeExpr getExpr = Jimple.v().newStaticInvokeExpr(libMethod.makeRef(), rightOp);
			unit.setRightOp(getExpr);
		}
	}

	private Value getAddExpr(Value rightOp) {
		// $d1 + $d2 -> staticinvoke <encryption.EncryptUtil: java.lang.String add(java.lang.String, int)>($d1, $d2);
		Value op1 = ((AddExpr) rightOp).getOp1();
		Value op2 = ((AddExpr) rightOp).getOp2();
		String op1Type = modifyTo(op1);
		String op2Type = modifyTo(op2);
		if (op1Type == null) op1Type = op1.getType().toString();
		if (op2Type == null) op2Type = op2.getType().toString();
		encryptionClass = Scene.v().getSootClass("encryptUtil.EncryptUtil");
		SootMethod libMethod = encryptionClass.getMethod("java.lang.String add(" + op1Type + "," + op2Type + ")");
		return Jimple.v().newStaticInvokeExpr(libMethod.makeRef(), op1, op2);
	}

	private void modifyMapMethod(SootMethod sm) {
		Set<Integer> index = new HashSet<>();
		if (shouldModify(jt.mapOutKeys.get(0))) {
			index.add(0);
		}
		if (shouldModify(jt.mapOutValues.get(0))) {
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
			if (shouldModify(jt.mapOutKeys.get(0))) {
				mapIndex.add(2);
				reduceIndex.add(0);
			}
			if (shouldModify(jt.mapOutValues.get(0))) {
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
			if (shouldModify(jt.mapOutValues.get(0))) {
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
	
	private void modifyMethodLocals(Body body) {
		for (Local local : body.getLocals()) {
			String type = modifyTo(local);
			if (type != null)
				local.setType(RefType.v(type));
		}
	}

	private boolean isPrimitive(Type type) {
		return type instanceof PrimType && !(type instanceof BooleanType) && !(type instanceof CharType);
	}

	private String modifyTo(Value value) {
		//System.out.println(value);
		if (!polyValues.contains(TransUtils.getIdenfication(value, sm)))
			return null;
		Type type = value.getType();
		if (mapreducePrimTypes.contains(type.toString())) return "org.apache.hadoop.io.Text";
		if (isPrimitive(type)) return "java.lang.String";
		return null;
	}
	
	// only for reduce method
	private boolean shouldModify(SootMethod sm) {
		String type = sm.getParameterType(0).toString();
		return mapreducePrimTypes.contains(type);
	}

	// only used to check map/reduce key/value for class/method signatures (generic types)
	private boolean shouldModify(AnnotatedValue value) {
		return value != null && !value.containsAnno(jt.CLEAR)
				&& mapreducePrimTypes.contains(value.getType().toString());
	}
	
	private void modifyCustomerMethod(SootMethod theMethod) {
		System.out.println("Modifying " + theMethod);
		if (theMethod.hasActiveBody()) 
			System.out.println(true);
		else System.out.println(false);

//
//		if (modifiedMethods.contains(theMethod))
//			return;
//		modifiedMethods.add(theMethod);
//		
//		Type returnType = theMethod.getReturnType();
//		if (isPrimitive(returnType) && InferenceUtils.overriddenMethods(theMethod).size() == 0) {
//			theMethod.setReturnType(RefType.v("java.lang.String"));
//		}
//
//		// Modify parameter type for all methods
//		List<Type> paramTypes = new LinkedList<>();
//		for (Iterator<Type> oldParamTypes = theMethod.getParameterTypes().iterator(); oldParamTypes.hasNext();) {
//			Type type = (Type) oldParamTypes.next();
//			if (isPrimitive(type)) {
//				paramTypes.add(RefType.v("java.lang.String"));
//			} else {
//				paramTypes.add(type);
//			}
//		}
//		theMethod.setParameterTypes(paramTypes);
//
//		Body newBody = theMethod.retrieveActiveBody();
//		// Modify local type for all methods
//		for (Iterator<Local> locals = newBody.getLocals().iterator(); locals.hasNext();) {
//			Local local = (Local) locals.next();
//			Type type = local.getType();
//			if (isPrimitive(type)) {
//				local.setType(RefType.v("java.lang.String"));
//			}
//		}
//
//		// modify statements inside methods
//		Iterator<Unit> j = newBody.getUnits().iterator();
//		while (j.hasNext()) {
//			Unit unit = (Unit) j.next();
//			Iterator<ValueBox> boxes = unit.getUseAndDefBoxes().iterator();
//			while (boxes.hasNext()) {
//				ValueBox box = (ValueBox) boxes.next();
//				Value value = box.getValue();
//				if (value instanceof FieldRef) {
//					// Fix references to fields
//					FieldRef r = (FieldRef) value;
//					SootFieldRef fieldRef = r.getFieldRef();
//					if (isPrimitive(fieldRef.type())) {
//						r.setFieldRef(Scene.v().makeFieldRef(fieldRef.declaringClass(), fieldRef.name(),
//								RefType.v("java.lang.String"), fieldRef.isStatic()));
//					}
//					fieldRef = r.getFieldRef();
//				} else if (value instanceof CastExpr) {
//					// Fix casts
//					CastExpr r = (CastExpr) value;
//					Type type = r.getType();
//					if (isPrimitive(type)) {
//						r.setCastType(RefType.v("java.lang.String"));
//					}
//				} else if (value instanceof ParameterRef) {
//					// Fix references to a parameter
//					ParameterRef r = (ParameterRef) value;
//					Type type = r.getType();
//					if (isPrimitive(type)) {
//						box.setValue(Jimple.v().newParameterRef(RefType.v("java.lang.String"), r.getIndex()));
//					}
//				} else if (value instanceof InvokeExpr) {
//					// Fix up the method invokes.
//					InvokeExpr r = (InvokeExpr) value;
//					SootMethodRef methodRef = r.getMethodRef();
//					List<Type> newParameterTypes = new LinkedList<>();
//					for (Iterator<Type> i = methodRef.parameterTypes().iterator(); i.hasNext();) {
//						Type type = (Type) i.next();
//						if (isPrimitive(type)) {
//							newParameterTypes.add(RefType.v("java.lang.String"));
//						} else {
//							newParameterTypes.add(type);
//						}
//					}
//					Type newReturnType = methodRef.returnType();
//					if (isPrimitive(newReturnType)) {
//						newReturnType = RefType.v("java.lang.String");
//					}
//
//					// Update the parameter types and the return type.
//					methodRef = Scene.v().makeMethodRef(methodRef.declaringClass(), methodRef.name(), newParameterTypes,
//							newReturnType, methodRef.isStatic());
//					r.setMethodRef(methodRef);
//				}
//			}
//		}
	}

}
