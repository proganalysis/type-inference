/**
 * 
 */
package checkers.inference2;

import java.io.PrintWriter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

import checkers.basetype.BaseTypeChecker;
import checkers.basetype.BaseTypeVisitor;
import checkers.types.AnnotatedTypeFactory;
import checkers.types.AnnotatedTypeMirror;
import checkers.types.AnnotatedTypeMirror.AnnotatedArrayType;
import checkers.types.AnnotatedTypeMirror.AnnotatedDeclaredType;
import checkers.util.AnnotationUtils;
import checkers.util.ElementUtils;
import checkers.util.InternalUtils;
import checkers.util.TreeUtils;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.PrimitiveTypeTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symbol.TypeSymbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.Type.ClassType;
import com.sun.tools.javac.comp.AttrContext;
import com.sun.tools.javac.comp.Enter;
import com.sun.tools.javac.comp.Env;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeInfo;

/**
 * @author huangw5
 *
 */
@SupportedOptions( { "warn", "checking" } ) 
public abstract class InferenceChecker extends BaseTypeChecker {
	
	public static boolean DEBUG = false;
	
    public final static String CALLSITE_PREFIX = "CALLSITE-";

    public final static String FAKE_PREFIX = "FAKE-";

    public final static String LIB_PREFIX = "LIB-";

    public final static String THIS_PREFIX = "THIS-";

    public final static String RETURN_PREFIX = "RETURN-";
	
	public static enum FailureStatus {
		IGNORE,
		WARN,
		ERROR
	}
	
	private boolean isChecking = false; 
	
	protected Enter enter;

	private SourcePositions positions;
	
	private Comparator<AnnotationMirror> comparator;

	private Types types;
	
	private ViewpointAdapter vpa;

    private static Map<String, Reference> annotatedReferences; 

	@Override
	public void initChecker(ProcessingEnvironment processingEnv) {
		super.initChecker(processingEnv);
		isChecking = processingEnv.getOptions().containsKey("checking");
		this.enter = Enter.instance(((JavacProcessingEnvironment) env).getContext());
		this.positions = Trees.instance(getProcessingEnvironment()).getSourcePositions();
		InferenceMain.getInstance().setInferenceChcker(this);
		if (getProcessingEnvironment().getOptions().containsKey(
				"debug")) {
			DEBUG = true;
		}
        types = processingEnv.getTypeUtils();
        vpa = getViewpointAdapter();
        annotatedReferences = new HashMap<String, Reference>();
	}
	
	@Override
	protected BaseTypeVisitor<?> createSourceVisitor(CompilationUnitTree root) {
		if (!isChecking) {
			// create inference visitor
			return getInferenceVisitor(this, root);
		}
		return super.createSourceVisitor(root);
	}
	
//	@Override
//	protected TypeHierarchy createTypeHierarchy() {
//    	return new InferenceTypeHierarchy(this, getQualifierHierarchy());
//	}
//	
//	
//    /** Factory method to easily change what Factory is used to
//     * create a QualifierHierarchy.
//     */
//	@Override
//    protected MultiGraphQualifierHierarchy.MultiGraphFactory createQualifierHierarchyFactory() {
//        return new InferenceGraphQualifierHierarchy.InferenceGraphFactory(this);
//    }

	public boolean isChecking() {
		return isChecking;
	}
	
	/**
	 * Get the root tree of element elt
	 * @param elt
	 * @return
	 */
	public CompilationUnitTree getRootByElement(Element elt) {
		Symbol symbol = (Symbol) elt;
		TypeSymbol enclosing = symbol.enclClass();
		Env<AttrContext> env = enter.getEnv(enclosing);
		if (env == null)
			return null;
		return env.toplevel;
	}
	
	public Tree getDeclaration(Element elt) {
        Tree fromElt;
        CompilationUnitTree newRoot = getRootByElement(elt);
        if (newRoot == null)
        	return null;
        switch (elt.getKind()) {
        case CLASS:
        case ENUM:
        case INTERFACE:
        case ANNOTATION_TYPE:
        case FIELD:
        case ENUM_CONSTANT:
        case METHOD:
        case CONSTRUCTOR:
            fromElt = trees.getTree(elt);
            break;
        default:
            fromElt = TreeInfo.declarationFor((Symbol)elt, (JCTree)newRoot);
            break;
        }
        return fromElt;
	}
	
