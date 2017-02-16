package edu.rpi.jcrypt;

import java.util.*;
import java.io.PrintStream;
import java.lang.annotation.*;
import soot.VoidType;
import soot.SootMethod;
import soot.Value;
import soot.ValueBox;
import soot.Body;
import soot.SootClass;
import soot.SootField;
import soot.jimple.ClassConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.VirtualInvokeExpr;
import soot.tagkit.*;
import edu.rpi.AnnotatedValue.FieldAdaptValue;
import edu.rpi.AnnotatedValue.MethodAdaptValue;
import edu.rpi.AnnotatedValue.Kind;
import edu.rpi.*;
import edu.rpi.ConstraintSolver.FailureStatus;

import checkers.inference.jcrypt.quals.*;
import checkers.inference.reim.quals.*;

public class JCryptTransformer extends InferenceTransformer {

	public static final String OPTION_POLY_LIBRARY = "polyLibrary";

	private Set<Annotation> sourceAnnos;

	private Set<Annotation> thisAnnos;

	private boolean isPolyLibrary;

	public final Annotation SENSITIVE;

	public final Annotation POLY;

	public final Annotation CLEAR;

	private final Annotation READONLYTHIS;

	private final Annotation POLYREADTHIS;

	private final Annotation SENSITIVETHIS;
	private final Annotation POLYTHIS;
	private final Annotation CLEARTHIS;

	private Set<String> clearLibMethods;
	
	public static Set<SootMethod> entryPoints = new HashSet<>();
	public static Set<String> mapperClasses = new HashSet<>();
	public static Set<String> reducerClasses = new HashSet<>();
	public static Set<String> combinerClasses = new HashSet<>();
	public static Set<String> partitionerClasses = new HashSet<>();

	public JCryptTransformer() {
		// isPolyLibrary = (System.getProperty(OPTION_POLY_LIBRARY) != null);
		isPolyLibrary = true;
		SENSITIVE = AnnotationUtils.fromClass(Sensitive.class);
		POLY = AnnotationUtils.fromClass(Poly.class);
		CLEAR = AnnotationUtils.fromClass(Clear.class);

		SENSITIVETHIS = AnnotationUtils.fromClass(SensitiveThis.class);
		POLYTHIS = AnnotationUtils.fromClass(PolyThis.class);
		CLEARTHIS = AnnotationUtils.fromClass(ClearThis.class);

		READONLYTHIS = AnnotationUtils.fromClass(ReadonlyThis.class);
		POLYREADTHIS = AnnotationUtils.fromClass(PolyreadThis.class);

		sourceAnnos = AnnotationUtils.createAnnotationSet();
		sourceAnnos.add(SENSITIVE);
		sourceAnnos.add(POLY);
		sourceAnnos.add(CLEAR);

		thisAnnos = AnnotationUtils.createAnnotationSet();
		thisAnnos.add(SENSITIVETHIS);
		thisAnnos.add(POLYTHIS);
		thisAnnos.add(CLEARTHIS);
		
		clearLibMethods = new HashSet<>();
		clearLibMethods.add("lastIndexOf");
		clearLibMethods.add("length");
		clearLibMethods.add("indexOf");
		clearLibMethods.add("size");
	}

	@Override
	protected void internalTransform(final Body b, String phaseName, @SuppressWarnings("rawtypes") Map options) {
		SootMethod sm = b.getMethod();
		String methodName = sm.getName();
		if (sm.getModifiers() != 4161
				&& (methodName.equals("map") || methodName.equals("reduce")))
			// 4161 means the modifier is volatile
			entryPoints.add(sm);
		else if (methodName.equals("run") || methodName.equals("main")) {
			// classify mapper, reducer, combiner and partitioner classes
			for (ValueBox vb: b.getUseBoxes()) {
				Value value = vb.getValue();
				if (value instanceof VirtualInvokeExpr) {
					String invokeName = ((VirtualInvokeExpr) value).getMethod().getName();
					Value arg0 = ((VirtualInvokeExpr) value).getArg(0);
					if (invokeName.equals("setMapperClass")) {
						mapperClasses.add(((ClassConstant) arg0).getValue());
					} else if (invokeName.equals("setReducerClass")) {
						reducerClasses.add(((ClassConstant) arg0).getValue());
					} else if (invokeName.equals("setCombinerClass")) {
						combinerClasses.add(((ClassConstant) arg0).getValue());
					} else if (invokeName.equals("setPartitionerClass")) {
						partitionerClasses.add(((ClassConstant) arg0).getValue());
					}
				}
			}
		}
		super.internalTransform(b, phaseName, options);
	}
	
