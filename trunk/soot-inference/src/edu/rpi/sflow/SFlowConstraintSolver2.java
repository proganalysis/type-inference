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


public class SFlowConstraintSolver2 extends AbstractConstraintSolver {

    protected SFlowTransformer st;

    protected Map<String, AnnotatedValue> reimValues;

    protected Annotation READONLY = AnnotationUtils.fromClass(Readonly.class);

	private Map<AnnotatedValue, Set<AnnotatedValue>> declRefToContextRefs = new HashMap<AnnotatedValue, Set<AnnotatedValue>>(); 

	private Map<AnnotatedValue, Set<Constraint>> lessValues = new HashMap<AnnotatedValue, Set<Constraint>>(); 

	private Map<AnnotatedValue, Set<Constraint>> greaterValues = new HashMap<AnnotatedValue, Set<Constraint>>(); 

    private boolean preferSource = true;

    private boolean isInteractive = false;

    private BitSet updated = new BitSet(AnnotatedValue.maxId());

    private byte[] initAnnos = new byte[AnnotatedValue.maxId()];

    private final byte TAINTED_MASK = 0x01;
    private final byte POLY_MASK = 0x02;
    private final byte SAFE_MASK = 0x04;
    private final byte BOTTOM_MASK = 0x08;

    public SFlowConstraintSolver2(InferenceTransformer t) {
        super(t);
        if (!(t instanceof SFlowTransformer)) 
            throw new RuntimeException("SFlowConstraintSolver2 expects SFlowTransformer");
        this.st = (SFlowTransformer) t;
        this.preferSource = !(System.getProperty("preferSink") != null);
        this.isInteractive = (System.getProperty("interactive") != null);

    }

    public SFlowConstraintSolver2(InferenceTransformer t, Map<String, AnnotatedValue> reimValues) {
        this(t);
        this.reimValues = reimValues;
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
        initAnnos[id] = toBits(av.getAnnotations());
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

        AnnotatedValue reimValue = reimValues.get(av.getIdentifier());
        if (reimValue == null || !reimValue.containsAnno(READONLY))
            return false;
        return true;
    }

    private void updateConstraintsWithReim(Map<SootMethod, Set<Constraint>> consByMethod) {
        for (Map.Entry<SootMethod, Set<Constraint>> entry : consByMethod.entrySet()) {
            updateConstraintsWithReim(entry.getValue());
        }
    }

    private Set<Constraint> getLessConstraints(AnnotatedValue av) {
        Set<Constraint> set = lessValues.get(av);
        if (set == null)
            return Collections.<Constraint>emptySet();
        else 
            return set;
    }

    private void addLessConstraint(AnnotatedValue av, Constraint less) {
        Set<Constraint> set = lessValues.get(av);
        if (set == null) {
            set = new HashSet<Constraint>();
            lessValues.put(av, set);
        }
        set.add(less);
    }

    private Set<Constraint> getGreaterConstraints(AnnotatedValue av) {
        Set<Constraint> set = greaterValues.get(av);
        if (set == null) 
            return Collections.<Constraint>emptySet();
        else 
            return set;
    }

