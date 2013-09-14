/**
 * 
 */
package checkers.inference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;

import checkers.types.AnnotatedTypeMirror;
import checkers.types.AnnotatedTypeMirror.AnnotatedArrayType;
import checkers.types.AnnotatedTypeMirror.AnnotatedDeclaredType;
import checkers.types.AnnotatedTypeMirror.AnnotatedExecutableType;
import checkers.types.AnnotatedTypeMirror.AnnotatedTypeVariable;
import checkers.types.AnnotatedTypeMirror.AnnotatedWildcardType;
import checkers.util.AnnotationUtils;
import checkers.util.TreeUtils;

import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.NewArrayTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.VariableTree;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeInfo;

/**
 * @author huangw5
 *
 */
public abstract class Reference {
    
    private static int counter = 0;
    
    /** A mapping from element to reference */
	private static transient Map<Element, Reference> elements = new HashMap<Element, Reference>();
	
	/** A mapping from tree (only NewClassTree and NewArrayTree) */
	private static transient Map<Tree, Reference> trees = new HashMap<Tree, Reference>();
	
	private static transient List<Reference> expRefs = new LinkedList<Reference>();
	
	public static void clearup() {
//		counter = 0;
		elements.clear();
		trees.clear();
		expRefs.clear();
	}
	
	public static Reference createConstantReference(AnnotationMirror anno) {
		Set<AnnotationMirror> annotations = AnnotationUtils.createAnnotationSet();
		annotations.add(anno);
		return createConstantReference(annotations, "");
	}
	
	public static Reference createConstantReference(AnnotationMirror anno, String name) {
		Set<AnnotationMirror> annotations = AnnotationUtils.createAnnotationSet();
		annotations.add(anno);
		return createConstantReference(annotations, name);
	}
	
	public static Reference createConstantReference(Set<AnnotationMirror> annos) {
		return createConstantReference(annos, "");
	}
	
	public static Reference createConstantReference(Set<AnnotationMirror> annos, String name) {
		ConstantReference ref = new ConstantReference(annos, name);
		// FIXME: this is bad
		expRefs.add(ref);
		return ref;
	}
	
	public static Reference createMethodAdaptReference(Reference contextRef, 
			Reference declRef) {
		return new MethodAdaptReference(contextRef, declRef);
	}
	
	public static Reference createFieldAdaptReference(Reference contextRef, 
			Reference declRef) {
		if (contextRef instanceof ExecutableReference || declRef instanceof ExecutableReference) 
			throw new RuntimeException("Shouldn't happen!");
		return new FieldAdaptReference(contextRef, declRef);
	}

	public static Reference createReference(Element element,
			InferenceAnnotatedTypeFactory factory) {
		Reference varRef = elements.get(element);
		if (varRef == null) {
			Tree decl = factory.getDeclaration(element);
			AnnotatedTypeMirror type = null;
			int offset = 0;
			if (decl != null) {
				type = factory.getAnnotatedType(decl);
				offset = TreeInfo.getStartPos((JCTree) decl);
				// FIXME: WEI comment it out for inserting annotations
				// It may affect other type inferences. 
//				if (decl instanceof MethodTree) 
//					decl = null; // We don't need method tree
			} else 
				type = factory.getAnnotatedType(element);
			
			varRef = createReferenceImpl(decl, element,
					factory.getFileName(element),
					factory.getLineNumber(element), offset, type, factory);
			elements.put(element, varRef);
		}
		return varRef;
    }
    
	public static Reference createReference(ExpressionTree tree,
			InferenceAnnotatedTypeFactory factory) {
    	// Create reference for new trees
    	if (tree.getKind() == Kind.NEW_ARRAY 
    			|| tree.getKind() == Kind.NEW_CLASS) {
    		Reference treeRef = trees.get(tree);
    		if (treeRef == null) {
				treeRef = createReferenceImpl(tree, null,
						factory.getFileName(tree), factory.getLineNumber(tree),
						TreeInfo.getStartPos((JCTree) tree),
						factory.getAnnotatedType(tree), factory);
    			trees.put(tree, treeRef);
    		}
			return treeRef;
    	}
    	
    	// For other trees
    	switch (tree.getKind()) {
    	case VARIABLE:
    		throw new RuntimeException("Should not happen!");
    	case METHOD:
    		Element methodElt = TreeUtils.elementFromDeclaration((MethodTree) tree);
    		Reference returnRef = elements.get(methodElt);
    		if (returnRef == null) {
				returnRef = createReferenceImpl(tree, methodElt,
						factory.getFileName(tree), factory.getLineNumber(tree),
						TreeInfo.getStartPos((JCTree) tree),
						factory.getAnnotatedType(tree), factory);
    			elements.put(methodElt, returnRef);
    			throw new RuntimeException("Should not happen");
    		}
    		return returnRef;
		default:
			return createReferenceImpl(tree, null, factory.getFileName(tree),
					factory.getLineNumber(tree),
					TreeInfo.getStartPos((JCTree) tree),
					factory.getAnnotatedType(tree), factory);
    	}
    }
    
