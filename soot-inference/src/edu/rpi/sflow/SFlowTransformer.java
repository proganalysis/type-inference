package edu.rpi.sflow;

import java.util.Iterator;
import java.util.*;
import java.lang.annotation.*;

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

import edu.rpi.Constraint.SubtypeConstraint;
import edu.rpi.AnnotatedValue.FieldAdaptValue;
import edu.rpi.AnnotatedValue.MethodAdaptValue;
import edu.rpi.AnnotatedValue.Kind;
import edu.rpi.*;
import edu.rpi.ConstraintSolver.FailureStatus;
 
import checkers.inference.sflow.quals.*;
import checkers.inference.reim.quals.*;


public class SFlowTransformer extends InferenceTransformer {

    public static final String OPTION_POLY_LIBRARY = "polyLibrary";

    public static final String OPTION_USE_REIM = "useReim";

    public static final String OPTION_ANDROID_APP = "inferAndroidApp";

    private Set<Annotation> sourceAnnos;

    private Set<String> androidClasses; 

    private boolean isPolyLibrary;

    private boolean useReim; 
    
    private boolean inferAndroidApp;

    public final Annotation TAINTED;

    public final Annotation POLY;

    public final Annotation SAFE;

    public final Annotation BOTTOM;

    private final Annotation READONLYTHIS;

    private final Annotation POLYTHIS;

