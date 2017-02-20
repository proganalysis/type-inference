package edu.rpi;

import java.util.*;

import java.lang.annotation.*;
import java.io.PrintStream;
import soot.*;
import soot.jimple.InvokeExpr;
import soot.jimple.*;
import soot.tagkit.*;

import edu.rpi.Constraint.SubtypeConstraint;
import edu.rpi.Constraint.EqualityConstraint;
import edu.rpi.AnnotatedValue.AdaptValue;
import edu.rpi.AnnotatedValue.Kind;
import edu.rpi.ConstraintSolver.FailureStatus;

public abstract class InferenceTransformer extends BodyTransformer {

	private VisitorState visitorState = new VisitorState();

	private Set<Constraint> constraints = new LinkedHashSet<Constraint>();

	private Comparator<SootClass> comparator = new Comparator<SootClass>() {
		public int compare(SootClass o1, SootClass o2) {
			return o1.toString().compareTo(o2.toString());
		}
	};

	private Comparator<Annotation> annoComparator;

	private Set<SootClass> visitedClasses = new TreeSet<SootClass>(comparator);

	private boolean needLocals = true;

	private static Map<SootMethod, Map<String, AnnotatedValue>> locals = new HashMap<SootMethod, Map<String, AnnotatedValue>>();

	private static Map<String, AnnotatedValue> adaptValues = new HashMap<String, AnnotatedValue>();

	// record map and reduce output key-value pairs for generic type modification
	public AnnotatedValue reduceKey, reduceValue;
	public List<AnnotatedValue> mapOutKeys = new ArrayList<>();
	public List<AnnotatedValue> mapOutValues = new ArrayList<>();

	/**
	 * This is actually static, because AnnotatedValueMap.v() always return the
	 * same object.
	 */
	private AnnotatedValueMap annotatedValues = AnnotatedValueMap.v();

	private ViewpointAdapter vpa = getViewpointAdapter();

	public final static String CALLSITE_PREFIX = "callsite-";

	public final static String FAKE_PREFIX = "fake-";

	public final static String LIB_PREFIX = "lib-";

	public VisitorState getVisitorState() {
		return visitorState;
	}

	protected abstract AnnotatedValue createFieldAdaptValue(AnnotatedValue context, AnnotatedValue decl,
			AnnotatedValue assignTo);

	protected abstract AnnotatedValue createMethodAdaptValue(AnnotatedValue receiver, AnnotatedValue decl,
			AnnotatedValue assignTo);

	protected abstract InferenceVisitor getInferenceVisitor(InferenceTransformer t);

	protected abstract boolean isAnnotated(AnnotatedValue v);

	public abstract ViewpointAdapter getViewpointAdapter();

	public abstract Set<Annotation> getSourceLevelQualifiers();

	public abstract int getAnnotationWeight(Annotation anno);

	public abstract boolean isStrictSubtyping();

	public abstract FailureStatus getFailureStatus(Constraint c);

	public abstract String getName();

	// Author: Lindsey
	// Threadfix variables

	private HashMap<Integer, String> threadFixClassConnector = null;

	private HashMap<String, InvokeExpr> threadFixRunnables = null;

	private String THREAD_CLASS = "java.lang.Thread";

	private String RUNNABLE_CLASS = "java.lang.Runnable";

	private String OBJ_CLASS = "java.lang.Object";

	private String EXEC_SERVICE_CLASS = "java.util.concurrent.ExecutorService";

	private String EXEC_CLASS = "java.util.concurrent.Executors";

	private String START_METHOD = "start";

	private String RUN_METHOD = "run";

	private String EXECUTE_METHOD = "execute";
	// end Lindsey

	protected AnnotatedValue getFieldAdaptValue(AnnotatedValue context, AnnotatedValue decl, AnnotatedValue assignTo) {
		AnnotatedValue av = createFieldAdaptValue(context, decl, assignTo);
		String identifier = av.getIdentifier();
		AnnotatedValue ret = adaptValues.get(identifier);
		if (ret == null) {
			ret = av;
			ret.setEnclosingClass(visitorState.getSootClass());
			ret.setEnclosingMethod(visitorState.getSootMethod());
			adaptValues.put(identifier, ret);
		}
		return ret;
	}