    private static Reference createReferenceImpl(Tree tree, Element elt, String fileName,
			long lineNum, int offset, AnnotatedTypeMirror type, InferenceAnnotatedTypeFactory factory) {
		if (type.getKind() == TypeKind.ARRAY) {
			AnnotatedArrayType aType = (AnnotatedArrayType) type;
			ArrayReference aRef = new ArrayReference(tree, elt, fileName,
					lineNum, offset, aType, aType.getAnnotations());
			AnnotatedTypeMirror componentType = aType.getComponentType();
			aRef.setComponentRef(createReferenceImpl(null, null, fileName, lineNum, offset, componentType, factory));
			expRefs.add(aRef);
			return aRef;
		} else if (type.getKind() == TypeKind.DECLARED) {
			AnnotatedDeclaredType dType = (AnnotatedDeclaredType) type;
			DeclaredReference dRef = new DeclaredReference(tree, elt, fileName,
					lineNum, offset, type, dType.getAnnotations());
			dRef.setGeneric(dType.isGeneric());
			List<Reference> typeArgs = new ArrayList<Reference>();
			for (AnnotatedTypeMirror t : dType.getTypeArguments()) {
				typeArgs.add(createReferenceImpl(null, null, fileName, lineNum, offset, t, factory));
			}
			dRef.setTypeArguments(typeArgs);
			expRefs.add(dRef);
			return dRef;
		} else if (type.getKind() == TypeKind.EXECUTABLE) {
			AnnotatedExecutableType methodType = (AnnotatedExecutableType) type;
			ExecutableReference dRef = new ExecutableReference(tree, elt,
					fileName, lineNum, offset, type, type.getAnnotations());
			
			Reference rcvRef = createReferenceImpl(null, elt, fileName,
					lineNum, offset, methodType.getReceiverType(), factory);
			rcvRef.readableName = "THIS_" + elt.toString();
			dRef.setReceiverRef(rcvRef);
			
			Reference returnRef = createReferenceImpl(null, elt, fileName,
					lineNum, offset, methodType.getReturnType(), factory);
			returnRef.readableName = "RET_" + elt.toString();
			dRef.setReturnRef(returnRef);
			
			List<Reference> ps = new ArrayList<Reference>();
			List<? extends VariableElement> parameters = ((ExecutableElement) elt).getParameters();
			List<AnnotatedTypeMirror> parameterTypes = methodType.getParameterTypes();
			for (int i = 0; i < parameters.size(); i++) {
				Reference paramRef = elements.get(parameters.get(i));
				if (paramRef == null) {
					int paramOffset = offset;
					VariableTree paramTree = null;
					if (tree != null) {
						// Update the offset
						MethodTree mTree = (MethodTree) tree;
						paramTree = mTree.getParameters().get(i);
						paramOffset = TreeInfo.getStartPos((JCTree) paramTree);
					}
					paramRef = createReferenceImpl(paramTree,
							parameters.get(i), fileName,
							factory.getLineNumber(parameters.get(i)),
							paramOffset, parameterTypes.get(i), factory);
					elements.put(parameters.get(i), paramRef);
				}
				ps.add(paramRef);
				
			}
			dRef.setParamRefs(ps);
			List<Reference> ts = new ArrayList<Reference>();
			for (AnnotatedTypeMirror t : methodType.getTypeVariables()) {
				ts.add(createReferenceImpl(null, t.getElement(), fileName,
						lineNum, offset, t, factory));
			}
			dRef.setTypeVarTypes(ts);
			expRefs.add(dRef);
			return dRef;
		} else if (type.getKind() == TypeKind.TYPEVAR) {
			AnnotatedTypeVariable tType = (AnnotatedTypeVariable) type;
//			DeclaredReference dRef = new DeclaredReference(tree, elt, fileName,
//					lineNum, offset, type, tType.getEffectiveUpperBoundAnnotations());
			DeclaredReference dRef = new DeclaredReference(tree, elt, fileName,
					lineNum, offset, type, tType.getAnnotations());
			expRefs.add(dRef);
			return dRef;
		} else if (type.getKind() == TypeKind.WILDCARD) {
			AnnotatedWildcardType wType = (AnnotatedWildcardType) type;
//			DeclaredReference dRef = new DeclaredReference(tree, elt, fileName,
//					lineNum, offset, type, wType.getExtendsBound().getAnnotations());
			DeclaredReference dRef = new DeclaredReference(tree, elt, fileName,
					lineNum, offset, type, wType.getAnnotations());
			expRefs.add(dRef);
			return dRef;
		} else if (type.getKind().isPrimitive()) {
			PrimitiveReference dRef = new PrimitiveReference(tree, elt,
					fileName, lineNum, offset, type, type.getAnnotations());
			expRefs.add(dRef);
    		return dRef;
		} else if (type.getKind() == TypeKind.NULL) {
			return new NullReference();
		} else if (type.getKind() == TypeKind.VOID) {
			return new VoidReference();
		}
		throw new RuntimeException("ERROR: Unknown type " + type.getKind()
				+ " for " + tree);
    }
    
