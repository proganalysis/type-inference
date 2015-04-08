package checkers.inference2.jcrypt2;

import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.CompoundAssignmentTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.IfTree;

import checkers.inference2.InferenceChecker;
import checkers.inference2.InferenceVisitor;
import checkers.inference2.Reference;

public class Jcrypt2InferenceVisitor extends InferenceVisitor {
	
	public Jcrypt2InferenceVisitor(InferenceChecker checker,
			CompilationUnitTree root) {
		super(checker, root);
	}
	
	@Override
	public Void visitCompoundAssignment(CompoundAssignmentTree node, Void p) {
		if (!visited.contains(node)) {
	        ExpressionTree var = node.getVariable();
	        ExpressionTree expr = node.getExpression();
	        Reference varRef = checker.getAnnotatedReference(var);
	        Reference exprRef = checker.getAnnotatedReference(expr);
	        ((Jcrypt2Checker) checker).annotateCompoundAssignmentTree(varRef, node);
	        ((Jcrypt2Checker) checker).annotateCompoundAssignmentTree(exprRef, node);
	        generateConstraint(var, expr);
		}
		return super.visitCompoundAssignment(node, p);
	}
	
	@Override
	public void processBinaryTree(Reference lhsRef, BinaryTree bTree) {
		ExpressionTree left = bTree.getLeftOperand();
		ExpressionTree right = bTree.getRightOperand();
		Reference leftRef = checker.getAnnotatedReference(left);
		Reference rightRef = checker.getAnnotatedReference(right);
		((Jcrypt2Checker) checker).annotateBinaryTree(leftRef, bTree);
		((Jcrypt2Checker) checker).annotateBinaryTree(rightRef, bTree);
		if (lhsRef != null) {
			checker.addSubtypeConstraint(leftRef, lhsRef);
			checker.addSubtypeConstraint(rightRef, lhsRef);
		}
		generateConstraint(leftRef, left);
		generateConstraint(rightRef, right);
    }

	@Override
	public Void visitIf(IfTree node, Void p) {
		ExpressionTree condition = node.getCondition();
		Reference ref = checker.getAnnotatedReference(condition);
		generateConstraint(ref, condition);
		return super.visitIf(node, p);
	}

}
