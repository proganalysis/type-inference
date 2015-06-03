/**
 * 
 */
package checkers.inference2.transform;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;

import checkers.inference2.InferenceChecker;
import checkers.inference2.Reference;
import checkers.source.SourceVisitor;
import checkers.types.AnnotatedTypeFactory;
import checkers.types.AnnotatedTypes;
import checkers.util.TreeUtils;

import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.IfTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import com.sun.tools.javac.code.TypeTags;
import com.sun.tools.javac.file.JavacFileManager;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCAnnotatedType;
import com.sun.tools.javac.tree.JCTree.JCArrayTypeTree;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCLiteral;
import com.sun.tools.javac.tree.JCTree.JCMethodInvocation;
import com.sun.tools.javac.tree.JCTree.JCNewArray;
import com.sun.tools.javac.tree.JCTree.JCPrimitiveTypeTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import com.sun.tools.javac.tree.JCTree.Tag;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;

/**
 * It has two modes: inference mode and checking mode
 * @author huangw5
 *
 */
public class TransformVisitor extends SourceVisitor<Void, Void> {
	
    /** The checker corresponding to this visitor. */
    //protected final InferenceChecker checker;
    protected final TransformChecker checker;
    
    /** utilities class for annotated types **/
    protected final AnnotatedTypes annoTypes;

	protected final AnnotatedTypeFactory factory;
	
	/** For recording visited method invocation trees or allocation sites */
	protected Set<Tree> visited = new HashSet<Tree>();

	public TransformVisitor(InferenceChecker checker, CompilationUnitTree root) {
		super(checker, root);
		this.checker = (TransformChecker) checker;
		this.checker.setCurrentFactory(atypeFactory);
		this.factory = atypeFactory;
        this.annoTypes =
            new AnnotatedTypes(checker.getProcessingEnvironment(), atypeFactory);
	}
	
    /**
     * Scan a single node.
     * The current path is updated for the duration of the scan.
     */
    @Override
    public Void scan(Tree tree, Void p) {
        if (tree == null)
            return null;

        TreePath prev = checker.currentPath;
        checker.currentPath = new TreePath(checker.currentPath, tree);
        try {
            return tree.accept(this, p);
        } finally {
            checker.currentPath = prev;
        }
    }

    /**
     * et the current path for the node, as built up by the
     * currently active set of scan calls.
     */
    @Override
    public TreePath getCurrentPath() {
        return checker.currentPath;
    }
    
    private JCArrayTypeTree getByteArrayTree(JCTree typeTree) {
    	((JCPrimitiveTypeTree) typeTree).typetag = TypeTags.BYTE;
		TreeMaker make = getTreeMaker();
		JCArrayTypeTree jcptt = make.TypeArray((JCExpression) typeTree);
		return jcptt;
    }

	private TreeMaker getTreeMaker() {
		Context context = new Context();
		JavacFileManager.preRegister(context);
		TreeMaker make = TreeMaker.instance(context);
		return make;
	}
    
    private void processVariableTree(JCTree jctree) {
    	JCTree eleTypeTree;
    	Tag tag = jctree.getTag();
    	switch (tag) {
    	case VARDEF:
    		eleTypeTree = ((JCVariableDecl) jctree).getType();
    		break;
    	case TYPEARRAY:
    		eleTypeTree = ((JCArrayTypeTree) jctree).getType();
    		break;
    	case NEWARRAY:
    		eleTypeTree = ((JCNewArray) jctree).getType();
    		break;
    	case ANNOTATED_TYPE:
    		eleTypeTree = ((JCAnnotatedType) jctree).getUnderlyingType();
    		break;
		default:
			eleTypeTree = null;
			break;
    	}
		if (eleTypeTree != null) {
			modifyTree(jctree, eleTypeTree, tag);
		}
    }

	private void modifyTree(JCTree jctree, JCTree eleTypeTree, Tag tag) {
		if (eleTypeTree.getTag() == Tag.TYPEIDENT) {
			JCPrimitiveTypeTree jcptt = (JCPrimitiveTypeTree) eleTypeTree;
			if (jcptt.getPrimitiveTypeKind() == TypeKind.INT) {
				JCArrayTypeTree jcatt = getByteArrayTree(eleTypeTree);
				switch (tag) {
				case VARDEF:
					((JCVariableDecl) jctree).vartype = jcatt;
					break;
				case TYPEARRAY:
					((JCArrayTypeTree) jctree).elemtype = jcatt;
					break;
				case NEWARRAY:
					((JCNewArray) jctree).elemtype = jcatt;
					break;
				case ANNOTATED_TYPE:
					((JCAnnotatedType) jctree).underlyingType = jcatt;
					break;
				default:
					break;
				}
			}
		} else {
			processVariableTree(eleTypeTree);
		}
	}
    
