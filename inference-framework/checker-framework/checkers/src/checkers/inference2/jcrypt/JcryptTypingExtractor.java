package checkers.inference2.jcrypt;

import java.util.List;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;

import checkers.inference2.Constraint;
import checkers.inference2.InferenceChecker;
import checkers.inference2.MaximalTypingExtractor;
import checkers.inference2.Reference;
import checkers.inference2.Reference.MethodAdaptReference;
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

	/* (non-Javadoc)
	 * @see checkers.inference2.AbstractTypingExtractor#extract()
	 */
	@Override
	public List<Constraint> extract() {
		List<Constraint> conflicts = super.extract();
		for (Constraint c : conflicts) {
			MethodAdaptReference adaptRef = (MethodAdaptReference) c.getRight();
			Reference callsiteRef = adaptRef.getContextRef();
			// update the annotation for the callsite reference
			Set<AnnotationMirror> finalAnnos = AnnotationUtils.createAnnotationSet();
			finalAnnos.add(checker.SENSITIVE);
			callsiteRef.setAnnotations(finalAnnos, checker);
		}
		return typeCheck();
	}

}
