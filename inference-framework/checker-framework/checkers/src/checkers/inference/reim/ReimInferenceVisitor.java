/**
 * 
 */
package checkers.inference.reim;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import checkers.inference.InferenceMain;
import checkers.inference.InferenceVisitor;
import checkers.inference.Reference;
import checkers.util.ElementUtils;
import checkers.util.TreeUtils;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodTree;

/**
 * @author huangw5
 *
 */
public class ReimInferenceVisitor extends InferenceVisitor {
	
	private ReimChecker checker;

	public ReimInferenceVisitor(ReimChecker checker, CompilationUnitTree root) {
		super(checker, root);
		this.checker = checker;
	}
	
	

	@Override
	protected void handleArrayRead(Reference lhsRef, Reference exprRef, Reference componentRef) {
		// TODO Auto-generated method stub
		super.handleArrayRead(lhsRef, exprRef, componentRef);
	}



	@Override
	protected void handleFieldRead(Reference lhsRef, Reference rcvRef, Reference fieldRef) {
		// WEI: For ReimFlow, we only enforce fieldRef <: lhsRef (UNSAFE!!!)
		// FIXME:
		Element fieldElt = fieldRef.getElement();
		super.handleFieldRead(lhsRef, rcvRef, fieldRef);
//    	if (!ElementUtils.isStatic(fieldElt)) {
//			addSubtypeConstraint(fieldRef, lhsRef);
//	    } else
//			addEqualityConstraint(fieldRef, lhsRef);
    	// WEI: end
		
		if (ElementUtils.isStatic(fieldElt)) {
			// add mutate static constraints
			ExecutableElement currentMethodElt = getCurrentMethodElt();
			if (currentMethodElt != null) {
				Reference methodRef = Reference.createReference(currentMethodElt, factory);
				InferenceMain.getInstance().getConstraintManager().addSubtypeConstraint(methodRef, lhsRef);
			}	
		}
	}



	@Override
	protected void handleArrayWrite(Reference exprRef, Reference componentRef, Reference rhsRef) {
		super.handleArrayWrite(exprRef, componentRef, rhsRef);
		// The exprRef has to be @Mutable
		Reference mutableRef = Reference.createConstantReference(checker.MUTABLE);
		InferenceMain.getInstance().getConstraintManager().addEqualityConstraint(exprRef, mutableRef);
	}



	@Override
	protected void handleFieldWrite(Reference rcvRef, Reference fieldRef, Reference rhsRef) {
		super.handleFieldWrite(rcvRef, fieldRef, rhsRef);
		Element fieldElt = fieldRef.getElement();
		if (fieldElt == null)
			throw new RuntimeException("Null Field!");
		if (!ElementUtils.isStatic(fieldElt)) {
			// TODO: The rcvRef has to be @Mutable if the field cannot be polyread
			// This is for the special case in Enumeration
			if (fieldRef.getAnnotations().contains(checker.POLYREAD)) {
				// In this case, we keep the dependence of the receiver and the field
				InferenceMain.getInstance().getConstraintManager().addSubtypeConstraint(rcvRef, fieldRef);
			} else {
				// If the enclosing method is a Constructor and the rcvRef is "this"
				// We should not enforce this constraint. Dec 1, 2012
//				MethodTree enclosingMethod = TreeUtils.enclosingMethod(getCurrentPath());
//				if (enclosingMethod != null && TreeUtils.isConstructor(enclosingMethod)
//						&& rcvRef.getReadableName() != null 
//						&& rcvRef.getReadableName().startsWith("THIS_")) {
//					// Skip
//				} else {
				Reference mutableRef = Reference.createConstantReference(checker.MUTABLE);
				InferenceMain.getInstance().getConstraintManager().addEqualityConstraint(rcvRef, mutableRef);
//				}
			}
		} else {
			// We add the mutateStatic constraints
			ExecutableElement currentMethodElt = getCurrentMethodElt();
			if (currentMethodElt != null) {
				Reference methodRef = Reference.createReference(currentMethodElt, factory);
				Reference mutableRef = Reference.createConstantReference(checker.MUTABLE);
				InferenceMain.getInstance().getConstraintManager().addEqualityConstraint(methodRef, mutableRef);
			}
		}
	}


	@Override
	protected void handleMethodCall(ExecutableElement methodElt,
			List<? extends ExpressionTree> arguments, Reference rcvRef, Reference lhsRef) {
		super.handleMethodCall(methodElt, arguments, rcvRef, lhsRef);
		// We add the mutateStatic constraints. However, if the method being
		// invoked is default pure like "toString()", we skip it.
		// Here we enfource that ST_current <:  ST_lhs |> ST_method if 
		// methodElt is static
		ExecutableElement currentMethodElt = getCurrentMethodElt();
		if (currentMethodElt != null && !checker.isDefaultPureMethod(methodElt)) {
			Reference currentMethodRef = Reference.createReference(currentMethodElt, factory);
			Reference methodRef = Reference.createReference(methodElt, factory);
			if (ElementUtils.isStatic(methodElt))
				InferenceMain.getInstance().getConstraintManager().addSubtypeConstraint(currentMethodRef,
						getMethodAdaptReference(rcvRef, methodRef, lhsRef));
			else	
				InferenceMain.getInstance().getConstraintManager().addSubtypeConstraint(currentMethodRef, methodRef);
		}
	}


	@Override
	protected void handleMethodOverride(ExecutableElement overrider, ExecutableElement overridden) {
		// add constraints except that overridden is default pure
		Reference overriderRef = Reference.createReference(overrider, factory);
		Reference overriddenRef = Reference.createReference(overridden, factory);
		if (!checker.isDefaultPureMethod(overridden)) {
			super.handleMethodOverride(overrider, overridden);
			InferenceMain.getInstance().getConstraintManager().addSubtypeConstraint(overriddenRef, overriderRef);
		}
	}



	/* (non-Javadoc)
	 * @see checkers.inference_new.InferenceVisitor#getFieldAdaptContext(checkers.inference_new.Reference, checkers.inference_new.Reference, checkers.inference_new.Reference)
	 */
	@Override
	public AdaptContext getFieldAdaptContext(Reference rcvRef,
			Reference fieldRef, Reference assignToRef) {
		return AdaptContext.RECEIVER;
	}

	/* (non-Javadoc)
	 * @see checkers.inference_new.InferenceVisitor#getMethodAdaptContext(checkers.inference_new.Reference, checkers.inference_new.Reference, checkers.inference_new.Reference)
	 */
	@Override
	public AdaptContext getMethodAdaptContext(Reference rcvRef,
			Reference declRef, Reference assignToRef) {
		if (assignToRef == null)
			return AdaptContext.NONE;
		else 
			return AdaptContext.ASSIGNTO;
	}
	

}