    public static List<Reference> getExpReferences() {
    	return new ArrayList<Reference>(expRefs);
    }
    
	/** The annotations for the Reference */
    protected Set<AnnotationMirror> annotations;
    
    /** Not all Reference types have the element */
    protected Element element;
    
    /** Most References have tree except the adapt Reference */
    protected Tree tree;
    
    /** A unique number */
    protected int id; 
    
    protected String fileName;
    
    protected long lineNum;
    
    protected int offset; 
    
    protected String readableName;
    
    protected AnnotatedTypeMirror type;
    
//    protected RefKind kind; 
    
	public Reference(Tree tree, Element element, String fileName,
			long lineNum, int offset, AnnotatedTypeMirror type,
			Set<AnnotationMirror> annotations) {
		super();
		this.id = counter++;
		this.annotations = AnnotationUtils.createAnnotationSet();
		if (annotations != null) 
			this.annotations.addAll(annotations);
		this.element = element;
		this.tree = tree;
		this.fileName = fileName;
		this.lineNum = lineNum;
		this.offset = offset;
		this.type = (type == null ? null : type.getCopy(false));
	}

	public Set<AnnotationMirror> getAnnotations() {
		Set<AnnotationMirror> set = AnnotationUtils.createAnnotationSet();
		set.addAll(annotations);
		return set;
	}

	public void setAnnotations(Set<AnnotationMirror> annotations) {
//		this.annotations = AnnotationUtils.createAnnotationSet();
		this.annotations.clear();
		this.annotations.addAll(annotations);
	}

	public Element getElement() {
		return element;
	}

	public Tree getTree() {
		return tree;
	}
	
	public AnnotatedTypeMirror getType() {
		return type;
	}
	

	public String getFileName() {
		return fileName;
	}

	public long getLineNum() {
		return lineNum;
	}

	public int getOffset() {
		return offset;
	}

	public String getIdentifier() {
		if (element != null && element.getKind() != ElementKind.LOCAL_VARIABLE
				&& element.getKind() != ElementKind.EXCEPTION_PARAMETER)
			return InferenceUtils.getElementSignature(element);
		else if (tree != null
				&& (tree instanceof NewClassTree
						|| tree instanceof VariableTree
						|| tree instanceof NewArrayTree 
						|| tree instanceof MemberSelectTree 
						|| tree instanceof MethodInvocationTree)) {
			return fileName + ":" + offset + ":" + tree.toString();
		}
		return null;
	}
	
	
	public int getId() {
		return id;
	}


//    public String getReadableName() {
//        return readableName;
//    }

	protected String formatAnnotations() {
		return "{" + InferenceUtils.formatAnnotationString(annotations) + "}";
	}
	