    private void addGreaterConstraint(AnnotatedValue av, Constraint greater) {
        Set<Constraint> set = greaterValues.get(av);
        if (set == null) {
            set = new HashSet<Constraint>();
            greaterValues.put(av, set);
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
                List<AnnotatedValue> avs = new ArrayList<AnnotatedValue>(3);
                avs.add(ref);
                if (ref instanceof AdaptValue) {
                    // Add decl and context
                    AnnotatedValue decl = ((AdaptValue) ref).getDeclValue();
                    AnnotatedValue context = ((AdaptValue) ref).getContextValue();
                    avs.add(decl);
                    avs.add(context);
                    Set<AnnotatedValue> contextSet = declRefToContextRefs.get(decl);
                    if (contextSet == null) {
                        contextSet = new HashSet<AnnotatedValue>();
                        declRefToContextRefs.put(decl, contextSet);
                    }
                    contextSet.add(context);
                }
            }
            if (c instanceof SubtypeConstraint) {
                addGreaterConstraint(left, c);
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
                    &&(ref.getAnnotations().size() == 1 && ref.getAnnotations().contains(st.POLY)
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
                        linear.addCause(c);
                        linear.addCause(lc);
                        tmplist.add(linear);
                    }
                }
            }
            // Step 2: field write
            // Nov 26, 2013: add linear constraint for array fields
            else if ((right instanceof FieldAdaptValue) ) {
                if ((ref = ((FieldAdaptValue) right).getDeclValue()) != null
                    && (ref.getAnnotations().size() == 1 && ref.getAnnotations().contains(st.POLY)
                        || !preferSource
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
                        linear.addCause(c);
                        linear.addCause(lc);
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
                    Set<AnnotatedValue> contextSetLeft = declRefToContextRefs.get(left);
                    Set<AnnotatedValue> contextSetRight = declRefToContextRefs.get(right);
                    if (contextSetLeft != null && contextSetRight != null) {
                        contextSetLeft.retainAll(contextSetRight);
                        for (AnnotatedValue y : contextSetLeft) {
                            AnnotatedValue yPar = st.getAnnotatedValues().get(y.getIdentifier() + left.getIdentifier());
                            AnnotatedValue yRet = st.getAnnotatedValues().get(y.getIdentifier() + right.getIdentifier());
                            if (yPar != null && yRet != null) {
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
        if (reimValues == null)
            return;
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
                        if (sup.getKind() == Kind.LITERAL) 
                            System.out.println();
                        newCons.add(reverse);
                    }
                }
            }
        }
        cons.clear();
        cons.addAll(newCons);
        reimValues.clear();
    }


    /**
     * Retore a Value to its initial annos
     */
    private boolean makeSatisfiable(Constraint c) {
        return makeSatisfiable(c, true);
    }

    /**
     * Retore a Value to its initial annos or to all
     */
    private boolean makeSatisfiable(Constraint c, boolean beenUpdated) {
        AnnotatedValue toUpdate;
        if (preferSource) {
            if (getAnnotations(c.getLeft()).contains(st.TAINTED) 
                    || getAnnotations(c.getLeft()).contains(st.POLY))
                toUpdate = c.getRight();
            else 
                toUpdate = c.getLeft();
        } else {
            if (getAnnotations(c.getRight()).contains(st.SAFE) 
                    || getAnnotations(c.getRight()).contains(st.POLY))
                toUpdate = c.getLeft();
            else 
                toUpdate = c.getRight();
        }
        boolean needSolve = false;
        AnnotatedValue[] avs;
        if (toUpdate instanceof AdaptValue) {
            avs = new AnnotatedValue[]{((AdaptValue) toUpdate).getContextValue(), 
                ((AdaptValue) toUpdate).getDeclValue()};
        } else 
            avs = new AnnotatedValue[]{toUpdate};
        
        for (AnnotatedValue av : avs) {
            int id = av.getId();
            if (beenUpdated) {
                // only restore values that have been updated before
                if (updated.get(id)) {
                    Set<Annotation> initAnnos = getInitAnnos(id);
                    System.out.println("Restoring " + av + " to " + initAnnos);
                    // restore
                    av.setAnnotations(initAnnos);
                    needSolve = true;
                }
            } else if (av.getKind() != Kind.CONSTANT) {
                // restore values that have never been updated
                // this essentially removes sources or sinks
                System.out.println("INFO: Eliminating a SOURCE/SINK: " + av);
                av.setAnnotations(st.getSourceLevelQualifiers());
                // here we need to restore all constraints?
                for (AnnotatedValue v : st.getAnnotatedValues().values()) {
                    int vid = v.getId();
                    if (updated.get(id)) {
                        Set<Annotation> initAnnos = getInitAnnos(id);
                        v.setAnnotations(initAnnos);
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

    @Override
	protected boolean setAnnotations(AnnotatedValue av, Set<Annotation> annos) 
			throws SolverException {
        Set<Annotation> oldAnnos = av.getAnnotations();
		if (av instanceof AdaptValue)
			return setAnnotations((AdaptValue) av, annos);
		if (oldAnnos.equals(annos))
			return false;
        if (av.getKind() == Kind.CONSTANT)
            return false;

//        if (preferSource && !annos.contains(st.TAINTED)) {
//            return false;
//        } else if (!preferSource && !annos.contains(st.SAFE))
//            return false;

        if (!updated.get(av.getId())) {
            // first update, remember the initial annos
            updated.set(av.getId());
            setInitAnnos(av);
        }
        return super.setAnnotations(av, annos);
    }


    @Override
    protected Set<Constraint> solveImpl() {
        Set<Constraint> constraints = st.getConstraints();
        // try using reim
        updateConstraintsWithReim(constraints);
        Set<Constraint> extendedConstraints = new LinkedHashSet<Constraint>(constraints);
        buildRefToConstraintMapping(extendedConstraints);

		Set<Constraint> warnConstraints = new HashSet<Constraint>();
		Set<Constraint> conflictConstraints = new LinkedHashSet<Constraint>();
		boolean hasUpdate = false;
		do {
			conflictConstraints = new LinkedHashSet<Constraint>();
			hasUpdate = false;
            Set<Constraint> newCons = new LinkedHashSet<Constraint>();
			for (Constraint c : extendedConstraints) {
				try {
					hasUpdate = handleConstraint(c) || hasUpdate;
                    newCons.addAll(addLinearConstraints(c, extendedConstraints));
				} catch (SolverException e) {
					FailureStatus fs = t.getFailureStatus(c);
					if (fs == FailureStatus.ERROR) {
                        hasUpdate = makeSatisfiable(c);
                        if (!hasUpdate && extendedConstraints.contains(c))
                            conflictConstraints.add(c);
					} else if (fs == FailureStatus.WARN) {
						if (!warnConstraints.contains(c)) {
							System.out.println("WARN: handling constraint " + c + " failed.");
							warnConstraints.add(c);
						}
					}
				}
			}
            if (!newCons.isEmpty()) {
                extendedConstraints.addAll(newCons);
                hasUpdate = true;
            }

            if (!hasUpdate && isInteractive) {
                // Interactive mode: allow user to eliminate sources or sinks
                List<Constraint> conflictList = new ArrayList<Constraint>(conflictConstraints.size());
                conflictList.addAll(conflictConstraints);
                System.out.println("\n-----Enter interactive mode------\n");
                int i = 1;
                for (Constraint cc : conflictList) {
                    System.out.println(i + ": " + cc + "\n");
                    i++;
                }
                System.out.print("Please select the constraint you want to satisfy (Press <Enter> to quit): ");
                BufferedReader br = null;
                try {
                    br = new BufferedReader(new InputStreamReader(System.in));
                    String line = br.readLine();
                    int index = Integer.parseInt(line);
                    Constraint cc = conflictList.get(index - 1);
                    hasUpdate = makeSatisfiable(cc, false);
                    if (!hasUpdate) {
                        System.out.println("INFO: No more type errors\n");
                    }
                } catch (Exception e) {
                    System.out.println("ERROR: invalid input: " + e.getMessage());
                } finally {
                    System.out.println("\n-----Exit interactive mode------\n");
                }
            }
		} while (hasUpdate);
        System.out.println("Added " + (extendedConstraints.size() - constraints.size()) + " linear constraints");
        constraints.clear();
        constraints.addAll(extendedConstraints);

        return conflictConstraints;
    }
}
