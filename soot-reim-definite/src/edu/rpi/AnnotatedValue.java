package edu.rpi;

import soot.Value;
import soot.Type;
import soot.VoidType;
import soot.SootClass;
import soot.SootMethod;

import java.util.*;
import java.lang.annotation.*;

public class AnnotatedValue {

    public enum Kind {
        LOCAL, 
        COMPONENT, 
        LITERAL, 
        FIELD, 
        PARAMETER,
        THIS, 
        RETURN, 
        CONSTANT,
        CLASS, 
        METH_ADAPT, 
        FIELD_ADAPT
    }

    private static int counter = 0;

    private int id;

    private String identifier;

    private Type type;

    private Kind kind;

    private Object value;

    private SootClass enclosingClass;

    private SootMethod enclosingMethod;

    private String name; 

    private int restoreNum = 0;

    protected Set<Annotation> annos;

    public static int maxId() {
        return counter;
    }

    public static void reset() {
        counter = 0;
    }

    public AnnotatedValue(String identifier, Type type, Kind kind, Object v) {
        init(identifier, type, kind, v, AnnotationUtils.createAnnotationSet());
    }

//    public AnnotatedValue(String identifier, Type type, Kind kind, Object v, Annotation anno) {
//        Set<Annotation> annos = AnnotationUtils.createAnnotationSet();
//        annos.add(anno);
//        init(identifier, type, kind, v, annos);
//    }

    public AnnotatedValue(String identifier, Type type, Kind kind, Object v, Set<Annotation> annos) {
        Set<Annotation> set = AnnotationUtils.createAnnotationSet();
        set.addAll(annos);
        init(identifier, type, kind, v, set);
    }

    private void init(String identifier, Type type, Kind kind, Object v, Set<Annotation> annos) {
        this.id = counter++;
        this.identifier = identifier;
        this.type = type;
        this.kind = kind;
        this.value = v;
        this.annos = annos;

        int i = identifier.lastIndexOf('@');
        name = (i >= 0 ? identifier.substring(i+1) : identifier);
    }

    public Type getType() {
        return type;
    }

    public Kind getKind() {
        return kind;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Object getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public SootClass getEnclosingClass() {
        return enclosingClass;
    }

    public void setEnclosingClass(SootClass sc) {
        this.enclosingClass = sc;
    }

    public SootMethod getEnclosingMethod() {
        return enclosingMethod;
    }

    public void setEnclosingMethod(SootMethod sm) {
        this.enclosingMethod = sm;
    }

    public int getRestoreNum() {
        return restoreNum;
    }

    public void setRestored() {
        restoreNum++;
    }

    public int getId() {
        return id;
    }

    public void clearAnnotations() {
        annos.clear();
    }

    public Set<Annotation> getRawAnnotations() {
        Set<Annotation> annos = AnnotationUtils.createAnnotationSet();
        annos.addAll(this.annos);
        return annos;
    }

    public Set<Annotation> getAnnotations(InferenceTransformer t) {
        Set<Annotation> annos = getRawAnnotations();
        annos.retainAll(t.getSourceLevelQualifiers());
        return annos;
    }

    public void setRawAnnotations(Set<Annotation> annos) {
        this.annos.clear();
        this.annos.addAll(annos);
    }

    public void setAnnotations(Set<Annotation> annos, InferenceTransformer t) {
        this.annos.removeAll(t.getSourceLevelQualifiers());
        this.annos.addAll(annos);
    }

    public void addAnnotation(Annotation anno) {
        this.annos.add(anno);
    }

    public boolean containsAnno(Annotation anno) {
        return annos.contains(anno);
    }

    public String toString() {
        return "(" +id + ")" + identifier + ": " 
            + annos.toString().replace('[', '{').replace(']', '}') 
            + " [" + type.toString() + "]";
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof AnnotatedValue)
            && this.identifier.equals(((AnnotatedValue) obj).getIdentifier());
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

    public static abstract class AdaptValue extends AnnotatedValue {
        protected AnnotatedValue context;
        protected AnnotatedValue decl;
        public AdaptValue(AnnotatedValue context, AnnotatedValue decl, Kind kind) {
            super(context.getIdentifier() + decl.getIdentifier(), 
                    VoidType.v(), kind, null);
            this.context = context;
            this.decl = decl;
        }

        public abstract Set<Annotation> getAnnotations(ViewpointAdapter va);

        @Deprecated
        public abstract void setAnnotations(ViewpointAdapter va, Set<Annotation> annos);

        public AnnotatedValue getContextValue() {
            return context;
        }

        public AnnotatedValue getDeclValue() {
            return decl;
        }

        @Override
        public boolean equals(Object obj) {
            return (obj instanceof AdaptValue)
                && this.getIdentifier().equals(((AdaptValue) obj).getIdentifier());
        }
    }

    public static class FieldAdaptValue extends AdaptValue {
        public FieldAdaptValue(AnnotatedValue context, AnnotatedValue decl) {
            super(context, decl, Kind.FIELD_ADAPT);
        }

        @Override
        public String toString() {
            return "(" + context.toString() + " =f=> " + decl.toString() + ")";
        }

        @Override
        public Set<Annotation> getAnnotations(ViewpointAdapter va) {
            Set<Annotation> annos = AnnotationUtils.createAnnotationSet();
            return annos;
        }

        @Override
        public void setAnnotations(ViewpointAdapter va, Set<Annotation> annos) {
            // TODO
            this.annos.clear();
            this.annos.addAll(annos);
        }
    }

    public static class MethodAdaptValue extends AdaptValue {
        public MethodAdaptValue(AnnotatedValue context, AnnotatedValue decl) {
            super(context, decl, Kind.METH_ADAPT);
        }

        @Override
        public String toString() {
            return "(" + context.toString() + " =m=> " + decl.toString() + ")";
        }

        @Override
        public Set<Annotation> getAnnotations(ViewpointAdapter va) {
            Set<Annotation> annos = AnnotationUtils.createAnnotationSet();
            return annos;
        }

        @Override
        public void setAnnotations(ViewpointAdapter va, Set<Annotation> annos) {
            // TODO
            this.annos.clear();
            this.annos.addAll(annos);
        }
    }
}