	protected AnnotatedValue getMethodAdaptValue(AnnotatedValue receiver, AnnotatedValue decl,
			AnnotatedValue assignTo) {
		AnnotatedValue av = createMethodAdaptValue(receiver, decl, assignTo);
		String identifier = av.getIdentifier();
		AnnotatedValue ret = adaptValues.get(identifier);
		if (ret == null) {
			ret = av;
			ret.setEnclosingClass(visitorState.getSootClass());
			ret.setEnclosingMethod(visitorState.getSootMethod());
			adaptValues.put(identifier, ret);
		}
		return ret;
	}

	protected AnnotatedValue getAnnotatedValue(String identifier, Type type, Kind kind, Object v,
			Set<Annotation> annos) {
		AnnotatedValue ret;
		if (kind == Kind.LOCAL) {
			SootMethod sm = visitorState.getSootMethod();
			Map<String, AnnotatedValue> localMap = locals.get(sm);
			if (localMap == null) {
				localMap = new LinkedHashMap<String, AnnotatedValue>();
				locals.put(sm, localMap);
			}
			ret = localMap.get(identifier);
			if (ret == null) {
				ret = annotatedValues.get(identifier);
				if (ret == null) {
					ret = new AnnotatedValue(identifier, type, kind, v, annos);
					ret.setEnclosingClass(visitorState.getSootClass());
					ret.setEnclosingMethod(visitorState.getSootMethod());
					if (kind != Kind.LITERAL)
						annotatedValues.put(identifier, ret);
				}
				if (v != null)
					localMap.put(identifier, ret);
			}
		} else {
			ret = annotatedValues.get(identifier);
			if (ret == null) {
				ret = new AnnotatedValue(identifier, type, kind, v, annos);
				ret.setEnclosingClass(visitorState.getSootClass());
				ret.setEnclosingMethod(visitorState.getSootMethod());
				if (kind != Kind.LITERAL)
					annotatedValues.put(identifier, ret);
			}
		}
		if (!isAnnotated(ret)) {
			if (kind == Kind.COMPONENT)
				annotateArrayComponent(ret, v);
			else
				annotateDefault(ret, kind, v);
		}
		return ret;
	}

	protected AnnotatedValue getAnnotatedValue(String identifier, Type type, Kind kind, Object v) {
		return getAnnotatedValue(identifier, type, kind, v, AnnotationUtils.createAnnotationSet());
	}

	protected AnnotatedValue getAnnotatedClass(SootClass sc) {
		String identifier = sc.getName() + "@CLASS";
		return getAnnotatedValue(identifier, sc.getType(), Kind.CLASS, sc);
	}

	protected AnnotatedValue getAnnotatedValue(Local local) {
		SootMethod sm = visitorState.getSootMethod();
		String identifier = sm.getSignature() + "@" + local.toString();
		AnnotatedValue ret = getAnnotatedValue(identifier, local.getType(), Kind.LOCAL, local);
		return ret;
	}

	protected AnnotatedValue getAnnotatedValue(Constant c) {
		SootClass sc = visitorState.getSootClass();
		String identifier = sc.getName() + "@" + c.toString();
		AnnotatedValue ret = getAnnotatedValue(identifier, c.getType(), Kind.LITERAL, c);
		return ret;
	}

	public AnnotatedValue getAnnotatedValue(Value v) {
		if (v instanceof Local)
			return getAnnotatedValue((Local) v);
		else if (v instanceof Constant)
			return getAnnotatedValue((Constant) v);
		else
			throw new RuntimeException("Not implemented for " + v.getClass());
	}

	public AnnotatedValue getAnnotatedField(SootField field) {
		field = getDeclaringField(field);
		String identifier = field.getSignature();
		AnnotatedValue ret = annotatedValues.get(identifier);
		if (ret == null) {
			ret = new AnnotatedValue(identifier, field.getType(), Kind.FIELD, field);
			ret.setEnclosingClass(field.getDeclaringClass());
			ret.setEnclosingMethod(null);
			annotatedValues.put(identifier, ret);
		}
		if (!isAnnotated(ret)) {
			ret.setAnnotations(getVisibilityTags(field, Kind.FIELD), this);
			annotateField(ret, field);
		}
		return ret;
	}

