package checkers.inference.reimflow;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.lang.model.element.AnnotationMirror;

import checkers.inference.Constraint;
import checkers.inference.Constraint.EmptyConstraint;
import checkers.inference.Constraint.EqualityConstraint;
import checkers.inference.Constraint.IfConstraint;
import checkers.inference.Constraint.SubtypeConstraint;
import checkers.inference.Constraint.UnequalityConstraint;
import checkers.inference.ConstraintSolver;
import checkers.inference.InferenceChecker;
import checkers.inference.InferenceChecker.FailureStatus;
import checkers.inference.InferenceMain;
import checkers.inference.InferenceUtils;
import checkers.inference.Reference;
import checkers.inference.Reference.AdaptReference;
import checkers.inference.Reference.FieldAdaptReference;
import checkers.inference.SetbasedSolver.SetbasedSolverException;
import checkers.util.AnnotationUtils;

public class WorklistSetbasedSolver implements ConstraintSolver {
	
	private InferenceChecker inferenceChecker;
	
	private List<Reference> exprRefs;
	
	private List<Constraint> constraints;
	
	private Map<Integer, List<Constraint>> refToConstraints; 
	
	public WorklistSetbasedSolver(InferenceChecker inferenceChecker, 
			List<Reference> exprRefs, List<Constraint> constraints) {
		this.inferenceChecker = inferenceChecker;
		this.exprRefs = exprRefs;
		this.constraints = constraints;
		this.refToConstraints = new HashMap<Integer, List<Constraint>>();
		this.secretSet = new LinkedHashSet<Constraint>(constraints.size());
		this.taintedSet = new LinkedHashSet<Constraint>(constraints.size() / 10);
		inferenceChecker.fillAllPossibleAnnos(exprRefs);
	}
	
	private Constraint currentConstraint; // For debug
	
	private Reference currentCause; // For debug
	
	private FileWriter tracePw; // For debug
	
	private static boolean isSameRun = false; // for debug
	
	private boolean preferSecret = false;
	
	private Set<Constraint> secretSet;
	
	private Set<Constraint> taintedSet;
	
