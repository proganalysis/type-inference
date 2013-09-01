/**
 * 
 */
package checkers.inference.sflow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;

import checkers.inference.InferenceUtils;
import checkers.inference.InferenceVisitor;
import checkers.inference.Reference;
import checkers.inference.Reference.AdaptReference;
import checkers.inference.Reference.ArrayReference;
import checkers.inference.Reference.ExecutableReference;
import checkers.inference.Reference.FieldAdaptReference;
import checkers.types.AnnotatedTypeMirror;
import checkers.util.AnnotationUtils;
import checkers.util.ElementUtils;
import checkers.util.TreeUtils;

import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.ParenthesizedTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.TypeCastTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import com.sun.tools.javac.code.Symbol.MethodSymbol;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeInfo;

/**
 * @author huangw5
 *
 */
public class SFlowInferenceVisitor extends InferenceVisitor {
	
	private SFlowChecker checker;
	
//	private Set<AnnotationMirror> reimQuals; 
	
	private class IndexEntry {
		int keyIndex;
		int valueIndex;
		public IndexEntry(int keyIndex, int valueIndex) {
			this.keyIndex = keyIndex;
			this.valueIndex = valueIndex;
		}
	}

	public SFlowInferenceVisitor(SFlowChecker checker, CompilationUnitTree root) {
		super(checker, root);
		this.checker = checker;
//		reimQuals = AnnotationUtils.createAnnotationSet();
//		reimQuals.add(checker.READONLY);
//		reimQuals.add(checker.POLYREAD);
//		reimQuals.add(checker.MUTABLE);
//		reimQuals.add(checker.UNCHECK);
	}	
	
	private boolean containsUncheck(Reference ref) {
		return containsAnno(ref, checker.UNCHECK);
	}
	
	private boolean containsReadonly(Reference ref) {
		return containsAnno(ref, checker.READONLY);
	}
	
	private boolean containsAnno(Reference ref, AnnotationMirror anno) {
		AnnotatedTypeMirror type = ref.getType();
		if (type != null && checker.isReadonlyType(type))
			return true;
		// FIXME:
		if (ref instanceof AdaptReference) {
			Reference contextRef = ((AdaptReference) ref).getContextRef();
			Reference declRef = ((AdaptReference) ref).getDeclRef();
			if (ref instanceof FieldAdaptReference) {
				if ((contextRef != null && containsAnno(contextRef, anno))
						|| containsAnno(declRef, anno)) {
					return true;
				}
			}
			else {
				if (containsAnno(declRef, anno)) {
					return true;
				}
			}
		}
		for (AnnotationMirror a : ref.getAnnotations()) {
			if (a.toString().equals(anno.toString()))
				return true;
		}
		return false;
	}
	
//	private boolean containsReadonly(Set<AnnotationMirror> annos) {
//		return false;
//	}
	
//	private void filterReimQuals(Reference ref) {
//		if (ref instanceof AdaptReference) {
//			filterReimQuals(((AdaptReference) ref).getContextRef());
//			filterReimQuals(((AdaptReference) ref).getDeclRef());
//		} else {
//			if (ref instanceof ArrayReference) {
//				filterReimQuals(((ArrayReference) ref).getComponentRef());
//			}
//			Set<AnnotationMirror> set = InferenceUtils
//					.differAnnotations(ref.getAnnotations(), reimQuals);
//			ref.setAnnotations(set);
//		}
//	}
	
	private String getArgumentSignature(ExpressionTree expr) {
		while (expr.getKind() == Kind.TYPE_CAST 
				|| expr.getKind() == Kind.PARENTHESIZED) {
			expr = TreeUtils.skipParens(expr);
			if (expr.getKind() == Kind.TYPE_CAST)
				expr = ((ParenthesizedTree) expr).getExpression();
		}
		String key = "";
		Element elt = TreeUtils.elementFromUse(expr);
//		if (elt == null)
//			System.out.println("expr = " + expr + "\n" + getCurrentPath().getLeaf().toString());
		if (expr instanceof LiteralTree)
			key = expr.toString();
		else if (elt.getKind() !=  ElementKind.LOCAL_VARIABLE){
			key = InferenceUtils.getElementSignature(elt);
		} else {
			Tree decl = checker.getDeclaration(elt);
			key = factory.getFileName(decl) + ":"
					+ TreeInfo.getStartPos((JCTree) decl) + ":"
					+ decl.toString();
		}
		return key;
	}
	
