/**
 * 
 */
package checkers.inference2.jcrypt2;

import static com.esotericsoftware.minlog.Log.info;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;

import checkers.inference2.Constraint;
import checkers.inference2.InferenceChecker;
import checkers.inference2.MaximalTypingExtractor;
import checkers.inference2.Reference;
import checkers.inference2.ConstraintSolver.FailureStatus;
import checkers.util.AnnotationUtils;

/**
 * @author huangw5
 * 
 */
public class Jcrypt2TypingExtractor extends MaximalTypingExtractor {

	private InferenceChecker checker;

	public Jcrypt2TypingExtractor(InferenceChecker c) {
		super(c);
		checker = c;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see checkers.inference2.AbstractTypingExtractor#extract()
	 */
	@Override
	public List<Constraint> extract() {
		Collection<Reference> references = checker.getAnnotatedReferences()
				.values();
		info(this.getClass().getSimpleName(),
				"Picking up the maximal qualifier for " + references.size()
						+ " variables...");
		Comparator<AnnotationMirror> comparator = checker.getComparator();
		for (Reference r : references) {
			AnnotationMirror[] annos = r.getAnnotations(checker).toArray(
					new AnnotationMirror[0]);
			if (annos.length != 0) {
				// sort
				Arrays.sort(annos, comparator);
				// get the maximal annotation
				Set<AnnotationMirror> maxAnnos = AnnotationUtils.createAnnotationSet();
				maxAnnos.add(annos[0]);
				r.setAnnotations(maxAnnos, checker);
			}
			AnnotationMirror[] removedAnnos = r.getRemovedAnnos().toArray(
					new AnnotationMirror[0]);
			if (removedAnnos.length != 0) {
				// sort
				Arrays.sort(removedAnnos, comparator);
				// get the maximal annotation
				Set<AnnotationMirror> maxAnnos = AnnotationUtils.createAnnotationSet();
				maxAnnos.add(removedAnnos[0]);
				r.setRemovedAnnotations(maxAnnos);
			}
		}
		return typeCheck();
	}

	@Override
	public List<Constraint> typeCheck() {
		info(this.getClass().getSimpleName(),
				"Verifying the concrete typing...");
		Set<Constraint> constraints = checker.getConstraints();
		List<Constraint> errors = new ArrayList<Constraint>();
		for (Constraint c : constraints) {
			Reference left = c.getLeft();
			Reference right = c.getRight();
			Set<AnnotationMirror> leftAnnos = left.getAnnotations(checker);
			Set<AnnotationMirror> rightAnnos = right.getAnnotations(checker);
			if (!leftAnnos.isEmpty() && !rightAnnos.isEmpty()) {
				AnnotationMirror leftAnno = leftAnnos.iterator().next();
				AnnotationMirror rightAnno = rightAnnos.iterator().next();
				if (!checker.getQualifierHierarchy().isSubtype(leftAnno,
						rightAnno)
						&& checker.getFailureStatus(c) == FailureStatus.ERROR) {
					errors.add(c);
				}
				if (!leftAnno.toString().equals(rightAnno.toString())) {
					System.out.println(c.toString());
				}
			}
		}
		info(this.getClass().getSimpleName(),
				"Finished verifying the concrete typing. " + errors.size()
						+ " error(s)");
		return errors;

	}

}
