package checkers.inference2.jcrypt;

import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.CompoundAssignmentTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.UnaryTree;

import checkers.inference2.InferenceChecker;
import checkers.inference2.InferenceVisitor;
import checkers.inference2.Reference;

public class JcryptInferenceVisitor extends InferenceVisitor {
	
	public JcryptInferenceVisitor(InferenceChecker checker,
			CompilationUnitTree root) {
		super(checker, root);
	}
	
	@Override
	public Void visitCompoundAssignment(CompoundAssignmentTree node, Void p) {
		if (!visited.contains(node)) {
	        ExpressionTree var = node.getVariable();
	        ExpressionTree expr = node.getExpression();
	        Reference ref = checker.getAnnotatedReference(node);
	        Reference varRef = checker.getAnnotatedReference(var);
	        Reference exprRef = checker.getAnnotatedReference(expr);
	        checker.addSubtypeConstraint(varRef, ref, ref.getLineNum());
	        checker.addSubtypeConstraint(exprRef, ref, ref.getLineNum());
		}
		return super.visitCompoundAssignment(node, p);
	}
	
	@Override
    public Void visitBinary(BinaryTree node, Void p) {
		if (!visited.contains(node)) {
			processBinaryTree(null, node);
		}
		return super.visitBinary(node, p);
	}
	
	@Override
	public void processBinaryTree(Reference lhsRef, BinaryTree bTree) {
		Reference ref = checker.getAnnotatedReference(bTree);
		ExpressionTree left = bTree.getLeftOperand();
		ExpressionTree right = bTree.getRightOperand();
		Reference leftRef = checker.getAnnotatedReference(left);
		Reference rightRef = checker.getAnnotatedReference(right);
		checker.addSubtypeConstraint(leftRef, ref, ref.getLineNum());
		checker.addSubtypeConstraint(rightRef, ref, ref.getLineNum());
		if (lhsRef != null) {
			checker.addSubtypeConstraint(ref, lhsRef, ref.getLineNum());
		}
		generateConstraint(leftRef, left);
		generateConstraint(rightRef, right);
    }
	
	@Override
    public Void visitUnary(UnaryTree node, Void p) {
		if (!visited.contains(node)) {
			processUnaryTree(null, node);
		}
		return super.visitUnary(node, p);
	}
	
	@Override
	public void processUnaryTree(Reference lhsRef, UnaryTree uTree) {
		Reference treeRef = checker.getAnnotatedReference(uTree);
		ExpressionTree exprTree = uTree.getExpression();
		Reference ref = checker.getAnnotatedReference(exprTree);
		checker.addSubtypeConstraint(ref, treeRef, checker.getLineNumber(uTree));
		if (lhsRef != null) {
			checker.addSubtypeConstraint(treeRef, lhsRef, treeRef.getLineNum());
		}
		generateConstraint(ref, exprTree);
    }

}
