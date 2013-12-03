/**
 * 
 */
package checkers.inference.sflow;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingDeque;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;

import checkers.inference.Constraint.EqualityConstraint;
import checkers.inference.Constraint.SubtypeConstraint;
import checkers.inference.Constraint.UnequalityConstraint;
import checkers.inference.Reference;
import checkers.inference.Reference.AdaptReference;
import checkers.inference.Reference.ArrayReference;
import checkers.inference.SetbasedSolver.SetbasedSolverException;
import checkers.inference.sflow.SFlowChecker;
import checkers.inference.sflow.WorklistSetbasedSolver;
import checkers.inference.*;
import checkers.types.AnnotatedTypeMirror;
import checkers.util.ElementUtils;
import checkers.util.*;

/**
 * @author huangw5
 *
 */
public class SFlowTypingExtractor implements TypingExtractor {
	
	private SFlowChecker inferenceChecker;
	
	private List<Reference> exprRefs;
	
	private Map<String, Reference> maximalSolution = null;
	
	private List<Constraint> constraints = null;
	
	private Map<Integer, List<Constraint>> refToConstraints; 
	
	public SFlowTypingExtractor(SFlowChecker inferenceChecker,
			List<Reference> exprRefs, List<Constraint> constraints) {
		super();
		this.inferenceChecker = inferenceChecker;
		this.exprRefs = exprRefs;
		this.constraints = constraints;
		this.refToConstraints = new HashMap<Integer, List<Constraint>>();
	}


    /**
     * Copied from WorklistSetbasedSolver
     */
    private boolean isParamReturnConstraint(Constraint c) {
        Reference left = c.getLeft();
        Reference right = c.getRight();
        if (left != null && !(left instanceof AdaptReference) 
                && right != null && !(right instanceof AdaptReference)) {
            // param/this -> return/param/this
            Element lElt = null;
            Element rElt = null;
             if ((lElt = left.getElement()) != null 
                        && (lElt.getKind() == ElementKind.PARAMETER 
                            || left.getRefName().startsWith("RET_")
                            || left.getRefName().startsWith("THIS_"))
                     && (rElt = right.getElement()) != null 
                        && (right.getRefName().startsWith("RET_")
                            || right.getRefName().startsWith("THIS_")
                            || rElt.getKind() == ElementKind.PARAMETER)) {
                 // check if they are from the same method
                 while (lElt != null && lElt.getKind() != ElementKind.METHOD 
                         && lElt.getKind() != ElementKind.CONSTRUCTOR)
                     lElt = lElt.getEnclosingElement();
                 while (rElt != null && rElt.getKind() != ElementKind.METHOD 
                         && rElt.getKind() != ElementKind.CONSTRUCTOR)
                     rElt = rElt.getEnclosingElement();
                 if (lElt.equals(rElt))
                     return true;
            }
        }
        return false;
    }

    private void setPolyLibraryMethods(List<Reference> refs, List<Constraint> cons, Map<String, Reference> solution) {
		Set<AnnotationMirror> sflowSet = AnnotationUtils.createAnnotationSet();
		sflowSet.add(SFlowChecker.SECRET);
		sflowSet.add(SFlowChecker.POLY);
		sflowSet.add(SFlowChecker.TAINTED);
        for (Constraint c : cons) {
            Reference left = c.getLeft();
            Reference right = c.getRight();
            if (isParamReturnConstraint(c)) {
                Set<AnnotationMirror> leftAnnos = left.getAnnotations(); 
                Set<AnnotationMirror> rightAnnos = right.getAnnotations();
                Set<AnnotationMirror> inter = InferenceUtils
                            .intersectAnnotations(leftAnnos, rightAnnos);
                if (inter.contains(SFlowChecker.POLY)) {
                    Set<AnnotationMirror> set = AnnotationUtils.createAnnotationSet();
                    set.add(SFlowChecker.POLY);
                    left.setAnnotations(set);
                    right.setAnnotations(set);
                } else if (InferenceUtils.intersectAnnotations(sflowSet, leftAnnos).size() == 3) {
                    // left is unconstrained.
                    Set<AnnotationMirror> set = AnnotationUtils.createAnnotationSet();
                    if (left.getRefName().startsWith("RET_"))
                        set.add(SFlowChecker.BOTTOM);
                    else
                        set.add(SFlowChecker.TOP);
                    left.setAnnotations(set);
                } else if (InferenceUtils.intersectAnnotations(sflowSet, rightAnnos).size() == 3) {
                    // right is unconstrained.
                    Set<AnnotationMirror> set = AnnotationUtils.createAnnotationSet();
                    if (right.getRefName().startsWith("RET_"))
                        set.add(SFlowChecker.BOTTOM);
                    else
                        set.add(SFlowChecker.TOP);
                    right.setAnnotations(set);
                }
                if (InferenceChecker.DEBUG) {
                    Element elt = left.getElement();
                    while (elt != null && elt.getKind() != ElementKind.METHOD
                            && elt.getKind() != ElementKind.CONSTRUCTOR) {
                        elt = elt.getEnclosingElement();
                    }
                    System.out.println("INFO: set " + left.toAnnotatedString() 
                            + " and " + right.toAnnotatedString() + " for "
                            + elt);
                }
            }
        }
        for (Reference ref : refs) {
            Element elt = null;
            Set<AnnotationMirror> annos = ref.getAnnotations(); 
            if ((elt = ref.getElement()) != null 
                && InferenceUtils.intersectAnnotations(sflowSet, annos).size() == 3
                && (elt.getKind() == ElementKind.PARAMETER 
                    || ref.getRefName().startsWith("RET_")
                    || ref.getRefName().startsWith("THIS_"))) {
                Set<AnnotationMirror> set = AnnotationUtils.createAnnotationSet();
                if (ref.getRefName().startsWith("RET_"))
                    set.add(SFlowChecker.BOTTOM);
                else
                    set.add(SFlowChecker.TOP);
                ref.setAnnotations(set);
                if (InferenceChecker.DEBUG) {
                    System.out.println("INFO: set " + ref + " from " + annos + " to " + set);
                }
            }
        }
    }
	
