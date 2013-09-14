/**
 * 
 */
package checkers.inference.reim;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import checkers.basetype.BaseTypeVisitor;
import checkers.inference.InferenceUtils;
import checkers.source.Result;
import checkers.types.AnnotatedTypeMirror;
import checkers.types.AnnotatedTypeMirror.AnnotatedDeclaredType;
import checkers.types.AnnotatedTypeMirror.AnnotatedExecutableType;
import checkers.types.AnnotatedTypeMirror.AnnotatedTypeVariable;
import checkers.util.Pair;
import checkers.util.TreeUtils;

import com.sun.source.tree.AnnotatedTypeTree;
import com.sun.source.tree.ArrayAccessTree;
import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ConditionalExpressionTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.NewArrayTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.ParameterizedTypeTree;
import com.sun.source.tree.PrimitiveTypeTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.TypeCastTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;

/**
 * @author huangw5
 *
 */
public class ReimVisitor extends BaseTypeVisitor<ReimChecker> {

	public ReimVisitor(ReimChecker checker, CompilationUnitTree root) {
		super(checker, root);
		// TODO Auto-generated constructor stub
	}
	
	
	private boolean canIgnored(AnnotatedTypeMirror varType,
			AnnotatedTypeMirror valueType) {
		if (varType != null && checker.isDefaultReadonlyType(varType)
				|| valueType != null && checker.isDefaultReadonlyType(valueType))
			return true;
		
		// Polyread can be assigned to any variable
		if (InferenceUtils.getMaxType(valueType, checker.getComparator()).hasAnnotation(
				checker.POLYREAD))
			return true;
		
		Element varElt = varType.getElement();
		Element valueElt = valueType.getElement();
		if (varElt != null && checker.isFromLibrary(varElt) 
				|| valueElt != null && checker.isFromLibrary(valueElt))
			return true;
		return false;
	}
	
