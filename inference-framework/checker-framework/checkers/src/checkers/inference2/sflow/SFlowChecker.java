/**
 * 
 */
package checkers.inference2.sflow;

import static com.esotericsoftware.minlog.Log.info;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import checkers.inference.reim.quals.Mutable;
import checkers.inference.reim.quals.Polyread;
import checkers.inference.reim.quals.Readonly;
import checkers.inference.sflow.quals.Bottom;
import checkers.inference.sflow.quals.Poly;
import checkers.inference.sflow.quals.Safe;
import checkers.inference.sflow.quals.Tainted;
import checkers.inference2.Constraint;
import checkers.inference2.ConstraintSolver.FailureStatus;
import checkers.inference2.InferenceChecker;
import checkers.inference2.InferenceVisitor;
import checkers.inference2.Reference;
import checkers.inference2.Reference.AdaptReference;
import checkers.inference2.Reference.ExecutableReference;
import checkers.inference2.Reference.FieldAdaptReference;
import checkers.inference2.Reference.MethodAdaptReference;
import checkers.inference2.Reference.RefKind;
import checkers.quals.TypeQualifiers;
import checkers.source.SourceVisitor;
import checkers.types.AnnotatedTypeMirror;
import checkers.types.AnnotatedTypeMirror.AnnotatedDeclaredType;
import checkers.util.AnnotationUtils;
import checkers.util.ElementUtils;
import checkers.util.TreeUtils;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree;

/**
 * @author huangw5
 *
 */
@SupportedOptions({ "warn", "infer", "debug", "noReim", "inferLibrary", "polyLibrary", "inferAndroidApp" })
@TypeQualifiers({ Readonly.class, Polyread.class, Mutable.class, Poly.class,
		Tainted.class, Safe.class, Bottom.class })
public class SFlowChecker extends InferenceChecker {
	
	
	public AnnotationMirror READONLY, POLYREAD, MUTABLE, POLY, TAINTED, SAFE;
//			BOTTOM;	
	
	private Set<AnnotationMirror> sourceAnnos;

	private List<Pattern> specialMethodPatterns = null;
	
	private boolean useReim = true;
	
	private boolean polyLibrary = true;
	
	private boolean inferLibrary = false;
	
    private boolean inferAndroidApp = false;

	private AnnotationUtils annoFactory;

    private Set<String> defaultReadonlyRefTypes;

    private Set<String> androidClasses; 


	public void initChecker(ProcessingEnvironment processingEnv) {
		super.initChecker(processingEnv);
		annoFactory = AnnotationUtils.getInstance(env);
		POLY = annoFactory.fromClass(Poly.class);
		TAINTED = annoFactory.fromClass(Tainted.class);
		SAFE = annoFactory.fromClass(Safe.class);
//		BOTTOM = annoFactory.fromClass(Bottom.class);

		READONLY = annoFactory.fromClass(Readonly.class);
		POLYREAD = annoFactory.fromClass(Polyread.class);
		MUTABLE = annoFactory.fromClass(Mutable.class);

		sourceAnnos = new HashSet<AnnotationMirror>(4);
		sourceAnnos.add(TAINTED);
		sourceAnnos.add(POLY);
		sourceAnnos.add(SAFE);

        defaultReadonlyRefTypes = new HashSet<String>();
        defaultReadonlyRefTypes.add("java.lang.String");
        defaultReadonlyRefTypes.add("java.lang.Boolean");
        defaultReadonlyRefTypes.add("java.lang.Byte");
        defaultReadonlyRefTypes.add("java.lang.Character");
        defaultReadonlyRefTypes.add("java.lang.Double");
        defaultReadonlyRefTypes.add("java.lang.Float");
        defaultReadonlyRefTypes.add("java.lang.Integer");
        defaultReadonlyRefTypes.add("java.lang.Long");
        defaultReadonlyRefTypes.add("java.lang.Number");
        defaultReadonlyRefTypes.add("java.lang.Short");
        defaultReadonlyRefTypes.add("java.util.concurrent.atomic.AtomicInteger");
        defaultReadonlyRefTypes.add("java.util.concurrent.atomic.AtomicLong");
        defaultReadonlyRefTypes.add("java.math.BigDecimal");
        defaultReadonlyRefTypes.add("java.math.BigInteger");

		specialMethodPatterns = new ArrayList<Pattern>(5);
		specialMethodPatterns.add(Pattern
				.compile(".*\\.equals\\(java\\.lang\\.Object\\)$"));
		specialMethodPatterns.add(Pattern.compile(".*\\.hashCode\\(\\)$"));
		specialMethodPatterns.add(Pattern.compile(".*\\.toString\\(\\)$"));
		specialMethodPatterns.add(Pattern.compile(".*\\.compareTo\\(.*\\)$"));
		
        androidClasses = new HashSet<String>();
        androidClasses.add("android.app.Activity");
        androidClasses.add("android.app.Service");
        androidClasses.add("android.location.LocationListener");
		
		
		if (getProcessingEnvironment().getOptions().containsKey(
				"inferLibrary")) {
			inferLibrary = true;
		}
		if (getProcessingEnvironment().getOptions().containsKey(
				"polyLibrary")) {
			polyLibrary = true;
		}
		if (getProcessingEnvironment().getOptions().containsKey(
				"noReim")) {
			useReim = false;
		}
		if (getProcessingEnvironment().getOptions().containsKey(
				"inferAndroidApp")) {
            inferAndroidApp = true;
		}

        if (DEBUG) {
            info("useReim = " + useReim);
            info("polyLibrary = " + polyLibrary);
            info("inferLibrary = " + inferLibrary);
            info("inferAndroidApp = " + inferAndroidApp);
        }
	}

