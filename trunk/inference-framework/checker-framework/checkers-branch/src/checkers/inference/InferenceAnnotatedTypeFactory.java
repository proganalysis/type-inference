/**
 * 
 */
package checkers.inference;

import java.util.List;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;

import checkers.inference.Reference.ExecutableReference;
import checkers.types.AnnotatedTypeFactory;
import checkers.types.AnnotatedTypeMirror;
import checkers.types.AnnotatedTypeMirror.AnnotatedArrayType;
import checkers.types.AnnotatedTypeMirror.AnnotatedDeclaredType;
import checkers.types.AnnotatedTypeMirror.AnnotatedExecutableType;
import checkers.types.TreeAnnotator;
import checkers.types.TypeAnnotator;
import checkers.util.AnnotationUtils;
import checkers.util.ElementUtils;
import checkers.util.TreeUtils;

import com.sun.source.tree.AnnotatedTypeTree;
import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.ArrayAccessTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.TypeCastTree;
import com.sun.source.tree.UnaryTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Attribute.TypeCompound;
import com.sun.tools.javac.comp.Enter;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCTypeAnnotation;
import com.sun.tools.javac.tree.JCTree.JCTypeCast;
import com.sun.tools.javac.tree.TreeInfo;

/**
 * @author huangw5
 *
 */
public class InferenceAnnotatedTypeFactory extends AnnotatedTypeFactory {
	
	protected final InferenceChecker checker; 
	
	protected final Enter enter;
	
    /** to annotate types based on the given un-annotated types */
    protected final TypeAnnotator typeAnnotator;
    
    /** to annotate types based on the given tree */
    protected final TreeAnnotator treeAnnotator;
    
	protected final SourcePositions positions;
	
	public InferenceAnnotatedTypeFactory(InferenceChecker checker,
			CompilationUnitTree root) {
		super(checker, root);
		this.checker = checker;
		this.enter = Enter.instance(((JavacProcessingEnvironment) env).getContext());
        this.typeAnnotator = createTypeAnnotator(checker);
        this.treeAnnotator = createTreeAnnotator(checker);
		this.positions = Trees.instance(checker.getProcessingEnvironment()).getSourcePositions();
        postInit();
	}

	
//    /**
//	 * @deprecated Use {@link checkers.inference.InferenceChecker#isSubtype(checkers.inference.InferenceAnnotatedTypeFactory,TypeElement,TypeElement)} instead
//	 */
//	public boolean isSubtype(TypeElement a1, TypeElement a2) {
//		return checker.isSubtype(a1, a2);
//	}
    

