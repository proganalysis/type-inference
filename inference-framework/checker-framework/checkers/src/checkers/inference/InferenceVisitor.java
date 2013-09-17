/**
 * 
 */
package checkers.inference;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.ElementFilter;

import checkers.basetype.BaseTypeVisitor;
import checkers.inference.Reference.ArrayReference;
import checkers.inference.Reference.ConstantReference;
import checkers.inference.Reference.DeclaredReference;
import checkers.inference.Reference.ExecutableReference;
import checkers.inference.Reference.PrimitiveReference;
import checkers.inference.Reference.FieldAdaptReference;
import checkers.inference.Reference.MethodAdaptReference;
import checkers.source.Result;
import checkers.types.AnnotatedTypeMirror;
import checkers.types.AnnotatedTypeMirror.AnnotatedDeclaredType;
import checkers.types.AnnotatedTypeMirror.AnnotatedExecutableType;
import checkers.types.AnnotatedTypeMirror.AnnotatedTypeVariable;
import checkers.util.ElementUtils;
import checkers.util.Pair;
import checkers.util.TreeUtils;

import com.sun.source.tree.AnnotatedTypeTree;
import com.sun.source.tree.ArrayAccessTree;
import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.CompoundAssignmentTree;
import com.sun.source.tree.ConditionalExpressionTree;
import com.sun.source.tree.EnhancedForLoopTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.NewArrayTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.ParameterizedTypeTree;
import com.sun.source.tree.ParenthesizedTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.TypeCastTree;
import com.sun.source.tree.UnaryTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import com.sun.tools.javac.tree.JCTree.JCBinary;
import com.sun.tools.javac.tree.JCTree.JCUnary;
import com.sun.tools.javac.tree.JCTree.Tag;

/**
 * It has two modes: inference mode and checking mode
 * @author huangw5
 *
 */
public abstract class InferenceVisitor extends BaseTypeVisitor<InferenceChecker> {
	
	protected final InferenceAnnotatedTypeFactory factory;
	
	/** For recording visited method invocation trees or allocation sites */
	private Set<Tree> visited = new HashSet<Tree>();

	public InferenceVisitor(InferenceChecker checker, CompilationUnitTree root) {
		super(checker, root);
		this.factory = (InferenceAnnotatedTypeFactory) atypeFactory;
	}
	
	public static enum AdaptContext {
		NONE,
		RECEIVER,
		ASSIGNTO
	}
	
	
    
	@Override
	protected void commonAssignmentCheck(Tree varTree, ExpressionTree valueExp,
			String errorKey) {
		// pass
	}

	@Override
	protected void commonAssignmentCheck(AnnotatedTypeMirror varType,
			ExpressionTree valueExp, String errorKey) {
		// pass
	}

	@Override
	protected void commonAssignmentCheck(AnnotatedTypeMirror varType,
			AnnotatedTypeMirror valueType, Tree valueTree, String errorKey) {
		// pass
	}

	@Override
	protected boolean checkMethodInvocability(AnnotatedExecutableType method,
			MethodInvocationTree node) {
		// pass
		return true;
	}

	@Override
	protected boolean checkConstructorInvocation(AnnotatedDeclaredType dt,
			AnnotatedExecutableType constructor, Tree src) {
		// pass	
		return true;
	}

	@Override
	protected void checkArguments(
			List<? extends AnnotatedTypeMirror> requiredArgs,
			List<? extends ExpressionTree> passedArgs) {
		// pass
	}

	@Override
	protected void checkAssignability(AnnotatedTypeMirror varType, Tree varTree) {
		// pass
	}

	@Override
	protected void checkTypecastRedundancy(TypeCastTree node, Void p) {
		// pass
	}

	@Override
	protected void checkTypecastSafety(TypeCastTree node, Void p) {
		// pass
	}

	@Override
	protected boolean checkOverride(MethodTree overriderTree,
			AnnotatedDeclaredType enclosingType,
			AnnotatedExecutableType overridden,
			AnnotatedDeclaredType overriddenType, Void p) {
		// pass
		return true;
	}
	

	@Override
	protected void typeCheckVectorCopyIntoArgument(MethodInvocationTree node,
			List<? extends AnnotatedTypeMirror> params) {
		// pass
	}

	@Override
	public Void visitAssignment(AssignmentTree node, Void p) {
		ExpressionTree variable = node.getVariable();
		ExpressionTree expression = node.getExpression();
		generateConstraint(variable, expression);
		return super.visitAssignment(node, p);
	}
	
	/**
     * A unary operation. ++, --, ...
     */
    @Override
    public Void visitUnary(UnaryTree node, Void p) {
		if (!visited.contains(node)) {
	        if (node instanceof JCUnary) {
	            Tag tag = ((JCUnary) node).getTag();
	            switch (tag) {
	            case PREINC:
	            case PREDEC:
	            case POSTINC:
	            case POSTDEC:
	                ExpressionTree expr = node.getExpression();
	                Reference ref = Reference.createReference(expr, factory);
	                generateConstraint(expr, ref);
	            }
	        }
		}
        return super.visitUnary(node, p);
    }

	
	@Override
    public Void visitBinary(BinaryTree node, Void p) {
		if (!visited.contains(node)) {
//	    	System.err.println("WARN: unhandled binary " + node);
			if (node instanceof JCBinary) {
	            Tag tag = ((JCBinary) node).getTag();
	            switch (tag) {
	            case BITOR_ASG: // |=
	            case BITXOR_ASG: // ^=
	            case BITAND_ASG: // &=
	            case SL_ASG: // <<=
	            case SR_ASG: // >>=
	            case USR_ASG: // >>>=
	            case PLUS_ASG: // +=
	            case MINUS_ASG: // -=
	            case MUL_ASG: // *=
	            case DIV_ASG: // /=
	            case MOD_ASG: // %=
	                ExpressionTree expr = node.getLeftOperand();
	                Reference ref = Reference.createReference(expr, factory);
	                generateConstraint(expr, ref);
	            }
	        }
		}
        return super.visitBinary(node, p);
    }

	
    /**
     * An assignment with "+=", "|=" ...
     */
	@Override
	public Void visitCompoundAssignment(CompoundAssignmentTree node, Void p) {
		if (!visited.contains(node)) {
//	    	System.err.println("WARN: unhandled compound " + node);
	        ExpressionTree var = node.getVariable();
	        ExpressionTree expr = node.getExpression();
	        generateConstraint(var, expr);
		}
		return super.visitCompoundAssignment(node, p);
	}
	