	protected String formatAnnotations(Set<AnnotationMirror> annos) {
		return "{" + InferenceUtils.formatAnnotationString(annos) + "}";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Reference) {
			Reference ref = (Reference) obj;
            return this.getFullRefName().equals(ref.getFullRefName());
//            if (!this.getRefName().equals(ref.getRefName()))
//                return false;
//            Element elt = ref.getElement();
//            if (this.element != null && elt != null)
//                return (element.equals(elt));
//            Tree t = ref.getTree();
//            if (this.tree != null && t != null) 
//                return (this.tree.equals(t));
		}
		return false;
	}


//	@Override
//	public int hashCode() {
//		return id;
//	}

	@Override
	public String toString() {
		String prefix = "";
		if (fileName != null) {
			int lastIndexOf = fileName.lastIndexOf('/');
			int index = lastIndexOf == -1 ? 0 : lastIndexOf + 1;
			prefix = fileName.substring(index) + ":" + lineNum;
		}
		prefix += "(" + getId() + ")";
		return prefix + ":" + getRefName();
	}
	
	public String getRefName() {
		if (readableName != null) {
			return readableName;
		} else if (element != null) {
			return "VAR_" + element;
		} else if (tree != null) {
			return "EXP_" + tree.toString();
		} else
			return "#INTERNAL#";
	}
	
	public String getFullRefName() {
		return fileName + ":" + lineNum + ":" + offset + ":" + getRefName();
	}
	
	public String toAnnotatedString() {
		return toString() + formatAnnotations();
	}
	
	public abstract Reference getCopy();
	
	public abstract void mergeAnnotations(Reference ref);
	
	public abstract boolean isSimilar(Reference ref);
	
	public AnnotatedTypeMirror getAnnotatedType() {
		if (type == null)
			return null;
		InferenceUtils.annotateReferenceType(type, this);
		return type;
	}
	

public static class DeclaredReference extends Reference {
	
	List<? extends Reference> typeArgs = new ArrayList<Reference>(0);
	
    boolean isGeneric = false;

	public DeclaredReference(Tree tree, Element element,
			String fileName, long lineNum, int offset,
			AnnotatedTypeMirror type, Set<AnnotationMirror> annotations) {
		super(tree, element, fileName, lineNum, offset, type, annotations);
	} 
	
    public void setTypeArguments(List<? extends Reference> ts) {
        typeArgs = Collections.unmodifiableList(new ArrayList<Reference>(ts));
    }
    
    public List<? extends Reference> getTypeArguments() {
        return typeArgs;
    }

	public boolean isGeneric() {
		return isGeneric;
	}

	public void setGeneric(boolean isGeneric) {
		this.isGeneric = isGeneric;
	}
    
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (typeArgs != null && !typeArgs.isEmpty()) {
			sb.append(":[");
			boolean first = true;
			for (Reference ts : typeArgs) {
				if (first)
					first = false;
				else
					sb.append(",");
				sb.append(ts.toString());
			}
			sb.append("]");
		}
		return super.toString() + sb.toString();
	}

	@Override
	public Reference getCopy() {
		DeclaredReference copy = new DeclaredReference(tree, element, fileName,
				lineNum, offset, type, getAnnotations());
		copy.setGeneric(isGeneric);
		if (typeArgs != null) {
			List<Reference> ts = new ArrayList<Reference>(typeArgs.size());
			for (Reference ref : typeArgs)
				ts.add(ref.getCopy());
			copy.setTypeArguments(ts);
		}
		copy.id = this.getId();
		copy.readableName = readableName;
		return copy;
	}

	@Override
	public void mergeAnnotations(Reference ref) {
		// Cannot merge if these are two different references
		String identifier = this.getIdentifier();
		if (identifier != null && !identifier.equals(ref.getIdentifier()))
			return;
		if (!(ref instanceof DeclaredReference))
			return;
		DeclaredReference dRef = (DeclaredReference) ref;
		this.annotations.addAll(dRef.getAnnotations());
		if (typeArgs != null && dRef.getTypeArguments() != null) {
				for (Iterator<? extends Reference> it1 = typeArgs.iterator(); 
						it1.hasNext();) {
					for (Iterator<? extends Reference> it2 
							= dRef.getTypeArguments().iterator(); it2.hasNext();) {
						it1.next().mergeAnnotations(it2.next());
					}
				}
		}
	}

	@Override
	public boolean isSimilar(Reference ref) {
		if (ref instanceof DeclaredReference) {
			DeclaredReference dRef = (DeclaredReference) ref;
			if (!this.fileName.equals(dRef.getFileName()))
				return false;
			if(!this.getRefName().equals(dRef.getRefName()) 
				|| this.tree != null && this.tree instanceof LiteralTree 
					&& ref.tree != null && ref.tree instanceof LiteralTree) {
				return false;
			}
			if (typeArgs != null && dRef.getTypeArguments() != null) {
				for (Iterator<? extends Reference> it1 = typeArgs
						.iterator(); it1.hasNext();) {
					for (Iterator<? extends Reference> it2 = dRef
							.getTypeArguments().iterator(); it2.hasNext();) {
						if (!it1.next().isSimilar(it2.next()))
							return false;
					}
				}
			}
			if (annotations.equals(ref.getAnnotations()))
				return true;
		}
		return false;
	}
}

