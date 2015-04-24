/**
 * 
 */
package checkers.inference2.jcrypt2;

import static com.esotericsoftware.minlog.Log.warn;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;

import checkers.inference2.AbstractConstraintSolver;
import checkers.inference2.Constraint;
import checkers.inference2.Constraint.EqualityConstraint;
import checkers.inference2.Constraint.SubtypeConstraint;
import checkers.inference2.Constraint.UnequalityConstraint;
import checkers.inference2.Reference.AdaptReference;
import checkers.inference2.Reference.FieldAdaptReference;
import checkers.inference2.Reference;

/**
 * @author huangw5
 * 
 */
public class Jcrypt2ConstraintSolver extends AbstractConstraintSolver<Jcrypt2Checker> {

	private Jcrypt2Checker checker;

	protected Set<Constraint> worklist = new LinkedHashSet<Constraint>();

	protected Map<Integer, Set<Constraint>> refToConstraints = new HashMap<Integer, Set<Constraint>>();

	protected Map<String, List<Constraint>> adaptRefToConstraints; 

	protected Map<Integer, Set<Reference>> declRefToContextRefs; 
	
	public Jcrypt2ConstraintSolver(Jcrypt2Checker t) {
		super(t);
		this.checker = t;
		adaptRefToConstraints = new HashMap<String, List<Constraint>>();
		declRefToContextRefs = new HashMap<Integer, Set<Reference>>();
	}
	
	protected void buildRefToConstraintMapping(Constraint c) {
        Reference left = null, right = null; 
        if (c instanceof SubtypeConstraint
                || c instanceof EqualityConstraint
                || c instanceof UnequalityConstraint) {
            left = c.getLeft();
            right = c.getRight();
        }
        if (left != null && right != null) {
            Reference[] refs = {left, right};
            for (Reference ref : refs) {
                List<Reference> avs = new ArrayList<Reference>(2);
                if (ref instanceof AdaptReference) {
                    Reference decl = ((AdaptReference) ref).getDeclRef();
                    Reference context = ((AdaptReference) ref).getContextRef();
                    avs.add(decl);
                    avs.add(context);
                    
                    String key = ref.getName();
                    List<Constraint> l = adaptRefToConstraints.get(key);
                    if (l == null) {
                        l = new ArrayList<Constraint>(2);
                        adaptRefToConstraints.put(key, l);
                    }
                    l.add(c);
                    Set<Reference> contextSet = declRefToContextRefs.get(decl.getId());
                    if (contextSet == null) {
                        contextSet = new HashSet<Reference>();
                        declRefToContextRefs.put(decl.getId(), contextSet);
                    }
                    contextSet.add(((AdaptReference) ref).getContextRef());
                } else
                    avs.add(ref);
                for (Reference av : avs) {
                    Set<Constraint> set = refToConstraints.get(av.getId());
                    if (set == null) {
                        set = new LinkedHashSet<Constraint>();
                        refToConstraints.put(av.getId(), set);
                    }
                    set.add(c);
                }
            }
        }
        if ((c instanceof SubtypeConstraint)
                && !(left instanceof AdaptReference)
                && !(right instanceof AdaptReference)) {
            left.addGreaterConstraint(c);
            right.addLessConstraint(c);
        }
    }
	
	private void buildRefToConstraintMapping(Set<Constraint> cons) {
		for (Constraint c : cons) {
            buildRefToConstraintMapping(c);
		}
	}

	@Override
	protected boolean setAnnotations(Reference av, Set<AnnotationMirror> annos) 
			throws SolverException {
        Set<AnnotationMirror> oldAnnos = av.getAnnotations(checker);
		if (av instanceof AdaptReference)
			return setAnnotations((AdaptReference) av, annos);
		if (oldAnnos.equals(annos))
			return false;

        Set<Constraint> relatedConstraints = refToConstraints.get(av.getId());
        if (relatedConstraints != null) {
            worklist.addAll(relatedConstraints);
        }

        return super.setAnnotations(av, annos);
    }
	
