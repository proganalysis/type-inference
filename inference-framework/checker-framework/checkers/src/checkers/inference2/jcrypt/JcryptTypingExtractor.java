package checkers.inference2.jcrypt;

import java.util.List;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;

import checkers.inference2.Constraint;
import checkers.inference2.InferenceChecker;
import checkers.inference2.MaximalTypingExtractor;
import checkers.inference2.Reference;
import checkers.inference2.Reference.MethodAdaptReference;
import checkers.inference2.Reference.RefKind;
import checkers.util.AnnotationUtils;

/**
 * @author dongy6
 *
 */
public class JcryptTypingExtractor extends MaximalTypingExtractor {

	private JcryptChecker checker;

	public JcryptTypingExtractor(InferenceChecker c) {
		super(c);
		checker = (JcryptChecker) c;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see checkers.inference2.AbstractTypingExtractor#extract()
	 */
	@Override
	public List<Constraint> extract() {
		List<Constraint> conflicts = super.extract();
		if (conflicts.isEmpty()) return conflicts;
		for (Constraint c : conflicts) {
			System.out.println(c.toString());
			if (check(c)) continue;
			Reference left = c.getLeft();
			Reference right = c.getRight();
			if (left.getKind() == RefKind.METH_ADAPT) {
				updateAnnotation(left, c);
			} else if (right.getKind() == RefKind.METH_ADAPT) {
				updateAnnotation(right, c);
			}
			System.out.println(c.toString());
		}
		return typeCheck();
	}

	private boolean updateAnnotation(Reference ref, Constraint c) {
		MethodAdaptReference adaptRef = (MethodAdaptReference) ref;
		Reference callsiteRef = adaptRef.getContextRef();
		// update the annotation for the callsite reference
		Set<AnnotationMirror> finalAnnos = AnnotationUtils.createAnnotationSet();
		AnnotationMirror anno = callsiteRef.getAnnotations(checker).iterator().next();
		if (anno == checker.CLEAR)
			finalAnnos.add(checker.POLY);
		else if (anno == checker.POLY)
			finalAnnos.add(checker.SENSITIVE);
		else
			return false;
		callsiteRef.setAnnotations(finalAnnos, checker);
		if (check(c))
			return true;
		else
			return updateAnnotation(ref, c);
	}

	private boolean check(Constraint c) {
		Reference left = c.getLeft();
		Reference right = c.getRight();
		Set<AnnotationMirror> leftAnnos = left.getAnnotations(checker);
		Set<AnnotationMirror> rightAnnos = right.getAnnotations(checker);
		AnnotationMirror leftAnno = leftAnnos.iterator().next();
		AnnotationMirror rightAnno = rightAnnos.iterator().next();
		if (checker.getQualifierHierarchy().isSubtype(leftAnno, rightAnno))
			return true;
		return false;
	}

}
