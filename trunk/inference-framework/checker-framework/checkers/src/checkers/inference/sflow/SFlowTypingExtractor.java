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
import javax.lang.model.element.Modifier;

import checkers.inference.Constraint.EqualityConstraint;
import checkers.inference.Constraint.SubtypeConstraint;
import checkers.inference.Constraint.UnequalityConstraint;
import checkers.inference.Reference;
import checkers.inference.Reference.AdaptReference;
import checkers.inference.Reference.ArrayReference;
import checkers.inference.Reference.MethodAdaptReference;
import checkers.inference.Reference.ConstantReference;
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


    private List<Constraint> handleLibraryInference(List<Reference> refs, List<Constraint> cons, Map<String, Reference> solution) {
		Set<AnnotationMirror> sflowSet = AnnotationUtils.createAnnotationSet();
		sflowSet.add(SFlowChecker.TAINTED);
		sflowSet.add(SFlowChecker.POLY);
		sflowSet.add(SFlowChecker.SAFE);
        for (Constraint c : cons) {
            Reference left = c.getLeft();
            Reference right = c.getRight();
            if (inferenceChecker.isParamReturnConstraint(c)) {
                // Get the method element
                Element methodElt = inferenceChecker.getEnclosingMethod(left.getElement());
                // skip non-public methods
                if (!methodElt.getModifiers().contains(Modifier.PUBLIC))
                    continue;
                Set<AnnotationMirror> leftAnnos = left.getAnnotations(); 
                Set<AnnotationMirror> rightAnnos = right.getAnnotations();
                Set<AnnotationMirror> inter = InferenceUtils
                            .intersectAnnotations(leftAnnos, rightAnnos);
                if (inter.contains(SFlowChecker.POLY)) {
                    Set<AnnotationMirror> set = AnnotationUtils.createAnnotationSet();
                    set.add(SFlowChecker.POLY);
                    left.setAnnotations(set);
                    right.setAnnotations(set);
//                    System.out.println("INFO: set " + left  + " from " + leftAnnos + "  and " 
//                            + right + " from " + rightAnnos + " constraint:" + c);
                } 
            }
        }
		List<Constraint> typeErrors = Collections.emptyList();
        // Step 2: Now propagate Poly->Poly
        WorklistSetbasedSolver solver = new WorklistSetbasedSolver(inferenceChecker, exprRefs, constraints);
        solver.setComputeLinearConstraints(false);
        typeErrors = solver.solve();
        if (!typeErrors.isEmpty()) {
            for (Constraint c : typeErrors)
                System.out.println(c);
            System.out.println("There are " + typeErrors.size() + " type errors after setting Poly");
            return typeErrors;
        }
        // At public methods, and readonly return whose set-based
        // solution is {Tainted,Poly,Safe}, type those returns Safe. 
        for (Reference ref : refs) {
            Element elt = ref.getElement();
            // Get the method element
            Element methodElt = inferenceChecker.getEnclosingMethod(elt);
            // skip non-public methods
            if (methodElt == null || !methodElt.getModifiers().contains(Modifier.PUBLIC))
                continue;
            Set<AnnotationMirror> annos = ref.getAnnotations(); 
            if (ref.getRefName().startsWith("RET_")
                    && inferenceChecker.isReadonlyType(ref.getType())
                    && InferenceUtils.intersectAnnotations(sflowSet, annos).size() == 3) {
                Set<AnnotationMirror> set = AnnotationUtils.createAnnotationSet();
                set.add(SFlowChecker.SAFE);
                ref.setAnnotations(set);
//                if (InferenceChecker.DEBUG) {
//                    System.out.println("INFO: set " + ref + " from " + annos + " to " + set + " type: " + ref.getType());
//                }
            }
        }
        constraints.clear();
        constraints.addAll(solver.getUpdatedConstraints());
        System.out.println("constraint size: " + constraints.size());
        solver = new WorklistSetbasedSolver(inferenceChecker, exprRefs, constraints);
        solver.setComputeLinearConstraints(false);
        typeErrors = solver.solve();
        if (!typeErrors.isEmpty()) {
            for (Constraint c : typeErrors)
                System.out.println(c);
            System.out.println("There are " + typeErrors.size() + " type errors after setting readonly RET");
            return typeErrors;
        }


        // At public methods, and readonly parameters whose set-based
        // solution is {Tainted,Poly,Safe}, type those parameters Tainted. 
        for (Reference ref : refs) {
            Element elt = ref.getElement();
            // Get the method element
            Element methodElt = inferenceChecker.getEnclosingMethod(elt);
            // skip non-public methods
            if (methodElt == null || !methodElt.getModifiers().contains(Modifier.PUBLIC))
                continue;
            Set<AnnotationMirror> annos = ref.getAnnotations(); 
            if (elt.getKind() == ElementKind.PARAMETER
                    && InferenceUtils.intersectAnnotations(sflowSet, annos).size() == 3) {
                Set<AnnotationMirror> set = AnnotationUtils.createAnnotationSet();
                if (inferenceChecker.isReadonlyType(ref.getType()))
                    set.add(SFlowChecker.TAINTED);
                else 
                    set.add(SFlowChecker.POLY);
                ref.setAnnotations(set);
//                if (InferenceChecker.DEBUG) {
//                    System.out.println("INFO: set " + ref + " from " + annos + " to " + set + " type: " + ref.getType());
//                }
            }
        }
        constraints.clear();
        constraints.addAll(solver.getUpdatedConstraints());
        solver = new WorklistSetbasedSolver(inferenceChecker, exprRefs, constraints);
        solver.setComputeLinearConstraints(false);
        System.out.println("constraint size: " + constraints.size());
        typeErrors = solver.solve();
        if (!typeErrors.isEmpty()) {
            for (Constraint c : typeErrors)
                System.out.println(c);
            System.out.println("There are " + typeErrors.size() + " type errors after setting readonly PAR");
            return typeErrors;
        }
        constraints.clear();
        constraints.addAll(solver.getUpdatedConstraints());
        System.out.println("constraint size: " + constraints.size());

        return typeErrors;
    }
	
	/**
	 * Build the maximal solution
	 */
	@Override
	public List<Constraint> extractConcreteTyping(int typeErrorNum) {
		// Check whether we need to resolve conflicts
		List<Constraint> typeErrors = Collections.emptyList();

		maximalSolution = new HashMap<String, Reference>();
		
        List<Reference> copyRefs = copyReferences(exprRefs);

        if (inferenceChecker.isInferLibrary()) {
            if (!(typeErrors = handleLibraryInference(exprRefs, constraints, maximalSolution)).isEmpty())
                return typeErrors;
        }
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
            WorklistSetbasedSolver solver = new WorklistSetbasedSolver(inferenceChecker,
                    exprRefs, constraints);
            solver.setComputeLinearConstraints(false);
            List<Constraint> conflictConstraints = solver.solve();
            InferenceChecker.DEBUG = prev;
            if (!conflictConstraints.isEmpty()) {
                recoverReferences(exprRefs, copyRefs);
                int counter = 0;
                for (Constraint c : conflictConstraints) {
                    Reference left = c.getLeft();
                    Reference right = c.getRight();
                    if (left instanceof MethodAdaptReference &&
                            ((MethodAdaptReference) left).getContextRef() instanceof ConstantReference
                        || right instanceof MethodAdaptReference &&
                            ((MethodAdaptReference) right).getContextRef() instanceof ConstantReference) 
                        continue;
                    System.out.println(c);
                    counter++;
                }
                System.out.println("There are " + counter
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
