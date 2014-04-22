/**
 * 
 */
package checkers.inference;

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
import checkers.inference.Reference.AdaptReference;
import checkers.inference.Reference.ArrayReference;
import checkers.inference.SetbasedSolver.SetbasedSolverException;
import checkers.inference.sflow.SFlowChecker;
import checkers.inference.sflow.WorklistSetbasedSolver;
import checkers.types.AnnotatedTypeMirror;
import checkers.util.ElementUtils;

/**
 * @author huangw5
 *
 */
public class MaximalTypingExtractor implements TypingExtractor {
	
	private InferenceChecker inferenceChecker;
	
	private List<Reference> exprRefs;
	
	private Map<String, Reference> maximalSolution = null;
	
	private List<Constraint> constraints = null;
	
	private Map<Integer, List<Constraint>> refToConstraints; 
	
	private int minId = Integer.MAX_VALUE;
	
	public MaximalTypingExtractor(InferenceChecker inferenceChecker,
			List<Reference> exprRefs, List<Constraint> constraints) {
		super();
		this.inferenceChecker = inferenceChecker;
		this.exprRefs = exprRefs;
		this.constraints = constraints;
		this.refToConstraints = new HashMap<Integer, List<Constraint>>();
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
						if (id < minId)
							minId = id;
					}
				}
			}
		}
//		int min = Integer.MAX_VALUE;
//		int max = Integer.MIN_VALUE;
//		int sum = 0;
//		try {
//			PrintWriter pw = new PrintWriter(
//					InferenceMain.outputDir + File.separator
//							+ "refToConstraints.log");
//			for (Entry<Integer, List<Constraint>> entry : refToConstraints.entrySet()) {
//				int size = entry.getValue().size();
//				if (min > size)
//					min = size;
//				if (max < size)
//					max = size;
//				sum += size;
//				pw.println(entry.getKey());
//			}
//			pw.close();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.out.println("INFO: REFS = " + refToConstraints.size() + " MIN = " + min + " MAX = " + max + " AVG = "
//				+ (sum / refToConstraints.size()));
	}
	
