package edu.rpi;

import java.lang.annotation.*;

public interface ViewpointAdapter {

    public Annotation adaptField(Annotation context, Annotation decl);

    public Annotation adaptMethod(Annotation context, Annotation decl);

}