	private JCMethodInvocation getEncryptMethod(JCLiteral jcl, String encryptType) {
		TreeMaker make = getTreeMaker();
		List<JCExpression> typeargs = null;
		JCExpression fn = make.Select(selected, selector);
		JCExpression encryptTypeTree = make.Literal(encryptType);
		List<JCExpression> args = List.of(jcl, encryptTypeTree);
		JCMethodInvocation jcmi = make.Apply(typeargs, fn, args);
		return jcmi;
	}
	
	private String getSimpleEncryptName(Reference r) {
		Set<AnnotationMirror> annos = r.getAnnotations(checker);
		String fullname = annos.iterator().next().toString();
		return fullname.substring(fullname.lastIndexOf('.') + 1);
	}
	
    public Void visitVariable(VariableTree node, Void p) {
    	VariableElement varElt = TreeUtils.elementFromDeclaration(node);
		Reference varRef = checker.getAnnotatedReference(varElt);
		if (!varRef.getRawAnnotations().contains(checker.CLEAR)) {
			JCVariableDecl jcvd = (JCVariableDecl) node;
			processVariableTree(jcvd);
			JCTree init = jcvd.getInitializer();
			if (init != null) {
				processVariableTree(init);
				// int x = 9 -> int x = Conversion.encrypt(x, "AH")
				if (init.getTag() == Tag.LITERAL) {
					
					// JCMethodInvocation jcmi = getEncryptMethod();
				}
			}
			
		}
		System.out.println(node.toString());
    	return super.visitVariable(node, p);
    }
    
