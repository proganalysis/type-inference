/**
 * 
 */
package checkers.inference2.reimN;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;

import checkers.inference2.reimN.ReimNChecker;
import checkers.inference2.reimN.quals.MutMut;
import checkers.inference2.reimN.quals.PolyMut;
import checkers.inference2.reimN.quals.PolyPoly;
import checkers.inference2.reimN.quals.ReadMut;
import checkers.inference2.reimN.quals.ReadPoly;
import checkers.inference2.reimN.quals.ReadRead;
import checkers.inference2.Constraint;
import checkers.inference2.ConstraintSolver.FailureStatus;
import checkers.inference2.InferenceChecker;
import checkers.inference2.InferenceUtils;
import checkers.inference2.Reference;
import checkers.inference2.Reference.FieldAdaptReference;
import checkers.inference2.Reference.MethodAdaptReference;
import checkers.inference2.Reference.RefKind;
import checkers.quals.TypeQualifiers;
import checkers.types.AnnotatedTypeMirror;
import checkers.types.AnnotatedTypeMirror.AnnotatedDeclaredType;
import checkers.util.AnnotationUtils;
import checkers.util.ElementUtils;

import com.sun.source.tree.Tree;

/**
 * @author dongy6
 * 
 */
@SupportedOptions({ "warn", "infer", "libPureMethods", "libMutateStatics",
		"jaif", "debug" })
@TypeQualifiers({ ReadRead.class, ReadPoly.class, ReadMut.class,
		PolyPoly.class, PolyMut.class, MutMut.class })
public class ReimNChecker extends InferenceChecker {

	public AnnotationMirror READREAD, READPOLY, READMUT, POLYPOLY, POLYMUT,
			MUTMUT;

	private Set<AnnotationMirror> sourceLevelQuals;

	/** For default pure method */
	private List<Pattern> defaultPurePatterns;

	private Set<String> defaultReadonlyRefTypes;

	private AnnotationUtils annoFactory;

	private static int count; // count reference in handleInstanceFieldWrite()

	@Override
	public void initChecker(ProcessingEnvironment processingEnv) {
		super.initChecker(processingEnv);
		annoFactory = AnnotationUtils.getInstance(env);
		READREAD = annoFactory.fromClass(ReadRead.class);
		READPOLY = annoFactory.fromClass(ReadPoly.class);
		READMUT = annoFactory.fromClass(ReadMut.class);
		POLYPOLY = annoFactory.fromClass(PolyPoly.class);
		POLYMUT = annoFactory.fromClass(PolyMut.class);
		MUTMUT = annoFactory.fromClass(MutMut.class);

		sourceLevelQuals = AnnotationUtils.createAnnotationSet();
		sourceLevelQuals.add(READREAD);
		sourceLevelQuals.add(READPOLY);
		sourceLevelQuals.add(READMUT);
		sourceLevelQuals.add(POLYPOLY);
		sourceLevelQuals.add(POLYMUT);
		sourceLevelQuals.add(MUTMUT);
		defaultPurePatterns = new ArrayList<Pattern>(5);
		defaultPurePatterns.add(Pattern
				.compile(".*\\.equals\\(java\\.lang\\.Object\\)$"));
		defaultPurePatterns.add(Pattern.compile(".*\\.hashCode\\(\\)$"));
		defaultPurePatterns.add(Pattern.compile(".*\\.toString\\(\\)$"));
		defaultPurePatterns.add(Pattern.compile(".*\\.compareTo\\(.*\\)$"));

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
		defaultReadonlyRefTypes
				.add("java.util.concurrent.atomic.AtomicInteger");
		defaultReadonlyRefTypes.add("java.util.concurrent.atomic.AtomicLong");
		defaultReadonlyRefTypes.add("java.math.BigDecimal");
		defaultReadonlyRefTypes.add("java.math.BigInteger");

		count = 0;
	}

	public boolean isDefaultReadonlyType(AnnotatedTypeMirror t) {
		if (t.getKind().isPrimitive())
			return true;
		TypeElement elt = null;
		if (t.getKind() == TypeKind.DECLARED) {
			AnnotatedDeclaredType dt = (AnnotatedDeclaredType) t;
			elt = (TypeElement) dt.getUnderlyingType().asElement();
		}
		if (elt != null
				&& defaultReadonlyRefTypes.contains(elt.getQualifiedName()
						.toString()))
			return true;
		return false;
	}

