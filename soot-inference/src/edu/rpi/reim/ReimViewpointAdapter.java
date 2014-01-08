package edu.rpi.reim;


import java.lang.annotation.*;

import edu.rpi.*;

import checkers.inference.sflow.quals.*;
import checkers.inference.reim.quals.*;

public class ReimViewpointAdapter implements ViewpointAdapter {

    private final Annotation READONLY;

    private final Annotation POLYREAD;

    private final Annotation MUTABLE;

    public ReimViewpointAdapter() {
        READONLY = AnnotationUtils.fromClass(Readonly.class);
        POLYREAD = AnnotationUtils.fromClass(Polyread.class);
        MUTABLE = AnnotationUtils.fromClass(Mutable.class);
    }

    public Annotation adaptField(Annotation context, Annotation decl) {
        if (decl.equals(READONLY))
            return READONLY;
        if (decl.equals(POLYREAD))
            return context;
        else if (decl.equals(MUTABLE))
            return MUTABLE;
        else
            throw new RuntimeException("Unknow decl annotation: " + decl);
    }

    public Annotation adaptMethod(Annotation context, Annotation decl) {
        return adaptField(context, decl);
    }

}