	protected AnnotatedValue getAnnotatedParameter(SootMethod sm, int index) {
		if (index < 0 || index >= sm.getParameterCount())
			return null;

		sm = getDeclaringMethod(sm);

		String identifier = (isLibraryMethod(sm) ? LIB_PREFIX : "") + sm.getSignature() + "@parameter" + index;
		AnnotatedValue ret = annotatedValues.get(identifier);
		if (ret == null) {
			ret = new AnnotatedValue(identifier, sm.getParameterType(index), Kind.PARAMETER, sm);
			ret.setEnclosingClass(sm.getDeclaringClass());
			ret.setEnclosingMethod(sm);
			annotatedValues.put(identifier, ret);
		}
		if (!isAnnotated(ret)) {
			ret.setAnnotations(getVisibilitParameterTags(sm, index), this);
			annotateParameter(ret, sm, index);
		}
		return ret;
	}

	protected AnnotatedValue getAnnotatedReturn(SootMethod sm) {
		sm = getDeclaringMethod(sm);
		String identifier = (isLibraryMethod(sm) ? LIB_PREFIX : "") + sm.getSignature() + "@return";
		AnnotatedValue ret = annotatedValues.get(identifier);
		if (ret == null) {
			ret = new AnnotatedValue(identifier, sm.getReturnType(), Kind.RETURN, sm);
			ret.setEnclosingClass(sm.getDeclaringClass());
			ret.setEnclosingMethod(sm);
			annotatedValues.put(identifier, ret);
		}
		if (!isAnnotated(ret)) {
			ret.setAnnotations(getVisibilityTags(sm, Kind.RETURN), this);
			annotateReturn(ret, sm);
		}
		return ret;
	}

	protected AnnotatedValue getAnnotatedThis(SootMethod sm) {
		sm = getDeclaringMethod(sm);
		String identifier = (isLibraryMethod(sm) ? LIB_PREFIX : "") + sm.getSignature() + "@this";
		AnnotatedValue ret = annotatedValues.get(identifier);
		if (ret == null) {
			ret = new AnnotatedValue(identifier, sm.getDeclaringClass().getType(), Kind.THIS, sm);
			// TODO: this can also be annotated
			ret.setEnclosingClass(sm.getDeclaringClass());
			ret.setEnclosingMethod(sm);
			annotatedValues.put(identifier, ret);
		} // else ret.setKind(Kind.THIS);
		if (!isAnnotated(ret)) {
			ret.setAnnotations(getVisibilityTags(sm, Kind.THIS), this);
			annotateThis(ret, sm);
		}
		return ret;
	}

	protected void addSubtypeConstraint(AnnotatedValue sub, AnnotatedValue sup) {
		if (sub.getKind() == Kind.LITERAL || sup.getKind() == Kind.LITERAL)
			return;
		if (sub == sup)
			return;
		Constraint c = new SubtypeConstraint(sub, sup);
		if (!constraints.add(c))
			return;
		addComponentConstraints(sub, sup);
	}

	protected void addEqualityConstraint(AnnotatedValue sub, AnnotatedValue sup) {
		if (sub.getKind() == Kind.LITERAL || sup.getKind() == Kind.LITERAL)
			return;
		Constraint c = new EqualityConstraint(sub, sup);
		if (!constraints.add(c))
			return;
		addComponentConstraints(sub, sup);
	}

	private void addComponentConstraints(AnnotatedValue sub, AnnotatedValue sup) {
		if (sub.getType() instanceof ArrayType && sup instanceof AdaptValue) {
			sup = ((AdaptValue) sup).getDeclValue();
		} else if (sub instanceof AdaptValue && sup.getType() instanceof ArrayType)
			sub = ((AdaptValue) sub).getDeclValue();

		if (sub.getType() instanceof ArrayType && sup.getType() instanceof ArrayType) {
			AnnotatedValue subComponent = getAnnotatedValue(sub.getIdentifier() + "[]",
					((ArrayType) sub.getType()).getElementType(), Kind.COMPONENT, null);
			AnnotatedValue supComponent = getAnnotatedValue(sup.getIdentifier() + "[]",
					((ArrayType) sup.getType()).getElementType(), Kind.COMPONENT, null);
			addEqualityConstraint(subComponent, supComponent);
		}
	}