    @Override
	public Void visitAssignment(AssignmentTree node, Void p) {
    	Reference lhsRef = checker.getAnnotatedReference(node.getVariable());
    	//System.out.println("a" + node.toString());
    	if (!lhsRef.getAnnotations(checker).contains(checker.CLEAR)) {
    	//	System.out.println(node.toString());
    	}
		return super.visitAssignment(node, p);
	}
    
//	/**
//     * A unary operation. ++, --, ...
//     */
//    @Override
//    public Void visitUnary(UnaryTree node, Void p) {
//		if (!visited.contains(node)) {
//	        if (node instanceof JCUnary) {
//	            Tag tag = ((JCUnary) node).getTag();
//	            switch (tag) {
//	            case PREINC:
//	            case PREDEC:
//	            case POSTINC:
//	            case POSTDEC:
//	            	processUnaryTree(null, node);
//				default:
//					break;
//	            }
//	        }
//		}
//        return super.visitUnary(node, p);
//    }
//
//	
//	@Override
//    public Void visitBinary(BinaryTree node, Void p) {
//		if (!visited.contains(node)) {
//			if (node instanceof JCBinary) {
//	            Tag tag = ((JCBinary) node).getTag();
//	            switch (tag) {
//	            case BITOR_ASG: // |=
//	            case BITXOR_ASG: // ^=
//	            case BITAND_ASG: // &=
//	            case SL_ASG: // <<=
//	            case SR_ASG: // >>=
//	            case USR_ASG: // >>>=
//	            case PLUS_ASG: // +=
//	            case MINUS_ASG: // -=
//	            case MUL_ASG: // *=
//	            case DIV_ASG: // /=
//	            case MOD_ASG: // %=
//	            	processBinaryTree(null, node);
//				default:
//					break;
//	            }
//	        }
//		}
//        return super.visitBinary(node, p);
//    }
//
//	
//    /**
//     * An assignment with "+=", "|=" ...
//     */
//	@Override
//	public Void visitCompoundAssignment(CompoundAssignmentTree node, Void p) {
//		if (!visited.contains(node)) {
//	        ExpressionTree var = node.getVariable();
//	        ExpressionTree expr = node.getExpression();
//	        generateConstraint(var, expr);
//		}
//		return super.visitCompoundAssignment(node, p);
//	}
//	
//    @Override
//    public Void visitEnhancedForLoop(EnhancedForLoopTree node, Void p) {
//    	// In the enhanced for loop: for (X var : iterables)
//    	// it is equivalent to X var; var = iterables.iteratedType;
//    	// Generate constraints for var, iterated_type and iterables
//    	
//    	// Get the variable
//        VariableTree varTree = node.getVariable();
//		Reference varRef = checker.getAnnotatedReference(TreeUtils
//				.elementFromDeclaration(varTree));
//        // Get the expression
//        ExpressionTree expr = node.getExpression();
//		Reference exprRef = checker.getAnnotatedReference(expr);
//        AnnotatedTypeMirror exprType = exprRef.getType();
//    	// Recursively
//    	generateConstraint(exprRef, expr);
//        
//        if (exprType.getKind() == TypeKind.ARRAY) {
//	        // In the case of arrays
//        	ArrayReference arrayRef = (ArrayReference) exprRef;
//        	Reference componentRef = arrayRef.getComponentRef();
//			checker.handleInstanceFieldRead(arrayRef, componentRef, varRef, varRef.getLineNum());
//        } else {
//        	// It is an iterable type, we simply enforce iterables <: var
//        	checker.addSubtypeConstraint(exprRef, varRef, varRef.getLineNum());
//        }
//        return super.visitEnhancedForLoop(node, p);
//    }
//    
//    
//	@Override
//	public Void visitMethod(MethodTree node, Void p) {
//		ExecutableElement methodElt = TreeUtils.elementFromDeclaration(node);
//		// Add override constraints
//        // Find which method this overrides!
//        Map<AnnotatedDeclaredType, ExecutableElement> overriddenMethods = annoTypes
//                .overriddenMethods(methodElt);
//        for (Map.Entry<AnnotatedDeclaredType, ExecutableElement> pair 
//        		: overriddenMethods.entrySet()) {
//        	ExecutableElement overriddenElement = pair.getValue();
//        	checker.handleMethodOverride(methodElt, overriddenElement);
//        }		
//		return super.visitMethod(node, p);
//	}
//    
//    @Override
//	public Void visitMethodInvocation(MethodInvocationTree node, Void p) {
//    	// If this statement is enclosed in another expression, then it should
//    	// have been visited and we skip it. 
//    	// E.g. X x = y.m(z); this method invocation is visited in the assignment
//    	// E.g. y.m(x.n(z));  the x.n(z) is visited as an argument   
//    	// it could have been visited in generateConstraints
//    	if (node.toString().equals("super()") 
//    			|| node.toString().equals("Object()")) {
//			return super.visitMethodInvocation(node, p);
//    	}
//		if (!visited.contains(node)) {
//			Reference assignTo = null;
//			// Assume the LHS is the node
//			assignTo = checker.getAnnotatedReference(node);
//			processMethodCall(node, assignTo); 
//    	}
//		return super.visitMethodInvocation(node, p);
//	}
//
//    
//	@Override
//	public Void visitNewArray(NewArrayTree node, Void p) {
//    	// If this statement is enclosed in another expression, then it should
//    	// have been visited and we skip it. 
//    	// E.g. X[] x = new X[3]; this method invocation is visited in the assignment
//		if (!visited.contains(node)) {
//    		warn("Unhandled statement " + node);
//    	}
//		return super.visitNewArray(node, p);
//	}
//	
//
//	@Override
//	public Void visitNewClass(NewClassTree node, Void p) {
//    	// If this statement is enclosed in another expression, then it should
//    	// have been visited and we skip it. 
//    	// E.g. X x = new X(); this method invocation is visited in the assignment
//		if (!visited.contains(node)) {
//            Reference assignTo = checker.getAnnotatedReference(node);
//			processNewClass(node, assignTo);
//    	}
//		return super.visitNewClass(node, p);
//	}
//
//
//	@Override
//	public Void visitReturn(ReturnTree node, Void p) {
//		ExpressionTree expr = node.getExpression();
//		if (expr != null) {
//			MethodTree methodTree = TreeUtils.enclosingMethod(getCurrentPath());
//			ExecutableElement methodElt = (ExecutableElement) 
//					TreeUtils.elementFromDeclaration(methodTree);
//			// Get the reference of return 
//			Reference methodRef = checker.getAnnotatedReference(methodElt);
//			Reference returnRef = ((ExecutableReference) methodRef).getReturnRef();
//            generateConstraint(returnRef, expr);
//		}
//		return super.visitReturn(node, p);
//	}
//	
//
//	@Override
//	public Void visitVariable(VariableTree node, Void p) {
//		VariableElement varElt = TreeUtils.elementFromDeclaration(node);
//		Reference varRef = checker.getAnnotatedReference(varElt);
//		ExpressionTree initializer = node.getInitializer();
//		if (initializer != null) {
//			if (varElt.getKind().isField()) {
//				Reference initRef = checker.getAnnotatedReference(initializer);
//				generateConstraint(initRef, initializer);
//				processVariableTree(node, initRef);
//			} else {
//				generateConstraint(varRef, initializer);
//			}
//		}
//		return super.visitVariable(node, p);
//	}
//	
//	
//	@Override
//	public Void visitClass(ClassTree node, Void p) {
//		//TypeElement classElt = TreeUtils.elementFromDeclaration(node);
//		//checker.addVisitedClass(classElt);
//		System.out.println(node.toString());
//		return super.visitClass(node, p);
//	}
	
