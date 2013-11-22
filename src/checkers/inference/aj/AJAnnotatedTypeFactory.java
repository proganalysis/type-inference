/**
 * 
 */
package checkers.inference.aj;

import java.util.List;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
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
public class AJAnnotatedTypeFactory extends InferenceAnnotatedTypeFactory {
	
	private AJChecker checker;

	public AJAnnotatedTypeFactory(AJChecker checker,
			CompilationUnitTree root) {
		super(checker, root);
		this.checker = checker;
	}
	
	private void annotateThis(ExecutableElement methodElt, AnnotatedTypeMirror type) {
		if (checker.isChecking())
			return;
		type.clearAnnotations();
		type.addAnnotation(checker.SELF);
		
		// if the enclosing method is not public, also add INTSELF
		if (!methodElt.getEnclosingElement().getModifiers().contains(Modifier.PUBLIC)
				|| !methodElt.getModifiers().contains(Modifier.PUBLIC)) {
		type.addAnnotation(checker.INTSELF);
		}
	}
	

	private void annotateMethod(ExecutableElement methodElt,
			AnnotatedExecutableType methodType) {
		if (checker.isChecking() || methodType.isAnnotated()) {
			return;
		}
		
		boolean isFromLib = checker.isFromLibrary(methodElt);
		
		// First annotate the receiver 
		annotateThis(methodElt, methodType.getReceiverType());
		
		// Now the parameters
		List<AnnotatedTypeMirror> parameterTypes = methodType.getParameterTypes();
		for (AnnotatedTypeMirror paramterType : parameterTypes) {
			if (isFromLib || !paramterType.isAnnotated()
					&& methodElt.getModifiers().contains(Modifier.PUBLIC)
					&& methodElt.getEnclosingElement().getModifiers().contains(
							Modifier.PUBLIC)) {
				paramterType.addAnnotation(checker.NONALIASED);
			} 
		}
		
		// Return 
		AnnotatedTypeMirror returnType = methodType.getReturnType();
		if (isFromLib || !returnType.isAnnotated() 
				&& returnType.getKind() != TypeKind.VOID
				&& methodElt.getEnclosingElement().getModifiers().contains(Modifier.PUBLIC) 
				&& methodElt.getModifiers().contains(Modifier.PUBLIC)
				) {
			returnType.clearAnnotations();
			returnType.addAnnotation(checker.NONALIASED);
			returnType.addAnnotation(checker.ALIASED); //FIXME: ANA: this is turned on in Phase 1 and off in Phase 2.
			// WEI: add INTALIASED ?
//            returnType.addAnnotation(checker.INTALIASED);
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
//		Element elt = InternalUtils.symbol(tree);
		if (!checker.isChecking() /*&& !checker.isDefaultAnyType(type)*/
				&& type.isAnnotated()
				&& !type.getKind().isPrimitive()
				&& type.getKind() != TypeKind.NULL
//				&& (elt == null || !ElementUtils.isStatic(elt))
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
		return new AJTypeAnnotator(checker);
	}


	@Override
	protected TreeAnnotator createTreeAnnotator(InferenceChecker checker) {
		return new AJTreeAnnotator(checker, this);
	}
	/**
	 * For adding default annotations
	 * @author huangw5
	 *
	 */
	private class AJTypeAnnotator extends TypeAnnotator {

		public AJTypeAnnotator(BaseTypeChecker checker) {
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
					&& checker.isExceptionClass(type)
					&& (elt != null && (elt.getKind() == ElementKind.FIELD
							|| elt.getKind() == ElementKind.LOCAL_VARIABLE
							|| elt.getKind() == ElementKind.EXCEPTION_PARAMETER
							|| elt.getKind() == ElementKind.ENUM_CONSTANT 
							|| elt.getKind() == ElementKind.PARAMETER))) {
            	type.clearAnnotations();
            	type.addAnnotation(checker.NONALIASED);
            } 
			return super.visitDeclared(type, p);
		}
		
	}
	
	/**
	 * For adding default annotations
	 * @author huangw5
	 *
	 */
	private class AJTreeAnnotator extends TreeAnnotator {
		
		private ExecutableElement methodElt = null;

		public AJTreeAnnotator(BaseTypeChecker checker, 
				AnnotatedTypeFactory typeFactory) {
			super(checker, typeFactory);
		}

		@Override
		public Void visitVariable(VariableTree node, AnnotatedTypeMirror p) {
			return super.visitVariable(node, p);
		}
		
		@Override
		public Void visitInstanceOf(InstanceOfTree node, AnnotatedTypeMirror p) {
			return super.visitInstanceOf(node, p);
		}

		@Override
		public Void visitLiteral(LiteralTree tree, AnnotatedTypeMirror type) {
			if (!type.isAnnotated()) {
            	type.clearAnnotations();
            	Set<AnnotationMirror> set = AnnotationUtils.createAnnotationSet();
            	set.add(checker.BOTTOM);
            	annotateConstants(type, set);
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
			ExecutableElement pre = methodElt;
			methodElt = TreeUtils.elementFromDeclaration(node);
			Void res = super.visitMethod(node, p);
			methodElt = pre;
			return res;
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
				if (TreeUtils.skipParens(
						node.getExpression()).toString().equals("this")) {
					annotateThis(methodElt, p);
				}
			}
			return super.visitTypeCast(node, p);
		}

	}

}
