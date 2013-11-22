/**
 * 
 */
package checkers.inference.confidentiality;

import java.util.List;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;

import checkers.basetype.BaseTypeChecker;
import checkers.inference.InferenceAnnotatedTypeFactory;
import checkers.inference.InferenceChecker;
import checkers.inference.InferenceUtils;
import checkers.types.AnnotatedTypeFactory;
import checkers.types.AnnotatedTypeMirror;
import checkers.types.AnnotatedTypeMirror.AnnotatedArrayType;
import checkers.types.AnnotatedTypeMirror.AnnotatedDeclaredType;
import checkers.types.AnnotatedTypeMirror.AnnotatedExecutableType;
import checkers.types.AnnotatedTypeMirror.AnnotatedPrimitiveType;
import checkers.types.TreeAnnotator;
import checkers.types.TypeAnnotator;
import checkers.util.AnnotationUtils;
import checkers.util.ElementUtils;
import checkers.util.TreeUtils;
import checkers.util.TypesUtils;

import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.InstanceOfTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.NewArrayTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.VariableTree;

/**
 * @author huangw5
 *
 */
public class ConfidentialityAnnotatedTypeFactory extends
		InferenceAnnotatedTypeFactory {
	
	private ConfidentialityChecker checker;

	public ConfidentialityAnnotatedTypeFactory(ConfidentialityChecker checker,
			CompilationUnitTree root) {
		super(checker, root);
		this.checker = checker;
	}
	
	
//	private void annotateField(AnnotatedTypeMirror type) {
//		if (checker.isChecking() || type.isAnnotated()) {
//			return;
//		}
//		// Field cannot be TAINTED. This can also be enforced 
//		// in the InferenceVisitor by adding an Inequality 
//		// constraint like arrays
//		if (checker.isPrimitiveType(type)) {
//			type.addAnnotation(checker.PPOLY);
//			type.addAnnotation(checker.PSECRET);
//		} else {
//			type.addAnnotation(checker.RPOLY);
//			type.addAnnotation(checker.RSECRET);
//		}
//	}
	
	private void annotateMethod(ExecutableElement methodElt, 
			AnnotatedExecutableType methodType) {
		if (checker.isChecking() || methodType.isAnnotated()) {
			return;
		}
		boolean fromLibrary = checker.isFromLibrary(methodElt);
//		String classStr = methodElt.getEnclosingElement().toString();
//		String clazzStr = methodElt.getEnclosingElement().toString();
//		if (methodElt.getModifiers().contains(Modifier.NATIVE)
//				&& !clazzStr.equals("java.lang.String")
//				) {
//			// For native method, we make the worst-case asusmption
//			Set<AnnotationMirror> pSet = AnnotationUtils.createAnnotationSet();
//			pSet.add(checker.PTAINTED);
//			Set<AnnotationMirror> rSet = AnnotationUtils.createAnnotationSet();
//			rSet.add(checker.RTAINTED);
//			annotateConstants(methodType, pSet, rSet);
//		} else 
		if (fromLibrary
//				|| methodElt.getModifiers().contains(Modifier.NATIVE)
				){ 
			// If it is from library, we make the worst-case assumption
			// that all primitive parameters are tainted. But we don't make 
			// make that assumption for those reference parameters 
			Set<AnnotationMirror> pSet = AnnotationUtils.createAnnotationSet();
			pSet.add(checker.PTAINTED);
			Set<AnnotationMirror> rSet = AnnotationUtils.createAnnotationSet();
			rSet.add(checker.RSECRET);
			rSet.add(checker.RPOLY);
			rSet.add(checker.RTAINTED);
			annotateConstants(methodType, pSet, rSet);
		} 
		
		// The return reference type can be TAINTED 
		// Add default types for return
		// FIXME: The return value of public library methods should not be SECRET
		AnnotatedTypeMirror returnType = methodType.getReturnType();
		if (returnType.getKind() != TypeKind.VOID)  {
			returnType.clearAnnotations();
			Set<AnnotationMirror> set = AnnotationUtils.createAnnotationSet();
			if (checker.isPrimitiveType(returnType)) {
				set.add(checker.PPOLY);
				set.add(checker.PSECRET);
				set.add(checker.PTAINTED); // WEI added on Dec 2, 2012
			} else {
				set.add(checker.RTAINTED);
				set.add(checker.RPOLY);
				set.add(checker.RSECRET);
			}
			returnType.addAnnotations(set);
		}
		methodType.addAnnotation(checker.BOTTOM);
	}
	
	public void annotateConstants(AnnotatedTypeMirror type, 
			Set<AnnotationMirror> pAnnos, Set<AnnotationMirror> rAnnos) {
		// Add recursively
		if (type.getKind() == TypeKind.ARRAY) {
			annotateConstants(((AnnotatedArrayType) type).getComponentType(), 
					pAnnos, rAnnos);
			// If the component is @PTainted/@RTainted, then we need to annotate 
			// the array itself as @RTainted
			AnnotatedTypeMirror componentType = ((AnnotatedArrayType) type)
					.getComponentType();
			Set<AnnotationMirror> annos = componentType.getAnnotations();
			if (annos.size() == 1 && (annos.contains(checker.PTAINTED)
					|| annos.contains(checker.RTAINTED))) {
				if (checker.isPrimitiveType(componentType))
					componentType.addAnnotation(checker.PPOLY);
				else
					componentType.addAnnotation(checker.RPOLY);
				rAnnos = AnnotationUtils.createAnnotationSet();
				rAnnos.add(checker.RTAINTED);
			}
		} else if (type.getKind() == TypeKind.DECLARED) {
			List<AnnotatedTypeMirror> typeArgs = ((AnnotatedDeclaredType) type)
					.getTypeArguments();
			for (AnnotatedTypeMirror t : typeArgs) {
				annotateConstants(t, pAnnos, rAnnos);
			}
		} else if (type.getKind() == TypeKind.EXECUTABLE){
			AnnotatedExecutableType methodType = (AnnotatedExecutableType) type;
			// Receiver
			annotateConstants(methodType.getReceiverType(), pAnnos, rAnnos);
			// Return
			AnnotatedTypeMirror returnType = methodType.getReturnType();
			annotateConstants(returnType, pAnnos, rAnnos);
			for (AnnotatedTypeMirror t : methodType.getParameterTypes()) {
				annotateConstants(t, pAnnos, rAnnos);
			}
		}
		
		if (type.isAnnotated() || type.getKind() == TypeKind.VOID
				/*|| type.getKind() == TypeKind.NULL*/)
			return;
		// Add annotations
		if (checker.isPrimitiveType(type))
			type.addAnnotations(pAnnos);
		else
			type.addAnnotations(rAnnos);
	}
	
	
	
	@Override
	public AnnotatedTypeMirror getAnnotatedType(Element elt) {
		AnnotatedTypeMirror type = super.getAnnotatedType(elt);
		if (checker.isChecking() && elt.getKind() == ElementKind.FIELD) {
			// We need to adapt it from PoV of THIS
//			MethodTree methodTree = this.getVisitorState().getMethodTree();
//			if (methodTree != null) {
//				ExecutableElement currentMethod = TreeUtils
//						.elementFromDeclaration(methodTree);
//				AnnotatedExecutableType methodType = getAnnotatedType(currentMethod);
//				Set<AnnotationMirror> set = checker.adaptFieldSet(methodType
//						.getReceiverType().getAnnotations(), type.getAnnotations());
//				type.clearAnnotations();
//				type.addAnnotations(set);
//			}
		}
		return type;
	}


	@Override
	public AnnotatedTypeMirror getAnnotatedType(Tree tree) {
		AnnotatedTypeMirror type = super.getAnnotatedType(tree);
		if (tree.getKind() == Kind.IDENTIFIER) {
			Element elt = TreeUtils.elementFromUse((IdentifierTree) tree);
			if (checker.isChecking() && elt.getKind() == ElementKind.FIELD) {
				// We need to adapt it from PoV of THIS
				MethodTree methodTree = this.getVisitorState().getMethodTree();
				if (methodTree != null) {
					ExecutableElement currentMethod = TreeUtils
							.elementFromDeclaration(methodTree);
					AnnotatedExecutableType methodType = getAnnotatedType(currentMethod);
					Set<AnnotationMirror> set = checker.adaptFieldSet(methodType
							.getReceiverType().getAnnotations(), type.getAnnotations());
					type.clearAnnotations();
					type.addAnnotations(set);
				}
			}
		}
		if (tree instanceof ExpressionTree) {
			ExpressionTree t = TreeUtils.skipParens((ExpressionTree) tree);
			if (!checker.isChecking() && 
					(t.getKind() == Kind.METHOD_INVOCATION
						|| t.getKind() == Kind.CONDITIONAL_EXPRESSION 
						|| t.getKind() == Kind.MEMBER_SELECT)) {
				// If it is doing inference, we don't want any annotations of the 
				// evaluation
				type = type.getCopy(false);
			} else if (checker.isChecking() && !type.isAnnotated()) {
				if (t instanceof BinaryTree) {
					ExpressionTree left = ((BinaryTree)t).getLeftOperand();
					ExpressionTree right = ((BinaryTree)t).getRightOperand();
					AnnotatedTypeMirror leftType = getAnnotatedType(left);
					AnnotatedTypeMirror rightType = getAnnotatedType(right);
					Set<AnnotationMirror> leftSet = leftType.getAnnotations();
					Set<AnnotationMirror> rightSet = rightType.getAnnotations();
					Set<AnnotationMirror> set = null;
					if (leftSet.size() == 1 && leftSet.contains(checker.BOTTOM))
						set = rightSet;
					else if (rightSet.size() == 1 && rightSet.contains(checker.BOTTOM))
						set = leftSet;
					else
						set = InferenceUtils.intersectAnnotations(leftSet,
								rightSet);
					type.addAnnotations(set);
				}
			}
		}
		return type;
	}


	@Override
	protected void annotateImplicit(Tree tree, AnnotatedTypeMirror type) {
		super.annotateImplicit(tree, type);
		treeAnnotator.visit(tree, type); 
		typeAnnotator.visit(type);
	}

	@Override
	protected void annotateImplicit(Element elt, AnnotatedTypeMirror type) {
		super.annotateImplicit(elt, type);
		typeAnnotator.visit(type);
	}

	@Override
	protected TypeAnnotator createTypeAnnotator(InferenceChecker checker) {
		return new ConfidentialityTypeAnnotator(checker);
	}


	@Override
	protected TreeAnnotator createTreeAnnotator(InferenceChecker checker) {
		return new ConfidentialityTreeAnnotator(checker, this);
	}
	
	/**
	 * For adding default annotations
	 * @author huangw5
	 *
	 */
	private class ConfidentialityTypeAnnotator extends TypeAnnotator {

		public ConfidentialityTypeAnnotator(BaseTypeChecker checker) {
			super(checker);
		}

		@Override
		public Void visitExecutable(AnnotatedExecutableType t, ElementKind p) {
			// The following lines are from super class, except adding the 
			// traverse of the receiver
	        scan(t.getReceiverType(), p);
	        scan(t.getReturnType(), p);
	        scanAndReduce(t.getParameterTypes(), p, null);
	        scanAndReduce(t.getThrownTypes(), p, null);
	        scanAndReduce(t.getTypeVariables(), p, null);
	        
			ExecutableElement elt = t.getElement();
			annotateMethod(elt, t);
			return null;
		}
		
		

		@Override
		public Void visitPrimitive(AnnotatedPrimitiveType type, ElementKind p) {
//			type.addAnnotation(checker.BOTTOM);
			return super.visitPrimitive(type, p);
		}

		@Override
		public Void visitDeclared(AnnotatedDeclaredType type, ElementKind p) {
            TypeElement elt = (TypeElement)type.getUnderlyingType().asElement();
            AnnotatedDeclaredType eltType = fromElement(elt);
            if (TypesUtils.isBoxedPrimitive(type.getUnderlyingType())
                    || elt.getQualifiedName().contentEquals("java.lang.String")) {
            	// Boxed primitive and strings are readonly by default when 
            	// their sources are not available
//            	type.clearAnnotations();
//            	type.addAnnotation(checker.READONLY);
            } 
			return super.visitDeclared(type, p);
		}
		
	}
	
	/**
	 * For adding default annotations
	 * @author huangw5
	 *
	 */
	private class ConfidentialityTreeAnnotator extends TreeAnnotator {

		public ConfidentialityTreeAnnotator(BaseTypeChecker checker, 
				AnnotatedTypeFactory typeFactory) {
			super(checker, typeFactory);
		}

		@Override
		public Void visitVariable(VariableTree node, AnnotatedTypeMirror p) {
			typeAnnotator.visit(p);
//			VariableElement varElt = TreeUtils
//					.elementFromDeclaration((VariableTree) node);
//			if (varElt.getKind().isField() 
//					&& !p.isAnnotated()
//					&& !varElt.getSimpleName().contentEquals("this")) {
//				// Field
//				annotateField(p);
//			}
			return super.visitVariable(node, p);
		}
		
		@Override
		public Void visitInstanceOf(InstanceOfTree node, AnnotatedTypeMirror p) {
//			p.addAnnotation(checker.READONLY);
			return super.visitInstanceOf(node, p);
		}

		@Override
		public Void visitLiteral(LiteralTree tree, AnnotatedTypeMirror type) {
			if (!type.isAnnotated()) {
				if (type.getKind() == TypeKind.NULL)
					type.addAnnotation(checker.BOTTOM);
				else {
//					type.addAnnotation(checker.READONLY);
				type.addAnnotation(checker.BOTTOM);
//				type.addAnnotation(checker.PSECRET);
//				type.addAnnotation(checker.PPOLY);
//				type.addAnnotation(checker.PTAINTED);
				}
			}
			return super.visitLiteral(tree, type);
		}

		@Override
		public Void visitMethod(MethodTree node, AnnotatedTypeMirror p) {
			return super.visitMethod(node, p);
		}

		@Override
		public Void visitNewArray(NewArrayTree tree, AnnotatedTypeMirror type) {
			// Skip
			return null;
		}
		
	}
	
}
