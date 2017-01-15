package checkers.inference2.rely;

import checkers.inference2.InferenceTypeVisitor;

import com.sun.source.tree.CompilationUnitTree;

public class RelyVisitor extends InferenceTypeVisitor<RelyChecker> {

	public RelyVisitor(RelyChecker checker, CompilationUnitTree root) {
		super(checker, root);
		// TODO Auto-generated constructor stub
	}

}