	protected void processMethod(SootMethod sm) {
		// Add override constraints
		if (sm.getName().equals("<init>") || sm.getName().equals("<clinit>"))
			return;
		Map<SootClass, SootMethod> overriddenMethods = InferenceUtils.overriddenMethods(sm);
		for (SootMethod overridden : overriddenMethods.values()) {
			handleMethodOverride(sm, overridden);
		}
	}

	protected SootField getDeclaringField(SootField field) {
		if (!field.isPhantom())
			return field;
		SootClass sc = field.getDeclaringClass();
		Set<SootClass> superTypes = InferenceUtils.getSuperTypes(sc);
		for (SootClass superClass : superTypes) {
			if (superClass.declaresField(field.getSubSignature())) {
				SootField f = superClass.getField(field.getSubSignature());
				if (!f.isPhantom())
					return f;
			}
		}
		return field;
	}

	protected SootMethod getDeclaringMethod(SootMethod method) {
		if (!method.isPhantom())
			return method;
		SootClass sc = method.getDeclaringClass();
		Set<SootClass> superTypes = InferenceUtils.getSuperTypes(sc);
		for (SootClass superClass : superTypes) {
			if (superClass.declaresMethod(method.getSubSignature())) {
				SootMethod m = superClass.getMethod(method.getSubSignature());
				if (!m.isPhantom())
					return m;
			}
		}
		return method;
	}

	protected void annotateDefault(AnnotatedValue v, Kind kind, Object o) {
	}

	protected void annotateArrayComponent(AnnotatedValue v, Object o) {
	}

	protected void annotateField(AnnotatedValue v, SootField field) {
	}

	protected void annotateThis(AnnotatedValue v, SootMethod method) {
	}

	protected void annotateParameter(AnnotatedValue v, SootMethod method, int index) {
	}

	protected void annotateReturn(AnnotatedValue v, SootMethod method) {
	}

	protected Set<Annotation> getVisibilitParameterTags(Host host, int index) {
		Set<Annotation> annos = AnnotationUtils.createAnnotationSet();
		VisibilityParameterAnnotationTag ptag = (VisibilityParameterAnnotationTag) host
				.getTag("VisibilityParameterAnnotationTag");
		VisibilityAnnotationTag vtag = null;
		if (ptag != null && index < ptag.getVisibilityAnnotations().size()
				&& (vtag = ptag.getVisibilityAnnotations().get(index)) != null && vtag.hasAnnotations()) {
			for (AnnotationTag at : vtag.getAnnotations()) {
				Annotation anno = AnnotationUtils.fromAnnotationTag(at);
				if (anno != null)
					annos.add(anno);
			}
		}
		Set<Annotation> srcAnnos = getSourceLevelQualifiers();
		annos.retainAll(srcAnnos);
		return annos;
	}

	protected Set<Annotation> getVisibilityTags(Host host, Kind kind) {
		Set<Annotation> annos = getRawVisibilityTags(host);
		Set<Annotation> set = AnnotationUtils.createAnnotationSet();
		for (Iterator<Annotation> it = annos.iterator(); it.hasNext();) {
			Annotation anno = it.next();
			if (anno.toString().endsWith("This")) {
				it.remove();
				if (kind == Kind.THIS) {
					String name = anno.annotationType().getCanonicalName();
					set.add(AnnotationUtils.fromName(name.substring(0, name.length() - 4)));
				}
			} else if (kind == Kind.THIS)
				it.remove();
		}
		annos.addAll(set);
		Set<Annotation> srcAnnos = getSourceLevelQualifiers();
		annos.retainAll(srcAnnos);
		return annos;
	}

