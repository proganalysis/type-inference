package edu.rpi.reim;

import java.lang.annotation.Annotation;

import checkers.inference.reim.quals.MaybeMutable;
import checkers.inference.reim.quals.PolyOrMaybe;
import edu.rpi.AnnotationUtils;

public class DimViewpointAdapter extends ReimViewpointAdapter {

	private final Annotation MAYBEMUTABLE;
	private final Annotation POLYORMAYBE;

    public DimViewpointAdapter() {
        MAYBEMUTABLE = AnnotationUtils.fromClass(MaybeMutable.class);
        POLYORMAYBE = AnnotationUtils.fromClass(PolyOrMaybe.class);
    }
    
    
    public Annotation adaptField(Annotation context, Annotation decl) {
        if (decl.equals(READONLY))
            return READONLY;
        if (decl.equals(POLYREAD))
            return context;
        else if (decl.equals(MUTABLE))
            return MUTABLE;
        else if (decl.equals(MAYBEMUTABLE)) {
        		return MAYBEMUTABLE;
        }
        else if (decl.equals(POLYORMAYBE)) {
        		if (context.equals(READONLY)) {
        			return MAYBEMUTABLE;
        		}
        		else if (context.equals(MUTABLE)) {
        			return MUTABLE;
        		}
        		else if (context.equals(POLYREAD) || context.equals(POLYORMAYBE)) {
        			return POLYORMAYBE;
        		}
        		else if (context.equals(MAYBEMUTABLE)) {
        			return MAYBEMUTABLE;
        		}
        		else {
        			throw new RuntimeException("Unknown context annotation: " + context);
        		}
        }
        else
            throw new RuntimeException("Unknow decl annotation: " + decl);
    }
    
    
}
