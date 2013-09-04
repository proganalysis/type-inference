/**
 * 
 */
package checkers.inference.sflow;

import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;

import checkers.basetype.BaseTypeChecker;
import checkers.inference.InferenceAnnotatedTypeFactory;
import checkers.inference.InferenceChecker;
import checkers.inference.InferenceMain;
import checkers.inference.TypingExtractor;
import checkers.types.AnnotatedTypeFactory;
import checkers.types.AnnotatedTypeMirror;
import checkers.types.AnnotatedTypeMirror.AnnotatedDeclaredType;
import checkers.types.AnnotatedTypeMirror.AnnotatedExecutableType;
import checkers.types.AnnotatedTypeMirror.AnnotatedPrimitiveType;
import checkers.types.TreeAnnotator;
import checkers.types.TypeAnnotator;
import checkers.util.AnnotationUtils;
import checkers.util.ElementUtils;
import checkers.util.TreeUtils;

import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.InstanceOfTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.NewArrayTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.VariableTree;

/**
 * @author huangw5
 *
 */
public class SFlowAnnotatedTypeFactory extends
		InferenceAnnotatedTypeFactory {
	
	private SFlowChecker checker;
	
	public SFlowAnnotatedTypeFactory(SFlowChecker checker,
			CompilationUnitTree root) {
		super(checker, root);
		this.checker = checker;
	}
	
	
	private void annotateField(Element varElt, AnnotatedTypeMirror type) {
		if (checker.isChecking() || checker.isAnnotated(type)) {
			return;
		}
		// Field cannot be TAINTED. This can also be enforced 
		// in the InferenceVisitor by adding an Inequality 
		// constraint like arrays
		if (!ElementUtils.isStatic(varElt)
				&& !checker.isInferLibrary()
				) {
			type.addAnnotation(checker.SECRET);
			type.addAnnotation(checker.POLY);
		}
	}
	
	private void annotateMethod(ExecutableElement methodElt, 
			AnnotatedExecutableType methodType) {
		if (checker.isChecking() || methodType.isAnnotated()) {
			return;
		}
		methodType.addAnnotation(checker.BOTTOM);
		boolean fromLibrary = checker.isFromLibrary(methodElt);
		
		if (fromLibrary && checker.isPolyLibrary()) {
			// We assume all library has Poly type
			// FIXME: move it to SFlowChecker.fillAllPossibleAnnos
//			Set<AnnotationMirror> pSet = AnnotationUtils.createAnnotationSet();
//			pSet.add(checker.POLY);
//			annotateConstants(methodType, pSet);
			return;
		}
		
//		if (fromLibrary
////				|| methodElt.getModifiers().contains(Modifier.NATIVE)
//				){ 
//			// If it is from library, we make the worst-case assumption
//			// that all primitive parameters are tainted. But we don't make 
//			// make that assumption for those reference parameters 
//			// FIXME: We don't make the worst-case assumption here 
////			Set<AnnotationMirror> pSet = AnnotationUtils.createAnnotationSet();
////			pSet.add(checker.TAINTED);
////			annotateConstants(methodType, pSet);
//		}
		
//		else {
		
//		if (checker.isInferLibrary()) {
//			// If it is a non-static public method, we don't want the parameters to be SECRET 
//			if (//!ElementUtils.isStatic(methodElt) && 
//					!methodElt.getModifiers().contains(Modifier.PRIVATE)) {
//				AnnotatedDeclaredType receiverType = methodType.getReceiverType();
//				Set<AnnotationMirror> set = AnnotationUtils.createAnnotationSet();
//				set.add(checker.POLY);
//				set.add(checker.TAINTED); 
//				if (!checker.isAnnotated(receiverType))
//					receiverType.addAnnotations(set);
//				for (AnnotatedTypeMirror paramType : methodType.getParameterTypes()) {
//					if (!checker.isAnnotated(paramType)) 
//						annotateConstants(paramType, set);
//				}
//			}
//		}
		
		// The return reference type can be TAINTED 
		// Add default types for return
		// FIXME: The return value of public library methods should not be SECRET
		// FIXME: It can be? (Mar 5)
		AnnotatedTypeMirror returnType = methodType.getReturnType();
		if (checker.isInferLibrary()) {
			if (methodElt.getModifiers().contains(Modifier.PUBLIC) 
					&& returnType.getKind() != TypeKind.VOID 
					&& !checker.isAnnotated(returnType))  {
//				returnType.clearAnnotations();
//				Set<AnnotationMirror> set = AnnotationUtils.createAnnotationSet();
//				set.add(checker.POLY);
//				set.add(checker.TAINTED); 
//				annotateConstants(returnType, set);
			} 
		} else if (returnType.getKind() != TypeKind.VOID){
//			Set<AnnotationMirror> set = AnnotationUtils.createAnnotationSet();
//			set.add(checker.SECRET);
//			set.add(checker.POLY);
//			annotateConstants(returnType, set);
		}
		
		
		/*
		 *  FIXME: Can the parameter of a public static method be Secret? 
		 *  I think it should be disallowed due to a constraint I observed:
		 *  SUB-467018: ServletContextResource.java:73(580115):EXP_path{@Poly @Secret}  
		 *  <:  (ServletContextResource.java:73(580114):EXP_StringUtils.cleanPath(path){@Tainted} 
         *  			=m=> StringUtils.java:579(418091):VAR_path{@Poly @Secret})
         *  Here, the parameter VAR_path is forced to be Secret, which doesn't 
         *  make sense because there is no state for a static method. 
		 */
		// FIXME: added on Mar 28, 2013
		if (ElementUtils.isStatic(methodElt) && 
					!methodElt.getModifiers().contains(Modifier.PRIVATE)) {
			Set<AnnotationMirror> set = AnnotationUtils.createAnnotationSet();
			set.add(checker.POLY);
			set.add(checker.TAINTED); 
			for (AnnotatedTypeMirror paramType : methodType.getParameterTypes()) {
				if (!checker.isAnnotated(paramType)) 
					annotateConstants(paramType, set);
			}
		}
	}

	
	@Override
	public boolean isSupportedQualifier(AnnotationMirror a) {
		if (super.isSupportedQualifier(a)) 
			return true;
        Name name = AnnotationUtils.annotationName(a);
		if (name != null && qualHierarchy != null) {
			Set<Name> typeQualifiers = qualHierarchy.getTypeQualifiers();
			for (Name tn : typeQualifiers) {
				if (tn != null && name.toString().equals(tn.toString()))
					return true;
			}
		} 
		return false;
	}


	@Override
	public AnnotatedTypeMirror getAnnotatedType(Element elt) {
		AnnotatedTypeMirror type = super.getAnnotatedType(elt);
//		if (checker.isChecking()
//				&& !checker.isAnnotated(type)/*type.isAnnotated()*/
//				&& (elt.getKind() == ElementKind.FIELD
//						|| elt.getKind() == ElementKind.LOCAL_VARIABLE
//						|| elt.getKind() == ElementKind.METHOD
//						|| elt.getKind() == ElementKind.CONSTRUCTOR 
//						|| elt.getKind() == ElementKind.PARAMETER)) {
//			InferenceMain.getInstance().getCurrentExtractor().addInferredType(getIdentifier(elt), type);
//		}
		
		if (!checker.isChecking()) {
			// If it is doing inference, try to get ReIm type
			TypingExtractor currentExtractor = InferenceMain.getInstance().getCurrentExtractor();
			if (currentExtractor != null) {
				if (elt.getKind() != ElementKind.CLASS && elt.getKind() != ElementKind.INTERFACE) 
					currentExtractor.addInferredType(getIdentifier(elt), type);
			}
		}
		return type;
	}


	@Override
	public AnnotatedTypeMirror getAnnotatedType(Tree tree) {
		AnnotatedTypeMirror type = super.getAnnotatedType(tree);
		
		if (checker.isChecking()) {
			if (tree instanceof ExpressionTree) {
				tree = TreeUtils.skipParens((ExpressionTree) tree);
			}
			// Consider moving the following IDENTIFIER and VARIABLE 
			// to InferenceAnnotatedTypeFactory
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
						if (!set.isEmpty()) {
							type.clearAnnotations();
							type.addAnnotations(set);
						}
					} else {
						// This happen in the static initializer 
						ClassTree classTree = this.getVisitorState().getClassTree();
					}
				}
			} else if (tree.getKind() == Kind.VARIABLE) {
				// If there is an initialization for field, we need adapt it from 
				// ClassTree type
				Element elt = TreeUtils.elementFromDeclaration((VariableTree) tree);
				if (elt.getKind().isField() && !ElementUtils.isStatic(elt)) {
					ClassTree classTree = this.getVisitorState().getClassTree();
					TypeElement classElt = TreeUtils.elementFromDeclaration(classTree);
					AnnotatedDeclaredType defConstructorType = getAnnotatedType(classElt);
					InferenceMain.getInstance().getCurrentExtractor()
						.annotateInferredType(getIdentifier(classElt), defConstructorType);
					Set<AnnotationMirror> set = checker.adaptFieldSet(defConstructorType
							.getAnnotations(), type.getAnnotations());
					if (!set.isEmpty()) {
						type.clearAnnotations();
						type.addAnnotations(set);
					}
				}
			} else if (tree instanceof BinaryTree && !checker.isAnnotated(type)) {
				ExpressionTree left = ((BinaryTree)tree).getLeftOperand();
				ExpressionTree right = ((BinaryTree)tree).getRightOperand();
				AnnotatedTypeMirror leftType = getAnnotatedType(left);
				AnnotatedTypeMirror rightType = getAnnotatedType(right);
				Set<AnnotationMirror> leftSet = leftType.getAnnotations();
				Set<AnnotationMirror> rightSet = rightType.getAnnotations();
				Set<AnnotationMirror> set = qualHierarchy.leastUpperBound(leftSet, rightSet);
				type.addAnnotations(set);
			} else if (tree.getKind() == Kind.MEMBER_SELECT
					&& !checker.isAnnotated(type)) {
				MemberSelectTree mTree = (MemberSelectTree) tree;
	            Element fieldElt = TreeUtils.elementFromUse(mTree);
	            if (fieldElt.getSimpleName().contentEquals("class"))
	            	type.addAnnotation(checker.BOTTOM);
			}
		} else {
			if (tree instanceof ExpressionTree) {
				ExpressionTree t = TreeUtils.skipParens((ExpressionTree) tree);
				if (!checker.isChecking() && 
						(t.getKind() == Kind.METHOD_INVOCATION
							|| t.getKind() == Kind.CONDITIONAL_EXPRESSION 
							|| t.getKind() == Kind.ARRAY_ACCESS
							|| (t.getKind() == Kind.IDENTIFIER 
								&& TreeUtils.elementFromUse(t).getKind().isField()) // WEI: added on feb 23
							|| t.getKind() == Kind.MEMBER_SELECT)) {
					// If it is doing inference, we don't want any annotations of the 
					// evaluation
					// TODO: consider moving it to the super class
					type = type.getCopy(false);
				}
			}
			// If it is doing inference, try to get ReIm type
			TypingExtractor currentExtractor = InferenceMain.getInstance().getCurrentExtractor();
			if (currentExtractor != null) {
				Element elt = null;
				switch (tree.getKind()) {
				case VARIABLE:
					elt = TreeUtils.elementFromDeclaration((VariableTree) tree);
					break;
				case METHOD:
					elt = TreeUtils.elementFromDeclaration((MethodTree) tree);
					break;
				default:
					break;
				}
				if (elt != null)
					currentExtractor.addInferredType(getIdentifier(elt), type);
				else
					currentExtractor.addInferredType(getIdentifier(tree), type);
			}
		}
		
