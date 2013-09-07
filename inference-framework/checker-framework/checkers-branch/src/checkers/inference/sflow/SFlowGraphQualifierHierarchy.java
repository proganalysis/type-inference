/**
 * 
 */
package checkers.inference.sflow;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;

import checkers.inference.InferenceUtils;
import checkers.source.SourceChecker;
import checkers.types.QualifierHierarchy;
import checkers.util.AnnotationUtils;
import checkers.util.GraphQualifierHierarchy;


/**
 * This is the same as {@link GraphQualifierHierarchy} except that it returns
 * an instance of {@link SFlowGraphQualifierHierarchy} instead.
 * @author huangw5
 *
 */
public class SFlowGraphQualifierHierarchy extends GraphQualifierHierarchy {
	
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

            return new SFlowGraphQualifierHierarchy(this);
        }
    }
    
    private final Set<AnnotationMirror> reimQuals;

	protected SFlowGraphQualifierHierarchy(GraphFactory f) {
		super(f);
		reimQuals = AnnotationUtils.createAnnotationSet();
		reimQuals.add(SFlowChecker.READONLY);
		reimQuals.add(SFlowChecker.POLYREAD);
		reimQuals.add(SFlowChecker.MUTABLE);
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
//            throw new RuntimeException("QualifierHierarchy: Empty annotations in lhs: " + lhs + " or rhs: " + rhs);
//            System.err.println("WARN: QualifierHierarchy: Empty annotations in lhs: " + lhs + " or rhs: " + rhs + " -- ignored");
            return false;
        }
        // check if lhs contains Readonly and remove all ReIm annotations
        // I don't trust the equal method for AnnotationMirror...
        // FIXME: The following code is very ugly!!!
        boolean isLhsReadonly = false;
        for (Iterator<AnnotationMirror> it = lhs.iterator(); it.hasNext();) {
        	AnnotationMirror anno = it.next();
        	if (anno.toString().equals(SFlowChecker.READONLY.toString())) {
        		isLhsReadonly = true;
        	}
        }
        Set<AnnotationMirror> rSet = AnnotationUtils.createAnnotationSet();
        rSet.addAll(rhs);
        Set<AnnotationMirror> lSet = AnnotationUtils.createAnnotationSet();
        lSet.addAll(lhs);
        rSet = InferenceUtils.differAnnotations(rSet, reimQuals);
        lSet = InferenceUtils.differAnnotations(lSet, reimQuals);
        
        for (AnnotationMirror lhsAnno : lSet) {
            for (AnnotationMirror rhsAnno : rSet) {
				if (isLhsReadonly || rhsAnno.toString().equals(SFlowChecker.BOTTOM.toString())) {
					if (isSubtype(rhsAnno, lhsAnno)) {
						return true;
					}
				} else {
					// We enforce equality 
					if (rhsAnno.toString().equals(lhsAnno.toString()))
						return true;
				}
            }
        }
        return false;
    }
}
