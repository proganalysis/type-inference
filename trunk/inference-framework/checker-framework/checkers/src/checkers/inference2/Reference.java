/**
 * 
 */
package checkers.inference2;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import checkers.inference2.InferenceChecker;
import checkers.inference.InferenceUtils;
import checkers.types.AnnotatedTypeMirror;
import checkers.util.AnnotationUtils;

import com.sun.source.tree.Tree;

/**
 * @author huangw5
 *
 */
public class Reference {
	
    public enum RefKind {
        LOCAL, 
        COMPONENT, 
        LITERAL, 
        FIELD, 
        PARAMETER,
        THIS, 
        RETURN, 
        CONSTANT,
        METHOD,
        CLASS, 
        ALLOCATION,
        METH_ADAPT, 
        FIELD_ADAPT
    } 	
    
    private static int counter = 0;
    
	/** The annotations for the Reference */
    private Set<AnnotationMirror> annotations;
    
    /** Not all Reference types have the element */
    private Element element;
    
    /** Most References have tree except the adapt Reference */
    private Tree tree;
    
    /** A unique number */
    private int id; 
    
    private RefKind kind;
    
    private String identifier;
    
    private String name;
    
    private AnnotatedTypeMirror type;

    private TypeElement enclosingType;
    
	public Reference(String identifier, RefKind kind, Tree tree,
			Element element, TypeElement enclosingType,
			AnnotatedTypeMirror type, Set<AnnotationMirror> annotations) {
		super();
		this.id = counter++;
		this.identifier = identifier;
		this.annotations = AnnotationUtils.createAnnotationSet();
		if (annotations != null) 
			this.annotations.addAll(annotations);
		this.element = element;
		this.name = (element != null ? element.toString() : (tree !=  null ? tree.toString() : "ADAPT" ));
		this.tree = tree;
        this.enclosingType = enclosingType;
		this.type = (type == null ? null : type.getCopy(false));
	}
	
	public Set<AnnotationMirror> getAnnotations(InferenceChecker checker) {
		Set<AnnotationMirror> set = getAnnotations();
		set.retainAll(checker.getSourceLevelQualifiers());
		return set;
	}

	public Set<AnnotationMirror> getAnnotations() {
		Set<AnnotationMirror> set = AnnotationUtils.createAnnotationSet();
		set.addAll(annotations);
		return set;
	}
	
	public void setAnnotations(Set<AnnotationMirror> annotations, InferenceChecker checker) {
		this.annotations.removeAll(checker.getSourceLevelQualifiers());
		this.annotations.addAll(annotations);
	}

	public void setAnnotations(Set<AnnotationMirror> annotations) {
		this.annotations.clear();
		this.annotations.addAll(annotations);
	}
	
    public void addAnnotation(AnnotationMirror anno) {
        this.annotations.add(anno);
    }

	
	public static int maxId() {
		return counter;
	}
	
	public String getName() {
		return name;
	}

	public Element getElement() {
		return element;
	}

	public Tree getTree() {
		return tree;
	}
	
	public RefKind getKind() {
		return kind;
	}

	public AnnotatedTypeMirror getType() {
		return type;
	}

    public TypeElement getEnclosingType() {
        return enclosingType;
    }

	public String getIdentifier() {
		return identifier;
	}
	
	public int getId() {
		return id;
	}

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
            return this.getIdentifier().equals(ref.getIdentifier());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return identifier.hashCode();
	}

	@Override
	public String toString() {
		return "(" + id + ")" + identifier;
	}
	
	public String toAnnotatedString() {
		return toString() + ": " + formatAnnotations();
	}
	
	public boolean isSimilar(Reference ref) {
		return false;
	}
	