    @Override
    public Void visitEnhancedForLoop(EnhancedForLoopTree node, Void p) {
    	// In the enhanced for loop: for (X var : iterables)
    	// it is equivalent to X var; var = iterables.iteratedType;
    	// Generate constraints for var, iterated_type and iterables
    	
    	// Get the variable
        VariableTree varTree = node.getVariable();
		Reference varRef = Reference.createReference(
				TreeUtils.elementFromDeclaration(varTree), factory);
        // Get the expression
        ExpressionTree expr = node.getExpression();
//        if (expr.toString().equals("this"))
//        	System.out.println();
        AnnotatedTypeMirror exprType = factory.getAnnotatedType(expr);
		Reference exprRef = Reference.createReference(expr, factory);
    	// Recursively
    	generateConstraint(exprRef, expr);
        
        // In the case of arrays
        if (exprType.getKind() == TypeKind.ARRAY) {
        	ArrayReference arrayRef = (ArrayReference) exprRef;
        	Reference componentRef = arrayRef.getComponentRef();
//			addSubtypeConstraint(
//					getFieldAdaptReference(arrayRef, componentRef, varRef),
//					varRef);
			handleArrayRead(varRef, arrayRef, componentRef);
        } else {
        	// It is an iterable type
        	DeclaredReference dRef = (DeclaredReference) exprRef;
//        	if (dRef.getTypeArguments().isEmpty() 
//        			&& expr.toString().contentEquals("this")) {
//        		// When expr is "this", the type doesn't contain type arguments
//        		ExecutableElement currentMethodElt = getCurrentMethodElt();
//        		if (currentMethodElt != null) {
//					dRef = (DeclaredReference) ((ExecutableReference) Reference.createReference(currentMethodElt,
//							factory)).getReceiverRef();
//        		}
//        	}
        	if (!dRef.getTypeArguments().isEmpty()) {
	        	// Get the first type reference
	        	Reference iteratedRef = dRef.getTypeArguments().get(0);
				addSubtypeConstraint(
						getFieldAdaptReference(dRef, iteratedRef, varRef),
						varRef);
        	}
        }
        return super.visitEnhancedForLoop(node, p);
    }
    
    
	@Override
	public Void visitMethod(MethodTree node, Void p) {
		ExecutableElement methodElt = TreeUtils.elementFromDeclaration(node);
		// First create method reference
		Reference methodRef = Reference.createReference(methodElt, factory);
		addEmptyConstraint(methodRef);
		// Add override constraints
        // Find which method this overrides!
        Map<AnnotatedDeclaredType, ExecutableElement> overriddenMethods = annoTypes
                .overriddenMethods(methodElt);
        for (Map.Entry<AnnotatedDeclaredType, ExecutableElement> pair 
        		: overriddenMethods.entrySet()) {
        	ExecutableElement overriddenElement = pair.getValue();
        	handleMethodOverride(methodElt, overriddenElement);
        }
        
        // FIXME: Enforce this of constructor: We assume all constructors invoke the
        // "default" constructor
//        if (TreeUtils.isConstructor(node)) {
//        	Reference defaultConstructorThisRef = getDefaultConstructorThisRef();
//        	Reference thisRef = ((ExecutableReference) methodRef).getReceiverRef();
//        	addSubtypeConstraint(thisRef, defaultConstructorThisRef);
//        }
		
		return super.visitMethod(node, p);
	}
    
    @Override
	public Void visitMethodInvocation(MethodInvocationTree node, Void p) {
    	// If this statement is enclosed in another expression, then it should
    	// have been visited and we skip it. 
    	// E.g. X x = y.m(z); this method invocation is visited in the assignment
    	// E.g. y.m(x.n(z));  the x.n(z) is visited as an argument   
    	ExecutableElement methodElt = TreeUtils.elementFromUse(node);
    	
    	if (node.toString().equals("super()") 
    			&& methodElt.toString().equals("Object()")) {
    		// skip
    		return super.visitMethodInvocation(node, p);
    	}
    	
//    	if (node.toString().contains("java.security.AccessController.checkPermission(perm)"))
//    		System.out.println();
    	
//		System.out.println("Visiting " + node.toString());
    	
		if (!visited.contains(node)) {
    		// The enclosing is not an expression
    		// Generate constraints for receiver and parameters
    		Reference rcvRef = null;
			if (!ElementUtils.isStatic(methodElt)) {
				ExpressionTree rcvTree = InferenceUtils.getReceiverTree(node);
				if (rcvTree == null) {
					// This may be a a self invocation like x = m(z); 
					ExecutableElement currentMethodElt = getCurrentMethodElt();
					if(currentMethodElt != null) {
						Reference currentMethodRef = Reference.createReference(
								currentMethodElt, factory);
						rcvRef = ((ExecutableReference) currentMethodRef).getReceiverRef();
					}
				} else {
					rcvRef = Reference.createReference(rcvTree, factory);
					// generate constraints on the receiver recursively
					generateConstraint(rcvRef, rcvTree);
				}
			} else 
				rcvRef = null;
			Reference assignTo = null;
			if (methodElt.getReturnType().getKind() != TypeKind.VOID) {
				assignTo = Reference.createReference(node, factory);
			}
			handleMethodCall(methodElt, node.getArguments(), rcvRef, assignTo, node); // WEI: July 7
    	}
		return super.visitMethodInvocation(node, p);
	}
    
    
	@Override
	public Void visitNewArray(NewArrayTree node, Void p) {
    	// If this statement is enclosed in another expression, then it should
    	// have been visited and we skip it. 
    	// E.g. X[] x = new X[3]; this method invocation is visited in the assignment
		if (!visited.contains(node)) {
    		System.out.println("WARN: Unhandled statement " + node);
    	}
		return super.visitNewArray(node, p);
	}
	

	@Override
	public Void visitNewClass(NewClassTree node, Void p) {
    	// If this statement is enclosed in another expression, then it should
    	// have been visited and we skip it. 
    	// E.g. X x = new X(); this method invocation is visited in the assignment
		if (!visited.contains(node)) {
			List<? extends ExpressionTree> arguments = node.getArguments();
			Reference rcvRef = Reference.createReference(node, factory);
			// TODO: lhsRef passed to  generateMethodCallConstraints() is null
			handleMethodCall(TreeUtils.elementFromUse(node), arguments, rcvRef, null, node);
    	}
		return super.visitNewClass(node, p);
	}


	@Override
	public Void visitReturn(ReturnTree node, Void p) {
		ExpressionTree expr = node.getExpression();
		if (expr != null) {
			Reference exprRef = Reference.createReference(expr, factory);
			// Recursively
			generateConstraint(exprRef, expr);
			
			MethodTree methodTree = TreeUtils.enclosingMethod(getCurrentPath());
			ExecutableElement methodElt = (ExecutableElement) 
					TreeUtils.elementFromDeclaration(methodTree);
			// Get the reference of return 
			Reference methodRef = Reference.createReference(methodElt, factory);
			Reference returnRef = ((ExecutableReference) methodRef).getReturnRef();
			addSubtypeConstraint(exprRef, returnRef);
		}
		return super.visitReturn(node, p);
	}
	