	public boolean isPolyLibrary() {
		return isPolyLibrary;
	}

	private Set<Annotation> extractLibraryAnnos(AnnotatedValue av) {
		Set<Annotation> annos = null;
		if (av.getKind() == Kind.PARAMETER) {
			int index = Integer.parseInt(av.getName().substring("parameter".length()));
			annos = getVisibilitParameterTags(av.getEnclosingMethod(), index);
			annos.retainAll(sourceAnnos);
		} else if (av.getKind() == Kind.RETURN) {
			annos = getVisibilityTags(av.getEnclosingMethod(), Kind.RETURN);
			annos.retainAll(sourceAnnos);
		} else if (av.getKind() == Kind.THIS) {
			annos = getVisibilityTags(av.getEnclosingMethod(), Kind.THIS);
			annos.retainAll(sourceAnnos);
		} else if (av.getKind() == Kind.FIELD) {
			annos = getVisibilityTags((Host) av.getValue(), Kind.FIELD);
			annos.retainAll(sourceAnnos);
		}
		return annos;
	}

	private boolean isSource(AnnotatedValue av) {
		Set<Annotation> annos = extractLibraryAnnos(av);
		if (annos != null && !annos.isEmpty() && annos.contains(SENSITIVE)) {
			return true;
		}
		return false;
	}

	private boolean isSink(AnnotatedValue av) {
		Set<Annotation> annos = extractLibraryAnnos(av);
		if (annos != null && !annos.isEmpty() && annos.contains(CLEAR)) {
			return true;
		}
		return false;
	}

	@Override
	protected void handleMethodCall(InvokeExpr v, AnnotatedValue assignTo) {
		// Add default annotations/constraints for library methods
		SootMethod invokeMethod = v.getMethod();
		if (isPolyLibrary() && isLibraryMethod(invokeMethod) && !clearLibMethods.contains(invokeMethod.getName())) {
			// Add constraints PARAM -> RET for library methods if
			// there are no sources or sinks. Otherwise, it may
			// lead to unnecessary propagations...
			AnnotatedValue aOut = null;
			if (invokeMethod.isConstructor()) {
				aOut = getAnnotatedThis(invokeMethod);
			} else if (invokeMethod.getReturnType() != VoidType.v()) {
				aOut = getAnnotatedReturn(invokeMethod);
			}
			if (aOut != null) {
				for (int i = 0; i < invokeMethod.getParameterCount(); i++) {
					AnnotatedValue aIn = getAnnotatedParameter(invokeMethod, i);
					if (extractLibraryAnnos(aIn).isEmpty() && extractLibraryAnnos(aOut).isEmpty())
						super.addSubtypeConstraint(aIn, aOut);
				}
			}
			if (!invokeMethod.isStatic() && !invokeMethod.isConstructor()) {
				AnnotatedValue aThis = getAnnotatedThis(invokeMethod);
				if (aOut != null && extractLibraryAnnos(aOut).isEmpty() && extractLibraryAnnos(aThis).isEmpty()) {
					super.addSubtypeConstraint(aThis, aOut);
					super.addSubtypeConstraint(aOut, aThis);
				}
				// if THIS is not annotated as READONLY
				Set<Annotation> annos = getRawVisibilityTags(invokeMethod);
				if (!annos.contains(READONLYTHIS) && !annos.contains(POLYREADTHIS)) {
					for (int i = 0; i < invokeMethod.getParameterCount(); i++) {
						AnnotatedValue aIn = getAnnotatedParameter(invokeMethod, i);
						if (extractLibraryAnnos(aIn).isEmpty() && extractLibraryAnnos(aThis).isEmpty())
							super.addSubtypeConstraint(aIn, aThis);
					}
				}
			}
		}
		// Output sources/sinks
		if (isLibraryMethod(invokeMethod)) {
			List<AnnotatedValue> list = new ArrayList<AnnotatedValue>();
			for (int i = 0; i < invokeMethod.getParameterCount(); i++) {
				AnnotatedValue aIn = getAnnotatedParameter(invokeMethod, i);
				list.add(aIn);
			}
			if (invokeMethod.getReturnType() != VoidType.v())
				list.add(getAnnotatedReturn(invokeMethod));
			for (AnnotatedValue l : list) {
				if (isSource(l)) {
					System.out.println("INFO: found SOURCE " + l + " at " + "\n\t" + getVisitorState().getSootMethod()
							+ "\n\t" + getVisitorState().getUnit());
				} else if (isSink(l)) {
					System.out.println("INFO: found SINK " + l + " at " + "\n\t" + getVisitorState().getSootMethod()
							+ "\n\t" + getVisitorState().getUnit());
				}
			}
		}
		super.handleMethodCall(v, assignTo);
	}