	@Override
	protected boolean setAnnotations(AdaptReference aav, Set<AnnotationMirror> annos)
			throws SolverException {
        Reference context = aav.getContextRef();
        Reference decl = aav.getDeclRef();

		Set<AnnotationMirror> contextAnnos = context.getAnnotations(checker);
		Set<AnnotationMirror> declAnnos = decl.getAnnotations(checker);
		if (contextAnnos.isEmpty()) return setAnnotations(decl, annos);
		
		// First iterate through contextAnnos and remove infeasible annotations
		for (Iterator<AnnotationMirror> it = contextAnnos.iterator(); it.hasNext();) {
			AnnotationMirror contextAnno = it.next();
			boolean isFeasible = false;
			for (AnnotationMirror declAnno : declAnnos) {
				AnnotationMirror outAnno = null;
				if (aav instanceof FieldAdaptReference) {
					outAnno = checker.adaptField(contextAnno, declAnno);
				} else {
					outAnno = checker.adaptMethod(contextAnno, declAnno);
				}
				if (outAnno != null && annos.contains(outAnno)) {
					isFeasible = true;
					break;
				}
			}
			if (!isFeasible)
				it.remove();
		}
		
		if (contextAnnos.isEmpty()) {
			throw new SolverException("ERROR: Empty set for contextRef in AdaptConstraint");
		}
		
		// Now iterate through declAnnos and remove infeasible annotations
		for (Iterator<AnnotationMirror> it = declAnnos.iterator(); it.hasNext();) {
			AnnotationMirror declAnno = it.next();
			boolean isFeasible = false;
			for (AnnotationMirror contextAnno : contextAnnos) {
				AnnotationMirror outAnno = null;
				if (aav instanceof FieldAdaptReference) {
					outAnno = checker.adaptField(contextAnno, declAnno);
				} else {
					outAnno = checker.adaptMethod(contextAnno, declAnno);
				}
				if (outAnno != null && annos.contains(outAnno)) {
					isFeasible = true;
					break;
				}
			}
			if (!isFeasible)
				it.remove();
		}
		
		if (declAnnos.isEmpty())
			throw new SolverException("ERROR: Empty set for declRef in AdaptConstraint");
		
		return setAnnotations(context, contextAnnos)
				|| setAnnotations(decl, declAnnos);
	}


	@Override
	public Set<Constraint> solveImpl() {
        Set<Constraint> constraints = checker.getConstraints();
		BitSet warnConstraints = new BitSet(Constraint.maxId());
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
                FailureStatus fs = checker.getFailureStatus(c);
                if (fs == FailureStatus.ERROR) {
                    conflictConstraints.add(c);
                } else if (fs == FailureStatus.WARN) {
                    if (!warnConstraints.get(c.getId())) {
                        warn(this.getClass().getSimpleName(), "Failed handling constraint " + c);
                        warnConstraints.set(c.getId());
                    }
                }
            }
        }
        return conflictConstraints;
	}


	@Override
	protected boolean handleSubtypeConstraint(SubtypeConstraint c) throws SolverException {
        Reference sub = c.getLeft();
        Reference sup = c.getRight();

		boolean hasUpdate = false;		
		
		// Get the annotations
		Set<AnnotationMirror> subAnnos = getAnnotations(sub);
		Set<AnnotationMirror> supAnnos = getAnnotations(sup);
		
		if (subAnnos.isEmpty() || supAnnos.isEmpty()) return false;

		// First update the left: If a left annotation is not 
		// subtype of any right annotation, then remove it. 
		for (Iterator<AnnotationMirror> it = subAnnos.iterator(); 
				it.hasNext();) {
			AnnotationMirror subAnno = it.next();
			boolean isFeasible = false;
			for (AnnotationMirror supAnno : supAnnos) {
                if (checker.getQualifierHierarchy().isSubtype(subAnno, supAnno)) {
					isFeasible = true;
					break;
				}
			}
			if (!isFeasible) {
				it.remove();
			}
		}
		
		// Now update the right: If a right annotation is not super type 
		// of any left annotation, remove it
		// We only do this if it is strict subtyping
		if (checker.isStrictSubtyping() && checker.getFailureStatus(c) != FailureStatus.WARN) {
			for (Iterator<AnnotationMirror> it = supAnnos.iterator(); 
					it.hasNext();) {
				AnnotationMirror supAnno = it.next();
				boolean isFeasible = false;
				for (AnnotationMirror subAnno : subAnnos) {
                    if (checker.getQualifierHierarchy().isSubtype(subAnno, supAnno)) {
						isFeasible = true;
						break;
					}
				}
				if (!isFeasible) {
					it.remove();
				}
			}
		}

		if (subAnnos.isEmpty() || supAnnos.isEmpty())
            throw new SolverException("ERROR: solve " + c + " failed becaue of an empty set.");
		
        hasUpdate = setAnnotations(sub, subAnnos) || setAnnotations(sup, supAnnos) || hasUpdate;
		
		return hasUpdate;
    }

}