public static class ArrayReference extends Reference {
	
	Reference componentRef;

	public ArrayReference(Tree tree, Element element, String fileName,
			long lineNum, int offset, AnnotatedTypeMirror type,
			Set<AnnotationMirror> annotations) {
		super(tree, element, fileName, lineNum, offset, type, annotations);
	}

	public Reference getComponentRef() {
		return componentRef;
	}

	public void setComponentRef(Reference componentRef) {
		this.componentRef = componentRef;
	}
	
	@Override
	public String toString() {
		return super.toString() + "[" + componentRef.toString() + "]";
	}

	@Override
	public Reference getCopy() {
		ArrayReference copy = new ArrayReference(tree, element, fileName,
				lineNum, offset, type, getAnnotations());
		copy.setComponentRef(componentRef.getCopy());
		copy.id = (this.getId());
		return copy;
	}

	@Override
	public void mergeAnnotations(Reference ref) {
		String identifier = this.getIdentifier();
		if (identifier != null && !identifier.equals(ref.getIdentifier()))
			return;
		if (!(ref instanceof ArrayReference))
			return;
		this.annotations.addAll(ref.getAnnotations());
		componentRef.mergeAnnotations(((ArrayReference) ref).getComponentRef());
	}

	@Override
	public boolean isSimilar(Reference ref) {
		if (ref instanceof ArrayReference) {
			ArrayReference aref = (ArrayReference) ref;
			if (this.getRefName().equals(aref.getRefName())
					&& this.annotations.equals(aref.getAnnotations())
					&& this.fileName.equals(aref.getFileName())
					&& this.getComponentRef().isSimilar((aref.getComponentRef())))
				return true;
		}
		return false;
	}
}

/**
 * The annotations of the reference indicate the mutateStatic
 * @author huangw5
 *
 */
public static class ExecutableReference extends Reference {
	
    Reference receiverRef;
    Reference returnRef;
    List<Reference> paramRefs = new ArrayList<Reference>(0);
	List<Reference> typeVarTypes = new ArrayList<Reference>(0);
	
	public ExecutableReference(Tree tree, Element element, String fileName,
			long lineNum, int offset, AnnotatedTypeMirror type,
			Set<AnnotationMirror> annotations) {
		super(tree, element, fileName, lineNum, offset, type, annotations);
	}
	
	
	public Reference getReceiverRef() {
		return receiverRef;
	}


	public void setReceiverRef(Reference receiverRef) {
		this.receiverRef = receiverRef;
	}


	public Reference getReturnRef() {
		return returnRef;
	}


	public void setReturnRef(Reference returnRef) {
		this.returnRef = returnRef;
	}


	public List<Reference> getParamRefs() {
		return paramRefs;
	}


	public void setParamRefs(List<Reference> paramRefs) {
		this.paramRefs = paramRefs;
	}


	public List<Reference> getTypeVarTypes() {
		return typeVarTypes;
	}


	public void setTypeVarTypes(List<Reference> typeVarTypes) {
		this.typeVarTypes = typeVarTypes;
	}