    public SFlowTransformer() {
        isPolyLibrary = (System.getProperty(OPTION_POLY_LIBRARY) != null);
        useReim = (System.getProperty(OPTION_USE_REIM) != null);
        inferAndroidApp = (System.getProperty(OPTION_ANDROID_APP) != null);

        TAINTED = AnnotationUtils.fromClass(Tainted.class);
        POLY = AnnotationUtils.fromClass(Poly.class);
        SAFE = AnnotationUtils.fromClass(Safe.class);
        BOTTOM = AnnotationUtils.fromClass(Bottom.class);
        READONLYTHIS = AnnotationUtils.fromClass(ReadonlyThis.class);
        POLYTHIS = AnnotationUtils.fromClass(PolyThis.class);

        sourceAnnos = AnnotationUtils.createAnnotationSet();
        sourceAnnos.add(TAINTED);
        sourceAnnos.add(POLY);
        sourceAnnos.add(SAFE);

        androidClasses = new HashSet<String>();
        androidClasses.add("android.app.Activity");
        androidClasses.add("android.app.Service");
        androidClasses.add("android.location.LocationListener");
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

    @Override
    protected void handleMethodCall(InvokeExpr v, AnnotatedValue assignTo) {
        // Add default annotations/constraints for library methods
        SootMethod invokeMethod = v.getMethod();
        if (isPolyLibrary() && isLibraryMethod(invokeMethod)) {
            // Add constraints PARAM -> RET for library methods
            AnnotatedValue aOut = null;
            if (invokeMethod.isConstructor())
                aOut = getAnnotatedThis(invokeMethod);
            else if (invokeMethod.getReturnType() != VoidType.v()) 
                aOut = getAnnotatedReturn(invokeMethod);
            if (aOut != null) {
                for (int i = 0; i < invokeMethod.getParameterCount(); i++) {
                    AnnotatedValue aIn = getAnnotatedParameter(invokeMethod, i);
                    super.addSubtypeConstraint(aIn, aOut);
                }
                if (!invokeMethod.isStatic() && !invokeMethod.isConstructor()) {
                    AnnotatedValue aThis = getAnnotatedThis(invokeMethod);
                    super.addSubtypeConstraint(aThis, aOut);
                    // if THIS is not annotated as READONLY
                    Set<Annotation> annos = getRawVisibilityTags(invokeMethod);
                    if (!annos.contains(READONLYTHIS) && !annos.contains(POLYTHIS) ) {
                        for (int i = 0; i < invokeMethod.getParameterCount(); i++) {
                            AnnotatedValue aIn = getAnnotatedParameter(invokeMethod, i);
                            super.addSubtypeConstraint(aIn, aThis);
                        }
                    }
                }
            }
        }
        super.handleMethodCall(v, assignTo);
    }

    @Override
    protected boolean isAnnotated(AnnotatedValue v) {
        return isAnnotated(v.getAnnotations());
    }

    private boolean isAnnotated(Set<Annotation> annos) {
        Set<Annotation> set = AnnotationUtils.createAnnotationSet();
        set.addAll(annos);
        set.retainAll(sourceAnnos);
        return !set.isEmpty();
    }

    @Override
    protected AnnotatedValue createFieldAdaptValue(AnnotatedValue context, 
            AnnotatedValue decl, AnnotatedValue assignTo) {
        return new FieldAdaptValue(context, decl);
    }

    @Override
    protected AnnotatedValue createMethodAdaptValue(AnnotatedValue receiver, 
            AnnotatedValue decl, AnnotatedValue assignTo) {
        String callSiteIdentifier = CALLSITE_PREFIX + getVisitorState().getSootMethod().getSignature()
            + "<" + getVisitorState().getUnit().hashCode() + ">";
        AnnotatedValue callSite = getAnnotatedValue(callSiteIdentifier, VoidType.v(), Kind.CONSTANT, null);
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
    protected void annotateField(AnnotatedValue v, SootField field) {
        if (!isAnnotated(v)) {
            if (field.getName().equals("this$0")) {
                v.setAnnotations(sourceAnnos);
            } 
            else if (!field.isStatic()) {
                v.addAnnotation(TAINTED);
                v.addAnnotation(POLY);
            } else
                v.setAnnotations(sourceAnnos);
        }
    }

    @Override
    protected void annotateThis(AnnotatedValue v, SootMethod method) {
        if (!isAnnotated(v) && !method.isStatic()) {
            if (isPolyLibrary() && isLibraryMethod(method)) {
                v.addAnnotation(POLY);
            } else
                v.setAnnotations(sourceAnnos);
        }
    }

    @Override
    protected void annotateParameter(AnnotatedValue v, SootMethod method, int index) {
        if (!isAnnotated(v)) {
            if (isPolyLibrary() && isLibraryMethod(method)) {
                v.addAnnotation(POLY);
            } else 
                v.setAnnotations(sourceAnnos);
        }
    }

    @Override
    protected void annotateReturn(AnnotatedValue v, SootMethod method) {
        if (!isAnnotated(v) && method.getReturnType() != VoidType.v()) {
            if (isPolyLibrary() && isLibraryMethod(method)) {
                v.addAnnotation(POLY);
            } else 
                v.setAnnotations(sourceAnnos);
        }
    }

    @Override
    protected void annotateDefault(AnnotatedValue v, Kind kind, Object o) {
        if (kind == Kind.LITERAL)
            v.addAnnotation(BOTTOM);
        else
            v.setAnnotations(sourceAnnos);
    }

    @Override
    protected void handleMethodOverride(SootMethod overrider, SootMethod overridden) {
        // only handle overridden methods with active body?
        if (!overrider.isStatic()) {
            // this: overridden <: overrider 
            AnnotatedValue overriderThis = getAnnotatedThis(overrider);
            AnnotatedValue overriddenThis = getAnnotatedThis(overridden);
            if (!isFromLibrary(overriddenThis) || isAnnotated(getVisibilityTags(overridden, Kind.THIS))) 
                addSubtypeConstraint(overriddenThis, overriderThis);
        }
        // parameter: overridden <: overrider 
        assert overrider.getParameterCount() == overridden.getParameterCount();
        for (int i = 0; i < overrider.getParameterCount(); i++) {
            AnnotatedValue overriderParam = getAnnotatedParameter(overrider, i);
            AnnotatedValue overriddenParam = getAnnotatedParameter(overridden, i);
            if (!isFromLibrary(overriddenParam) || isAnnotated(getVisibilitParameterTags(overridden, i))) 
                addSubtypeConstraint(overriddenParam, overriderParam);
        }
        if (overrider.getReturnType() != VoidType.v()) {
            // return: overrider <: overridden 
            AnnotatedValue overriderRet = getAnnotatedReturn(overrider);
            AnnotatedValue overriddenRet = getAnnotatedReturn(overridden);
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