//	public static class DeclaredReference extends Reference {
//		
//		List<? extends Reference> typeArgs = new ArrayList<Reference>(0);
//		
//	    boolean isGeneric = false;
//	
//		public DeclaredReference(String identifier, Tree tree, Element element,
//				TypeElement enclosingType, AnnotatedTypeMirror type,
//				Set<AnnotationMirror> annotations) {
//			super(identifier, null, tree, element, enclosingType, type, annotations);
//		} 
//		
//	    public void setTypeArguments(List<? extends Reference> ts) {
//	        typeArgs = Collections.unmodifiableList(new ArrayList<Reference>(ts));
//	    }
//	    
//	    public List<? extends Reference> getTypeArguments() {
//	        return typeArgs;
//	    }
//	
//		public boolean isGeneric() {
//			return isGeneric;
//		}
//	
//		public void setGeneric(boolean isGeneric) {
//			this.isGeneric = isGeneric;
//		}
//	    
//		@Override
//		public String toString() {
//			StringBuilder sb = new StringBuilder();
//			if (typeArgs != null && !typeArgs.isEmpty()) {
//				sb.append(":[");
//				boolean first = true;
//				for (Reference ts : typeArgs) {
//					if (first)
//						first = false;
//					else
//						sb.append(",");
//					sb.append(ts.toString());
//				}
//				sb.append("]");
//			}
//			return super.toString() + sb.toString();
//		}
//	}
	
	public static class ArrayReference extends Reference {
		
		Reference componentRef;
	
		public ArrayReference(String identifier, RefKind kind, Tree tree,
				Element element, TypeElement enclosingType,
				AnnotatedTypeMirror type, Set<AnnotationMirror> annotations) {
			super(identifier, kind, tree, element, enclosingType, type, annotations);
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
	}
	
	/**
	 * The annotations of the reference indicate the mutateStatic
	 * @author huangw5
	 *
	 */
	public static class ExecutableReference extends Reference {
		
	    Reference thisRef;
	    Reference returnRef;
	    List<Reference> paramRefs = new ArrayList<Reference>();
		
		public ExecutableReference(String identifier, Tree tree,
				Element element, TypeElement enclosingType,
				AnnotatedTypeMirror type, Set<AnnotationMirror> annotations) {
			super(identifier, RefKind.METHOD, tree, element, enclosingType, type, annotations);
		}
		
		public Reference getThisRef() {
			return thisRef;
		}
	
	
		public void setThisRef(Reference thisRef) {
			this.thisRef = thisRef;
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
	
		@Override
		public String toString() {
			return returnRef.toString()
					+ getElement().getSimpleName().toString()
					+ (paramRefs.isEmpty() ? "()" : "(" + paramRefs + ")") + " "
					+ thisRef;
		}
	}
	
	public static abstract class AdaptReference extends Reference {
		Reference contextRef;
		Reference declRef; 
		public AdaptReference(Reference contextRef, Reference declRef, RefKind kind) {
			super(contextRef.getIdentifier() + "|" + declRef.getIdentifier(),
					kind, null, null, null, null, AnnotationUtils.createAnnotationSet());
			this.declRef = declRef;
			this.contextRef = contextRef;
		}
		public Reference getContextRef() {
			return contextRef;
		}
		public Reference getDeclRef() {
			return declRef;
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
		
	}
	
	public static class FieldAdaptReference extends AdaptReference {
	
		public FieldAdaptReference(Reference contextRef, Reference declRef) {
			super(contextRef, declRef, RefKind.FIELD_ADAPT);
		}
	
		@Override
		public String toString() {
			return "(" + contextRef.toString() + " =f=> " + declRef.toString() + ")";
		}
		
		@Override
		public String toAnnotatedString() {
			return toString();
		}
	}
	
	public static class MethodAdaptReference extends AdaptReference {
	
		public MethodAdaptReference(Reference contextRef, Reference declRef) {
			super(contextRef, declRef, RefKind.METH_ADAPT);
		}
	
		@Override
		public String toString() {
			return "(" + contextRef.toString() + " =m=> " + declRef.toString() + ")";
		}
	
		@Override
		public String toAnnotatedString() {
			return toString();
		}
	}

}
