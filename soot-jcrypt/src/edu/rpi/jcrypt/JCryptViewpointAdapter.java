package edu.rpi.jcrypt;

import java.lang.annotation.*;

import edu.rpi.*;

import checkers.inference.jcrypt.quals.*;

public class JCryptViewpointAdapter implements ViewpointAdapter {

    private final Annotation SENSITIVE;

    private final Annotation POLY;

    private final Annotation CLEAR;

    public JCryptViewpointAdapter() {
        SENSITIVE = AnnotationUtils.fromClass(Sensitive.class);
        POLY = AnnotationUtils.fromClass(Poly.class);
        CLEAR = AnnotationUtils.fromClass(Clear.class);
    }

    public Annotation adaptField(Annotation context, Annotation decl) {
        if (decl.equals(SENSITIVE))
            return SENSITIVE;
        if (decl.equals(POLY))
            return context;
        else if (decl.equals(CLEAR))
            return CLEAR;
        else
            throw new RuntimeException("Unknow decl annotation: " + decl);
    }

    public Annotation adaptMethod(Annotation context, Annotation decl) {
        return adaptField(context, decl);
    }

}

