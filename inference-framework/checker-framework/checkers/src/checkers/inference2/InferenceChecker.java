/**
 * 
 */
package checkers.inference2;

import static com.esotericsoftware.minlog.Log.*;
import static com.esotericsoftware.minlog.Log.error;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
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
import checkers.inference2.Constraint.EqualityConstraint;
import checkers.inference2.Constraint.SubtypeConstraint;
import checkers.inference2.Constraint.UnequalityConstraint;
import checkers.inference2.ConstraintSolver.FailureStatus;
import checkers.inference2.Reference.AdaptReference;
import checkers.inference2.Reference.ArrayReference;
import checkers.inference2.Reference.ExecutableReference;
import checkers.inference2.Reference.RefKind;
import checkers.source.SourceVisitor;
import checkers.types.AnnotatedTypeFactory;
import checkers.types.AnnotatedTypeMirror;
import checkers.types.AnnotatedTypeMirror.AnnotatedArrayType;
import checkers.types.AnnotatedTypeMirror.AnnotatedDeclaredType;
import checkers.types.AnnotatedTypeMirror.AnnotatedExecutableType;
import checkers.util.AnnotationUtils;
import checkers.util.ElementUtils;
import checkers.util.InternalUtils;
import checkers.util.TreeUtils;

import com.esotericsoftware.minlog.Log;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.PrimitiveTypeTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreePath;
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
@SupportedOptions( { "warn", "infer" } ) 
public abstract class InferenceChecker extends BaseTypeChecker {
	
	public static boolean DEBUG = false;
	
    public final static String CALLSITE_PREFIX = "CALLSITE-";

    public final static String FAKE_PREFIX = "FAKE-";

    public final static String LIB_PREFIX = "LIB-";

    public final static String THIS_PREFIX = "THIS-";

    public final static String RETURN_PREFIX = "RETURN-";
    
    public final static String ARRAY_SUFFIX = "[]";
	
	private boolean isInferring = false; 
	
	protected Enter enter;

	private SourcePositions positions;
	
	private Comparator<AnnotationMirror> comparator;

	private Types types;
	
    private static Map<String, Reference> annotatedReferences = new HashMap<String, Reference>();
    
    private Set<Constraint> constraints = new LinkedHashSet<Constraint>();

    private AnnotatedTypeFactory currentFactory;
    
	@Override
	public void initChecker(ProcessingEnvironment processingEnv) {
		super.initChecker(processingEnv);
		isInferring = processingEnv.getOptions().containsKey("infer");
		this.enter = Enter.instance(((JavacProcessingEnvironment) env).getContext());
		this.positions = Trees.instance(getProcessingEnvironment()).getSourcePositions();
		InferenceMain.getInstance().setInferenceChcker(this);
//        if (getProcessingEnvironment().getOptions().containsKey(
//                "debug")) {
			Log.set(LEVEL_DEBUG);
//        }
        types = processingEnv.getTypeUtils();
	}
	

	@Override
	public AnnotatedTypeFactory createFactory(CompilationUnitTree root) {
		if (isInferring) {
	        return new AnnotatedTypeFactory(this, root);
		}
		return super.createFactory(root);
	}

