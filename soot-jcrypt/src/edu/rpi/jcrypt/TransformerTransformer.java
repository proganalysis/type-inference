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
import edu.rpi.AnnotatedValue.AdaptValue;
import edu.rpi.AnnotatedValue.Kind;
import edu.rpi.Constraint;
import edu.rpi.InferenceUtils;
import soot.Body;
import soot.BodyTransformer;
import soot.BooleanType;
import soot.CharType;
import soot.IntType;
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
		Scene.v().addBasicClass("java.util.HashMap", SootClass.SIGNATURES);
		Scene.v().addBasicClass("java.util.TreeMap", SootClass.SIGNATURES);
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
					InvokeExpr invokeExpr = ((InvokeStmt) unit).getInvokeExpr();
					if (unit.toString().contains("Set: boolean add(java.lang.Object)"))
						modifySetAdd((VirtualInvokeExpr) invokeExpr, unit);
					else
						// specialinvoke and virtualinvoke in volatile methods
						getInvokeExpr(invokeExpr);
				}
			}
			sm.setParameterTypes(paramTypes);
			modifyMethodLocals(body);
			modifiedMethod.put(sm.getDeclaringClass(), body.getMethod());
		} else if (methodName.equals("run") || methodName.equals("main"))
			modifyRunMethod(body);

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
	}
	
	private void modifySetAdd(VirtualInvokeExpr invokeExpr, Unit unit) {
		if (!polyValues.contains(TransUtils.getIdenfication(invokeExpr.getBase(), sm)))
			return;
		Body body = sm.getActiveBody();
		LocalGenerator lg = new LocalGenerator(body);
		String className = "org.apache.hadoop.io.Text";
		Type textType = RefType.v(className);
		// $r33 = (org.apache.hadoop.io.Text) $r32;
		Local r33 = lg.generateLocal(textType);
		Value r32 = invokeExpr.getArg(0);
		CastExpr castExpr = Jimple.v().newCastExpr(r32, textType);
		AssignStmt toAdd = Jimple.v().newAssignStmt(r33, castExpr);
		body.getUnits().insertBefore(toAdd, unit);
		// r43 = virtualinvoke $r33.<org.apache.hadoop.io.Text: java.lang.String toString()>();
		Local r43 = lg.generateLocal(RefType.v("java.lang.String"));
		InvokeExpr virtualExpr = Jimple.v().newVirtualInvokeExpr(r33,
				Scene.v().getSootClass(className).getMethodByName("toString").makeRef());
		toAdd = Jimple.v().newAssignStmt(r43, virtualExpr);
		body.getUnits().insertBefore(toAdd, unit);
		// i2 = virtualinvoke r43.<java.lang.String: int indexOf(int)>(35);
		Local i2 = lg.generateLocal(IntType.v());
		virtualExpr = Jimple.v().newVirtualInvokeExpr(r43,
				Scene.v().getMethod("<java.lang.String: int indexOf(int)>").makeRef(), IntConstant.v(35));
		toAdd = Jimple.v().newAssignStmt(i2, virtualExpr);
		body.getUnits().insertBefore(toAdd, unit);
		// $r34 = new org.apache.hadoop.io.Text;
		Local r34 = lg.generateLocal(textType);
		NewExpr newExpr = Jimple.v().newNewExpr(RefType.v(className));
		toAdd = Jimple.v().newAssignStmt(r34, newExpr);
		body.getUnits().insertBefore(toAdd, unit);
		// $r35 = virtualinvoke r43.<java.lang.String: java.lang.String substring(int,int)>(0, i2);
		Local r35 = lg.generateLocal(RefType.v("java.lang.String"));
		virtualExpr = Jimple.v().newVirtualInvokeExpr(r43,
				Scene.v().getMethod("<java.lang.String: java.lang.String substring(int,int)>").makeRef(),
				IntConstant.v(0), i2);
		toAdd = Jimple.v().newAssignStmt(r35, virtualExpr);
		body.getUnits().insertBefore(toAdd, unit);
		// specialinvoke $r34.<org.apache.hadoop.io.Text: void <init>(java.lang.String)>($r35);
		InvokeExpr specialExpr = Jimple.v().newSpecialInvokeExpr(r34,
				Scene.v().getMethod("<org.apache.hadoop.io.Text: void <init>(java.lang.String)>").makeRef(), r35);
		InvokeStmt invokeStmt = Jimple.v().newInvokeStmt(specialExpr);
		body.getUnits().insertBefore(invokeStmt, unit);
		// $r36 = new org.apache.hadoop.io.Text;
		Local r36 = lg.generateLocal(textType);
		newExpr = Jimple.v().newNewExpr(RefType.v(className));
		toAdd = Jimple.v().newAssignStmt(r36, newExpr);
		body.getUnits().insertBefore(toAdd, unit);
		// $r37 = virtualinvoke r43.<java.lang.String: java.lang.String substring(int)>(i2);
		Local r37 = lg.generateLocal(RefType.v("java.lang.String"));
		virtualExpr = Jimple.v().newVirtualInvokeExpr(r43,
				Scene.v().getMethod("<java.lang.String: java.lang.String substring(int)>").makeRef(), i2);
		toAdd = Jimple.v().newAssignStmt(r37, virtualExpr);
		body.getUnits().insertBefore(toAdd, unit);
		// specialinvoke $r36.<org.apache.hadoop.io.Text: void <init>(java.lang.String)>($r37);
		specialExpr = Jimple.v().newSpecialInvokeExpr(r36,
				Scene.v().getMethod("<org.apache.hadoop.io.Text: void <init>(java.lang.String)>").makeRef(), r37);
		invokeStmt = Jimple.v().newInvokeStmt(specialExpr);
		body.getUnits().insertBefore(invokeStmt, unit);
		// virtualinvoke $r9.<java.util.HashMap: java.lang.Object put(java.lang.Object,java.lang.Object)>($r34, $r36);
		String invokeClass = invokeExpr.getMethod().getDeclaringClass().getName().replace("Set", "Map");
		virtualExpr = Jimple.v().newVirtualInvokeExpr((Local) ((VirtualInvokeExpr) invokeExpr).getBase(),
				Scene.v().getMethod("<" + invokeClass + ": java.lang.Object put(java.lang.Object,java.lang.Object)>").makeRef(), r34, r36);
		invokeStmt = Jimple.v().newInvokeStmt(virtualExpr);
		body.getUnits().insertBefore(invokeStmt, unit);
		body.getUnits().remove(unit);
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
			} else if (rightValue instanceof VirtualInvokeExpr)
				modifySetIterator((VirtualInvokeExpr) rightValue, (AssignStmt) unit);
		} else if (rightValue instanceof ParameterRef) {
			if (leftType != null) {
				unit.getRightOpBox().setValue(new ParameterRef(RefType.v(leftType),
						((ParameterRef) rightValue).getIndex()));
				paramTypes.add(RefType.v(leftType));
			} else paramTypes.add(rightValue.getType());
		}
	}
	
	private void modifySetIterator(VirtualInvokeExpr invokeExpr, AssignStmt unit) {
		String methodSignature = invokeExpr.getMethod().getSignature();
		if (!methodSignature.equals("<java.util.HashSet: java.util.Iterator iterator()>")
				&& !methodSignature.equals("<java.util.TreeSet: java.util.Iterator iterator()>")) return;
		Body body = sm.getActiveBody();
		LocalGenerator lg = new LocalGenerator(body);
		Value r9 = invokeExpr.getBase();
		// $r11 = virtualinvoke $r9.<java.util.HashMap: java.util.Collection values()>();
		Local r11 = lg.generateLocal(RefType.v("java.util.Collection"));
		String invokeClass = invokeExpr.getMethod().getDeclaringClass().getName().replace("Set", "Map");
		InvokeExpr virtualExpr = Jimple.v().newVirtualInvokeExpr((Local) r9,
				Scene.v().getSootClass(invokeClass).getMethodByName("values").makeRef());
		AssignStmt toAdd = Jimple.v().newAssignStmt(r11, virtualExpr);
		body.getUnits().insertBefore(toAdd, unit);
		// r45 = interfaceinvoke $r11.<java.util.Collection: java.util.Iterator iterator()>();
		InvokeExpr interfaceExpr = Jimple.v().newInterfaceInvokeExpr(r11,
				Scene.v().getSootClass("java.util.Collection").getMethodByName("iterator").makeRef());
		unit.setRightOp(interfaceExpr);
	}

	private Value getRightOpOfAssignStmt(Value rightValue, Value leftValue, String leftType) {
		if (rightValue instanceof NewExpr) {
			// $r6 = new org.apache.hadoop.io.IntWritable;
			// -> $r6 = new org.apache.hadoop.io.Text;
			// $r9 = new java.util.HashSet; -> $r9 = new java.util.HashMap;
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
			if (parseMethods.contains(expr.getMethod().getName())) {
				Value arg = expr.getArg(0);
				if (polyValues.contains(TransUtils.getIdenfication(arg, sm)))
					return expr.getArg(0);
			}
		} else if (expr instanceof SpecialInvokeExpr) {
			// specialinvoke $r6.<org.apache.hadoop.io.IntWritable: void <init>(int)>($i3);
			// -> specialinvoke $r6.<org.apache.hadoop.io.Text: void <init>(java.lang.String)>($i3);
			// specialinvoke $r9.<java.util.HashSet: void <init>()>();
			// -> specialinvoke $r9.<java.util.HashMap: void <init>()>();
			Value receiver = ((SpecialInvokeExpr) expr).getBase();
			String type = modifyTo(receiver);
			if (type != null) {
				String methodSignature = type.startsWith("java") ? "<java.util.HashMap: void <init>()>"
						: "<org.apache.hadoop.io.Text: void <init>(java.lang.String)>";
				SootMethod toCall = Scene.v().getMethod(methodSignature);
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
					if (polyValues.contains(TransUtils.getIdenfication(((VirtualInvokeExpr) expr).getBase(), sm))) {
							SootMethod toCall = Scene.v()
								.getMethod("<org.apache.hadoop.io.Text: java.lang.String toString()>");
							expr.setMethodRef(toCall.makeRef());
							return expr;
					}
				}
			}
		}
		return null;
	}

	private void modifyRunMethod(Body body) {
		List<String> mapperClasses = new ArrayList<>(), reducerClasses = new ArrayList<>();
		List<InvokeExpr> outKeyExprs = new ArrayList<>(), outValueExprs = new ArrayList<>(),
				mapOutKeyExprs = new ArrayList<>(), mapOutValueExprs = new ArrayList<>();
		for (Unit unit : body.getUnits()) {
			if (unit instanceof InvokeStmt) {
				InvokeExpr invoke = ((InvokeStmt) unit).getInvokeExpr();
				if (invoke instanceof VirtualInvokeExpr) {
					String methodName = invoke.getMethod().getName();
					if (methodName.equals("setMapperClass")) {
						String className = ((ClassConstant) invoke.getArg(0)).getValue().replace('/', '.');
						mapperClasses.add(className);
					} else if (methodName.equals("setReducerClass")) {
						String className = ((ClassConstant) invoke.getArg(0)).getValue().replace('/', '.');
						reducerClasses.add(className);
					} else if (methodName.equals("setOutputKeyClass")) outKeyExprs.add(invoke);
					else if (methodName.equals("setOutputValueClass")) outValueExprs.add(invoke);
					else if (methodName.equals("setMapOutputKeyClass")) mapOutKeyExprs.add(invoke);
					else if (methodName.equals("setMapOutputValueClass")) mapOutValueExprs.add(invoke);
				}
			}
		}
		String out1 = "lib-<org.apache.hadoop.mapreduce.TaskInputOutputContext: void write(java.lang.Object,java.lang.Object)>@parameter";
		String out2 = "lib-<org.apache.hadoop.mapred.OutputCollector: void collect(java.lang.Object,java.lang.Object)>@parameter";
		String outkey1 = out1 + "0", outkey2 = out2 + "0";
		String outvalue1 = out1 + "1", outvalue2 = out2 + "1";
		Map<String, List<Boolean>> keyType = new HashMap<>(), valueType = new HashMap<>();
		for (Constraint c : jt.getConstraints()) {
			AnnotatedValue left = c.getLeft(), right = c.getRight();
			String className = left.getEnclosingClass().getName();
			if (right.getKind() == Kind.METH_ADAPT) {
				String declId = ((AdaptValue) right).getDeclValue().getIdentifier();
				if (declId.equals(outkey1) || declId.equals(outkey2)) {
					List<Boolean> list = keyType.getOrDefault(className, new ArrayList<>());
					list.add(polyValues.contains(left.getIdentifier()));
					keyType.put(className, list);
				}
				else if (declId.equals(outvalue1) || declId.equals(outvalue2)) {
					List<Boolean> list = valueType.getOrDefault(className, new ArrayList<>());
					list.add(polyValues.contains(left.getIdentifier()));
					valueType.put(className, list);
				}
			}
		}
		setOutputKeyValueClass(mapperClasses, mapOutKeyExprs, keyType);
		setOutputKeyValueClass(mapperClasses, mapOutValueExprs, valueType);
		if (reducerClasses.isEmpty()) {
			setOutputKeyValueClass(mapperClasses, outKeyExprs, keyType);
			setOutputKeyValueClass(mapperClasses, outValueExprs, valueType);
		} else {
			setOutputKeyValueClass(reducerClasses, outKeyExprs, keyType);
			setOutputKeyValueClass(reducerClasses, outValueExprs, valueType);
		}
	}

	private void setOutputKeyValueClass(List<String> mapperClasses, List<InvokeExpr> mapOutKeyExprs,
			Map<String, List<Boolean>> keyType) {
		int index = 0;
		for (InvokeExpr invoke : mapOutKeyExprs) {
			String className = mapperClasses.get(index);
			String type = ((ClassConstant) invoke.getArg(0)).getValue().replace('/', '.');
			if (keyType.get(className).get(index) && mapreducePrimTypes.contains(type))
				invoke.setArg(0, ClassConstant.v("org/apache/hadoop/io/Text"));
			index++;
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
		if (type.toString().equals("java.util.HashSet")) return "java.util.HashMap";
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
