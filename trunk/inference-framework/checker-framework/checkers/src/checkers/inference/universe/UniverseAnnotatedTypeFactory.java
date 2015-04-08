/**
 * 
 */
package checkers.inference.universe;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;

import checkers.basetype.BaseTypeChecker;
import checkers.inference.InferenceAnnotatedTypeFactory;
import checkers.inference.InferenceChecker;
import checkers.types.AnnotatedTypeFactory;
import checkers.types.AnnotatedTypeMirror;
import checkers.types.AnnotatedTypeMirror.AnnotatedDeclaredType;
import checkers.types.AnnotatedTypeMirror.AnnotatedExecutableType;
import checkers.types.AnnotatedTypeMirror.AnnotatedPrimitiveType;
import checkers.types.TreeAnnotator;
import checkers.types.TypeAnnotator;
import checkers.util.AnnotationUtils;
import checkers.util.ElementUtils;
import checkers.util.InternalUtils;
import checkers.util.TreeUtils;

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
import com.sun.source.tree.TypeCastTree;
import com.sun.source.tree.VariableTree;

/**
 * @author huangw5
 *
 */
public class UniverseAnnotatedTypeFactory extends InferenceAnnotatedTypeFactory {
	
	private UniverseChecker checker;

	public UniverseAnnotatedTypeFactory(UniverseChecker checker,
			CompilationUnitTree root) {
		super(checker, root);
		this.checker = checker;
	}
	
	
	private void annotateThis(AnnotatedTypeMirror type) {
		if (checker.isChecking())
			return;
		type.clearAnnotations();
		type.addAnnotation(checker.SELF);
	}
	

	private void annotateMethod(ExecutableElement methodElt,
			AnnotatedExecutableType methodType) {
		if (checker.isChecking() || methodType.isAnnotated()) {
			return;
		}
		// First annotate the receiver 
		annotateThis(methodType.getReceiverType());
		
		// If it is from library
		if (checker.isFromLibrary(methodElt)) {
			Set<AnnotationMirror> set = AnnotationUtils.createAnnotationSet();
			set.add(checker.PEER);
			annotateConstants(methodType, set);
		}
		// methodType.isAnnotated() only checks the annotations on methodType,
		// but not the type of its receiver, parameters or return;
		if (!methodType.isAnnotated())
			methodType.addAnnotation(checker.BOTTOM);
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
		Element elt = InternalUtils.symbol(tree);
		if (!checker.isChecking() /*&& !checker.isDefaultAnyType(type)*/
				&& type.isAnnotated()
				&& !type.getKind().isPrimitive()
				&& type.getKind() != TypeKind.NULL
				&& (elt == null || !ElementUtils.isStatic(elt))
				) {
			// We don't want annotations on the following trees
			Kind kind = tree.getKind();
			if (kind ==  Kind.METHOD_INVOCATION
					|| kind == Kind.ARRAY_ACCESS
					|| kind == Kind.MEMBER_SELECT
					|| kind == Kind.CONDITIONAL_EXPRESSION) {
				type = type.getCopy(false);
			}
		}
		return type;
	}
	
	@Override
	protected TypeAnnotator createTypeAnnotator(InferenceChecker checker) {
		return new UniverseTypeAnnotator(checker);
	}


	@Override
	protected TreeAnnotator createTreeAnnotator(InferenceChecker checker) {
		return new UniverseTreeAnnotator(checker, this);
	}
	/**
	 * For adding default annotations
	 * @author huangw5
	 *
	 */
	private class UniverseTypeAnnotator extends TypeAnnotator {

		public UniverseTypeAnnotator(BaseTypeChecker checker) {
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
			type.addAnnotation(checker.BOTTOM);
			return super.visitPrimitive(type, p);
		}

		@Override
		public Void visitDeclared(AnnotatedDeclaredType type, ElementKind p) {
			Element elt = type.getElement();
			if (!type.isAnnotated()
					&& (/*checker.isDefaultAnyType(type) ||*/ 
					elt != null && ElementUtils.isStatic(elt) 
						&& (elt.getKind() == ElementKind.FIELD
							|| elt.getKind() == ElementKind.LOCAL_VARIABLE
							|| elt.getKind() == ElementKind.EXCEPTION_PARAMETER
							|| elt.getKind() == ElementKind.ENUM_CONSTANT 
							|| elt.getKind() == ElementKind.PARAMETER))) {
            	type.clearAnnotations();
            	Set<AnnotationMirror> set = AnnotationUtils.createAnnotationSet();
            	set.add(checker.ANY);
            	if (elt != null && ElementUtils.isStatic(elt))
            		set.add(checker.PEER);
            	
            	annotateConstants(type, set);
            } 
			return super.visitDeclared(type, p);
		}
		
	}
	
	/**
	 * For adding default annotations
	 * @author huangw5
	 *
	 */
	private class UniverseTreeAnnotator extends TreeAnnotator {

		public UniverseTreeAnnotator(BaseTypeChecker checker, 
				AnnotatedTypeFactory typeFactory) {
			super(checker, typeFactory);
		}

		@Override
		public Void visitVariable(VariableTree node, AnnotatedTypeMirror p) {
			typeAnnotator.visit(p);
			VariableElement varElt = TreeUtils
					.elementFromDeclaration((VariableTree) node);
            if (!p.isAnnotated() && ElementUtils.isStatic(varElt)) {
            	p.clearAnnotations();
            	Set<AnnotationMirror> set = AnnotationUtils.createAnnotationSet();
            	set.add(checker.ANY);
            	set.add(checker.PEER);
            	annotateConstants(p, set);
            } 
			return super.visitVariable(node, p);
		}
		
		@Override
		public Void visitInstanceOf(InstanceOfTree node, AnnotatedTypeMirror p) {
			return super.visitInstanceOf(node, p);
		}

		@Override
		public Void visitLiteral(LiteralTree tree, AnnotatedTypeMirror type) {
			if (!type.isAnnotated()) {
//				if (type.getKind() == TypeKind.NULL) {
	            	type.clearAnnotations();
	            	Set<AnnotationMirror> set = AnnotationUtils.createAnnotationSet();
	            	set.add(checker.BOTTOM);
	            	annotateConstants(type, set);
//				}
//				else
//					type.addAnnotation(checker.READONLY);
			}
			return super.visitLiteral(tree, type);
		}
		
		@Override
		public Void visitBinary(BinaryTree node, AnnotatedTypeMirror p) {
			
			if (!p.isAnnotated() && checker.isChecking()) {
				p.addAnnotation(checker.BOTTOM);
			}
			
			return super.visitBinary(node, p);
		}

		@Override
		public Void visitMethod(MethodTree node, AnnotatedTypeMirror p) {
//			typeAnnotator.visit(p);
			return super.visitMethod(node, p);
		}

		@Override
		public Void visitNewArray(NewArrayTree tree, AnnotatedTypeMirror type) {
			// Skip
			return null;
		}

		@Override
		public Void visitIdentifier(IdentifierTree node, AnnotatedTypeMirror p) {
			return super.visitIdentifier(node, p);
		}

		@Override
		public Void visitTypeCast(TypeCastTree node, AnnotatedTypeMirror p) {
			
			if (!p.isAnnotated()) {
				// check if it is casting "this"
				if (TreeUtils.skipParens(node.getExpression()).toString().equals("this")) {
					annotateThis(p);
				}
			}
			
			return super.visitTypeCast(node, p);
		}

	}
}
