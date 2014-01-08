package edu.rpi;

import java.util.Iterator;
import java.util.*;
import java.lang.annotation.*;

import soot.Body;
import soot.BodyTransformer;
import soot.Local;
import soot.PackManager;
import soot.PatchingChain;
import soot.RefType;
import soot.ArrayType;
import soot.VoidType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.SootField;
import soot.Transform;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.AbstractStmtSwitch;
import soot.jimple.AbstractJimpleValueSwitch;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.*;
import soot.jimple.Jimple;
import soot.jimple.StringConstant;
import soot.options.Options;
import soot.tagkit.*; 

import edu.rpi.Constraint.SubtypeConstraint;
import edu.rpi.AnnotatedValue.Kind;

public class InferenceVisitor extends AbstractStmtSwitch {

    private InferenceTransformer t; 

    public InferenceVisitor(InferenceTransformer t) {
        this.t = t;
    }

    public void defaultCase(Object obj)
    {
        System.out.println("Default case (" + obj.getClass() + "): " + obj);
    }

    public void caseInvokeStmt(InvokeStmt stmt) {
        AnnotatedValue fakeLhs = t.getAnnotatedValue("fake-" + t.getVisitorState().getSootClass().getName() 
                + "<" + stmt.hashCode() + ">", VoidType.v(), Kind.CONSTANT, stmt.getInvokeExpr());
        stmt.getInvokeExpr().apply(new ValueVisitor(null, fakeLhs));
    }

    public void caseAssignStmt(AssignStmt stmt)
    {
        Value leftOp = stmt.getLeftOp();
        Value rightOp = stmt.getRightOp();
        if (leftOp instanceof Local) {
            AnnotatedValue aLeft = t.getAnnotatedValue((Local) leftOp);
            rightOp.apply(new ValueVisitor(null, aLeft));
        } else if (rightOp instanceof Local) {
            AnnotatedValue aRight = t.getAnnotatedValue((Local) rightOp);
            leftOp.apply(new ValueVisitor(aRight, null));
        }
    }

    public void caseReturnStmt(ReturnStmt stmt) {
        AnnotatedValue aReturn = t.getAnnotatedReturn(t.getVisitorState().getSootMethod());
        Value returnOp = stmt.getOp();
        returnOp.apply(new ValueVisitor(null, aReturn));
    }

    public void caseIdentityStmt(IdentityStmt stmt) {
        Value left = stmt.getLeftOp();
        AnnotatedValue aLeft = null;
        if (left instanceof Local) 
            aLeft = t.getAnnotatedValue((Local) left);
        else
            throw new RuntimeException("Unhandled: " + left + ": " + left.getClass());
        Value rightOp = stmt.getRightOp();
        rightOp.apply(new ValueVisitor(null, aLeft));
    }

    public void caseReturnVoidStmt(ReturnVoidStmt stmt) {}

    class ValueVisitor extends AbstractJimpleValueSwitch {
        AnnotatedValue sub; 
        AnnotatedValue sup; 
        public ValueVisitor(AnnotatedValue sub, AnnotatedValue sup) {
            this.sub = sub;
            this.sup = sup;
            if (sub != null && sup != null)
                throw new RuntimeException("Only one of sub and sup can be non-null");
        }
        @Override
        public void caseCastExpr(CastExpr v) {
            Value cv = v.getOp();
            AnnotatedValue av = t.getAnnotatedValue(cv);
            add(av);
        }

        @Override
        public void caseParameterRef(ParameterRef v) {
            AnnotatedValue av = t.getAnnotatedParameter(
                    t.getVisitorState().getSootMethod(), v.getIndex());
            add(av);
        }

        @Override 
        public void caseThisRef(ThisRef v) {
            AnnotatedValue av = t.getAnnotatedThis(t.getVisitorState().getSootMethod());
            add(av);
        }

        @Override
        public void caseLocal(Local v) {
            AnnotatedValue av = t.getAnnotatedValue(v); 
            add(av);
        }

        @Override
        public void caseStaticFieldRef(StaticFieldRef v) {
            SootField field = v.getField();
            AnnotatedValue aField = t.getAnnotatedField(field);
            add(aField);
        }

