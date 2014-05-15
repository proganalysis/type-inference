package edu.rpi.sflow;

import java.util.Iterator;
import java.util.*;
import java.io.*;
import java.lang.annotation.*;

import soot.Body;
import soot.BodyTransformer;
import soot.Local;
import soot.PackManager;
import soot.PatchingChain;
import soot.RefType;
import soot.Type;
import soot.ArrayType;
import soot.VoidType;
import soot.NullType;
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

import edu.rpi.AnnotatedValue.FieldAdaptValue;
import edu.rpi.AnnotatedValue.MethodAdaptValue;
import edu.rpi.AnnotatedValue.AdaptValue;
import edu.rpi.AnnotatedValue.Kind;
import edu.rpi.*;
import edu.rpi.ConstraintSolver.FailureStatus;
import edu.rpi.ConstraintSolver.SolverException;
import edu.rpi.Constraint.SubtypeConstraint;
import edu.rpi.Constraint.EqualityConstraint;
import edu.rpi.Constraint.UnequalityConstraint;
 
import checkers.inference.sflow.quals.*;
import checkers.inference.reim.quals.*;


public class SFlowConstraintSolver extends AbstractConstraintSolver {

    protected SFlowTransformer st;

    protected Annotation READONLY = AnnotationUtils.fromClass(Readonly.class);

    protected Annotation POLYREAD = AnnotationUtils.fromClass(Polyread.class);

	private Map<String, Set<AdaptValue>> declRefToAdaptValue = new HashMap<String, Set<AdaptValue>>(); 

	private Map<String, Set<AdaptValue>> contextRefToAdaptValue = new HashMap<String, Set<AdaptValue>>(); 

	private Map<String, Set<Constraint>> lessValues = new HashMap<String, Set<Constraint>>(); 

	private Map<String, Set<Constraint>> greaterValues = new HashMap<String, Set<Constraint>>(); 

    private Set<Constraint> worklist = new LinkedHashSet<Constraint>();

    private boolean preferSource = false;

    private boolean preferSink = false;

    private boolean isInteractive = false;

    private BitSet updated = new BitSet(AnnotatedValue.maxId());
//    private BitSet restored = new BitSet(AnnotatedValue.maxId());

    private byte[] initAnnos = new byte[AnnotatedValue.maxId()];

    private final byte TAINTED_MASK = 0x01;
    private final byte POLY_MASK = 0x02;
    private final byte SAFE_MASK = 0x04;
    private final byte BOTTOM_MASK = 0x08;

    public SFlowConstraintSolver(InferenceTransformer t) {
    	this(t, false);
    }

    public SFlowConstraintSolver(InferenceTransformer t, boolean b) {
        super(t, b);
        if (!(t instanceof SFlowTransformer)) 
            throw new RuntimeException("SFlowConstraintSolver expects SFlowTransformer");
        this.st = (SFlowTransformer) t;
        this.preferSource = (System.getProperty("preferSource") != null);
        this.preferSink = (System.getProperty("preferSink") != null);
        this.isInteractive = (System.getProperty("interactive") != null);

        if (preferSink && preferSource) {
            throw new RuntimeException("Can only have one of {preferSource, preferSource}!");
        }
    }


    private byte toBits(Set<Annotation> annos) {
        byte b = 0;
        for (Annotation anno : annos) {
            if (anno.equals(st.TAINTED))
                b |= TAINTED_MASK;
            else if (anno.equals(st.POLY))
                b |= POLY_MASK;
            else if (anno.equals(st.SAFE))
                b |= SAFE_MASK;
            else if (anno.equals(st.BOTTOM))
                b |= BOTTOM_MASK;
        }
        return b;
    }

    private Set<Annotation> fromBits(byte b) {
        Set<Annotation> annos = AnnotationUtils.createAnnotationSet();
        if ((b & TAINTED_MASK) > 0)
            annos.add(st.TAINTED);
        if ((b & POLY_MASK) > 0)
            annos.add(st.POLY);
        if ((b & SAFE_MASK) > 0)
            annos.add(st.SAFE);
        if ((b & BOTTOM_MASK) > 0)
            annos.add(st.BOTTOM);
        return annos;
    }

