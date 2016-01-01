package edu.rpi.reim;

import java.lang.annotation.*;

import edu.rpi.*;

import checkers.inference.leak.quals.*;

public class LeakViewpointAdapter implements ViewpointAdapter {

    private final Annotation LEAK;

    private final Annotation NOLEAK;
    
    private final Annotation POLY;

    public LeakViewpointAdapter() {
        LEAK = AnnotationUtils.fromClass(Leak.class);
        NOLEAK = AnnotationUtils.fromClass(Noleak.class);
        POLY = AnnotationUtils.fromClass(Poly.class);
    }

    public Annotation adaptField(Annotation context, Annotation decl) {
    	/*
    	if (decl.equals(NOLEAK))
            return NOLEAK;
        else if (decl.equals(POLY))
            return context;
        else
            throw new RuntimeException("Unknow decl annotation: " + decl);
        */
    	return decl;
    }

    public Annotation adaptMethod(Annotation context, Annotation decl) {
        if (decl.equals(POLY))
        	return context;
        else
        	return decl;
    }

}