        @Override
        public void caseInstanceFieldRef(InstanceFieldRef v) {
            Value base = v.getBase();
            assert base instanceof Local;
            AnnotatedValue aBase = t.getAnnotatedValue((Local) base);
            SootField field = v.getField();
            AnnotatedValue aField = t.getAnnotatedField(field);

            if (field.getName().equals("this$0")) {
                // this is inner class, no adaptation
                add(aField);
            }
            else if (sub != null && sup == null) 
                t.handleInstanceFieldWrite(aBase, aField, sub);
            else if (sub == null && sup != null)
                t.handleInstanceFieldRead(aBase, aField, sup);
            else
                throw new RuntimeException("what happened?");
        }

        @Override
        public void caseArrayRef(ArrayRef v) {
            Value base = v.getBase();
            assert base instanceof Local;
            AnnotatedValue aBase = t.getAnnotatedValue((Local) base);
            AnnotatedValue aComponent = t.getAnnotatedValue(aBase.getIdentifier() + "[]", 
                    ((ArrayType) base.getType()).getElementType(), Kind.LOCAL, base);
            if (sub != null && sup == null) 
                t.handleInstanceFieldWrite(aBase, aComponent, sub);
            else if (sub == null && sup != null)
                t.handleInstanceFieldRead(aBase, aComponent, sup);
        }

        public void caseInterfaceInvokeExpr(InterfaceInvokeExpr v) {
            t.handleMethodCall(v, sup);
        }

        public void caseSpecialInvokeExpr(SpecialInvokeExpr v) {
            // Skip Object.<init>
            SootMethod sm = v.getMethod();
            if (sm.getName().equals("<init>") 
                    && sm.getDeclaringClass().getName().equals("java.lang.Object"))
                return;
            t.handleMethodCall(v, sup);
        }

        public void caseStaticInvokeExpr(StaticInvokeExpr v) {
            // Skip accesses from inner classes, e.g. access$0
            if (v.getMethod().getName().startsWith("access$")) {
                handleInnerAccessCall(v, sup);
                return;
            }
            t.handleMethodCall(v, sup);
        }

        public void caseVirtualInvokeExpr(VirtualInvokeExpr v) {
            t.handleMethodCall(v, sup);
        }
        
        public void caseDynamicInvokeExpr(DynamicInvokeExpr v) {
            t.handleMethodCall(v, sup);
        }
 
        @Override
        public void caseDoubleConstant(DoubleConstant v) { }

        @Override
        public void caseFloatConstant(FloatConstant v) { }

        @Override
        public void caseIntConstant(IntConstant v) { }

        @Override
        public void caseLongConstant(LongConstant v) { }

        @Override
        public void caseNullConstant(NullConstant v) { }

        @Override
        public void caseStringConstant(StringConstant v) { }

        @Override
        public void caseClassConstant(ClassConstant v) { }

        @Override
        public void defaultCase(Object v) {
            System.out.println("Unhandled value: " + v + " of type " + v.getClass()
                    + "\n\t" + sub + "  <:  " + v + "  <:  " + sup);
        }

        /**
         * Handle accesses from inner class, e.g. access$0
         * No adaptation
         */
        private void handleInnerAccessCall(StaticInvokeExpr v, AnnotatedValue assignTo) {
            SootMethod invokeMethod = v.getMethod();
            AnnotatedValue aBase = null;
            // parameters
            List<Value> args = v.getArgs();
            for (int i = 0; i < v.getArgCount(); i++) {
                Value arg = args.get(i);
                assert arg instanceof Local;
                AnnotatedValue aArg = t.getAnnotatedValue(arg);
                AnnotatedValue aParam = t.getAnnotatedParameter(invokeMethod, i);
                t.addSubtypeConstraint(aArg, aParam);
            }
            // return
            if (invokeMethod.getReturnType() != VoidType.v()) {
                if (assignTo == null)
                    throw new RuntimeException("Null assignTo");
                AnnotatedValue aReturn = t.getAnnotatedReturn(invokeMethod);
                t.addSubtypeConstraint(aReturn, assignTo);
            }
        }

        private void add(AnnotatedValue av) {
            if (sub == null && sup != null) 
                t.addSubtypeConstraint(av, sup);
            else if (sub != null && sup == null) 
                t.addSubtypeConstraint(sub, av);
        }
    }

}
