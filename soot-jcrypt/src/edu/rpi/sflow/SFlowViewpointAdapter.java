package edu.rpi.sflow;

import java.lang.annotation.*;

import edu.rpi.*;

import checkers.inference.sflow.quals.*;
import checkers.inference.reim.quals.*;

public class SFlowViewpointAdapter implements ViewpointAdapter {

    private final Annotation TAINTED;

    private final Annotation POLY;

    private final Annotation SAFE;

    public final Annotation BOTTOM;

    public SFlowViewpointAdapter() {
        TAINTED = AnnotationUtils.fromClass(Tainted.class);
        POLY = AnnotationUtils.fromClass(Poly.class);
        SAFE = AnnotationUtils.fromClass(Safe.class);
        BOTTOM = AnnotationUtils.fromClass(Bottom.class);
    }

    public Annotation adaptField(Annotation context, Annotation decl) {
        if (decl.equals(TAINTED))
            return TAINTED;
        if (decl.equals(POLY))
            return context;
        else if (decl.equals(SAFE))
            return SAFE;
        else
            throw new RuntimeException("Unknow decl annotation: " + decl);
    }

    public Annotation adaptMethod(Annotation context, Annotation decl) {
        return adaptField(context, decl);
    }

}