	/**
	 * Get all original annotations without filtering
	 */
	protected Set<Annotation> getRawVisibilityTags(Host host) {
		Set<Annotation> annos = AnnotationUtils.createAnnotationSet();
		VisibilityAnnotationTag vtag = (VisibilityAnnotationTag) host.getTag("VisibilityAnnotationTag");
		if (vtag != null && vtag.hasAnnotations()) {
			for (AnnotationTag at : vtag.getAnnotations()) {
				Annotation anno = AnnotationUtils.fromAnnotationTag(at);
				if (anno != null)
					annos.add(anno);
			}
		}
		return annos;
	}

	public Set<Constraint> getConstraints() {
		return constraints;
	}

	public Map<String, AnnotatedValue> getAnnotatedValues() {
		return annotatedValues;
	}

	public void clear() {
		locals.clear();
		constraints.clear();
		visitedClasses.clear();
	}

	public boolean isLibraryMethod(SootMethod sm) {
		sm = getDeclaringMethod(sm);
		SootClass sc = sm.getDeclaringClass();
		return sc.isLibraryClass();
	}

	public boolean isFromLibrary(AnnotatedValue av) {
		Object o = av.getValue();
		if (o != null && o instanceof SootMethod && isLibraryMethod((SootMethod) o))
			return true;
		return false;
	}

	public void printJaif(PrintStream out) {
		for (SootClass sc : visitedClasses) {
			printJaifClass(sc, "", out);
		}
	}

	protected void printJaifClass(SootClass sc, String indent, PrintStream out) {
		out.println(indent + "package " + sc.getPackageName() + ":");
		out.println();
		out.println(indent + "class " + sc.getShortName() + ":");
		int numOfLocations = 0;
		for (SootField sf : sc.getFields()) {
			printJaifField(sf, indent + "\t", out);
			out.println();
		}
		for (SootMethod sm : sc.getMethods()) {
			numOfLocations += printJaifMethod(sm, indent + "\t", out);
			out.println();
		}
		out.println("Number of Locations: " + numOfLocations);
		out.println();
	}

	protected int printJaifMethod(SootMethod sm, String indent, PrintStream out) {
		int numOfLocations = 0;
		out.println(indent + "method " + sm.getSubSignature() + ":");
		if (sm.getReturnType() != VoidType.v()) {
			printAnnotatedValue(getAnnotatedReturn(sm), "return", indent + "\t", out);
			numOfLocations++;
		}
		if (!sm.isStatic()) {
			printAnnotatedValue(getAnnotatedThis(sm), "receiver", indent + "\t", out);
			numOfLocations++;
		}
		indent += "\t";
		for (int i = 0; i < sm.getParameterCount(); i++) {
			out.println(indent + "parameter #" + i + ":");
			printAnnotatedValue(getAnnotatedParameter(sm, i), "type", indent + "\t", out);
		}
		// locals
		if (needLocals) {
			SootMethod prev = visitorState.getSootMethod();
			visitorState.setSootMethod(sm);
			try {
				Map<String, AnnotatedValue> map = locals.get(sm);
				if (map == null)
					return numOfLocations;
				for (AnnotatedValue l : map.values()) {
					if (l.getName().equals("r1") || l.getName().equals("r2")) {
					out.println(indent + "local " + l.getName() + ":");
					printAnnotatedValue(l, "type", indent + "\t", out);
					String id = l.getIdentifier();
					if (!id.startsWith(CALLSITE_PREFIX) && !id.startsWith(FAKE_PREFIX) && !id.startsWith(LIB_PREFIX))
						numOfLocations++;
					}
				}
			} finally {
				visitorState.setSootMethod(prev);
			}
		}
		return numOfLocations;
	}

	protected void printJaifField(SootField sf, String indent, PrintStream out) {
		out.println(indent + "field " + sf.getName() + ":");
		printAnnotatedValue(getAnnotatedField(sf), "type", indent + "\t", out);
	}

	protected void printAnnotatedValue(AnnotatedValue av, String typeStr, String indent, PrintStream out) {
		out.println(indent + typeStr + ": " + av.getAnnotations(this) + " (" + av.getId() + ")");
		if (av.getType() instanceof ArrayType) {
			AnnotatedValue component = getAnnotatedValue(av.getIdentifier() + "[]",
					((ArrayType) av.getType()).getElementType(), Kind.COMPONENT, null);
			printAnnotatedValue(component, "inner-type", indent + "\t", out);
		}
	}