	private AnnotatedTypeMirror getAdaptContextType(Tree node) {
		if (!(node instanceof MethodInvocationTree) 
				&& !(node instanceof NewClassTree))
			return null;
		if (node instanceof MethodInvocationTree) {
			ExecutableElement methodElt = TreeUtils.elementFromUse(
					(MethodInvocationTree) node);
			TypeMirror returnType = methodElt.getReturnType();
			if (returnType.getKind() == TypeKind.VOID 
					|| returnType.getKind().isPrimitive()) {
				return null;
			}
		}
    	AnnotatedTypeMirror adaptContextType = null;
        // Look for the adapt context, it can be:
        // 1) LHS of an assignment: lhs = a.m();
        // 2) Variable: Object x = a.m();
        // 3) As an argument in another method invocation: b.n(a.m());
    	// 4) As the receiver in another method invocation: a.m().n(); 
    	// 5) As the return value: return a.m();
    	// 6) As the conditional expression: a == null? b.m() : null;
    	// 7) As New Array
    	// 8) instanceof, etc
    	// More?
        TreePath path = getCurrentPath().getParentPath();
        Tree leaf = null;
        do {
        	leaf = path.getLeaf();
			if (leaf.getKind() == Kind.ASSIGNMENT) {
				ExpressionTree var = ((AssignmentTree) leaf).getVariable();
				ExpressionTree expr = ((AssignmentTree) leaf).getExpression();
				// Now check if expr is equal to node. However, expr can 
				// be a type cast
				expr = TreeUtils.skipParens(expr);
				while (expr.getKind() == Kind.TYPE_CAST) {
					expr = ((TypeCastTree) expr).getExpression();
					expr = TreeUtils.skipParens(expr);
				}
				if (expr.equals(node))
					adaptContextType = atypeFactory.getAnnotatedType(var);
			} else if (leaf.getKind() == Kind.VARIABLE) {
				ExpressionTree initializer = ((VariableTree) leaf).getInitializer();
				if (initializer != null) {
					// Same as above 
					initializer = TreeUtils.skipParens(initializer);
					while (initializer.getKind() == Kind.TYPE_CAST) {
						initializer = ((TypeCastTree) initializer).getExpression();
						initializer = TreeUtils.skipParens(initializer);
					}
					if (initializer.equals(node))
						adaptContextType = atypeFactory.getAnnotatedType(leaf);
				}
			} else if (leaf.getKind() == Kind.METHOD_INVOCATION
					|| leaf.getKind() == Kind.NEW_CLASS) {
				// Now look for where this node locates and the corresponding 
				// parameter type
				List<? extends ExpressionTree> arguments = null;
				ExecutableElement methodElt = null;
				if (leaf.getKind() == Kind.METHOD_INVOCATION) {
					MethodInvocationTree mTree = (MethodInvocationTree) leaf;
					arguments = mTree.getArguments();
					methodElt = TreeUtils.elementFromUse(mTree);
				} else {
					NewClassTree nTree = (NewClassTree) leaf;
					arguments = nTree.getArguments();
					methodElt = TreeUtils.elementFromUse(nTree);
				}
				List<? extends VariableElement> parameters = methodElt.getParameters();
				assert arguments.size() == parameters.size();
				for (int i = 0; i < parameters.size(); i++) {
					ExpressionTree argExpr = arguments.get(i);
					// In case argExpr is a type cast
					argExpr = TreeUtils.skipParens(argExpr);
					while (argExpr.getKind() == Kind.TYPE_CAST) {
						argExpr = ((TypeCastTree) argExpr).getExpression();
						argExpr = TreeUtils.skipParens(argExpr);
					}
					if (argExpr.equals(node)) {
						adaptContextType = atypeFactory.getAnnotatedType(
								parameters.get(i));
						break;
					}
				}
				if (adaptContextType == null 
						&& leaf.getKind() == Kind.METHOD_INVOCATION) {
					// Now it may be the receiver in this method invocation.
					ExpressionTree rcvTree = TreeUtils.getReceiverTree((ExpressionTree) leaf);
					if (rcvTree != null) {
						// In case rcvTree is a type cast
						rcvTree = TreeUtils.skipParens(rcvTree);
						while (rcvTree.getKind() == Kind.TYPE_CAST) {
							rcvTree = ((TypeCastTree) rcvTree).getExpression();
							rcvTree = TreeUtils.skipParens(rcvTree);
						}
						if (rcvTree.equals(node)) {
							adaptContextType = atypeFactory.getAnnotatedType(rcvTree);
						}
					}
				}
			} else if (leaf.getKind() == Kind.RETURN) {
				ReturnTree rTree = (ReturnTree) leaf;
				ExpressionTree returnExpr = rTree.getExpression();
				returnExpr = TreeUtils.skipParens(returnExpr);
				while (returnExpr.getKind() == Kind.TYPE_CAST) {
					returnExpr = ((TypeCastTree) returnExpr).getExpression();
					returnExpr = TreeUtils.skipParens(returnExpr);
				}
				if (returnExpr.equals(node)) {
					ExecutableElement methodElt = TreeUtils.elementFromDeclaration(TreeUtils
							.enclosingMethod(getCurrentPath()));
					AnnotatedExecutableType methodType = atypeFactory
							.getAnnotatedType(methodElt);
					adaptContextType = methodType.getReturnType();
				}
			} else if (leaf.getKind() == Kind.CONDITIONAL_EXPRESSION) {
				ConditionalExpressionTree cTree = (ConditionalExpressionTree) leaf;
				ExpressionTree trueExpr = cTree.getTrueExpression();
				trueExpr = TreeUtils.skipParens(trueExpr);
				while (trueExpr.getKind() == Kind.TYPE_CAST) {
					trueExpr = ((TypeCastTree) trueExpr).getExpression();
					trueExpr = TreeUtils.skipParens(trueExpr);
				}
				if (trueExpr.equals(node))
					adaptContextType = atypeFactory.getAnnotatedType(cTree);
				else {
					ExpressionTree falseExpr = cTree.getFalseExpression();
					falseExpr = TreeUtils.skipParens(falseExpr);
					while (falseExpr.getKind() == Kind.TYPE_CAST) {
						falseExpr = ((TypeCastTree) falseExpr).getExpression();
						falseExpr = TreeUtils.skipParens(falseExpr);
					}
					if (falseExpr.equals(node))
						adaptContextType = atypeFactory.getAnnotatedType(cTree);
				}
			} else if (leaf.getKind() == Kind.NEW_ARRAY) {
				NewArrayTree naTree = (NewArrayTree) leaf;
				List<? extends ExpressionTree> initializers = naTree.getInitializers();
				if (initializers != null && !initializers.isEmpty()) {
					for (ExpressionTree initializer : initializers) {
						initializer = TreeUtils.skipParens(initializer);
						while (initializer.getKind() == Kind.TYPE_CAST) {
							initializer = ((TypeCastTree) initializer).getExpression();
							initializer = TreeUtils.skipParens(initializer);
						}
						if (initializer.equals(node)) {
							adaptContextType = atypeFactory.getAnnotatedType(
									initializer);
							break;
						}
					}
				}
			} else if (leaf.getKind() == Kind.EXPRESSION_STATEMENT) {
				if (leaf.toString().equals(node.toString())) {
					adaptContextType = atypeFactory.getAnnotatedType(node);
					adaptContextType.clearAnnotations();
					
					adaptContextType.addAnnotation(checker.READONLY);
				}
			} else if (leaf.getKind() == Kind.MEMBER_SELECT) {
				// Get the receiver type
				MemberSelectTree mTree = (MemberSelectTree) leaf;
				adaptContextType = atypeFactory.getAnnotatedType(mTree.getExpression());
			} else if (leaf.getKind() == Kind.INSTANCE_OF
					|| (leaf instanceof BinaryTree)) {
				// use immutable
				adaptContextType = atypeFactory.getAnnotatedType(node);
				adaptContextType.clearAnnotations();
				adaptContextType.addAnnotation(checker.READONLY);
			}
			path = path.getParentPath();
		} while (path != null && leaf.getKind() != Kind.BLOCK 
				&& adaptContextType == null);
        return adaptContextType;
	}


