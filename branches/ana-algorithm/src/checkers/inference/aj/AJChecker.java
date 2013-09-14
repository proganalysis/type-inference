/**
 * 
 */
package checkers.inference.aj;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeKind;

import checkers.basetype.BaseTypeVisitor;
import checkers.inference.Constraint;
import checkers.inference.InferenceChecker;
import checkers.inference.Reference;
import checkers.inference.Reference.ExecutableReference;
import checkers.inference.aj.quals.AJBottom;
import checkers.inference.aj.quals.Aliased;
import checkers.inference.aj.quals.IntAliased;
import checkers.inference.aj.quals.IntSelf;
import checkers.inference.aj.quals.NonAliased;
import checkers.inference.aj.quals.Self;
import checkers.quals.TypeQualifiers;
import checkers.types.AnnotatedTypeMirror;
import checkers.util.AnnotationUtils;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree.Kind;

/**
 * @author huangw5
 *
 */
@SupportedOptions( { "warn", "checking"} ) 
@TypeQualifiers({ Aliased.class, NonAliased.class, IntAliased.class,
		Self.class, IntSelf.class, AJBottom.class })
public class AJChecker extends InferenceChecker {
	
    public AnnotationMirror ALIASED, NONALIASED, INTALIASED, SELF, INTSELF, BOTTOM;
    
	private Set<AnnotationMirror> sourceLevelQuals; 

	@Override
	public void initChecker(ProcessingEnvironment processingEnv) {
		super.initChecker(processingEnv);
		AnnotationUtils annoFactory = AnnotationUtils.getInstance(processingEnv);
		
        ALIASED = annoFactory.fromClass(Aliased.class);
        NONALIASED = annoFactory.fromClass(NonAliased.class);
        INTALIASED = annoFactory.fromClass(IntAliased.class);
        SELF = annoFactory.fromClass(Self.class);
        INTSELF = annoFactory.fromClass(IntSelf.class);
        BOTTOM = annoFactory.fromClass(AJBottom.class);
        
		sourceLevelQuals = AnnotationUtils.createAnnotationSet();
		sourceLevelQuals.add(ALIASED);
		sourceLevelQuals.add(NONALIASED);
		sourceLevelQuals.add(INTALIASED);

	}

	/* (non-Javadoc)
	 * @see checkers.inference.InferenceChecker#isStrictSubtyping()
	 */
	@Override
	public boolean isStrictSubtyping() {
		// TODO Auto-generated method stub
		return true;
	}

	/* (non-Javadoc)
	 * @see checkers.inference.InferenceChecker#getFailureStatus(checkers.inference.Constraint)
	 */
	@Override
	public FailureStatus getFailureStatus(Constraint c) {
		return FailureStatus.ERROR;
	}

	/* (non-Javadoc)
	 * @see checkers.inference.InferenceChecker#getInferenceVisitor(checkers.inference.InferenceChecker, com.sun.source.tree.CompilationUnitTree)
	 */
	@Override
	public BaseTypeVisitor<?> getInferenceVisitor(InferenceChecker checker,
			CompilationUnitTree root) {
		return new AJInferenceVisitor((AJChecker) checker, root);
	}

	/* (non-Javadoc)
	 * @see checkers.inference.InferenceChecker#getSourceLevelQualifiers()
	 */
	@Override
	public Set<AnnotationMirror> getSourceLevelQualifiers() {
		return sourceLevelQuals;
	}

	/* (non-Javadoc)
	 * @see checkers.inference.InferenceChecker#adaptField(javax.lang.model.element.AnnotationMirror, javax.lang.model.element.AnnotationMirror)
	 */
	@Override
	public AnnotationMirror adaptField(AnnotationMirror contextAnno,
			AnnotationMirror declAnno) {
		if (contextAnno.toString().equals(INTSELF.toString())) { 
			if (declAnno.toString().equals(SELF.toString()))
				return null;
			else
				return declAnno;
		}
		if (contextAnno.toString().equals(SELF.toString())) {
			if (declAnno.toString().equals(INTSELF.toString()))
				return null;
			else
				return declAnno;
		}
		if (contextAnno.toString().equals(INTALIASED.toString())) {
			if (declAnno.toString().equals(INTSELF.toString()))
				return INTALIASED;
			if (declAnno.toString().equals(SELF.toString()))
				return null;
			else
				return declAnno;
		}
		if (contextAnno.toString().equals(ALIASED.toString())) {
			if (declAnno.toString().equals(SELF.toString()))
				return ALIASED;
			if (declAnno.toString().equals(INTSELF.toString()))
				return null;
			else
				return declAnno;
		}
		// If control reaches here, then adapter is NONALIASED
		if (declAnno.toString().equals(NONALIASED.toString()) 
				|| declAnno.toString().equals(SELF.toString()))
			return NONALIASED;
		else 
			return null;

	}