	@Override
	protected boolean isAnnotated(AnnotatedValue v) {
		return isAnnotated(v.getRawAnnotations());
	}

	private boolean isAnnotated(Set<Annotation> annos) {
		Set<Annotation> set = AnnotationUtils.createAnnotationSet();
		set.addAll(annos);
		set.retainAll(sourceAnnos);
		return !set.isEmpty();
	}

	@Override
	protected AnnotatedValue createFieldAdaptValue(AnnotatedValue context, AnnotatedValue decl,
			AnnotatedValue assignTo) {
		// if (context.getName().equals("this"))
		// return decl;
		return new FieldAdaptValue(context, decl);
	}

	@Override
	protected AnnotatedValue createMethodAdaptValue(AnnotatedValue receiver, AnnotatedValue decl,
			AnnotatedValue assignTo) {
		String callSiteIdentifier = CALLSITE_PREFIX + getVisitorState().getSootMethod().getSignature() + "<"
				+ getVisitorState().getUnit().hashCode() + ">";
		AnnotatedValue callSite = getAnnotatedValue(callSiteIdentifier, VoidType.v(), Kind.LOCAL, callSiteIdentifier);
		return new MethodAdaptValue(callSite, decl);
	}

	@Override
	protected InferenceVisitor getInferenceVisitor(InferenceTransformer t) {
		return new InferenceVisitor(t);
	}

	@Override
	public Set<Annotation> getSourceLevelQualifiers() {
		return sourceAnnos;
	}

	@Override
	protected void annotateArrayComponent(AnnotatedValue v, Object o) {
		if (!isAnnotated(v)) {
			v.addAnnotation(CLEAR);
			v.addAnnotation(POLY);
		}
	}

	@Override
	protected void annotateField(AnnotatedValue v, SootField field) {
		if (!isAnnotated(v)) {
			if (field.getName().equals("this$0")) {
				v.setAnnotations(sourceAnnos, this);
			} else if (!field.isStatic()) {
				v.addAnnotation(CLEAR);
				v.addAnnotation(POLY);
			} else
				v.setAnnotations(sourceAnnos, this);
		}
	}

	@Override
	protected void annotateThis(AnnotatedValue v, SootMethod method) {
		if (!isAnnotated(v) && !method.isStatic()) {
			if (isPolyLibrary() && isLibraryMethod(method)) {
//				if (clearLibMethods.contains(method.getName()))
//					v.setAnnotations(sourceAnnos, this);
//				else
					v.addAnnotation(POLY);
			} else
				v.setAnnotations(sourceAnnos, this);
		}
	}

	@Override
	protected void annotateParameter(AnnotatedValue v, SootMethod method, int index) {
		if (!isAnnotated(v)) {
			if (isPolyLibrary() && isLibraryMethod(method)) {
				v.addAnnotation(POLY);
			} else
				v.setAnnotations(sourceAnnos, this);
		}
	}

	@Override
	protected void annotateReturn(AnnotatedValue v, SootMethod method) {
		if (!isAnnotated(v) && method.getReturnType() != VoidType.v()) {
			if (isPolyLibrary() && isLibraryMethod(method)) {
				if (clearLibMethods.contains(method.getName()))
					v.addAnnotation(CLEAR);
				else
					v.addAnnotation(POLY);
			} else
				v.setAnnotations(sourceAnnos, this);
		}
	}

	@Override
	protected void annotateDefault(AnnotatedValue v, Kind kind, Object o) {
		if (kind == Kind.LITERAL)
			v.addAnnotation(CLEAR);
		else
			v.setAnnotations(sourceAnnos, this);
	}

	@Override
	protected void handleStaticFieldRead(AnnotatedValue aField, AnnotatedValue aLhs) {
		super.handleStaticFieldRead(aField, aLhs);
		if (isSource(aField)) {
			System.out.println("INFO: found SOURCE " + aField + " at " + "\n\t" + getVisitorState().getSootMethod()
					+ "\n\t" + getVisitorState().getUnit());
		}
	}

