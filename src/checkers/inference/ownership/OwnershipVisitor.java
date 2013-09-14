/**
 * 
 */
package checkers.inference.ownership;

import java.util.List;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.TypeElement;


import checkers.basetype.BaseTypeVisitor;
import checkers.source.Result;
import checkers.types.AnnotatedTypeMirror;
import checkers.types.AnnotatedTypeMirror.AnnotatedDeclaredType;
import checkers.types.AnnotatedTypeMirror.AnnotatedExecutableType;
import checkers.types.AnnotatedTypeMirror.AnnotatedTypeVariable;
import checkers.util.ElementUtils;
import checkers.util.Pair;
import checkers.util.TreeUtils;

import com.sun.source.tree.AnnotatedTypeTree;
import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.NewArrayTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.ParameterizedTypeTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;

/**
 * @author huangw5
 *
 */
public class OwnershipVisitor extends BaseTypeVisitor<OwnershipChecker> {

	public OwnershipVisitor(OwnershipChecker checker, CompilationUnitTree root) {
		super(checker, root);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected OwnershipTypeValidator createTypeValidator() {
        return new OwnershipTypeValidator();
    }
	
	
	@Override
    protected void checkArguments(List<? extends AnnotatedTypeMirror> requiredArgs,
            List<? extends ExpressionTree> passedArgs) {
        assert requiredArgs.size() == passedArgs.size();
        
        // look for the adapt context --- the receiver in this case
        TreePath currentPath = getCurrentPath();
        Tree leaf = null;
		do {
			leaf = currentPath.getLeaf();
			currentPath = currentPath.getParentPath();
		} while (leaf.getKind() != Kind.METHOD_INVOCATION
				&& leaf.getKind() != Kind.NEW_CLASS);
		
		AnnotatedTypeMirror rcvType  = null;
		if (leaf.getKind() == Kind.METHOD_INVOCATION) {
			MethodInvocationTree mTree = (MethodInvocationTree) leaf;
	        rcvType = atypeFactory.getReceiverType(mTree);
	        
			if (!ElementUtils.isStatic(TreeUtils.elementFromUse(mTree))
					&& rcvType == null) {
				System.out.println("DEBUGDEBUG");
			}
		} else {
			rcvType = atypeFactory.getAnnotatedType(leaf);
		}
		
        for (int i = 0; i < requiredArgs.size(); ++i) {
        	AnnotatedTypeMirror requiredType = requiredArgs.get(i);
        	ExpressionTree passedArgTree = passedArgs.get(i);
        	
        	if (rcvType != null) {
        		// If the method is static, rcvType can be null
				Set<AnnotationMirror> adaptedAnnos = checker.adaptMethodSet(
						rcvType.getAnnotations(), requiredType.getAnnotations());
				if (!adaptedAnnos.isEmpty()) {
					requiredType.clearAnnotations();
					requiredType.addAnnotations(adaptedAnnos);
				}
        	}

			commonAssignmentCheck(requiredType, passedArgTree,
					"argument.type.incompatible");
        }
    }
	
	@Override
    protected boolean checkConstructorInvocation(AnnotatedDeclaredType dt,
            AnnotatedExecutableType constructor, Tree src) {
        AnnotatedDeclaredType receiver = constructor.getReceiverType();
        
		Set<AnnotationMirror> adaptedAnnos = checker.adaptMethodSet(
				dt.getAnnotations(), constructor.getReceiverType().getAnnotations());
		if (!adaptedAnnos.isEmpty()) {
			constructor.getReceiverType().clearAnnotations();
			constructor.getReceiverType().addAnnotations(adaptedAnnos);			
		}
        boolean b = checker.isSubtype(dt, receiver) || checker.isSubtype(receiver, dt);

        if (!b) {
            checker.report(Result.failure("constructor.invocation.invalid",
                    constructor.toString(), dt, receiver), src);
        }
        return b;
    }
	
	@Override
	protected boolean checkMethodInvocability(AnnotatedExecutableType method,
			MethodInvocationTree node) {
        AnnotatedTypeMirror methodReceiver = method.getReceiverType().getErased();
        AnnotatedTypeMirror treeReceiver = methodReceiver.getCopy(false);
        AnnotatedTypeMirror rcv = atypeFactory.getReceiverType(node);
        treeReceiver.addAnnotations(rcv.getEffectiveAnnotations());
        
		Set<AnnotationMirror> adaptedAnnos = checker.adaptMethodSet(
				treeReceiver.getAnnotations(), 
				methodReceiver.getAnnotations());
		if (!adaptedAnnos.isEmpty()) {
			methodReceiver.clearAnnotations();
			methodReceiver.addAnnotations(adaptedAnnos);
		}
        
        if (!checker.isSubtype(treeReceiver, methodReceiver)) {
            checker.report(Result.failure("method.invocation.invalid",
                TreeUtils.elementFromUse(node),
                treeReceiver.toString(), methodReceiver.toString()), node);
            return false;
        }
        return true;	
	}

	

	@Override
	protected void checkArrayInitialization(AnnotatedTypeMirror type,
			List<? extends ExpressionTree> initializers) {
		// this type is the component type. 
		// we need the adapted type
		// First look for new array tree
		TreePath currentPath = getCurrentPath();
		Tree leaf = null;
		do {
			leaf = currentPath.getLeaf();
			currentPath = currentPath.getParentPath();
		} while (currentPath != null && leaf.getKind() != Kind.NEW_ARRAY);
		
		if (leaf != null && currentPath != null) {
			AnnotatedTypeMirror arrayType = atypeFactory.getAnnotatedType(leaf);
			Set<AnnotationMirror> set = checker.adaptFieldSet(
					arrayType.getAnnotations(), type.getAnnotations());
			if (!set.isEmpty()) {
				type.clearAnnotations();
				type.addAnnotations(set);
			}
		}
        for (ExpressionTree init : initializers)
            commonAssignmentCheck(type, init, "type.incompatible");
	}

	@Override
	public Void visitAssignment(AssignmentTree node, Void p) {
//		if (node.toString().contains("main.prop"))
//			System.out.println();
		return super.visitAssignment(node, p);
	}



	@Override
	public Void visitVariable(VariableTree node, Void p) {
		return super.visitVariable(node, p);
	}



	@Override
	public Void visitNewArray(NewArrayTree node, Void p) {
//		if (node.toString().contains("Company.class,"))
//			System.out.println();
		return super.visitNewArray(node, p);
	}



	/**
	 * The following class is copied from {@link BaseTypeVisitor} with one line
	 * commented
	 * @author huangw5
	 *
	 */
    protected class OwnershipTypeValidator extends BaseTypeVisitor<OwnershipChecker>.TypeValidator {
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
                System.err.printf("TypeValidator.visitDeclared unhandled tree: %s of kind %s\n", tree, tree.getKind());
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
