package edu.rpi.reim;

import java.util.*;
import java.lang.annotation.*;
import java.util.regex.Pattern;

import soot.Body;
import soot.BodyTransformer;
import soot.Local;
import soot.PackManager;
import soot.PatchingChain;
import soot.RefType;
import soot.Type;
import soot.VoidType;
import soot.PrimType;
import soot.NullType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.SootField;
import soot.MethodSource;
import soot.Transform;
import soot.Unit;
import soot.Value;
import soot.jimple.*;
import soot.options.Options;
import soot.tagkit.*; 
import edu.rpi.Constraint.SubtypeConstraint;
import edu.rpi.Constraint.EqualityConstraint;
import edu.rpi.AnnotatedValue.FieldAdaptValue;
import edu.rpi.AnnotatedValue.MethodAdaptValue;
import edu.rpi.AnnotatedValue.Kind;
import edu.rpi.*;
import edu.rpi.ConstraintSolver.FailureStatus;
import checkers.inference.sflow.quals.*;
import checkers.inference.reim.quals.*;

public class ReimTransformer2 extends InferenceTransformer {

	private Set<Annotation> sourceAnnos;

    public final Annotation READONLY;

    public final Annotation POLYREAD;

    public final Annotation MUTABLE;

	/** For default pure method */
	private List<Pattern> defaultPurePatterns = null;

    private Set<String> defaultReadonlyRefTypes = null;
    
    // private HashMap<SootMethod,HashSet<AnnotatedValue>> parameters = new HashMap<SootMethod,HashSet<AnnotatedValue>>();

    public ReimTransformer2() {

        READONLY = AnnotationUtils.fromClass(Readonly.class);
        POLYREAD = AnnotationUtils.fromClass(Polyread.class);
        MUTABLE = AnnotationUtils.fromClass(Mutable.class);

        sourceAnnos = AnnotationUtils.createAnnotationSet();
        sourceAnnos.add(READONLY);
        sourceAnnos.add(POLYREAD);
        sourceAnnos.add(MUTABLE);

		defaultPurePatterns = new ArrayList<Pattern>(5);
        defaultPurePatterns.add(Pattern.compile("[^ \t]* equals\\(java\\.lang\\.Object\\)$"));
        defaultPurePatterns.add(Pattern.compile("[^ \t]* hashCode\\(\\)$"));
        defaultPurePatterns.add(Pattern.compile("[^ \t]* toString\\(\\)$"));
        defaultPurePatterns.add(Pattern.compile("[^ \t]* compareTo\\(.*\\)$"));

        defaultReadonlyRefTypes = new HashSet<String>();
        defaultReadonlyRefTypes.add("java.lang.String");
        defaultReadonlyRefTypes.add("java.lang.Boolean");
        defaultReadonlyRefTypes.add("java.lang.Byte");
        defaultReadonlyRefTypes.add("java.lang.Character");
        defaultReadonlyRefTypes.add("java.lang.Double");
        defaultReadonlyRefTypes.add("java.lang.Float");
        defaultReadonlyRefTypes.add("java.lang.Integer");
        defaultReadonlyRefTypes.add("java.lang.Long");
        defaultReadonlyRefTypes.add("java.lang.Number");
        defaultReadonlyRefTypes.add("java.lang.Short");
        defaultReadonlyRefTypes.add("java.util.concurrent.atomic.AtomicInteger");
        defaultReadonlyRefTypes.add("java.util.concurrent.atomic.AtomicLong");
        defaultReadonlyRefTypes.add("java.math.BigDecimal");
        defaultReadonlyRefTypes.add("java.math.BigInteger");
    }


	public boolean isDefaultReadonlyType(Type t) {
        if (t instanceof PrimType)
            return true;
        if (defaultReadonlyRefTypes.contains(t.toString()))
            return true;
        return false;
    }

	/**
	 * Check if the method is default pure. E.g. We assume 
	 * java.lang.Object.toString() is default pure. 
	 * @param methodElt
	 * @return
	 */
	public boolean isDefaultPureMethod(SootMethod sm) {
		String key = sm.getSubSignature();
		for (Pattern p : defaultPurePatterns) {
			if (p.matcher(key).matches()) {
				return true;
			}
		}
		return false;
	}

    @Override
    protected boolean isAnnotated(AnnotatedValue v) {
        Set<Annotation> diff = v.getRawAnnotations();
                
        diff.retainAll(sourceAnnos);
                
        return !diff.isEmpty();
        
    }

    @Override
    protected AnnotatedValue createFieldAdaptValue(AnnotatedValue context, 
            AnnotatedValue decl, AnnotatedValue assignTo) {
        return new FieldAdaptValue(context, decl);
    }

