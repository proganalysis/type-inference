/**
 * 
 */
package checkers.inference.universe;

import java.util.List;

import javax.lang.model.element.ExecutableElement;

import checkers.inference.InferenceMain;
import checkers.inference.InferenceVisitor;
import checkers.inference.Reference;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;

/**
 * @author huangw5
 *
 */
public class UniverseInferenceVisitor extends InferenceVisitor {
	
	private UniverseChecker checker;


	public UniverseInferenceVisitor(UniverseChecker checker,
			CompilationUnitTree root) {
		super(checker, root);
		this.checker = checker;
	}
	
	

	@Override
	protected void handleArrayWrite(Reference exprRef, Reference componentRef,
			Reference rhsRef) {
		super.handleArrayWrite(exprRef, componentRef, rhsRef);
		
		// Enforce that the componentRef cannot be REP
		InferenceMain.getInstance().getConstraintManager().addInequalityConstraint(
				componentRef, Reference.createConstantReference(checker.REP));
		
		if (!checker.isDefaultAnyType(exprRef.getType())) {
			// the rcvRef cannot be ANY and LOST
			InferenceMain.getInstance().getConstraintManager().addInequalityConstraint(
					exprRef, Reference.createConstantReference(checker.ANY));
			InferenceMain.getInstance().getConstraintManager().addInequalityConstraint(
					exprRef, Reference.createConstantReference(checker.LOST));
		}
	}

	


	@Override
	protected void handleFieldRead(Reference lhsRef, Reference rcvRef,
			Reference fieldRef) {
		super.handleFieldRead(lhsRef, rcvRef, fieldRef);
		
		// TODO: WEI: remove the following lines?
		if (rcvRef == null)
			return;
		if(isThisReference(rcvRef)) {
			// because no adaptation is needed. fieldRef should be equal to rhsRef
			InferenceMain.getInstance().getConstraintManager().addEqualityConstraint(
					fieldRef, lhsRef);
			return;
		}
	}



	@Override
	protected void handleFieldWrite(Reference rcvRef, Reference fieldRef,
			Reference rhsRef) {
		super.handleFieldWrite(rcvRef, fieldRef, rhsRef);
		
		// TODO: WEI: remove the following lines?
		if (rcvRef == null)
			return;
		if(isThisReference(rcvRef)) {
			// because no adaptation is needed. fieldRef should be equal to rhsRef
			InferenceMain.getInstance().getConstraintManager().addEqualityConstraint(
					fieldRef, rhsRef);
			return;
		}
		
		// Enforce that the fieldRef cannot be REP
		InferenceMain.getInstance().getConstraintManager().addInequalityConstraint(
				fieldRef, Reference.createConstantReference(checker.REP));
		
		if (!checker.isDefaultAnyType(rcvRef.getType())) {
			// the rcvRef cannot be ANY and LOST
			InferenceMain.getInstance().getConstraintManager().addInequalityConstraint(
					rcvRef, Reference.createConstantReference(checker.ANY));
			InferenceMain.getInstance().getConstraintManager().addInequalityConstraint(
					rcvRef, Reference.createConstantReference(checker.LOST));
		}
	}



	@Override
	protected void handleMethodCall(ExecutableElement methodElt,
			List<? extends ExpressionTree> arguments, Reference rcvRef,
			Reference lhsRef) {
		super.handleMethodCall(methodElt, arguments, rcvRef, lhsRef);
		
		// First check if it is a constructor
		Tree tree = (rcvRef != null? rcvRef.getTree() : null);
		if (tree != null && tree.getKind() == Kind.NEW_CLASS
				&& !checker.isDefaultAnyType(rcvRef.getType())) {
			// A constructor, the receiver cannot be ANY
			InferenceMain.getInstance().getConstraintManager().addInequalityConstraint(
					rcvRef, Reference.createConstantReference(checker.ANY));
		}
		
		// Now check if the method being invoked is pure or not
		if (rcvRef != null && !isThisReference(rcvRef) 
				&& !checker.isDefaultAnyType(rcvRef.getType())
				&& !checker.isPureLibraryMethod(methodElt)) {
			// The receiver cannot be REP or LOST
			InferenceMain.getInstance().getConstraintManager().addInequalityConstraint(
					rcvRef, Reference.createConstantReference(checker.ANY));
			InferenceMain.getInstance().getConstraintManager().addInequalityConstraint(
					rcvRef, Reference.createConstantReference(checker.LOST));
		}
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
