package checkers.inference2.enerj;

import checkers.inference2.InferenceTypeVisitor;

import com.sun.source.tree.CompilationUnitTree;

public class EnerjVisitor extends InferenceTypeVisitor<EnerjChecker> {

	public EnerjVisitor(EnerjChecker checker, CompilationUnitTree root) {
		super(checker, root);
		// TODO Auto-generated constructor stub
	}

}