	@Override
	public Void visitVariable(VariableTree node, Void p) {
		ExpressionTree initializer = node.getInitializer();
		VariableElement varElt = TreeUtils.elementFromDeclaration(node);
		Reference varRef = Reference.createReference(varElt, factory);
		if (initializer != null) {
			// Add subtype constraint
			Reference initilizerRef = Reference.createReference(initializer, 
					factory);
			generateConstraint(initilizerRef, initializer);
			
			// FIXME: The field should be accessed by adapting it from PoV
			// the default constructor. The implementation below can be 
			// merged with the field access in static initializer.
			if (varElt.getKind().isField()) {
				Reference defaultConstructorThisRef = getDefaultConstructorThisRefWithField(varElt);
				handleFieldWrite(defaultConstructorThisRef, varRef, initilizerRef);
			} else {
				addSubtypeConstraint(initilizerRef, varRef);
			}
		} else 
			addEmptyConstraint(varRef);
		return super.visitVariable(node, p);
	}
	
	
	@Override
	public Void visitClass(ClassTree node, Void p) {
		TypeElement classElt = TreeUtils.elementFromDeclaration(node);
		InferenceMain.getInstance().getConstraintManager().addVisitedClass(classElt);
		return super.visitClass(node, p);
	}

	/**
	 * Get current method element; 
	 * @return <code>null</code> if it is not in a method; otherwise return the
	 * current method element. 
	 */
    public ExecutableElement getCurrentMethodElt() {
        MethodTree enclosingMethod = TreeUtils.enclosingMethod(
        		this.getCurrentPath());
        if (enclosingMethod == null)
            return null;
        else
            return TreeUtils.elementFromDeclaration(enclosingMethod);
    }
    
    public Tree getCurrentStatement() {
    	TreePath currentPath = getCurrentPath();
    	Tree leaf = currentPath.getLeaf();
    	while (currentPath != null && !(leaf instanceof StatementTree)) {
    		currentPath = currentPath.getParentPath();
    		leaf = currentPath.getLeaf();
    	}
    	if (currentPath == null)
    		return null;
    	else
    		return leaf;
    }
    
    /**
     * We use the first constructor as the default
     * @return
     */
//    @Deprecated
    public Reference getDefaultConstructorThisRef() {
    	ClassTree classTree = TreeUtils.enclosingClass(getCurrentPath());
        TypeElement elem = TreeUtils.elementFromDeclaration(classTree);
        return Reference.createReference(elem, factory);
    }

    /**
     * with field
     */
    public Reference getDefaultConstructorThisRefWithField(Element fieldElt) {
        if (!fieldElt.getKind().isField())
            return null;
        TreePath p = getCurrentPath();
        while (p != null) {
            Tree leaf = p.getLeaf();
            assert leaf != null; /*nninvariant*/
            if (TreeUtils.isClassTree(leaf.getKind())) {
                AnnotatedDeclaredType classType = (AnnotatedDeclaredType) factory
                        .getAnnotatedType((ClassTree) leaf);
                TypeElement elem = TreeUtils.elementFromDeclaration((ClassTree) leaf);
                if (checker.isFieldElt(classType, fieldElt)) {
                    return Reference.createReference(elem, factory);
                }
            }
            p = p.getParentPath();
        }
        return null;
    }

    /**
     * Get the method element which is a sibling of fieldElt
     */
    public ExecutableElement getEnclosingMethodWithField(Element fieldElt) {
        if (!fieldElt.getKind().isField())
            return null;
        TreePath p = getCurrentPath();
        while (p != null) {
            Tree leaf = p.getLeaf();
            assert leaf != null; /*nninvariant*/
            if (leaf.getKind() == Tree.Kind.METHOD) {
                p = p.getParentPath();
                Tree t = p.getLeaf();
                if (t.getKind() == Tree.Kind.CLASS) {
                    AnnotatedDeclaredType classType = (AnnotatedDeclaredType) factory
                            .getAnnotatedType((ClassTree) t);
                    if (checker.isFieldElt(classType, fieldElt))
                        return TreeUtils.elementFromDeclaration((MethodTree) leaf);
                }
            }
            p = p.getParentPath();
        }
        return null;
    }
    
	/**
	 * lhsTree = rhsTree;
	 * ===>
	 * lhsTree = lhsRef; lhsRef = rhsRef; rhsRef = rhsTree;
	 * @param lhsTree
	 * @param rhsTree
	 */
	protected void generateConstraint(ExpressionTree lhsTree, ExpressionTree rhsTree) {
		Reference lhsRef = Reference.createReference(lhsTree, factory);
		Reference rhsRef = Reference.createReference(rhsTree, factory);
		addSubtypeConstraint(rhsRef, lhsRef);
		generateConstraint(lhsTree, lhsRef);
		generateConstraint(rhsRef, rhsTree);
	}
	
