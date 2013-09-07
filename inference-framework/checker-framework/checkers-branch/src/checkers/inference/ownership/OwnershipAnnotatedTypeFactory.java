/**
 * 
 */
package checkers.inference.ownership;

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
public class OwnershipAnnotatedTypeFactory extends
		InferenceAnnotatedTypeFactory {
	
	private OwnershipChecker checker;

	public OwnershipAnnotatedTypeFactory(OwnershipChecker checker,
			CompilationUnitTree root) {
		super(checker, root);
		this.checker = checker;
	}
	
	private void annotateThis(AnnotatedTypeMirror type) {
		if (checker.isChecking())
			return;
		type.clearAnnotations();
		type.addAnnotation(checker.OWNPAR);
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
			set.add(checker.OWNPAR);
			set.add(checker.OWNNOREP);
			set.add(checker.PARPAR);
			set.add(checker.PARNOREP);
			set.add(checker.NOREP);
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
		if (!checker.isChecking() && !checker.isDefaultNorepType(type)
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
		return new OwnershipTypeAnnotator(checker);
	}


	@Override
	protected TreeAnnotator createTreeAnnotator(InferenceChecker checker) {
		return new OwnershipTreeAnnotator(checker, this);
	}
	/**
	 * For adding default annotations
	 * @author huangw5
	 *
	 */
	private class OwnershipTypeAnnotator extends TypeAnnotator {

		public OwnershipTypeAnnotator(BaseTypeChecker checker) {
			super(checker);
			// TODO Auto-generated constructor stub
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
					&& (checker.isDefaultNorepType(type) || elt != null
							&& ElementUtils.isStatic(elt)
							&& (elt.getKind() == ElementKind.FIELD
									|| elt.getKind() == ElementKind.LOCAL_VARIABLE
//									|| elt.getKind() == ElementKind.METHOD
//									|| elt.getKind() == ElementKind.CONSTRUCTOR
									|| elt.getKind() == ElementKind.EXCEPTION_PARAMETER
									|| elt.getKind() == ElementKind.ENUM_CONSTANT 
									|| elt.getKind() == ElementKind.PARAMETER))) {
            	type.clearAnnotations();
            	Set<AnnotationMirror> set = AnnotationUtils.createAnnotationSet();
            	set.add(checker.NOREP);
            	annotateConstants(type, set);
//            	type.addAnnotation(checker.NOREP);
            } 
			return super.visitDeclared(type, p);
		}
		
	}
	
	/**
	 * For adding default annotations
	 * @author huangw5
	 *
	 */
	private class OwnershipTreeAnnotator extends TreeAnnotator {

		public OwnershipTreeAnnotator(BaseTypeChecker checker, 
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
            	set.add(checker.NOREP);
            	annotateConstants(p, set);
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
			if (!type.isAnnotated()) {
				if (type.getKind() == TypeKind.NULL) {
	            	type.clearAnnotations();
	            	Set<AnnotationMirror> set = AnnotationUtils.createAnnotationSet();
	            	set.add(checker.BOTTOM);
	            	annotateConstants(type, set);
				}
//				else
//					type.addAnnotation(checker.READONLY);
			}
			return super.visitLiteral(tree, type);
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
//			if (node.getName().contentEquals("this")) {
//				
//			}
			return super.visitIdentifier(node, p);
		}
		
		
	}

}