	private boolean isLeftViewpointInstanceMethod(ExecutableElement methodElt) {
		String ownerStr = ((MethodSymbol) methodElt).owner.toString();
		String methodStr = methodElt.toString();
		if (ownerStr.equals("java.text.DateFormat")
				&& methodStr.equals("format(java.util.Date)")) {
			return true;
		}
		return false;
	}

	private IndexEntry isConstantSetMappingMethod(ExecutableElement methodElt,
			List<? extends ExpressionTree> arguments) {
		if (arguments.size() < 2)
			return null;
		
		String ownerStr = ((MethodSymbol) methodElt).owner.toString();
		String methodStr = methodElt.toString();
		Element elt = null;
		
		
		if (((ownerStr.equals("javax.servlet.ServletRequest")
				|| ownerStr.equals("javax.servlet.http.HttpServletRequest")
				|| ownerStr.equals("javax.servlet.http.HttpSession"))
				&& methodStr.equals("setAttribute(java.lang.String,java.lang.Object)"))
				||
			(ownerStr.equals("java.util.Properties")
					&& (methodStr.equals("setProperty(java.lang.String,java.lang.String)")
						|| methodStr.startsWith("put(")))
				||
			(((ownerStr.equals("javax.servlet.jsp.JspContext")
					|| ownerStr.equals("javax.servlet.jsp.PageContext"))
				&& methodStr.startsWith("setAttribute(java.lang.String,java.lang.Object")))
				||
			((ownerStr.equals("java.util.Map")
					|| ownerStr.equals("java.util.Hashtable")				
					|| ownerStr.equals("java.util.HashMap"))
					&& methodStr.startsWith("put("))
				||
			(ownerStr.equals("org.apache.velocity.context.Context")
					&& methodStr.equals("put(java.lang.String,java.lang.Object)"))
					) {
				ExpressionTree expr = TreeUtils.skipParens(arguments.get(0));
//				if (getCurrentPath().getLeaf().toString().contains("m_props.put(")) {
//					System.out.println("ownerStr = " + ownerStr + "\nmethodStr = " + methodStr
//						+ "\n" + getCurrentPath().getLeaf().toString()
//						+ "\n" + "expr = " + expr + " type = " + expr.getClass());
//				}
			
			if (expr instanceof LiteralTree && expr.getKind() != Kind.NULL_LITERAL
				|| (elt = TreeUtils.elementFromUse(expr)) != null 
					&& elt.getModifiers().contains(Modifier.FINAL)
				) {
			
				return new IndexEntry(0, 1);
			}
		}
		else if (
				ownerStr.equals("javax.servlet.jsp.jstl.core.Config")
				&& methodStr.startsWith("set(")
				) {
			ExpressionTree expr = TreeUtils.skipParens(arguments.get(1));
			if (expr instanceof LiteralTree && expr.getKind() != Kind.NULL_LITERAL
				|| (elt = TreeUtils.elementFromUse(expr)) != null 
				&& elt.getModifiers().contains(Modifier.FINAL)
				)
//				System.out.println("Skip " + methodElt);
				return new IndexEntry(1, 2);
		}
			
		return null;
	}