    @Override
    protected AnnotatedValue createMethodAdaptValue(AnnotatedValue receiver, 
            AnnotatedValue decl, AnnotatedValue assignTo) {
        if (assignTo == null)
            return decl;
        else 
            return new MethodAdaptValue(assignTo, decl);
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
            v.addAnnotation(READONLY);
            v.addAnnotation(POLYREAD);
        }
    }

    @Override
    protected void annotateField(AnnotatedValue v, SootField field) {
        if (!isAnnotated(v)) {
            if (field.getName().equals("this$0")) {
                v.setAnnotations(sourceAnnos, this);
            } else if (isDefaultReadonlyType(v.getType())) {
                v.addAnnotation(READONLY);
            } else if (!field.isStatic()) {
                v.addAnnotation(READONLY);
                v.addAnnotation(POLYREAD);
            } else
                v.setAnnotations(sourceAnnos, this);
        }
    }

    @Override
    protected void annotateThis(AnnotatedValue v, SootMethod method) {
        if (!isAnnotated(v) && !method.isStatic()) {
            if (isDefaultReadonlyType(v.getType()) && !method.isConstructor()) {
                v.addAnnotation(READONLY);
            } else if (isLibraryMethod(method)) {
            	// System.out.println("ANNOTATING a library method with default MUTABLE: "+method);
                v.addAnnotation(MUTABLE);
                // annotateLibraryParameter(v);               
            } else
                v.setAnnotations(sourceAnnos, this);
        }
        // ANA: added to check if any lib is annotated.
        else {
            // System.out.println("ANNOTATED LIB: "+method+" is annotated: "+isAnnotated(v));
        }
    }

    @Override
    protected void annotateParameter(AnnotatedValue v, SootMethod method, int index) {
        if (!isAnnotated(v)) {
            if (isDefaultReadonlyType(v.getType())) {
                v.addAnnotation(READONLY);
            } else if (isLibraryMethod(method)) {
                v.addAnnotation(MUTABLE);
            	// annotateLibraryParameter(v);
            } else 
                v.setAnnotations(sourceAnnos, this);
        }
    }

    @Override
    protected void annotateReturn(AnnotatedValue v, SootMethod method) {
        if (!isAnnotated(v) && method.getReturnType() != VoidType.v()) {
            if (isDefaultReadonlyType(v.getType())) {
                v.addAnnotation(READONLY);
            } else if (isLibraryMethod(method)) {
            	// TODO: Revisit. Add annotateLibraryReturn(v);
                v.addAnnotation(READONLY);
                v.addAnnotation(POLYREAD);
            } else 
                v.setAnnotations(sourceAnnos, this);
        }
    }

    @Override
    protected void annotateDefault(AnnotatedValue v, Kind kind, Object o) {
        if (!isAnnotated(v)) {
            if (v.getType() == NullType.v()) 
                v.addAnnotation(MUTABLE);
            else if (kind == Kind.LITERAL)
                v.addAnnotation(READONLY);
            else if (isDefaultReadonlyType(v.getType())) {
                v.addAnnotation(READONLY);
            } else 
                v.setAnnotations(sourceAnnos, this);
        }
    }

    @Override
    protected void handleInstanceFieldWrite(AnnotatedValue aBase, 
            AnnotatedValue aField, AnnotatedValue aRhs) {
        Set<Annotation> set = AnnotationUtils.createAnnotationSet();
 
        // ANA: Ignore writes on ALLOC vars
        // System.out.println("TRYING to skip init write: "+aBase+" "+aField+"="+aRhs);
        if (ReimUtils.isAllocVar(aBase) && isDefaultReadonlyType(aRhs.getType())) {
        	// System.out.println("HERE skipping init write: "+aBase+" "+aField+"="+aRhs);
        	super.handleInstanceFieldWrite(aBase, aField, aRhs);
        	return;
        }
        {
        	set.add(MUTABLE);
        	AnnotatedValue mutableConstant = getAnnotatedValue(
                MUTABLE.annotationType().getCanonicalName(), 
                VoidType.v(), Kind.CONSTANT, null, set);
        	addEqualityConstraint(aBase, mutableConstant);
        }
        super.handleInstanceFieldWrite(aBase, aField, aRhs);
    }

    @Override
    protected void handleMethodOverride(SootMethod overrider, SootMethod overridden) {
		// add constraints except that overridden is default pure
        if (!isDefaultPureMethod(overridden))
            super.handleMethodOverride(overrider, overridden);
    }

    @Override
    public int getAnnotationWeight(Annotation anno) {
        return 0;
    }

    @Override
    public FailureStatus getFailureStatus(Constraint c) {
        AnnotatedValue left = c.getLeft();
        AnnotatedValue right = c.getRight();
        if (isDefaultReadonlyType(left.getType()) || left.getKind() == Kind.LITERAL
                || isDefaultReadonlyType(right.getType()) || right.getKind() == Kind.LITERAL)
            return FailureStatus.IGNORE;

        if (isFromLibrary(left) || isFromLibrary(right))
            return FailureStatus.WARN;

        return FailureStatus.ERROR;
    }

    @Override
    public ViewpointAdapter getViewpointAdapter() {
        return new Reim2ViewpointAdapter();
    }

    @Override
    public boolean isStrictSubtyping() {
        return false;
    }

    @Override
    public String getName() {
        return "reim2";
    }
    
    
    @Override
    protected void addSubtypeConstraint(AnnotatedValue sub, AnnotatedValue sup) {
        if (sub.getKind() == Kind.LITERAL || sup.getKind() == Kind.LITERAL)
            return;
        
        // Skips constraint x <: q |> this, when x is AllocVar and callee has readonly params!
        // System.out.println("HERE a sub <: sup constraint: "+sub);
        // System.out.println("<: "+sup);
        if (ReimUtils.isMethAdapt(sup) &&
        	ReimUtils.isThis(((AnnotatedValue.AdaptValue) sup).getDeclValue()) &&
        	ReimUtils.isAllocVar(sub) &&
        	ReimUtils.isNotEscaping(((AnnotatedValue.AdaptValue) sup).getDeclValue().getEnclosingMethod()) &&
        	hasDefaultReadonlyParameters(((AnnotatedValue.AdaptValue) sup).getDeclValue().getEnclosingMethod())) {
        	// System.out.println("HERE Skipping a call on alloc var: "+sub+" <: "+sup);
        	return;
        }
        // skips constraint r0 |> f <: lhs, when EnclMethod is init.
        // System.out.println("HERE a sub <: sup constraint: "+sub);
        // System.out.println("<: "+sup);
        // NOT NEEDED WITH NEW THING...
        /*
        if (ReimUtils.isFieldAdapt(sub) &&
        	(ReimUtils.isThis(((AnnotatedValue.AdaptValue) sub).getContextValue()) ||
        			ReimUtils.isR0(((AnnotatedValue.AdaptValue) sub).getContextValue())) &&
        	isInitMethod(sup.getEnclosingMethod())) {
        	// System.out.println("HERE Skipping a read of r0 in init method: "+sub+" <: "+sup);
        	return; 
        }
        */
                
        super.addSubtypeConstraint(sub,sup);
        
        //Constraint c = new SubtypeConstraint(sub, sup);
        //if (!constraints.add(c))
        //    return;
        //addComponentConstraints(sub, sup);
	}    
	
    @Override
    // Includes the additional inits
    public boolean isInitMethod(SootMethod sm) {
    	if (sm.getName().equals("run") || sm.getName().equals("srart"))
    		return false;
    	else 
    		return sm.getName().equals("<init>") || ReimUtils.isAdditionalInit(sm);
    }
    
    private boolean hasDefaultReadonlyParameters(SootMethod sm) {
    	boolean result = true;
    	//for (int i=0; i < sm.getParameterCount(); i++) {
    	//	if (!isDefaultReadonlyType(sm.getParameterType(i)))
    	//		return false;
    	//}
    	
    	return result;
    }
    
    
    
    /*
 
    private void annotateLibraryParameter(AnnotatedValue v) {
    	Set<Annotation> set = v.getRawAnnotations();
    	
        // System.out.println("ANNOTATING LIBRARY PARAMETER: "+v+ " and the set "+set);
    	
    	// if the set is empty, or if it does not contain either of the Reim annotations, add Mutable.
    	if (set.isEmpty() || !(set.contains(AnnotationUtils.fromClass(Readonly.class)) ||
    			set.contains(AnnotationUtils.fromClass(Polyread.class)) ||
    			set.contains(AnnotationUtils.fromClass(Mutable.class)))) { 
    		v.addAnnotation(MUTABLE);
    	}
    	else {
    		System.out.println("HERE, annotating "+v+" and set: "+set);
    		if (set.contains(AnnotationUtils.fromClass(Readonly.class))) {
    			v.addAnnotation(READONLY); 
    			System.out.println("HERE, ADDED Readonly anno on the parameter!");
    		}
    		if (set.contains(AnnotationUtils.fromClass(Polyread.class))) v.addAnnotation(POLYREAD);
    		if (set.contains(AnnotationUtils.fromClass(Mutable.class))) v.addAnnotation(MUTABLE);    		    		
    	}
    	
    }
    */
    
}