	/**
	 * Check whether fieldElt is a field in typeElt
	 * @param typeElt
	 * @param fieldElt
	 * @return
	 */
	public boolean isFieldElt(AnnotatedTypeMirror type, Element fieldElt) {
		if (fieldElt.getKind() != ElementKind.FIELD)
			return false;
		if (ElementUtils.isStatic(fieldElt) 
				&& fieldElt.getSimpleName().contentEquals("class"))
			return true;
		if (type instanceof AnnotatedArrayType && fieldElt.getSimpleName().contentEquals("length"))
			return true;
		TypeElement typeElt = (type instanceof AnnotatedDeclaredType)? 
            		(TypeElement) ((AnnotatedDeclaredType) type).getUnderlyingType().asElement() 
            		: null;
        if (typeElt == null)
        	return false;
		VariableElement findFieldInType = ElementUtils.findFieldInType(typeElt, fieldElt.toString());
		TypeMirror superclass = typeElt.getSuperclass();
		while (findFieldInType == null && superclass.getKind() != TypeKind.NONE) {
			typeElt = (TypeElement) ((ClassType) superclass).asElement();
			superclass = typeElt.getSuperclass();
			findFieldInType = ElementUtils.findFieldInType(typeElt, fieldElt.toString());
		}
		return findFieldInType != null;
	}
    
	public Element getOuterThisElement(MemberSelectTree mTree,
			ExecutableElement currentMethodElt) {
    	if (!isAccessOuterThis(mTree))
    		return null;
        if (currentMethodElt == null) {
        	return null;
        }
    	Element enclosingElt = currentMethodElt.getEnclosingElement();
        TypeElement element = (TypeElement)InternalUtils.symbol(mTree.getExpression());
        while (enclosingElt != null) {
            if (enclosingElt instanceof ExecutableElement) {
                ExecutableElement method = (ExecutableElement) enclosingElt;
                if (method.asType() != null
                        && isSubtype(
                    		(TypeElement) method.getEnclosingElement(), element))
                    if (ElementUtils.isStatic(method)) {
                        enclosingElt = null;
                        break;
                    }
                    else
                        break;
            }
            else if (enclosingElt instanceof TypeElement) {
                if (isSubtype((TypeElement) enclosingElt, element))
                    break;
            }
            enclosingElt = enclosingElt.getEnclosingElement();
        }
        return enclosingElt;
    }
	
    /**
     * Check if this mTree is accessing expr like "Body.this"
     * Consider replace it with {@link AnnotatedTypeFactory#isAnyEnclosingThisDeref(ExpressionTree)}
     * @param mTree
     * @return
     */
    public boolean isAccessOuterThis(MemberSelectTree mTree) {
		if (!(mTree.getExpression() instanceof PrimitiveTypeTree)) {
			if (mTree.getIdentifier().contentEquals("this")) {
				return true;
			}
		}
		return false;
    }
    
	
	public boolean isExceptionClass(AnnotatedTypeMirror type) {
		boolean result = false;
		TypeMirror underlyingType = type.getUnderlyingType();
		if (underlyingType instanceof ClassType) {
			Type supertype_field = ((ClassType) underlyingType).supertype_field;
			while (supertype_field != null) {
				if (supertype_field.toString().equals("java.lang.Throwable")) {
					result = true;
					break;
				} else if (supertype_field instanceof ClassType) {
					supertype_field = ((ClassType) supertype_field).supertype_field;
				} else
					break;
			}
		}
		return result;
	}

	
	/**
	 * Check if the {@code elt} is from library
	 * @param elt
	 * @return
	 */
	public boolean isFromLibrary(Element elt) {
		return this.getRootByElement(elt) == null;
	}
	
	
	public boolean isCompilerAddedConstructor(ExecutableElement methodElt) {
		MethodTree node = (MethodTree) getDeclaration(methodElt);
		Element enclosingElement = methodElt.getEnclosingElement();
		if (enclosingElement instanceof TypeElement && TreeUtils.isConstructor(node)) {
			// Check if it is a default constructor
			if (node.getParameters().isEmpty()) {
				List<? extends StatementTree> statements = node.getBody().getStatements();
				if (statements.size() == 1 && statements.get(0).toString().contains("super")) {
					CompilationUnitTree root = getRootByElement(methodElt);
					long constructorLineNum = positions.getStartPosition(root, node);
					constructorLineNum = root.getLineMap().getLineNumber(constructorLineNum);
					ClassTree enclosingClassDecl = (ClassTree) getDeclaration(enclosingElement);
					long classLineNum = positions.getStartPosition(root, enclosingClassDecl);
					classLineNum = root.getLineMap().getLineNumber(classLineNum);
					if (constructorLineNum == classLineNum) {
						// a default constructor, skip it
						return true;
					}
				}
			}
		}
        return false;
	}


    public long getLineNumber(Element elt) {
    	CompilationUnitTree newRoot = getRootByElement(elt);
    	if (newRoot == null) {
    		// It is from library
    		return 0;
    	}
    	return getLineNumber(newRoot, getDeclaration(elt));
    }
    
