/**
 * 
 */
package checkers.inference2.rely;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;

import checkers.inference.reim.quals.Mutable;
import checkers.inference.reim.quals.Polyread;
import checkers.inference.reim.quals.Readonly;
import checkers.inference.sflow.quals.Poly;
import checkers.inference.sflow.quals.Safe;
import checkers.inference.sflow.quals.Tainted;
import checkers.inference2.Constraint;
import checkers.inference2.ConstraintSolver.FailureStatus;
import checkers.inference2.InferenceChecker;
import checkers.inference2.InferenceVisitor;
import checkers.inference2.Reference;
import checkers.inference2.Reference.AdaptReference;
import checkers.inference2.Reference.FieldAdaptReference;
import checkers.inference2.Reference.MethodAdaptReference;
import checkers.inference2.Reference.RefKind;
import checkers.quals.TypeQualifiers;
import checkers.source.SourceVisitor;
import checkers.types.AnnotatedTypeMirror;
import checkers.util.AnnotationUtils;
import checkers.util.ElementUtils;
import checkers.util.TreeUtils;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree;

/**
 * @author dongy6
 * 
 */
@SupportedOptions({ "warn", "infer", "debug", "noReim", "inferLibrary", "polyLibrary", "inferAndroidApp" })
@TypeQualifiers({ Readonly.class, Polyread.class, Mutable.class, Poly.class, Tainted.class, Safe.class })
public class RelyChecker extends InferenceChecker {

	public AnnotationMirror READONLY, POLYREAD, MUTABLE, POLY, TAINTED, SAFE;

	private Set<AnnotationMirror> sourceAnnos;

	private AnnotationUtils annoFactory;

	public void initChecker(ProcessingEnvironment processingEnv) {
		super.initChecker(processingEnv);
		annoFactory = AnnotationUtils.getInstance(env);
		POLY = annoFactory.fromClass(Poly.class);
		TAINTED = annoFactory.fromClass(Tainted.class);
		SAFE = annoFactory.fromClass(Safe.class);

		READONLY = annoFactory.fromClass(Readonly.class);
		POLYREAD = annoFactory.fromClass(Polyread.class);
		MUTABLE = annoFactory.fromClass(Mutable.class);

		sourceAnnos = AnnotationUtils.createAnnotationSet();
		sourceAnnos.add(TAINTED);
		sourceAnnos.add(POLY);
		sourceAnnos.add(SAFE);
	}

	public Element getEnclosingMethod(Element elt) {
		while (elt != null && elt.getKind() != ElementKind.METHOD && elt.getKind() != ElementKind.CONSTRUCTOR) {
			elt = elt.getEnclosingElement();
		}
		return elt;
	}