	public Annotation adaptField(Annotation contextAnno, Annotation declAnno) {
		// ViewpointAdapter vp = getViewpointAdapter();
		return vpa.adaptField(contextAnno, declAnno);
	}

	public Annotation adaptMethod(Annotation contextAnno, Annotation declAnno) {
		// ViewpointAdapter vp = getViewpointAdapter();
		return vpa.adaptMethod(contextAnno, declAnno);
	}

	/**
	 * Adapt the declared type of a field from the point of view the receiver
	 * 
	 * @param contextSet
	 *            The set of annotations of the receiver type
	 * @param declSet
	 *            The set of annotations of the declared type
	 * @return
	 */
	public Set<Annotation> adaptFieldSet(Set<Annotation> contextSet, Set<Annotation> declSet) {
		ViewpointAdapter vp = getViewpointAdapter();
		Set<Annotation> outSet = AnnotationUtils.createAnnotationSet();
		for (Annotation declAnno : declSet) {
			for (Annotation rcvAnno : contextSet) {
				Annotation anno = vp.adaptField(rcvAnno, declAnno);
				if (anno != null)
					outSet.add(anno);
			}
		}
		return outSet;
	}

	public Set<Annotation> adaptMethodSet(Set<Annotation> contextSet, Set<Annotation> declSet) {
		ViewpointAdapter vp = getViewpointAdapter();
		Set<Annotation> outSet = AnnotationUtils.createAnnotationSet();
		for (Annotation declAnno : declSet) {
			for (Annotation rcvAnno : contextSet) {
				Annotation anno = vp.adaptMethod(rcvAnno, declAnno);
				if (anno != null)
					outSet.add(anno);
			}
		}
		return outSet;
	}

	protected void handleMethodOverride(SootMethod overrider, SootMethod overridden) {
		// only handle overridden methods with active body?
		if (!overrider.isStatic()) {
			// this: overridden <: overrider
			AnnotatedValue overriderThis = getAnnotatedThis(overrider);
			AnnotatedValue overriddenThis = getAnnotatedThis(overridden);
			addSubtypeConstraint(overriddenThis, overriderThis);
		}
		// parameter: overridden <: overrider
		assert overrider.getParameterCount() == overridden.getParameterCount();
		for (int i = 0; i < overrider.getParameterCount(); i++) {
			AnnotatedValue overriderParam = getAnnotatedParameter(overrider, i);
			AnnotatedValue overriddenParam = getAnnotatedParameter(overridden, i);
			addSubtypeConstraint(overriddenParam, overriderParam);
		}
		if (overrider.getReturnType() != VoidType.v()) {
			// return: overrider <: overridden
			AnnotatedValue overriderRet = getAnnotatedReturn(overrider);
			AnnotatedValue overriddenRet = getAnnotatedReturn(overridden);
			addSubtypeConstraint(overriderRet, overriddenRet);
		}
	}

	protected void handleInstanceFieldRead(AnnotatedValue aBase, AnnotatedValue aField, AnnotatedValue aLhs) {
		AnnotatedValue afv = getFieldAdaptValue(aBase, aField, aLhs);
		addSubtypeConstraint(afv, aLhs);
	}

	protected void handleInstanceFieldWrite(AnnotatedValue aBase, AnnotatedValue aField, AnnotatedValue aRhs) {
		// if (aField.getKind() == Kind.FIELD
		// && ((SootField) aField.getValue()).getName().equals(aRhs.getName()))
		// addSubtypeConstraint(aRhs, aField);
		// else {
		AnnotatedValue afv = getFieldAdaptValue(aBase, aField, null);
		addSubtypeConstraint(aRhs, afv);
		// }
	}

	protected void handleStaticFieldRead(AnnotatedValue aField, AnnotatedValue aLhs) {
		addSubtypeConstraint(aField, aLhs);
	}

	protected void handleStaticFieldWrite(AnnotatedValue aField, AnnotatedValue aRhs) {
		addSubtypeConstraint(aRhs, aField);
	}