	/**
	 * Build the maximal solution
	 */
	@Override
	public List<Constraint> extractConcreteTyping(int typeErrorNum) {
		// Check whether we need to resolve conflicts
		List<Constraint> typeErrors = Collections.emptyList();

		maximalSolution = new HashMap<String, Reference>();

        if (inferenceChecker.isInferLibrary()) {
            setPolyLibraryMethods(exprRefs, constraints, maximalSolution);
            ConstraintSolver solver = new WorklistSetbasedSolver(inferenceChecker, exprRefs, constraints);
            typeErrors = solver.solve();
            if (!typeErrors.isEmpty()) {
                for (Constraint c : typeErrors)
                    System.out.println(c);
                System.out.println("There are " + typeErrors.size() + " type errors after setting Poly");
                return typeErrors;
            }
        }
		
        List<Reference> copyRefs = copyReferences(exprRefs);
		for (Reference ref : exprRefs) {
			String identifier = ref.getIdentifier();
			if (identifier != null) {
				Reference maxRef = null;
                maxRef = inferenceChecker.getMaximal(ref);
				maximalSolution.put(identifier, maxRef);
                ref.setAnnotations(maxRef.getAnnotations());
			}
		}

        if (typeErrorNum == 0) {
            // Check if maximal typing type-checks
            boolean prev = InferenceChecker.DEBUG;
            InferenceChecker.DEBUG = false;
            SetbasedSolver solver = new SetbasedSolver(inferenceChecker,
                    exprRefs, constraints);
            List<Constraint> conflictConstraints = solver.solve();
            InferenceChecker.DEBUG = prev;
            if (!conflictConstraints.isEmpty()) {
                recoverReferences(exprRefs, copyRefs);
                for (Constraint c : conflictConstraints)
                    System.out.println(c);
                System.out.println("There are " + conflictConstraints.size()
                        + " conflicts:");
            } else 
                System.out.println("No conflicts!");
        }
		return typeErrors;
	}

	/* (non-Javadoc)
	 * @see checkers.inference.TypingExtractor#printAllVariables(java.io.PrintWriter)
	 */
	@Override
	public void printAllVariables(PrintWriter out) {
		inferenceChecker.printResult(maximalSolution, out);
	}


	/* (non-Javadoc)
	 * @see checkers.inference.TypingExtractor#annotateInferredType(java.lang.String, checkers.types.AnnotatedTypeMirror)
	 */
	@Override
	public void annotateInferredType(String identifier, AnnotatedTypeMirror type) {
		if (maximalSolution == null)
			throw new RuntimeException("Should have called buildMaximalSolution()"
					+ " before retrieving the inferrred type!");
		Reference ref = maximalSolution.get(identifier);
		if (ref != null) {
//			if (identifier.equals("Simple"))
//				System.out.println();
			InferenceUtils.annotateReferenceType(type, ref);
		}
	}
	
	@Override
	public void addInferredType(String identifier, AnnotatedTypeMirror type) {
		if (maximalSolution == null)
			throw new RuntimeException("Should have called buildMaximalSolution()"
					+ " before retrieving the inferrred type!");
		Reference ref = maximalSolution.get(identifier);
		if (ref != null) {
			InferenceUtils.annotateReferenceType(type, ref, false);
		}
	}

	/* (non-Javadoc)
	 * @see checkers.inference.TypingExtractor#getInferredReference(java.lang.String)
	 */
	@Override
	public Reference getInferredReference(String identifier) {
		if (maximalSolution == null)
			throw new RuntimeException("Should have called buildMaximalSolution()"
					+ " before retrieving the inferrred type!");
		return maximalSolution.get(identifier);
	}
	

	/* (non-Javadoc)
	 * @see checkers.inference.TypingExtractor#getInferredReferences()
	 */
	@Override
	public List<Reference> getInferredReferences() {
		if (maximalSolution == null)
			throw new RuntimeException("Should have called buildMaximalSolution()"
					+ " before retrieving the inferrred type!");
		return new ArrayList<Reference>(maximalSolution.values());
	}
	
	private List<Reference> copyReferences(List<Reference> exprRefs) {
		List<Reference> copy = new ArrayList<Reference>(exprRefs.size());
		for (Reference ref : exprRefs) {
			copy.add(ref.getCopy());
		}
		return copy;
	}
	
	private void recoverReferences(List<Reference> exprRefs, List<Reference> copyRefs) {
		for (Iterator<Reference> it1 = exprRefs.iterator(); it1.hasNext();) {
			for (Iterator<Reference> it2 = copyRefs.iterator(); it2.hasNext();) {
				Reference ref = it1.next();
				Reference copy = it2.next();
				ref.setAnnotations(copy.getAnnotations());
				if (ref.getId() != copy.getId())
					throw new RuntimeException("Different ID!");
			}
		}
	}


	@Override
	public Map<String, Reference> getInferredSolution() {
		return maximalSolution;
	}

}