//		if (tree.getKind() == Kind.IDENTIFIER) {
//			Element elt = TreeUtils.elementFromUse((IdentifierTree) tree);
//			if (checker.isChecking() && elt.getKind() == ElementKind.FIELD) {
//				// We need to adapt it from PoV of THIS
//				MethodTree methodTree = this.getVisitorState().getMethodTree();
//				if (methodTree != null) {
//					ExecutableElement currentMethod = TreeUtils
//							.elementFromDeclaration(methodTree);
//					AnnotatedExecutableType methodType = getAnnotatedType(currentMethod);
//					Set<AnnotationMirror> set = checker.adaptFieldSet(methodType
//							.getReceiverType().getAnnotations(), type.getAnnotations());
//					type.clearAnnotations();
//					type.addAnnotations(set);
//				}
//			}
//		}
//		if (tree instanceof ExpressionTree) {
//			ExpressionTree t = TreeUtils.skipParens((ExpressionTree) tree);
//			if (!checker.isChecking() && 
//					(t.getKind() == Kind.METHOD_INVOCATION
//						|| t.getKind() == Kind.CONDITIONAL_EXPRESSION 
//						|| t.getKind() == Kind.MEMBER_SELECT)) {
//				// If it is doing inference, we don't want any annotations of the 
//				// evaluation
//				type = type.getCopy(false);
//			} else if (checker.isChecking() && !checker.isAnnotated(type)/*type.isAnnotated()*/) {
//				if (t instanceof BinaryTree) {
//					ExpressionTree left = ((BinaryTree)t).getLeftOperand();
//					ExpressionTree right = ((BinaryTree)t).getRightOperand();
//					AnnotatedTypeMirror leftType = getAnnotatedType(left);
//					AnnotatedTypeMirror rightType = getAnnotatedType(right);
//					Set<AnnotationMirror> leftSet = leftType.getAnnotations();
//					Set<AnnotationMirror> rightSet = rightType.getAnnotations();
//					Set<AnnotationMirror> set = null;
//					if (leftSet.size() == 1 && leftSet.contains(checker.BOTTOM))
//						set = rightSet;
//					else if (rightSet.size() == 1 && rightSet.contains(checker.BOTTOM))
//						set = leftSet;
//					else
//						set = InferenceUtils.intersectAnnotations(leftSet,
//								rightSet);
//					type.addAnnotations(set);
//				}
//			}
//		}
//		
//		if (!checker.isChecking()) {
//			// If it is doing inference, try to get ReIm type
//			TypingExtractor currentExtractor = InferenceMain.getInstance().getCurrentExtractor();
//			if (currentExtractor != null) {
//				Element elt = null;
//				switch (tree.getKind()) {
//				case VARIABLE:
//					elt = TreeUtils.elementFromDeclaration((VariableTree) tree);
//					break;
//				case METHOD:
//					elt = TreeUtils.elementFromDeclaration((MethodTree) tree);
//					break;
//				default:
//					break;
//				}
//				Set<AnnotationMirror> annos = AnnotationUtils.createAnnotationSet();
//				annos.addAll(type.getAnnotations());
//				if (elt != null)
//					currentExtractor.annotateInferredType(getIdentifier(elt), type);
//				else
//					currentExtractor.annotateInferredType(getIdentifier(tree), type);
//				type.addAnnotations(annos);
//			}
//		}
		
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
//            if (checker.isReadonlyType(type)) {
//            	type.addAnnotation(checker.READONLY);
//            }
//            TypeElement elt = (TypeElement)type.getUnderlyingType().asElement();
//            AnnotatedDeclaredType eltType = fromElement(elt);
//            if (TypesUtils.isBoxedPrimitive(type.getUnderlyingType())
//                    || elt.getQualifiedName().contentEquals("java.lang.String")) {
//            	// Boxed primitive and strings are readonly by default when 
//            	// their sources are not available
////            	type.clearAnnotations();
////            	type.addAnnotation(checker.READONLY);
//            } 
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
			VariableElement varElt = TreeUtils
					.elementFromDeclaration((VariableTree) node);
			if (varElt.getKind().isField() 
					&& !varElt.getSimpleName().contentEquals("this")) {
				// Field
				annotateField(varElt, p);
			}
			return super.visitVariable(node, p);
		}
		
		@Override
		public Void visitInstanceOf(InstanceOfTree node, AnnotatedTypeMirror p) {
//			p.addAnnotation(checker.READONLY);
			return super.visitInstanceOf(node, p);
		}

		@Override
		public Void visitLiteral(LiteralTree tree, AnnotatedTypeMirror type) {
			if (!checker.isAnnotated(type)/*type.isAnnotated()*/) {
				if (type.getKind() == TypeKind.NULL)
					type.addAnnotation(checker.BOTTOM);
				else {
					type.addAnnotation(checker.BOTTOM);
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
