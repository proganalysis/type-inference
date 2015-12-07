package checkers.inference2.jcrypt2;

import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.tools.javac.tree.JCTree.JCBinary;
import com.sun.tools.javac.tree.JCTree.Tag;

import checkers.inference2.InferenceChecker;
import checkers.inference2.InferenceVisitor;
import checkers.inference2.Reference;

public class Jcrypt2InferenceVisitor extends InferenceVisitor {
	
	//public static Map<ExpressionTree, Reference> memberSelectRefs = new HashMap<>();
	
	public Jcrypt2InferenceVisitor(InferenceChecker checker,
			CompilationUnitTree root) {
		super(checker, root);
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
		if (!checker.containsAnno(leftRef, ((Jcrypt2Checker) checker).CLEAR)
				|| ((JCBinary) bTree).getTag() != Tag.MUL) {
			checker.addSubtypeConstraint(leftRef, ref, checker.getPosition(left));
		}
		if (!checker.containsAnno(rightRef, ((Jcrypt2Checker) checker).CLEAR)
				|| ((JCBinary) bTree).getTag() != Tag.MUL) {
			checker.addSubtypeConstraint(rightRef, ref, checker.getPosition(right));
		}
		if (lhsRef != null) {
			checker.addSubtypeConstraint(ref, lhsRef, checker.getPosition(left));
		}
		generateConstraint(leftRef, left);
		generateConstraint(rightRef, right);
    }
		
}
