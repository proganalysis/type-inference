/**
 * 
 */
package checkers.inference.ownership;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import checkers.basetype.BaseTypeVisitor;
import checkers.inference.Constraint;
import checkers.inference.InferenceChecker;
import checkers.inference.Reference;
import checkers.inference.Reference.ExecutableReference;
import checkers.inference.ownership.quals.NoRep;
import checkers.inference.ownership.quals.OTBottom;
import checkers.inference.ownership.quals.OwnNorep;
import checkers.inference.ownership.quals.OwnOwn;
import checkers.inference.ownership.quals.OwnPar;
import checkers.inference.ownership.quals.ParNorep;
import checkers.inference.ownership.quals.ParPar;
import checkers.inference.ownership.quals.RepNorep;
import checkers.inference.ownership.quals.RepOwn;
import checkers.inference.ownership.quals.RepPar;
import checkers.inference.ownership.quals.RepRep;
import checkers.quals.TypeQualifiers;
import checkers.types.AnnotatedTypeMirror;
import checkers.types.AnnotatedTypeMirror.AnnotatedDeclaredType;
import checkers.util.AnnotationUtils;
import checkers.util.TypesUtils;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree.Kind;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.Type.ClassType;

/**
 * @author huangw5
 *
 */
@SupportedOptions( { "warn", "checking"} ) 
@TypeQualifiers({ RepRep.class, OwnOwn.class, ParPar.class, RepOwn.class,
		RepPar.class, OwnPar.class, NoRep.class, RepNorep.class,
		OwnNorep.class, ParNorep.class, OTBottom.class })
public class OwnershipChecker extends InferenceChecker {
	
	public AnnotationMirror REPREP, REPOWN, REPPAR, REPNOREP, OWNOWN, OWNPAR,
			OWNNOREP, PARPAR, PARNOREP, NOREP, BOTTOM;
	
	protected AnnotationUtils annoFactory;
	
	private Set<AnnotationMirror> sourceLevelQuals; 
	
	private Map<String, Integer> annotationWeights = new HashMap<String, Integer>();

	private Map<String, AnnotationMirror> adaptMap = new HashMap<String, AnnotationMirror>();

	@Override
	public void initChecker(ProcessingEnvironment processingEnv) {
		super.initChecker(processingEnv);
		annoFactory = AnnotationUtils.getInstance(env);
		REPREP = annoFactory.fromClass(RepRep.class);
		REPOWN = annoFactory.fromClass(RepOwn.class);
		REPPAR = annoFactory.fromClass(RepPar.class);
		REPNOREP = annoFactory.fromClass(RepNorep.class);
		OWNOWN = annoFactory.fromClass(OwnOwn.class);
		OWNPAR = annoFactory.fromClass(OwnPar.class);
		OWNNOREP = annoFactory.fromClass(OwnNorep.class);
		PARPAR = annoFactory.fromClass(ParPar.class);
		PARNOREP = annoFactory.fromClass(ParNorep.class);
		NOREP = annoFactory.fromClass(NoRep.class);
		BOTTOM = annoFactory.fromClass(OTBottom.class);
		
		annotationWeights.put(REPREP.toString(), 0);
		annotationWeights.put(REPOWN.toString(), 1);
		annotationWeights.put(REPPAR.toString(), 2);
		annotationWeights.put(REPNOREP.toString(), 3);
		annotationWeights.put(OWNOWN.toString(), 4);
		annotationWeights.put(OWNPAR.toString(), 5);
		annotationWeights.put(OWNNOREP.toString(), 6);
		annotationWeights.put(PARPAR.toString(), 7);
		annotationWeights.put(PARNOREP.toString(), 8);
		annotationWeights.put(NOREP.toString(), 9);
		annotationWeights.put(BOTTOM.toString(), Integer.MAX_VALUE);
		
		sourceLevelQuals = AnnotationUtils.createAnnotationSet();
		// BOTTOM does not belong to source qualifiers
		sourceLevelQuals.add(REPREP);
		sourceLevelQuals.add(REPOWN);
		sourceLevelQuals.add(REPPAR);
		sourceLevelQuals.add(REPNOREP);
		sourceLevelQuals.add(OWNOWN);
		sourceLevelQuals.add(OWNPAR);
		sourceLevelQuals.add(OWNNOREP);
		sourceLevelQuals.add(PARPAR);
		sourceLevelQuals.add(PARNOREP);
		sourceLevelQuals.add(NOREP);
	}
	
	
	public boolean isDefaultNorepType(AnnotatedTypeMirror type) {
        TypeElement elt = null;
        if (type instanceof AnnotatedDeclaredType)
			elt = (TypeElement) ((AnnotatedDeclaredType) type)
					.getUnderlyingType().asElement();
        if (TypesUtils.isBoxedPrimitive(type.getUnderlyingType())
        		|| isExceptionClass(type)
        		|| type.getUnderlyingType().toString().equals("java.util.Date")
        		|| type.getUnderlyingType().toString().equals("java.math.BigInteger")
        		|| type.getUnderlyingType().toString().equals("java.math.BigDecimal")
                || (elt != null && elt.getQualifiedName().contentEquals("java.lang.String"))) {
        	return true;
        }
        return false;
	}
	
