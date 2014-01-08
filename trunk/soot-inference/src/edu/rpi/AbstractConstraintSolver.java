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

public abstract class AbstractConstraintSolver implements ConstraintSolver {

    protected InferenceTransformer t;

    protected Constraint currentConstraint;

    public AbstractConstraintSolver(InferenceTransformer t) {
        this.t = t;
    }

    public Constraint getCurrentConstraint() {
        return currentConstraint;
    }

	protected boolean handleConstraint(Constraint c) throws SolverException {
		currentConstraint = c;
		boolean hasUpdate = false;
		if (c instanceof SubtypeConstraint) {
			hasUpdate = handleSubtypeConstraint((SubtypeConstraint) c);
		} else if (c instanceof EqualityConstraint) {
			hasUpdate = handleEqualityConstraint((EqualityConstraint) c);
		} else if (c instanceof UnequalityConstraint) {
			hasUpdate = handleInequalityConstraint((UnequalityConstraint) c);
		} 
        return hasUpdate;
	}

	/**
	 * Return true if there are updates
	 * @param c
	 * @return
	 * @throws SolverException
	 */
	protected boolean handleSubtypeConstraint(SubtypeConstraint c) throws SolverException {
        AnnotatedValue sub = c.getLeft();
        AnnotatedValue sup = c.getRight();

		boolean hasUpdate = false;		
		
		// Get the annotations
		Set<Annotation> subAnnos = getAnnotations(sub);
		Set<Annotation> supAnnos = getAnnotations(sup);

		// First update the left: If a left annotation is not 
		// subtype of any right annotation, then remove it. 
		for (Iterator<Annotation> it = subAnnos.iterator(); 
				it.hasNext();) {
			Annotation subAnno = it.next();
			boolean isFeasible = false;
			for (Annotation supAnno : supAnnos) {
                if (AnnotationUtils.isSubtype(subAnno, supAnno)) {
					isFeasible = true;
					break;
				}
			}
			if (!isFeasible)
				it.remove();
		}
		
		// Now update the right: If a right annotation is not super type 
		// of any left annotation, remove it
		// We only do this if it is strict subtyping
		if (t.isStrictSubtyping()) {
			for (Iterator<Annotation> it = supAnnos.iterator(); 
					it.hasNext();) {
				Annotation supAnno = it.next();
				boolean isFeasible = false;
				for (Annotation subAnno : subAnnos) {
                    if (AnnotationUtils.isSubtype(subAnno, supAnno)) {
						isFeasible = true;
						break;
					}
				}
				if (!isFeasible)
					it.remove();
			}
		}

		if (subAnnos.isEmpty() || supAnnos.isEmpty())
            throw new SolverException("ERROR: solve " + c + " failed becaue of an empty set.");
		
        hasUpdate = setAnnotations(sub, subAnnos) || setAnnotations(sup, supAnnos) || hasUpdate;
		
		return hasUpdate;
    }

	protected boolean handleEqualityConstraint(EqualityConstraint c) throws SolverException {
		AnnotatedValue left = c.getLeft();
		AnnotatedValue right = c.getRight();
		
		// Get the annotations
		Set<Annotation> leftAnnos = getAnnotations(left);
		Set<Annotation> rightAnnos = getAnnotations(right);
		
		Set<Annotation> interAnnos = leftAnnos;
        interAnnos.retainAll(rightAnnos);
		
		if (interAnnos.isEmpty()) {
            throw new SolverException("ERROR: solve " + c + " failed becaue of an empty set.");
		}
		// update both
		return setAnnotations(left, interAnnos)
				|| setAnnotations(right, interAnnos);
    }

	protected boolean handleInequalityConstraint(UnequalityConstraint c) throws SolverException {
		AnnotatedValue left = c.getLeft();
		AnnotatedValue right = c.getRight();
		
		// Get the annotations
		Set<Annotation> leftAnnos = getAnnotations(left);
		Set<Annotation> rightAnnos = getAnnotations(right);
	
		// The default intersection of Set doesn't work well
		Set<Annotation> differAnnos = leftAnnos;
        differAnnos.removeAll(rightAnnos);
		
		if (differAnnos.isEmpty()) {
			throw new SolverException("ERROR: solve " + c 
					+ " failed becaue of an empty set.");
		}
		// Update the left
        return setAnnotations(left, differAnnos);
    }

	protected Set<Annotation> getAnnotations(AnnotatedValue av) {
		if (av instanceof AdaptValue) {
			AdaptValue aav = (AdaptValue) av;
			AnnotatedValue context = aav.getContextValue();
			AnnotatedValue decl = aav.getDeclValue();
			
			if (av instanceof FieldAdaptValue)
				return t.adaptFieldSet(context.getAnnotations(), 
						decl.getAnnotations());
			else
				return t.adaptMethodSet(context.getAnnotations(), 
						decl.getAnnotations());
		} else
			return av.getAnnotations();
	}

	/**
	 * Return true if there are updates
	 * @param av
	 * @param annos
	 * @return
	 * @throws SolverException
	 */
	protected boolean setAnnotations(AnnotatedValue av, Set<Annotation> annos)
			throws SolverException {
		if (av instanceof AdaptValue)
			return setAnnotations((AdaptValue) av, annos);
		if (av.getAnnotations().equals(annos))
			return false;
        av.setAnnotations(annos);
        return true;
    }

	protected boolean setAnnotations(AdaptValue aav, Set<Annotation> annos)
			throws SolverException {
        AnnotatedValue context = aav.getContextValue();
        AnnotatedValue decl = aav.getDeclValue();

		Set<Annotation> contextAnnos = context.getAnnotations();
		Set<Annotation> declAnnos = decl.getAnnotations();

		// First iterate through contextAnnos and remove infeasible annotations
		for (Iterator<Annotation> it = contextAnnos.iterator(); it.hasNext();) {
			Annotation contextAnno = it.next();
			boolean isFeasible = false;
			for (Annotation declAnno : declAnnos) {
				Annotation outAnno = null;
				if (aav instanceof FieldAdaptValue)
					outAnno = t.adaptField(contextAnno, declAnno);
				else
					outAnno = t.adaptMethod(contextAnno, declAnno);
				if (outAnno != null && annos.contains(outAnno)) {
					isFeasible = true;
					break;
				}
			}
			if (!isFeasible)
				it.remove();
		}
		
		if (contextAnnos.isEmpty())
			throw new SolverException("ERROR: Empty set for contextRef in AdaptConstraint");
		
		// Now iterate through declAnnos and remove infeasible annotations
		for (Iterator<Annotation> it = declAnnos.iterator(); it.hasNext();) {
			Annotation declAnno = it.next();
			boolean isFeasible = false;
			for (Annotation contextAnno : contextAnnos) {
				Annotation outAnno = null;
				if (aav instanceof FieldAdaptValue)
					outAnno = t.adaptField(contextAnno, declAnno);
				else
					outAnno = t.adaptMethod(contextAnno, declAnno);
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

    public abstract Set<Constraint> solve();
}
