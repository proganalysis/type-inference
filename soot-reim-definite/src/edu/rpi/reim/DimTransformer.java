package edu.rpi.reim;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;

import checkers.inference.reim.quals.MaybeMutable;
import checkers.inference.reim.quals.PolyOrMaybe;
import checkers.inference.reim.quals.Readonly;
import edu.rpi.AnnotatedValue;
import edu.rpi.AnnotationUtils;
import edu.rpi.ViewpointAdapter;
import edu.rpi.AnnotatedValue.Kind;
import soot.Body;
import soot.VoidType;

public class DimTransformer extends ReimTransformer {

	
	public final Annotation MAYBEMUTABLE;
	public final Annotation POLYORMAYBE;
		
	public DimTransformer() {
		
		MAYBEMUTABLE = AnnotationUtils.fromClass(MaybeMutable.class);
		POLYORMAYBE = AnnotationUtils.fromClass(PolyOrMaybe.class);
		
		sourceAnnos.add(MAYBEMUTABLE);
		sourceAnnos.add(POLYORMAYBE);
	}
	
	@Override
    public ViewpointAdapter getViewpointAdapter() {
        return new DimViewpointAdapter();
    }

	@Override
    protected void handleInstanceFieldWrite(AnnotatedValue aBase, 
            AnnotatedValue aField, AnnotatedValue aRhs) {
        Set<Annotation> set = AnnotationUtils.createAnnotationSet();
        set.add(MUTABLE);
        AnnotatedValue mutableConstant = getAnnotatedValue(
                MUTABLE.annotationType().getCanonicalName(), 
                VoidType.v(), Kind.CONSTANT, null, set);
        addEqualityConstraint(aBase, mutableConstant);
        
        Set<Annotation> set2 = AnnotationUtils.createAnnotationSet();
        set2.add(MAYBEMUTABLE);
        AnnotatedValue maybeMutableConstant = getAnnotatedValue(
        		MAYBEMUTABLE.annotationType().getCanonicalName(),
        		VoidType.v(), Kind.CONSTANT, null, set2);
        super.handleInstanceFieldWrite(maybeMutableConstant, aField, aRhs);
        
    }
	
	
}
