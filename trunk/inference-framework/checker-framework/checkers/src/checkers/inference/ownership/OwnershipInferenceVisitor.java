/**
 * 
 */
package checkers.inference.ownership;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

import checkers.inference.InferenceMain;
import checkers.inference.InferenceVisitor;
import checkers.inference.Reference;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionTree;

/**
 * @author huangw5
 *
 */
public class OwnershipInferenceVisitor extends InferenceVisitor {
	
	private List<Reference> repReferences;

	public OwnershipInferenceVisitor(OwnershipChecker checker,
			CompilationUnitTree root) {
		super(checker, root);
		repReferences = new ArrayList<Reference>(4);
		repReferences.add(Reference.createConstantReference(checker.REPREP));
		repReferences.add(Reference.createConstantReference(checker.REPOWN));
		repReferences.add(Reference.createConstantReference(checker.REPPAR));
		repReferences.add(Reference.createConstantReference(checker.REPNOREP));
	}
	
	@Override
	protected void handleArrayRead(Reference lhsRef, Reference exprRef,
			Reference componentRef) {
		super.handleArrayRead(lhsRef, exprRef, componentRef);
		
		// The fields cannot be Rep*
		for (Reference repRef : repReferences)
			InferenceMain.getInstance().getConstraintManager()
					.addInequalityConstraint(componentRef, repRef);
	}



	@Override
	protected void handleFieldRead(Reference lhsRef, Reference rcvRef,
			Reference fieldRef) {
		super.handleFieldRead(lhsRef, rcvRef, fieldRef);
		
		if (!isThisReference(rcvRef)) {
			// The fields cannot be Rep*
			for (Reference repRef : repReferences)
				InferenceMain.getInstance().getConstraintManager()
						.addInequalityConstraint(fieldRef, repRef);
		}
	}



	@Override
	protected void handleArrayWrite(Reference exprRef, Reference componentRef,
			Reference rhsRef) {
		super.handleArrayWrite(exprRef, componentRef, rhsRef);
		// The fields cannot be Rep*
		for (Reference repRef : repReferences)
			InferenceMain.getInstance().getConstraintManager()
					.addInequalityConstraint(componentRef, repRef);
	}



	@Override
	protected void handleFieldWrite(Reference rcvRef, Reference fieldRef,
			Reference rhsRef) {
		super.handleFieldWrite(rcvRef, fieldRef, rhsRef);
		
		if (!isThisReference(rcvRef)) {
			// The fields cannot be Rep*
			for (Reference repRef : repReferences)
				InferenceMain.getInstance().getConstraintManager()
						.addInequalityConstraint(fieldRef, repRef);
		}
	}



	@Override
	protected void handleMethodCall(ExecutableElement methodElt,
			List<? extends ExpressionTree> arguments, Reference rcvRef,
			Reference lhsRef) {
		super.handleMethodCall(methodElt, arguments, rcvRef, lhsRef);
		
		if (rcvRef != null && !isThisReference(rcvRef)) {
			List<? extends VariableElement> parameters = methodElt.getParameters();
			for (VariableElement elt : parameters) {
				Reference paramRef = Reference.createReference(elt, factory);
				for (Reference repRef : repReferences)
					InferenceMain.getInstance().getConstraintManager()
							.addInequalityConstraint(paramRef, repRef);
				}
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