	/**
	 * Generate constraints for lhsRef = rhsTree;
	 * @param lhsRef
	 * @param rhsTree
	 */
	protected void generateConstraint(Reference lhsRef, ExpressionTree rhsTree) {
		switch (rhsTree.getKind()) {
		case NEW_CLASS:
			NewClassTree ncTree = (NewClassTree) rhsTree;
			ExecutableElement methodElt = TreeUtils.elementFromUse(ncTree);
			Reference rcvRef = Reference.createReference(ncTree, factory);
			// Recursively
			// TODO: lhsRef passed to  generateMethodCallConstraints() is null
			handleMethodCall(methodElt, ncTree.getArguments(), rcvRef, lhsRef, rhsTree);
			break;
		case METHOD_INVOCATION:
			MethodInvocationTree miTree = (MethodInvocationTree) rhsTree;
			ExecutableElement iMethodElt = TreeUtils.elementFromUse(miTree);
			ExpressionTree rcvTree = InferenceUtils.getReceiverTree(miTree);
			Reference iRcvRef = null;
			if (!ElementUtils.isStatic(iMethodElt)) {
				ExecutableElement currentMethodElt = getCurrentMethodElt();
				if (rcvTree == null) {
					// This may be a a self invocation like x = m(z); 
					if (currentMethodElt == null)
						iRcvRef = null;
					else {
						Reference currentMethodRef = Reference.createReference(
								currentMethodElt, factory);
						iRcvRef = ((ExecutableReference) currentMethodRef)
								.getReceiverRef();
					}
				} else {
					iRcvRef = Reference.createReference(rcvTree, factory);
					// generate constraints on the receiver recursively
					generateConstraint(iRcvRef, rcvTree);
				}
			} else 
				iRcvRef = null;
			handleMethodCall(iMethodElt, miTree.getArguments(), iRcvRef, lhsRef, rhsTree);
			break;
		case MEMBER_SELECT:
			MemberSelectTree mTree = (MemberSelectTree) rhsTree;
            ExpressionTree expr = mTree.getExpression();
            Element fieldElt = TreeUtils.elementFromUse(mTree);
            AnnotatedTypeMirror exprType = factory.getAnnotatedType(expr);
            if (checker.isAccessOuterThis(mTree)) {
            	// If it is like Body.this
            	Element outerElt = checker.getOuterThisElement(mTree, getCurrentMethodElt());
            	if (outerElt != null 
            			&& outerElt.getKind() ==  ElementKind.METHOD) {
            		ExecutableElement outerExecutableElt = (ExecutableElement) outerElt;
					Reference outerMethodRef = Reference.createReference(
							outerExecutableElt, factory);
            		Reference outerThisRef = ((ExecutableReference) outerMethodRef).getReceiverRef();
            		addSubtypeConstraint(outerThisRef, lhsRef);
            	} else {
            		// FIXME: we have to enforce currentMethod <: lhsRef
					ExecutableElement currentMethodElt = getCurrentMethodElt();
					if (currentMethodElt != null) {
						Reference currentMethodRef = Reference.createReference(
								currentMethodElt, factory);
						Reference thisRef = ((ExecutableReference) currentMethodRef).getReceiverRef();
	            		addSubtypeConstraint(thisRef, lhsRef);
					}
            	}
			} else if (!fieldElt.getSimpleName().contentEquals("super")
					&& checker.isFieldElt(exprType, fieldElt)) {
            	Reference fieldRef = Reference.createReference(fieldElt, factory);
                // TODO There may be type casts on "this", 
            	// e.g. ((@mutable X) this).field
				Reference exprRef = Reference.createReference(expr, factory);
            	// Recursively generate constraints
            	generateConstraint(exprRef, expr);
            	handleFieldRead(lhsRef, exprRef, fieldRef);
            } 
			break;
		case IDENTIFIER:
			ExecutableElement currentMethodElt = getCurrentMethodElt();
			IdentifierTree idTree = (IdentifierTree) rhsTree;
			Element idElt = TreeUtils.elementFromUse(idTree);
			// TODO: idElt should be the same as idTree. They should be equivalent. 
			// If idElt is "this", then we create the thisRef
			Reference idRef = null;
			if (idElt.getSimpleName().contentEquals("this")
					&& currentMethodElt != null) {
				Reference currentMethodRef = Reference.createReference(
						currentMethodElt, factory);
				idRef = ((ExecutableReference) currentMethodRef).getReceiverRef();
			} else
				idRef = Reference.createReference(idElt, factory);
			// Check if idElt is a field or not
			if (idRef == null) {
				// do nothing. This happens when currentMethodElt is null;
			} else if (!idElt.getSimpleName().contentEquals("this")
					&& idElt.getKind() == ElementKind.FIELD  // FIXME: WEI: this is line added back on Nov 27
//					&& isCurrentFieldElt(idElt)  //FIXME: WEI: this line is commented out on Nov 27
					/*&& currentMethodElt != null*/) {
				Reference thisRef = null;
                if (!isCurrentFieldElt(idElt)) {
                    // If accessing fields of outer class
                    ExecutableElement enclosingMethodElt = getEnclosingMethodWithField(idElt);
                    if (enclosingMethodElt != null) {
                        // If there is an enclosing method
                        Reference currentMethodRef = Reference.createReference(
                                enclosingMethodElt, factory);
                        thisRef = ((ExecutableReference) currentMethodRef).getReceiverRef();
                    } else {
                        // Otherwise, we use the class as THIS
                        thisRef = getDefaultConstructorThisRefWithField(idElt);
                    }
                } else if (currentMethodElt != null) {
					Reference currentMethodRef = Reference.createReference(
							currentMethodElt, factory);
					thisRef = ((ExecutableReference) currentMethodRef).getReceiverRef();
				} else {
					// This happens in static initializer. But reading should not
					// be possible
					thisRef = getDefaultConstructorThisRefWithField(idElt); // WEI: Add on Dec 5, 2012
				}
				handleFieldRead(lhsRef, thisRef, idRef);
			} else if (lhsRef.getTree() != null && lhsRef.getTree().equals(rhsTree)){
				// They are equivalent
				addEqualityConstraint(idRef, lhsRef);
			} else {
				addSubtypeConstraint(idRef, lhsRef);
			}
			break;
		case NEW_ARRAY:
			NewArrayTree nArrayTree = (NewArrayTree) rhsTree;
//			if (nArrayTree.toString().contains("user") && nArrayTree.toString().contains("pass"))
//				System.out.println();
			// Create the reference of the new array
			ArrayReference nArrayRef = (ArrayReference) Reference
					.createReference(nArrayTree, factory);
			// Generate constraints
			addSubtypeConstraint(nArrayRef, lhsRef);
			
			List<? extends ExpressionTree> aInitializers = nArrayTree.getInitializers();
			
			// Generate constraints for the initializers and the array 
			// if the initializers are not empty
			if (aInitializers != null && !aInitializers.isEmpty()) {
				// Get its component reference
				Reference componentRef = nArrayRef.getComponentRef();
				// TODO: What is the relation between the initializer
				// and the component? In the previous implementation, 
				// there is no adapt... 
				// I think it should do the adapt first, because it is
				// equivalent to:
				// X[] a = new X[2]; a[0] = x1; a[1] = x2;
				for (ExpressionTree initializer : aInitializers) {
					Reference initializerRef = Reference.createReference(
							initializer, factory);
					// Recursively 
					generateConstraint(initializerRef, initializer);
					// Now add the adapt constraint
					addSubtypeConstraint(initializerRef, 
							getFieldAdaptReference(nArrayRef, componentRef, null));
				}
			}
			break;
		case ARRAY_ACCESS:
			ArrayAccessTree aaTree = (ArrayAccessTree) rhsTree;
			ExpressionTree aaExpr = aaTree.getExpression();
			Reference exprRef = Reference.createReference(aaExpr, factory);
			
			// Recursively
			generateConstraint(exprRef, aaExpr);
			
			// Get the component reference of this array access
			Reference componentRef = ((ArrayReference) exprRef).getComponentRef();
			
			// Now add the adapt constraint
			handleArrayRead(lhsRef, exprRef, componentRef);
			
			break;
		case TYPE_CAST:
			// Get the tree being casted 
//			if (rhsTree.toString().contains("String)this"))
//				System.out.println();
			ExpressionTree castedTree = ((TypeCastTree) rhsTree).getExpression();
			Reference castedRef = Reference
					.createReference(castedTree, factory);
			// Recursively 
			generateConstraint(castedRef, castedTree);
			
			AnnotatedTypeMirror rhsType = factory.getAnnotatedType(rhsTree);
			if (!checker.isAnnotated(rhsType)) {
				// In the case no annotations appear in the cast 
				// connect the casted expr and the lhs 
				// TODO rhsType may have been annotated with all possible qualifiers
				addSubtypeConstraint(castedRef, lhsRef);
			}
			break;
		case PARENTHESIZED:
//			if (rhsTree.toString().contains("null != mParser"))
//				System.out.println();
            ParenthesizedTree pTree = (ParenthesizedTree) rhsTree;
            ExpressionTree pExpr = pTree.getExpression();
			Reference pRef = Reference.createReference(pExpr, factory);
            // Recursively 
            generateConstraint(pRef, pExpr);
            
            addSubtypeConstraint(pRef, lhsRef);
			break;
		case ASSIGNMENT:
            AssignmentTree aTree = (AssignmentTree) rhsTree; 
            ExpressionTree aExpr = aTree.getVariable();
			Reference aRef = Reference.createReference(aExpr, factory);
            // Recursively
            generateConstraint(aRef, aExpr);
            
            addSubtypeConstraint(aRef, lhsRef);
			break;
		case CONDITIONAL_EXPRESSION:
            ConditionalExpressionTree cTree = (ConditionalExpressionTree) rhsTree;
            ExpressionTree cTrueExpr = cTree.getTrueExpression();
			Reference cTrueRef = Reference.createReference(cTrueExpr, factory);
            generateConstraint(cTrueRef, cTrueExpr);
            addSubtypeConstraint(cTrueRef, lhsRef);
            
            ExpressionTree cFalseExpr = cTree.getFalseExpression();
			Reference cFalseRef = Reference
					.createReference(cFalseExpr, factory);
            generateConstraint(cFalseRef, cFalseExpr);
            addSubtypeConstraint(cFalseRef, lhsRef);
			break;
		case INT_LITERAL:
		case LONG_LITERAL:
		case FLOAT_LITERAL:
		case DOUBLE_LITERAL:
		case BOOLEAN_LITERAL:
		case CHAR_LITERAL:
		case STRING_LITERAL:
		case NULL_LITERAL:
			break;
		default:
			// Check other cases
			if (rhsTree instanceof BinaryTree) {
				BinaryTree bTree = (BinaryTree) rhsTree;
//				visitBinary(bTree, null);
				// WEI: Add constraints for BinaryTree on Dec 2, 2012
				ExpressionTree left = bTree.getLeftOperand();
				ExpressionTree right = bTree.getRightOperand();
				Reference leftRef = Reference.createReference(left, factory);
				Reference rightRef = Reference.createReference(right, factory);
				addSubtypeConstraint(leftRef, lhsRef);
				addSubtypeConstraint(rightRef, lhsRef);
				generateConstraint(leftRef, left);
				generateConstraint(rightRef, right);
				
			} else if (rhsTree instanceof UnaryTree) {
				UnaryTree uTree = (UnaryTree) rhsTree;
				ExpressionTree exprTree = uTree.getExpression();
                Reference ref = Reference.createReference(exprTree, factory);
                addSubtypeConstraint(ref, lhsRef);
                generateConstraint(ref, exprTree);
//				visitUnary(uTree, null);
			} 
//			else
//				System.out.println("WARN: Unhandled statment: " + rhsTree
//						+ " type: " + rhsTree.getKind());
		}
		visited.add(rhsTree);
	}
	