//	private List<Constraint> solveReleventConstraints(Reference reference) {
//		buildRefToConstraintMapping();
//		Set<Constraint> cons = new HashSet<Constraint>();
//		Set<Integer> visited = new HashSet<Integer>();
////		BitSet bitset = new BitSet(refToConstraints.size());
//		Queue<Reference> queue = new LinkedBlockingDeque<Reference>();
//		queue.add(reference);
//		visited.add(reference.id);
////		bitset.set(reference.id - minId);
//		while (!queue.isEmpty()) {
//			Reference ref = queue.poll();
//			List<Constraint> cs = refToConstraints.get(ref.id);
//			if (cs != null && !cs.isEmpty()) {
//				cons.addAll(cs);
//				for (Constraint c : cs) {
//					Reference left = null, right = null;
//					if (c instanceof SubtypeConstraint
//							|| c instanceof EqualityConstraint
//							|| c instanceof UnequalityConstraint) {
//						left = c.getLeft();
//						right = c.getRight();
//					}
//					if (left != null && right != null) {
//						Reference[] refs = { left, right };
//						for (Reference r : refs) {
//							if (r instanceof AdaptReference) {
//								Reference declRef = ((AdaptReference) r)
//										.getDeclRef();
//								Reference contextRef = ((AdaptReference) r)
//										.getContextRef();
//								if (!visited.contains(Integer.valueOf(declRef.id))) {
////								if (!bitset.get(declRef.id - minId)) {
//									queue.add(declRef);
////									bitset.set(declRef.id - minId);
//									visited.add(declRef.id);
//								}
//								if (!visited.contains(Integer.valueOf(contextRef.id))) {
////								if (!bitset.get(contextRef.id - minId)) {
//									queue.add(contextRef);
////									bitset.set(contextRef.id - minId);
//									visited.add(contextRef.id);
//								}
//							} else {
//								if (!visited.contains(Integer.valueOf(r.id))) {
////								if (!bitset.get(r.id - minId)) {
//									queue.add(r);
////									bitset.set(r.id - minId);
//									visited.add(r.id);
//								}
//							}
//						}
//					}
//				}
//			}
//		}
//		System.out.println("INFO: Constraints relevent to " + reference.toString()
//				+ ": " + cons.size());
//		List<Reference> copyRefs = copyReferences(exprRefs);
//		List<Constraint> releventCons = new ArrayList<Constraint>(cons.size());
//		for (Constraint con : cons)
//			releventCons.add(con);
//		SetbasedSolver solver = new SetbasedSolver(inferenceChecker,
//				exprRefs, releventCons);
//		List<Constraint> conflictConstraints = solver.solve();
//		if (!conflictConstraints.isEmpty()) {
//			// Recover the annotations
//			recoverReferences(exprRefs, copyRefs);
//		}
//		return conflictConstraints;
//	}
	
	private enum ConflictType {
		RECEIVER, 
		FIELD, 
		RETURN,
		PARAMETER,
		ARRAY_LEN
	}
	
	private boolean solveRelevantConstraints(Reference reference) {
		Set<Constraint> cons = new HashSet<Constraint>();
		Set<Integer> visited = new HashSet<Integer>();
		Queue<Reference> queue = new LinkedBlockingDeque<Reference>();
		queue.add(reference);
		visited.add(reference.getId());
		boolean b = InferenceChecker.DEBUG;
		InferenceChecker.DEBUG = false;
		List<Reference> copyRefs = copyReferences(exprRefs);
		SetbasedSolver solver = new SetbasedSolver(inferenceChecker,
				exprRefs, constraints);
		boolean hasError = false;
//		System.out.println("INFO: solving relevant constraints");
		while (!queue.isEmpty() && !hasError) {
			Reference ref = queue.poll();
			List<Constraint> cs = refToConstraints.get(ref.getId());
			if (cs != null && !cs.isEmpty()) {
				cons.addAll(cs);
				for (Constraint c : cons) {
					try {
						if (!solver.handleConstraint(c))
							continue;
					} catch (SetbasedSolverException e) {
						System.err.println(e.getMessage());
						hasError = true;
						break;
					}
					Reference left = null, right = null;
					if (c instanceof SubtypeConstraint
							|| c instanceof EqualityConstraint
							|| c instanceof UnequalityConstraint) {
						left = c.getLeft();
						right = c.getRight();
					}
					if (left != null && right != null) {
						Reference[] refs = { left, right };
						for (Reference r : refs) {
							if (r instanceof AdaptReference) {
								Reference declRef = ((AdaptReference) r)
										.getDeclRef();
								Reference contextRef = ((AdaptReference) r)
										.getContextRef();
								if (!visited.contains(Integer.valueOf(declRef.getId()))) {
									queue.add(declRef);
									visited.add(declRef.getId());
								}
								if (!visited.contains(Integer.valueOf(contextRef.getId()))) {
									queue.add(contextRef);
									visited.add(contextRef.getId());
								}
							} else {
								if (!visited.contains(Integer.valueOf(r.getId()))) {
									queue.add(r);
									visited.add(r.getId());
								}
							}
						}
					}
				}
			}
		}
		recoverReferences(exprRefs, copyRefs);
//		System.out.println("INFO: finish solving relevant constraints");
//		System.out.println("INFO: Constraints relevant to " + reference.toString()
//				+ ": " + cons.size());
		InferenceChecker.DEBUG = b;
		List<Constraint> relevantCons = new ArrayList<Constraint>(cons.size());
		for (Constraint con : cons)
			relevantCons.add(con);
//		SetbasedSolver solver2 = new SetbasedSolver(inferenceChecker,
//				exprRefs, relevantCons);
		WorklistSetbasedSolver solver2 = new WorklistSetbasedSolver(inferenceChecker,
				exprRefs, relevantCons);
		List<Constraint> conflictConstraints = solver2.solve();
		if (!conflictConstraints.isEmpty()) {
			// Recover the annotations
			recoverReferences(exprRefs, copyRefs);
		}
		return conflictConstraints.isEmpty();
	}	
	
	private int resolveFieldConflicts(List<Constraint> conflictConstraints) {
//		return resolve(conflictConstraints, ConflictType.FIELD);
		int num = 0;
		for (Iterator<Constraint> it = conflictConstraints.iterator(); it.hasNext();) {
			Constraint c = it.next();
			Reference left = c.getLeft();
			Reference right = c.getRight();
			Reference declRef, contextRef, outRef;
			if (left instanceof AdaptReference) {
				outRef = right;
				declRef = ((AdaptReference) left).getDeclRef();
				contextRef = ((AdaptReference) left).getContextRef();
			} else if (right instanceof AdaptReference){
				outRef = left;
				declRef = ((AdaptReference) right).getDeclRef();
				contextRef = ((AdaptReference) right).getContextRef();
			} else {
				// No adapt constraint
				continue;
			}
			Element declElt = declRef.getElement();
			
			if (outRef.getAnnotations().size() == 1 
					&& outRef.getAnnotations().contains(SFlowChecker.SAFE)
					&& ((declElt != null && !ElementUtils.isStatic(declElt) 
							&& declElt.getKind() == ElementKind.FIELD)
						|| 
						(declElt == null && contextRef instanceof ArrayReference)
						)
					&& declRef.getAnnotations().size() > 1
					&& declRef.getAnnotations().contains(SFlowChecker.POLY)
					&& declRef.getAnnotations().contains(SFlowChecker.SAFE)) {
				if (declElt != null && declElt.toString().equals("length")
						&& contextRef instanceof ArrayReference) {
					// This is array.length, skip
					continue;
				}
				
				if (inferenceChecker instanceof SFlowChecker
						&& ((SFlowChecker) inferenceChecker).isInferLibrary()
						&& declRef.getType() != null
						&& !((SFlowChecker) inferenceChecker).isTaintableType(declRef.getType())) {
					// NOT taintable, skip
					continue;
				}
				
				
//				System.out.print("RESO: " + c.toString());
				Set<AnnotationMirror> annos = declRef.getAnnotations();
				annos.clear();
				if (inferenceChecker instanceof SFlowChecker
						&& !(contextRef instanceof ArrayReference)
						&& ((SFlowChecker) inferenceChecker).isInferLibrary()) {
					annos.add(SFlowChecker.SAFE);
				} else {
					annos.add(SFlowChecker.POLY);
				}
				if (InferenceChecker.DEBUG)
					logTrace(declRef, annos, c);
				declRef.setAnnotations(annos);
//				System.out.println(" Setting FIELD to " + declRef.getAnnotations());
				num++;
			}
		}
		return num;
	}
	
	private int resolveArrayLenConflicts(List<Constraint> conflictConstraints) {
		int num = 0;
		for (Constraint c : conflictConstraints) {
			Reference left = c.getLeft();
			Reference right = c.getRight();
			Reference declRef, contextRef, outRef;
			if (left instanceof AdaptReference) {
				outRef = right;
				declRef = ((AdaptReference) left).getDeclRef();
				contextRef = ((AdaptReference) left).getContextRef();
			} else if (right instanceof AdaptReference){
				outRef = left;
				declRef = ((AdaptReference) right).getDeclRef();
				contextRef = ((AdaptReference) right).getContextRef();
			} else {
				// No adapt constraint
				continue;
			}
			Element declElt = declRef.getElement();
			
			if (outRef.getAnnotations().size() == 1 
					&& outRef.getAnnotations().contains(SFlowChecker.SAFE)
					&& declElt != null && declElt.getKind() == ElementKind.FIELD
					&& declRef.getAnnotations().size() > 1
					&& declRef.getAnnotations().contains(SFlowChecker.POLY)
					&& declRef.getAnnotations().contains(SFlowChecker.SAFE)) {
				// set declRef as SAFE
				if (declElt != null && declElt.toString().equals("length")
						&& contextRef instanceof ArrayReference) {
					// This is array.length
					Set<AnnotationMirror> annos = declRef.getAnnotations();
					annos.clear();
					annos.add(SFlowChecker.POLY);
					declRef.setAnnotations(annos);
					num++;
				}
			}
		}
		return num;
	}
	
	private int resolveParameterConflicts(List<Constraint> conflictConstraints) {
//		return resolve(conflictConstraints, ConflictType.PARAMETER);
		int num = 0;
		for (Constraint c : conflictConstraints) {
			Reference left = c.getLeft();
			Reference right = c.getRight();
			Reference declRef, contextRef, outRef;
			if (left instanceof AdaptReference) {
				outRef = right;
				declRef = ((AdaptReference) left).getDeclRef();
				contextRef = ((AdaptReference) left).getContextRef();
			} else if (right instanceof AdaptReference){
				outRef = left;
				declRef = ((AdaptReference) right).getDeclRef();
				contextRef = ((AdaptReference) right).getContextRef();
			} else {
				// No adapt constraint
				continue;
			}
//			Element contextElt = contextRef.getElement();
			Element declElt = declRef.getElement();
			
			
			if (outRef.getAnnotations().size() == 1 
					&& outRef.getAnnotations().contains(SFlowChecker.SAFE)
					&& contextRef.getAnnotations().contains(SFlowChecker.SAFE)
					&& contextRef.getAnnotations().contains(SFlowChecker.POLY)
					&& (declElt == null || declElt.getKind() == ElementKind.PARAMETER)
					&& declRef.getAnnotations().size() > 1
					&& declRef.getAnnotations().contains(SFlowChecker.POLY)
					&& declRef.getAnnotations().contains(SFlowChecker.SAFE)) {
				
				if (inferenceChecker instanceof SFlowChecker
						&& ((SFlowChecker) inferenceChecker).isInferLibrary()
						&& declRef.getType() != null
						&& !((SFlowChecker) inferenceChecker).isTaintableType(declRef.getType())) {
					// NOT taintable, skip
					continue;
				}
				
				Reference copy = declRef.getCopy();
				
				// set declRef as POLY 
//				System.out.print("RESO: " + c.toString());
				Set<AnnotationMirror> annos = declRef.getAnnotations();
				annos.clear();
//				if (declElt == null || !ElementUtils.isStatic(declElt.getEnclosingElement())) {
//					// Nonstatic
//					annos.add(SFlowChecker.SAFE);
//				} else {
//					annos.add(SFlowChecker.POLY);
//				}
				
				if (inferenceChecker instanceof SFlowChecker
						&& ((SFlowChecker) inferenceChecker).isInferLibrary()) {
					annos.add(SFlowChecker.SAFE);
					declRef.setAnnotations(annos);
				}
				else {	
					// First try setting POLY
					annos.add(SFlowChecker.POLY);
					declRef.setAnnotations(annos);
	//				List<Constraint> conflicts = solveReleventConstraints(declRef);
					if (!solveRelevantConstraints(declRef)) {
						// set declRef as SAFE
						System.out.println("WARN: Setting " + declRef + " to POLY failed.");
						annos.clear();
						annos.add(SFlowChecker.SAFE);
						declRef.setAnnotations(annos);
	//					if (!solveRelevantConstraints(declRef)) {
	//						System.out.println("ERROR: Setting either POLY or SAFE failed! " + c);
	//					}
					}
				}
//				System.out.println(" Setting PARAM to " + declRef.getAnnotations());
				if (InferenceChecker.DEBUG)
					logTrace(copy, annos, c);
				num++;
			}
		}
		return num;
	}
	
	private int resolveReturnConflicts(List<Constraint> conflictConstraints) {
//		return resolve(conflictConstraints, ConflictType.RETURN);
		int num = 0;
		for (Constraint c : conflictConstraints) {
			Reference left = c.getLeft();
			Reference right = c.getRight();
			Reference declRef, contextRef, outRef;
			if (left instanceof AdaptReference) {
				outRef = right;
				declRef = ((AdaptReference) left).getDeclRef();
				contextRef = ((AdaptReference) left).getContextRef();
			} else if (right instanceof AdaptReference){
				outRef = left;
				declRef = ((AdaptReference) right).getDeclRef();
				contextRef = ((AdaptReference) right).getContextRef();
			} else {
				// No adapt constraint
				continue;
			}
			Element contextElt = contextRef.getElement();
			Element declElt = declRef.getElement();
			
			if (outRef.getAnnotations().size() == 1 
					&& outRef.getAnnotations().contains(SFlowChecker.SAFE)
					&& declElt != null 
					&& declElt.getKind() == ElementKind.METHOD
					&& declRef.getRefName().startsWith("RET_")
					&& declRef.getAnnotations().size() > 1
					&& declRef.getAnnotations().contains(SFlowChecker.POLY) 
					&& declRef.getAnnotations().contains(SFlowChecker.SAFE)) {
				// FIXME: move resolution to checker
				
				if (inferenceChecker instanceof SFlowChecker
						&& ((SFlowChecker) inferenceChecker).isInferLibrary()
						&& declRef.getType() != null
						&& !((SFlowChecker) inferenceChecker).isTaintableType(declRef.getType())) {
					// NOT taintable, skip
					continue;
				}
				
				Reference copy = declRef.getCopy();
				
//				System.out.print("RESO: " + c.toString());
				Set<AnnotationMirror> annos = declRef.getAnnotations();
				annos.clear();
				if (inferenceChecker instanceof SFlowChecker
						&& ((SFlowChecker) inferenceChecker).isInferLibrary()) {
//					Set<AnnotationMirror> annos = contextRef.getAnnotations();
//					annos.clear();
//					// Set context to SAFE
//					annos.add(SFlowChecker.SAFE);
//					contextRef.setAnnotations(annos);
					// First try setting POLY
					annos.add(SFlowChecker.SAFE);
					declRef.setAnnotations(annos);
				}
				else {
					// First try setting POLY
					annos.add(SFlowChecker.POLY);
					declRef.setAnnotations(annos);
					if (!solveRelevantConstraints(declRef)) {
						// set declRef as SAFE
						System.out.println("WARN: Setting " + declRef + " to POLY failed.");
						annos.clear();
						annos.add(SFlowChecker.SAFE);
						declRef.setAnnotations(annos);
					}
				}
				if (InferenceChecker.DEBUG)
					logTrace(copy, annos, c);
//				System.out.println(" Setting RET to " + declRef.getAnnotations());
				num++;
			}
		}
		return num;
	}
	
	private int resolveReceiverConflicts(List<Constraint> conflictConstraints) {
		int num = 0;
		for (Constraint c : conflictConstraints) {
			Reference left = c.getLeft();
			Reference right = c.getRight();
			Reference declRef, contextRef, outRef;
			if (left instanceof AdaptReference) {
				outRef = right;
				declRef = ((AdaptReference) left).getDeclRef();
				contextRef = ((AdaptReference) left).getContextRef();
			} else if (right instanceof AdaptReference){
				outRef = left;
				declRef = ((AdaptReference) right).getDeclRef();
				contextRef = ((AdaptReference) right).getContextRef();
			} else {
				// No adapt constraint
				continue;
			}
			Element contextElt = contextRef.getElement();
			Element declElt = declRef.getElement();
			
			if (contextRef.getAnnotations().size() > 1 
                    && (contextElt == null || contextElt.getKind() != ElementKind.FIELD)
					&& contextRef.getAnnotations().contains(SFlowChecker.TAINTED)) {
				Set<AnnotationMirror> annos = contextRef.getAnnotations();
				annos.remove(SFlowChecker.TAINTED);
				if (InferenceChecker.DEBUG)
					logTrace(contextRef, annos, c);
				contextRef.setAnnotations(annos);
				num++;
			}
		}
		return num;
	}	
	
	private int resolve(List<Constraint> conflictConstraints, ConflictType cType) {
		if (cType == ConflictType.RECEIVER)
			return resolveReceiverConflicts(conflictConstraints);
		else if (cType == ConflictType.ARRAY_LEN)
			return resolveArrayLenConflicts(conflictConstraints);
		else if (cType == ConflictType.FIELD)
			return resolveFieldConflicts(conflictConstraints);
		int num = 0;
		int partitionSize = 10;
		if (cType == ConflictType.FIELD)
			partitionSize = 100; // FIXME: we know there is no type errors on fields
		List<Reference> partition = new ArrayList<Reference>(partitionSize);
		List<Reference> copyRefs = copyReferences(exprRefs);
		for (Iterator<Constraint> it = conflictConstraints.iterator(); it.hasNext();) {
			Constraint c = it.next();
			Reference left = c.getLeft();
			Reference right = c.getRight();
			Reference declRef, contextRef, outRef;
			if (left instanceof AdaptReference) {
				outRef = right;
				declRef = ((AdaptReference) left).getDeclRef();
				contextRef = ((AdaptReference) left).getContextRef();
			} else if (right instanceof AdaptReference){
				outRef = left;
				declRef = ((AdaptReference) right).getDeclRef();
				contextRef = ((AdaptReference) right).getContextRef();
			} else {
				// No adapt constraint
				continue;
			}
			Element declElt = declRef.getElement();
			
			// TODO: optimize the following
			if (cType == ConflictType.RETURN
					&& outRef.getAnnotations().size() == 1 
					&& outRef.getAnnotations().contains(SFlowChecker.SAFE)
					&& declElt != null 
					&& declElt.getKind() == ElementKind.METHOD
					&& declRef.getRefName().startsWith("RET_")
					&& declRef.getAnnotations().size() > 1
					&& declRef.getAnnotations().contains(SFlowChecker.POLY) 
					&& declRef.getAnnotations().contains(SFlowChecker.SAFE)
					|| cType == ConflictType.PARAMETER
					&& outRef.getAnnotations().size() == 1 
					&& outRef.getAnnotations().contains(SFlowChecker.SAFE)
					&& contextRef.getAnnotations().contains(SFlowChecker.SAFE)
					&& contextRef.getAnnotations().contains(SFlowChecker.POLY)
					&& (declElt == null || declElt.getKind() == ElementKind.PARAMETER)
					&& declRef.getAnnotations().size() > 1
					&& declRef.getAnnotations().contains(SFlowChecker.POLY)
					&& declRef.getAnnotations().contains(SFlowChecker.SAFE)
					) {
				Set<AnnotationMirror> annos = declRef.getAnnotations();
				annos.clear();
				annos.add(SFlowChecker.POLY);
				declRef.setAnnotations(annos);
				num++;
				System.out.println("Resolved: " + declRef.toString());
				partition.add(declRef);
				if (partition.size() == partitionSize || !it.hasNext()) {
					// check if there are any type errors
					boolean b = InferenceChecker.DEBUG;
					InferenceChecker.DEBUG = false;
					SetbasedSolver solver = new SetbasedSolver(inferenceChecker,
							exprRefs, constraints);
					List<Constraint> conflicts = solver.solve();
					InferenceChecker.DEBUG = b;
					if (!conflicts.isEmpty()) {
						// We got type errors, resolve it one by one
						recoverReferences(exprRefs, copyRefs);
						for (Reference ref : partition) {
							// again, first try POLY
							Set<AnnotationMirror> refAnnos = ref.getAnnotations();
							refAnnos.clear();
							refAnnos.add(SFlowChecker.POLY);
							ref.setAnnotations(refAnnos);
							if (!solveRelevantConstraints(ref)) {
								// set declRef as SAFE
								System.out.println("WARN: Setting " + ref + " to POLY failed.");
								refAnnos.clear();
								refAnnos.add(SFlowChecker.SAFE);
								ref.setAnnotations(refAnnos);
							}
						}
					}
					// clear the partition and copy the exprRefs
					partition.clear();
					copyRefs = copyReferences(exprRefs);
				}
			}
		}
		return num;
	}
	
	private FileWriter tracePw = null;
	
	private List<Constraint> resolveConflicts() {
		
		try {
			tracePw = new FileWriter(new File(InferenceMain.outputDir
					+ File.separator + "trace.log"), true);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		List<Constraint> typeErrors =  Collections.emptyList();
		
		int maxTries = 100;
		int t = 0;
		int oldNum = Integer.MAX_VALUE;
		while (t < maxTries) {
			t++;
			List<Reference> copyRefs = copyReferences(exprRefs);
			// Keep only the maximal annotation
			for (Reference ref : exprRefs) {
				ref.setAnnotations(inferenceChecker.getMaximal(ref).getAnnotations());
			}
			// Run the SetbasedSolver again with maximal typing
//			System.out.println("INFO: Looking for conflicts...");
			SetbasedSolver solver = new SetbasedSolver(inferenceChecker,
					exprRefs, constraints);
//            WorklistSetbasedSolver solver = new WorklistSetbasedSolver(inferenceChecker,
//                    exprRefs, constraints);
			List<Constraint> conflictConstraints = solver.solve();
			if (!conflictConstraints.isEmpty()) {
				if (InferenceChecker.DEBUG) {
					System.out.println("WARN: there are " + conflictConstraints.size() + " conflicts");
				}
				// Recover the annotations
				recoverReferences(exprRefs, copyRefs);
				if (!conflictConstraints.isEmpty()) {
					PrintWriter pw;
					try {
						pw = new PrintWriter(InferenceMain.outputDir + File.separator + "set-conflicts.log");
						for (Constraint c : conflictConstraints)
							pw.println(c);
						pw.close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
				// Need to resolve the conflicts
					
				int num = resolveFieldConflicts(conflictConstraints);
				System.out.println("INFO: Resolved " + num + " conflicts on fields");
				if (num == 0) {
					num = resolveReturnConflicts(conflictConstraints);
					System.out.println("INFO: Resolved " + num + " conflicts on returns");
					if (num == 0) {
						num = resolveParameterConflicts(conflictConstraints);
						System.out.println("INFO: Resolved " + num + " conflicts on parameters");
						if (num == 0) {
							num = resolveReceiverConflicts(conflictConstraints);
							System.out.println("INFO: Resolved " + num + " conflicts on receivers");
							if (num == 0) {
								num = resolveArrayLenConflicts(conflictConstraints);
								System.out.println("INFO: Resolved " + num + " conflicts on Array.length");
							}
						}
					}
				}	
				
//				if (conflictConstraints.size() == oldNum && num == 0) {
//					break;
//				} else
//					oldNum = conflictConstraints.size();
				
				// Solve again
				conflictConstraints = solver.solve();
				if (!conflictConstraints.isEmpty()) {
					System.out.println("WARN: Unresolvable constraints: " + conflictConstraints.size());
					PrintWriter pw;
					try {
						pw = new PrintWriter(InferenceMain.outputDir + File.separator + "max-conflicts.log");
						for (Constraint c : conflictConstraints)
							pw.println(c);
						pw.close();
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					typeErrors = conflictConstraints;
//					break;
				}
				if (num == 0)
					break;
			} else {
				System.out.println("INFO: No conflict!");
				typeErrors = Collections.emptyList();
				PrintWriter pw;
				try {
					pw = new PrintWriter(InferenceMain.outputDir + File.separator + "max-conflicts.log");
					pw.println("No conflicts!");
					pw.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
		}

		try {
			if (tracePw != null)
				tracePw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return typeErrors;
	}
	
	private void logTrace(Reference ref, Set<AnnotationMirror> annos, Constraint currentConstraint) {
		StringBuilder sb = new StringBuilder();
		sb.append(ref.getId()).append("|").append(ref.toString().replace("\r", "").replace("\n", "").replace('|', ' ')).append("|");
		sb.append("{" + InferenceUtils.formatAnnotationString(ref.getAnnotations()) + "}|");
		sb.append("{" + InferenceUtils.formatAnnotationString(annos) + "}|");
		sb.append("RESO-" + currentConstraint.toString().replace("\r", "").replace("\n", "").replace('|', ' '));
		sb.append("|NONE|0");
		try {
			tracePw.write(sb.toString().replace('\'', ' ') + "\n");
			tracePw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
//	private boolean resolveConflicts() {
//		int maxTries = 100;
//		int t = 0;
//		int oldNum = Integer.MAX_VALUE;
//		List<Constraint> conflictConstraints = new ArrayList<Constraint>(1);
//		while (t < maxTries) {
//			t++;
//			System.out.println("INFO: Iteration " + t);
//			List<Reference> copyRefs = copyReferences(exprRefs);
//			// Keep only the maximal annotation
//			for (Reference ref : exprRefs) {
//				ref.setAnnotations(inferenceChecker.getMaximal(ref).getAnnotations());
//			}
//			// Run the SetbasedSolver again with maximal typing
//			SetbasedSolver solver = new SetbasedSolver(inferenceChecker,
//					exprRefs, constraints);
//			conflictConstraints = solver.solve();
//			if (!conflictConstraints.isEmpty()) {
//				if (InferenceChecker.DEBUG) {
//					System.out.println("WARN: there are " + conflictConstraints.size() + " conflicts");
//				}
//				// Recover the annotations
//				recoverReferences(exprRefs, copyRefs);
//				if (!conflictConstraints.isEmpty()) {
//					PrintWriter pw;
//					try {
//						pw = new PrintWriter(InferenceMain.outputDir + File.separator + "set-conflicts.log");
//						for (Constraint c : conflictConstraints)
//							pw.println(c);
//						pw.close();
//					} catch (FileNotFoundException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//				if (conflictConstraints.size() == oldNum) {
//					break;
//				} else
//					oldNum = conflictConstraints.size();
//				// Need to resolve the conflicts
//				inferenceChecker.resolveConflictConstraints(conflictConstraints);
//				// Solve again
//				conflictConstraints = solver.solve();
//				if (!conflictConstraints.isEmpty()) {
//					System.out.println("WARN: Unresolvable constraints: " + conflictConstraints.size());
//					PrintWriter pw;
//					try {
//						pw = new PrintWriter(InferenceMain.outputDir + File.separator + "max-conflicts.log");
//						for (Constraint c : conflictConstraints)
//							pw.println(c);
//						pw.close();
//					} catch (FileNotFoundException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//			} else {
//				System.out.println("INFO: No conflict!");
//				break;
//			}
//		}
//		return true;
//	}
	
	
	/**
	 * Build the maximal solution
	 */
	@Override
	public List<Constraint> extractConcreteTyping(int typeErrorNum) {
		// Check whether we need to resolve conflicts
		List<Constraint> typeErrors = Collections.emptyList();
//        if (inferenceChecker.needCheckConflict()
//                && typeErrorNum == 0
//                ) {
//            buildRefToConstraintMapping();
//            typeErrors = resolveConflicts();
//        }
//        else 
			System.out.println("INFO: Skip resolving conflicts");
		
        List<Reference> copyRefs = copyReferences(exprRefs);
		maximalSolution = new HashMap<String, Reference>();
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
            SetbasedSolver solver = new SetbasedSolver(inferenceChecker,
                    exprRefs, constraints);
            List<Constraint> conflictConstraints = solver.solve();
            if (!conflictConstraints.isEmpty()) {
                recoverReferences(exprRefs, copyRefs);
                System.out.println("There are " + conflictConstraints.size()
                        + " conflicts:");
                for (Constraint c : conflictConstraints)
                    System.out.println(c);
            } else 
                System.out.println("No conflicts!");
        }
//        if (InferenceChecker.DEBUG) {
//            try {
//                PrintWriter pw = new PrintWriter(InferenceMain.outputDir
//                        + File.separator + "maximalsolution.log");
//                for (Entry<String, Reference> entry : maximalSolution.entrySet()) {
//                    Reference ref = entry.getValue();
//                    pw.println(entry.getKey() + ": " + ref.toString() + ref.formatAnnotations());
//                }
//                pw.close();
//            } catch (Exception e) {
//            }
//        }
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
				if (ref.id != copy.id)
					throw new RuntimeException("Different ID!");
			}
		}
	}


	@Override
	public Map<String, Reference> getInferredSolution() {
		return maximalSolution;
	}

}