	@Override
	protected void commonAssignmentCheck(AnnotatedTypeMirror varType,
			AnnotatedTypeMirror valueType, Tree valueTree, String errorKey) {
//		if (valueTree.toString().contains("short.class"))
//			valueType = atypeFactory.getAnnotatedType(valueTree);
        boolean success = checker.isSubtype(valueType, varType);
        if (!success && canIgnored(varType, valueType))
        	return;  // Ignored
		super.commonAssignmentCheck(varType, valueType, valueTree, errorKey);
	}

	
	
	@Override
	protected void typeCheckVectorCopyIntoArgument(MethodInvocationTree node,
			List<? extends AnnotatedTypeMirror> params) {
		// TODO:
//		super.typeCheckVectorCopyIntoArgument(node, params);
	}

	@Override
	protected boolean checkOverride(MethodTree overriderTree,
			AnnotatedDeclaredType enclosingType,
			AnnotatedExecutableType overridden,
			AnnotatedDeclaredType overriddenType, Void p) {
		if (checker.shouldSkipUses(overriddenType.getElement())) {
            return true;
        }
		
		boolean fromLibrary = false;
		Element overriddenElt = overriddenType.getUnderlyingType().asElement();
		
		if (overriddenElt != null
				&& (overriddenElt instanceof ExecutableElement
						&& checker.isDefaultPureMethod((ExecutableElement) overriddenElt) 
						|| checker.isFromLibrary(overriddenElt))) {
			fromLibrary = true;
		}
		
        // Get the type of the overriding method.
        AnnotatedExecutableType overrider =
            atypeFactory.getAnnotatedType(overriderTree);

        boolean result = true;

        if (overrider.getTypeVariables().isEmpty() && !overridden.getTypeVariables().isEmpty()) {
            overridden = overridden.getErased();
        }
        String overriderMeth = overrider.getElement().toString();
        String overriderTyp = enclosingType.getUnderlyingType().asElement().toString();
        String overriddenMeth = overridden.getElement().toString();
        String overriddenTyp = overriddenType.getUnderlyingType().asElement().toString();

        // Check the return value.
        if ((overrider.getReturnType().getKind() != TypeKind.VOID)
            && !checker.isSubtype(overrider.getReturnType(),
                overridden.getReturnType())) {
        	if (!fromLibrary && !checker.isDefaultReadonlyType(overrider.getReturnType())) {
	            checker.report(Result.failure("override.return.invalid",
	                    overriderMeth, overriderTyp, overriddenMeth, overriddenTyp,
	                    overrider.getReturnType().toString(),
	                    overridden.getReturnType().toString()),
	                    overriderTree.getReturnType());
	        	
	            // emit error message
	            result = false;
            } else {
	            checker.report(Result.warning("override.return.invalid",
	                    overriderMeth, overriderTyp, overriddenMeth, overriddenTyp,
	                    overrider.getReturnType().toString(),
	                    overridden.getReturnType().toString()),
	                    overriderTree.getReturnType());
        	}
        }

        // Check parameter values. (FIXME varargs)
        List<AnnotatedTypeMirror> overriderParams =
            overrider.getParameterTypes();
        List<AnnotatedTypeMirror> overriddenParams =
            overridden.getParameterTypes();
        for (int i = 0; i < overriderParams.size(); ++i) {
            if (!checker.isSubtype(overriddenParams.get(i), overriderParams.get(i))) {
	        	if (!fromLibrary && !checker.isDefaultReadonlyType(overriddenParams.get(i))) {
	                checker.report(Result.failure("override.param.invalid",
	                        overriderMeth, overriderTyp, overriddenMeth, overriddenTyp,
	                        overriderParams.get(i).toString(),
	                        overriddenParams.get(i).toString()
	                        ), overriderTree.getParameters().get(i));
	                // emit error message
	                result = false;
	        	} 
	        	else {
	                checker.report(Result.warning("override.param.invalid",
	                        overriderMeth, overriderTyp, overriddenMeth, overriddenTyp,
	                        overriderParams.get(i).toString(),
	                        overriddenParams.get(i).toString()
	                        ), overriderTree.getParameters().get(i));
	        	}
            }
        }

        // Check the receiver type.
        // isSubtype() requires its arguments to be actual subtypes with
        // respect to JLS, but overrider receiver is not a subtype of the
        // overridden receiver.  Hence copying the annotations
        AnnotatedTypeMirror overriddenReceiver =
            overrider.getReceiverType().getErased().getCopy(false);
        overriddenReceiver.addAnnotations(overridden.getReceiverType().getAnnotations());
        if (!checker.isSubtype(overriddenReceiver,
                overrider.getReceiverType().getErased())) {
        	if (!fromLibrary) {
	            checker.report(Result.failure("override.receiver.invalid",
	                    overriderMeth, overriderTyp, overriddenMeth, overriddenTyp,
	                    overrider.getReceiverType(),
	                    overridden.getReceiverType()),
	                    overriderTree);
	            result = false;
        	}
        	else {
	            checker.report(Result.warning("override.receiver.invalid",
	                    overriderMeth, overriderTyp, overriddenMeth, overriddenTyp,
	                    overrider.getReceiverType(),
	                    overridden.getReceiverType()),
	                    overriderTree);
        	}
        }
        return result;
	}
	
	

