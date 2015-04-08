/**
 * 
 */
package checkers.inference2.reimN;

import com.sun.source.tree.CompilationUnitTree;

import checkers.inference2.InferenceAnnotatedTypeFactory;

/**
 * @author huangw5
 *
 */
public class ReimNAnnotatedTypeFactory extends InferenceAnnotatedTypeFactory {

	public ReimNAnnotatedTypeFactory(ReimNChecker checker,
			CompilationUnitTree root) {
		super(checker, root);
	}
	
}