    private void setInitAnnos(AnnotatedValue av) {
        int id = av.getId();
        initAnnos[id] = toBits(av.getAnnotations(st));
    }

    private Set<Annotation> getInitAnnos(int id) {
        byte b = initAnnos[id];
        return fromBits(b);
    }

    private boolean containsReadonly(AnnotatedValue av) {
        if (av instanceof AdaptValue)
            av = ((AdaptValue) av).getDeclValue();
        if (av.getType() == NullType.v())
            return true;

        AnnotatedValue reimValue = AnnotatedValueMap.v().get(av.getIdentifier());
        if (reimValue != null && (reimValue.containsAnno(READONLY) 
                    /*|| reimValue.containsAnno(POLYREAD)*/))
            return true;
        return false;
    }

    private void updateConstraintsWithReim(Map<SootMethod, Set<Constraint>> consByMethod) {
        for (Map.Entry<SootMethod, Set<Constraint>> entry : consByMethod.entrySet()) {
            updateConstraintsWithReim(entry.getValue());
        }
    }

    private Set<Constraint> getLessConstraints(AnnotatedValue av) {
        Set<Constraint> set = lessValues.get(av.getIdentifier());
        if (set == null)
            return Collections.<Constraint>emptySet();
        else 
            return set;
    }

    private void addLessConstraint(AnnotatedValue av, Constraint less) {
        Set<Constraint> set = lessValues.get(av.getIdentifier());
        if (set == null) {
            set = new HashSet<Constraint>();
            lessValues.put(av.getIdentifier(), set);
        }
        set.add(less);
    }

    private Set<Constraint> getGreaterConstraints(AnnotatedValue av) {
        Set<Constraint> set = greaterValues.get(av.getIdentifier());
        if (set == null) 
            return Collections.<Constraint>emptySet();
        else 
            return set;
    }

    private void addGreaterConstraint(AnnotatedValue av, Constraint greater) {
        Set<Constraint> set = greaterValues.get(av.getIdentifier());
        if (set == null) {
            set = new HashSet<Constraint>();
            greaterValues.put(av.getIdentifier(), set);
        }
        set.add(greater);
    }

	private void buildRefToConstraintMapping(Constraint c) {
        AnnotatedValue left = null, right = null; 
        if (c instanceof SubtypeConstraint
                || c instanceof EqualityConstraint
                || c instanceof UnequalityConstraint) {
            left = c.getLeft();
            right = c.getRight();
        }
        if (left != null && right != null) {
            AnnotatedValue[] refs = {left, right};
            for (AnnotatedValue ref : refs) {
                if (ref instanceof AdaptValue) {
                    // Add decl and context
                    AnnotatedValue decl = ((AdaptValue) ref).getDeclValue();
                    AnnotatedValue context = ((AdaptValue) ref).getContextValue();
                    // Add mapping from decl -> adaptValue
                    Set<AdaptValue> adaptSet = declRefToAdaptValue.get(decl.getIdentifier());
                    if (adaptSet == null) {
                        adaptSet = new HashSet<AdaptValue>();
                        declRefToAdaptValue.put(decl.getIdentifier(), adaptSet);
                    }
                    adaptSet.add((AdaptValue) ref);

                    // Add mapping from context -> adaptValue
                    if (!context.getIdentifier().startsWith(InferenceTransformer.CALLSITE_PREFIX)
                            && !context.getIdentifier().startsWith(InferenceTransformer.FAKE_PREFIX)) {
                        Set<AdaptValue> adaptSet2 = contextRefToAdaptValue.get(context.getIdentifier());
                        if (adaptSet2 == null) {
                            adaptSet2 = new HashSet<AdaptValue>();
                            contextRefToAdaptValue.put(context.getIdentifier(), adaptSet2);
                        }
                        adaptSet2.add((AdaptValue) ref);
                    }
                }
            }
            if (c instanceof SubtypeConstraint) {
                addGreaterConstraint(left, c);
                addLessConstraint(right, c);
            } else if (c instanceof EqualityConstraint) {
                addGreaterConstraint(left, c);
                addLessConstraint(left, c);
                addGreaterConstraint(right, c);
                addLessConstraint(right, c);
            }
        }
    }
	