	public boolean isUseReim() {
		return useReim;
	}

	public boolean isInferLibrary() {
		return inferLibrary;
	}

	public boolean isPolyLibrary() {
		return polyLibrary;
	}

    public boolean isInferAndroidApp() {
        return inferAndroidApp; 
    }

	public boolean isDefaultReadonlyType(AnnotatedTypeMirror t) {
        if (t.getKind().isPrimitive())
            return true;
		TypeElement elt = null;
		if (t.getKind() == TypeKind.DECLARED) {
			AnnotatedDeclaredType dt = (AnnotatedDeclaredType) t;
	        elt = (TypeElement)dt.getUnderlyingType().asElement();
		}
        if (elt != null &&
        		defaultReadonlyRefTypes.contains(elt.getQualifiedName().toString()))
            return true;
        return false;
    }
	

    public Element getEnclosingMethod(Element elt) {
        while (elt != null && elt.getKind() != ElementKind.METHOD
                && elt.getKind() != ElementKind.CONSTRUCTOR) {
            elt = elt.getEnclosingElement();
        }
        return elt;
    }
	
    /**
     * Check if this constraint connects param/ret
     * @param c
     * @return
     */
    public boolean isParamReturnConstraint(Constraint c) {
        Reference left = c.getLeft();
        Reference right = c.getRight();
        if (isParamOrRetRef(left) && isParamOrRetRef(right)) {
            Element lElt = null;
            Element rElt = null;
            // check if they are from the same method
            lElt = getEnclosingMethod(left.getElement());
            rElt = getEnclosingMethod(right.getElement());
            if (lElt.equals(rElt))
                return true;
        }
        return false;
    }

    /**
     * Check if this ref is param/ret/this
     * @param ref
     * @return
     */
    public boolean isParamOrRetRef(Reference ref) {
        if (ref != null && !(ref instanceof AdaptReference)) {
        	RefKind kind = ref.getKind();
        	return kind == RefKind.PARAMETER 
        			|| kind == RefKind.THIS
        			|| kind == RefKind.RETURN;
        }
        return false;
    }

    @Override
	protected void handleMethodOverride(ExecutableElement overrider, 
			ExecutableElement overridden) {
		ExecutableReference overriderRef = (ExecutableReference) getAnnotatedReference(overrider);
		ExecutableReference overriddenRef = (ExecutableReference) getAnnotatedReference(overridden);
		
		// THIS: overridden <: overrider 
		if (!ElementUtils.isStatic(overrider)) {
			Reference overriderThisRef = overriderRef.getThisRef();
			Reference overriddenThisRef = overriddenRef.getThisRef();
			if (!isFromLibrary(overridden) || isAnnotated(overriddenThisRef)) {
				addSubtypeConstraint(overriddenThisRef, overriderThisRef);
			}
		}
		
		// RETURN: overrider <: overridden
		if (overrider.getReturnType().getKind() != TypeKind.VOID) {
	    	Reference overriderReturnRef = overriderRef.getReturnRef();
	    	Reference overriddenReturnRef = overriddenRef.getReturnRef();
			if (!isFromLibrary(overridden) || isAnnotated(overriddenReturnRef)) {
				addSubtypeConstraint(overriderReturnRef, overriddenReturnRef);
			}
		}
		
		// PARAMETERS: 
		Iterator<Reference> overriderIt = overriderRef.getParamRefs().iterator();
		Iterator<Reference> overriddenIt = overriddenRef.getParamRefs().iterator();
		for (; overriderIt.hasNext() && overriddenIt.hasNext(); ) {
                Reference oerriddenParam = overriddenIt.next();
			if (!isFromLibrary(overridden) || isAnnotated(oerriddenParam)) {
				addSubtypeConstraint(oerriddenParam, overriderIt.next());
			}
		}
	}

