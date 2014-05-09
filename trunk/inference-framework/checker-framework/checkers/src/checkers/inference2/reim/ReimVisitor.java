/**
 * 
 */
package checkers.inference2.reim;

import java.util.List;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import checkers.inference2.InferenceTypeVisitor;
import checkers.source.Result;
import checkers.types.AnnotatedTypeMirror;
import checkers.types.AnnotatedTypeMirror.AnnotatedArrayType;
import checkers.types.AnnotatedTypeMirror.AnnotatedExecutableType;
import checkers.util.TreeUtils;

import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ConditionalExpressionTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.NewArrayTree;
import com.sun.source.tree.NewClassTree;
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
public class ReimVisitor extends InferenceTypeVisitor<ReimChecker> {
	
	private ReimChecker checker;

	public ReimVisitor(ReimChecker checker, CompilationUnitTree root) {
		super(checker, root);
		this.checker = checker;
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
        TreePath path = getCurrentPath();
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
				if (expr.equals(node)) {
					adaptContextType = atypeFactory.getAnnotatedType(var);
				}
			} else if (leaf.getKind() == Kind.VARIABLE) {
				ExpressionTree initializer = ((VariableTree) leaf).getInitializer();
				if (initializer != null) {
					// Same as above 
					initializer = TreeUtils.skipParens(initializer);
					while (initializer.getKind() == Kind.TYPE_CAST) {
						initializer = ((TypeCastTree) initializer).getExpression();
						initializer = TreeUtils.skipParens(initializer);
					}
					if (initializer.equals(node)) {
						adaptContextType = atypeFactory.getAnnotatedType(leaf);
					}
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
				if (trueExpr.equals(node)) {
					adaptContextType = atypeFactory.getAnnotatedType(cTree);
				} else {
					ExpressionTree falseExpr = cTree.getFalseExpression();
					falseExpr = TreeUtils.skipParens(falseExpr);
					while (falseExpr.getKind() == Kind.TYPE_CAST) {
						falseExpr = ((TypeCastTree) falseExpr).getExpression();
						falseExpr = TreeUtils.skipParens(falseExpr);
					}
					if (falseExpr.equals(node)) {
						adaptContextType = atypeFactory.getAnnotatedType(cTree);
					}
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
	
	
	@Override
	protected void commonAssignmentCheck(AnnotatedTypeMirror varType,
            ExpressionTree valueExp, String errorKey) {
        if (shouldSkipUses(valueExp))
            return;
        if (varType.getKind() == TypeKind.ARRAY
                && valueExp instanceof NewArrayTree
                && ((NewArrayTree)valueExp).getType() == null) {
            AnnotatedTypeMirror compType = ((AnnotatedArrayType)varType).getComponentType();
            NewArrayTree arrayTree = (NewArrayTree)valueExp;
            assert arrayTree.getInitializers() != null;
            checkArrayInitialization(compType, arrayTree.getInitializers());
        }
        AnnotatedTypeMirror valueType = atypeFactory.getAnnotatedType(valueExp);
        assert valueType != null;
        
        
		// get adapt context type
		AnnotatedTypeMirror adaptContextType = getAdaptContextType(valueExp);
		if (adaptContextType != null) {
			Set<AnnotationMirror> adaptedAnnos = checker.adaptMethodSet(
					adaptContextType.getAnnotations(),
					valueType.getAnnotations());
			if (!adaptedAnnos.isEmpty()) {
				valueType.clearAnnotations();
				valueType.addAnnotations(adaptedAnnos);
			}
		}
        
        commonAssignmentCheck(varType, valueType, valueExp, errorKey);
    }

	@Override
	public Void visitTypeCast(TypeCastTree node, Void p) {
		return super.visitTypeCast(node, p);
	}
	
	
}