	/**
	 * An assignment of "tree = ref". Generate constraints based on the 
	 * structure of the <code>lhsTree</code>
	 * @param lhsTree
	 * @param rhsRef
	 */
	protected void generateConstraint(ExpressionTree lhsTree, Reference rhsRef) {
		switch (lhsTree.getKind()) {
		case VARIABLE:
			// generate Reference of element
			Reference varRef = Reference.createReference(lhsTree, factory);
			addSubtypeConstraint(rhsRef, varRef);
			break;
		case IDENTIFIER:
			// lhsTree is an identifier: get its use element.
			Element idElt = TreeUtils.elementFromUse((IdentifierTree) lhsTree);
//			AnnotatedTypeMirror idType = factory.getAnnotatedType(lhsTree);
			ExecutableElement currentMethodElt = getCurrentMethodElt();
			// generate Reference of element
			Reference idRef = null;
			if (idElt.getSimpleName().contentEquals("this")) {
				Reference currentMethodRef = Reference.createReference(
						currentMethodElt, factory);
				idRef = ((ExecutableReference) currentMethodRef).getReceiverRef();
			} else
				idRef = Reference.createReference(idElt, factory);
			
			if (idRef == null) {
				// Do nothing
			} else if (!idElt.getSimpleName().contentEquals("this")
					&& idElt.getKind() == ElementKind.FIELD  // FIXME: WEI: this is line added back on Nov 27
//					&& isCurrentFieldElt(idElt)  //FIXME: WEI: this line is commented out on Nov 27
					/*&& currentMethodElt != null*/) {
				// Now we need to check if idElt is a field. If so, then we need to 
				// generate adapt constraint
				Reference thisRef = null;
                if (!isCurrentFieldElt(idElt)) {
                    // If accessing fields of outer class
                    ExecutableElement enclosingMethodElt = getEnclosingMethodWithField(idElt);
                    if (enclosingMethodElt != null) {
                        // If there is an enclosing method
                        Reference currentMethodRef = Reference.createReference(
                                enclosingMethodElt, factory);
                        thisRef = ((ExecutableReference) currentMethodRef).getReceiverRef();
                    } else {
                        // Otherwise, we use the class as THIS
                        thisRef = getDefaultConstructorThisRefWithField(idElt);
                    }
                } else if (currentMethodElt != null) {
					Reference currentMethodRef = Reference.createReference(
							currentMethodElt, factory);
					thisRef = ((ExecutableReference) currentMethodRef).getReceiverRef();
				} else {
					// This happens when initializing a field in static initializer
					thisRef = getDefaultConstructorThisRefWithField(idElt);
				}
				handleFieldWrite(thisRef, idRef, rhsRef);
			} else if (rhsRef.getTree() != null && rhsRef.getTree().equals(lhsTree)){
				// They are equivalent
				addEqualityConstraint(rhsRef, idRef);
			} else 
				addSubtypeConstraint(rhsRef, idRef);
			break;
		case MEMBER_SELECT:
			// Okay, it is an member selection. Need to construct the adapt
			// constraint. 
			MemberSelectTree mTree = (MemberSelectTree) lhsTree;
			// Get the expr
			ExpressionTree rcvExpr = mTree.getExpression();
			// Get the field
			Element fieldElt = TreeUtils.elementFromUse(mTree);
            AnnotatedTypeMirror rcvType = factory.getAnnotatedType(rcvExpr);
			if (!fieldElt.getSimpleName().contentEquals("super")
					&& checker.isFieldElt(rcvType, fieldElt)) {
				Reference fieldRef = Reference.createReference(fieldElt, factory);
				Reference rcvRef = Reference.createReference(rcvExpr, factory);
				// Recursively, this is equal to:
				// exprRef = rcvExpr; exprRef.fieldRef = rhsRef;
				generateConstraint(rcvRef, rcvExpr);
				handleFieldWrite(rcvRef, fieldRef, rhsRef);
			} 
			break;
		case ARRAY_ACCESS:
			// It is an array access expression. Also need the viewpoint adaptation.
			ArrayAccessTree aTree = (ArrayAccessTree) lhsTree;
			ExpressionTree expr = aTree.getExpression();
			Reference exprRef = Reference.createReference(expr, factory);
			// Recursively 
			generateConstraint(exprRef, expr);
			
			// Get the component reference of this array access
			Reference componentRef = ((ArrayReference) exprRef).getComponentRef();
			handleArrayWrite(exprRef, componentRef, rhsRef);
			break;
		default:
//			System.out.println("WARN: Unhandled statements: " + lhsTree
//					+ " type: " + lhsTree.getKind());
		}
		visited.add(lhsTree);
	}
	
//	/**
//	 * Handle assignment l = r;
//	 * @param lhsRef
//	 * @param rhsRef
//	 */
//	protected void handleAssignment(Reference lhsRef, Reference rhsRef) {
//		// Skip, but subclasses may want to override it
//	}
	
