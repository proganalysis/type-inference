/**
 * 
 */
package checkers.inference2.jcrypt2;

import static com.esotericsoftware.minlog.Log.info;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;

import checkers.inference2.Constraint;
import checkers.inference2.InferenceChecker;
import checkers.inference2.MaximalTypingExtractor;
import checkers.inference2.Reference;
import checkers.inference2.ConstraintSolver.FailureStatus;
import checkers.inference2.Reference.AdaptReference;
import checkers.inference2.Reference.MethodAdaptReference;
import checkers.inference2.Reference.RefKind;
import checkers.util.AnnotationUtils;

/**
 * @author huangw5
 * 
 */
public class Jcrypt2TypingExtractor extends MaximalTypingExtractor {

	private InferenceChecker checker;
	private Map<String, Constraint> conversions;

	public Jcrypt2TypingExtractor(InferenceChecker c) {
		super(c);
		checker = c;
		conversions = new HashMap<>();
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
				Set<AnnotationMirror> maxAnnos = AnnotationUtils
						.createAnnotationSet();
				maxAnnos.add(annos[0]);
				r.setAnnotations(maxAnnos, checker);
			}
		}
		return typeCheck();
	}

	@Override
	public List<Constraint> typeCheck() {
		info(this.getClass().getSimpleName(),
				"Verifying the concrete typing...");
		Set<Constraint> constraints = checker.getConstraints();
		List<Constraint> errors = new ArrayList<>();
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
			}
			conversionCheck(c, left, right, leftAnnos, rightAnnos);
		}
		info(this.getClass().getSimpleName(),
				"Finished verifying the concrete typing. " + errors.size()
						+ " error(s)");
		info(this.getClass().getSimpleName(),
				"Finished extracting type conversions. " + conversions.size()
						+ " conversion(s)");
		return errors;
	}

	public void conversionCheck(Constraint c, Reference left, Reference right,
			Set<AnnotationMirror> leftAnnos, Set<AnnotationMirror> rightAnnos) {
		//RefKind rightKind = right.getKind();
		if (right.getKind() == RefKind.EQUAL_NULL || left.getKind() == RefKind.EQUAL_NULL
				|| c.getLineNum() == 0) return;
		//if (leftKind != RefKind.LITERAL || leftKind != RefKind.STRING) return;
		//if (left instanceof AdaptReference || right instanceof AdaptReference) return;
		Jcrypt2Checker jcrypt2checker = (Jcrypt2Checker) checker;
		// ignore clear <: parameter, clear <: clear, clear <: component(parameter)
		if (jcrypt2checker.containsAnno(left, jcrypt2checker.CLEAR)
				&& !(right instanceof MethodAdaptReference) && !rightAnnos.isEmpty()) {
			Element rightEle = right.getElement();
			if (rightEle != null && checker.getAnnotatedReference(rightEle).getKind()
					== RefKind.PARAMETER) return;
			String rightCryptType = right.getCryptType() == null ?
					rightAnnos.iterator().next().toString()
					: right.getCryptType().toString();
			if (conversions.put(c.getLineNum() + ":" + left.getIdentifier(), c) == null) {
				System.out.println(c.toString());
				System.out.println("Line " + c.getLineNum() + ": " + left.getName()
						+ " CLEAR => "
						+ rightCryptType.substring(rightCryptType.lastIndexOf('.')+1));
			}
			return;
		}
		if (!leftAnnos.isEmpty() && !rightAnnos.isEmpty()) {
			AnnotationMirror leftAnno = leftAnnos.iterator().next();
			AnnotationMirror rightAnno = rightAnnos.iterator().next();
			String leftCryptType = left.getCryptType() == null ? leftAnno
					.toString() : left.getCryptType().toString();
			String rightCryptType = right.getCryptType() == null ? rightAnno
					.toString() : right.getCryptType().toString();
			if (!leftCryptType.equals(rightCryptType)) {
				if (conversions.put(c.getLineNum() + ":" + left.getIdentifier(), c) == null) {
					String leftName = left instanceof AdaptReference ?
							((AdaptReference) left).getDeclRef().getName() : left.getName();
					System.out.println(c.toString());
					System.out.println("Line " + c.getLineNum() + ": " + leftName
							+ " " + leftCryptType.substring(leftCryptType.lastIndexOf('.')+1)
							+ " => " + rightCryptType.substring(rightCryptType.lastIndexOf('.')+1));
				}
			}
		}
	}

}