	@Override
	protected SourceVisitor<?, ?> createSourceVisitor(CompilationUnitTree root) {
		if (isInferring) {
			// create inference visitor
			return new InferenceVisitor(this, root);
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



	public void setCurrentFactory(AnnotatedTypeFactory factory) {
		this.currentFactory = factory;
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
	
    public long getLineNumber(CompilationUnitTree root, Tree tree) {
		long lineNum = positions.getStartPosition(root, tree);
		lineNum = root.getLineMap().getLineNumber(lineNum);
		return lineNum;
    }
    
    public ExecutableElement getCurrentMethodElt() {
		MethodTree enclosingMethod = TreeUtils.enclosingMethod(currentPath);
        if (enclosingMethod == null)
            return null;
        else
            return TreeUtils.elementFromDeclaration(enclosingMethod);
    }
    
    
    public String getFileName(Element elt) {
    	CompilationUnitTree newRoot = getRootByElement(elt);
    	if (newRoot == null) {
    		// It is from library
    		return LIB_PREFIX + elt.getEnclosingElement();
    	}
    	return getFileName(newRoot, getDeclaration(elt));
    }
    
	public String getFileName(Tree tree) {
		return getFileName(currentRoot, tree);
	}
	
	public String getFileName(CompilationUnitTree root, Tree tree) {
		String fileName = root.getSourceFile().getName();
		// FIXME: comment out the following lines on Aug 16, 2012. It may affect
		// the Eclipse plugin
//		fileName = fileName.substring(fileName.lastIndexOf('/') + 1);
//		ExpressionTree packageName = root.getPackageName();
//		if (packageName != null)
//			fileName = packageName.toString().replace('.', '/') + "/" + fileName;
		return fileName;
	}

	public String getIdentifier(ExpressionTree tree) {
		TypeElement classElt = TreeUtils.elementFromDeclaration(TreeUtils
				.enclosingClass(currentFactory.getPath(tree)));
		String id = classElt.getQualifiedName() 
				+ ":" + getLineNumber(tree)
				+ ":";
		switch (tree.getKind()) {
		case METHOD:
			id += ((MethodTree) tree).getName();
			break;
		case NEW_ARRAY:
		case NEW_CLASS:
			id += tree.toString();
			break;
		default:
			// FIXME: may use hashCode() for the rest
			id += TreeUtils.elementFromUse(tree).toString();
		}
		return id;
	}
	
	public String getIdentifier(Element elt) {
		Tree decl = getDeclaration(elt);
		ExecutableElement currentMethod; 
		if (decl != null && (decl instanceof ExpressionTree) ) {
			return getIdentifier((ExpressionTree) decl);
		} else if (elt.getSimpleName().contentEquals("this") 
				&& (currentMethod = getCurrentMethodElt()) != null) {
			return THIS_PREFIX + getIdentifier(currentMethod);
		} else {
			String res = LIB_PREFIX + ElementUtils.enclosingClass(elt).getQualifiedName();
			if (elt.getKind() != ElementKind.FIELD) {
				res = res + "." + elt.getEnclosingElement().getSimpleName();
			}
			res = res + "." + elt.toString();
			return res;
		}
	}
	
	public String getName() {
		return this.getClass().getSimpleName().replace("Checker", "");
	}
	
	public boolean isAnnotated(AnnotatedTypeMirror type) {
		return isAnnotated(type.getAnnotations());
	}

	public boolean isAnnotated(Reference ref) {
		return isAnnotated(ref.getAnnotations());
	}
	
	public boolean isAnnotated(Set<AnnotationMirror> annos) {
        Set<AnnotationMirror> set = AnnotationUtils.createAnnotationSet();
        set.addAll(annos);
        set.retainAll(getSourceLevelQualifiers());
        return !set.isEmpty();
	}
	
	public Reference getAnnotatedReferenceByIdentifier(String identifier) {
		return annotatedReferences.get(identifier);
	}

    public Reference getAnnotatedReference(Tree t) {
    	String identifier = getIdentifier((ExpressionTree) t);
    	AnnotatedTypeMirror type = currentFactory.getAnnotatedType(t);
		TypeElement enclosingType = TreeUtils.elementFromDeclaration(
				TreeUtils.enclosingClass(currentFactory.getPath(t)));
    	Kind tKind = t.getKind();
    	Reference ret = null;
    	if (tKind == Kind.IDENTIFIER) {
    		ret = getAnnotatedReference(TreeUtils.elementFromUse((IdentifierTree) t));
    		return ret;
    	}
    	RefKind rk; 
    	switch (tKind) {
        case INT_LITERAL:
        case LONG_LITERAL:
        case FLOAT_LITERAL:
        case DOUBLE_LITERAL:
        case BOOLEAN_LITERAL:
        case CHAR_LITERAL:
        case STRING_LITERAL:
        case NULL_LITERAL:
        	rk = RefKind.LITERAL;
			break;
    	case NEW_ARRAY:
    	case NEW_CLASS:
    		rk = RefKind.ALLOCATION;
			break;
		default:
			rk = RefKind.LOCAL;
    	}
		ret = getAnnotatedReference(identifier, rk, t, null, enclosingType,
				type);
    	return ret;
    }

    public Reference getAnnotatedReference(Element elt) {
    	String identifier = getIdentifier(elt);
    	TypeElement enclosingType = ElementUtils.enclosingClass(elt);
    	AnnotatedTypeMirror type = currentFactory.getAnnotatedType(elt);
    	RefKind kind; 
    	switch (elt.getKind()) {
    	case PARAMETER:
    	case EXCEPTION_PARAMETER:
    		kind = RefKind.PARAMETER;
    		break;
    	case METHOD:
    	case CONSTRUCTOR:
    	case STATIC_INIT:
    	case INSTANCE_INIT:
    		kind = RefKind.METHOD;
    		break;
    	case FIELD:
    		kind = RefKind.FIELD;
    		break;
    	case CLASS:
    	case INTERFACE:
    	case ANNOTATION_TYPE:
    	case ENUM:
    		kind = RefKind.CLASS;
    		break;
		default:
    		kind = RefKind.LOCAL;
    	}
    	return getAnnotatedReference(identifier, kind, null, elt, enclosingType, type);
    }
    
	public Reference getAnnotatedReference(String identifier, RefKind kind, Tree tree,
			Element element, TypeElement enclosingType,
			AnnotatedTypeMirror type) {
		return getAnnotatedReference(identifier, kind, tree, element,
				enclosingType, type, type.getAnnotations());
	}
    
	public Reference getAnnotatedReference(String identifier, RefKind kind, Tree tree,
			Element element, TypeElement enclosingType,
			AnnotatedTypeMirror type, Set<AnnotationMirror> annos) {
    	Reference ret = annotatedReferences.get(identifier);
    	if (ret == null) {
    		switch (type.getKind()) {
    		case ARRAY:
				ret = new ArrayReference(identifier, kind, tree, element,
						enclosingType, type, type.getAnnotations());
    			AnnotatedTypeMirror componentType = ((AnnotatedArrayType) type).getComponentType();
    			String componentIdentifier = identifier + ARRAY_SUFFIX;
				Reference componentRef = getAnnotatedReference(
						componentIdentifier, RefKind.COMPONENT, null, null,
						enclosingType, componentType);
    			((ArrayReference) ret).setComponentRef(componentRef);
    			break;
    		case EXECUTABLE:
				ret = new ExecutableReference(identifier, tree, element,
						enclosingType, type, type.getAnnotations());
				ExecutableElement methodElt = (ExecutableElement) element;
				AnnotatedExecutableType methodType = (AnnotatedExecutableType) type;
				// THIS
				if (!ElementUtils.isStatic(methodElt)) {
					Reference thisRef = getAnnotatedReference(
							THIS_PREFIX + identifier, 
							RefKind.THIS, tree, element,
							enclosingType, methodType.getReceiverType());
					((ExecutableReference) ret).setThisRef(thisRef);
				}
				// RETURN
				AnnotatedTypeMirror returnType = (element.getKind() == ElementKind.CONSTRUCTOR ? 
						currentFactory.getAnnotatedType(enclosingType) : methodType.getReturnType());
                Reference returnRef = getAnnotatedReference(
                        RETURN_PREFIX + identifier, 
                        RefKind.RETURN, tree, element, 
                        enclosingType, returnType);
                ((ExecutableReference) ret).setReturnRef(returnRef);
				// PARAMETERS
				List<? extends VariableElement> parameters = methodElt.getParameters();
				List<Reference> paramRefs = new ArrayList<Reference>();
				for (VariableElement paramElt : parameters) {
					Reference paramRef = getAnnotatedReference(paramElt);
					paramRefs.add(paramRef);
				}
				((ExecutableReference) ret).setParamRefs(paramRefs);
    			break;
			default:
				ret = new Reference(identifier, kind, tree, element,
						enclosingType, type, type.getAnnotations());
    		}
    		// add default annotations
    		switch (kind) {
    		case FIELD:
    			annotateField(ret, element);
    			break;
    		case COMPONENT:
    			annotateArrayComponent(ret, element);
    			break;
    		case THIS:
    			annotateThis(ret, (ExecutableElement) element);
    			break;
    		case RETURN:
    			annotateReturn(ret, (ExecutableElement) element);
    			break;
    		case PARAMETER:
    			annotateParameter(ret, element);
    			break;
    		case FIELD_ADAPT:
    		case METH_ADAPT:
    		case CONSTANT:
    			// We don't want annotations
    			break;
    		default:
    			// Do default annotations
    			annotateDefault(ret, kind, element, tree);
    		}
    		annotatedReferences.put(identifier, ret);
    	}
    	return ret;
    }
	
	public Set<Constraint> getConstraints() {
		return constraints;
	}

    public Map<String, Reference> getAnnotatedReferences() {
		return annotatedReferences;
	}


	protected Reference getFieldAdaptReference(Reference context, Reference decl,
			Reference assignTo) {
        Reference av = createFieldAdaptReference(context, decl, assignTo);
        String identifier = av.getIdentifier();
        Reference ret = annotatedReferences.get(identifier);
        if (ret == null) {
            ret = av;
            annotatedReferences.put(identifier, ret);
        }
        return ret;
	}
    
    protected Reference getMethodAdaptReference(Reference context, Reference decl,
			Reference assignTo) {
        Reference av = createMethodAdaptReference(context, decl, assignTo);
        String identifier = av.getIdentifier();
        Reference ret = annotatedReferences.get(identifier);
        if (ret == null) {
            ret = av;
            annotatedReferences.put(identifier, ret);
        }
        return ret;
	}
	
	protected void addSubtypeConstraint(Reference sub, Reference sup) {
		if (sub.equals(sup)) 
			return;
        Constraint c = new SubtypeConstraint(sub, sup);
        debug(c.toString());
        if (!constraints.add(c))
            return;
        addComponentConstraints(sub, sup);
	}
	
	protected void addEqualityConstraint(Reference left, Reference right) {
		if (left.equals(right)) 
			return;
        Constraint c = new EqualityConstraint(left, right);
        if (!constraints.add(c))
            return;
        addComponentConstraints(left, right);
	}
	
	protected void addUnequalityConstraint(Reference left, Reference right) {
		if (left.equals(right)) 
			return;
        Constraint c = new UnequalityConstraint(left, right);
        if (!constraints.add(c))
            return;
        addComponentConstraints(left, right);

	}
	
    private void addComponentConstraints(Reference sub, Reference sup) {
        if (sub.getType() instanceof AnnotatedArrayType && sup instanceof AdaptReference) {
            sup = ((AdaptReference) sup).getDeclRef();
        } else if (sub instanceof AdaptReference && sup.getType() instanceof AnnotatedArrayType)
            sub = ((AdaptReference) sub).getDeclRef();

        if (sub.getType() instanceof AnnotatedArrayType && sup.getType() instanceof AnnotatedArrayType) {
            Reference subComponent = ((ArrayReference) sub).getComponentRef();
            Reference supComponent = ((ArrayReference) sup).getComponentRef();
            addEqualityConstraint(subComponent, supComponent);
        }
    }
    
    
	
	/**
	 * Generate constraints for method overriding
	 * @param overrider
	 * @param overridden
	 */
	protected void handleMethodOverride(ExecutableElement overrider, 
			ExecutableElement overridden) {
		ExecutableReference overriderRef = (ExecutableReference) getAnnotatedReference(overrider);
		ExecutableReference overriddenRef = (ExecutableReference) getAnnotatedReference(overridden);
		
		// THIS: overridden <: overrider 
		if (!ElementUtils.isStatic(overrider)) {
			Reference overriderThisRef = overriderRef.getThisRef();
			Reference overriddenThisRef = overriddenRef.getThisRef();
			addSubtypeConstraint(overriddenThisRef, overriderThisRef);
		}
		
		// RETURN: overrider <: overridden
		if (overrider.getReturnType().getKind() != TypeKind.VOID) {
	    	Reference overriderReturnRef = overriderRef.getReturnRef();
	    	Reference overriddenReturnRef = overriddenRef.getReturnRef();
			addSubtypeConstraint(overriderReturnRef, overriddenReturnRef);
		}
		
		// PARAMETERS: 
		Iterator<Reference> overriderIt = overriderRef.getParamRefs().iterator();
		Iterator<Reference> overriddenIt = overriddenRef.getParamRefs().iterator();
		for (; overriderIt.hasNext() && overriddenIt.hasNext(); ) {
			addSubtypeConstraint(overriderIt.next(), overriddenIt.next());
		}
	}

    protected void handleInstanceFieldRead(Reference aBase,
            Reference aField, Reference aLhs) {
        Reference afv = getFieldAdaptReference(aBase, aField, aLhs);
        addSubtypeConstraint(afv, aLhs);
    }

	protected void handleInstanceFieldWrite(Reference aBase,
            Reference aField, Reference aRhs) {
        Reference afv = getFieldAdaptReference(aBase, aField, aRhs);
        addSubtypeConstraint(aRhs, afv);
    }

    protected void handleStaticFieldRead(Reference aField, Reference aLhs) {
        addSubtypeConstraint(aField, aLhs);
    }

    protected void handleStaticFieldWrite(Reference aField, Reference aRhs) {
        addSubtypeConstraint(aRhs, aField);
    }
    
	protected void handleMethodCall(ExecutableElement invokeMethod,
			Reference receiverRef, Reference assignToRef, List<Reference> argumentRefs) {
		ExecutableReference methodRef = (ExecutableReference) getAnnotatedReference(invokeMethod);
        if (!ElementUtils.isStatic(invokeMethod)) {
            // receiver: y <: C |> this
            addSubtypeConstraint(receiverRef, 
            		getMethodAdaptReference(receiverRef, methodRef.getThisRef(), assignToRef));
        }
        // return: Here we used methodRef.getReturnRef().getType() to check VOID type         
        // because invokeMethod.getReturnType() would return VOID for constructors.
        // C |> ret <: x
        if (methodRef.getReturnRef().getType().getKind() != TypeKind.VOID) {
            if (assignToRef == null) {
            	error("Null assignTo in handleMethodCall: \n" + Thread.currentThread().getStackTrace().toString());
            }
            Reference returnRef = methodRef.getReturnRef();
            addSubtypeConstraint(getMethodAdaptReference(receiverRef, returnRef, assignToRef), assignToRef);
        }
        // parameters: z <: C |> p
        Iterator<Reference> argIt = argumentRefs.iterator();
        Iterator<Reference> paramIt = methodRef.getParamRefs().iterator();
        for (; argIt.hasNext() && paramIt.hasNext(); ) {
            Reference argRef = argIt.next();
            Reference paramRef = paramIt.next();
            addSubtypeConstraint(argRef, getMethodAdaptReference(receiverRef, paramRef, assignToRef));
        }
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

	public void printResult(PrintWriter out) {
		
	}
	
	public void printJaif(PrintWriter out) {
		
	}
	
	
    protected abstract Reference createFieldAdaptReference(Reference context, 
            Reference decl, Reference assignTo);

    protected abstract Reference createMethodAdaptReference(Reference context, 
            Reference decl, Reference assignTo);

    
    protected abstract void annotateDefault(Reference r, RefKind kind, Element elt, Tree t);

    protected abstract void annotateArrayComponent(Reference r, Element elt);

    protected abstract void annotateField(Reference r, Element fieldElt);

    protected abstract void annotateThis(Reference r, ExecutableElement methodElt);

    protected abstract void annotateParameter(Reference r, Element elt);

    protected abstract void annotateReturn(Reference r, ExecutableElement methodElt);
    
    
	public abstract AnnotationMirror adaptField(AnnotationMirror contextAnno,
			AnnotationMirror declAnno);

	public abstract AnnotationMirror adaptMethod(AnnotationMirror contextAnno,
			AnnotationMirror declAnno);
	
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
	
	public abstract Set<AnnotationMirror> getSourceLevelQualifiers();

	public abstract int getAnnotaionWeight(AnnotationMirror anno);
	
	public abstract boolean needCheckConflict();

	public boolean isSubtype(TypeElement a1, TypeElement a2) {
        // TODO
	    return (a1.equals(a2)
	            || types.isSubtype(types.erasure(a1.asType()),
	                    types.erasure(a2.asType())));
	}

}
