/**
 * 
 */
package edu.rpi;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import edu.rpi.ConstraintSolver.FailureStatus;

import static com.esotericsoftware.minlog.Log.*;

/**
 * @author huangw5
 *
 */
public abstract class AbstractTypingExtractor implements TypingExtractor {
	
	protected InferenceTransformer checker;
	protected ConstraintSolver solver;
	
	public AbstractTypingExtractor(InferenceTransformer c, ConstraintSolver solver) {
		this.checker = c;
		this.solver = solver;
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
			AnnotatedValue left = c.getLeft();
			AnnotatedValue right = c.getRight();
			Set<Annotation> leftAnnos = solver.getAnnotations(left);
			Set<Annotation> rightAnnos = solver.getAnnotations(right);
			if (leftAnnos.isEmpty() || rightAnnos.isEmpty()) {
				errors.add(c);
			} else {
				Annotation leftAnno = leftAnnos.iterator().next();
				Annotation rightAnno = rightAnnos.iterator().next();
				if (!AnnotationUtils.isSubtype(leftAnno, rightAnno)
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
	 * @see checkers.inference.TypingExtractor#extract()
	 */
	@Override
	public abstract List<Constraint> extract();

}