	protected void addSubtypeConstraint(Reference sub, Reference sup) {
		InferenceMain.getInstance().getConstraintManager()
				.addSubtypeConstraint(sub, sup);
	}
	
	protected void addEqualityConstraint(Reference left, Reference right) {
		InferenceMain.getInstance().getConstraintManager()
				.addEqualityConstraint(left, right);
	}
	
	protected void addInequalityConstraint(Reference left, Reference right) {
		InferenceMain.getInstance().getConstraintManager()
				.addInequalityConstraint(left, right);
	}
	
	protected void addEmptyConstraint(Reference ref) {
		InferenceMain.getInstance().getConstraintManager()
				.addEmptyConstraint(ref);
	}
	
	/**
	 * Handle array read: a = b[i];
	 * @param lhsRef
	 * @param exprRef
	 * @param componentRef
	 */
	protected void handleArrayRead(Reference lhsRef, Reference exprRef, Reference componentRef) {
		addSubtypeConstraint(
				getFieldAdaptReference(exprRef, componentRef, lhsRef), lhsRef);
	}
	
	/**
	 * Handle field read: x = y.f;
	 * @param lhsRef
	 * @param rcvRef
	 * @param fieldRef
	 */
	protected void handleFieldRead(Reference lhsRef, Reference rcvRef, Reference fieldRef) {
		Element fieldElt = fieldRef.getElement();
    	if (!ElementUtils.isStatic(fieldElt)) {
	    	Reference msAdaptRef = getFieldAdaptReference(rcvRef, 
	    			fieldRef, lhsRef);
			addSubtypeConstraint(msAdaptRef, lhsRef);
	    } else
			addEqualityConstraint(fieldRef, lhsRef);
//			addSubtypeConstraint(fieldRef, lhsRef);
	}
	
	/**
	 * Handle array write: a[i] = b;
	 * @param exprRef
	 * @param componentRef
	 * @param rhsRef
	 */
	protected void handleArrayWrite(Reference exprRef, Reference componentRef, 
			Reference rhsRef) {
		// Now add the adapt constraint
		addSubtypeConstraint(rhsRef,
				getFieldAdaptReference(exprRef, componentRef, null));
	}
	
	/**
	 * Handle field write: x.f = y;
	 * @param rcvRef
	 * @param fieldRef
	 * @param rhsRef
	 */
	protected void handleFieldWrite(Reference rcvRef, Reference fieldRef, Reference rhsRef) {
		Element fieldElt = fieldRef.getElement();
		if (!ElementUtils.isStatic(fieldElt)) {
			// Get the adapt context's reference
			Reference adaptRef = getFieldAdaptReference(rcvRef, fieldRef, null);
			addSubtypeConstraint(rhsRef, adaptRef);
		} else 
			addEqualityConstraint(rhsRef, fieldRef);
//			addSubtypeConstraint(rhsRef, fieldRef);
	}
	
	/**
	 * Handle method invocation
	 * @param methodElt The method element 
	 * @param arguments The actual arguments 
	 * @param rcvRef The reference of the receiver. It can be null if the method 
	 * is static
	 * @param lhsRef The reference of the return value is assigned to. It can be 
	 * null. We can assume it is assigned to {ALL POSSIBLE QUALIFIERS}
	 */
	protected void handleMethodCall(ExecutableElement methodElt, 
			List<? extends ExpressionTree> arguments, Reference rcvRef, 
            Reference lhsRef, Tree tree) {
		if (lhsRef == null) {
			// WEI: Added on Dec 9, 2012
			lhsRef = Reference.createConstantReference(checker.getSourceLevelQualifiers(), "DummyLHS");
		}
		Reference methodRef = Reference.createReference(methodElt, factory);
		if (!ElementUtils.isStatic(methodElt) && rcvRef != null) {
			// Generate constraints for the receiver, 
			// e.g. for x = y.m(z), we have t_y <: _ |> t_this_m
			// we skip this for static methods
			// Get the receiver reference of the call method
			Reference thisRef = ((ExecutableReference) methodRef).getReceiverRef();
			// Construct the adapt reference
			Reference adaptRef = getMethodAdaptReference(rcvRef, 
					thisRef, lhsRef, tree);
			addSubtypeConstraint(rcvRef, adaptRef);
		}
			
		// Generate constraints for parameters: t_z <: _ |> t_p
		List<? extends VariableElement> parameters = methodElt.getParameters();
		int size = parameters.size() > arguments.size() ? 
				arguments.size() : parameters.size();
		for (int i = 0; i < size; i++) {
			VariableElement paramElt = parameters.get(i);
			Reference paramRef = Reference.createReference(paramElt, factory);
			ExpressionTree argTree = arguments.get(i);
			Reference argRef = Reference.createReference(argTree, factory);
			// Recursively generate constraints 
			generateConstraint(argRef, argTree);
			
			// rcvRef is null if the method is static 
			Reference adaptRef = getMethodAdaptReference(rcvRef, 
					paramRef, lhsRef, tree);
			addSubtypeConstraint(argRef, adaptRef);
		}
		if (lhsRef != null && !(lhsRef instanceof ConstantReference)) {
			if (methodElt.getReturnType().getKind() != TypeKind.VOID) { 
				// Generate constraints for returns: |> t_return <: t_lhs
				Reference returnRef = ((ExecutableReference) methodRef).getReturnRef();
				Reference adaptRef = getMethodAdaptReference(rcvRef, 
						returnRef, lhsRef, tree);
				addSubtypeConstraint(adaptRef, lhsRef);
			} else if (isConstructor(methodElt)){
				// It is a constructor, we connect rcvRef and lhsRef
				addSubtypeConstraint(rcvRef, lhsRef);
			}
		}
	}