	@Override
	public Void visitIf(IfTree node, Void p) {
		//System.out.println(node.toString());
		return super.visitIf(node, p);
	}

//	/**
//	 * Get current method element; 
//	 * @return <code>null</code> if it is not in a method; otherwise return the
//	 * current method element. 
//	 */
//    private ExecutableElement getCurrentMethodElt() {
//        MethodTree enclosingMethod = TreeUtils.enclosingMethod(
//        		this.getCurrentPath());
//        if (enclosingMethod == null)
//            return null;
//        else
//            return TreeUtils.elementFromDeclaration(enclosingMethod);
//    }
//    
//    /**
//     * with field
//     */
//    private Reference getDefaultConstructorThisRefWithField(Element fieldElt) {
//        if (!fieldElt.getKind().isField())
//            return null;
//        TreePath p = getCurrentPath();
//        while (p != null) {
//            Tree leaf = p.getLeaf();
//            assert leaf != null; /*nninvariant*/
//            if (TreeUtils.isClassTree(leaf.getKind())) {
//                AnnotatedDeclaredType classType = (AnnotatedDeclaredType) factory
//                        .getAnnotatedType((ClassTree) leaf);
//                TypeElement elem = TreeUtils.elementFromDeclaration((ClassTree) leaf);
//                if (checker.isFieldElt(classType, fieldElt)) {
//                    return checker.getAnnotatedReference(elem);
//                }
//            }
//            p = p.getParentPath();
//        }
//        return null;
//    }
//
//	
//    
//    private void processMethodCall(MethodInvocationTree node, Reference assignToRef) {
//    	ExecutableElement invokeMethodElt = TreeUtils.elementFromUse(node);
//		// Get the receiver
//		Reference rcvRef = null;
//		if (!ElementUtils.isStatic(invokeMethodElt)) {
//			ExpressionTree rcvTree = TreeUtils.getReceiverTree(node);
//			if (rcvTree == null) {
//				// This may be a a self invocation like x = m(z); 
//				// WEI: Need considering method calls to outer class
//				ExecutableElement currentMethodElt = checker.getEnclosingMethodWithElt(invokeMethodElt);
//				// If the invokeMethod is from the super class, then currentMethodElt could be null
//				if (currentMethodElt == null) {
//					currentMethodElt = getCurrentMethodElt();
//				}
//				if(currentMethodElt != null) {
//					Reference currentMethodRef = checker.getAnnotatedReference(currentMethodElt);
//					rcvRef = ((ExecutableReference) currentMethodRef).getThisRef();
//				}
//			} else {
//				rcvRef = checker.getAnnotatedReference(rcvTree);
//				// generate constraints on the receiver recursively
//				generateConstraint(rcvRef, rcvTree);
//			}
//		}
//		// Process arguments
//		List<? extends ExpressionTree> arguments = node.getArguments();
//		List<Reference> argumentRefs = new ArrayList<Reference>(arguments.size());
//		for (ExpressionTree arg : arguments) {
//			Reference argRef = checker.getAnnotatedReference(arg);
//			// recursively 
//			generateConstraint(argRef, arg);
//			argumentRefs.add(argRef);
//		}
//		checker.handleMethodCall(invokeMethodElt, rcvRef, assignToRef, argumentRefs);
//    }
//    
//    private void processNewClass(NewClassTree node, Reference assignToRef) {
//		// Receiver
//		Reference rcvRef = checker.getAnnotatedReference(node);
//		// always connect to the LHS
//		checker.addSubtypeConstraint(rcvRef, assignToRef, rcvRef.getLineNum());
//		// Arguments
//		List<? extends ExpressionTree> arguments = node.getArguments();
//		List<Reference> argumentRefs = new ArrayList<Reference>(arguments.size());
//		for (ExpressionTree arg : arguments) {
//			Reference argRef = checker.getAnnotatedReference(arg);
//			// recursively 
//			generateConstraint(argRef, arg);
//			argumentRefs.add(argRef);
//		}
//		checker.handleMethodCall(TreeUtils.elementFromUse(node), rcvRef,
//				assignToRef, argumentRefs);
//    }
//    
//    private void processMemberSelect(Reference lhsRef, MemberSelectTree mTree, Reference rhsRef) {
//        ExpressionTree rcvExpr = mTree.getExpression();
//        Element fieldElt = TreeUtils.elementFromUse(mTree);
//        AnnotatedTypeMirror rcvType = factory.getAnnotatedType(rcvExpr);
//        long lineNum = checker.getLineNumber(mTree);
//        if (checker.isAccessOuterThis(mTree)) {
//            // If it is like Body.this
//            Element outerElt = checker.getOuterThisElement(mTree, getCurrentMethodElt());
//            if (outerElt != null 
//                    && outerElt.getKind() ==  ElementKind.METHOD) {
//                ExecutableElement outerExecutableElt = (ExecutableElement) outerElt;
//                Reference outerMethodRef = checker.getAnnotatedReference(outerExecutableElt);
//                Reference outerThisRef = ((ExecutableReference) outerMethodRef).getThisRef();
//                checker.addSubtypeConstraint(outerThisRef, lhsRef, lineNum);
//            } else {
//                // we have to enforce currentMethod <: lhsRef
//                ExecutableElement currentMethodElt = getCurrentMethodElt();
//                if (currentMethodElt != null) {
//                    Reference currentMethodRef = checker.getAnnotatedReference(currentMethodElt);
//                    Reference thisRef = ((ExecutableReference) currentMethodRef).getThisRef();
//                    checker.addSubtypeConstraint(thisRef, lhsRef, lineNum);
//                }
//            }
//        } else if (ElementUtils.isStatic(fieldElt)) {
//            Reference fieldRef = checker.getAnnotatedReference(fieldElt);
//            if (lhsRef != null && rhsRef == null) {
//	            checker.handleStaticFieldRead(fieldRef, lhsRef, lineNum);
//            } else if (lhsRef == null && rhsRef != null) {
//            	checker.handleStaticFieldWrite(fieldRef, rhsRef, lineNum);
//            } 
//        } else if (!fieldElt.getSimpleName().contentEquals("super")
//                && checker.isFieldElt(rcvType, fieldElt)) {
//            Reference fieldRef = checker.getAnnotatedReference(fieldElt);
//            // There may be type casts on "this", 
//            // e.g. ((@mutable X) this).field
//            Reference rcvRef = checker.getAnnotatedReference(rcvExpr);
//            // Recursively generate constraints
//            generateConstraint(rcvRef, rcvExpr);
//            if (lhsRef != null && rhsRef == null) {
//	            checker.handleInstanceFieldRead(rcvRef, fieldRef, lhsRef, lineNum);
//            } else if (lhsRef == null && rhsRef != null) {
//            	checker.handleInstanceFieldWrite(rcvRef, fieldRef, rhsRef, lineNum);
//            } 
//        } 
//		if (lhsRef == null && rhsRef == null || lhsRef != null
//				&& rhsRef != null) {
//			warn("Something is wrong when processing member select: lhs = "
//					+ lhsRef + "; rhs = " + rhsRef);
//		}
//    }
//    
//    private void processIdentifier(Reference lhsRef, IdentifierTree idTree, Reference rhsRef) {
//        ExecutableElement currentMethodElt = getCurrentMethodElt();
//        Element idElt = TreeUtils.elementFromUse(idTree);
//        long lineNum = checker.getLineNumber(idTree);
//        // If idElt is "this", then we create the thisRef
//        Reference idRef = null;
//        if (idElt.getSimpleName().contentEquals("this")) {
//        	if (currentMethodElt != null) {
//        		Reference currentMethodRef = checker.getAnnotatedReference(currentMethodElt);
//                idRef = ((ExecutableReference) currentMethodRef).getThisRef();
//        	} else { // handle instance initialization blocks 
//        		idRef = lhsRef;
//        	}
//        } else {
//            idRef = checker.getAnnotatedReference(idElt);
//        }
//        if (idElt.getSimpleName().contentEquals("super")) {
//        	idRef = lhsRef;
//        }
//        // Check if idElt is a field or not
//        if (!idElt.getSimpleName().contentEquals("this")
//                && !idElt.getSimpleName().contentEquals("super")
//                && idElt.getKind() == ElementKind.FIELD) {
//        	if (ElementUtils.isStatic(idElt)) {
//	            if (lhsRef != null && rhsRef == null) {
//		            checker.handleStaticFieldRead(idRef, lhsRef, lineNum);
//	            } else if (lhsRef == null && rhsRef != null) {
//	            	checker.handleStaticFieldWrite(idRef, rhsRef, lineNum);
//	            } 
//        	} else {
//	            Reference thisRef = null;
//	            ExecutableElement enclosingMethodElt = checker.getEnclosingMethodWithField(idElt);
//	            if (enclosingMethodElt != null) {
//	                // If there is an enclosing method. In most cases, it is just the current
//	            	// visiting method
//	                Reference currentMethodRef = checker.getAnnotatedReference(enclosingMethodElt);
//	                thisRef = ((ExecutableReference) currentMethodRef).getThisRef();
//	            } else {
//	                // This happens in static initializer. But reading should not
//	                // be possible
//	                thisRef = getDefaultConstructorThisRefWithField(idElt); // WEI: Add on Dec 5, 2012
//	            }
//	            if (lhsRef != null && rhsRef == null) {
//		            checker.handleInstanceFieldRead(thisRef, idRef, lhsRef, lineNum);
//	            } else if (lhsRef == null && rhsRef != null) {
//	            	checker.handleInstanceFieldWrite(thisRef, idRef, rhsRef, lineNum);
//	            } 
//        	}
//        } else {
//			if (lhsRef != null && rhsRef == null) {
//				checker.addSubtypeConstraint(idRef, lhsRef, lineNum);
//			} else if (lhsRef == null && rhsRef != null) {
//				checker.addSubtypeConstraint(rhsRef, idRef, lineNum);
//			} 
//        }
//		if (lhsRef == null && rhsRef == null 
//				|| lhsRef != null && rhsRef != null) {
//			warn("Something is wrong when processing identifier: lhs = "
//					+ lhsRef + "; rhs = " + rhsRef);
//		}
//    }
//    
//    private void processNewArray(Reference lhsRef, NewArrayTree nArrayTree) {
//    	long lineNum = checker.getLineNumber(nArrayTree);
//		// Create the reference of the new array
//		ArrayReference nArrayRef = (ArrayReference) checker.getAnnotatedReference(nArrayTree);
//		// Generate constraints
//		checker.addSubtypeConstraint(nArrayRef, lhsRef, lineNum);
//		
//		List<? extends ExpressionTree> aInitializers = nArrayTree.getInitializers();
//		// Generate constraints for the initializers and the array
//		// if the initializers are not empty
//		if (aInitializers != null && !aInitializers.isEmpty()) {
//			// Get its component reference
//			Reference componentRef = nArrayRef.getComponentRef();
//			// I think it should do the adapt first, because it is
//			// equivalent to:
//			// X[] a = new X[2]; a[0] = x1; a[1] = x2;
//			for (ExpressionTree initializer : aInitializers) {
//				Reference initializerRef = checker.getAnnotatedReference(initializer);
//				// Recursively, because it could be like X[] a = new X[]{c.getInt(), b[2]};
//				generateConstraint(initializerRef, initializer);
//				// Now add the adapt constraint
//				checker.handleInstanceFieldWrite(nArrayRef, componentRef, initializerRef, lineNum);
//			}
//		}
//    }
//    
//    private void processArrayAccess(Reference lhsRef, ArrayAccessTree aaTree, Reference rhsRef) {
//		ExpressionTree aaExpr = aaTree.getExpression();
//		Reference exprRef = checker.getAnnotatedReference(aaExpr);
//
//		// Recursively
//		generateConstraint(exprRef, aaExpr);
//
//		// Get the component reference of this array access
//		Reference componentRef = ((ArrayReference) exprRef).getComponentRef();
//		long lineNum = checker.getLineNumber(aaTree);
//		// Now add the adapt constraint
//		if (lhsRef != null && rhsRef == null) {
//			checker.handleInstanceFieldRead(exprRef, componentRef, lhsRef, lineNum);
//		} else if (lhsRef == null && rhsRef != null) {
//			checker.handleInstanceFieldWrite(exprRef, componentRef, rhsRef, lineNum);
//		}
//		if (lhsRef == null && rhsRef == null 
//				|| lhsRef != null && rhsRef != null) {
//			warn("Something is wrong when processing ArrayAccessTree: lhs = "
//					+ lhsRef + "; rhs = " + rhsRef);
//		}
//    }
//    
//    private void processTypeCast(Reference lhsRef, TypeCastTree tree) {
//        // Get the tree being casted 
//        ExpressionTree castedTree = tree.getExpression();
//        Reference castedRef = checker.getAnnotatedReference(castedTree);
//        // Recursively, cases like (Long (Int)) var;
//        generateConstraint(castedRef, castedTree);
//        
//        AnnotatedTypeMirror type = factory.getAnnotatedType(tree);
//        if (!checker.isAnnotated(type)) {
//            // In the case no annotations appear in the cast 
//            // connect the casted expr and the lhs.
//        	// However, if we have something like A x = (@Mutable A) y, 
//        	// then we don't connect, because "type" is annotated
//            checker.addSubtypeConstraint(castedRef, lhsRef, checker.getLineNumber(tree));
//        }
//    }
//    
//    private void processParenthesized(Reference lhsRef, ParenthesizedTree pTree, Reference rhsRef) {
//        ExpressionTree pExpr = pTree.getExpression();
//        long lineNum = checker.getLineNumber(pTree);
//        Reference pRef = checker.getAnnotatedReference(pExpr);
//        // Recursively 
//        generateConstraint(pRef, pExpr);
//		if (lhsRef != null && rhsRef == null) {
//			checker.addSubtypeConstraint(pRef, lhsRef, lineNum);
//		} else if (lhsRef == null && rhsRef != null) {
//			checker.addSubtypeConstraint(rhsRef, pRef, lineNum);
//		}
//    }
//    
//    private void processNestedAssignment(Reference lhsRef, AssignmentTree aTree) {
//        ExpressionTree aExpr = aTree.getVariable();
//        Reference aRef = checker.getAnnotatedReference(aExpr);
//        // Recursively
//        generateConstraint(aRef, aExpr);
//        checker.addSubtypeConstraint(aRef, lhsRef, checker.getLineNumber(aTree));
//	}
//    
//    /**
//     * Tree like a > b ? a : b;
//     * @param lhsRef
//     * @param cTree
//     */
//    private void processConditionalExpression(Reference lhsRef, ConditionalExpressionTree cTree) {
//        long lineNum = checker.getLineNumber(cTree);
//    	ExpressionTree cTrueExpr = cTree.getTrueExpression();
//        Reference cTrueRef = checker.getAnnotatedReference(cTrueExpr);
//        generateConstraint(cTrueRef, cTrueExpr);
//        checker.addSubtypeConstraint(cTrueRef, lhsRef, lineNum);
//        
//        ExpressionTree cFalseExpr = cTree.getFalseExpression();
//        Reference cFalseRef = checker.getAnnotatedReference(cFalseExpr);
//        generateConstraint(cFalseRef, cFalseExpr);
//        checker.addSubtypeConstraint(cFalseRef, lhsRef, lineNum);
//    }
//    
//    public void processBinaryTree(Reference lhsRef, BinaryTree bTree) {
//    	long lineNum = checker.getLineNumber(bTree);
//		ExpressionTree left = bTree.getLeftOperand();
//		ExpressionTree right = bTree.getRightOperand();
//		Reference leftRef = checker.getAnnotatedReference(left);
//		Reference rightRef = checker.getAnnotatedReference(right);
//		if (lhsRef != null) {
//			checker.addSubtypeConstraint(leftRef, lhsRef, lineNum);
//			checker.addSubtypeConstraint(rightRef, lhsRef, lineNum);
//		}
//		generateConstraint(leftRef, left);
//		generateConstraint(rightRef, right);
//    }
//    
//    public void processUnaryTree(Reference lhsRef, UnaryTree uTree) {
//		ExpressionTree exprTree = uTree.getExpression();
//		Reference ref = checker.getAnnotatedReference(exprTree);
//		if (lhsRef != null) {
//			checker.addSubtypeConstraint(ref, lhsRef, checker.getLineNumber(uTree));
//		}
//		generateConstraint(ref, exprTree);
//    }
//    
//    private void processVariableTree(VariableTree tree, Reference initRef) {
//    	long lineNum = checker.getLineNumber(tree);
//		VariableElement varElt = TreeUtils.elementFromDeclaration(tree);
//		Reference varRef = checker.getAnnotatedReference(varElt);
//		if (initRef != null) {
//			// Add subtype constraint
//			// The field should be accessed by adapting it from PoV
//			// the default constructor. The implementation below can be 
//			// merged with the field access in static initializer.
//			if (varElt.getKind().isField()) {
//				Reference defaultConstructorThisRef = getDefaultConstructorThisRefWithField(varElt);
//				checker.handleInstanceFieldWrite(defaultConstructorThisRef, varRef, initRef, lineNum);
//			} else {
//                checker.addSubtypeConstraint(initRef, varRef, lineNum);
//			}
//		}
//    }
//    
//	/**
//	 * lhsTree = rhsTree;
//	 * ===>
//	 * lhsTree = lhsRef; lhsRef = rhsRef; rhsRef = rhsTree;
//	 * @param lhsTree
//	 * @param rhsTree
//	 */
//	protected void generateConstraint(ExpressionTree lhsTree, ExpressionTree rhsTree) {
//		Reference lhsRef = checker.getAnnotatedReference(lhsTree);
//		Reference rhsRef = checker.getAnnotatedReference(rhsTree);
//		checker.addSubtypeConstraint(rhsRef, lhsRef, checker.getLineNumber(rhsTree));
//		generateConstraint(lhsTree, lhsRef);
//		generateConstraint(rhsRef, rhsTree);
//	}
//	
//	/**
//	 * Generate constraints for lhsRef = rhsTree;
//	 * @param lhsRef
//	 * @param rhsTree
//	 */
//	protected void generateConstraint(Reference lhsRef, ExpressionTree rhsTree) {
//        TreePath prev = checker.currentPath;
//		checker.currentPath = new TreePath(checker.currentPath, rhsTree);
//        try {
//            switch (rhsTree.getKind()) {
//            case NEW_CLASS:
//                processNewClass((NewClassTree) rhsTree, lhsRef);
//                break;
//            case METHOD_INVOCATION:
//                processMethodCall((MethodInvocationTree) rhsTree, lhsRef);
//                break;
//            case MEMBER_SELECT:
//                processMemberSelect(lhsRef, (MemberSelectTree) rhsTree, null);
//                break;
//            case IDENTIFIER:
//            	processIdentifier(lhsRef, (IdentifierTree) rhsTree, null);
//                break;
//            case NEW_ARRAY:
//            	processNewArray(lhsRef, (NewArrayTree) rhsTree);
//                break;
//            case ARRAY_ACCESS:
//                processArrayAccess(lhsRef, (ArrayAccessTree) rhsTree, null);
//                break;
//            case TYPE_CAST:
//            	processTypeCast(lhsRef, (TypeCastTree) rhsTree);
//                break;
//            case PARENTHESIZED:
//                processParenthesized(lhsRef, (ParenthesizedTree) rhsTree, null);
//                break;
//            case ASSIGNMENT:
//                processNestedAssignment(lhsRef, (AssignmentTree) rhsTree);
//                break;
//            case CONDITIONAL_EXPRESSION:
//                processConditionalExpression(lhsRef, (ConditionalExpressionTree) rhsTree);
//                break;
//            case INT_LITERAL:
//            case LONG_LITERAL:
//            case FLOAT_LITERAL:
//            case DOUBLE_LITERAL:
//            case BOOLEAN_LITERAL:
//            case CHAR_LITERAL:
//            case STRING_LITERAL:
//            case NULL_LITERAL:
//                break;
//            default:
//                // Check other cases
//                if (rhsTree instanceof BinaryTree) {
//                    processBinaryTree(lhsRef, (BinaryTree) rhsTree);
//                } else if (rhsTree instanceof UnaryTree) {
//                    processUnaryTree(lhsRef, (UnaryTree) rhsTree);
//                } 
//            }
//            visited.add(rhsTree);
//        } finally {
//            checker.currentPath = prev;
//        }
//	}
//	
//	/**
//	 * An assignment of "tree = ref". Generate constraints based on the 
//	 * structure of the <code>lhsTree</code>
//	 * @param lhsTree
//	 * @param rhsRef
//	 */
//	protected void generateConstraint(ExpressionTree lhsTree, Reference rhsRef) {
//        TreePath prev = checker.currentPath;
//		checker.currentPath = new TreePath(checker.currentPath, lhsTree);
//        try {
//            switch (lhsTree.getKind()) {
//            case VARIABLE:
//                // generate Reference of element
//                processVariableTree((VariableTree) lhsTree, rhsRef);
//                break;
//            case IDENTIFIER:
//            	processIdentifier(null, (IdentifierTree) lhsTree, rhsRef);
//                break;
//            case MEMBER_SELECT:
//                // Okay, it is an member selection. Need to construct the adapt
//                // constraint. 
//                processMemberSelect(null, (MemberSelectTree) lhsTree, rhsRef);
//                break;
//            case ARRAY_ACCESS:
//                processArrayAccess(null, (ArrayAccessTree) lhsTree, rhsRef);
//                break;
//            case PARENTHESIZED:
//                processParenthesized(null, (ParenthesizedTree) lhsTree, rhsRef);
//                break;
//            default:
//            }
//            visited.add(lhsTree);
//        } finally {
//            checker.currentPath = prev;
//        }
//	}
	
}
