/**
 * 
 */
package checkers.inference2.transform;

import java.io.PrintWriter;
import java.util.Set;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import checkers.inference2.jcrypt2.quals.RND;
import checkers.inference2.jcrypt2.quals.OPE;
import checkers.inference2.jcrypt2.quals.AH;
import checkers.inference2.jcrypt2.quals.DET;
import checkers.inference2.jcrypt2.quals.BOT;
import checkers.inference2.jcrypt.quals.Clear;
import checkers.inference2.jcrypt.quals.Poly;
import checkers.inference2.Constraint;
import checkers.inference2.ConstraintSolver.FailureStatus;
import checkers.inference2.InferenceChecker;
import checkers.inference2.InferenceMain;
import checkers.inference2.Reference;
import checkers.inference2.Reference.RefKind;
import checkers.quals.TypeQualifiers;
import checkers.source.SourceVisitor;
import checkers.util.AnnotationUtils;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree;

/**
 * @author dongy6
 * 
 */
@SupportedOptions({ "warn", "infer", "debug", "noReim", "inferLibrary",
		"polyLibrary", "inferAndroidApp" })
@TypeQualifiers({ RND.class, OPE.class, DET.class, AH.class ,BOT.class })
public class TransformChecker extends InferenceChecker {

	public AnnotationMirror RND, OPE, DET, AH, CLEAR, POLY, BOT;

	private Set<AnnotationMirror> sourceAnnos;

	private AnnotationUtils annoFactory;
	
	private CompilationUnitTree root;

	public void initChecker(ProcessingEnvironment processingEnv) {
		super.initChecker(processingEnv);
		annoFactory = AnnotationUtils.getInstance(env);
		RND = annoFactory.fromClass(RND.class);
		OPE = annoFactory.fromClass(OPE.class);
		AH = annoFactory.fromClass(AH.class);
		DET = annoFactory.fromClass(DET.class);
		CLEAR = annoFactory.fromClass(Clear.class);
		BOT = annoFactory.fromClass(BOT.class);
		POLY = annoFactory.fromClass(Poly.class);

		sourceAnnos = AnnotationUtils.createAnnotationSet();
		sourceAnnos.add(RND);
		sourceAnnos.add(OPE);
		sourceAnnos.add(AH);
		sourceAnnos.add(DET);
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
		return null;
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
		return null;
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
		if (InferenceMain.fullEncrypt) {
			r.addAnnotation(RND);
		} else {
			r.addAnnotation(CLEAR);
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
	protected void annotateArrayComponent(Reference r, Element elt) {}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * checkers.inference2.InferenceChecker#annotateField(checkers.inference2
	 * .Reference, javax.lang.model.element.Element)
	 */
	@Override
	protected void annotateField(Reference r, Element fieldElt) {
		r.getElement();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * checkers.inference2.InferenceChecker#annotateThis(checkers.inference2
	 * .Reference, javax.lang.model.element.ExecutableElement)
	 */
	@Override
	protected void annotateThis(Reference r, ExecutableElement methodElt) {}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * checkers.inference2.InferenceChecker#annotateParameter(checkers.inference2
	 * .Reference, javax.lang.model.element.Element)
	 */
	@Override
	protected void annotateParameter(Reference r, Element elt) {}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * checkers.inference2.InferenceChecker#annotateReturn(checkers.inference2
	 * .Reference, javax.lang.model.element.ExecutableElement)
	 */
	@Override
	protected void annotateReturn(Reference r, ExecutableElement methodElt) {}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * checkers.inference2.InferenceChecker#adaptField(javax.lang.model.element
	 * .AnnotationMirror, javax.lang.model.element.AnnotationMirror)
	 */
	@Override
	public AnnotationMirror adaptField(AnnotationMirror contextAnno,
			AnnotationMirror declAnno) {
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
	public AnnotationMirror adaptMethod(AnnotationMirror contextAnno,
			AnnotationMirror declAnno) {
		return declAnno;
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
		return 0;
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
	protected SourceVisitor<?, ?> getInferenceVisitor(
			InferenceChecker inferenceChecker, CompilationUnitTree root) {
		if (this.root != null && this.root != root) printResult();
		this.root = root;
		return new TransformVisitor(this, root);
	}
	
	public void printResult() {
		String fullname = root.getSourceFile().toString();
		int start = fullname.lastIndexOf('/') + 1;
		int end = fullname.indexOf(']');
		String filename = fullname.substring(start, end);
		if (filename.equals("EncryptionSample.java")) return;
		try {
			PrintWriter pw = new PrintWriter(TransformMain.outputDirTrans
					+ TransformMain.packageName + filename);
			printResult(pw);
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void printResult(PrintWriter out) {
		out.print(this.root.toString().
				replaceAll("@Poly\\(\\)\\s*.*this,?", "").
				replace("@Sensitive()", "").
				replace("@Poly()", "").
				replace("@Clear()", "").replace("@BOT()", ""));
	}

}
