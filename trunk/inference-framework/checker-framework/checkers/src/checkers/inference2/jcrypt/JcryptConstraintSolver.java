/**
 * 
 */
package checkers.inference2.jcrypt;

import java.util.BitSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TypeCastTree;
import com.sun.source.tree.Tree.Kind;

import checkers.inference2.Constraint;
import checkers.inference2.Constraint.SubtypeConstraint;
import checkers.inference2.Reference;
import checkers.inference2.Reference.AdaptReference;
import checkers.inference2.Reference.FieldAdaptReference;
import checkers.inference2.Reference.MethodAdaptReference;
import checkers.inference2.SetbasedSolver;
import checkers.util.TreeUtils;
import static com.esotericsoftware.minlog.Log.*;

/**
 * @author huangw5
 * 
 */
public class JcryptConstraintSolver extends SetbasedSolver {

	private JcryptChecker checker;
	private Set<Constraint> constraints;

	public JcryptConstraintSolver(JcryptChecker t) {
		super(t);
		this.checker = t;
		constraints = checker.getConstraints();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see checkers.inference2.AbstractConstraintSolver#solveImpl()
	 */
	@Override
	protected Set<Constraint> solveImpl() {
		Set<Constraint> conflictCons = super.solveImpl();
		BitSet warnConstraints = new BitSet(Constraint.maxId());
		Set<Constraint> newConstraints = new LinkedHashSet<Constraint>();
		worklist.addAll(constraints);
		while (!worklist.isEmpty()) {
			Iterator<Constraint> it = worklist.iterator();
			Constraint c = it.next();
			it.remove();
			try {
				handleConstraint(c);
				Set<Constraint> newCons = addLinearConstraints(c, constraints,
					newConstraints);
//				for (Constraint con : newCons) {
//					System.out.println(con.toString());
//				}
				if (!newCons.isEmpty()) {
					worklist.addAll(newCons);
					worklist.addAll(constraints);
				}
				
				newConstraints.addAll(newCons);

			} catch (SolverException e) {
				FailureStatus fs = checker.getFailureStatus(c);
				if (fs == FailureStatus.ERROR) {
					conflictCons.add(c);
				} else if (fs == FailureStatus.WARN) {
					if (!warnConstraints.get(c.getId())) {
						warn(this.getClass().getSimpleName(),
								"Failed handling constraint " + c);
						warnConstraints.set(c.getId());
					}
				}
			}
		}
		return conflictCons;
	}

	private boolean canConnectVia(Reference left, Reference right) {
		if (left == null || right == null)
			return false;
		TypeElement leftEType = left.getEnclosingType();
		TypeElement rightEType = right.getEnclosingType();
		if (leftEType != null && rightEType != null
				&& !checker.isSubtype(leftEType, rightEType)
				&& !checker.isSubtype(rightEType, leftEType))
			return false;

		Tree leftTree = left.getTree();
		Tree rightTree = right.getTree();
		if (leftTree != null
				&& (leftTree instanceof MethodInvocationTree
						|| leftTree instanceof NewClassTree
						|| leftTree instanceof BinaryTree || leftTree instanceof LiteralTree)
				&& rightTree != null
				&& !(right instanceof AdaptReference)
				&& (rightTree instanceof MethodInvocationTree
						|| leftTree instanceof NewClassTree
						|| leftTree instanceof BinaryTree || leftTree instanceof LiteralTree))
			return false;

		// no internal elements of arrays
		if (!(left instanceof AdaptReference)
				&& left.getName().equals("#INTERNAL#")
				|| !(right instanceof AdaptReference)
				&& right.getName().equals("#INTERNAL#"))
			return false;
		// no subclass constraints
		// Nov 29, 2013: Actuall we need such constraints. For the
		// abstract method String getSQLString() in hibernate, we want
		// to connect THIS and RET because one of its subclasses
		// connects THIS and RET
		Element leftElt = null, rightElt = null;
		if ((leftElt = left.getElement()) != null
				&& (rightElt = right.getElement()) != null) {
			if (leftElt.getKind() == ElementKind.PARAMETER
					&& rightElt.getKind() == ElementKind.PARAMETER)
				return false; // both are parameters
			if (leftElt instanceof ExecutableElement
					&& rightElt instanceof ExecutableElement
					&& leftElt.toString().equals(rightElt.toString())) {
				if (left.getName().startsWith("THIS_")
						&& right.getName().startsWith("THIS_")
						|| left.getName().startsWith("RET_")
						&& right.getName().startsWith("RET_"))
					return false; // both are THIS or RET
			}
		}
		return true;
	}

	// Add new linear constraint c, return a new set
	private Set<Constraint> addLinearConstraints(Constraint con,
			Set<Constraint> cons, Set<Constraint> tmpNewConstraints) {
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
			if (left instanceof FieldAdaptReference
					&& (ref = ((FieldAdaptReference) left).getDeclRef()) != null
					&& ref.getAnnotations(checker).size() == 1
					&& ref.getAnnotations(checker).contains(checker.POLY)) {
				Constraint linear = new SubtypeConstraint(
						((FieldAdaptReference) left).getContextRef(), right);
				tmplist.add(linear);
			}
			// Step 2: field write
			// Nov 26, 2013: add linear constraint for array fields
			else if ((right instanceof FieldAdaptReference)
					&& (ref = ((FieldAdaptReference) right).getDeclRef()) != null
					&& ref.getAnnotations(checker).size() == 1
					&& ref.getAnnotations(checker).contains(checker.POLY)) {
				Constraint linear = new SubtypeConstraint(left,
						((FieldAdaptReference) right).getContextRef());
				tmplist.add(linear);
			}
			// Step 3: linear constraints
			else if (!(left instanceof AdaptReference)
					&& !(right instanceof AdaptReference)
					&& canConnectVia(left, right)) {
				for (Constraint lc : left.getLessSet()) {
					Reference r = lc.getLeft();
					if (!r.equals(right)
							&& (right.getElement() != null || r.getElement() != null)
							&& checker.isParamOrRetRef(r)) {
						Constraint linear = new SubtypeConstraint(r, right);
						tmplist.add(linear);
					}
				}
				if (checker.isParamOrRetRef(left)) {
					for (Constraint gc : right.getGreaterSet()) {
						Reference r = gc.getRight();
						if (!left.equals(r)
								&& (left.getElement() != null || r.getElement() != null)) {
							Constraint linear = new SubtypeConstraint(left, r);
							tmplist.add(linear);
						}
					}
				}
				// if c is a new linear constraint between parameters
				// and returns, add it into original constraints
				if (!constraints.contains(c)
						&& checker.isParamReturnConstraint(c)) {
					// param/this -> return/param/this
					constraints.add(c);
				}
				// look for method adapt constraint
				Set<Reference> contextSetLeft = declRefToContextRefs.get(left
						.getId());
				Set<Reference> contextSetRight = declRefToContextRefs.get(right
						.getId());
				if (contextSetLeft != null && contextSetRight != null) {
					contextSetLeft.retainAll(contextSetRight);
					if (!contextSetLeft.isEmpty()) {
						Set<Constraint> relatedCons = refToConstraints.get(left
								.getId());
						for (Constraint related : relatedCons) {
							if (!(related.getLeft() instanceof AdaptReference)
									&& (related.getRight() instanceof MethodAdaptReference)
									&& contextSetLeft
											.contains(((MethodAdaptReference) related
													.getRight())
													.getContextRef()))
								tmplist.add(related);
						}
					}
				}
			}
			// step 4: z <: (y |> par)
			else if (!(left instanceof AdaptReference)
					&& (right instanceof MethodAdaptReference)) {
				Reference parRef = ((MethodAdaptReference) right).getDeclRef();
				Reference rcvRef = ((MethodAdaptReference) right)
						.getContextRef();
				Set<Constraint> parCons = parRef.getGreaterSet();
				for (Constraint parCon : parCons) {
					Reference parPrime = parCon.getRight();
					MethodAdaptReference mr = new MethodAdaptReference(rcvRef,
							parPrime);
					List<Constraint> adaptCons = adaptRefToConstraints.get(mr
							.getName());
					if (adaptCons != null) {
						for (Constraint adaptCon : adaptCons) {
							if (adaptCon.getLeft().equals(mr)) {
								Constraint linear = new SubtypeConstraint(left,
										adaptCon.getRight());
								tmplist.add(linear);
							}
						}
					}
				}
			}
			for (Constraint linear : tmplist) {
				Tree leftTree = linear.getLeft().getTree();
				Tree rightTree = linear.getRight().getTree();
				removeTypeCast(leftTree);
				removeTypeCast(rightTree);
				if (leftTree != null
						&& (leftTree instanceof MethodInvocationTree
								|| leftTree instanceof NewClassTree
								|| leftTree instanceof BinaryTree || leftTree instanceof LiteralTree)
						&& rightTree != null
						&& !(linear.getRight() instanceof AdaptReference)
						&& (rightTree instanceof MethodInvocationTree
								|| leftTree instanceof NewClassTree
								|| leftTree instanceof BinaryTree || leftTree instanceof LiteralTree)
						&& !rightTree.toString().contains(leftTree.toString()))
					continue;

				// Should be in the same file or they are subclasses
				TypeElement leftEType = linear.getLeft().getEnclosingType();
				TypeElement rightEType = linear.getRight().getEnclosingType();
				if (!c.equals(linear)
						&& !cons.contains(linear)
						&& linear.getLeft().getId() != linear.getRight()
								.getId()
						&& !tmpNewConstraints.contains(linear)
						&& !newCons.contains(linear)
						&& (leftEType == null || rightEType == null
								|| checker.isSubtype(leftEType, rightEType) || checker
									.isSubtype(rightEType, leftEType))) {
					// if leftEType != rightEType, then it should be
					// subclassing constraint
					Element leftElt, rightElt;
					if (leftEType != null
							&& rightEType != null
							&& leftEType.equals(rightEType)
							|| (leftElt = linear.getLeft().getElement()) != null
							&& (rightElt = linear.getRight().getElement()) != null
							&& leftElt.getKind() == rightElt.getKind()) {
						newCons.add(linear);
						buildRefToConstraintMapping(linear);
						queue.add(linear);
					}
				} else if (!(linear.getLeft() instanceof AdaptReference)
						&& (linear.getRight() instanceof MethodAdaptReference))
					queue.add(linear); // add method adapt constraint
			}
		}
		return newCons;
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

}
