/**
 * 
 */
package edu.rpi;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static com.esotericsoftware.minlog.Log.*;


/**
 * @author huangw5
 *
 */
public class MaximalTypingExtractor extends AbstractTypingExtractor {

	public MaximalTypingExtractor(InferenceTransformer c, ConstraintSolver solver) {
		super(c, solver);
	}

	/* (non-Javadoc)
	 * @see checkers.inference.AbstractTypingExtractor#extract()
	 */
	@Override
	public List<Constraint> extract() {
		Collection<AnnotatedValue> references = checker.getAnnotatedValues().values();
		info(this.getClass().getSimpleName(),
				"Picking up the maximal qualifier for " + references.size()
						+ " variables...");
		Comparator<Annotation> comparator = checker.getComparator();
		for (AnnotatedValue r : references) {
			Annotation[] annos = r.getAnnotations(checker).toArray(new Annotation[0]);
			if (annos.length == 0) {
				continue;
			}
			// sort
			Arrays.sort(annos, comparator);
			// get the maximal annotation
			Set<Annotation> maxAnnos = AnnotationUtils.createAnnotationSet();
			maxAnnos.add(annos[0]);
			r.setAnnotations(maxAnnos, checker);
		}
		return typeCheck();
	}

}