	/**
	 * Check if the method is default pure. E.g. We assume
	 * java.lang.Object.toString() is default pure.
	 * 
	 * @param methodElt
	 * @return
	 */
	public boolean isDefaultPureMethod(ExecutableElement methodElt) {
		String key = InferenceUtils.getMethodSignature(methodElt);
		for (Pattern p : defaultPurePatterns) {
			if (p.matcher(key).matches()) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected void handleInstanceFieldWrite(Reference aBase, Reference aField,
			Reference aRhs, int pos) {
		if (aBase != null) {
			count++;
			Set<AnnotationMirror> set = AnnotationUtils.createAnnotationSet();
			set.add(READMUT);
			set.add(POLYMUT);
			set.add(MUTMUT);
			Reference mutableRef = getAnnotatedReference(
					set.toString() + count, RefKind.CONSTANT, null, null, null,
					null, set);
			addEqualityConstraint(aBase, mutableRef);
			super.handleInstanceFieldWrite(aBase, aField, aRhs, pos);
		}
	}

	/**
	 * Get the mutateStatic of library methodElt. If no input file is given,
	 * then assume it doesn't mutate statics
	 * 
	 * @param methodElt
	 * @return
	 */
	public AnnotationMirror getLibraryStaticTypeOf(ExecutableElement methodElt) {
		// TODO:
		return READREAD;
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
	protected Reference createFieldAdaptReference(Reference context,
			Reference decl, Reference assignTo) {
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
	protected Reference createMethodAdaptReference(Reference context,
			Reference decl, Reference assignTo) {
		// if (assignTo == null)
		// return decl;
		// else
		// return new MethodAdaptReference(assignTo, decl);
		if (context == null)
			return decl;
		else
			return new MethodAdaptReference(context, decl);
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
	protected void annotateDefault(Reference r, RefKind kind, Element elt,
			Tree t) {
		if (!isAnnotated(r)) {
			if (r.getType().getKind() == TypeKind.NULL) {
				r.addAnnotation(MUTMUT);
			} else if (kind == RefKind.LITERAL
					|| isDefaultReadonlyType(r.getType())) {
				r.addAnnotation(READREAD);
			} else {
				r.setAnnotations(getSourceLevelQualifiers(), this);
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
			r.addAnnotation(POLYPOLY);
			r.addAnnotation(READPOLY);
			// r.addAnnotation(READREAD);
			if (elt == null || !isFromLibrary(elt)) {
				r.addAnnotation(READREAD);
			}
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
			if (isDefaultReadonlyType(r.getType())) {
				r.addAnnotation(READREAD);
			} else if (!ElementUtils.isStatic(fieldElt)) {
				r.addAnnotation(READREAD);
				r.addAnnotation(POLYPOLY);
				r.addAnnotation(READPOLY);
			} else {
				r.setAnnotations(getSourceLevelQualifiers(), this);
				// r.addAnnotation(READREAD);
				// r.addAnnotation(READMUT);
				// r.addAnnotation(MUTMUT);
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
			if (isDefaultReadonlyType(r.getType())) {
				r.addAnnotation(READREAD);
			} else if (isFromLibrary(methodElt)) {
				r.addAnnotation(MUTMUT);
			} else {
				r.setAnnotations(getSourceLevelQualifiers(), this);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * checkers.inference2.InferenceChecker#annotateParameter(checkers.inference2
	 * .Reference, javax.lang.model.element.Element)
	 */
	@Override
	protected void annotateParameter(Reference r, Element elt) {
		if (!isAnnotated(r) && !ElementUtils.isStatic(elt)) {
			if (isDefaultReadonlyType(r.getType())) {
				r.addAnnotation(READREAD);
			} else if (isFromLibrary(elt)) {
				r.addAnnotation(MUTMUT);
			} else {
				r.setAnnotations(getSourceLevelQualifiers(), this);
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
			if (isDefaultReadonlyType(r.getType())) {
				r.addAnnotation(READREAD);
			} else {
				r.setAnnotations(getSourceLevelQualifiers(), this);
				// r.addAnnotation(READREAD);
				// r.addAnnotation(POLYPOLY);
				// r.addAnnotation(READPOLY);
			}
		}
	}

	@Override
	public AnnotationMirror adaptField(AnnotationMirror contextAnno,
			AnnotationMirror declAnno) {
		return adaptMethod(contextAnno, declAnno);
	}

	@Override
	public AnnotationMirror adaptMethod(AnnotationMirror contextAnno,
			AnnotationMirror declAnno) {
		if (declAnno.toString().equals(READREAD.toString())) {
			return READREAD;
		} else if (declAnno.toString().equals(READPOLY.toString())) {
			if (contextAnno.toString().equals(MUTMUT.toString())) {
				return READMUT;
			} else if (contextAnno.toString().equals(POLYPOLY.toString())
					|| contextAnno.toString().equals(POLYMUT.toString())) {
				return READPOLY;
			} else
				return READREAD;
		} else if (declAnno.toString().equals(POLYPOLY.toString())) {
			if (contextAnno.toString().equals(MUTMUT.toString()))
				return MUTMUT;
			else if (contextAnno.toString().equals(POLYPOLY.toString())
					|| contextAnno.toString().equals(POLYMUT.toString()))
				return POLYPOLY;
			else
				return READREAD;
		} else if (declAnno.toString().equals(READMUT.toString())) {
			return READMUT;
		} else if (declAnno.toString().equals(POLYMUT.toString())) {
			if (contextAnno.toString().equals(MUTMUT.toString()))
				return MUTMUT;
			else if (contextAnno.toString().equals(POLYPOLY.toString())
					|| contextAnno.toString().equals(POLYMUT.toString()))
				return POLYMUT;
			else
				return READMUT;
		} else if (declAnno.toString().equals(MUTMUT.toString()))
			return MUTMUT;
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
		Reference left = c.getLeft();
		Reference right = c.getRight();
		AnnotatedTypeMirror leftType = left.getType();
		AnnotatedTypeMirror rightType = right.getType();
		if (leftType != null && isDefaultReadonlyType(leftType)
				|| rightType != null && isDefaultReadonlyType(rightType))
			return FailureStatus.IGNORE;
		Element leftElt = left.getElement();
		Element rightElt = right.getElement();
		if (leftElt != null && isFromLibrary(leftElt) || rightElt != null
				&& isFromLibrary(rightElt))
			return FailureStatus.WARN;
		return FailureStatus.ERROR;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see checkers.inference2.InferenceChecker#getSourceLevelQualifiers()
	 */
	@Override
	public Set<AnnotationMirror> getSourceLevelQualifiers() {
		return sourceLevelQuals;
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
		if (anno.toString().equals(READREAD.toString()))
			return 1;
		else if (anno.toString().equals(READPOLY.toString()))
			return 2;
		else if (anno.toString().equals(POLYPOLY.toString()))
			return 3;
		else if (anno.toString().equals(READMUT.toString()))
			return 4;
		else if (anno.toString().equals(POLYMUT.toString()))
			return 5;
		else if (anno.toString().equals(MUTMUT.toString()))
			return 6;
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
	public void printResult(PrintWriter out) {
		List<Reference> references = new ArrayList<Reference>(
				annotatedReferences.values());
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
		int totalElementNum = 0, readNum = 0;
		for (Reference r : references) {
			Element elt = r.getElement();
			if ((elt == null && r.getKind() != RefKind.ALLOCATION)
					|| r.getIdentifier().startsWith(LIB_PREFIX)
					|| (elt instanceof ExecutableElement)
					&& isCompilerAddedConstructor((ExecutableElement) elt)
					|| r.getKind() == RefKind.CONSTANT
					|| r.getKind() == RefKind.CALL_SITE
					|| r.getKind() == RefKind.CLASS
					|| r.getKind() == RefKind.FIELD_ADAPT
					|| r.getKind() == RefKind.LITERAL
					|| r.getKind() == RefKind.METH_ADAPT
					|| r.getKind() == RefKind.METHOD
					|| (r.getKind() == RefKind.PARAMETER && r.getName().equals(
							"this"))) {
				continue;
			}

			totalElementNum++;
			Iterator<AnnotationMirror> annoIter = r.getAnnotations(this)
					.iterator();
			if (annoIter.hasNext()) {
				if (getAnnotaionWeight(annoIter.next()) == 1) {
					readNum++;
				}
			} else {
				totalElementNum--;
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

		out.println("There are  " + readNum + " ("
				+ (((float) readNum / totalElementNum) * 100)
				+ "%) readonly references out of " + totalElementNum
				+ " references.");
	}
}