	/* (non-Javadoc)
	 * @see checkers.inference.InferenceChecker#adaptMethod(javax.lang.model.element.AnnotationMirror, javax.lang.model.element.AnnotationMirror)
	 */
	@Override
	public AnnotationMirror adaptMethod(AnnotationMirror contextAnno,
			AnnotationMirror declAnno) {
		return adaptField(contextAnno, declAnno);
	}

	/* (non-Javadoc)
	 * @see checkers.inference.InferenceChecker#getAnnotaionWeight(javax.lang.model.element.AnnotationMirror)
	 */
	@Override
	public int getAnnotaionWeight(AnnotationMirror anno) {
		if (anno.toString().equals(INTALIASED.toString()))
			return 1;
		if (anno.toString().equals(NONALIASED.toString()))
			return 3;
		else if (anno.toString().equals(ALIASED.toString()))
			return 2;
		else if (anno.toString().equals(INTSELF.toString()))
			return 4;
		else if (anno.toString().equals(SELF.toString()))
			return 5;			
		else 
			return Integer.MAX_VALUE;
	}

	/* (non-Javadoc)
	 * @see checkers.inference.InferenceChecker#printResult(java.util.Map, java.io.PrintWriter)
	 */
	@Override
	public void printResult(Map<String, Reference> solution, PrintWriter out) {
		Collection<Reference> values = solution.values();
		List<Reference> lst = new ArrayList<Reference>(values.size());
		for (Reference ref : values) {
			if ((ref.getElement() != null 
					|| ref.getTree().getKind() == Kind.NEW_ARRAY 
					|| ref.getTree().getKind() == Kind.NEW_CLASS) 
					&& !ref.getType().getKind().isPrimitive()
					&& !ref.getFileName().startsWith("zLIB")
					&& ref.getLineNum() > 0
							) {
				lst.add(ref);
			}
		}
		Collections.sort(lst, new Comparator<Reference>(){
			@Override
			public int compare(Reference o1, Reference o2) {
				int res = o1.getFileName().compareTo(o2.getFileName());
				if (res != 0)
					return res;
				else 
					res = (int) (o1.getLineNum() - o2.getLineNum());
				if (res != 0)
					return res;
				else if (o1.getElement() != null && o2.getElement() != null) {
					return o1.getElement().toString().compareTo(o2.getElement().toString());
				}
				return 0;
			}});
		
		for (Reference ref : lst) {
			AnnotatedTypeMirror tmpType = null;
			if (ref instanceof ExecutableReference) {
				if(isCompilerAddedConstructor((ExecutableElement) ref.getElement()))
					continue;
				ExecutableReference eRef = (ExecutableReference) ref;
				
				// output type
				out.println(ref.getFileName() + "\t" + ref.getLineNum() + "\t"
						+ ref.getElement() + "\t\t" + ref.getAnnotatedType());
				
				// return
				AnnotatedTypeMirror returnType = eRef.getReturnRef().getAnnotatedType();
				if (returnType != null && !returnType.getKind().isPrimitive()
						&& returnType.getKind() != TypeKind.VOID) {
					tmpType = returnType;
				}
			} else {
				// output type
				if (ref.getElement() != null) {
					out.println(ref.getFileName() + "\t" + ref.getLineNum() + "\t"
							+ ref.getElement() + "\t\t" + ref.getAnnotatedType());
					tmpType = ref.getAnnotatedType();
				} else {  // output allocation sites and skip statistics
					out.println(ref.getFileName() + "\t" + ref.getLineNum() + "\t"
							+ ref.getTree() + "\t\t" + ref.getAnnotatedType());
//					continue;
				}
			}
		}
		
		insertInferredAnnotations(lst);
	}

	@Override
	public boolean needCheckConflict() {
		return false;
	}

}