	/**
	 * Check if this constraint connects param/ret
	 * 
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
	 * 
	 * @param ref
	 * @return
	 */
	public boolean isParamOrRetRef(Reference ref) {
		if (ref != null && !(ref instanceof AdaptReference)) {
			RefKind kind = ref.getKind();
			return kind == RefKind.PARAMETER || kind == RefKind.THIS || kind == RefKind.RETURN;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * checkers.inference2.InferenceChecker#createFieldAdaptReference(checkers
	 * .inference2.Reference, checkers.inference2.Reference,
	 * checkers.inference2.Reference)
	 */
	@Override
	protected Reference createFieldAdaptReference(Reference context, Reference decl, Reference assignTo) {
		return new FieldAdaptReference(context, decl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * checkers.inference2.InferenceChecker#createMethodAdaptReference(checkers
	 * .inference2.Reference, checkers.inference2.Reference,
	 * checkers.inference2.Reference)
	 */
	@Override
	protected Reference createMethodAdaptReference(Reference context, Reference decl, Reference assignTo) {
		// Use callsite as context
		Set<Tree.Kind> kinds = EnumSet.of(Tree.Kind.METHOD_INVOCATION, Tree.Kind.NEW_CLASS);
		Tree tree = TreeUtils.enclosingOfKind(currentPath, kinds);
		if (tree != null) {
			// use constant reference
			String identifier = CALLSITE_PREFIX + getIdentifier(tree);
			TypeElement enclosingType = TreeUtils
					.elementFromDeclaration(TreeUtils.enclosingClass(currentFactory.getPath(tree)));
			// yao: different callsite ref for each constraint. 1/9/2017
			Reference callsiteRef = getAnnotatedReference(identifier, RefKind.CALL_SITE, null, null, enclosingType,
					currentFactory.getAnnotatedType(tree));
			return new MethodAdaptReference(callsiteRef, decl);
		} else {
			throw new RuntimeException("Invalid adaptation context!");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * checkers.inference2.InferenceChecker#annotateDefault(checkers.inference2
	 * .Reference, checkers.inference2.Reference.RefKind,
	 * javax.lang.model.element.Element, com.sun.source.tree.Tree)
	 */
	@Override
	protected void annotateDefault(Reference r, RefKind kind, Element elt, Tree t) {
		if (!isAnnotated(r) || (t != null && t.getKind() == com.sun.source.tree.Tree.Kind.ARRAY_ACCESS)) {
			if (r.getKind() == RefKind.LITERAL) {
				r.addAnnotation(SAFE);
			} else {
				r.setAnnotations(sourceAnnos, this);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * checkers.inference2.InferenceChecker#annotateArrayComponent(checkers.
	 * inference2.Reference, javax.lang.model.element.Element)
	 */
	@Override
	protected void annotateArrayComponent(Reference r, Element elt) {
		if (!isAnnotated(r)) {
			r.addAnnotation(SAFE);
			r.addAnnotation(POLY);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * checkers.inference2.InferenceChecker#annotateField(checkers.inference2
	 * .Reference, javax.lang.model.element.Element)
	 */
	@Override
	protected void annotateField(Reference r, Element fieldElt) {
		if (!isAnnotated(r)) {
			if (!ElementUtils.isStatic(fieldElt)) {
				r.addAnnotation(SAFE);
				r.addAnnotation(POLY);
			} else {
				r.setAnnotations(getSourceLevelQualifiers(), this);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * checkers.inference2.InferenceChecker#annotateThis(checkers.inference2
	 * .Reference, javax.lang.model.element.ExecutableElement)
	 */
	@Override
	protected void annotateThis(Reference r, ExecutableElement methodElt) {
		if (!isAnnotated(r) && !ElementUtils.isStatic(methodElt)) {
			if (isFromLibrary(methodElt)) {
				r.addAnnotation(POLY);
			} else {
				r.setAnnotations(sourceAnnos, this);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see checkers.inference2.InferenceChecker#annotateParameter(checkers.
	 * inference2 .Reference, javax.lang.model.element.Element)
	 */
	@Override
	protected void annotateParameter(Reference r, Element elt) {
		if (!isAnnotated(r)) {
			if (isFromLibrary(elt)) {
				r.addAnnotation(POLY);
			} else {
				r.setAnnotations(sourceAnnos, this);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * checkers.inference2.InferenceChecker#annotateReturn(checkers.inference2
	 * .Reference, javax.lang.model.element.ExecutableElement)
	 */
	@Override
	protected void annotateReturn(Reference r, ExecutableElement methodElt) {
		if (!isAnnotated(r) && r.getType().getKind() != TypeKind.VOID) {
			if (isFromLibrary(methodElt) && !isSpecialMethod(methodElt.toString())) {
				r.addAnnotation(POLY);
			} else {
				r.setAnnotations(sourceAnnos, this);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * checkers.inference2.InferenceChecker#adaptField(javax.lang.model.element
	 * .AnnotationMirror, javax.lang.model.element.AnnotationMirror)
	 */
	@Override
	public AnnotationMirror adaptField(AnnotationMirror contextAnno, AnnotationMirror declAnno) {
		return adaptMethod(contextAnno, declAnno);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * checkers.inference2.InferenceChecker#adaptMethod(javax.lang.model.element
	 * .AnnotationMirror, javax.lang.model.element.AnnotationMirror)
	 */
	@Override
	public AnnotationMirror adaptMethod(AnnotationMirror contextAnno, AnnotationMirror declAnno) {
		if (declAnno.toString().equals(TAINTED.toString()))
			return TAINTED;
		else if (declAnno.toString().equals(SAFE.toString()))
			return SAFE;
		else if (declAnno.toString().equals(POLY.toString()))
			return contextAnno;
		else
			return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see checkers.inference2.InferenceChecker#isStrictSubtyping()
	 */
	@Override
	public boolean isStrictSubtyping() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * checkers.inference2.InferenceChecker#getFailureStatus(checkers.inference2
	 * .Constraint)
	 */
	@Override
	public FailureStatus getFailureStatus(Constraint c) {
		return FailureStatus.ERROR;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see checkers.inference2.InferenceChecker#getSourceLevelQualifiers()
	 */
	@Override
	public Set<AnnotationMirror> getSourceLevelQualifiers() {
		return sourceAnnos;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * checkers.inference2.InferenceChecker#getAnnotaionWeight(javax.lang.model
	 * .element.AnnotationMirror)
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see checkers.inference2.InferenceChecker#needCheckConflict()
	 */
	@Override
	public boolean needCheckConflict() {
		return false;
	}

	@Override
	protected SourceVisitor<?, ?> getInferenceVisitor(InferenceChecker inferenceChecker, CompilationUnitTree root) {
		return new InferenceVisitor(this, root);
	}

	@Override
	public void printResult(PrintWriter out) {
		List<Reference> references = new ArrayList<Reference>(annotatedReferences.values());
		Collections.sort(references, new Comparator<Reference>() {
			@Override
			public int compare(Reference o1, Reference o2) {
				int ret = o1.getFileName().compareTo(o2.getFileName());
				if (ret == 0) {
					ret = o1.getLineNum() - o2.getLineNum();
				}
				if (ret == 0) {
					ret = o1.getName().compareTo(o2.getName());
				}
				return ret;
			}
		});

		for (Reference r : references) {
			Element elt = r.getElement();
			if (r.getKind() == RefKind.RETURN) {
				MethodTree node = (MethodTree) getDeclaration(elt);
				Element enclosingElement = elt.getEnclosingElement();
				if (node != null && enclosingElement instanceof TypeElement && TreeUtils.isConstructor(node))
					continue;
			}
			if ((elt == null && r.getKind() != RefKind.ALLOCATION) || r.getIdentifier().startsWith(LIB_PREFIX)
					|| (elt instanceof ExecutableElement) && isCompilerAddedConstructor((ExecutableElement) elt)
					|| r.getKind() == RefKind.CONSTANT || r.getKind() == RefKind.CALL_SITE
					|| r.getKind() == RefKind.CLASS || r.getKind() == RefKind.FIELD_ADAPT
					|| r.getKind() == RefKind.LITERAL || r.getKind() == RefKind.METH_ADAPT
					|| r.getKind() == RefKind.METHOD || r.getKind() == RefKind.THIS || r.getKind() == RefKind.COMPONENT
					|| r.getType().getKind() == TypeKind.VOID || r.getKind() == RefKind.ALLOCATION
					|| (r.getKind() == RefKind.PARAMETER && r.getName().equals("this"))) {
				continue;
			}

			AnnotatedTypeMirror type = r.getType();
			annotateInferredType(type, r);

			StringBuilder sb = new StringBuilder();
			sb.append(r.getFileName()).append("\t");
			sb.append(r.getLineNum()).append("\t");
			sb.append(r.getName()).append("\t\t");
			sb.append(type.toString()).append("\t");
			sb.append("(" + r.getId() + ")");
			sb.append(r.getKind());
			out.println(sb.toString());
		}
	}

	@Override
	public Reference getAnnotatedReference(String identifier, RefKind kind, Tree tree, Element element,
			TypeElement enclosingType, AnnotatedTypeMirror type, Set<AnnotationMirror> annos) {
		Reference ret = super.getAnnotatedReference(identifier, kind, tree, element, enclosingType, type, annos);
		// we have reim annotation, now we want to add jcrypt annotation
		Set<AnnotationMirror> reimAnnos = ret.getRawAnnotations();
		if (!isAnnotated(ret)) {
			annotatedReferences.remove(identifier);
			Reference newRef = super.getAnnotatedReference(identifier, kind, tree, element, enclosingType, type, annos);
			for (AnnotationMirror anno : reimAnnos) {
				newRef.addAnnotation(anno);
			}
			annotatedReferences.put(identifier, newRef);
			return newRef;
		}
		return ret;
	}

}
