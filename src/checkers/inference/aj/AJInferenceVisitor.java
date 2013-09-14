/**
 * 
 */
package checkers.inference.aj;

import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeKind;

import checkers.inference.Constraint;
import checkers.inference.InferenceMain;
import checkers.inference.InferenceVisitor;
import checkers.inference.Reference;
import checkers.inference.Reference.ExecutableReference;
import checkers.util.ElementUtils;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;

/**
 * @author huangw5
 *
 */
public class AJInferenceVisitor extends InferenceVisitor {
	
	private AJChecker checker;

	public AJInferenceVisitor(AJChecker checker, CompilationUnitTree root) {
		super(checker, root);
		this.checker = checker;
	}
	
	

	@Override
	protected void handleFieldRead(Reference lhsRef, Reference rcvRef,
			Reference fieldRef) {
		super.handleFieldRead(lhsRef, rcvRef, fieldRef);
//		if (rcvRef != null && isThisReference(rcvRef)) {
//			// No adaptation needed
//			InferenceMain.getInstance().getConstraintManager().addEqualityConstraint(
//					fieldRef, lhsRef);
//			return;
//		}
		// fieldRef cannot be INTALIASED
//		InferenceMain.getInstance().getConstraintManager().addInequalityConstraint(
//				fieldRef, Reference.createConstantReference(checker.INTALIASED));
	}



	@Override
	protected void handleFieldWrite(Reference rcvRef, Reference fieldRef,
			Reference rhsRef) {
		super.handleFieldWrite(rcvRef, fieldRef, rhsRef);
//		if (rcvRef != null && isThisReference(rcvRef)) {
//			// No adaptation needed
//			InferenceMain.getInstance().getConstraintManager().addEqualityConstraint(
//					fieldRef, rhsRef);
//			return;
//		}
//		// fieldRef cannot be INTALIASED
//		InferenceMain.getInstance().getConstraintManager().addInequalityConstraint(
//				fieldRef, Reference.createConstantReference(checker.INTALIASED));
	}



	@Override
	protected void handleMethodCall(ExecutableElement methodElt,
			List<? extends ExpressionTree> arguments, Reference rcvRef,
			Reference lhsRef) {
		super.handleMethodCall(methodElt, arguments, rcvRef, lhsRef);
		
        // WEI: Remove INTALIASED if the method return a non-void
        // reference
//		if ((rcvRef == null || !isThisReference(rcvRef)) 
//				&& methodElt.getReturnType().getKind() != TypeKind.VOID) {
//			// If the return type is not void, forbid it from being INTALIASED
//			Reference methodRef = Reference.createReference(methodElt, factory);
//			Reference returnRef = ((ExecutableReference) methodRef).getReturnRef();
//			InferenceMain.getInstance().getConstraintManager().addInequalityConstraint(
//					returnRef, Reference.createConstantReference(checker.INTALIASED));
//		}
		
		// If rcvRef is intaliased, the receiver of methodElt has to be intaliased
		// It is q_r = intaliased ==>  q_this = intaliased, which is equivalent  to
		// ~(q_r = intaliased) V (q_this = intaliased). We construct an OrConconstraint
		// TODO
		// First check if it is a constructor
//		Tree tree = (rcvRef != null? rcvRef.getTree() : null);
//		if (!ElementUtils.isStatic(methodElt) 
//				&& tree != null && tree.getKind() != Kind.NEW_CLASS) {
//			Reference methodRef = Reference.createReference(methodElt, factory);
//			Reference thisRef = ((ExecutableReference) methodRef).getReceiverRef();
//			Reference iaRef = Reference.createConstantReference(checker.INTALIASED);
//			Constraint condition = new Constraint.EqualityConstraint(rcvRef, iaRef);
//			Reference isRef = Reference.createConstantReference(checker.INTSELF);
//			Constraint ifConstraint = new Constraint.EqualityConstraint(thisRef, isRef);
//			InferenceMain.getInstance().getConstraintManager()
//					.addIfConstraint(condition, ifConstraint, null);
//		}
	}



	/* (non-Javadoc)
	 * @see checkers.inference.InferenceVisitor#getFieldAdaptContext(checkers.inference.Reference, checkers.inference.Reference, checkers.inference.Reference)
	 */
	@Override
	public AdaptContext getFieldAdaptContext(Reference rcvRef,
			Reference fieldRef, Reference assignToRef) {
		if (rcvRef == null || isThisReference(rcvRef))
			return AdaptContext.NONE;
		else 
			return AdaptContext.RECEIVER;
	}

	/* (non-Javadoc)
	 * @see checkers.inference.InferenceVisitor#getMethodAdaptContext(checkers.inference.Reference, checkers.inference.Reference, checkers.inference.Reference)
	 */
	@Override
	public AdaptContext getMethodAdaptContext(Reference rcvRef,
			Reference declRef, Reference assignToRef) {
		return getFieldAdaptContext(rcvRef, declRef, assignToRef);
	}

}