	private IndexEntry isConstantGetMappingMethod(ExecutableElement methodElt,
			List<? extends ExpressionTree> arguments) {
		if (arguments.size() == 0)
			return null;
		String ownerStr = ((MethodSymbol) methodElt).owner.toString();
		String methodStr = methodElt.toString();
		Element elt = null;
		
		if (((ownerStr.equals("javax.servlet.ServletRequest")
				|| ownerStr.equals("javax.servlet.http.HttpSession")
				|| ownerStr.equals("javax.servlet.http.HttpServletRequest"))
				&& methodStr.equals("getAttribute(java.lang.String)")
				)
				||
		(ownerStr.equals("java.util.Properties")
				&& (methodStr.startsWith("getProperty(java.lang.String")
					|| methodStr.startsWith("get(")))
				||
		((ownerStr.equals("java.util.Map")
				|| ownerStr.equals("java.util.Hashtable")				
				|| ownerStr.equals("java.util.HashMap"))
				&& methodStr.startsWith("get("))
				||
		(((ownerStr.equals("javax.servlet.jsp.JspContext")
				|| ownerStr.equals("javax.servlet.jsp.PageContext"))
				&& (methodStr.startsWith("getAttribute(java.lang.String")
					|| methodStr.equals("findAttribute(java.lang.String)"))))
				||
		(ownerStr.equals("org.apache.velocity.context.Context")
				&& methodStr.equals("get(java.lang.String)"))) {
			ExpressionTree expr = TreeUtils.skipParens(arguments.get(0));
			if (expr instanceof LiteralTree && expr.getKind() != Kind.NULL_LITERAL
				|| (elt = TreeUtils.elementFromUse(expr)) != null 
				&& elt.getModifiers().contains(Modifier.FINAL)
				) {
				return new IndexEntry(0, 0);
			}
		}
		else if (
				ownerStr.equals("javax.servlet.jsp.jstl.core.Config")
				) {
			// TODO
		}
		
		
		return null;
	}
	
	private void addMappingConstraint(String key, Reference valueRef) {
		Reference keyRef = mappingKeys.get(key);
		if (keyRef == null) {
			keyRef = Reference.createConstantReference(AnnotationUtils.createAnnotationSet(), key);
			mappingKeys.put(key, keyRef);
		}
		addEqualityConstraint(keyRef, valueRef);
		if (valueRef instanceof ArrayReference) {
			String componentKey = key + "[]";
			addMappingConstraint(componentKey, ((ArrayReference) valueRef).getComponentRef());
		}
	}
	
	@Override
	protected void addSubtypeConstraint(Reference sub, Reference sup) {
		if (checker.isUseReim()) {
			// We enforce equality constraint if the LHS (sup) is not readonly
			// But if sup is ClassType (default constructor), we also treat it
			// as readonly
			boolean hasReadonlySup = false;
			if (containsReadonly(sup) || containsReadonly(sub)) // WEI: add sub Mar 16
				hasReadonlySup = true;
			else if (sup.getElement() != null 
						&& (sup.getElement().getKind() == ElementKind.CLASS 
							|| sup.getElement().getKind() == ElementKind.INTERFACE))
				hasReadonlySup = true;
			
//			filterReimQuals(sub);
//			filterReimQuals(sup);
			if (!hasReadonlySup)
				super.addEqualityConstraint(sub, sup);
			else
				super.addSubtypeConstraint(sub, sup);
		}
		else {
			// Only use subtyping when there are default readonly types
			// TODO: improve the code! But this part will be probably removed
			// in the future. 
			boolean hasReadonlySup = false;
			if (sup.getType() != null && sup.getType().getKind().isPrimitive())
//					checker.isReadonlyType(sup.getType()))
				hasReadonlySup = true;
			else if (sup.getElement() != null 
						&& (sup.getElement().getKind() == ElementKind.CLASS 
							|| sup.getElement().getKind() == ElementKind.INTERFACE))
				hasReadonlySup = true;
			
//			filterReimQuals(sub);
//			filterReimQuals(sup);
			if (!hasReadonlySup) {
				super.addEqualityConstraint(sub, sup);
			} else
				super.addSubtypeConstraint(sub, sup);
		}
	}
	
	@Override
	protected void addEqualityConstraint(Reference left, Reference right) {
//		filterReimQuals(left);
//		filterReimQuals(right);
		super.addEqualityConstraint(left, right);
	}
	
	@Override
	protected void addInequalityConstraint(Reference left, Reference right) {
//		filterReimQuals(left);
//		filterReimQuals(right);
		super.addInequalityConstraint(left, right);
	}
	
	@Override
	protected void addEmptyConstraint(Reference ref) {
//		filterReimQuals(ref);
		super.addEmptyConstraint(ref);
	}

	@Override
	protected void handleArrayRead(Reference lhsRef, Reference exprRef, Reference componentRef) {
		super.handleArrayRead(lhsRef, exprRef, componentRef);
		// The componentRef cannot be TAINTED
		if (componentRef.getAnnotations().isEmpty()) {
			addInequalityConstraint(componentRef,
					Reference.createConstantReference(checker.TAINTED));
		}
	}