	@Override
	public Reference getCopy() {
		ExecutableReference copy = new ExecutableReference(tree, element,
				fileName, lineNum, offset, type, getAnnotations());
		copy.setReceiverRef(receiverRef.getCopy());
		copy.setReturnRef(returnRef.getCopy());
		ArrayList<Reference> ps = new ArrayList<Reference>(paramRefs.size());
		for (Reference paramRef : paramRefs)
			ps.add(paramRef);
		copy.setParamRefs(ps);
		ArrayList<Reference> ts = new ArrayList<Reference>(typeVarTypes.size());
		for (Reference typeVarRef : typeVarTypes)
			ts.add(typeVarRef);
		copy.setTypeVarTypes(ts);
		copy.id = (this.getId());
		copy.readableName = readableName;
		return copy;
	}

	
	@Override
	public void mergeAnnotations(Reference ref) {
		String identifier = this.getIdentifier();
		if (identifier != null && !identifier.equals(ref.getIdentifier()))
			return;
		if (!(ref instanceof ExecutableReference))
			return;
		ExecutableReference eRef = (ExecutableReference) ref;
		this.annotations.addAll(eRef.getAnnotations());
		receiverRef.mergeAnnotations(eRef.getReceiverRef());
		returnRef.mergeAnnotations(eRef.getReturnRef());
		//FIXME: Skip merging parameters? 
//		for (Iterator<Reference> it1 = paramRefs.iterator(); it1.hasNext();) {
//			for (Iterator<Reference> it2 = eRef.getParamRefs().iterator(); 
//					it2.hasNext();) {
//				it1.next().mergeAnnotations(it2.next());
//			}
//		}
	}

	@Override
	public String toString() {
		return returnRef.toString()
				+ (typeVarTypes.isEmpty() ? " " : "<" + typeVarTypes + ">")
				+ element.getSimpleName().toString()
				+ (paramRefs.isEmpty() ? "()" : "(" + paramRefs + ")") + " "
				+ receiverRef;
//				+ " extra: " + formatAnnotations();
	}


	@Override
	public boolean isSimilar(Reference ref) {
		// TODO Auto-generated method stub
		return false;
	}


}

public static class PrimitiveReference extends Reference {
	
	public PrimitiveReference(Tree tree, Element element, String fileName,
			long lineNum, int offset, AnnotatedTypeMirror type,
			Set<AnnotationMirror> annotations) {
		super(tree, element, fileName, lineNum, offset, type, annotations);
	}
	
	@Override
	public String toString() {
		return super.toString();
	}

	@Override
	public Reference getCopy() {
		PrimitiveReference copy = new PrimitiveReference(tree, element,
				fileName, lineNum, offset, type, this.getAnnotations());
		copy.id = (this.getId());
		copy.readableName = readableName;
		return copy;
	}

	@Override
	public void mergeAnnotations(Reference ref) {
		String identifier = this.getIdentifier();
		if (identifier != null && !identifier.equals(ref.getIdentifier()))
			return;
		if (!(ref instanceof PrimitiveReference))
			return;
		this.annotations.addAll(ref.getAnnotations());
	}

	@Override
	public boolean isSimilar(Reference ref) {
		if (ref instanceof PrimitiveReference) {
			return this.fileName.equals(ref.getFileName())
					&& (this.getRefName().equals(ref.getRefName())
						|| this.tree != null && this.tree instanceof LiteralTree 
							&& ref.tree != null && ref.tree instanceof LiteralTree)
					&& this.annotations.equals(ref.getAnnotations());
		}
		return false;
	}

}

public static abstract class AdaptReference extends Reference {
	Reference contextRef;
	Reference declRef; 
	public AdaptReference(Reference contextRef, Reference declRef) {
		super(null, null, null, 0, 0, null, AnnotationUtils.createAnnotationSet());
		this.declRef = declRef;
		this.contextRef = contextRef;
	}
	public Reference getContextRef() {
		return contextRef;
	}
	public Reference getDeclRef() {
		return declRef;
	} 
	public void setContextRef(Reference ref) {
		contextRef = ref;
	}
	public void setDeclRef(Reference ref) {
		declRef = ref;
	} 

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AdaptReference) {
			AdaptReference ref = (AdaptReference) obj;
			return contextRef.equals(ref.contextRef) 
					&& declRef.equals(ref.declRef);
		}
		return false;
	}
	@Override
	public boolean isSimilar(Reference ref) {
		if (ref instanceof AdaptReference) {
			AdaptReference aref = (AdaptReference) ref;
            return this.declRef.isSimilar(aref.getDeclRef())
                && this.contextRef.isSimilar(aref.getContextRef());
		}
		return false;
	}
	
}

public static class FieldAdaptReference extends AdaptReference {

