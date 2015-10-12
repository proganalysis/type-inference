package edu.rpi.sflow;

import java.util.Iterator;
import java.util.*;
import java.lang.annotation.*;
import java.lang.Thread;

import soot.Body;
import soot.BodyTransformer;
import soot.Local;
import soot.PackManager;
import soot.PatchingChain;
import soot.RefType;
import soot.Type;
import soot.VoidType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.SootField;
import soot.MethodSource;
import soot.Transform;
import soot.Unit;
import soot.Value;
import soot.jimple.AbstractStmtSwitch;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.*;
import soot.jimple.Jimple;
import soot.jimple.StringConstant;
import soot.options.Options;
import soot.tagkit.*;
import soot.util.Chain;
import edu.rpi.Constraint.SubtypeConstraint;
import edu.rpi.AnnotatedValue.FieldAdaptValue;
import edu.rpi.AnnotatedValue.MethodAdaptValue;
import edu.rpi.AnnotatedValue.Kind;
import edu.rpi.*;
import edu.rpi.ConstraintSolver.FailureStatus;
 
import checkers.inference.sflow.quals.*;
import checkers.inference.reim.quals.*;
import soot.util.NumberedString;


public class SFlowTransformer extends InferenceTransformer {

    public static final String OPTION_POLY_LIBRARY = "polyLibrary";

    public static final String OPTION_USE_REIM = "useReim";

    public static final String OPTION_ANDROID_APP = "inferAndroidApp";

    private Set<Annotation> sourceAnnos;

    private Set<Annotation> thisAnnos;

    private Set<String> androidClasses; 

    private boolean isPolyLibrary;

    private boolean useReim; 
    
    private boolean inferAndroidApp;

    public final Annotation TAINTED;

    public final Annotation POLY;

    public final Annotation SAFE;

    public final Annotation BOTTOM;

    private final Annotation READONLYTHIS;

    private final Annotation POLYREADTHIS;

    private final Annotation TAINTEDTHIS;
    private final Annotation POLYTHIS;
    private final Annotation SAFETHIS;

    private int sourceNum; 

    private int sinkNum; 

    public SFlowTransformer() {
        isPolyLibrary = (System.getProperty(OPTION_POLY_LIBRARY) != null);
        useReim = (System.getProperty(OPTION_USE_REIM) != null);
        inferAndroidApp = (System.getProperty(OPTION_ANDROID_APP) != null);

        TAINTED = AnnotationUtils.fromClass(Tainted.class);
        POLY = AnnotationUtils.fromClass(Poly.class);
        SAFE = AnnotationUtils.fromClass(Safe.class);
        BOTTOM = AnnotationUtils.fromClass(Bottom.class);

        TAINTEDTHIS = AnnotationUtils.fromClass(TaintedThis.class);
        POLYTHIS = AnnotationUtils.fromClass(PolyThis.class);
        SAFETHIS = AnnotationUtils.fromClass(SafeThis.class);

        READONLYTHIS = AnnotationUtils.fromClass(ReadonlyThis.class);
        POLYREADTHIS = AnnotationUtils.fromClass(PolyreadThis.class);

        sourceAnnos = AnnotationUtils.createAnnotationSet();
        sourceAnnos.add(TAINTED);
        sourceAnnos.add(POLY);
        sourceAnnos.add(SAFE);

        thisAnnos = AnnotationUtils.createAnnotationSet();
        thisAnnos.add(TAINTEDTHIS);
        thisAnnos.add(POLYTHIS);
        thisAnnos.add(SAFETHIS);

        androidClasses = new HashSet<String>();
        androidClasses.add("android.app.Activity");
        androidClasses.add("android.app.Service");
        androidClasses.add("android.location.LocationListener");

        sourceNum = 0;
        sinkNum = 0;
    }

    public boolean isPolyLibrary() {
        return isPolyLibrary;
    }

