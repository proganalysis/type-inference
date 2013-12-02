/**
 * 
 */
package checkers.inference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.type.TypeKind;

import checkers.inference.Constraint.EqualityConstraint;
import checkers.inference.Constraint.IfConstraint;
import checkers.inference.Constraint.SubtypeConstraint;
import checkers.inference.Constraint.UnequalityConstraint;
import checkers.inference.InferenceChecker.FailureStatus;
import checkers.inference.Reference.AdaptReference;
import checkers.inference.Reference.FieldAdaptReference;
import checkers.inference.sflow.SFlowChecker;
import checkers.util.InternalUtils;
import checkers.util.TreeUtils;


/**
 * @author huangw5
 *
 */
public class SetbasedSolver implements ConstraintSolver {
	
	public static class SetbasedSolverException extends Exception {
		/**
		 * 
		 */
		private static final long serialVersionUID = -7773156808761868415L;

		public SetbasedSolverException(String string) {
			super(string);
		}
		
	}
	
	private InferenceChecker inferenceChecker;
	
	private List<Reference> exprRefs;
	
	private List<Constraint> constraints;
	
	public SetbasedSolver(InferenceChecker inferenceChecker, 
			List<Reference> exprRefs, List<Constraint> constraints) {
		this.inferenceChecker = inferenceChecker;
		this.exprRefs = exprRefs;
		this.constraints = constraints;
		inferenceChecker.fillAllPossibleAnnos(exprRefs);
	}
	
	private Constraint currentConstraint; // For debug
	
	private FileWriter tracePw; // For debug
	
	private static boolean isSameRun = false; // for debug
	