	@Override
	protected void checkArguments(List<? extends AnnotatedTypeMirror> requiredArgs,
			List<? extends ExpressionTree> passedArgs) {
        assert requiredArgs.size() == passedArgs.size();
        for (int i = 0; i < requiredArgs.size(); ++i) {
        	AnnotatedTypeMirror requiredType = requiredArgs.get(i);
        	ExpressionTree argTree = passedArgs.get(i);
        	// get the methodinvocationtree or newclasstree
        	Set<Tree.Kind> kinds = new HashSet<Tree.Kind>(2);
        	kinds.add(Tree.Kind.METHOD_INVOCATION);
        	kinds.add(Tree.Kind.NEW_CLASS);
        	Tree methodTree = TreeUtils.enclosingOfKind(getCurrentPath(), kinds);
        	
        	// get adapt context type
        	AnnotatedTypeMirror adaptContextType = getAdaptContextType(methodTree);
        	if (adaptContextType != null) {
				Set<AnnotationMirror> adaptedAnnos = checker.adaptMethodSet(
						adaptContextType.getAnnotations(), 
						requiredType.getAnnotations());
				if (!adaptedAnnos.isEmpty()) {
					requiredType.clearAnnotations();
					requiredType.addAnnotations(adaptedAnnos);
				}
        	}
        	
            commonAssignmentCheck(requiredType,
                    passedArgs.get(i),
                    "argument.type.incompatible");
        }
	}


