package checkers.inference2.rely;

import static com.esotericsoftware.minlog.Log.info;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;

import checkers.inference2.Constraint;
import checkers.inference2.InferenceChecker;
import checkers.inference2.MaximalTypingExtractor;
import checkers.inference2.Reference;
import checkers.inference2.Reference.RefKind;
import checkers.inference2.ConstraintSolver.FailureStatus;

/**
 * @author dongy6
 *
 */
public class RelyTypingExtractor extends MaximalTypingExtractor {

	public RelyTypingExtractor(InferenceChecker c) {
		super(c);
	}

	@Override
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
					if (left.getKind() != RefKind.METH_ADAPT && right.getKind() != RefKind.METH_ADAPT) {
						errors.add(c);
					}
				}
			}
		}
		info(this.getClass().getSimpleName(), "Finished verifying the concrete typing. " + errors.size() + " error(s)");
		return errors;
	}
}
