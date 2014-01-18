package edu.rpi;

import soot.Value;
import soot.Type;
import soot.VoidType;
import soot.SootClass;
import soot.SootMethod;

import java.util.*;
import java.lang.annotation.*;


public class AnnotatedValueMap extends HashMap<String, AnnotatedValue> {

    private static AnnotatedValueMap instance = new AnnotatedValueMap();

    private AnnotatedValueMap() {
    }

    public static AnnotatedValueMap v() {
        return instance;
    }

}
