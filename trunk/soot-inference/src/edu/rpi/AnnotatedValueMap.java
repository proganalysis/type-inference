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

    @Override
    public AnnotatedValue put(String key, AnnotatedValue value) {
        if (this.size() % 10000 == 0) {
            System.out.println(String.format("%6s: %14d", "size", this.size()));
            System.out.println(String.format("%6s: %14f MB", "free", ((float) Runtime.getRuntime().freeMemory()) / (1024*1024)));
            System.out.println(String.format("%6s: %14f MB", "total", ((float) Runtime.getRuntime().totalMemory()) / (1024*1024)));
        }
        return super.put(key, value);
    }

}
