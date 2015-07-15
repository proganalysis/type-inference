/**
 * 
 */
package checkers.inference2;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import checkers.inference2.InferenceChecker;
import checkers.inference2.Constraint;
import checkers.inference2.InferenceUtils;
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
        NULL,
        FIELD, 
        PARAMETER,
        THIS, 
        RETURN, 
        CONSTANT,
        METHOD,
        CLASS, 
        ALLOCATION,
        CALL_SITE,
        METH_ADAPT, 
        FIELD_ADAPT
    } 	
    
    private static int counter = 0;
        
	/** The annotations for the Reference */
    private Set<AnnotationMirror> annotations;
    
    private AnnotationMirror cryptType;
    
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
    
    private String fileName;
    
    private int lineNum;
    
    /** For adding linear constraints*/
    protected Set<Constraint> lessSet = new LinkedHashSet<Constraint>();

    protected Set<Constraint> greaterSet = new LinkedHashSet<Constraint>();
    
	public Reference(String identifier, RefKind kind, Tree tree,
			Element element, TypeElement enclosingType,
			AnnotatedTypeMirror type, Set<AnnotationMirror> annotations, long pos) {
		super();
		this.id = counter++;
		this.identifier = identifier;
		this.annotations = AnnotationUtils.createAnnotationSet();
		if (annotations != null) 
			this.annotations.addAll(annotations);
		this.element = element;
		this.tree = tree;
        this.enclosingType = enclosingType;
		this.type = (type == null ? null : type.getCopy(false));
		this.kind = kind;
		this.cryptType = null;
		String[] split = identifier.split(":");
		if (split.length == 3) {
			fileName = split[0];
			lineNum = Integer.valueOf(split[1]);
			name = split[2];
		} else {
			fileName = identifier;
			lineNum = -1;
			name = (element != null ? element.toString() : (tree != null ? tree
					.toString() : identifier));
		}
	}
	
	public Set<AnnotationMirror> getAnnotations(InferenceChecker checker) {
		Set<AnnotationMirror> set = getRawAnnotations();
		set.retainAll(checker.getSourceLevelQualifiers());
		return set;
	}
	
	public AnnotationMirror getCryptType() {
		return this.cryptType;
	}

	public int getLineNum() {
		return lineNum;
	}

	public void setLineNum(int lineNum) {
		this.lineNum = lineNum;
	}

	public Set<AnnotationMirror> getRawAnnotations() {
		Set<AnnotationMirror> set = AnnotationUtils.createAnnotationSet();
		set.addAll(annotations);
		return set;
	}
	
	public void setAnnotations(Set<AnnotationMirror> annotations, InferenceChecker checker) {
		this.annotations.removeAll(checker.getSourceLevelQualifiers());
		this.annotations.addAll(annotations);
	}

	public void setRawAnnotations(Set<AnnotationMirror> annotations) {
		this.annotations.clear();
		this.annotations.addAll(annotations);
	}
	
    public void addAnnotation(AnnotationMirror anno) {
        this.annotations.add(anno);
    }
    
    public void setCryptType(AnnotationMirror type) {
    	this.cryptType = type;
    }
    
	public static int maxId() {
		return counter;
	}
	
	public void setRefKind(RefKind k) {
		kind = k;
	}
	
	public String getFileName() {
		return fileName;
	}

	public String getName() {
		return name;
	}

	public Element getElement() {
		return element;
	}
	
	public void addLessConstraint(Constraint c) {
        lessSet.add(c);
    }

    public void addGreaterConstraint(Constraint c) {
        greaterSet.add(c);
    }
	
	public Set<Constraint> getLessSet() {
        return lessSet;
    }

    public Set<Constraint> getGreaterSet() {
        return greaterSet;
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
		return "(" + id + ")" + identifier + " " + formatAnnotations();
	}
	
	public String toAnnotatedString() {
		return toString() + ": " + formatAnnotations();
	}
	
	public boolean isSimilar(Reference ref) {
		return false;
	}
	
	public static class ArrayReference extends Reference {
		
		Reference componentRef;
	
		public ArrayReference(String identifier, RefKind kind, Tree tree,
				Element element, TypeElement enclosingType,
				AnnotatedTypeMirror type, Set<AnnotationMirror> annotations, long pos) {
			super(identifier, kind, tree, element, enclosingType, type, annotations, pos);
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
				AnnotatedTypeMirror type, Set<AnnotationMirror> annotations, long pos) {
			super(identifier, RefKind.METHOD, tree, element, enclosingType, type, annotations, pos);
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
			return returnRef.toString() + " "
					+ getElement().getSimpleName().toString() + " "
					+ (paramRefs.isEmpty() ? "()" : "(" + paramRefs + ")") + " "
					+ thisRef;
		}
	}
	
	public static abstract class AdaptReference extends Reference {
		Reference contextRef;
		Reference declRef; 
		public AdaptReference(Reference contextRef, Reference declRef, RefKind kind) {
			super(contextRef.getIdentifier() + "|" + declRef.getIdentifier(),
					kind, null, null, null, null, AnnotationUtils.createAnnotationSet(), 0);
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
		public Set<AnnotationMirror> getAnnotations(InferenceChecker checker) {
			return checker.adaptFieldSet(contextRef.getAnnotations(checker),
					declRef.getAnnotations(checker));
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
		public Set<AnnotationMirror> getAnnotations(InferenceChecker checker) {
			return checker.adaptMethodSet(contextRef.getAnnotations(checker),
					declRef.getAnnotations(checker));
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