	/**
	 * This code is ugly..
	 */
	@Override
	protected boolean checkMethodInvocability(AnnotatedExecutableType method,
			MethodInvocationTree node) {
        AnnotatedTypeMirror methodReceiver = method.getReceiverType()
        		.getCopy(true).getErased();
        AnnotatedTypeMirror treeReceiver = methodReceiver.getCopy(false);
        treeReceiver.addAnnotations(atypeFactory.getReceiverType(node).getAnnotations());
        
        AnnotatedTypeMirror adaptContextType = getAdaptContextType(node);
		if (adaptContextType != null) {
			Set<AnnotationMirror> adaptedAnnos = checker.adaptMethodSet(
					adaptContextType.getAnnotations(), 
					methodReceiver.getAnnotations());
			if (!adaptedAnnos.isEmpty()) {
				methodReceiver.clearAnnotations();
				methodReceiver.addAnnotations(adaptedAnnos);
			}
		}

        if (!checker.isSubtype(treeReceiver, methodReceiver)
        		&& !methodReceiver.hasAnnotation(checker.POLYREAD)) { // WEI: Ignored polyread
            checker.report(Result.failure("method.invocation.invalid",
                TreeUtils.elementFromUse(node),
                treeReceiver.toString(), methodReceiver.toString()), node);
            return false;
        }
        return true;
	}
	
	/**
	 * Check if expr is mutable
	 * @param expr
	 */
	protected void checkMutable(ExpressionTree expr) {
		while (expr.getKind() == Kind.PARENTHESIZED) {
			expr = TreeUtils.skipParens(expr);
			if (expr.getKind() == Kind.TYPE_CAST)
				expr = ((TypeCastTree) expr).getExpression();
		}
		AnnotatedTypeMirror receiverType = null;
        if (expr.getKind() == Tree.Kind.ARRAY_ACCESS) {
            ExpressionTree receiver = TreeUtils.skipParens(((ArrayAccessTree)expr).getExpression());
            receiverType = atypeFactory.getAnnotatedType(receiver);
        } else if (expr.getKind() == Tree.Kind.MEMBER_SELECT) {
            ExpressionTree receiver = ((MemberSelectTree)expr).getExpression();
            // Avoid int.class
            if (receiver instanceof PrimitiveTypeTree) {
                receiver =  null;
            }
            if (receiver != null)
	            receiverType = atypeFactory.getAnnotatedType(receiver);
        } else if (expr.getKind() == Kind.IDENTIFIER 
        		&& TreeUtils.elementFromUse(expr).getKind() == ElementKind.FIELD) {
        	// Field
        	MethodTree enclosingMethod = TreeUtils.enclosingMethod(getCurrentPath());
        	ExecutableElement methodElt = TreeUtils.elementFromDeclaration(enclosingMethod);
        	AnnotatedExecutableType methodType = atypeFactory.getAnnotatedType(methodElt);
        	receiverType = methodType.getReceiverType();
        }
        if (receiverType != null) {
        	Set<AnnotationMirror> annos = receiverType.getAnnotations();
        	if (annos.contains(checker.READONLY) || annos.contains(checker.POLYREAD)) {
	            checker.report(Result.failure("type.incompatible",
	                   "@Mutable" , receiverType), expr);
        	}
        }
	}
	
	
	@Override
	public Void visitNewClass(NewClassTree node, Void p) {
//		if (node.toString().contains("File(javaHome"))
//			System.out.println();
		return super.visitNewClass(node, p);
	}


	@Override
	public Void visitMethodInvocation(MethodInvocationTree node, Void p) {
//		if (node.toString().contains("java.lang.String.class.toString()"))
//			System.out.println();
		return super.visitMethodInvocation(node, p);
	}

	

	@Override
	public Void visitAssignment(AssignmentTree node, Void p) {
		checkMutable(node.getVariable());
		return super.visitAssignment(node, p);
	}


	@Override
	public Void visitVariable(VariableTree node, Void p) {
		return super.visitVariable(node, p);
	}



	@Override
	protected ReimTypeValidator createTypeValidator() {
        return new ReimTypeValidator();
    }

	/**
	 * The following class is copied from {@link BaseTypeVisitor} with one line
	 * commented
	 * @author huangw5
	 *
	 */
    protected class ReimTypeValidator extends BaseTypeVisitor<ReimChecker>.TypeValidator {
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
                atypeFactory.getAnnotatedType(
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
//                    type = (AnnotatedDeclaredType) atypeFactory.getAnnotatedType(typeargtree);
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

            List<AnnotatedTypeVariable> typevars = atypeFactory.typeVariablesFromUse(type, element);

            checkTypeArguments(tree, typevars, type.getTypeArguments(), tree.getTypeArguments());

            return null;
        }
    }
}
