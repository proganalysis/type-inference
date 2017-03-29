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
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.javaToJimple.LocalGenerator;
import soot.jimple.AddExpr;
import soot.jimple.AssignStmt;
import soot.jimple.BinopExpr;
import soot.jimple.CastExpr;
import soot.jimple.ClassConstant;
import soot.jimple.DefinitionStmt;
import soot.jimple.IfStmt;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.NewExpr;
import soot.jimple.NumericConstant;
import soot.jimple.ParameterRef;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.VirtualInvokeExpr;
import soot.tagkit.SignatureTag;
import soot.util.Chain;

import static com.esotericsoftware.minlog.Log.*;

public class TransformerTransformer extends BodyTransformer {

	// record classes whose generic types have been modified
	private JCryptTransformer jt;
	private Set<String> polyValues;
	private Map<String, SootMethod> modifiedMethod = new HashMap<>();
	private static SootClass encryptionClass;
	private Set<String> mapreducePrimTypes, primWrappers;
	private Set<String> parseMethods;
	private Map<String, Byte> encryptions;
	private SootMethod sm;
	private List<Type> paramTypes;
	private Map<String, Job> jobs;
	private boolean[] keyvalues; // shoule input/output or key/value be modified
	private Set<String> valueMethods;
	
	public TransformerTransformer(JCryptTransformer jcryptTransformer, Set<String> polyValues, Map<String, Byte> map) {
		info(this.getClass().getSimpleName(), "Transforming ...");
		jt = jcryptTransformer;
		jobs = jt.getJobs();
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
		primWrappers = new HashSet<>();
		primWrappers.add("java.lang.Double");
		primWrappers.add("java.lang.Integer");
		primWrappers.add("java.lang.Long");
		primWrappers.add("java.lang.Short");
		primWrappers.add("java.lang.Float");
		valueMethods = new HashSet<>();
		valueMethods.add("doubleValue");
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected synchronized void internalTransform(Body body, String arg1, Map arg2) {
		sm = body.getMethod();
		paramTypes = new LinkedList<>();
		keyvalues = new boolean[4];
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
			// The order of following methods is important!
			modifyGenericSignatures();
			modifyMethodLocals(body);
			sm.setParameterTypes(paramTypes);
			modifiedMethod.put(sm.getDeclaringClass().toString() + sm.getName(), body.getMethod());
		} else if (methodName.equals("run") || methodName.equals("main"))
			modifyRunMethod(body);
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
			} else if (rightValue instanceof VirtualInvokeExpr) {
				modifySetIterator((VirtualInvokeExpr) rightValue, (AssignStmt) unit);
				modifyAppendMethod((VirtualInvokeExpr) rightValue);
			}
		} else if (rightValue instanceof ParameterRef) {
			if (leftType != null) {
				unit.getRightOpBox().setValue(new ParameterRef(RefType.v(leftType),
						((ParameterRef) rightValue).getIndex()));
				paramTypes.add(RefType.v(leftType));
			} else paramTypes.add(rightValue.getType());
		}
	}
	
	private void modifyAppendMethod(VirtualInvokeExpr rightValue) {
		// $r20 = virtualinvoke $r19.<java.lang.StringBuilder: java.lang.StringBuilder append(double)>(d1);
		// -> $r20 = virtualinvoke $r19.<java.lang.StringBuilder: java.lang.StringBuilder append(java.lang.String)>(d1);
		SootMethod invokeMethod = rightValue.getMethod();
		if (invokeMethod.getName().equals("append")) {
			String argType = modifyTo(rightValue.getArg(0));
			if (argType != null) {
				SootMethod toCall = Scene.v().getMethod("<java.lang.StringBuilder: java.lang.StringBuilder append(java.lang.String)>");
				rightValue.setMethodRef(toCall.makeRef());
			}
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
		if ((0b100 & typeSet) != 0) libMethodName = "getAH";
		else if ((0b1 & typeSet) != 0) libMethodName = "getOPE";
		encryptionClass = Scene.v().getSootClass("encryptUtil.EncryptUtil");
		SootMethod libMethod = encryptionClass
				.getMethod("java.lang.String " + libMethodName + "(" + rightValue.getType() + ")");
		return Jimple.v().newStaticInvokeExpr(libMethod.makeRef(), rightValue);
	}

	private Value getInvokeExpr(InvokeExpr expr) {
		if (expr instanceof StaticInvokeExpr) {
			// $i3 = staticinvoke <java.lang.Integer: int parseInt(java.lang.String)>($r8);
			// -> $i3 = $r8;
			// $r21 = staticinvoke <java.lang.Double: java.lang.Double valueOf(java.lang.String)>($r20);
			// -> $r21 = $r20;
			String methodName = expr.getMethod().getName();
			if (parseMethods.contains(methodName) || methodName.equals("valueOf")) {
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
				expr.setMethodRef(modifiedMethod.get(sm.getDeclaringClass().toString() + sm.getName()).makeRef());
				//System.out.println("Volatile: " + expr);
			} else {
				// $i0 = virtualinvoke r5.<org.apache.hadoop.io.IntWritable: int get()>();
				// -> $i0 = virtualinvoke r5.<org.apache.hadoop.io.Text: String toString()>();
				SootMethod invokeMethod = expr.getMethod();
				Value base = ((VirtualInvokeExpr) expr).getBase();
				String baseId = TransUtils.getIdenfication(base, sm);
				if (mapreducePrimTypes.contains(invokeMethod.getDeclaringClass().getName())
						&& invokeMethod.getName().equals("get")) {
					if (polyValues.contains(baseId)) {
						SootMethod toCall = Scene.v()
							.getMethod("<org.apache.hadoop.io.Text: java.lang.String toString()>");
						expr.setMethodRef(toCall.makeRef());
						return expr;
					}
				} else if (valueMethods.contains(invokeMethod.getName())) {
					// d0 = virtualinvoke $r15.<java.lang.Double: double doubleValue()>();
					// -> d0 = $r15;
					if (polyValues.contains(baseId)) return base;
				} else modifyAppendMethod((VirtualInvokeExpr) expr);
			}
		} else if (expr.getMethod().getName().equals("write")
				|| expr.getMethod().getName().equals("collect")) {
			keyvalues[2] = polyValues.contains(TransUtils.getIdenfication(expr.getArg(0), sm));
			keyvalues[3] = polyValues.contains(TransUtils.getIdenfication(expr.getArg(1), sm));
		}
		return null;
	}

	private void modifyRunMethod(Body body) {
		for (Unit unit : body.getUnits()) {
			if (unit instanceof InvokeStmt) {
				InvokeExpr invoke = ((InvokeStmt) unit).getInvokeExpr();
				if (invoke instanceof VirtualInvokeExpr) {
					String methodName = invoke.getMethod().getName();
					String jobId = ((VirtualInvokeExpr) invoke).getBase().toString();
					Job job = jobs.get(jobId);
					if (methodName.equals("setOutputKeyClass")) {
						List<AnnotatedValue> reduceOutKey = job.getRok();
						if (!reduceOutKey.isEmpty())
							setOutputKeyValueClass(reduceOutKey, invoke);
						else setOutputKeyValueClass(job.getMok(), invoke);
					} else if (methodName.equals("setOutputValueClass")) {
						List<AnnotatedValue> reduceOutValue = job.getRov();
						if (!reduceOutValue.isEmpty())
							setOutputKeyValueClass(reduceOutValue, invoke);
						else setOutputKeyValueClass(job.getMov(), invoke);
					} else if (methodName.equals("setMapOutputKeyClass"))
						setOutputKeyValueClass(job.getMok(), invoke);
					else if (methodName.equals("setMapOutputValueClass"))
						setOutputKeyValueClass(job.getMov(), invoke);
				}
			}
		}
	}

	private void setOutputKeyValueClass(List<AnnotatedValue> output, InvokeExpr invoke) {
		for (AnnotatedValue av : output)
			if (polyValues.contains(av.getIdentifier())) {
				String type = ((ClassConstant) invoke.getArg(0)).getValue().replace('/', '.');
				if (mapreducePrimTypes.contains(type))
					invoke.setArg(0, ClassConstant.v("org/apache/hadoop/io/Text"));
				break;
			}
	}

	private void modifyGenericSignatures() {
		for (int i = 0; i < 2; i++) {
			Value value = sm.getActiveBody().getParameterLocal(i);
			keyvalues[i] = polyValues.contains(TransUtils.getIdenfication(value, sm));
		}
		modifyClassGenerics();
		modifyMethodGenerics();
	}

	private void modifyMethodGenerics() {
		SignatureTag sigTag = (SignatureTag) sm.getTag("SignatureTag");
		if (sigTag == null) return;
		sm.removeTag("SignatureTag");
		String signature = sigTag.getSignature().replace('.', ';');
		signature = signature.substring(1, signature.lastIndexOf(';'));
		List<String> genericParts = new ArrayList<>();
		int start = 0;
		while (signature.contains(">")) {
			start = signature.indexOf('<', start) + 1;
			int end = signature.indexOf('>');
			genericParts.add(signature.substring(start, end));
			signature = signature.substring(0, start) + signature.substring(end + 1);
		}
		String[] genericTypes = signature.split(";");
		String textType = "Lorg/apache/hadoop/io/Text";
		if (keyvalues[0]) genericTypes[0] = textType;
		if (genericParts.size() == 1) {
			if (keyvalues[1]) genericTypes[1] = textType;
		} else genericTypes[1] += (keyvalues[1] ? textType + ";" : genericParts.get(0)) + ">";
		String[] keyValueGeneric = genericParts.get(genericParts.size() - 1).split(";");
		int size = keyValueGeneric.length;
		if (size == 2) {
			if (keyvalues[2]) keyValueGeneric[0] = textType;
			if (keyvalues[3]) keyValueGeneric[1] = textType;
		} else
			for (int i = 0; i < 4; i++)
				if (keyvalues[i]) keyValueGeneric[i] = textType;
		for (String keyValue : keyValueGeneric)
			genericTypes[2] += keyValue + ";";
		genericTypes[2] += ">";
		String newSig = "(";
		for (String sig : genericTypes) newSig += sig + ";";
		sm.addTag(new SignatureTag(newSig.replace(";Context", ".Context") + ")V"));
	}

	private void modifyClassGenerics() {
		SootClass sc = sm.getDeclaringClass();
		SignatureTag sigTag = (SignatureTag) sc.getTag("SignatureTag");
		if (sigTag == null) return;
		sc.removeTag("SignatureTag");
		String signature = sigTag.getSignature();
		String methodName = sm.getName().equals("map") ? "Mapper<" : "Reducer<";
		int start = signature.indexOf(methodName) + methodName.length();
		int end = signature.indexOf('>', start);
		String[] genericTypes = signature.substring(start, end).split(";");
		for (int i = 0; i < 4; i++)
			if (keyvalues[i]) genericTypes[i] = "Lorg/apache/hadoop/io/Text";
		String newSig = signature.substring(0, start);
		for (String s : genericTypes) newSig += s + ";";
		sc.addTag(new SignatureTag(newSig + signature.substring(end)));
	}

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
			case " <= ": libMethodName = "isGt"; break;
			case " >= ": libMethodName = "isLt";
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
		if (!polyValues.contains(TransUtils.getIdenfication(value, sm)))
			return null;
		Type type = value.getType();
		if (mapreducePrimTypes.contains(type.toString())) return "org.apache.hadoop.io.Text";
		if (isPrimitive(type) || primWrappers.contains(type.toString())) return "java.lang.String";
		if (type.toString().equals("java.util.HashSet")) return "java.util.HashMap";
		return null;
	}

}