	@Override
	public List<Constraint> solve() {
//		System.out.println("INFO: Using " + this.getClass());
		// FIXME: output constraints
		if (InferenceChecker.DEBUG) {
			try {
				if (!isSameRun) { 
					tracePw = new FileWriter(new File(InferenceMain.outputDir
							+ File.separator + "trace.log"), false);
					isSameRun = true;
				} else {
					tracePw = new FileWriter(new File(InferenceMain.outputDir
							+ File.separator + "trace.log"), true);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		Set<Constraint> warnConstraints = new HashSet<Constraint>();
		List<Constraint> conflictConstraints;
		boolean hasUpdate = false;
		do {
			conflictConstraints = new LinkedList<Constraint>();
			hasUpdate = false;
			for (Constraint c : constraints) {
				try {
					hasUpdate = handleConstraint(c) || hasUpdate;
				} catch (SetbasedSolverException e) {
					FailureStatus fs = inferenceChecker.getFailureStatus(c);
					if (fs == FailureStatus.ERROR) {
						hasUpdate = false;
						conflictConstraints.add(c);
					} else if (fs == FailureStatus.WARN) {
						if (!warnConstraints.contains(c)) {
							System.out.println("WARN: handling constraint " + c + " failed.");
							warnConstraints.add(c);
						}
					}
				}
			}
		} while (hasUpdate);
		
		if (InferenceChecker.DEBUG) {
			if (tracePw != null)
				try {
					tracePw.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			// Output all references with id
			try {
				PrintWriter pw2 = new PrintWriter(InferenceMain.outputDir
						+ File.separator + "all-refs.log");
				for (Reference ref : exprRefs) {
					if (ref.getIdentifier() == null)
						continue;
					String s = ref.getId()
							+ "|"
							+ ref.toString().replace("\n", " ")
									.replace("\r", " ").replace('|', ' ') + "|";
					s = s
							+ " {"
							+ InferenceUtils.formatAnnotationString(ref
									.getAnnotations()) + "}";
					pw2.println(s.replace('\'', ' '));
				}
				pw2.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return conflictConstraints;
	}
	
	protected boolean handleConstraint(Constraint c) throws SetbasedSolverException {
		currentConstraint = c;
		boolean hasUpdate = false;
		if (c instanceof SubtypeConstraint) {
			hasUpdate = handleSubtypeConstraint((SubtypeConstraint) c);
		} else if (c instanceof EqualityConstraint) {
			hasUpdate = handleEqualityConstraint((EqualityConstraint) c);
		} else if (c instanceof UnequalityConstraint) {
			hasUpdate = handleInequalityConstraint((UnequalityConstraint) c);
		} else if (c instanceof IfConstraint) {
			hasUpdate = handleIfConstraint((IfConstraint) c);
		}
		return hasUpdate;
	}

	/**
	 * Return true if there are updates
	 * @param c
	 * @return
	 * @throws SetbasedSolverException
	 */
	protected boolean handleSubtypeConstraint(SubtypeConstraint c) throws SetbasedSolverException {
		// Get the references
		Reference subRef = c.getLeft();
		Reference supRef = c.getRight();
		
		boolean hasUpdate = false;		
		
		// Get the annotations
		Set<AnnotationMirror> subAnnos = getAnnotations(subRef);
		Set<AnnotationMirror> supAnnos = getAnnotations(supRef);
		
//		if (c.id == 26951 && supAnnos.size() == 1)
//			System.out.println();
		
		// First update the left: If a left annotation is not 
		// subtype of any right annotation, then remove it. 
		for (Iterator<AnnotationMirror> it = subAnnos.iterator(); 
				it.hasNext();) {
			AnnotationMirror subAnno = it.next();
			boolean isFeasible = false;
			for (AnnotationMirror supAnno : supAnnos) {
				if (inferenceChecker.getQualifierHierarchy().isSubtype(
						subAnno, supAnno)) {
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
		if (inferenceChecker.isStrictSubtyping()) {
			for (Iterator<AnnotationMirror> it = supAnnos.iterator(); 
					it.hasNext();) {
				AnnotationMirror supAnno = it.next();
				boolean isFeasible = false;
				for (AnnotationMirror subAnno : subAnnos) {
					if (inferenceChecker.getQualifierHierarchy().isSubtype(
							subAnno, supAnno)) {
						isFeasible = true;
						break;
					}
				}
				if (!isFeasible)
					it.remove();
			}
		}
		
		if (subAnnos.isEmpty() || supAnnos.isEmpty())
			throw new SetbasedSolverException("ERROR: solve " + c 
					+ " failed becaue of an empty set.");
		
		hasUpdate = setAnnotations(subRef, subAnnos)
				|| setAnnotations(supRef, supAnnos) || hasUpdate;
		
		
		return hasUpdate;
	}
	
	protected boolean handleEqualityConstraint(EqualityConstraint c) throws SetbasedSolverException {
		// Get the references
		Reference left = c.getLeft();
		Reference right = c.getRight();
		
		boolean hasUpdate = false;
		
		// Get the annotations
		Set<AnnotationMirror> leftAnnos = getAnnotations(left);
		Set<AnnotationMirror> rightAnnos = getAnnotations(right);
		
//		if (c.id == 93 && leftAnnos.size() == 1)
//			System.out.println();
	
		// The default intersection of Set doesn't work well
		Set<AnnotationMirror> interAnnos = InferenceUtils.intersectAnnotations(
				leftAnnos, rightAnnos);
		
		if (interAnnos.isEmpty()) {
			throw new SetbasedSolverException("ERROR: solve " + c 
					+ " failed becaue of an empty set.");
		}
		// update both
		return setAnnotations(left, interAnnos)
				|| setAnnotations(right, interAnnos)
				|| hasUpdate;
	}
	
	protected boolean handleInequalityConstraint(UnequalityConstraint c) throws SetbasedSolverException {
		// Get the references
		Reference left = c.getLeft();
		Reference right = c.getRight();
		
		// Get the annotations
		Set<AnnotationMirror> leftAnnos = getAnnotations(left);
		Set<AnnotationMirror> rightAnnos = getAnnotations(right);
	
		// The default intersection of Set doesn't work well
		Set<AnnotationMirror> differAnnos = InferenceUtils.differAnnotations(
				leftAnnos, rightAnnos);
		
		if (differAnnos.isEmpty()) {
			throw new SetbasedSolverException("ERROR: solve " + c 
					+ " failed becaue of an empty set.");
		}
		// Update the left
	return setAnnotations(left, differAnnos);
	}
	
	protected boolean handleIfConstraint(IfConstraint c) throws SetbasedSolverException {
		boolean hasUpdate = false;
		Constraint condition = c.getCondition();
		boolean satisfied = false;
		// Currently, condition can only be Equal or Unequal
		if (condition instanceof EqualityConstraint) {
			satisfied = condition.getLeft().getAnnotations()
					.equals(condition.getRight().getAnnotations());
		} else if (condition instanceof UnequalityConstraint) {
			satisfied = !condition.getLeft().getAnnotations()
					.equals(condition.getRight().getAnnotations());
		} else 
			throw new RuntimeException("Illegal conditon constraint! " + condition);
		
		Constraint ifConstraint = c.getIfConstraint();
		Constraint elseConstraint = c.getElseConstraint();
		
		if (satisfied && ifConstraint != null) 
			hasUpdate = handleConstraint(ifConstraint);
		else if (!satisfied && elseConstraint != null)
			hasUpdate = handleConstraint(elseConstraint);
		
		return hasUpdate;
	}
	
	protected Set<AnnotationMirror> getAnnotations(Reference ref) {
		if (ref instanceof AdaptReference) {
			AdaptReference aRef = (AdaptReference) ref;
			Reference contextRef = aRef.getContextRef();
			Reference declRef = aRef.getDeclRef();
			
			if (aRef instanceof FieldAdaptReference)
				return inferenceChecker.adaptFieldSet(contextRef.getAnnotations(), 
						declRef.getAnnotations());
			else
				return inferenceChecker.adaptMethodSet(contextRef.getAnnotations(), 
						declRef.getAnnotations());
		} else
			return ref.getAnnotations();
	}
	
	/**
	 * Return true if there are updates
	 * @param ref
	 * @param annos
	 * @return
	 * @throws SetbasedSolverException
	 */
	protected boolean setAnnotations(Reference ref, Set<AnnotationMirror> annos)
			throws SetbasedSolverException {
		if (ref instanceof AdaptReference)
			return setAnnotations((AdaptReference) ref, annos);
		if (ref.getAnnotations().equals(annos))
			return false;
		
//		if (ref.getId() == 24463) {
//			System.out.println("Setting " + ref.toAnnotatedString() + " to "
//					+ annos + " DEBUG: " + InferenceChecker.DEBUG);
//		}
		
		// FIXME: Check whether to propagate or not 
		boolean hasUpdate = false;
		if (inferenceChecker instanceof SFlowChecker 
				&& ((SFlowChecker) inferenceChecker).isInferLibrary()
				&& annos.size() == 1 && annos.contains(SFlowChecker.TAINTED)
				) {
			if (ref.getType() != null
					&& ((SFlowChecker) inferenceChecker).isTaintableRef(ref)) {
				hasUpdate = true;
			}
			else {
				// Skip
//				if (ref.toString().contains("new "))
//					System.out.println("Taintable? " + ((SFlowChecker) inferenceChecker).isTaintableRef(ref) + ": " + ref.toString());
			}
		}
		else {
			hasUpdate = true;
		}
		// FIXME: End
		
		if (InferenceChecker.DEBUG && hasUpdate) {
			// Output modification trace
			// FIXME: The code is ugly, use log4j?
			StringBuilder sb = new StringBuilder();
			sb.append(ref.getId()).append("|").append(ref.toString().replace("\r", "").replace("\n", "").replace('|', ' ')).append("|");
			sb.append("{" + InferenceUtils.formatAnnotationString(ref.getAnnotations()) + "}|");
			sb.append("{" + InferenceUtils.formatAnnotationString(annos) + "}|");
			sb.append(currentConstraint.toString().replace("\r", "").replace("\n", "").replace('|', ' '));
            sb.append("|NONE|0");
			try {
				if (tracePw == null) {
					tracePw = new FileWriter(new File(InferenceMain.outputDir
							+ File.separator + "trace.log"), true);
					tracePw.write(sb.toString().replace('\'', ' ') + "\n");
					tracePw.close();
					tracePw = null;
				}
				else
					tracePw.write(sb.toString().replace('\'', ' ') + "\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			if (ref.id == 24590 || ref.id == 24575)
//				System.out.println("BUG: setting " + ref.toAnnotatedString() + " to " + annos);
		}
		
		if (hasUpdate)
			ref.setAnnotations(annos);

		return hasUpdate;
	}
	protected boolean setAnnotations(AdaptReference ref, Set<AnnotationMirror> annos) 
			throws SetbasedSolverException {
		AdaptReference aRef = (AdaptReference) ref;
		Reference contextRef = aRef.getContextRef();
		Reference declRef = aRef.getDeclRef();
		
		Set<AnnotationMirror> contextAnnos = contextRef.getAnnotations();
		Set<AnnotationMirror> declAnnos = declRef.getAnnotations();
		
		// First iterate through contextAnnos and remove infeasible annotations
		for (Iterator<AnnotationMirror> it = contextAnnos.iterator(); it.hasNext();) {
			AnnotationMirror contextAnno = it.next();
			boolean isFeasible = false;
			for (AnnotationMirror declAnno : declAnnos) {
				AnnotationMirror outAnno = null;
				if (aRef instanceof FieldAdaptReference)
					outAnno = inferenceChecker.adaptField(contextAnno, declAnno);
				else
					outAnno = inferenceChecker.adaptMethod(contextAnno, declAnno);
				if (outAnno != null && annos.contains(outAnno)) {
					isFeasible = true;
					break;
				}
			}
			if (!isFeasible)
				it.remove();
		}
		
		if (contextAnnos.isEmpty())
			throw new SetbasedSolverException("ERROR: Empty set for contextRef in AdaptConstraint");
		
		// Now iterate through declAnnos and remove infeasible annotations
		for (Iterator<AnnotationMirror> it = declAnnos.iterator(); it.hasNext();) {
			AnnotationMirror declAnno = it.next();
			boolean isFeasible = false;
			for (AnnotationMirror contextAnno : contextAnnos) {
				AnnotationMirror outAnno = null;
				if (aRef instanceof FieldAdaptReference)
					outAnno = inferenceChecker.adaptField(contextAnno, declAnno);
				else
					outAnno = inferenceChecker.adaptMethod(contextAnno, declAnno);
				if (outAnno != null && annos.contains(outAnno)) {
					isFeasible = true;
					break;
				}
			}
			if (!isFeasible)
				it.remove();
		}
		
		if (declAnnos.isEmpty())
			throw new SetbasedSolverException("ERROR: Empty set for declRef in AdaptConstraint");
		
		return setAnnotations(contextRef, contextAnnos)
				|| setAnnotations(declRef, declAnnos);
	}


    public List<Constraint> getUpdatedConstraints() {
        return constraints;
    }
	
//	/**
//	 * Build the maximal solution
//	 */
//	protected void buildMaximalSolution() {
//		if (maximalSolution != null)
//			return;
//		maximalSolution = new HashMap<String, Reference>();
//		for (Reference ref : exprRefs) {
//			String identifier = ref.getIdentifier();
//			if (identifier != null) {
//				Reference maxRef = getMaximal(ref);
//				maximalSolution.put(identifier, maxRef);
//			}
//		}
//		if (InferenceChecker.DEBUG) {
//			try {
//				PrintWriter pw = new PrintWriter(InferenceMain.outputDir
//						+ File.separator + "maximalsolution.log");
//				for (Entry<String, Reference> entry : maximalSolution.entrySet()) {
//					Reference ref = entry.getValue();
//					pw.println(entry.getKey() + ": " + ref.toString() + ref.formatAnnotations());
//				}
//				pw.close();
//			} catch (Exception e) {
//			}
//		}
//	}
	
//	/**
//	 * Get a copy of {@code ref} with only the maximal annotation left
//	 * @param ref
//	 * @return
//	 */
//	protected Reference getMaximal(Reference ref) {
//		return inferenceChecker.getMaximal(ref);
//	}
//	
//	@Override
//	public void annotateInferredType(Element elt, AnnotatedTypeMirror type) {
//		if (elt.getKind() == ElementKind.METHOD 
//				|| elt.getKind() == ElementKind.CONSTRUCTOR) {
//			if (type.getKind() != TypeKind.EXECUTABLE)
//				throw new RuntimeException("Incompatible method type!");
//			ExecutableElement methodElt = (ExecutableElement) elt;
//			AnnotatedExecutableType methodType = (AnnotatedExecutableType) type;
//			assert methodElt.getParameters().size() == methodType.getParameterTypes().size();
//			// Parameters
//			for (Iterator<? extends VariableElement> itParamElt = methodElt
//					.getParameters().iterator(); itParamElt.hasNext();) {
//				for (Iterator<AnnotatedTypeMirror> itParamType = methodType
//						.getParameterTypes().iterator(); itParamType.hasNext();) {
//					annotateInferredType(itParamElt.next(), itParamType.next());
//				}
//			}
//			String methodSig = InferenceUtils.getElementSignature(elt);
//			ExecutableReference methodRef = (ExecutableReference) maximalSolution.get(methodSig);
//			if (methodRef != null) {
//				// Return
//				InferenceUtils.annotateReferenceType(methodType.getReturnType(), 
//						methodRef.getReturnRef());
//				// Receiver
//				InferenceUtils.annotateReferenceType(methodType.getReceiverType(), 
//						methodRef.getReceiverRef());
//			}
//		} else
//			annotateInferredType(InferenceUtils.getElementSignature(elt), type);
//	}

//	@Override
//	public void annotateInferredType(String identifier, AnnotatedTypeMirror type) {
//		if (maximalSolution == null)
//			throw new RuntimeException("Should have called buildMaximalSolution()"
//					+ " before retrieving the inferrred type!");
//		Reference ref = maximalSolution.get(identifier);
//		if (ref != null) {
//			InferenceUtils.annotateReferenceType(type, ref);
//		}
//	}
	
//	@Override
//	public Reference getInferredReference(String identifier) {
//		if (maximalSolution == null)
//			throw new RuntimeException("Should have called buildMaximalSolution()"
//					+ " before retrieving the inferrred type!");
//		Reference ref = maximalSolution.get(identifier);
//		return ref;
//	}

//	@Override
//	public List<Reference> getInferredReferences() {
//		if (maximalSolution == null)
//			throw new RuntimeException("Should have called buildMaximalSolution()"
//					+ " before retrieving the inferrred type!");
//		return new ArrayList<Reference>(maximalSolution.values());
//	}


	
	
//	@Override
//	public void printAllVariables(PrintWriter out) {
//		inferenceChecker.printResult(maximalSolution, out);
//	}

}