	private void buildRefToConstraintMapping(Set<Constraint> cons) {
		for (Constraint c : cons) {
            buildRefToConstraintMapping(c);
		}
	}

    private boolean canConnectVia(AnnotatedValue left, AnnotatedValue right) {
        if (left == null || right == null) 
            return false;

        if (left.getKind() == Kind.LITERAL || right.getKind() == Kind.LITERAL)
            return false;

        SootMethod leftSm = left.getEnclosingMethod();
        SootMethod rightSm = right.getEnclosingMethod();

        if (leftSm != null && rightSm != null) {
            if (leftSm.equals(rightSm))
                return true;
            else if (leftSm.getName().equals(rightSm.getName())) {
                SootClass leftSc = left.getEnclosingClass();
                SootClass rightSc = right.getEnclosingClass();
                Set<SootClass> leftSuper = InferenceUtils.getSuperTypes(leftSc);
                if (leftSuper.contains(right))
                    return true;

                Set<SootClass> rightSuper = InferenceUtils.getSuperTypes(rightSc);
                if (rightSuper.contains(right))
                    return true;
            }
        }
        return false;
    }

    private boolean canConnectViaOld(AnnotatedValue left, AnnotatedValue right) {

        SootClass leftSc = left.getEnclosingClass();
        SootClass rightSc = right.getEnclosingClass();

        if (leftSc.equals(rightSc))
            return true;

        Set<SootClass> leftSuper = InferenceUtils.getSuperTypes(leftSc);
        if (leftSuper.contains(right))
            return true;

        Set<SootClass> rightSuper = InferenceUtils.getSuperTypes(rightSc);
        if (rightSuper.contains(right))
            return true;

        return false;
    }

    private boolean isParamOrRetValue(AnnotatedValue av) {
        return (av.getKind() == Kind.PARAMETER 
                || av.getKind() == Kind.THIS
                || av.getKind() == Kind.RETURN);
    }

