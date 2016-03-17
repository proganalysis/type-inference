/**
 * 
 */
package edu.rpi;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import edu.rpi.AnnotatedValue.MethodAdaptValue;
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
		Annotation[] sourceAnnotations = checker.getSourceLevelQualifiers().toArray(new Annotation[0]);
		Arrays.sort(sourceAnnotations, checker.getComparator());
		
		for (Constraint c : constraints) {
			if (!isTypeCheck(c)) errors.add(c);
		}
		info(this.getClass().getSimpleName(),
				"Finished verifying the concrete typing. " + errors.size()
						+ " error(s)");
		// handle callsite typing
		info(this.getClass().getSimpleName(), "Choosing the proper callsite typing...");
		for (Constraint c : errors) {
			if (isTypeCheck(c)) continue;
			AnnotatedValue[] annoValues = new AnnotatedValue[] { c.getLeft(), c.getRight() };
			for (AnnotatedValue annoValue : annoValues) {
				if (!(annoValue instanceof MethodAdaptValue))
					continue;
				Set<Annotation> annos = solver.getAnnotations(annoValue);
				if (annos.isEmpty())
					continue;
				AnnotatedValue callsite = ((MethodAdaptValue) annoValue).getContextValue();
				Annotation anno = callsite.getAnnotations(checker).iterator().next();
				if (anno == sourceAnnotations[0]) {
					setAnnotation(sourceAnnotations[1], callsite);
					if (!isTypeCheck(c))
						setAnnotation(sourceAnnotations[2], callsite);
				} else if (anno == sourceAnnotations[1]) {
					setAnnotation(sourceAnnotations[2], callsite);
				}
			}
		}
		
		// After handling callsite typing, check all constraints again.
		errors = new ArrayList<Constraint>();
		for (Constraint c : constraints) {
			if (!isTypeCheck(c)) errors.add(c);
		}
		
		info(this.getClass().getSimpleName(),
				"Finished choosing the callsite typing. " + errors.size()
						+ " error(s)");
		return errors;
	}

	private void setAnnotation(Annotation anno, AnnotatedValue callsite) {
		Set<Annotation> annos = AnnotationUtils.createAnnotationSet();
		annos.add(anno);
		callsite.setAnnotations(annos, checker);
	}

	private boolean isTypeCheck(Constraint c) {
		AnnotatedValue left = c.getLeft();
		AnnotatedValue right = c.getRight();
		Set<Annotation> leftAnnos = solver.getAnnotations(left);
		Set<Annotation> rightAnnos = solver.getAnnotations(right);
		if (leftAnnos.isEmpty() || rightAnnos.isEmpty()) return false;
		else {
			Annotation leftAnno = leftAnnos.iterator().next();
			Annotation rightAnno = rightAnnos.iterator().next();
			if (!AnnotationUtils.isSubtype(leftAnno, rightAnno)) return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see checkers.inference.TypingExtractor#extract()
	 */
	@Override
	public abstract List<Constraint> extract();

}