	public FieldAdaptReference(Reference contextRef, Reference declRef) {
		super(contextRef, declRef);
	}

    public void setTree(Tree tree) {
        this.tree = tree;
    }
	
	@Override
	public String toString() {
		return "(" + contextRef.toString() + " =f=> " + declRef.toString() + ")";
	}
    @Override
	public String getRefName() {
        return "(" + contextRef.getRefName() + " =f=> " + declRef.getRefName() + ")";
	}
	
	@Override
	public String toAnnotatedString() {
		return "(" + contextRef.toAnnotatedString() + " =f=> " + declRef.toAnnotatedString() + ")";
	}

    @Override
	public String getFullRefName() {
		return "(" + contextRef.getFullRefName() + " =f=> " + declRef.getFullRefName() + ")";
	}

	@Override
	public Reference getCopy() {
		FieldAdaptReference copy = new FieldAdaptReference(contextRef, declRef);
		copy.id = (this.getId());
		copy.readableName = readableName;
		return copy; 
	}

	@Override
	public void mergeAnnotations(Reference ref) {
		// SKIP
	}

}

public static class MethodAdaptReference extends AdaptReference {

	public MethodAdaptReference(Reference contextRef, Reference declRef) {
		super(contextRef, declRef);
	}

    public void setTree(Tree tree) {
        this.tree = tree;
    }
	
	@Override
	public String toString() {
		return "(" + contextRef.toString() + " =m=> " + declRef.toString() + ")";
	}

    @Override
	public String getRefName() {
        return "(" + contextRef.getRefName() + " =m=> " + declRef.getRefName() + ")";
	}

    @Override
	public String getFullRefName() {
		return "(" + contextRef.getFullRefName() + " =m=> " + declRef.getFullRefName() + ")";
	}
	
	@Override
	public String toAnnotatedString() {
		return "(" + contextRef.toAnnotatedString() + " =m=> " + declRef.toAnnotatedString() + ")";
	}

	@Override
	public Reference getCopy() {
		MethodAdaptReference copy = new MethodAdaptReference(contextRef, declRef);
		copy.id = (this.getId());
		copy.readableName = readableName;
		return copy;
	}

	@Override
	public void mergeAnnotations(Reference ref) {
		// SKIP
	}

}

public static class VoidReference extends Reference {
	
	public VoidReference() {
		super(null, null, null, 0, 0, null, AnnotationUtils.createAnnotationSet());
	}

	@Override
	public Reference getCopy() {
		return new VoidReference();
	}

	@Override
	public void mergeAnnotations(Reference ref) {
		// SKIP
	}

	@Override
	public boolean isSimilar(Reference ref) {
		// TODO Auto-generated method stub
		return true;
	}

}

public static class NullReference extends Reference {
	
	public NullReference() {
		super(null, null, null, 0, 0, null, AnnotationUtils.createAnnotationSet());
	}

	@Override
	public Reference getCopy() {
		return new NullReference();
	}

	@Override
	public void mergeAnnotations(Reference ref) {
		// SKIP
	}

	@Override
	public boolean isSimilar(Reference ref) {
		// TODO Auto-generated method stub
		return true;
	}
	
}

public static class ConstantReference extends Reference {
	
	public ConstantReference(Set<AnnotationMirror> annotations, String name) {
		super(null, null, null, 0, 0, null, annotations);
		this.readableName = name;
	}
	
	@Override
	public String toString() {
		return getId() + ":" + "#CONSTANT#" + readableName;
	}

	@Override
	public Reference getCopy() {
		Reference copy = new ConstantReference(getAnnotations(), readableName);
		copy.id = (this.getId());
		copy.readableName = readableName;
		return copy;
	}

	@Override
	public void mergeAnnotations(Reference ref) {
		String identifier = this.getIdentifier();
		if (identifier != null && !identifier.equals(ref.getIdentifier()))
			return;
		if (!(ref instanceof ConstantReference))
			return;
		this.annotations.addAll(ref.getAnnotations());
	}

	@Override
	public boolean isSimilar(Reference ref) {
		if (ref instanceof ConstantReference) {
			return this.getRefName().equals(ref.getRefName()) 
					&& this.fileName.equals(ref.getFileName())
					&& this.annotations.equals(ref.getAnnotations());
		}
		return false;
	}
}

}