	@Override
	protected void handleFieldRead(Reference lhsRef, Reference rcvRef, Reference fieldRef) {
		super.handleFieldRead(lhsRef, rcvRef, fieldRef);
		// The instant field cannot be Tainted
		// WEI: now we allow it
//		Element fieldElt = fieldRef.getElement();
//		if (!ElementUtils.isStatic(fieldElt) && fieldRef.getAnnotations().isEmpty())
//			addInequalityConstraint(fieldRef,
//					Reference.createConstantReference(checker.TAINTED));
	}


	@Override
	protected void handleArrayWrite(Reference exprRef, Reference componentRef, Reference rhsRef) {
		super.handleArrayWrite(exprRef, componentRef, rhsRef);
		// The componentRef cannot be TAINTED
		// WEI: now we allow it
		if (componentRef.getAnnotations().isEmpty()) {
			addInequalityConstraint(componentRef,
					Reference.createConstantReference(checker.TAINTED));
		}
	}


	@Override
	protected void handleFieldWrite(Reference rcvRef, Reference fieldRef, Reference rhsRef) {
		super.handleFieldWrite(rcvRef, fieldRef, rhsRef);
		// The instant field cannot be Tainted
		// WEI: now we allow it
//		Element fieldElt = fieldRef.getElement();
//		if (!ElementUtils.isStatic(fieldElt) && fieldRef.getAnnotations().isEmpty())
//			addInequalityConstraint(fieldRef,
//					Reference.createConstantReference(checker.TAINTED));
	}


