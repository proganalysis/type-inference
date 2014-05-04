/**
 * 
 */
package checkers.inference2.reim;

import com.sun.source.tree.CompilationUnitTree;

import checkers.basetype.BaseTypeVisitor;

/**
 * @author huangw5
 *
 */
public class ReimVisitor extends BaseTypeVisitor<ReimChecker> {

	public ReimVisitor(ReimChecker checker, CompilationUnitTree root) {
		super(checker, root);
		// TODO Auto-generated constructor stub
	}

}