    public long getLineNumber(Tree tree) {
    	return getLineNumber(currentRoot, tree);
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
    		return LIB_PREFIX + elt.getEnclosingElement();
    	}
    	return getFileName(newRoot, getDeclaration(elt));
    }
    
	public String getFileName(Tree tree) {
		return getFileName(currentRoot, tree);
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

	public String getIdentifier(Tree tree) {
		String id = getFileName(tree) + ":"
				+ TreeInfo.getStartPos((JCTree) tree) + ":" + tree.toString();
		return id;
	}
	
	public String getIdentifier(Element elt) {
		if (elt.getKind() == ElementKind.LOCAL_VARIABLE
				|| elt.getKind() == ElementKind.EXCEPTION_PARAMETER) {
			return getIdentifier(getDeclaration(elt));
		} else
			return InferenceUtils.getElementSignature(elt);
	}


	public boolean isAnnotated(AnnotatedTypeMirror type) {
		return isAnnotated(type.getAnnotations());
	}

	public boolean isAnnotated(Reference ref) {
		return isAnnotated(ref.getAnnotations());
	}
	
	private boolean isAnnotated(Set<AnnotationMirror> annos) {
        Set<AnnotationMirror> set = AnnotationUtils.createAnnotationSet();
        set.addAll(annos);
        set.retainAll(getSourceLevelQualifiers());
        return !set.isEmpty();
	}


    public Reference getAnnotatedReference(Element elt) {
    }

    public Reference getAnnotatedReference(Tree t) {
    }


	public AnnotationMirror adaptField(AnnotationMirror contextAnno, 
			AnnotationMirror declAnno) {
        return vpa.adaptField(contextAnno, declAnno);
	}
	
	public AnnotationMirror adaptMethod(AnnotationMirror contextAnno, 
			AnnotationMirror declAnno) {
        return vpa.adaptMethod(contextAnno, declAnno);
	}
	
	/**
	 * Adapt the declared type of a field from the point of view the receiver
	 * @param contextSet The set of annotations of the receiver type
	 * @param declSet The set of annotations of the declared type
	 * @return
	 */
	public Set<AnnotationMirror> adaptFieldSet(Set<AnnotationMirror> contextSet,
			Set<AnnotationMirror> declSet) {
		Set<AnnotationMirror> outSet = AnnotationUtils.createAnnotationSet();
		for (AnnotationMirror declAnno : declSet) {
			for (AnnotationMirror rcvAnno : contextSet) {
				AnnotationMirror anno = adaptField(rcvAnno, declAnno);
				if (anno != null)
					outSet.add(anno);
			}
		}
		return outSet;
	}
	
	/**
	 * Adapt the declared type of a parameter/return from the point of view the
	 * receiver
	 * 
	 * @param contextSet
	 *            The set of annotations of the receiver type
	 * @param declSet
	 *            The set of annotations of the declared type
	 * @return
	 */
	public Set<AnnotationMirror> adaptMethodSet(Set<AnnotationMirror> contextSet,
			Set<AnnotationMirror> declSet) {
		Set<AnnotationMirror> outSet = AnnotationUtils.createAnnotationSet();
		for (AnnotationMirror declAnno : declSet) {
			for (AnnotationMirror rcvAnno : contextSet) {
				AnnotationMirror anno = adaptMethod(rcvAnno, declAnno);
				if (anno != null)
					outSet.add(anno);
			}
		}
		return outSet;
	}
	
    /**
	 * Create the comparator
	 * @return
	 */
	public Comparator<AnnotationMirror> getComparator() {
		if (comparator == null) {
			comparator = new Comparator<AnnotationMirror>() {
				@Override
				public int compare(AnnotationMirror o1, AnnotationMirror o2) {
					int ow1 = getAnnotaionWeight(o1);
					int ow2 = getAnnotaionWeight(o2);
					return ow1 - ow2;
				}
			};
		}
		return comparator;
	}


	
	/**
	 * Indicate if it needs to force all elements in sup to be the super type
	 * of all elements in sub;
	 * @return
	 */
	public abstract boolean isStrictSubtyping();
				
	/**
	 * Indicate if the error on this constraint can be ignored
	 * @param c
	 * @return
	 */
	public abstract FailureStatus getFailureStatus(Constraint c);
	
	public abstract ViewpointAdapter getViewpointAdapter();
	
	public abstract BaseTypeVisitor<?> getInferenceVisitor(InferenceChecker checker, 
			CompilationUnitTree root);
	
	public abstract Set<AnnotationMirror> getSourceLevelQualifiers();

	public abstract int getAnnotaionWeight(AnnotationMirror anno);
	
	public abstract void printResult(Map<String, Reference> solution, 
			PrintWriter out);
	
	public abstract boolean needCheckConflict();

	public boolean isSubtype(TypeElement a1, TypeElement a2) {
        // TODO
	    return (a1.equals(a2)
	            || types.isSubtype(types.erasure(a1.asType()),
	                    types.erasure(a2.asType())));
	}

}