	// Method returns true iff the type contains a $ I use this to
	// check for anon variables
	// private boolean isAnon(Type t) {
	// return t.toString().contains("$");
	// }
	protected void handleMethodCall(InvokeExpr v, AnnotatedValue assignTo) {
		SootMethod invokeMethod = v.getMethod();
		// Author: Lindsey
		// These are fixes for Javathread1, JavaThread2, and Executor1
		String methodName = invokeMethod.getName();
		String superClassName;
		String className;
		boolean runReplace = false;
		try {
			className = v.getMethodRef().declaringClass().getName();
			superClassName = v.getMethodRef().declaringClass().getSuperclass().getName();
		} catch (RuntimeException e) {
			// The class has no superclass
			superClassName = "";
			className = "";
		}
		if (invokeMethod.isConstructor() && invokeMethod.getDeclaringClass().implementsInterface(RUNNABLE_CLASS)
				&& !invokeMethod.getDeclaringClass().getName().equals(THREAD_CLASS)
				&& !invokeMethod.getDeclaringClass().getName().equals(EXEC_SERVICE_CLASS)
				&& !invokeMethod.getDeclaringClass().getName().equals(EXEC_CLASS)) {
			// System.out.println("THREADFIX: got runnable a constructor!");
			if (threadFixRunnables == null) {
				threadFixRunnables = new HashMap<>();
			}
			threadFixRunnables.put(invokeMethod.getDeclaringClass().getName(), v);
		}
		// for Executor1
		else if (methodName.equals(EXECUTE_METHOD) && className.equals(EXEC_SERVICE_CLASS)) {
			// System.out.println("THREADFIX: execute method found. ");
			// System.out.println("\t " +
			// invokeMethod.getDeclaringClass().getName());
			if (invokeMethod.getParameterCount() == 1) {
				String argType = v.getArgs().get(0).getType().toString();
				for (Map.Entry<String, InvokeExpr> entry : threadFixRunnables.entrySet()) {
					String key = entry.getKey();
					InvokeExpr value = entry.getValue();
					if (key.equals(argType)) {
						v = value;
						invokeMethod = value.getMethod().getDeclaringClass().getMethodByName(RUN_METHOD);
						runReplace = true;
					}
				}
			}
		} else if (superClassName.equals(THREAD_CLASS) || className.equals(THREAD_CLASS)) {
			if (methodName.equals(START_METHOD) && !className.equals(THREAD_CLASS)) {
				// basic thread start run replace (JavaThread1)
				invokeMethod = v.getMethodRef().declaringClass().getMethodByName(RUN_METHOD);
			}
			// more complex thread replace (JavaThread2)
			else if (className.equals(THREAD_CLASS) && superClassName.equals(OBJ_CLASS)
					&& methodName.equals(START_METHOD)) {
				// System.out.println("THREADFIX: at start method, hashcode = "
				// +
				// invokeMethod.getDeclaringClass().hashCode());
				for (Map.Entry<Integer, String> entry : threadFixClassConnector.entrySet()) {
					int key = entry.getKey();
					String value = entry.getValue();
					if (key == invokeMethod.getDeclaringClass().hashCode() && threadFixRunnables != null) {
						for (Map.Entry<String, InvokeExpr> innerEntry : threadFixRunnables.entrySet()) {
							String innerKey = innerEntry.getKey();
							InvokeExpr innerValue = innerEntry.getValue();
							if (value.equals(innerKey)) {
								v = innerValue;
								invokeMethod = innerValue.getMethod().getDeclaringClass().getMethodByName(RUN_METHOD);
								runReplace = true;
							}
						}
					}
				}
			}
		}
		// End Lindsey
		AnnotatedValue aBase = null;
		boolean isMapOutput = false, isReduceOutput = false;
		if (v instanceof InstanceInvokeExpr) {
			// receiver
			InstanceInvokeExpr iv = (InstanceInvokeExpr) v;
			Value base = iv.getBase();
			aBase = getAnnotatedValue(base);
			AnnotatedValue aThis = getAnnotatedThis(invokeMethod);
			addSubtypeConstraint(aBase, getMethodAdaptValue(aBase, aThis, assignTo));
			// check if it is the key-value pair of map output
			isMapOutput = visitorState.getSootMethod().getName().equals("map")
					&& isMapOutputMethod(className, methodName);
			isReduceOutput = visitorState.getSootMethod().getName().equals("reduce")
					&& isReduceOutputMethod(className, methodName);
		}
		// parameters
		if (!runReplace) {
			List<Value> args = v.getArgs();
			for (int i = 0; i < v.getArgCount(); i++) {
				Value arg = args.get(i);
				assert arg instanceof Local;
				AnnotatedValue aArg = getAnnotatedValue(arg);
				AnnotatedValue aParam = getAnnotatedParameter(invokeMethod, i);
				// Author: Lindsey, for JavaThread2
				SootClass decClass = invokeMethod.getDeclaringClass();
				if (decClass.getName().equals(THREAD_CLASS) && invokeMethod.isConstructor()) {
					// System.out.println("GOT CONSTRUCTOR FOR new Thread(new
					// Runnable)");
					// System.out.println("\t thread class = " +
					// decClass.hashCode());
					// System.out.println("\t runnable class = " +
					// arg.getType());
					if (threadFixClassConnector == null) {
						threadFixClassConnector = new HashMap<>();
					}
					// connect using hash code
					threadFixClassConnector.put(decClass.hashCode(), arg.getType().toString());
				}
				// End Lindsey
				addSubtypeConstraint(aArg, getMethodAdaptValue(aBase, aParam, assignTo));
				// record the output key-value pair of map
				if (isMapOutput)
					if (i == 0)
						mapOutKeys.add(aArg);
					else
						mapOutValues.add(aArg);
				if (isReduceOutput)
					if (reduceKey == null)
						reduceKey = aArg;
					else
						reduceValue = aArg;
			}
		}
		// return
		if (invokeMethod.getReturnType() != VoidType.v()) {
			if (assignTo == null)
				throw new RuntimeException("Null assignTo");
			AnnotatedValue aReturn = getAnnotatedReturn(invokeMethod);
			addSubtypeConstraint(getMethodAdaptValue(aBase, aReturn, assignTo), assignTo);
		}
	}

