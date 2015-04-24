/**
 * 
 */
package checkers.inference2;

import java.util.Collection;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;

import checkers.source.SourceChecker;
import checkers.types.QualifierHierarchy;
import checkers.util.AnnotationUtils;
import checkers.util.GraphQualifierHierarchy;

/**
 * This is the same as {@link GraphQualifierHierarchy} except that it returns
 * an instance of {@link InferenceGraphQualifierHierarchy} instead.
 * @author huangw5
 *
 */
public class InferenceGraphQualifierHierarchy extends GraphQualifierHierarchy {
	
	/**
     * We only need to make sure that "build" instantiates the right QualifierHierarchy. 
     */
    public static class InferenceGraphFactory extends GraphFactory {
        private final AnnotationMirror bottom;

        public InferenceGraphFactory(SourceChecker checker) {
            super(checker);
            this.bottom = null;
        }

        public InferenceGraphFactory(SourceChecker checker, AnnotationMirror bottom) {
            super(checker);
            this.bottom = bottom;
        }

        @Override
        protected QualifierHierarchy createQualifierHierarchy() {
            if (this.bottom!=null) {
                // A special bottom qualifier was provided; go through the existing
                // bottom qualifiers and tie them all to this bottom qualifier.
                Set<AnnotationMirror> bottoms = findBottoms(supertypes, null);
                for (AnnotationMirror abot : bottoms) {
                    if (!AnnotationUtils.areSame(bottom, abot)) {
                        addSubtype(bottom, abot);
                    }
                }

                if (!this.polyQualifiers.isEmpty()) {
                    for (AnnotationMirror poly : polyQualifiers.values()) {
                        addSubtype(bottom, poly);
                    }
                }
            }

            return new InferenceGraphQualifierHierarchy(this);
        }
    }

	protected InferenceGraphQualifierHierarchy(GraphFactory f) {
		super(f);
		// TODO Auto-generated constructor stub
	}
	
	

    @Override
	public boolean isSubtype(AnnotationMirror anno1, AnnotationMirror anno2) {
    	if (anno1.toString().equals(anno2.toString()))
    		return true;
		return super.isSubtype(anno1, anno2);
	}



	@Override
    public boolean isSubtype(Collection<AnnotationMirror> rhs, Collection<AnnotationMirror> lhs) {
        if (lhs.isEmpty() || rhs.isEmpty()) {
            return false;
        }
        for (AnnotationMirror lhsAnno : lhs) {
            for (AnnotationMirror rhsAnno : rhs) {
                if (isSubtype(rhsAnno, lhsAnno)) {
                    return true;
                }
            }
        }
        return false;
    }
}
