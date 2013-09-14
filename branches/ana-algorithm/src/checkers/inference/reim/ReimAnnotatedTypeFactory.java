/**
 * 
 */
package checkers.inference.reim;

import java.util.List;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
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
import checkers.util.InternalUtils;
import checkers.util.TreeUtils;
import checkers.util.TypesUtils;

import com.sun.source.tree.ArrayAccessTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionTree;
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
public class ReimAnnotatedTypeFactory extends InferenceAnnotatedTypeFactory {
	
	private ReimChecker checker;
	
	public ReimAnnotatedTypeFactory(ReimChecker checker,
			CompilationUnitTree root) {
		super(checker, root);
		this.checker = checker;
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
	public AnnotatedTypeMirror getAnnotatedType(Tree tree) {
		AnnotatedTypeMirror type = super.getAnnotatedType(tree);
		if (tree instanceof ExpressionTree)
			tree = TreeUtils.skipParens((ExpressionTree) tree);
		if (checker.isChecking()) {
			switch (tree.getKind()) {
			// For array access, we need to do the adapt
//			case ARRAY_ACCESS:
//				ArrayAccessTree aTree = (ArrayAccessTree) tree;
//				ExpressionTree aExpr = aTree.getExpression();
//				AnnotatedTypeMirror aExprType = getAnnotatedType(aExpr);
//				assert aExprType.getKind() == TypeKind.ARRAY;
//				Set<AnnotationMirror> componentAnnos = ((AnnotatedArrayType) aExprType)
//						.getComponentType().getAnnotations();
//				Set<AnnotationMirror> adaptedAnnos = checker.adaptFieldSet(
//						aExprType.getAnnotations(), componentAnnos);
//				if (!adaptedAnnos.isEmpty()) {
//					type.clearAnnotations();
//					type.addAnnotations(adaptedAnnos);
//				}
//				break;
			}
		} else {
			// If it is doing inference
			// TODO: We don't want any annotations. E.g. we don't want the 
			// result of a method invocation 
			// TODO: We add all annotations?
			if ((tree.getKind() == Kind.METHOD_INVOCATION
					|| tree.getKind() == Kind.CONDITIONAL_EXPRESSION 
					|| tree.getKind() == Kind.MEMBER_SELECT)
					&& !checker.isDefaultReadonlyType(type)) {
//				type = InferenceUtils.getDeepCopy(type, false);
				type = type.getCopy(false); 
//				type.addAnnotation(checker.READONLY);
//				type.addAnnotation(checker.POLYREAD);
//				type.addAnnotation(checker.MUTABLE);
			}
		}
		return type;
	}
	
	/**
	 * Add default annotations for fields. 
	 * Field are annotated as readonly and mutable, except for special fields
	 * in iterator or enumerator.
	 * @param type
	 */
	private void annotateField(AnnotatedTypeMirror type) {
		if (checker.isChecking() || type.isAnnotated()) {
			return;
		}
		type.addAnnotation(checker.MUTABLE);
		type.addAnnotation(checker.READONLY);
		
		// FIXME: Special handling for the field in java.util.Enumeration
		ClassTree classTree = getVisitorState().getClassTree();
		List<? extends Tree> implementsClause = classTree.getImplementsClause();
		for (Tree implementClause : implementsClause) { 
			Element symbol = InternalUtils.symbol(implementClause);
			AnnotatedTypeMirror t = getAnnotatedType(symbol);
			if (t.getErased().getUnderlyingType().toString()
					.equals("java.util.Enumeration")) {
				type.addAnnotation(checker.POLYREAD);
			}
		}
	}
	
	/**
	 * Add annotations for methods. Notice that the annotations of the method
	 * itself (other than its receiver, return or parameters) are used for 
	 * mutateStatics. 
	 * @param methodElt
	 * @param methodType
	 */
	private void annotateMethod(ExecutableElement methodElt, 
			AnnotatedExecutableType methodType) {
		if (checker.isChecking() || methodType.isAnnotated()) {
			return;
		}
		// If it is from library
		if (checker.isFromLibrary(methodElt)){
			// Check if it is pure library method
			if (checker.isPureLibraryMethod(methodElt)) {
				Set<AnnotationMirror> set = AnnotationUtils.createAnnotationSet();
				set.add(checker.READONLY);
				set.add(checker.POLYREAD);
				set.add(checker.MUTABLE);
				annotateConstants(methodType, set);
			} else {
				Set<AnnotationMirror> set = AnnotationUtils.createAnnotationSet();
				set.add(checker.MUTABLE);
				//FIXME: WEI: Don't use annotated jdk for fse demo
				annotateConstants(methodType, set);
				// TODO: We assume the method doesn't mutate statics
				methodType.addAnnotation(checker.getLibraryMutateStatic(methodElt));
			}
		} else {
			// TODO: Special case for methods in java.lang.String;
			// If we enforce the "this" in these methods to be Readonly, 
			// some statements cannot type-check
			// TODO: WEI: I think it is better to have manual type case on String.java
//			String classStr = methodElt.getEnclosingElement().toString();
//			if (classStr != null && classStr.equals("java.lang.String")) {
//				methodType.getReceiverType().addAnnotation(checker.MUTABLE);
//			}
		}
		
		// The return type cannot be MUTABLE
		// Add default types for return
		AnnotatedTypeMirror returnType = methodType.getReturnType();
		if (returnType.getKind() != TypeKind.VOID
				&& !checker.isDefaultReadonlyType(returnType))  {
			returnType.clearAnnotations();
			Set<AnnotationMirror> set = AnnotationUtils.createAnnotationSet();
			set.add(checker.READONLY);
			set.add(checker.POLYREAD);
			returnType.addAnnotations(set);
		}
	}
	

	@Override
	protected TypeAnnotator createTypeAnnotator(InferenceChecker checker) {
		return new ReimTypeAnnotator(checker);
	}


	@Override
	protected TreeAnnotator createTreeAnnotator(InferenceChecker checker) {
		return new ReimTreeAnnotator(checker, this);
	}
	
	/**
	 * For adding default annotations
	 * @author huangw5
	 *
	 */
	private class ReimTypeAnnotator extends TypeAnnotator {

		public ReimTypeAnnotator(BaseTypeChecker checker) {
			super(checker);
			// TODO Auto-generated constructor stub
		}

		@Override
		public Void visitExecutable(AnnotatedExecutableType t, ElementKind p) {
			// The fllowing lines are from super class, except adding the 
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
			type.addAnnotation(checker.READONLY);
			return super.visitPrimitive(type, p);
		}

		@Override
		public Void visitDeclared(AnnotatedDeclaredType type, ElementKind p) {
            TypeElement elt = (TypeElement)type.getUnderlyingType().asElement();
//            AnnotatedDeclaredType eltType = fromElement(elt);
//            if (TypesUtils.isBoxedPrimitive(type.getUnderlyingType())
//                    || elt.getQualifiedName().contentEquals("java.lang.String")) {
            if (checker.isDefaultReadonlyType(type)) {
            	// Boxed primitive and strings are readonly by default when 
            	// their sources are not available
//            	if (checker.isFromLibrary(elt)) {
            	if (!type.isAnnotated()) {
//            	type.clearAnnotations();
            	type.addAnnotation(checker.READONLY);
            	}
//            	}
	            	
            	// Special case when checking String.java
            } 
			return super.visitDeclared(type, p);
		}
		
	}
	
	/**
	 * For adding default annotations
	 * @author huangw5
	 *
	 */
	private class ReimTreeAnnotator extends TreeAnnotator {

		public ReimTreeAnnotator(BaseTypeChecker checker, 
				AnnotatedTypeFactory typeFactory) {
			super(checker, typeFactory);
		}

		@Override
		public Void visitVariable(VariableTree node, AnnotatedTypeMirror p) {
			typeAnnotator.visit(p);
			VariableElement varElt = TreeUtils
					.elementFromDeclaration((VariableTree) node);
			if (varElt.getKind().isField() 
					&& !p.isAnnotated()
					&& !varElt.getSimpleName().contentEquals("this")) {
				// Field
				annotateField(p);
			}
			return super.visitVariable(node, p);
		}
		
		@Override
		public Void visitInstanceOf(InstanceOfTree node, AnnotatedTypeMirror p) {
			p.addAnnotation(checker.READONLY);
			return super.visitInstanceOf(node, p);
		}

		@Override
		public Void visitLiteral(LiteralTree tree, AnnotatedTypeMirror type) {
			if (!type.isAnnotated()) {
				if (type.getKind() == TypeKind.NULL)
					type.addAnnotation(checker.MUTABLE);
				else
					type.addAnnotation(checker.READONLY);
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
