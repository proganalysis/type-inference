/**
 * 
 */
package checkers.inference2;

import java.util.List;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;

import checkers.inference2.Reference.ExecutableReference;
import checkers.types.AnnotatedTypeFactory;
import checkers.types.AnnotatedTypeMirror;
import checkers.types.AnnotatedTypeMirror.AnnotatedDeclaredType;
import checkers.util.AnnotationUtils;
import checkers.util.TreeUtils;

import com.sun.source.tree.AnnotatedTypeTree;
import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.ArrayAccessTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.TypeCastTree;
import com.sun.tools.javac.code.Attribute.TypeCompound;
import com.sun.tools.javac.tree.JCTree.JCTypeAnnotation;

/**
 * @author huangw5
 *
 */
public class InferenceAnnotatedTypeFactory extends AnnotatedTypeFactory {
	
	private InferenceChecker checker;
	
	public InferenceAnnotatedTypeFactory(InferenceChecker checker,
			CompilationUnitTree root) {
		super(checker, root);
		this.checker = checker;
        postInit();
	}


	protected ExecutableElement getCurrentMethodElt() {
        MethodTree methodTree = this.getVisitorState().getMethodTree();
        if (methodTree != null) {
            ExecutableElement currentMethod = TreeUtils.elementFromDeclaration(methodTree);
            return currentMethod;
        }
        return null;
    }
	
	private void fixTypeCast(Tree tree, AnnotatedTypeMirror type) {
		// If we call main.compile(...) for the second time, casted type would
		// be ignored by the checker framework. We manually fix it here
		if (tree.getKind() == Kind.TYPE_CAST && !checker.isAnnotated(type)) {
			TypeCastTree castTree = (TypeCastTree) tree;
			Tree typeTree = castTree.getType();
			if (typeTree instanceof AnnotatedTypeTree) {
				List<? extends AnnotationTree> annos = ((AnnotatedTypeTree) typeTree).getAnnotations();
				for (AnnotationTree anno : annos) {
					if (anno instanceof JCTypeAnnotation) {
						TypeCompound attribute_field = ((JCTypeAnnotation) anno).attribute_field;
						String annoStr = attribute_field.toString();
						if (annoStr.startsWith("@"))
							annoStr = annoStr.substring(1);
						AnnotationMirror fromName = annotations.fromName(annoStr);
						if (fromName != null)
							type.addAnnotation(fromName);
					}
				}
			}
		}
	}

    /**
     * The implementation in {@link AnnotatedTypeFactory} cannot recognize the 
     * correct annotations when invoking the Main.compile() in the second time
     */
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
		checker.annotateInferredType(type, elt);
		return type;
	}
	
	@Override
	public AnnotatedTypeMirror getAnnotatedType(Tree tree) {
		AnnotatedTypeMirror type = super.getAnnotatedType(tree);
		if (tree instanceof ExpressionTree)
			tree = TreeUtils.skipParens((ExpressionTree) tree);
		checker.annotateInferredType(type, tree);
		
		fixTypeCast(tree, type);
		
		switch (tree.getKind()) {
		case ARRAY_ACCESS: 
			ExpressionTree arrayExpr  = ((ArrayAccessTree) tree).getExpression();
			AnnotatedTypeMirror arrayType = getAnnotatedType(arrayExpr);
			Set<AnnotationMirror> arraySet = checker.adaptFieldSet(
					arrayType.getAnnotations(), type.getAnnotations());
			if (!arraySet.isEmpty()) {
				type.clearAnnotations();
				type.addAnnotations(arraySet);
			}
			break;
		case MEMBER_SELECT:
			// TODO:
		default:
			break;
		}
		
		return type;
	}
	
	@Override
	protected AnnotatedDeclaredType getImplicitReceiverType(ExpressionTree tree) {
		AnnotatedDeclaredType type = super.getImplicitReceiverType(tree);
		if (type != null && !checker.isAnnotated(type)) {
			if (tree.getKind() == Kind.METHOD_INVOCATION) {
				// This tree has implicit receiver "this"
				ExecutableElement invokeElt = TreeUtils.elementFromUse(
						(MethodInvocationTree) tree);
				ExecutableElement enclosingMethodElt = checker
						.getEnclosingMethodWithElt(invokeElt);
				if (enclosingMethodElt != null) {
					Reference enclosingRef = checker
							.getAnnotatedReference(enclosingMethodElt);
					checker.annotateInferredType(type, 
							((ExecutableReference) enclosingRef).getThisRef());
				}
			}
			// MEMBER_SELECT?
		}
		return type;
	}
}