	/* (non-Javadoc)
	 * @see checkers.inference2.InferenceChecker#createFieldAdaptReference(checkers.inference2.Reference, checkers.inference2.Reference, checkers.inference2.Reference)
	 */
	@Override
	protected Reference createFieldAdaptReference(Reference context,
			Reference decl, Reference assignTo) {
		return new FieldAdaptReference(context, decl);
	}

	/* (non-Javadoc)
	 * @see checkers.inference2.InferenceChecker#createMethodAdaptReference(checkers.inference2.Reference, checkers.inference2.Reference, checkers.inference2.Reference)
	 */
	@Override
	protected Reference createMethodAdaptReference(Reference context,
			Reference decl, Reference assignTo) {
		// Use callsite as context
        Set<Tree.Kind> kinds = EnumSet.of(Tree.Kind.METHOD_INVOCATION, 
            Tree.Kind.NEW_CLASS);
        Tree tree = TreeUtils.enclosingOfKind(currentPath, kinds);
        if (tree != null) {
            // use constant reference
        	String identifier = CALLSITE_PREFIX + getIdentifier(tree);
			TypeElement enclosingType = TreeUtils
					.elementFromDeclaration(TreeUtils
							.enclosingClass(currentFactory.getPath(tree)));
			Reference callsiteRef = getAnnotatedReference(identifier,
					RefKind.CALL_SITE, null, null, enclosingType,
					currentFactory.getAnnotatedType(tree));
            return new MethodAdaptReference(callsiteRef, decl);
        } else {
            throw new RuntimeException("Invalid adaptation context!");
        }
	}

	/* (non-Javadoc)
	 * @see checkers.inference2.InferenceChecker#annotateDefault(checkers.inference2.Reference, checkers.inference2.Reference.RefKind, javax.lang.model.element.Element, com.sun.source.tree.Tree)
	 */
	@Override
	protected void annotateDefault(Reference r, RefKind kind, Element elt,
			Tree t) {
		if (!isAnnotated(r)) {
			if (kind == RefKind.LITERAL) {
				r.addAnnotation(SAFE);
			} else {
				r.setAnnotations(sourceAnnos, this);
			}
		}
	}

	/* (non-Javadoc)
	 * @see checkers.inference2.InferenceChecker#annotateArrayComponent(checkers.inference2.Reference, javax.lang.model.element.Element)
	 */
	@Override
	protected void annotateArrayComponent(Reference r, Element elt) {
		if (!isAnnotated(r)) {
			r.addAnnotation(TAINTED);
			r.addAnnotation(POLY);
		}
	}

	/* (non-Javadoc)
	 * @see checkers.inference2.InferenceChecker#annotateField(checkers.inference2.Reference, javax.lang.model.element.Element)
	 */
	@Override
	protected void annotateField(Reference r, Element fieldElt) {
        if (!isAnnotated(r)) {
            if (!ElementUtils.isStatic(fieldElt)) {
                r.addAnnotation(TAINTED);
                r.addAnnotation(POLY);
            } else {
                r.setAnnotations(getSourceLevelQualifiers(), this);
            }
        }
	}

	/* (non-Javadoc)
	 * @see checkers.inference2.InferenceChecker#annotateThis(checkers.inference2.Reference, javax.lang.model.element.ExecutableElement)
	 */
	@Override
	protected void annotateThis(Reference r, ExecutableElement methodElt) {
        if (!isAnnotated(r) && !ElementUtils.isStatic(methodElt)) {
            if (isPolyLibrary() && isFromLibrary(methodElt)) {
                r.addAnnotation(POLY);
            } else {
                r.setAnnotations(sourceAnnos, this);
            }
        }
	}

	/* (non-Javadoc)
	 * @see checkers.inference2.InferenceChecker#annotateParameter(checkers.inference2.Reference, javax.lang.model.element.Element)
	 */
	@Override
	protected void annotateParameter(Reference r, Element elt) {
        if (!isAnnotated(r)) {
            if (isPolyLibrary() && isFromLibrary(elt)) {
                r.addAnnotation(POLY);
            } else {
                r.setAnnotations(sourceAnnos, this);
            }
        }
	}

	/* (non-Javadoc)
	 * @see checkers.inference2.InferenceChecker#annotateReturn(checkers.inference2.Reference, javax.lang.model.element.ExecutableElement)
	 */
	@Override
	protected void annotateReturn(Reference r, ExecutableElement methodElt) {
        if (!isAnnotated(r) && r.getType().getKind() != TypeKind.VOID) {
            if (isPolyLibrary() && isFromLibrary(methodElt)) {
                r.addAnnotation(POLY); 
            } else {
                r.setAnnotations(sourceAnnos, this);
            }
        }
	}

