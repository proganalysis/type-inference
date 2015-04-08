/**
 * 
 */
package checkers.inference2;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;

import checkers.inference2.ConstraintSolver.FailureStatus;

import static com.esotericsoftware.minlog.Log.*;


/**
 * @author huangw5
 *
 */
public abstract class AbstractTypingExtractor implements TypingExtractor {
	
	protected InferenceChecker checker;
	
	public AbstractTypingExtractor(InferenceChecker c) {
		this.checker = c;
	}
	
	/**
	 * Type-check the current solution.
	 * It assumes there is a singleton annotation set, therefore 
	 * <code>extract</code> is required before calling this method.
	 * @return a list of type errors
	 */
	public List<Constraint> typeCheck() {
		info(this.getClass().getSimpleName(), "Verifying the concrete typing...");
		Set<Constraint> constraints = checker.getConstraints();
		List<Constraint> errors = new ArrayList<Constraint>();
		for (Constraint c : constraints) {
			Reference left = c.getLeft();
			Reference right = c.getRight();
			Set<AnnotationMirror> leftAnnos = left.getAnnotations(checker);
			Set<AnnotationMirror> rightAnnos = right.getAnnotations(checker);
			if (leftAnnos.isEmpty() || rightAnnos.isEmpty()) {
				errors.add(c);
			} else {
				AnnotationMirror leftAnno = leftAnnos.iterator().next();
				AnnotationMirror rightAnno = rightAnnos.iterator().next();
				if (!checker.getQualifierHierarchy().isSubtype(leftAnno, rightAnno)
						&& checker.getFailureStatus(c) == FailureStatus.ERROR) {
					errors.add(c);
				}
			}
		}
		info(this.getClass().getSimpleName(),
				"Finished verifying the concrete typing. " + errors.size()
						+ " error(s)");
		return errors;
		
	}

	/* (non-Javadoc)
	 * @see checkers.inference2.TypingExtractor#extract()
	 */
	@Override
	public abstract List<Constraint> extract();

}