    /**
     * The implementation in {@link AnnotatedTypeFactory} cannot recognize the 
     * correct annotations when invoking the Main.compile() in the second time
     */
	@Override
	public boolean isSupportedQualifier(AnnotationMirror a) {
		// We do nothing if it is inferring
		if (!checker.isChecking())
			return super.isSupportedQualifier(a);
		else {
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
	}


	@Override
	protected void annotateInheritedFromClass(AnnotatedTypeMirror type) {
		// TODO Auto-generated method stub
//		super.annotateInheritedFromClass(type);
	}


	@Override
	protected AnnotatedDeclaredType getImplicitReceiverType(ExpressionTree tree) {
		AnnotatedDeclaredType type = super.getImplicitReceiverType(tree);
		if (checker.isChecking() && tree instanceof MethodInvocationTree 
				&& type != null && !checker.isAnnotated(type)) {
			MethodTree enclosingMethod = TreeUtils.enclosingMethod(getPath(tree));
			if (enclosingMethod != null) {
				ExecutableElement methodElt = TreeUtils.elementFromDeclaration(enclosingMethod);
				// FIXME: WEI: Replace the following lines with annotateInferredType 
				// on Nov 30, 2012
				Reference inferredRef = InferenceMain.getInstance().getCurrentExtractor()
						.getInferredReference(InferenceUtils.getElementSignature(methodElt));
				if (inferredRef != null)
					InferenceUtils.annotateReferenceType(type, 
							((ExecutableReference) inferredRef).getReceiverRef());
//				InferenceMain.getInstance().getCurrentExtractor().annotateInferredType(getIdentifier(methodElt), type);
			}
		}
		return type;
	}


	@Override
	public AnnotatedTypeMirror getAnnotatedType(Element elt) {
		AnnotatedTypeMirror type = super.getAnnotatedType(elt);
		if (checker.isChecking()
				&& !checker.isAnnotated(type)
				&& (elt.getKind() == ElementKind.FIELD
						|| elt.getKind() == ElementKind.LOCAL_VARIABLE
						|| elt.getKind() == ElementKind.METHOD
						|| elt.getKind() == ElementKind.CONSTRUCTOR 
						|| elt.getKind() == ElementKind.PARAMETER)) {
			InferenceMain.getInstance().getCurrentExtractor().annotateInferredType(getIdentifier(elt), type);
		}
		return type;
	}

	@Override
	public AnnotatedTypeMirror getAnnotatedType(Tree tree) {
//		if (tree.getKind() == Kind.CONDITIONAL_EXPRESSION
//				&& tree.toString().contains("collection == this"))
//			System.out.println();
		AnnotatedTypeMirror type  = null;
//		try {
			type = super.getAnnotatedType(tree);
//		} catch (RuntimeException e) {
//			System.out.println("ERROR: visiting " + tree.toString());
//			e.printStackTrace();
//			throw e;
//		}
		if (tree instanceof ExpressionTree)
			tree = TreeUtils.skipParens((ExpressionTree) tree);
		
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
//						String annoStr = anno.getAnnotationType().toString();
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
		
		if (checker.isChecking()) {
			switch (tree.getKind()) {
			case NEW_ARRAY:
			case NEW_CLASS:
				InferenceMain.getInstance().getCurrentExtractor().annotateInferredType(getIdentifier(tree), type);
				break;
			case METHOD_INVOCATION:
				if (/*!type.getKind().isPrimitive() &&*/ type.getKind() != TypeKind.VOID) {
					InferenceMain.getInstance().getCurrentExtractor().annotateInferredType(getIdentifier(tree), type);
				}
				break;
			case METHOD:
				ExecutableElement methodElt = TreeUtils.elementFromDeclaration(
						(MethodTree) tree);
				InferenceMain.getInstance().getCurrentExtractor().annotateInferredType(getIdentifier(methodElt), type);
				break;
			case VARIABLE:
				// TODO: Consider using viewpoint adaptation
				VariableElement varElt = TreeUtils.elementFromDeclaration((VariableTree)tree);
				InferenceMain.getInstance().getCurrentExtractor().annotateInferredType(getIdentifier(varElt), type);
				break;
			case IDENTIFIER:
				// TODO: Consider using viewpoint adaptation
				Element idElt = TreeUtils.elementFromUse((IdentifierTree) tree);
				// We don't want to annotate CLASS type
				if (idElt.getKind() != ElementKind.CLASS && idElt.getKind() != ElementKind.INTERFACE) 
					InferenceMain.getInstance().getCurrentExtractor().annotateInferredType(getIdentifier(idElt), type);
				break;
			case TYPE_CAST:
				if (!checker.isAnnotated(type)) {
					Tree t = tree;
					while (t.getKind() == Kind.TYPE_CAST) {
						t = ((TypeCastTree) t).getExpression();
						if (t instanceof ExpressionTree)
							t = TreeUtils.skipParens((ExpressionTree) t);
					}
					AnnotatedTypeMirror castType = getAnnotatedType(t);
					InferenceUtils.assignAnnotations(type, castType);
				}
				break;
			case ARRAY_ACCESS:
				// WEI: move from ReimAnnotatedTypeFactory on Aug 2. 
				ArrayAccessTree aTree = (ArrayAccessTree) tree;
				ExpressionTree aExpr = aTree.getExpression();
				AnnotatedTypeMirror aExprType = getAnnotatedType(aExpr);
				assert aExprType.getKind() == TypeKind.ARRAY;
				Set<AnnotationMirror> componentAnnos = ((AnnotatedArrayType) aExprType)
						.getComponentType().getAnnotations();
				Set<AnnotationMirror> adaptedAnnos = checker.adaptFieldSet(
						aExprType.getAnnotations(), componentAnnos);
				if (!adaptedAnnos.isEmpty()) {
					type.clearAnnotations();
					type.addAnnotations(adaptedAnnos);
				}
				break;	
			case MEMBER_SELECT:
				// WEI: added on Aug 2
//				InferenceMain.getInstance().getCurrentExtractor().annotateInferredType(
//						getIdentifier(tree), type);
				// WEI: Remove the above line, also considering remove the 
				// tree.getKind() == Kind.MEMBER_SELECT in the "default" case
				MemberSelectTree mTree = (MemberSelectTree) tree;
	            Element fieldElt = TreeUtils.elementFromUse(mTree);
	            if (checker.isAccessOuterThis(mTree)) {
	            	// If it is like Body.this
	            	// FIXME:
					MethodTree methodTree = this.getVisitorState().getMethodTree();
					if (methodTree != null) {
						ExecutableElement currentMethodElt = TreeUtils
								.elementFromDeclaration(methodTree);
		            	Element outerElt = checker.getOuterThisElement(mTree, currentMethodElt);
		            	Reference inferredRef = null;
		            	if (outerElt != null && outerElt.getKind() == ElementKind.METHOD) {
		            		inferredRef = InferenceMain.getInstance().getCurrentExtractor().getInferredReference(getIdentifier(outerElt));
		            	} else {
		            		inferredRef = InferenceMain.getInstance().getCurrentExtractor().getInferredReference(getIdentifier(currentMethodElt));
		            	}
		            	if (inferredRef != null)
							InferenceUtils.annotateReferenceType(type, 
									((ExecutableReference) inferredRef).getReceiverRef());
		            	else
		            		System.err.println("WARN: Cannot annotate " + mTree);
					}
				} else if (!fieldElt.getSimpleName().contentEquals("super")
						&& fieldElt.getKind() == ElementKind.FIELD
						&& !ElementUtils.isStatic(fieldElt)
//						&& checker.isFieldElt(exprType, fieldElt)
						) {
					// Do viewpoint adaptation
		            ExpressionTree expr = mTree.getExpression();
		            AnnotatedTypeMirror exprType = getAnnotatedType(expr);
					AnnotatedTypeMirror fieldType = getAnnotatedType(fieldElt);
					Set<AnnotationMirror> set = checker.adaptFieldSet(
							exprType.getAnnotations(),
							fieldType.getAnnotations());
					if (!set.isEmpty()) {
						type.clearAnnotations();
						type.addAnnotations(set);
					}
				}
				break;
			default: 
				if(!checker.isAnnotated(type)) {
					if (tree instanceof UnaryTree) {
						AnnotatedTypeMirror aType = getAnnotatedType(
								((UnaryTree) tree).getExpression());
						InferenceUtils.assignAnnotations(type, aType);
					} else if (tree.getKind() == Kind.MEMBER_SELECT) {
						InferenceMain.getInstance().getCurrentExtractor().annotateInferredType(
								getIdentifier(tree), type);
					}
				}
			}
		} else {
			// TODO: remove annotations on some trees, e.g. ArrayAccessTree, 
		}
		
		return type;
	}
	

	public void annotateConstants(AnnotatedTypeMirror type, 
			Set<AnnotationMirror> annos) {
		// Add recursively
		if (type.getKind() == TypeKind.ARRAY) {
			annotateConstants(((AnnotatedArrayType) type).getComponentType(), 
					annos);
		} else if (type.getKind() == TypeKind.DECLARED) {
			List<AnnotatedTypeMirror> typeArgs = ((AnnotatedDeclaredType) type)
					.getTypeArguments();
			for (AnnotatedTypeMirror t : typeArgs) {
				annotateConstants(t, annos);
			}
		} else if (type.getKind() == TypeKind.EXECUTABLE){
			AnnotatedExecutableType methodType = (AnnotatedExecutableType) type;
			// Receiver
			annotateConstants(methodType.getReceiverType(), annos);
			// Return
			annotateConstants(methodType.getReturnType(), annos);
			for (AnnotatedTypeMirror t : methodType.getParameterTypes()) {
				annotateConstants(t, annos);
			}
		}
		
		if (checker.isAnnotated(type) || type.getKind() == TypeKind.VOID
				/*|| type.getKind() == TypeKind.NULL*/)
			return;
		// Add annotations
		type.addAnnotations(annos);
	}
	
	public void annotateConstantsIgnoringExisting(AnnotatedTypeMirror type, 
			Set<AnnotationMirror> annos) {
		// Add recursively
		if (type.getKind() == TypeKind.ARRAY) {
			annotateConstantsIgnoringExisting(((AnnotatedArrayType) type).getComponentType(), 
					annos);
		} else if (type.getKind() == TypeKind.DECLARED) {
			List<AnnotatedTypeMirror> typeArgs = ((AnnotatedDeclaredType) type)
					.getTypeArguments();
			for (AnnotatedTypeMirror t : typeArgs) {
				annotateConstantsIgnoringExisting(t, annos);
			}
		} else if (type.getKind() == TypeKind.EXECUTABLE){
			AnnotatedExecutableType methodType = (AnnotatedExecutableType) type;
			// Receiver
			annotateConstantsIgnoringExisting(methodType.getReceiverType(), annos);
			// Return
			annotateConstantsIgnoringExisting(methodType.getReturnType(), annos);
			for (AnnotatedTypeMirror t : methodType.getParameterTypes()) {
				annotateConstantsIgnoringExisting(t, annos);
			}
		}
		
		if (type.getKind() == TypeKind.VOID
				/*|| type.getKind() == TypeKind.NULL*/)
			return;
		// Add annotations
		type.clearAnnotations();
		type.addAnnotations(annos);
	}
	
	/**
	 * Similar to declarationFromElement from {@link AnnotatedTypeFactory}
	 * @param elt
	 * @return
	 */
	public Tree getDeclaration(Element elt) {
		return checker.getDeclaration(elt);
	}
	
	
    public long getLineNumber(Element elt) {
    	CompilationUnitTree newRoot = checker.getRootByElement(elt);
    	if (newRoot == null) {
    		// It is from library
    		return 0;
    	}
    	return getLineNumber(newRoot, getDeclaration(elt));
    }
    
    public long getLineNumber(Tree tree) {
    	return getLineNumber(root, tree);
    }
	
    private long getLineNumber(CompilationUnitTree root, Tree tree) {
		long lineNum = positions.getStartPosition(root, tree);
		lineNum = root.getLineMap().getLineNumber(lineNum);
		return lineNum;
    }
    
    public String getFileName(Element elt) {
    	CompilationUnitTree newRoot = checker.getRootByElement(elt);
    	if (newRoot == null) {
    		// It is from library
    		return "zLIB:" + elt.getEnclosingElement();
    	}
    	return getFileName(newRoot, getDeclaration(elt));
    }
    
	public String getFileName(Tree tree) {
		return getFileName(root, tree);
	}
	
	private String getFileName(CompilationUnitTree root, Tree tree) {
		String fileName = root.getSourceFile().getName();
		// FIXME: comment out the following lines on Aug 16, 2012. It may affect
		// the Eclipse plugin
//		fileName = fileName.substring(fileName.lastIndexOf('/') + 1);
//		ExpressionTree packageName = root.getPackageName();
//		if (packageName != null)
//			fileName = packageName.toString().replace('.', '/') + "/" + fileName;
		return fileName;
	}
	
	/**
	 * FIXME: This has to be consistent with the Reference.getIdentifier()
	 * @param elt
	 * @return
	 */
	protected String getIdentifier(Tree tree) {
		String id = getFileName(tree) + ":"
				+ TreeInfo.getStartPos((JCTree) tree) + ":" + tree.toString();
		return id;
	}
	
	protected String getIdentifier(Element elt) {
		if (elt.getKind() == ElementKind.LOCAL_VARIABLE
				|| elt.getKind() == ElementKind.EXCEPTION_PARAMETER) {
			return getIdentifier(getDeclaration(elt));
		} else
			return InferenceUtils.getElementSignature(elt);
	}
	
	protected TypeAnnotator createTypeAnnotator(InferenceChecker checker) {
		return new TypeAnnotator(checker);
	}
	
	protected TreeAnnotator createTreeAnnotator(InferenceChecker checker) {
		return new TreeAnnotator(checker, this);
	}

}
