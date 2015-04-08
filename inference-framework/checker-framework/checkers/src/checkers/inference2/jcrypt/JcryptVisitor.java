package checkers.inference2.jcrypt;

import checkers.inference2.InferenceTypeVisitor;

import com.sun.source.tree.CompilationUnitTree;

public class JcryptVisitor extends InferenceTypeVisitor<JcryptChecker> {

	public JcryptVisitor(JcryptChecker checker, CompilationUnitTree root) {
		super(checker, root);
		// TODO Auto-generated constructor stub
	}

}
