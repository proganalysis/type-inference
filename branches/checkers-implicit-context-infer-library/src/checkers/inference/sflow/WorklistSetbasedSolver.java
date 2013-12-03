package checkers.inference.sflow;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
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
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import com.sun.source.tree.*;
import com.sun.source.tree.Tree.*;

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
import checkers.inference.Reference.*;
import checkers.inference.Reference.AdaptReference;
import checkers.inference.Reference.FieldAdaptReference;
import checkers.inference.Reference.MethodAdaptReference;
import checkers.inference.SetbasedSolver.SetbasedSolverException;
import checkers.util.*;

public class WorklistSetbasedSolver implements ConstraintSolver {
	
	private InferenceChecker inferenceChecker;
	
	private List<Reference> exprRefs;
	
	private Set<Constraint> constraints;
	
	private Map<Integer, List<Constraint>> refToConstraints; 

	private Map<String, List<Constraint>> adaptRefToConstraints; 

	private Map<Integer, Set<Reference>> declRefToContextRefs; 

    private Map<Integer, Constraint> subconsToEqucons; 
	
	public WorklistSetbasedSolver(InferenceChecker inferenceChecker, 
			List<Reference> exprRefs, List<Constraint> constraints) {
		this.inferenceChecker = inferenceChecker;
		this.exprRefs = exprRefs;
        this.constraints = new LinkedHashSet<Constraint>(constraints);
		this.refToConstraints = new HashMap<Integer, List<Constraint>>();
        this.adaptRefToConstraints = new HashMap<String, List<Constraint>>();
        this.declRefToContextRefs = new HashMap<Integer, Set<Reference>>();
        this.subconsToEqucons = new HashMap<Integer, Constraint>();
		this.secretSet = new LinkedHashSet<Constraint>(this.constraints.size());
		this.taintedSet = new LinkedHashSet<Constraint>(this.constraints.size() / 10);
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
        replaceUseVarWithDeclVar(constraints);
        rewriteEqualityConstraints(constraints);
        Set<Constraint> extendedConstraints = new LinkedHashSet<Constraint>(constraints);
		
		Set<Constraint> warnConstraints = new HashSet<Constraint>();
		Set<Constraint> conflictConstraints = new HashSet<Constraint>();
        int size = 0;
		boolean hasUpdate = false;
		buildRefToConstraintMapping(extendedConstraints);
//        int it = 1;
//        while (size != constraints.size() || hasUpdate) {
//            System.out.println("Iteration " + (it++) + ":");
//            size = constraints.size();
//            hasUpdate = false;
        warnConstraints.clear();
        conflictConstraints.clear();
        System.out.println("Solving constraints...");
        long startTime = System.currentTimeMillis();
        // First add to secretSet
        for (Constraint c : extendedConstraints) {
            if (!(c instanceof EmptyConstraint)) {
                secretSet.add(c);
            }
        }
        Set<Constraint> newConstraints = new LinkedHashSet<Constraint>();
        while (!secretSet.isEmpty() || !taintedSet.isEmpty()) {
            Constraint c = null;
            if (!secretSet.isEmpty()) {
                c = secretSet.iterator().next();
                secretSet.remove(c);
                preferSecret = true; 
            } else if (!taintedSet.isEmpty()) {
                c = taintedSet.iterator().next();
                taintedSet.remove(c);
                preferSecret = false; 
            }
            try {
                hasUpdate = handleConstraint(c) || hasUpdate;
                Set<Constraint> newCons = addLinearConstraints(c, extendedConstraints, newConstraints);
//                    if (newCons.size() > 800) {
//                        System.out.println("Adding linear constraints for " + c);
//                        System.out.println(" Size = " + newCons.size());
//                    }
                newConstraints.addAll(newCons);
            } catch (SetbasedSolverException e) {
                FailureStatus fs = inferenceChecker.getFailureStatus(c);
                if (fs == FailureStatus.ERROR && constraints.contains(c)) {
                    conflictConstraints.add(c);
                } else if (fs == FailureStatus.WARN) {
                    if (!warnConstraints.contains(c)) {
                        System.out.println("WARN: handling constraint " + c + " failed.");
                        warnConstraints.add(c);
                    }
                }
            }
            if (secretSet.isEmpty() && taintedSet.isEmpty()) {
//                buildRefToConstraintMapping(newConstraints);
                extendedConstraints.addAll(newConstraints);
                secretSet.addAll(newConstraints);
                System.out.println("Added " + newConstraints.size() + " new constraints");
                newConstraints.clear();
            }
        }
//            System.out.println("Time: " + (System.currentTimeMillis() - startTime) + " ms");
//            System.out.println("Constraint size - " + constraints.size());
//        }
		if (InferenceChecker.DEBUG) {
            PrintWriter pw;
			try {
				pw = new PrintWriter(InferenceMainSFlow.outputDir
						+ File.separator + "new-constraints.log");
                for (Constraint c : extendedConstraints) {
                    pw.println(c.toString());
                }
				pw.close();
				
			} catch (Exception e) {
				e.printStackTrace();
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
		ArrayList<Constraint> list = new ArrayList<Constraint>(conflictConstraints.size());
        for (Constraint c : conflictConstraints) {
            Constraint ec = subconsToEqucons.get(c.getID());
            if (ec != null)
                list.add(ec);
            else
                list.add(c);
        }
        Collections.sort(list, new Comparator<Constraint>() {
            @Override
            public int compare(Constraint o1, Constraint o2) {
                int res = o1.getLeft().getFullRefName().compareTo(o2.getLeft().getFullRefName());
                if (res == 0)
                    res = o1.getRight().getFullRefName().compareTo(o2.getRight().getFullRefName());
                if (res == 0)
                    res = o1.getID() - o2.getID();
                return res;
            }
        });
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

//        if (c.getLeft().getId() == 382461 && c.getRight().getId() == 382489)
//            System.out.println();
		
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
		
        // Skip for the following cases
		if (preferSecret && annos.size() == 1 && annos.contains(SFlowChecker.SAFE)
				|| preferSecret && annos.size() == 2 && !annos.contains(SFlowChecker.TAINTED)) {
			if (currentConstraint != null) {
				taintedSet.remove(currentConstraint);
				taintedSet.add(currentConstraint);
			}
			return false;
		}
        // For Issue-3:
        // Skip if setting the parameter of a non-private static methods
        // to Tainted by viewpoint adaptation. 
        Element elt = null;
        if ((elt = ref.getElement()) != null && elt.getKind() == ElementKind.PARAMETER
                && currentConstraint != null && (currentConstraint.getRight() instanceof MethodAdaptReference)
                && annos.size() == 1 && annos.contains(SFlowChecker.TAINTED)) {
            MethodAdaptReference mref = (MethodAdaptReference) currentConstraint.getRight();
            Tree tree = null;
            ExecutableElement methodElt = null;
            if ((tree = mref.getTree()) != null && (tree instanceof MethodInvocationTree)
                    && (methodElt = TreeUtils.elementFromUse((MethodInvocationTree) tree)) != null
                    && ElementUtils.isStatic(methodElt) 
                    && !methodElt.getModifiers().contains(Modifier.PRIVATE))
//                System.out.println("skip static parameter: " + currentConstraint);
                return false;
        } 

		
		// FIXME: Check whether to propagate or not. This is for
        // inferLibary only
		boolean hasUpdate = false;
		if (inferenceChecker instanceof SFlowChecker 
				&& ((SFlowChecker) inferenceChecker).isInferLibrary()
				&& annos.size() == 1 && annos.contains(SFlowChecker.SAFE)
				) {
			if (ref.getType() != null
					&& ((SFlowChecker) inferenceChecker).isTaintableRef(ref)) {
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

        // Nov 22, 2013: Skip updating CONSTANT method adaptation context
//        if (aRef instanceof MethodAdaptReference)
//            return setAnnotations(declRef, declAnnos);

		return setAnnotations(contextRef, contextAnnos)
				|| setAnnotations(declRef, declAnnos);
	}

    private Reference replaceRef(Reference ref, Map<Integer, Reference> map) {
        if (ref instanceof ArrayReference) {
            Reference componentRef = ((ArrayReference) ref).getComponentRef();
            ((ArrayReference) ref).setComponentRef(replaceRef(componentRef, map));
        } else if (ref instanceof AdaptReference) {
            Reference contextRef = ((AdaptReference) ref).getContextRef();
            ((AdaptReference) ref).setContextRef(replaceRef(contextRef, map));
            Reference declRef = ((AdaptReference) ref).getDeclRef();
            ((AdaptReference) ref).setDeclRef(replaceRef(declRef, map));
        }
        Reference rRef = map.get(ref.getId());
        if (rRef == null)
            rRef = ref;
        return rRef;
    }

    private void replaceConstraint(Constraint c, Map<Integer, Reference> map) {
        Reference rRef = null;
        Reference left = c.getLeft();
        if (left != null) {
            c.setLeft(replaceRef(left, map));
        }
        Reference right = c.getRight();
        if (right != null) {
            c.setRight(replaceRef(right, map));
        }
    }

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

    private boolean isSame(Reference left, Reference right, Map<Integer, Reference> map) {
        if (!(left instanceof ConstantReference) && !(right instanceof ConstantReference)
                && left.getRefName().equals(right.getRefName())
                && left.getRefName().equals("#INTERNAL#")
            || left instanceof ArrayReference && right instanceof ArrayReference) {
            return false;
        }


        Reference varRef = null, expRef = null;
        Element elt = null;
        Tree tree = null;
        if ((elt = left.getElement()) != null 
                && (tree = right.getTree()) != null
                && elt.getKind() != ElementKind.CLASS
                && elt.getKind() != ElementKind.INTERFACE
                && elt.getKind() != ElementKind.ENUM
                && elt.getKind() != ElementKind.ANNOTATION_TYPE
                && !ElementUtils.isStatic(elt)
                && left.getFileName() != null
                && left.getFileName().equals(right.getFileName())
                && (elt.toString().equals(tree.toString())
                        && TreeUtils.elementFromUse((ExpressionTree) tree).equals(elt)
                    || left.getRefName().equals("THIS_" + elt.toString()) 
                        && right.getRefName().equals("EXP_this"))) {
            varRef = left; 
            expRef = right;
        } else if ((elt = right.getElement()) != null 
                && (tree = left.getTree()) != null
                && elt.getKind() != ElementKind.CLASS
                && elt.getKind() != ElementKind.INTERFACE
                && elt.getKind() != ElementKind.ENUM
                && elt.getKind() != ElementKind.ANNOTATION_TYPE
                && !ElementUtils.isStatic(elt)
                && left.getFileName() != null
                && left.getFileName().equals(right.getFileName())
                && (elt.toString().equals(tree.toString()) 
                        && TreeUtils.elementFromUse((ExpressionTree) tree).equals(elt)
                    || right.getRefName().equals("THIS_" + elt.toString()) 
                        && left.getRefName().equals("EXP_this"))) {
            varRef = right; 
            expRef = left;
        } /*else if (!(left instanceof ConstantReference) && !(right instanceof ConstantReference)
                && left.getRefName().equals(right.getRefName())
                && left.getRefName().equals("#INTERNAL#")) {
            varRef = left.getId() < right.getId() ? left : right; 
            expRef = left.getId() < right.getId() ? right : left; 
        }*/
        if (varRef != null && expRef != null) {
            Set<AnnotationMirror> interAnnos = InferenceUtils.intersectAnnotations(
                    varRef.getAnnotations(), expRef.getAnnotations());
            varRef.setAnnotations(interAnnos);
            int varId = varRef.getId();
            Reference ref = null;
            while ((ref = map.get(varId)) != null) {
                varRef = ref;
                varId = ref.getId();
            }
            map.put(expRef.getId(), varRef); 
            return true;
        }
        return false;
    }

    private boolean canRewrite(Reference ref) {
        if (ref == null || ref instanceof ConstantReference)
            return false;
        Element elt = ref.getElement();
        if (elt != null 
                && (elt.getKind() == ElementKind.CLASS
                    || elt.getKind() == ElementKind.INTERFACE
                    || elt.getKind() == ElementKind.ENUM
                    || elt.getKind() == ElementKind.ANNOTATION_TYPE
                    || ElementUtils.isStatic(elt)))
            return false;
        return true;
    }

    private void replaceUseVarWithDeclVar(Set<Constraint> cons) {
        // First find out constraints like "VAR_id == EXP_id" and
        // replace each appearence of EXP_id with VAR_id;
        Map<Integer, Reference> map = new HashMap<Integer, Reference>();
        for (Iterator<Constraint> it = cons.iterator(); it.hasNext();) {
            Constraint c = it.next();
            if (c instanceof EqualityConstraint) {
                Reference left = c.getLeft();
                Reference right = c.getRight();
                if (isSame(left, right, map)) {
                    // remove c from cons 
                    it.remove();
                }
            }
        }
        // Now do the replacement
        for (Constraint c : cons) {
            replaceConstraint(c, map);
        }
    }

    private void rewriteEqualityConstraints(Set<Constraint> cons) {
        // Rewrite other EqualityConstraint 
        List<Constraint> list = new LinkedList<Constraint>();
        for (Constraint c : cons) {
            Reference left = c.getLeft();
            Reference right = c.getRight();
            if (c instanceof EqualityConstraint
                    && canRewrite(left) && canRewrite(right)
                    && (!left.getRefName().equals("#INTERNAL#")
                            || !right.getRefName().equals("#INTERNAL#"))) {
                Constraint sc = new SubtypeConstraint(left, right);
                list.add(sc);
                subconsToEqucons.put(sc.getID(), c);
                sc = new SubtypeConstraint(right, left);
                list.add(sc);
                subconsToEqucons.put(sc.getID(), c);
            } else
                list.add(c);
        }
        cons.clear();
        cons.addAll(list);
    }

	
    private void removeTypeCast(Tree t) {
        if (!(t instanceof ExpressionTree))
            return;
        t = TreeUtils.skipParens((ExpressionTree) t);
        while (t.getKind() == Kind.TYPE_CAST) {
            t = ((TypeCastTree) t).getExpression();
            if (t instanceof ExpressionTree)
                t = TreeUtils.skipParens((ExpressionTree) t);
        }
    }


	private void buildRefToConstraintMapping(Constraint c) {
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
                    // For adaptReference, we use fullRefName as key
                    String key = ref.getFullRefName();
                    List<Constraint> l = adaptRefToConstraints.get(key);
                    if (l == null) {
                        l = new ArrayList<Constraint>(2);
                        adaptRefToConstraints.put(key, l);
                    }
                    l.add(c);
                    Set<Reference> contextSet = declRefToContextRefs.get(ids[0]);
                    if (contextSet == null) {
                        contextSet = new HashSet<Reference>();
                        declRefToContextRefs.put(ids[0], contextSet);
                    }
                    contextSet.add(((AdaptReference) ref).getContextRef());
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
            if ((c instanceof SubtypeConstraint)
                    && !(left instanceof AdaptReference)
                    && !(right instanceof AdaptReference)) {
                left.addGreaterRef(right);
                right.addLessRef(left);
            }
        }
    }
	
	private void buildRefToConstraintMapping(Set<Constraint> cons) {
		for (Constraint c : cons) {
            buildRefToConstraintMapping(c);
		}
	}

    /**
     * Get a list of references which have ref on the left or right
     */
    private List<Reference> getRelatedReferences(Reference ref, boolean onLeft) {
        List<Reference> list = new LinkedList<Reference>();
        Set<Constraint> relatedSet = null;
        if (ref instanceof AdaptReference) {
            List<Constraint> adaptCons = adaptRefToConstraints.get(ref.getFullRefName());
            if (adaptCons != null)
                relatedSet = new HashSet<Constraint>(adaptCons);
        } else {
            List<Constraint> relatedCons = refToConstraints.get(ref.getId());
            if (relatedCons != null) {
                relatedSet = new HashSet<Constraint>(relatedCons);
            } 
        }
        if (relatedSet == null)
            return list;
        for (Constraint c : relatedSet) {
            if (!(c instanceof SubtypeConstraint))
                continue;
            if (onLeft && c.getLeft().equals(ref) && canConnectVia(c.getRight(), ref)) 
                list.add(c.getRight());
            else if (!onLeft && c.getRight().equals(ref) && canConnectVia(c.getLeft(), ref)) 
                list.add(c.getLeft());
        }
        return list;
    }

    private boolean canConnectVia(Reference left, Reference right) {
        if (left == null || right == null)
            return false;
        // Should be in the same file or same class tree
        String leftFile = left.getFileName();
        String rightFile = right.getFileName();
        TypeElement leftEType = left.getEnclosingType();
        TypeElement rightEType = right.getEnclosingType();
//        if (leftFile != null && rightFile != null 
//                && !leftFile.equals(rightFile))
//            return false;
        if (leftEType != null && rightEType != null
                && !inferenceChecker.isSubtype(leftEType, rightEType)
                && !inferenceChecker.isSubtype(rightEType, leftEType))
            return false;

        Tree leftTree = left.getTree();
        Tree rightTree = right.getTree();
        if (leftTree != null && (leftTree instanceof MethodInvocationTree 
                    || leftTree instanceof NewClassTree
                    || leftTree instanceof BinaryTree 
                    || leftTree instanceof LiteralTree)
                && rightTree != null && !(right instanceof AdaptReference)
                    && (rightTree instanceof MethodInvocationTree 
                    || leftTree instanceof NewClassTree
                    || leftTree instanceof BinaryTree 
                    || leftTree instanceof LiteralTree))
            return false;


        //  no internal elements of arrays
        if (!(left instanceof AdaptReference) && left.getRefName().equals("#INTERNAL#") 
                || !(right instanceof AdaptReference) && right.getRefName().equals("#INTERNAL#"))
            return false;
        // no subclass constraints
        // Nov 29, 2013: Actuall we need such constraints. For the 
        // abstract method String getSQLString() in hibernate, we want 
        // to connect THIS and RET because one of its subclasses
        // connects THIS and RET
        Element leftElt = null, rightElt = null;
        if ((leftElt = left.getElement()) != null && (rightElt = right.getElement()) != null) {
            if (leftElt.getKind() == ElementKind.PARAMETER 
                    && rightElt.getKind() == ElementKind.PARAMETER)
                return false;  // both are parameters
            if (leftElt instanceof ExecutableElement && rightElt instanceof ExecutableElement
                    && leftElt.toString().equals(rightElt.toString())) {
                if (left.getRefName().startsWith("THIS_") && right.getRefName().startsWith("THIS_")
                        || left.getRefName().startsWith("RET_") && right.getRefName().startsWith("RET_"))
                    return false; // both are THIS or RET
            }
        }
        return true;
    }

    // Add new linear constraint c, return a new set
    private Set<Constraint> addLinearConstraints(Constraint con, Set<Constraint> cons, 
            Set<Constraint> tmpNewConstraints) {
        Set<Constraint> newCons = new LinkedHashSet<Constraint>();
        if (!(con instanceof SubtypeConstraint))
            return newCons;
        Queue<Constraint> queue = new LinkedList<Constraint>();
        queue.add(con); 
        while (!queue.isEmpty()) {
            Constraint c = queue.poll();
            Reference left = c.getLeft();
            Reference right = c.getRight();
            Reference ref = null;
            List<Constraint> tmplist = new LinkedList<Constraint>();
            // Step 1: field read
            // Nov 26, 2013: add linear constraint for array fields
            if ((left instanceof FieldAdaptReference) 
                    && (ref = ((FieldAdaptReference) left).getDeclRef()) != null
                    && (/*inferenceChecker instanceof SFlowChecker && ((SFlowChecker) inferenceChecker).isInferLibrary()
                        ||*/ ref.getAnnotations().size() == 1 && ref.getAnnotations().contains(SFlowChecker.POLY)
                        || ((FieldAdaptReference) left).getContextRef() instanceof ArrayReference)) {
                Constraint linear = new SubtypeConstraint(
                            ((FieldAdaptReference) left).getContextRef(), right, c.getID());
                tmplist.add(linear);
            }
            // Step 2: field write
            // Nov 26, 2013: add linear constraint for array fields
            else if ((right instanceof FieldAdaptReference) 
                    && (ref = ((FieldAdaptReference) right).getDeclRef()) != null
                    && (/*inferenceChecker instanceof SFlowChecker && ((SFlowChecker) inferenceChecker).isInferLibrary()
                        ||*/ ref.getAnnotations().size() == 1 && ref.getAnnotations().contains(SFlowChecker.POLY)
                        || ((FieldAdaptReference) right).getContextRef() instanceof ArrayReference)) {
                Constraint linear = new SubtypeConstraint(
                            left, ((FieldAdaptReference) right).getContextRef(), c.getID());
                tmplist.add(linear);
            }
            // Step 3: linear constraints
            else if (!(left instanceof AdaptReference) && !(right instanceof AdaptReference) 
                    && canConnectVia(left, right)) {
                for (Reference r : left.getLessSet()) {
                    if (!r.equals(right) && (right.getElement() != null || r.getElement() != null)) {
                        Constraint linear = new SubtypeConstraint(r, right, c.getID());
                        tmplist.add(linear);
                    }
                }
                for (Reference r : right.getGreaterSet()) {
                    if (!left.equals(r) && (left.getElement() != null || r.getElement() != null)) {
                        Constraint linear = new SubtypeConstraint(left, r, c.getID());
                        tmplist.add(linear);
                    }
                }
                // if c is a new linear constraint between parameters
                // and returns, add it into original constraints
                if (!constraints.contains(c) && isParamReturnConstraint(c)) {
                    // param/this -> return/param/this
                     constraints.add(c);
                }
                // look for method adapt constraint
                Set<Reference> contextSetLeft = declRefToContextRefs.get(left.getId());
                Set<Reference> contextSetRight = declRefToContextRefs.get(right.getId());
                if (contextSetLeft != null && contextSetRight != null) {
                    contextSetLeft.retainAll(contextSetRight);
                    if (!contextSetLeft.isEmpty()) {
                        List<Constraint> relatedCons = refToConstraints.get(left.getId());
                        for (Constraint related : relatedCons) {
                            if (!(related.getLeft() instanceof AdaptReference) 
                                    && (related.getRight() instanceof MethodAdaptReference)
                                    && contextSetLeft.contains(((MethodAdaptReference) related.getRight()).getContextRef()))
                                tmplist.add(related);
                        }
                    }
                }
            }
            // step 4: z <: (y |> par)
            else if (!(left instanceof AdaptReference) && (right instanceof MethodAdaptReference)) {
                Reference parRef = ((MethodAdaptReference) right).getDeclRef();
                Reference rcvRef = ((MethodAdaptReference) right).getContextRef();
                Set<Reference> parPrimes = parRef.getGreaterSet();
                for (Reference parPrime : parPrimes) {
                    MethodAdaptReference mr = new MethodAdaptReference(rcvRef, parPrime);
                    List<Reference> xs = getRelatedReferences(mr, true/*onLeft*/);
                    for (Reference x : xs) {
                        int retConId = -1;
                        List<Constraint> relatedCons = refToConstraints.get(parRef.getId());
                        for (Constraint relatedCon : relatedCons) {
                            if (relatedCon.getRight().equals(parPrime)) {
                                retConId = relatedCon.getID();
                                break;
                            }
                        }
                        Constraint linear = new SubtypeConstraint(left, x, new int[]{c.getID(), retConId});
                        tmplist.add(linear);
                        // add it into original constraints
                        if (!constraints.contains(linear)) {
//                            System.out.println("added2: " + linear);
//                            constraints.add(linear);
//                            if (linear.getLeft().getId() == 382461 && linear.getRight().getId() == 382489)
//                                System.out.println();
                        }
                    }
                }
            }
            // Extra step for Poly library
            if (inferenceChecker instanceof SFlowChecker
                    && ((SFlowChecker) inferenceChecker).isPolyLibrary()) {
                Element elt = null;
                if (!(left instanceof AdaptReference) && (right instanceof MethodAdaptReference)
                        && (ref = ((MethodAdaptReference) right).getDeclRef()) != null
                        && (elt = ref.getElement()) != null
                        && inferenceChecker.isFromLibrary(elt)) {
                    // z <: (y |> par)
                    // Nov 26, 2013: if z is NewClassTree/NewArrayTree
                    Constraint linear;
                    Tree ltree = null; 
                    if ((ltree = left.getTree()) != null 
                            && (ltree instanceof NewClassTree || ltree instanceof NewArrayTree)
                            && ref.getRefName().startsWith("THIS_")) 
                        linear = new SubtypeConstraint(((MethodAdaptReference) right).getContextRef(), left, c.getID());
                    else
                        linear = new SubtypeConstraint(left, ((MethodAdaptReference) right).getContextRef(), c.getID());
                    tmplist.add(linear);
                } else if (!(right instanceof AdaptReference) && (left instanceof MethodAdaptReference)
                        && (ref = ((MethodAdaptReference) left).getDeclRef()) != null
                        && (elt = ref.getElement()) != null
                        && inferenceChecker.isFromLibrary(elt)) {
                    // y |> ret <: x
                    Constraint linear = new SubtypeConstraint(((MethodAdaptReference) left).getContextRef(), right, c.getID());
                    tmplist.add(linear);
                }
            }
            for (Constraint linear : tmplist) {
                Tree leftTree = linear.getLeft().getTree();
                Tree rightTree = linear.getRight().getTree();
                removeTypeCast(leftTree);
                removeTypeCast(rightTree);
                if (leftTree != null && (leftTree instanceof MethodInvocationTree 
                            || leftTree instanceof NewClassTree
                            || leftTree instanceof BinaryTree 
                            || leftTree instanceof LiteralTree)
                        && rightTree != null && !(linear.getRight() instanceof AdaptReference)
                            && (rightTree instanceof MethodInvocationTree 
                            || leftTree instanceof NewClassTree
                            || leftTree instanceof BinaryTree 
                            || leftTree instanceof LiteralTree)
                        && !rightTree.toString().contains(leftTree.toString()))
                    continue;
//                if (!canConnectVia(linear.getLeft(), linear.getRight()))
//                    continue;

//                if (linear.getLeft().getId() == 509811 && linear.getRight().getId() == 428153) 
//                    System.out.println();

                // Should be in the same file or they are subclasses
//                String leftFile = linear.getLeft().getFileName();
//                String rightFile = linear.getRight().getFileName();
                TypeElement leftEType = linear.getLeft().getEnclosingType();
                TypeElement rightEType = linear.getRight().getEnclosingType();
                if (!c.equals(linear) && !cons.contains(linear)
                        && linear.getLeft().getId() != linear.getRight().getId()
                        && !tmpNewConstraints.contains(linear) 
                        && !newCons.contains(linear)
//                        && (leftFile == null || rightFile == null || leftFile.equals(rightFile))
                        && (leftEType == null || rightEType == null 
                            || inferenceChecker.isSubtype(leftEType, rightEType)
                            || inferenceChecker.isSubtype(rightEType, leftEType))
                        ) {
                    newCons.add(linear);
                    linear.getLeft().addGreaterRef(linear.getRight());
                    linear.getRight().addLessRef(linear.getLeft());
                    buildRefToConstraintMapping(linear);
                    queue.add(linear);
                } else if (!(linear.getLeft() instanceof AdaptReference) 
                        && (linear.getRight() instanceof MethodAdaptReference)) 
                    queue.add(linear); // add method adapt constraint
            }
        }
        return newCons;
    }


    public List<Constraint> getUpdatedConstraints() {
        return new ArrayList<Constraint>(constraints);
    }

}
