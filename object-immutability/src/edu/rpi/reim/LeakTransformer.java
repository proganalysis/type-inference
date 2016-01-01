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
import checkers.inference.leak.quals.*;
import checkers.inference.reim.quals.Readonly;

public class LeakTransformer extends InferenceTransformer {


    private Set<Annotation> sourceAnnos;

    public final Annotation LEAK;

    public final Annotation NOLEAK;
    
    public final Annotation POLY;

    private HashSet<AnnotatedValue> thisSet = new HashSet<AnnotatedValue>();

    public LeakTransformer() {

        LEAK = AnnotationUtils.fromClass(Leak.class);
        NOLEAK = AnnotationUtils.fromClass(Noleak.class);
        POLY = AnnotationUtils.fromClass(Poly.class);
        
        sourceAnnos = AnnotationUtils.createAnnotationSet();
        sourceAnnos.add(LEAK);
        sourceAnnos.add(NOLEAK);
        sourceAnnos.add(POLY);

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
            v.addAnnotation(LEAK);
            // v.addAnnotation(NOLEAK);
        }
    }

    @Override
    protected void annotateField(AnnotatedValue v, SootField field) {
    	
        if (!isAnnotated(v)) {
            if (field.getName().equals("this$0")) {
                v.setAnnotations(sourceAnnos, this);
            } else {
            	
            	//System.out.println("Annotating field "+v);
            	
            	if (!v.getRawAnnotations().contains(AnnotationUtils.fromClass(Readonly.class))) {    
            		//System.out.println("NON-READONLY field, annotating LEAK");
            		v.addAnnotation(LEAK);
            		
            	}
            	else {
            		//System.out.println("READONLY Field "+v);
            		v.addAnnotation(LEAK);
            		v.addAnnotation(NOLEAK);
            		//v.addAnnotation(POLY);
            	}
            }
        }
        else {
        	System.out.println("Field is already annotated: "+v);
        }
    }

    @Override
    protected void annotateThis(AnnotatedValue v, SootMethod method) {
        if (!isAnnotated(v) && !method.isStatic()) {
        	if (isLibraryMethod(method)) {
            	// System.out.println("Library THIS in LeakTransformer. Is there a Reim anno?: "+v);
                v.addAnnotation(LEAK);
            	v.addAnnotation(NOLEAK); // ANA, will change!
            } else {
                v.setAnnotations(sourceAnnos, this);        	
                thisSet.add(v); // Adding the this annotated values. TODO: Are all values unanotated? I think yes.            
            }
        }
        // ANA: added to check if any lib is annotated.
        else {
        	System.out.println("Annotated lib: "+method+" is annotated: "+isAnnotated(v) + v.getAnnotations(this).toString());
        }
    }

    @Override
    protected void annotateParameter(AnnotatedValue v, SootMethod method, int index) {
        if (!isAnnotated(v)) {
        	if (isLibraryMethod(method)) {
                v.addAnnotation(LEAK);
            } else {
                v.setAnnotations(sourceAnnos, this);
                // thisSet.add(v); // ANA: Added to count number of parameters.
            }
        }
    }

    @Override
    protected void annotateReturn(AnnotatedValue v, SootMethod method) {
        if (!isAnnotated(v) && method.getReturnType() != VoidType.v()) {
        	if (isLibraryMethod(method)) {
                v.addAnnotation(POLY);
                v.addAnnotation(NOLEAK);
            } else 
                v.setAnnotations(sourceAnnos, this);
        }
    }

    @Override
    protected void annotateDefault(AnnotatedValue v, Kind kind, Object o) {
        if (!isAnnotated(v)) {
            if (v.getType() == NullType.v()) 
                v.addAnnotation(LEAK); // TODO: What's going on here?
            else if (kind == Kind.LITERAL) {
                v.addAnnotation(NOLEAK);
                v.addAnnotation(LEAK);
                v.addAnnotation(POLY);
            	// v.setAnnotations(sourceAnnos, this);
            } 
            else 
            	v.setAnnotations(sourceAnnos, this);
        }
    }
    
/* No reason to handle instanceFieldWrite and methodOverride specially!
 * 
    @Override
    protected void handleInstanceFieldWrite(AnnotatedValue aBase, 
            AnnotatedValue aField, AnnotatedValue aRhs) {
        Set<Annotation> set = AnnotationUtils.createAnnotationSet();
        // ANA: Added ignore of r0
        if (isInitMethod(aBase.getEnclosingMethod()) &&
        		aBase.getName().equals("r0")) {
        	super.handleInstanceFieldWrite(aBase, aField, aRhs);
        	return;
        }
        
        set.add(MUTABLE);
        AnnotatedValue mutableConstant = getAnnotatedValue(
                MUTABLE.annotationType().getCanonicalName(), 
                VoidType.v(), Kind.CONSTANT, null, set);
        addEqualityConstraint(aBase, mutableConstant);        
        
        super.handleInstanceFieldWrite(aBase, aField, aRhs);
    }

    
    @Override
    protected void handleMethodOverride(SootMethod overrider, SootMethod overridden) {
		// add constraints except that overridden is default pure
        if (!isDefaultPureMethod(overridden))
            super.handleMethodOverride(overrider, overridden);
    }
*/

    @Override
    public int getAnnotationWeight(Annotation anno) {
        return 0;
    }

    @Override
    public FailureStatus getFailureStatus(Constraint c) {
        AnnotatedValue left = c.getLeft();
        AnnotatedValue right = c.getRight();
        /*
        if (isDefaultReadonlyType(left.getType()) || left.getKind() == Kind.LITERAL
                || isDefaultReadonlyType(right.getType()) || right.getKind() == Kind.LITERAL)
            return FailureStatus.IGNORE;
         */
        if (isFromLibrary(left) || isFromLibrary(right))
            return FailureStatus.WARN;

        return FailureStatus.ERROR;
    }

    @Override
    public ViewpointAdapter getViewpointAdapter() {
        return new LeakViewpointAdapter();
    }

    @Override
    public boolean isStrictSubtyping() {
        return false;
    }

    @Override
    public String getName() {
        return "leak";
    }
    
    public HashSet<AnnotatedValue> getThisSet() {
    	return thisSet;
    }
    
}    