	@Override
	protected void handleMethodCall(ExecutableElement methodElt,
			List<? extends ExpressionTree> arguments, Reference rcvRef, Reference lhsRef) {
		// FIXME: Special-case for System.arraycopy and Memory.memmove: 
		// We only enforce the constraint that src <: dst
		String classStr = methodElt.getEnclosingElement().toString();
		String ownerStr = ((MethodSymbol) methodElt).owner.toString();
		String methodStr = methodElt.toString();
		IndexEntry ie = null;
		Reference methodRef = Reference.createReference(methodElt, factory);
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
			addSubtypeConstraint(srcRef, dstRef);
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
			addSubtypeConstraint(srcRef, dstRef);
		} else if (classStr != null && classStr.equals("libcore.io.Memory") 
				&& (methodElt.toString().startsWith("poke") || methodElt.toString().startsWith("peek"))) {
			// Special-case those "peek" and "poke" methods
			// Generate constraints for parameters: t_z <: _ |> t_p
			List<? extends VariableElement> parameters = methodElt.getParameters();
			int size = parameters.size() > arguments.size() ? 
					arguments.size() : parameters.size();
			for (int i = 0; i < size; i++) {
				VariableElement paramElt = parameters.get(i);
				Reference paramRef = Reference.createReference(paramElt, factory);
				// FIXME: assume it is readonly
				Set<AnnotationMirror> annos = paramRef.getAnnotations();
				annos.add(checker.READONLY);
				paramRef.setAnnotations(annos);
				ExpressionTree argTree = arguments.get(i);
				Reference argRef = Reference.createReference(argTree, factory);
				// Recursively generate constraints 
				generateConstraint(argRef, argTree);
				
				// rcvRef is null if the method is static 
				Reference adaptRef = getMethodAdaptReference(rcvRef, 
						paramRef, lhsRef);
				addSubtypeConstraint(argRef, adaptRef);
			}
		} 
		else if ((ie = isConstantSetMappingMethod(methodElt, arguments)) != null) {
			// FIXME: this can be improved...
			String key = getArgumentSignature(arguments.get(ie.keyIndex));
			Reference valueRef = Reference.createReference(arguments.get(ie.valueIndex), factory);
			addMappingConstraint(key, valueRef);
			// Recursively generate constraints 
			generateConstraint(valueRef, arguments.get(ie.valueIndex));
		}
		else if ((ie = isConstantGetMappingMethod(methodElt, arguments)) != null) {
			String key = getArgumentSignature(arguments.get(ie.keyIndex));
			addMappingConstraint(key, lhsRef);
		}
		else if ((ownerStr.equals("javax.servlet.ServletRequest")
				|| ownerStr.equals("javax.servlet.http.HttpServletRequest"))
				&& !methodStr.equals("getAttribute(java.lang.String)")
				&& !methodStr.equals("setAttribute(java.lang.String,java.lang.Object)")
				&& !((ExecutableReference) methodRef).getReturnRef().getAnnotations().contains(checker.SECRET)
				) {
			// FIXME: 
			// Generate constraints for actual arguments
			int size = arguments.size();
			for (int i = 0; i < size; i++) {
				ExpressionTree argTree = arguments.get(i);
				Reference argRef = Reference.createReference(argTree, factory);
				// Recursively generate constraints 
				generateConstraint(argRef, argTree);
			}
		}
		else if (ElementUtils.isStatic(methodElt)
				&& methodElt.getReturnType().getKind() == TypeKind.VOID
				&& checker.isFromLibrary(methodElt)
				&& ((ownerStr.equals("java.util.Arrays") 
						|| ownerStr.equals("java.util.Collections")) 
						&& methodElt.toString().contains("sort("))
				) {
	 		// SKIP
//			if (currentInvocation != null)
//				System.out.println("INFO: Skip hanlding " + currentInvocation);
		}
		else if (isLeftViewpointInstanceMethod(methodElt)) {
			// We may want to special-case some instance methods with viewpoint on
			// the left. 
			super.handleMethodCall(methodElt, arguments, null, lhsRef);
		}
		else {
			if (methodElt.toString().contains("sort"))
					System.out.println("KEEP SORT: " + ownerStr + " " + methodElt);
			super.handleMethodCall(methodElt, arguments, rcvRef, lhsRef);
		}
	}

	/**
	 * For special-casing getAttribute, etc.
	 */
	private static Map<String, Reference> mappingKeys = new HashMap<String, Reference>();

	@Override
	protected void handleMethodOverride(ExecutableElement overrider, ExecutableElement overridden) {
//		super.handleMethodOverride(overrider, overridden);
		
		// FIXME: we skip some special methods
		if (checker.isSpecialMethod(overridden))
			return;
		
		// FIXME: we don't enforce library subtying Mar 8, 2013
//		if (checker.isFromLibrary(overridden))
//			return;
		
		// FIXME: we relax the subtyping constraint on THIS
		ExecutableReference overriderRef = (ExecutableReference) Reference
				.createReference(overrider, factory);
		ExecutableReference overriddenRef = (ExecutableReference) Reference
				.createReference(overridden, factory);
		
		// Method Receiver: overridden <: overrider  
		Reference overriderRcvRef = overriderRef.getReceiverRef();
		Reference overriddenRcvRef = overriddenRef.getReceiverRef();
		// Here, we don't enforce equality even if they are not readonly
		// Mar 30: we only add override constraint for library methods when there
		// are annotations.
		if (!checker.isFromLibrary(overridden) 
				|| !InferenceUtils.intersectAnnotations(overriddenRcvRef.getAnnotations(), checker.getSourceLevelQualifiers()).isEmpty())
			super.addSubtypeConstraint(overriddenRcvRef, overriderRcvRef);
		
		// Parameters: overridden <: overrider  
    	List<? extends VariableElement> overriderParams = overrider.getParameters();
    	List<? extends VariableElement> overriddenParams = overridden.getParameters();
    	int size = overriderParams.size() > overriddenParams.size() ? 
    			overriddenParams.size() : overriderParams.size();
    	for (int i = 0; i < size; i++) {
    		VariableElement overriderParam = overriderParams.get(i);
    		Reference overriderParamRef = Reference.createReference(overriderParam, 
    				factory);
    		VariableElement overriddenParam = overriddenParams.get(i);
    		Reference overriddenParamRef = Reference.createReference(overriddenParam, 
    				factory);
			if (!checker.isFromLibrary(overridden) 
				|| !InferenceUtils.intersectAnnotations(overriddenParamRef.getAnnotations(), checker.getSourceLevelQualifiers()).isEmpty())
				super.addSubtypeConstraint(overriddenParamRef, overriderParamRef);
    	}
    	
    	if (overrider.getReturnType().getKind() == TypeKind.VOID)
    		return;
    	// Returns: overrider <: overridden
    	Reference overriderReturnRef = overriderRef.getReturnRef();
    	Reference overriddenReturnRef = overriddenRef.getReturnRef();
    	// Skip for library methods
		if (!checker.isFromLibrary(overridden))
			super.addSubtypeConstraint(overriderReturnRef, overriddenReturnRef);
	}

	


	@Override
	public Void visitTypeCast(TypeCastTree node, Void p) {
		// FIXME: special case for BlogEntry[] entryArray 
		// = (BlogEntry[]) request.getAttribute(BlojsomConstants.BLOJSOM_ENTRIES);
		ExpressionTree expr = node.getExpression();
		expr = TreeUtils.skipParens(expr);
		if (expr.getKind() == Kind.METHOD_INVOCATION 
				&& factory.getAnnotatedType(node).getKind() == TypeKind.ARRAY) {
			MethodInvocationTree mTree = (MethodInvocationTree) expr;
			ExecutableElement methodElt = TreeUtils.elementFromUse(mTree);
			IndexEntry ie = null;
			if ((ie = isConstantGetMappingMethod(methodElt, mTree.getArguments())) != null) {
				// look for the LHS
				TreePath currentPath = getCurrentPath();
				Tree leaf = currentPath.getLeaf();
		    	while (currentPath != null && !(leaf instanceof AssignmentTree 
		    			|| leaf instanceof VariableTree)) {
		    		currentPath = currentPath.getParentPath();
		    		leaf = currentPath.getLeaf();
		    	}
		    	if (currentPath != null) {
		    		Reference valueRef = null;
		    		if (leaf instanceof AssignmentTree)
						valueRef = Reference.createReference(
								((AssignmentTree) leaf).getVariable(), factory);
		    		else if (leaf instanceof VariableTree)
						valueRef = Reference.createReference(
								TreeUtils.elementFromDeclaration((VariableTree) leaf),
								factory);
					String key = getArgumentSignature(mTree.getArguments().get(ie.keyIndex));
		    		if (valueRef != null)
		    			addMappingConstraint(key, valueRef);
		    	}
			}
		}
		return super.visitTypeCast(node, p);
	}

	@Override
	public Void visitVariable(VariableTree node, Void p) {
		ClassTree enclosingClass = TreeUtils.enclosingClass(getCurrentPath());
		return super.visitVariable(node, p);
	}
	
	
