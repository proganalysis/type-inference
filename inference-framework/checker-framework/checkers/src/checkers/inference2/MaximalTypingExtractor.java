/**
 * 
 */
package checkers.inference2;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;

import checkers.util.AnnotationUtils;

/**
 * @author huangw5
 *
 */
public class MaximalTypingExtractor extends AbstractTypingExtractor {

	public MaximalTypingExtractor(InferenceChecker c) {
		super(c);
	}

	/* (non-Javadoc)
	 * @see checkers.inference2.AbstractTypingExtractor#extract()
	 */
	@Override
	public List<Constraint> extract() {
		Collection<Reference> references = checker.getAnnotatedReferences().values();
		Comparator<AnnotationMirror> comparator = checker.getComparator();
		for (Reference r : references) {
			AnnotationMirror[] annos = r.getAnnotations(checker).toArray(
					new AnnotationMirror[0]);
			if (annos.length == 0) {
				continue;
			}
			// sort
			Arrays.sort(annos, comparator);
			// get the maximal annotation
			Set<AnnotationMirror> maxAnnos = AnnotationUtils.createAnnotationSet();
			maxAnnos.add(annos[0]);
		}
		return typeCheck();
	}

}