	@Override
	public List<Constraint> solve() {
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
		
		for (Constraint c : constraints) {
			if (!(c instanceof EmptyConstraint))
				secretSet.add(c);
		}
		
		Set<Constraint> warnConstraints = new HashSet<Constraint>();
		Set<Constraint> conflictConstraints = new HashSet<Constraint>();
		boolean hasUpdate = false;
		buildRefToConstraintMapping();
		while (!secretSet.isEmpty() || !taintedSet.isEmpty()) {
			Constraint c = null;
			if (!secretSet.isEmpty()) {
				c = secretSet.iterator().next();
				secretSet.remove(c);
				preferSecret = true; 
			} else if (!taintedSet.isEmpty()) {
//				System.out.println("There are " + taintedSet.size() + " tainted constraints");
				c = taintedSet.iterator().next();
				taintedSet.remove(c);
				preferSecret = false; 
			}
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
		ArrayList<Constraint> list = new ArrayList<Constraint>(conflictConstraints);
		Collections.sort(list, new Comparator<Constraint>() {
			@Override
			public int compare(Constraint o1, Constraint o2) {
				int res = o1.getLeft().getFullRefName().compareTo(o2.getLeft().getFullRefName());
				if (res == 0)
					res = o1.getRight().getFullRefName().compareTo(o2.getRight().getFullRefName());
				return res;
			}
		});
//		return new ArrayList<Constraint>(conflictConstraints);
		return list;
	}
	
	public boolean handleConstraint(Constraint c) throws SetbasedSolverException {
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
		
		// For tracing
		Set<AnnotationMirror> oldSubAnnos = AnnotationUtils.createAnnotationSet();
		oldSubAnnos.addAll(subAnnos);
		Set<AnnotationMirror> oldSupAnnos = AnnotationUtils.createAnnotationSet();
		oldSupAnnos.addAll(supAnnos);
		
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
		
		if (InferenceChecker.DEBUG) {
			currentCause = null;
			if (oldSubAnnos.size() == subAnnos.size())
				currentCause = subRef;
			else if (oldSupAnnos.size() == supAnnos.size())
				currentCause = supRef;
//			else
//				currentCause = null;
//			if (c.getID() == 21517) {
//				System.out.println("oldSubAnnos = " + oldSubAnnos);
//				System.out.println("subAnnos = " + subAnnos);
//				System.out.println("oldSupAnnos = " + oldSupAnnos);
//				System.out.println("supAnnos = " + supAnnos);
//				System.out.println("currentCause = " + currentCause);
//			}
		}
		
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
		
		// For tracing
		Set<AnnotationMirror> oldLeftAnnos = AnnotationUtils.createAnnotationSet();
		oldLeftAnnos.addAll(leftAnnos);
		Set<AnnotationMirror> oldRightAnnos = AnnotationUtils.createAnnotationSet();
		oldRightAnnos.addAll(rightAnnos);
		
		// The default intersection of Set doesn't work well
		Set<AnnotationMirror> interAnnos = InferenceUtils.intersectAnnotations(
				leftAnnos, rightAnnos);
		
		if (interAnnos.isEmpty()) {
			throw new SetbasedSolverException("ERROR: solve " + c 
					+ " failed becaue of an empty set.");
		}
		
		if (InferenceChecker.DEBUG) {
			currentCause = null;
			if (oldLeftAnnos.size() == interAnnos.size())
				currentCause = left;
			else if (oldRightAnnos.size() == interAnnos.size())
				currentCause = right;
//			if (c.getID() == 24890) {
//				System.out.println("oldSubAnnos = " + oldLeftAnnos);
//				System.out.println("subAnnos = " + leftAnnos);
//				System.out.println("oldSupAnnos = " + oldRightAnnos);
//				System.out.println("supAnnos = " + rightAnnos);
//				System.out.println("currentCause = " + currentCause);
//			}
		
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
		
		// For tracing
		Set<AnnotationMirror> oldLeftAnnos = AnnotationUtils.createAnnotationSet();
		oldLeftAnnos.addAll(leftAnnos);
		Set<AnnotationMirror> oldRightAnnos = AnnotationUtils.createAnnotationSet();
		oldRightAnnos.addAll(rightAnnos);
	
		// The default intersection of Set doesn't work well
		Set<AnnotationMirror> differAnnos = InferenceUtils.differAnnotations(
				leftAnnos, rightAnnos);
		
		if (differAnnos.isEmpty()) {
			throw new SetbasedSolverException("ERROR: solve " + c 
					+ " failed becaue of an empty set.");
		}
		
		if (InferenceChecker.DEBUG) {
			if (oldLeftAnnos.equals(leftAnnos))
				currentCause = left;
			else if (oldRightAnnos.equals(rightAnnos))
				currentCause = right;
			else
				currentCause = null;
		}
		// Update the left
		return setAnnotations(left, differAnnos);
	}
	
	@Deprecated
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
		Set<AnnotationMirror> oldAnnos = ref.getAnnotations();
		if (ref instanceof AdaptReference)
			return setAnnotations((AdaptReference) ref, annos);
		if (oldAnnos.equals(annos))
			return false;
		
//		if (ref.getId() == 308802) {
//			System.out.println((preferSecret ? "Forward" : "Backward") + " to: " + currentConstraint);
//		}
//		if (ref.getId() == 308863) {
//			System.out.println((preferSecret ? "Forward" : "Backward") + " to: " + currentConstraint);
//		}
		
		if (preferSecret && annos.size() == 1 && annos.contains(ReimFlowChecker.TAINTED)
//				|| preferSecret && annos.size() == 1 && annos.contains(ReimFlowChecker.POLY) 
//					&& oldAnnos.size() >= 2 && !oldAnnos.contains(ReimFlowChecker.TAINTED)
				|| preferSecret && annos.size() == 2 && !annos.contains(ReimFlowChecker.SECRET)) {
//		if (preferSecret && (oldAnnos.contains(ReimFlowChecker.SECRET) && !annos.contains(ReimFlowChecker.SECRET)
//				|| !oldAnnos.contains(ReimFlowChecker.SECRET) && annos.contains(ReimFlowChecker.TAINTED))) {
			if (currentConstraint != null) {
				taintedSet.remove(currentConstraint);
				taintedSet.add(currentConstraint);
			}
			return false;
		}
		
		// FIXME: Check whether to propagate or not 
		boolean hasUpdate = false;
		if (inferenceChecker instanceof ReimFlowChecker 
				&& ((ReimFlowChecker) inferenceChecker).isInferLibrary()
				&& annos.size() == 1 && annos.contains(ReimFlowChecker.TAINTED)
				) {
			if (ref.getType() != null
					&& ((ReimFlowChecker) inferenceChecker).isTaintableRef(ref)) {
				hasUpdate = true;
			}
			else {
				// Skip
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
			sb.append("|" + (currentCause != null ? currentCause.toAnnotatedString().replace("\r", "").replace("\n", "").replace('|', ' ') : "NONE") 
					+ " (" + (preferSecret ? "forward" : "backward") + ")");
			sb.append("|");
			if (currentCause != null) {
				if (currentCause instanceof AdaptReference) {
					Reference contextRef = ((AdaptReference) currentCause).getContextRef();
					Reference declRef = ((AdaptReference) currentCause).getDeclRef();
					if (contextRef.getId() == ref.getId())
						sb.append(declRef.getId());
					else {
//						if (declRef.getAnnotations().size() == 1 && declRef.toString().startsWith("zLIB"))
//							sb.append(declRef.getId());
//						else
							sb.append(currentCause.getId());
					}
				} else
					sb.append(currentCause.getId());
			}
			else 
				sb.append("0");
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
		}
		
		if (hasUpdate) {
			ref.setAnnotations(annos);
			// put the related constraints
			List<Constraint> list = refToConstraints.get(ref.getId());
			if (list != null)
				for (Constraint c : list) {
					secretSet.remove(c);
					secretSet.add(c);
//					if (ref.getId() == 308802) {
//						System.out.println("Adding: " + c);
//					}
				}
		}	

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
	
	
	private void buildRefToConstraintMapping() {
		for (Constraint c : constraints) {
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
					int[] ids = null;
					if (ref instanceof AdaptReference) {
						ids = new int[] {
								((AdaptReference) ref).getDeclRef().getId(),
								((AdaptReference) ref).getContextRef().getId() };
					} else 
						ids = new int[] {ref.getId()};
					for (int id : ids) {
						List<Constraint> l = refToConstraints.get(id);
						if (l == null) {
							l = new ArrayList<Constraint>(5);
							refToConstraints.put(id, l);
						}
						l.add(c);
					}
				}
			}
		}
	}

}