//	private MethodInvocationTree currentInvocation = null;

	@Override
	public Void visitMethodInvocation(MethodInvocationTree node, Void p) {
		// FIXME: Special case for System.out.print / System.err.print
    	ExecutableElement methodElt = TreeUtils.elementFromUse(node);
    	
		if (((MethodSymbol) methodElt).owner.toString().equals(
				"java.io.PrintStream") && methodElt.toString().startsWith("print")
				&& node.toString().startsWith("System.")
				)	
			// skip
			return p;
//		if (node.toString().contains("m_props.put("))
//			System.out.println("INFOFO: " + methodElt + "   " + ((MethodSymbol) methodElt).owner.toString());
//		currentInvocation = node;
		Void v = super.visitMethodInvocation(node, p);
//		currentInvocation = null;
		return v;
	}

	@Override
	protected Reference getMethodAdaptReference(Reference rcvRef, 
			Reference declRef, Reference assignToRef) {
		switch (getMethodAdaptContext(rcvRef, declRef, assignToRef)) {
		case NONE:
			return declRef;
		case RECEIVER:
			return Reference.createMethodAdaptReference(rcvRef, declRef);
		case ASSIGNTO:
			return Reference.createMethodAdaptReference(assignToRef, declRef);
		}
		System.out.println("ERROR: No adapt context is found!");
		return null;
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
//		// We may want to special-case some instance methods with viewpoint on
//		// the left. 
//		Element elt = null;
//		if (declRef != null && assignToRef != null
//				&& (elt = declRef.getElement()) != null
//				&& elt instanceof ExecutableElement
//				&& isLeftViewpointInstanceMethod((ExecutableElement) elt)) {
//			return AdaptContext.ASSIGNTO;
//		}
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
