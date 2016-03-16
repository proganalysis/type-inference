package edu.rpi;

import java.util.*;
import java.lang.annotation.*;

import edu.rpi.ConstraintSolver.FailureStatus;
import edu.rpi.ConstraintSolver.SolverException;
import edu.rpi.Constraint.SubtypeConstraint;
import edu.rpi.Constraint.EqualityConstraint;
import edu.rpi.Constraint.UnequalityConstraint;
import edu.rpi.AnnotatedValue.*;
import edu.rpi.*;

public class SetbasedSolver extends AbstractConstraintSolver {

    private Set<Constraint> worklist = new LinkedHashSet<Constraint>();

	private Map<String, Set<Constraint>> refToConstraints = new HashMap<String, Set<Constraint>>(); 

    public SetbasedSolver(InferenceTransformer t) {
        super(t);
    }

    public SetbasedSolver(InferenceTransformer t, boolean b) {
        super(t, b);
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
                List<AnnotatedValue> avs = new ArrayList<AnnotatedValue>(2);
                if (ref instanceof AdaptValue) {
                    AnnotatedValue decl = ((AdaptValue) ref).getDeclValue();
                    AnnotatedValue context = ((AdaptValue) ref).getContextValue();
                    avs.add(decl);
                    avs.add(context);
                } else
                    avs.add(ref);
                for (AnnotatedValue av : avs) {
                    Set<Constraint> set = refToConstraints.get(av.getIdentifier());
                    if (set == null) {
                        set = new LinkedHashSet<Constraint>();
                        refToConstraints.put(av.getIdentifier(), set);
                    }
                    set.add(c);
                }
            }
        }
    }
	
	private void buildRefToConstraintMapping(Set<Constraint> cons) {
		for (Constraint c : cons) {
            buildRefToConstraintMapping(c);
		}
	}

	protected boolean setAnnotations(AnnotatedValue av, Set<Annotation> annos) 
			throws SolverException {
        Set<Annotation> oldAnnos = av.getAnnotations(t);
		if (av instanceof AdaptValue)
			return setAnnotations((AdaptValue) av, annos);
		if (oldAnnos.equals(annos))
			return false;

        Set<Constraint> relatedConstraints = refToConstraints.get(av.getIdentifier());
        if (relatedConstraints != null) {
            worklist.addAll(relatedConstraints);
        }

        return super.setAnnotations(av, annos);
    }

    @Override
    protected Set<Constraint> solveImpl() {
        Set<Constraint> constraints = t.getConstraints();
		Set<Constraint> warnConstraints = new HashSet<Constraint>();
        buildRefToConstraintMapping(constraints);
        worklist.addAll(constraints);
        Set<Constraint> conflictConstraints = new LinkedHashSet<Constraint>();
        while(!worklist.isEmpty()) {
            Iterator<Constraint> it = worklist.iterator();
            Constraint c = it.next();
            it.remove();
            try {
                handleConstraint(c);
            } catch (SolverException e) {
                FailureStatus fs = t.getFailureStatus(c);
                if (fs == FailureStatus.ERROR) {
                    conflictConstraints.add(c);
                } else if (fs == FailureStatus.WARN) {
                    if (!warnConstraints.contains(c)) {
                        System.out.println("WARN: handling constraint " + c + " failed.");
                        warnConstraints.add(c);
                    }
                }
            }
        }
//        boolean hasUpdate = false;
//        do {
//            conflictConstraints = new LinkedHashSet<Constraint>();
//            hasUpdate = false;
//            for (Constraint c : constraints) {
//                try {
//                    hasUpdate = handleConstraint(c) || hasUpdate;
//                } catch (SolverException e) {
//                    FailureStatus fs = t.getFailureStatus(c);
//                    if (fs == FailureStatus.ERROR) {
//                        hasUpdate = false;
//                        conflictConstraints.add(c);
//                    } else if (fs == FailureStatus.WARN) {
//                        if (!warnConstraints.contains(c)) {
//                            System.out.println("WARN: handling constraint " + c + " failed.");
//                            warnConstraints.add(c);
//                        }
//                    }
//                }
//            }
//        } while (hasUpdate);
        refToConstraints.clear();
        return conflictConstraints;
    }
}