    private Set<Constraint> addLinearConstraints(Constraint con, Set<Constraint> existingCons) {
        Set<Constraint> newCons = new LinkedHashSet<Constraint>();
        if (!(con instanceof SubtypeConstraint))
            return newCons;
        Queue<Constraint> queue = new LinkedList<Constraint>();
        queue.add(con); 
        while (!queue.isEmpty()) {
            Constraint c = queue.poll();
            AnnotatedValue left = c.getLeft();
            AnnotatedValue right = c.getRight();
            AnnotatedValue ref = null;
            List<Constraint> tmplist = new LinkedList<Constraint>();
            // Step 1: field read
            // Nov 26, 2013: add linear constraint for array fields
            if ((left instanceof FieldAdaptValue)) {
                if ((ref = ((FieldAdaptValue) left).getDeclValue()) != null
                    &&(ref.getAnnotations(st).size() == 1 && ref.getAnnotations(st).contains(st.POLY)
                        || ((FieldAdaptValue) left).getContextValue().getType() instanceof ArrayType)) {
                    Constraint linear = new SubtypeConstraint(
                                ((FieldAdaptValue) left).getContextValue(), right);
                    linear.addCause(c);
                    tmplist.add(linear);
                }
                for (Constraint lc : getLessConstraints(left)) {
                    AnnotatedValue r = lc.getLeft();
                    if (!r.equals(right) && !(r instanceof MethodAdaptValue)) {
                        Constraint linear = new SubtypeConstraint(r, right);
                        linear.addCause(lc);
                        linear.addCause(c);
                        tmplist.add(linear);
                    }
                }
            }
            // Step 2: field write
            // Nov 26, 2013: add linear constraint for array fields
            else if ((right instanceof FieldAdaptValue) ) {
                if ((ref = ((FieldAdaptValue) right).getDeclValue()) != null
                    && (ref.getAnnotations(st).size() == 1 && ref.getAnnotations(st).contains(st.POLY)
//                        || preferSource  // FIXED fields on May 7, 2014
                        || ((FieldAdaptValue) right).getContextValue().getType() instanceof ArrayType)) {
                    Constraint linear = new SubtypeConstraint(
                                left, ((FieldAdaptValue) right).getContextValue());
                    linear.addCause(c);
                    tmplist.add(linear);
                }
                for (Constraint gc : getGreaterConstraints(right)) {
                    AnnotatedValue r = gc.getRight();
                    if (!left.equals(r) && !(r instanceof MethodAdaptValue)) {
                        Constraint linear = new SubtypeConstraint(left, r);
                        linear.addCause(c);
                        linear.addCause(gc);
                        tmplist.add(linear);
                    }
                }
            }
            // Step 3: linear constraints
            else if (!(left instanceof MethodAdaptValue) && !(right instanceof MethodAdaptValue) 
                    && canConnectVia(left, right)) {
                for (Constraint lc : getLessConstraints(left)) {
                    AnnotatedValue r = lc.getLeft();
                    if (!r.equals(right) && isParamOrRetValue(r) && !(r instanceof MethodAdaptValue)) {
                        Constraint linear = new SubtypeConstraint(r, right);
                        linear.addCause(lc);
                        linear.addCause(c);
                        tmplist.add(linear);
                    }
                }
                if (isParamOrRetValue(left)) {
                    for (Constraint gc : getGreaterConstraints(right)) {
                        AnnotatedValue r = gc.getRight();
                        if (!left.equals(r) && !(r instanceof MethodAdaptValue)) {
                            Constraint linear = new SubtypeConstraint(left, r);
                            linear.addCause(c);
                            linear.addCause(gc);
                            tmplist.add(linear);
                        }
                    }
                }
                // step 4: z <: (y |> par) |> (y |> ret) |> x
                // if c is a new linear constraint between parameters
                // and returns, look for method adapt constraints
                if (isParamOrRetValue(left) && isParamOrRetValue(right)) {
                    // /return/param/this -> return/param/this
                    Set<AdaptValue> adaptSetLeft = declRefToAdaptValue.get(left.getIdentifier());
                    Set<AdaptValue> adaptSetRight = declRefToAdaptValue.get(right.getIdentifier());
                    if (adaptSetLeft != null && adaptSetRight != null) {
                        for (AdaptValue yPar : adaptSetLeft) {
                            for (AdaptValue yRet : adaptSetRight) {
                                if (yPar.getContextValue().getId() == yRet.getContextValue().getId()) {
                                    for (Constraint lc : getLessConstraints(yPar)) {
                                        for (Constraint gc : getGreaterConstraints(yRet)) {
                                            Constraint linear = new SubtypeConstraint(lc.getLeft(), gc.getRight());
                                            linear.addCause(lc);
                                            linear.addCause(c);
                                            linear.addCause(gc);
                                            tmplist.add(linear);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            for (Constraint linear : tmplist) {
                if (!existingCons.contains(linear)  && !newCons.contains(linear)
                        && canConnectVia(linear.getLeft(), linear.getRight())) {
                    newCons.add(linear);
                    buildRefToConstraintMapping(linear);
                    queue.add(linear);
                } 
            }
        }
        return newCons;
    }


    private void updateConstraintsWithReim(Set<Constraint> cons) {
        Set<Constraint> newCons = new LinkedHashSet<Constraint>();
        for (Constraint c : cons) {
            newCons.add(c);
            if (c instanceof SubtypeConstraint) {
                AnnotatedValue av = null;
                AnnotatedValue sub = c.getLeft();
                AnnotatedValue sup = c.getRight();
                if (!containsReadonly(sub) && !containsReadonly(sup)) {
                    // If this constraint is from library method, skip
                    // it. 
                    if (isParamOrRetValue(sub) && isParamOrRetValue(sup)
                            && st.isLibraryMethod((SootMethod) sub.getValue())
                            && st.isLibraryMethod((SootMethod) sup.getValue())
                            ) {
                        // skip
                    } else {
                        // need equality constraint: just add the reverse
                        Constraint reverse = new SubtypeConstraint(sup, sub);
                        newCons.add(reverse);
                    }
                }
            }
        }
        cons.clear();
        cons.addAll(newCons);
    }


    /**
     * Retore a Value to its initial annos
     */
    private boolean makeSatisfiable(Constraint c) {
        return makeSatisfiable(c, true);
    }


    private int restoreCounter = 0;

    /**
     * Retore a Value to its initial annos or to all
     */
    private boolean makeSatisfiable(Constraint c, boolean beenUpdated) {
        AnnotatedValue toUpdate = null;
        if (preferSink) {
            if (getAnnotations(c.getLeft()).contains(st.TAINTED) 
                    || (getAnnotations(c.getLeft()).contains(st.POLY)
                        && getAnnotations(c.getRight()).contains(st.SAFE)))
                toUpdate = c.getRight();
            else 
                toUpdate = c.getLeft();
        } else if (preferSource)  {
            if (getAnnotations(c.getRight()).contains(st.SAFE) 
                    || (getAnnotations(c.getRight()).contains(st.POLY)
                        && getAnnotations(c.getLeft()).contains(st.TAINTED)))
                toUpdate = c.getLeft();
            else 
                toUpdate = c.getRight();
        }
        boolean needSolve = false;
        if (toUpdate == null) 
            return needSolve;
        AnnotatedValue[] avs;
        if (toUpdate instanceof MethodAdaptValue) {
            avs = new AnnotatedValue[]{((AdaptValue) toUpdate).getContextValue(), 
                ((AdaptValue) toUpdate).getDeclValue()};
        } else if (toUpdate instanceof FieldAdaptValue) {
            // skip
            AnnotatedValue fieldAv = ((AdaptValue) toUpdate).getDeclValue();
            Set<Annotation> set = fieldAv.getAnnotations(st);
            if (preferSource && set.size() == 1 && set.contains(st.TAINTED)) 
                avs = new AnnotatedValue[]{fieldAv};
            else
                avs = new AnnotatedValue[]{((AdaptValue) toUpdate).getContextValue()};
//            System.out.println("Restoring " + c);
        } else 
            avs = new AnnotatedValue[]{toUpdate};
        
        for (AnnotatedValue av : avs) {
            int id = av.getId();
            if (beenUpdated) {
                // only restore values that have been updated before
                if (updated.get(id) && av.getRestoreNum() < 3) {
                    Set<Annotation> initAnnos = getInitAnnos(id);
//                    System.out.println("Restoring " + av + " to " + initAnnos);
                    // restore
                    av.setAnnotations(initAnnos, st);
                    needSolve = true;
                    restoreCounter++;
                    av.setRestored();
                }
            } else if (av.getKind() != Kind.CONSTANT) {
                // restore values that have never been updated
                // this essentially removes sources or sinks
                System.out.println("INFO: Eliminating a SOURCE/SINK: " + av);
                av.setAnnotations(st.getSourceLevelQualifiers(), st);
                // here we need to restore all constraints?
                for (AnnotatedValue v : AnnotatedValueMap.v().values()) {
                    int vid = v.getId();
                    if (updated.get(id)) {
                        Set<Annotation> initAnnos = getInitAnnos(id);
                        v.setAnnotations(initAnnos, st);
                        updated.flip(id);
                    }
                }
                needSolve = true;
            }
        }

        if (!needSolve)
            return false;

        // solve again
        boolean b = false;
        try {
            b = handleConstraint(c);
        } catch (Exception e) {
        }
        return b;
    }

    private void addToWorklist(Set<AnnotatedValue> avs) {
        for (AnnotatedValue av : avs) 
            addToWorklist(av);
    }

    private void addToWorklist(AnnotatedValue av) {
        worklist.addAll(getLessConstraints(av));
        worklist.addAll(getGreaterConstraints(av));
        Set<AdaptValue> set = declRefToAdaptValue.get(av.getIdentifier());
        if (set != null) {
            Set<AnnotatedValue> avs = new HashSet<AnnotatedValue>();
            for (AdaptValue a : set)
                avs.add(a);
            addToWorklist(avs);
        }
        set = contextRefToAdaptValue.get(av.getIdentifier());
        if (set != null) {
            Set<AnnotatedValue> avs = new HashSet<AnnotatedValue>();
            for (AdaptValue a : set)
                avs.add(a);
            addToWorklist(avs);
        }
    }

    @Override
	protected boolean setAnnotations(AnnotatedValue av, Set<Annotation> annos) 
			throws SolverException {
        Set<Annotation> oldAnnos = av.getAnnotations(st);
		if (av instanceof AdaptValue)
			return setAnnotations((AdaptValue) av, annos);
		if (oldAnnos.equals(annos))
			return false;
        if (av.getKind() == Kind.CONSTANT 
                || av.getIdentifier().startsWith(InferenceTransformer.CALLSITE_PREFIX)
                || av.getIdentifier().startsWith(InferenceTransformer.FAKE_PREFIX))
            return false;

        if (!updated.get(av.getId())) {
            // first update, remember the initial annos
            updated.set(av.getId());
            setInitAnnos(av);
        }

        // Add related constraints to worklist
        addToWorklist(av);

        return super.setAnnotations(av, annos);
    }

    @Override
    protected Set<Constraint> solveImpl() {
        System.out.println("preferSource = " + preferSource);
        System.out.println("preferSink = " + preferSink);

        System.out.println("INFO: Sources: " + st.getSourceNum());
        System.out.println("INFO: Sinks: " + st.getSinkNum());

        Set<Constraint> constraints = st.getConstraints();
        // try using reim
        updateConstraintsWithReim(constraints);

        buildRefToConstraintMapping(constraints);

        worklist.addAll(constraints);

		Set<Constraint> warnConstraints = new HashSet<Constraint>();
		Set<Constraint> conflictConstraints = new LinkedHashSet<Constraint>();
		boolean hasUpdate = false;

        while (!worklist.isEmpty()) {
            Iterator<Constraint> it = worklist.iterator();
            Constraint c = it.next();
            it.remove();
            try {
                hasUpdate = handleConstraint(c) || hasUpdate;
                Set<Constraint> newCons = addLinearConstraints(c, constraints);
                worklist.addAll(newCons);
                constraints.addAll(newCons);
            } catch (SolverException e) {
                FailureStatus fs = t.getFailureStatus(c);
                if (fs == FailureStatus.ERROR) {
                    if (preferSource || preferSink) {
                        if (!makeSatisfiable(c) && constraints.contains(c))
                            conflictConstraints.add(c);
                    }
                    else if (constraints.contains(c))
                        conflictConstraints.add(c);
                } else if (fs == FailureStatus.WARN) {
                    if (!warnConstraints.contains(c)) {
                        System.out.println("WARN: handling constraint " + c + " failed.");
                        warnConstraints.add(c);
                    }
                }
            }
        }
        // Now make sure conflictConstraints are not satisfiable
        for (Iterator<Constraint> it = conflictConstraints.iterator(); it.hasNext(); ) {
            Constraint c = it.next();
            try {
                handleConstraint(c);
                it.remove();
            } catch (SolverException e) {
            }
        }
        System.out.println("Total restore number: " + restoreCounter);
        return conflictConstraints;
    }
}
