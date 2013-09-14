/**
 * 
 */
package checkers.inference.confidentiality;

import java.util.List;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import checkers.inference.InferenceMain;
import checkers.inference.InferenceVisitor;
import checkers.inference.Reference;
import checkers.types.AnnotatedTypeMirror;
import checkers.util.AnnotationUtils;
import checkers.util.ElementUtils;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodInvocationTree;

/**
 * @author huangw5
 *
 */
public class ConfidentialityInferenceVisitor extends InferenceVisitor {
	
	private ConfidentialityChecker checker;

	public ConfidentialityInferenceVisitor(ConfidentialityChecker checker, CompilationUnitTree root) {
		super(checker, root);
		this.checker = checker;
	}	
	

	@Override
	protected void handleArrayRead(Reference lhsRef, Reference exprRef, Reference componentRef) {
		super.handleArrayRead(lhsRef, exprRef, componentRef);
		// The componentRef cannot be TAINTED
//		Set<AnnotationMirror> set = AnnotationUtils.createAnnotationSet();
//		set.add(checker.PTAINTED);
//		set.add(checker.RTAINTED);
//		Reference ref = Reference.createConstantReference(set);
//		InferenceMain.getInstance().getConstraintManager().addInequalityConstraint(componentRef, ref);
	}


	@Override
	protected void handleFieldRead(Reference lhsRef, Reference rcvRef, Reference fieldRef) {
		super.handleFieldRead(lhsRef, rcvRef, fieldRef);
		// Field cannot be TAITED, but this is enforced in AnnotatedTypeFactory. 
		// Consider move it to here
//		Element fieldElt = fieldRef.getElement();
//		if (fieldElt != null && !ElementUtils.isStatic(fieldElt)
//				&& checker.isPrimitiveType(fieldRef.getType())
//				) {
//			Set<AnnotationMirror> set = AnnotationUtils.createAnnotationSet();
//			set.add(checker.PTAINTED);
//			set.add(checker.RTAINTED);
//			Reference ref = Reference.createConstantReference(set);
//			InferenceMain.getInstance().getConstraintManager().addInequalityConstraint(fieldRef, ref);
//		}
	}


	@Override
	protected void handleArrayWrite(Reference exprRef, Reference componentRef, Reference rhsRef) {
		super.handleArrayWrite(exprRef, componentRef, rhsRef);
		// The componentRef cannot be TAINTED
//		Set<AnnotationMirror> set = AnnotationUtils.createAnnotationSet();
//		set.add(checker.PTAINTED);
//		set.add(checker.RTAINTED);
//		Reference ref = Reference.createConstantReference(set);
//		InferenceMain.getInstance().getConstraintManager().addInequalityConstraint(componentRef, ref);
	}


	@Override
	protected void handleFieldWrite(Reference rcvRef, Reference fieldRef, Reference rhsRef) {
		super.handleFieldWrite(rcvRef, fieldRef, rhsRef);
		// Field cannot be TAITED, but this is enforced in AnnotatedTypeFactory. 
		// Consider move it to here
//		Element fieldElt = fieldRef.getElement();
//		if (fieldElt != null && !ElementUtils.isStatic(fieldElt)
//				&& checker.isPrimitiveType(fieldRef.getType())
//				) {
//			Set<AnnotationMirror> set = AnnotationUtils.createAnnotationSet();
//			set.add(checker.PTAINTED);
//			set.add(checker.RTAINTED);
//			Reference ref = Reference.createConstantReference(set);
//			InferenceMain.getInstance().getConstraintManager().addInequalityConstraint(fieldRef, ref);
//		}
	}


	@Override
	protected void handleMethodCall(ExecutableElement methodElt,
			List<? extends ExpressionTree> arguments, Reference rcvRef, Reference lhsRef) {
		// FIXME: Special-case for System.arraycopy and Memory.memmove: 
		// We only enforce the constraint that src <: dst
		String classStr = methodElt.getEnclosingElement().toString();
		if (methodElt.toString().equals(
				"arraycopy(java.lang.Object,int,java.lang.Object,int,int)")
				&& classStr != null && classStr.equals("java.lang.System")) {
			ExpressionTree srcTree = arguments.get(0);
			Reference srcRef = Reference.createReference(srcTree, factory);
			// Recursively generate constraints 
			generateConstraint(srcRef, srcTree);
			
			ExpressionTree dstTree = arguments.get(2);
			Reference dstRef = Reference.createReference(dstTree, factory);
			// Recursively generate constraints 
			generateConstraint(dstRef, dstTree);

			// Enforce src <: dst
			InferenceMain.getInstance().getConstraintManager().addSubtypeConstraint(srcRef, dstRef);
		} else if (methodElt.toString().equals(
				"memmove(java.lang.Object,int,java.lang.Object,int,long)")
				&& classStr != null && classStr.equals("libcore.io.Memory")) {
			ExpressionTree srcTree = arguments.get(2);
			Reference srcRef = Reference.createReference(srcTree, factory);
			// Recursively generate constraints 
			generateConstraint(srcRef, srcTree);
			
			ExpressionTree dstTree = arguments.get(0);
			Reference dstRef = Reference.createReference(dstTree, factory);
			// Recursively generate constraints 
			generateConstraint(dstRef, dstTree);

			// Enforce src <: dst
			InferenceMain.getInstance().getConstraintManager().addSubtypeConstraint(srcRef, dstRef);
		}
		else
			super.handleMethodCall(methodElt, arguments, rcvRef, lhsRef);
	}


	@Override
	protected void handleMethodOverride(ExecutableElement overrider, ExecutableElement overridden) {
		super.handleMethodOverride(overrider, overridden);
	}



	/* (non-Javadoc)
	 * @see checkers.inference.InferenceVisitor#getFieldAdaptContext(checkers.inference_new.Reference, checkers.inference_new.Reference, checkers.inference_new.Reference)
	 */
	@Override
	public AdaptContext getFieldAdaptContext(Reference rcvRef,
			Reference fieldRef, Reference assignToRef) {
		return AdaptContext.RECEIVER;
	}

	/* (non-Javadoc)
	 * @see checkers.inference.InferenceVisitor#getMethodAdaptContext(checkers.inference_new.Reference, checkers.inference_new.Reference, checkers.inference_new.Reference)
	 */
	@Override
	public AdaptContext getMethodAdaptContext(Reference rcvRef,
			Reference declRef, Reference assignToRef) {
		// The adapt context for static method is the left-hand-side
		// If revRef is null, then it is a static method
		if (rcvRef == null) {
			if (assignToRef != null)
				return AdaptContext.ASSIGNTO;
			else
				return AdaptContext.NONE;
		} else 
			return AdaptContext.RECEIVER;
	}


	@Override
	protected void typeCheckVectorCopyIntoArgument(MethodInvocationTree node,
			List<? extends AnnotatedTypeMirror> params) {
		// FIXME: Skip
//		super.typeCheckVectorCopyIntoArgument(node, params);
	}
	

}