	@Override
	protected void handleMethodOverride(SootMethod overrider, SootMethod overridden) {
		// only handle overridden methods with active body?
		if (!overrider.isStatic()) {
			// this: overridden <: overrider
			AnnotatedValue overriderThis = getAnnotatedThis(overrider);
			AnnotatedValue overriddenThis = getAnnotatedThis(overridden);
			if (!isFromLibrary(overriddenThis) || isAnnotated(getVisibilityTags(overridden, Kind.THIS))) {
				addSubtypeConstraint(overriddenThis, overriderThis);
			}
		}
		// parameter: overridden <: overrider
		assert overrider.getParameterCount() == overridden.getParameterCount();
		for (int i = 0; i < overrider.getParameterCount(); i++) {
			AnnotatedValue overriderParam = getAnnotatedParameter(overrider, i);
			AnnotatedValue overriddenParam = getAnnotatedParameter(overridden, i);
			if (!isFromLibrary(overriddenParam) || isAnnotated(getVisibilitParameterTags(overridden, i))) {
				addSubtypeConstraint(overriddenParam, overriderParam);
				if (isSource(overriddenParam)) {
					System.out.println("INFO: found SOURCE " + overriddenParam + " at " + "\n\t"
							+ getVisitorState().getSootMethod() + "\n\t" + getVisitorState().getUnit());
				}
			}
		}
		if (overrider.getReturnType() != VoidType.v()) {
			// return: overrider <: overridden
			AnnotatedValue overriderRet = getAnnotatedReturn(overrider);
			AnnotatedValue overriddenRet = getAnnotatedReturn(overridden);
			if (!isFromLibrary(overriddenRet) || isAnnotated(getVisibilityTags(overridden, Kind.RETURN)))
				addSubtypeConstraint(overriderRet, overriddenRet);
		}
	}

	@Override
	public int getAnnotationWeight(Annotation anno) {
		if (anno.toString().equals(SENSITIVE.toString()))
			return 3;
		else if (anno.toString().equals(POLY.toString()))
			return 2;
		else if (anno.toString().equals(CLEAR.toString()))
			return 1;
		else
			return Integer.MAX_VALUE;
	}

	@Override
	public FailureStatus getFailureStatus(Constraint c) {
		return FailureStatus.ERROR;
	}

	@Override
	public ViewpointAdapter getViewpointAdapter() {
		return new JCryptViewpointAdapter();
	}

	@Override
	public boolean isStrictSubtyping() {
		return true;
	}

	@Override
	public String getName() {
		return "jcrypt";
	}
	
	public Set<String> getPolyValues() {
		Set<String> polyValues = new HashSet<>();
		for (AnnotatedValue av : getAnnotatedValues().values()) {
			if (av.getIdentifier().startsWith("callsite"))
				continue;
			Kind kind = av.getKind();
			if (kind == Kind.FIELD || kind == Kind.LOCAL) {
				Annotation anno = av.getAnnotations(this).iterator().next();
				if (anno == CLEAR)
					continue;
				SootClass sc = av.getEnclosingClass();
				if (sc.isLibraryClass())
					continue;
				polyValues.add(av.getIdentifier());
			}
		}
		return polyValues;
	}

	@Override
	public void printPolyResult(PrintStream out) {
		Set<String> polyMethods = new HashSet<>();
		for (AnnotatedValue av : getAnnotatedValues().values()) {
			if (av.getIdentifier().startsWith("callsite"))
				continue;
			Kind kind = av.getKind();
			if (kind == Kind.FIELD || kind == Kind.LOCAL || kind == Kind.THIS) {
				Annotation anno = av.getAnnotations(this).iterator().next();
				if (anno == CLEAR)
					continue;
				SootClass sc = av.getEnclosingClass();
				if (sc.isLibraryClass())
					continue;
				out.println(av.getIdentifier());
			}
			// find poly methods
			if (kind == Kind.PARAMETER || kind == Kind.THIS
					|| (kind == Kind.LOCAL && av.getName().equals("this"))) {
				Annotation anno = av.getAnnotations(this).iterator().next();
				if (anno == CLEAR)
					continue;
				SootMethod sm = av.getEnclosingMethod();
				if (sm.isJavaLibraryMethod())
					continue;
				polyMethods.add(sm.getSignature());
			}
		}
		for (String s : polyMethods)
			out.println(s);
	}

}