package checkers.inference2.sflow;

import checkers.inference2.InferenceTypeVisitor;

import com.sun.source.tree.CompilationUnitTree;

public class SFlowVisitor extends InferenceTypeVisitor<SFlowChecker> {

	public SFlowVisitor(SFlowChecker checker, CompilationUnitTree root) {
		super(checker, root);
		// TODO Auto-generated constructor stub
	}

}