	/* (non-Javadoc)
	 * @see checkers.inference2.InferenceChecker#adaptField(javax.lang.model.element.AnnotationMirror, javax.lang.model.element.AnnotationMirror)
	 */
	@Override
	public AnnotationMirror adaptField(AnnotationMirror contextAnno,
			AnnotationMirror declAnno) {
		return adaptMethod(contextAnno, declAnno);
	}

	/* (non-Javadoc)
	 * @see checkers.inference2.InferenceChecker#adaptMethod(javax.lang.model.element.AnnotationMirror, javax.lang.model.element.AnnotationMirror)
	 */
	@Override
	public AnnotationMirror adaptMethod(AnnotationMirror contextAnno,
			AnnotationMirror declAnno) {
		if (declAnno.toString().equals(TAINTED.toString()))
			return TAINTED;
		else if (declAnno.toString().equals(SAFE.toString()))
			return SAFE;
		else if (declAnno.toString().equals(POLY.toString()))
			return contextAnno;
		else
			return null;
	}

	/* (non-Javadoc)
	 * @see checkers.inference2.InferenceChecker#isStrictSubtyping()
	 */
	@Override
	public boolean isStrictSubtyping() {
		return true;
	}

	/* (non-Javadoc)
	 * @see checkers.inference2.InferenceChecker#getFailureStatus(checkers.inference2.Constraint)
	 */
	@Override
	public FailureStatus getFailureStatus(Constraint c) {
		return FailureStatus.ERROR;
	}

	/* (non-Javadoc)
	 * @see checkers.inference2.InferenceChecker#getSourceLevelQualifiers()
	 */
	@Override
	public Set<AnnotationMirror> getSourceLevelQualifiers() {
		return sourceAnnos;
	}

	/* (non-Javadoc)
	 * @see checkers.inference2.InferenceChecker#getAnnotaionWeight(javax.lang.model.element.AnnotationMirror)
	 */
	@Override
	public int getAnnotaionWeight(AnnotationMirror anno) {
		if (anno.toString().equals(TAINTED.toString()))
			return 3;
		else if (anno.toString().equals(POLY.toString()))
			return 2;
		else if (anno.toString().equals(SAFE.toString()))
			return 1;
		else 
			return Integer.MAX_VALUE;
	}

	/* (non-Javadoc)
	 * @see checkers.inference2.InferenceChecker#needCheckConflict()
	 */
	@Override
	public boolean needCheckConflict() {
		return false;
	}
	
	@Override
	public void addSubtypeConstraint(Reference sub, Reference sup) {
		super.addSubtypeConstraint(sub, sup);
		if (!containsReadonly(sub) && !containsReadonly(sup)) {
			// add a subtying constraint with opposite direction
			super.addSubtypeConstraint(sup, sub);
		}
	}

	private boolean containsReadonly(Reference ref) {
		AnnotatedTypeMirror type = ref.getType();
		if (type != null && isDefaultReadonlyType(type))
			return true;
		if (ref instanceof AdaptReference) {
			Reference contextRef = ((AdaptReference) ref).getContextRef();
			Reference declRef = ((AdaptReference) ref).getDeclRef();
			if (ref instanceof FieldAdaptReference) {
				if (containsReadonly(contextRef) || containsReadonly(declRef)) {
					return true;
				}
			} else if (containsReadonly(declRef)) {
				return true;
			}
		}
		for (AnnotationMirror a : ref.getRawAnnotations()) {
			if (a.toString().equals(READONLY.toString())) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected SourceVisitor<?, ?> getInferenceVisitor(
			InferenceChecker inferenceChecker, CompilationUnitTree root) {
		return new SFlowInferenceVisitor(this, root);
	}
	
	private class SFlowInferenceVisitor extends InferenceVisitor {

		public SFlowInferenceVisitor(InferenceChecker checker,
				CompilationUnitTree root) {
			super(checker, root);
		}

		@Override
		public Void visitMethod(MethodTree node, Void p) {
			// Connect THIS for special Android methods.
			ExecutableElement methodElt = TreeUtils
					.elementFromDeclaration(node);
			if (isInferAndroidApp()) {
				TypeElement enclosingClass = ElementUtils
						.enclosingClass(methodElt);
				TypeMirror superclass = enclosingClass.getSuperclass();
				boolean needConnect = false;
				if (androidClasses.contains(superclass.toString())) {
					needConnect = true;
				} else {
					for (TypeMirror t : enclosingClass.getInterfaces()) {
						if (androidClasses.contains(t.toString())) {
							needConnect = true;
							break;
						}
					}
				}
				if (needConnect) {
					ExecutableReference methodRef = (ExecutableReference) getAnnotatedReference(methodElt);
					Reference classRef = getAnnotatedReference(enclosingClass);
					addEqualityConstraint(methodRef.getThisRef(), classRef);
				}
			}
			return super.visitMethod(node, p);
		}
		
		
	}

}