    public boolean useReim() {
        return useReim;
    }

    public boolean inferAndroidApp() {
        return inferAndroidApp;
    }

    private Set<Annotation> extractLibraryAnnos(AnnotatedValue av) {
        Set<Annotation> annos = null;
        if (av.getKind() == Kind.PARAMETER) {
            int index = Integer.parseInt(
                    av.getName().substring("parameter".length()));
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
        if (annos != null && !annos.isEmpty() 
                && annos.contains(TAINTED)) {
            return true;
        }
        return false;
    }

    private boolean isSink(AnnotatedValue av) {
        Set<Annotation> annos = extractLibraryAnnos(av);
        if (annos != null && !annos.isEmpty() 
                && annos.contains(SAFE)) {
            return true;
        }
        return false;
    }

    @Override
    protected void handleMethodCall(InvokeExpr v, AnnotatedValue assignTo) {
        // Add default annotations/constraints for library methods
        SootMethod invokeMethod = v.getMethod();
        // String lindsey_format = "LINDSEY: %s --> %s";
        String method_name = invokeMethod.getName();
        // String invoking_class;
        String pnames;
        try {
        	// invoking_class = v.getMethodRef().declaringClass().getName();
            pnames = v.getMethodRef().declaringClass().getSuperclass().getName();
        } catch(RuntimeException e) {
            // invoking_class = "ERROR getting invoking class";
            pnames = "ERROR getting superclass";
        }
        if(pnames.equals("java.lang.Thread") && method_name.equals("start")) {
            /*System.out.println(String.format(lindsey_format, "Method", method_name));
            System.out.println(String.format(lindsey_format, "Invoking Class", invoking_class));
            System.out.println(String.format(lindsey_format, "Parent superclass", pnames));
            System.out.println("----------------------");*/
            invokeMethod = v.getMethodRef().declaringClass().getMethodByName("run");
        }
        if (isPolyLibrary() && isLibraryMethod(invokeMethod)) {
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
                    if (extractLibraryAnnos(aIn).isEmpty() 
                            && extractLibraryAnnos(aOut).isEmpty())
                        super.addSubtypeConstraint(aIn, aOut);
                }
            }
            if (!invokeMethod.isStatic() && !invokeMethod.isConstructor()) {
                AnnotatedValue aThis = getAnnotatedThis(invokeMethod);
                if (aOut != null && extractLibraryAnnos(aOut).isEmpty() 
                        && extractLibraryAnnos(aThis).isEmpty()) {
                    super.addSubtypeConstraint(aThis, aOut);
                    super.addSubtypeConstraint(aOut, aThis);
                }
                // if THIS is not annotated as READONLY
                Set<Annotation> annos = getRawVisibilityTags(invokeMethod);
                if (!annos.contains(READONLYTHIS) && !annos.contains(POLYREADTHIS) ) {
                    for (int i = 0; i < invokeMethod.getParameterCount(); i++) {
                        AnnotatedValue aIn = getAnnotatedParameter(invokeMethod, i);
                        if (extractLibraryAnnos(aIn).isEmpty() 
                                && extractLibraryAnnos(aThis).isEmpty())
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
                    System.out.println("INFO: found SOURCE " + l + " at " 
                            + "\n\t" + getVisitorState().getSootMethod()
                            + "\n\t" + getVisitorState().getUnit());
                    sourceNum++;
                } else if (isSink(l)) {
                    System.out.println("INFO: found SINK " + l + " at " 
                            + "\n\t" + getVisitorState().getSootMethod()
                            + "\n\t" + getVisitorState().getUnit());
                    sinkNum++;
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

    public int getSourceNum() {
        return sourceNum;
    }

    public int getSinkNum() {
        return sinkNum;
    }

    @Override
    protected AnnotatedValue createFieldAdaptValue(AnnotatedValue context, 
            AnnotatedValue decl, AnnotatedValue assignTo) {
//        if (context.getName().equals("this"))
//            return decl;
        return new FieldAdaptValue(context, decl);
    }

    @Override
    protected AnnotatedValue createMethodAdaptValue(AnnotatedValue receiver, 
            AnnotatedValue decl, AnnotatedValue assignTo) {
        String callSiteIdentifier = CALLSITE_PREFIX + getVisitorState().getSootMethod().getSignature()
            + "<" + getVisitorState().getUnit().hashCode() + ">";
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
            v.addAnnotation(TAINTED);
            v.addAnnotation(POLY);
        }
    }

    @Override
    protected void annotateField(AnnotatedValue v, SootField field) {
        if (!isAnnotated(v)) {
            if (field.getName().equals("this$0")) {
                v.setAnnotations(sourceAnnos, this);
            } 
            else if (!field.isStatic()) {
                v.addAnnotation(TAINTED);
                v.addAnnotation(POLY);
            } else
                v.setAnnotations(sourceAnnos, this);
        }
    }

    @Override
    protected void annotateThis(AnnotatedValue v, SootMethod method) {
        if (!isAnnotated(v) && !method.isStatic()) {
            if (isPolyLibrary() && isLibraryMethod(method)) {
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
                v.addAnnotation(POLY);
            } else 
                v.setAnnotations(sourceAnnos, this);
        }
    }

    @Override
    protected void annotateDefault(AnnotatedValue v, Kind kind, Object o) {
        if (kind == Kind.LITERAL)
            v.addAnnotation(BOTTOM);
        else
            v.setAnnotations(sourceAnnos, this);
    }  

    @Override
    protected void handleStaticFieldRead(AnnotatedValue aField, AnnotatedValue aLhs) {
        super.handleStaticFieldRead(aField, aLhs);
        if (isSource(aField)) {
            System.out.println("INFO: found SOURCE " + aField + " at " 
                    + "\n\t" + getVisitorState().getSootMethod()
                    + "\n\t" + getVisitorState().getUnit());
            sourceNum++;
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
					System.out.println("INFO: found SOURCE " + overriddenParam + " at "
							+ "\n\t" + getVisitorState().getSootMethod()
							+ "\n\t" + getVisitorState().getUnit());
					sourceNum++;
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
        return 0;
    }

    @Override
    public FailureStatus getFailureStatus(Constraint c) {
        return FailureStatus.ERROR;
    }

    @Override
    public ViewpointAdapter getViewpointAdapter() {
        return new SFlowViewpointAdapter();
    }

    @Override
    public boolean isStrictSubtyping() {
        return true;
    }

    @Override
    protected void processMethod(SootMethod sm) {
        super.processMethod(sm);
        // connect THIS of callback methods for Android app
        if (inferAndroidApp) {
            boolean needConnect = false;
//            Map<SootClass, SootMethod> overriddens = InferenceUtils.overriddenMethods(sm);
//            for (SootClass sc : overriddens.keySet()) {
//                if (androidClasses.contains(sc.getName())) {
//                    needConnect = true;
//                    break;
//                }
//            }
            String methodName = sm.getName();
            if (!needConnect/* && sm.isConstructor()*/
                    && !sm.isStatic()
                    && !methodName.equals("<clinit>")
                    && !methodName.startsWith("access$")) {
                // check if it is constructor
                Set<SootClass> supertypes = InferenceUtils.getSuperTypes(sm.getDeclaringClass());
                for (SootClass sc : supertypes) 
                    if (androidClasses.contains(sc.getName())) {
                        needConnect = true;
                        break;
                    }
            }
            if (needConnect) {
                AnnotatedValue classValue = 
                    getAnnotatedClass(getVisitorState().getSootClass());
                AnnotatedValue thisValue = getAnnotatedThis(sm);
                addEqualityConstraint(classValue, thisValue);
            }
        }
    }
    
    @Override
    public String getName() {
        return "sflow";
    }
}