	private boolean isReduceOutputMethod(String decClass, String methodName) {
		if (decClass.equals("org.apache.hadoop.mapreduce.Reducer$Context") && methodName.equals("write"))
			return true;
		if (decClass.equals("org.apache.hadoop.mapred.OutputCollector") && methodName.equals("collect"))
			return true;
		return false;
	}

	public boolean isMapOutputMethod(String decClass, String methodName) {
		if (decClass.equals("org.apache.hadoop.mapreduce.Mapper$Context") && methodName.equals("write"))
			return true;
		if (decClass.equals("org.apache.hadoop.mapred.OutputCollector") && methodName.equals("collect"))
			return true;
		return false;
	}

	@Override
	protected void internalTransform(final Body b, String phaseName, @SuppressWarnings("rawtypes") Map options) {
		synchronized (this) {
			SootMethod sm = b.getMethod();
			SootClass sc = (sm == null ? null : sm.getDeclaringClass());
			visitorState.setSootMethod(sm);
			visitorState.setSootClass(sc);
			visitedClasses.add(sc);
			processMethod(sm);
			InferenceVisitor visitor = getInferenceVisitor(this);

			final PatchingChain<Unit> units = b.getUnits();
			for (Iterator<Unit> iter = units.snapshotIterator(); iter.hasNext();) {
				final Unit u = iter.next();
				visitorState.setUnit(u);
				u.apply(visitor);
			}
			visitorState.setSootMethod(null);
			visitorState.setSootClass(null);
			visitorState.setUnit(null);
			adaptValues.clear();
		}
	}

	/**
	 * Create the comparator
	 * 
	 * @return
	 */
	public Comparator<Annotation> getComparator() {
		if (annoComparator == null) {
			annoComparator = new Comparator<Annotation>() {
				@Override
				public int compare(Annotation o1, Annotation o2) {
					int ow1 = getAnnotationWeight(o1);
					int ow2 = getAnnotationWeight(o2);
					return ow1 - ow2;
				}
			};
		}
		return annoComparator;
	}

	public abstract void printPolyResult(PrintStream out);
}