	protected void handleMethodCall(ExecutableElement methodElt, 
			List<? extends ExpressionTree> arguments, Reference rcvRef, 
            Reference lhsRef) {
        handleMethodCall(methodElt, arguments, rcvRef, lhsRef, null);
    }
	
	/**
	 * Generate constraints for method overriding
	 * @param overrider
	 * @param overridden
	 */
	protected void handleMethodOverride(ExecutableElement overrider, 
			ExecutableElement overridden) {
		
		ExecutableReference overriderRef = (ExecutableReference) Reference
				.createReference(overrider, factory);
		ExecutableReference overriddenRef = (ExecutableReference) Reference
				.createReference(overridden, factory);
		
		// Method Receiver: overridden <: overrider  
		Reference overriderRcvRef = overriderRef.getReceiverRef();
		Reference overriddenRcvRef = overriddenRef.getReceiverRef();
		addSubtypeConstraint(overriddenRcvRef, overriderRcvRef);
		
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
			addSubtypeConstraint(overriddenParamRef, overriderParamRef);
    	}
    	
    	if (overrider.getReturnType().getKind() == TypeKind.VOID)
    		return;
    	// Returns: overrider <: overridden
    	Reference overriderReturnRef = overriderRef.getReturnRef();
    	Reference overriddenReturnRef = overriddenRef.getReturnRef();
		addSubtypeConstraint(overriderReturnRef, overriddenReturnRef);
	}
	
	protected boolean isConstructor(ExecutableElement methodElt) {
		Tree tree = factory.getDeclaration(methodElt);
		if (tree != null && tree instanceof MethodTree)
			return TreeUtils.isConstructor((MethodTree) tree);
		return false;
	}
	
	
	protected boolean isThisReference(Reference ref) {
		Element elt = ref.getElement();
		Tree tree = ref.getTree();
		
		// There may be type cast on "this", e.g.
		// ((/*@Mutable*/ tinySQLTableView) this).tsColumnCache
		if (tree != null) {
			if (TreeUtils.isExpressionTree(tree))
				tree = TreeUtils.skipParens((ExpressionTree) tree);
			while (tree.getKind() == Kind.TYPE_CAST) {
				tree = ((TypeCastTree) tree).getExpression();
				if (TreeUtils.isExpressionTree(tree))
					tree = TreeUtils.skipParens((ExpressionTree) tree);
			}
		}
				
		if (elt != null
				&& !(ref instanceof ExecutableReference) 
				&& ref.getRefName().startsWith("THIS_") 
				|| tree.toString().equals("this"))
			return true;
		else
			return false;
	}
	
	 protected boolean isCurrentFieldElt(Element fieldElt) {
		if (fieldElt.getKind() != ElementKind.FIELD)
			return false;
		ClassTree enclosingOfClass = TreeUtils.enclosingOfClass(
				getCurrentPath(), ClassTree.class);
		AnnotatedDeclaredType classType = (AnnotatedDeclaredType) factory
				.getAnnotatedType(enclosingOfClass);
//		TypeElement typeElt = (TypeElement)classType.getUnderlyingType().asElement();
		return checker.isFieldElt(classType, fieldElt);
	}
	
	
	protected Reference getFieldAdaptReference(Reference rcvRef, 
			Reference fieldRef, Reference assignToRef) {
//		if (rcvRef == null) {
//			System.out.println(this.getCurrentMethodElt());
//			System.out.println(this.getCurrentPath().getLeaf());
//			throw new RuntimeException("Null context!");
//		}
		switch (getFieldAdaptContext(rcvRef, fieldRef, assignToRef)) {
		case NONE:
			return fieldRef;
		case RECEIVER:
			return Reference.createFieldAdaptReference(rcvRef, fieldRef);
		case ASSIGNTO:
			// If assignTo is primitive, then return directly
			if (assignToRef instanceof PrimitiveReference)
				return fieldRef;
			else
				return Reference.createFieldAdaptReference(assignToRef, fieldRef);
		}
		System.out.println("ERROR: No adapt context is found!");
		return null;
	}

	protected Reference getFieldAdaptReference(Reference rcvRef, 
			Reference fieldRef, Reference assignToRef, Tree tree) {
        Reference ref = getFieldAdaptReference(rcvRef, fieldRef, assignToRef);
        if (ref != null && (ref instanceof FieldAdaptReference))
            ((FieldAdaptReference) ref).setTree(tree);
        return ref;
    }
	
	protected Reference getMethodAdaptReference(Reference rcvRef, 
			Reference declRef, Reference assignToRef) {
		switch (getMethodAdaptContext(rcvRef, declRef, assignToRef)) {
		case NONE:
			return declRef;
		case RECEIVER:
			return Reference.createMethodAdaptReference(rcvRef, declRef);
		case ASSIGNTO:
			// If assignTo is primitive, then return directly
			// FIXME: But it is not true for SFlow. We now ignore
			// it by overriding 
			if (assignToRef instanceof PrimitiveReference)
				return declRef;
			else
				return Reference.createMethodAdaptReference(assignToRef, declRef);
		}
		System.out.println("ERROR: No adapt context is found!");
		return null;
	}

	protected Reference getMethodAdaptReference(Reference rcvRef, 
			Reference declRef, Reference assignToRef, Tree tree) {
        Reference ref = getMethodAdaptReference(rcvRef, declRef, assignToRef);
        if (ref != null && (ref instanceof MethodAdaptReference))
            ((MethodAdaptReference) ref).setTree(tree);
        return ref;
    }
	
	/**
	 * Get the adapt context for field access
	 * @param rcvRef The reference of the receiver. It shouldn't be {@code null}
	 * @param fieldRef The reference of the field
	 * @param assignToRef The reference of this field access is assigned to. 
	 * If it is {@code null}, then it is a field write, e.g. x.f = y; 
	 * @return
	 */
	public abstract AdaptContext getFieldAdaptContext(Reference rcvRef, 
			Reference fieldRef, Reference assignToRef); 
	
	/**
	 * Get the adapt context for method call. 
	 * @param rcvRef The reference of the receiver. If it is {@code null}, then
	 * it is a static method. 
	 * @param declRef The reference of the parameter or the return
	 * @param assignToRef The reference of the return value is assigned to. 
	 * If the return value is void, it is {@code null}. 
	 * @return
	 */
	public abstract AdaptContext getMethodAdaptContext(Reference rcvRef, 
			Reference declRef, Reference assignToRef); 
	
	
	@Override
	protected InferenceTypeValidator createTypeValidator() {
        return new InferenceTypeValidator();
    }

	/**
	 * The following class is copied from {@link BaseTypeVisitor} with one line
	 * commented
	 * @author huangw5
	 *
	 */
    protected class InferenceTypeValidator extends BaseTypeVisitor<InferenceChecker>.TypeValidator {
        protected void reportError(AnnotatedTypeMirror type, Tree p) {
            checker.report(Result.failure("type.invalid",
                        type.getAnnotations(), type.toString()), p);
        }

        @Override
        public Void visitDeclared(AnnotatedDeclaredType type, Tree tree) {
            if (checker.shouldSkipUses(type.getElement()))
                return super.visitDeclared(type, tree);

            // Ensure that type use is a subtype of the element type
            AnnotatedDeclaredType useType = type.getErased();
            AnnotatedDeclaredType elemType = (AnnotatedDeclaredType)
                factory.getAnnotatedType(
                        useType.getUnderlyingType().asElement()).getErased();

            if (!isValidUse(elemType, useType)) {
                reportError(useType, tree);
            }

            /* Try to reconstruct the ParameterizedTypeTree from the given tree.
             * TODO: there has to be a nicer way to do this...
             */
            Pair<ParameterizedTypeTree, AnnotatedDeclaredType> p = extractParameterizedTypeTree(tree, type);
            ParameterizedTypeTree typeargtree = p.first;
            type = p.second;

            if (typeargtree!=null) {
                // We have a ParameterizedTypeTree -> visit it.

                visitParameterizedType(type, typeargtree);

                /* Instead of calling super with the unchanged "tree", adapt the second
                 * argument to be the corresponding type argument tree.
                 * This ensures that the first and second parameter to this method always correspond.
                 * visitDeclared is the only method that had this problem.
                 */
                List<? extends AnnotatedTypeMirror> tatypes = type.getTypeArguments();

                if (tatypes == null)
                    return null;

                assert tatypes.size() == typeargtree.getTypeArguments().size();

                for (int i=0; i < tatypes.size(); ++i) {
                    scan(tatypes.get(i), typeargtree.getTypeArguments().get(i));
                }

                return null;

                // Don't call the super version, because it creates a mismatch between
                // the first and second parameters.
                // return super.visitDeclared(type, tree);
            }

            return super.visitDeclared(type, tree);
        }

        private Pair<ParameterizedTypeTree, AnnotatedDeclaredType>
        extractParameterizedTypeTree(Tree tree, AnnotatedDeclaredType type) {
            ParameterizedTypeTree typeargtree = null;

            switch (tree.getKind()) {
            case VARIABLE:
                Tree lt = ((VariableTree)tree).getType();
                if (lt instanceof ParameterizedTypeTree) {
                    typeargtree = (ParameterizedTypeTree) lt;
                } else {
                  //   System.out.println("Found a: " + lt);
                }
                break;
            case PARAMETERIZED_TYPE:
                typeargtree = (ParameterizedTypeTree) tree;
                break;
            case NEW_CLASS:
                NewClassTree nct = (NewClassTree) tree;
                ExpressionTree nctid = nct.getIdentifier();
                if (nctid.getKind()==Tree.Kind.PARAMETERIZED_TYPE) {
                    typeargtree = (ParameterizedTypeTree) nctid;
                    /*
                     * This is quite tricky... for anonymous class instantiations,
                     * the type at this point has no type arguments.
                     * By doing the following, we get the type arguments again.
                     */
                    // FIXME: The following line is commented by huangw5
//                    type = (AnnotatedDeclaredType) factory.getAnnotatedType(typeargtree);
                }
                break;
            case ANNOTATED_TYPE:
                AnnotatedTypeTree tr = (AnnotatedTypeTree) tree;
                ExpressionTree undtr = tr.getUnderlyingType();
                if (undtr instanceof ParameterizedTypeTree) {
                    typeargtree = (ParameterizedTypeTree) undtr;
                } else if (undtr instanceof IdentifierTree) {
                    // @Something D -> Nothing to do
                } else {
                    // TODO: add more test cases to ensure that nested types are handled correctly,
                    // e.g. @Nullable() List<@Nullable Object>[][]
                    Pair<ParameterizedTypeTree, AnnotatedDeclaredType> p = extractParameterizedTypeTree(undtr, type);
                    typeargtree = p.first;
                    type = p.second;
                }
                break;
            case IDENTIFIER:
            case ARRAY_TYPE:
            case NEW_ARRAY:
            case MEMBER_SELECT:
            case UNBOUNDED_WILDCARD:
            case EXTENDS_WILDCARD:
            case SUPER_WILDCARD:
                // Nothing to do.
                // System.out.println("Found a: " + (tree instanceof ParameterizedTypeTree));
                break;
            default:
//                System.err.printf("TypeValidator.visitDeclared unhandled tree: %s of kind %s\n", tree, tree.getKind());
            }

            return Pair.of(typeargtree, type);
        }


        /**
         * Checks that the annotations on the type arguments supplied to a type or a
         * method invocation are within the bounds of the type variables as
         * declared, and issues the "generic.argument.invalid" error if they are
         * not.
         *
         * This method used to be visitParameterizedType, which incorrectly handles the main
         * annotation on generic types.
         */
        protected Void visitParameterizedType(AnnotatedDeclaredType type, ParameterizedTypeTree tree) {
            // System.out.printf("TypeValidator.visitParameterizedType: type: %s, tree: %s\n", type, tree);

            if (TreeUtils.isDiamondTree(tree))
                return null;

            final TypeElement element = (TypeElement) type.getUnderlyingType().asElement();
            if (checker.shouldSkipUses(element))
                return null;

            List<AnnotatedTypeVariable> typevars = factory.typeVariablesFromUse(type, element);

            checkTypeArguments(tree, typevars, type.getTypeArguments(), tree.getTypeArguments());

            return null;
        }
    }
	
}