	/* (non-Javadoc)
	 * @see checkers.inference.InferenceChecker#isStrictSubtyping()
	 */
	@Override
	public boolean isStrictSubtyping() {
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
		return new OwnershipInferenceVisitor((OwnershipChecker) checker, root);
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
		String key = declAnno.toString() + "|" + contextAnno.toString();
		AnnotationMirror retVal = adaptMap.get(key);
		if (retVal != null)
			return retVal;

		if (declAnno.toString().equals(OWNOWN.toString())) {
			if (contextAnno.toString().equals(REPREP.toString())
					|| contextAnno.toString().equals(REPOWN.toString())
					|| contextAnno.toString().equals(REPPAR.toString())
					|| contextAnno.toString().equals(REPNOREP.toString()))
				retVal = REPREP;
			else if (contextAnno.toString().equals(OWNOWN.toString())
					|| contextAnno.toString().equals(OWNPAR.toString())
					|| contextAnno.toString().equals(OWNNOREP.toString()))
				retVal = OWNOWN;
			else if (contextAnno.toString().equals(PARPAR.toString())
					|| contextAnno.toString().equals(PARNOREP.toString()))
				retVal = PARPAR;
			else if (contextAnno.toString().equals(NOREP.toString()))
				retVal = NOREP;
		} else if (declAnno.toString().equals(OWNPAR.toString())) {
			retVal = contextAnno;
		} else if (declAnno.toString().equals(OWNNOREP.toString())) {
			if (contextAnno.toString().equals(REPREP.toString())
					|| contextAnno.toString().equals(REPOWN.toString())
					|| contextAnno.toString().equals(REPPAR.toString())
					|| contextAnno.toString().equals(REPNOREP.toString()))
				retVal = REPNOREP;
			else if (contextAnno.toString().equals(OWNOWN.toString())
					|| contextAnno.toString().equals(OWNPAR.toString())
					|| contextAnno.toString().equals(OWNNOREP.toString()))
				retVal = OWNNOREP;
			else if (contextAnno.toString().equals(PARPAR.toString())
					|| contextAnno.toString().equals(PARNOREP.toString()))
				retVal = PARNOREP;
			else if (contextAnno.toString().equals(NOREP.toString()))
				retVal = NOREP;
		} else if (declAnno.toString().equals(PARPAR.toString())) {
			if (contextAnno.toString().equals(REPREP.toString()))
				retVal = REPREP;
			else if (contextAnno.toString().equals(REPOWN.toString())
					|| contextAnno.toString().equals(OWNOWN.toString()))
				retVal = OWNOWN;
			else if (contextAnno.toString().equals(REPPAR.toString())
					|| contextAnno.toString().equals(OWNPAR.toString())
					| contextAnno.toString().equals(PARPAR.toString()))
				retVal = PARPAR;
			else if (contextAnno.toString().equals(NOREP.toString())
					|| contextAnno.toString().equals(REPNOREP.toString())
					|| contextAnno.toString().equals(OWNNOREP.toString())
					|| contextAnno.toString().equals(PARNOREP.toString()))
				retVal = NOREP;

		} else if (declAnno.toString().equals(PARNOREP.toString())) {
			if (contextAnno.toString().equals(REPREP.toString()))
				retVal = REPNOREP;
			else if (contextAnno.toString().equals(REPOWN.toString())
					|| contextAnno.toString().equals(OWNOWN.toString()))
				retVal = OWNNOREP;
			else if (contextAnno.toString().equals(REPPAR.toString())
					|| contextAnno.toString().equals(OWNPAR.toString())
					| contextAnno.toString().equals(PARPAR.toString()))
				retVal = PARNOREP;
			else if (contextAnno.toString().equals(NOREP.toString())
					|| contextAnno.toString().equals(REPNOREP.toString())
					|| contextAnno.toString().equals(OWNNOREP.toString())
					|| contextAnno.toString().equals(PARNOREP.toString()))
				retVal = NOREP;

		} else if (declAnno.toString().equals(NOREP.toString()))
			retVal = NOREP;

		if (retVal != null)
			adaptMap.put(key, retVal);

		return retVal;
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
		Integer weight = annotationWeights.get(anno.toString());
		if (weight == null)
			return Integer.MAX_VALUE;
		else
			return weight;
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
		int repNum = 0, ownNum = 0, pNum = 0, norepNum = 0, defaultNorepNum = 0;
		int totalElementNum = 0, methodNum = 0;
		for (Reference ref : lst) {
			AnnotatedTypeMirror tmpType = null;
			if (ref instanceof ExecutableReference) {
				if(isCompilerAddedConstructor((ExecutableElement) ref.getElement()))
					continue;
				methodNum++;
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
					continue;
				}
			}
			// statistics
			if (tmpType != null && !tmpType.getKind().isPrimitive()
					&& tmpType.getKind() != TypeKind.VOID) {
				totalElementNum++;
				if (tmpType.hasAnnotation(REPREP)
						|| tmpType.hasAnnotation(REPOWN)
						|| tmpType.hasAnnotation(REPPAR)
						|| tmpType.hasAnnotation(REPNOREP))
					repNum++;
				else if (tmpType.hasAnnotation(OWNOWN)
						|| tmpType.hasAnnotation(OWNPAR)
						|| tmpType.hasAnnotation(OWNNOREP)
						)
					ownNum++;
				else if (tmpType.hasAnnotation(PARPAR)
						|| tmpType.hasAnnotation(PARNOREP))
					pNum++;
				else if (tmpType.hasAnnotation(NOREP)) {
					norepNum++;
					if (isDefaultNorepType(tmpType))
						defaultNorepNum++;
				}
				else 
					System.err.println("WARN: Unknown type! " + tmpType);
			}
		}	
		String s = "INFO: There are " + repNum + " ("
				+ (((float) repNum / totalElementNum) * 100)
				+ "%) rep, " + ownNum + " ("
				+ (((float) ownNum / totalElementNum) * 100)
				+ "%) own, " + pNum + " ("
				+ (((float) pNum / totalElementNum) * 100) 
				+ "%) p, " + norepNum + " ("
				+ (((float) norepNum / totalElementNum) * 100) 
				+ "%) norep "
				+ " references out of " + totalElementNum + " references." 
				+ " there are " + defaultNorepNum + " default norep "
				+ " Total methods: " + methodNum;
		System.out.println(s);
		out.println(s);
		insertInferredAnnotations(lst);
	}


	@Override
	public boolean needCheckConflict() {
		return true;
	}

}
