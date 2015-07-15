package checkers.inference2.jcrypt2;

import com.sun.source.tree.CompilationUnitTree;
import checkers.inference2.InferenceChecker;
import checkers.inference2.jcrypt.JcryptInferenceVisitor;

public class Jcrypt2InferenceVisitor extends JcryptInferenceVisitor {
	
	public Jcrypt2InferenceVisitor(InferenceChecker checker,
			CompilationUnitTree root) {
		super(checker, root);
	}
	
//	@Override
//    protected void processArrayAccess(Reference lhsRef, ArrayAccessTree aaTree, Reference rhsRef) {
//		super.processArrayAccess(lhsRef, aaTree, rhsRef);
//		ExpressionTree aaExpr = aaTree.getExpression();
//		Reference exprRef = checker.getAnnotatedReference(aaExpr);
//
//		// Get the component reference of this array access
//		Reference componentRef = ((ArrayReference) exprRef).getComponentRef();
//		//checker.addSubtypeConstraint(exprRef, componentRef, checker.getPosition(exprRef));
//		//checker.addSubtypeConstraint(componentRef, exprRef, checker.getPosition(componentRef));
//    }
	
}
