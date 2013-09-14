/**
 * 
 */
package checkers.inference.confidentiality;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
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
import javax.lang.model.type.TypeKind;

import checkers.basetype.BaseTypeVisitor;
import checkers.inference.Constraint;
import checkers.inference.InferenceChecker;
import checkers.inference.Reference;
import checkers.inference.Constraint.SubtypeConstraint;
import checkers.inference.Reference.AdaptReference;
import checkers.inference.Reference.ExecutableReference;
import checkers.inference.confidentiality.quals.Bottom;
import checkers.inference.confidentiality.quals.PPoly;
import checkers.inference.confidentiality.quals.PSecret;
import checkers.inference.confidentiality.quals.PTainted;
import checkers.inference.confidentiality.quals.RPoly;
import checkers.inference.confidentiality.quals.RSecret;
import checkers.inference.confidentiality.quals.RTainted;
import checkers.quals.TypeQualifiers;
import checkers.types.AnnotatedTypeMirror;
import checkers.types.AnnotatedTypeMirror.AnnotatedDeclaredType;
import checkers.util.AnnotationUtils;
import checkers.util.ElementUtils;
import checkers.util.TypesUtils;

import com.sun.source.tree.CompilationUnitTree;

/**
 * @author huangw5
 *
 */
@SupportedOptions( { "warn", "checking", "insertAnnos", "debug", "var", "cid" } ) 
@TypeQualifiers({PPoly.class, PSecret.class, PTainted.class, RPoly.class, RSecret.class, RTainted.class, Bottom.class})
public class ConfidentialityChecker extends InferenceChecker {
	
	public AnnotationMirror PPOLY, PSECRET, PTAINTED, RPOLY, RSECRET, RTAINTED, BOTTOM;
	
	private Map<String, Integer> annoWeights = new HashMap<String, Integer>();
	
	protected AnnotationUtils annoFactory;
	
	private Set<String> extraPrimitiveTypes;
	
	private Map<String, AnnotationMirror> mapPrimitives;
	
	private Map<String, AnnotationMirror> mapReferences;
	
	@Override
	public void initChecker(ProcessingEnvironment processingEnv) {
		super.initChecker(processingEnv);
		annoFactory = AnnotationUtils.getInstance(env);		
		PPOLY = annoFactory.fromClass(PPoly.class);
		PSECRET = annoFactory.fromClass(PSecret.class);
		PTAINTED = annoFactory.fromClass(PTainted.class);
		RPOLY = annoFactory.fromClass(RPoly.class);
		RSECRET = annoFactory.fromClass(RSecret.class);
		RTAINTED = annoFactory.fromClass(RTainted.class);				
		BOTTOM = annoFactory.fromClass(Bottom.class);
		
		mapPrimitives = new HashMap<String, AnnotationMirror>(6);
		mapReferences = new HashMap<String, AnnotationMirror>(6);
		
		mapPrimitives.put(PSECRET.toString(), PSECRET);
		mapPrimitives.put(RSECRET.toString(), PSECRET);
		mapPrimitives.put(PPOLY.toString(), PPOLY);
		mapPrimitives.put(RPOLY.toString(), PPOLY);
		mapPrimitives.put(PTAINTED.toString(), PTAINTED);
		mapPrimitives.put(RTAINTED.toString(), PTAINTED);
		
		mapReferences.put(RSECRET.toString(), RSECRET);
		mapReferences.put(PSECRET.toString(), RSECRET);
		mapReferences.put(RPOLY.toString(), RPOLY);
		mapReferences.put(PPOLY.toString(), RPOLY);
		mapReferences.put(RTAINTED.toString(), RTAINTED);
		mapReferences.put(PTAINTED.toString(), RTAINTED);
		
		annoWeights.put(PSECRET.toString(), 1);
		annoWeights.put(RSECRET.toString(), 1);
		
		annoWeights.put(PPOLY.toString(), 3);
		annoWeights.put(RPOLY.toString(), 3);
		
		annoWeights.put(PTAINTED.toString(), 5);
		annoWeights.put(RTAINTED.toString(), 5);
		
		extraPrimitiveTypes = new HashSet<String>();
		extraPrimitiveTypes.add("java.lang.String");
	}
	
	/**
	 * Our primitive type also includes String, Integer, and more
	 * @param type
	 * @return
	 */
	public boolean isPrimitiveType(AnnotatedTypeMirror type) {
		TypeElement elt = null;
		if (type.getKind() == TypeKind.DECLARED) {
			AnnotatedDeclaredType dt = (AnnotatedDeclaredType) type;
	        elt = (TypeElement)dt.getUnderlyingType().asElement();
	        String qualifiedName = elt.getQualifiedName().toString();
	        if (extraPrimitiveTypes.contains(qualifiedName))
	        	return true;
		}
		return type.getKind().isPrimitive()
				|| TypesUtils.isBoxedPrimitive(type.getUnderlyingType());
	}
	
	

	/**
	 * We need to override this method because we need to distinguish primitive
	 * and reference
	 */
	@Override
	public void fillAllPossibleAnnos(List<Reference> refs) {
		Set<AnnotationMirror> pSet = AnnotationUtils.createAnnotationSet();
		pSet.add(PSECRET);
		pSet.add(PPOLY);
		pSet.add(PTAINTED);
		Set<AnnotationMirror> rSet = AnnotationUtils.createAnnotationSet();
		rSet.add(RSECRET);
		rSet.add(RTAINTED);
		rSet.add(RPOLY);
		for (Reference ref : refs) {
			if (ref.getAnnotations().isEmpty()) {
				Reference r = ref;
				while (r instanceof AdaptReference) {
					r = ((AdaptReference) r).getDeclRef();
				}
				AnnotatedTypeMirror type = r.getType();
				if (type != null) {
					if (isPrimitiveType(type))
						ref.setAnnotations(pSet);
					else
						ref.setAnnotations(rSet);
				} else {
					throw new RuntimeException("Null Type!");
				}
			}
		}
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
		Reference left = c.getLeft();
		Reference right = c.getRight();
		AnnotatedTypeMirror leftType = left.getType();
		AnnotatedTypeMirror rightType = right.getType();
		if (leftType != null && isPrimitiveType(leftType)
				|| rightType != null && isPrimitiveType(rightType))
			return FailureStatus.IGNORE;
		return FailureStatus.ERROR;
	}

	/* (non-Javadoc)
	 * @see checkers.inference.InferenceChecker#getInferenceVisitor(checkers.inference.InferenceChecker, com.sun.source.tree.CompilationUnitTree)
	 */
	@Override
	public BaseTypeVisitor<?> getInferenceVisitor(InferenceChecker checker,
			CompilationUnitTree root) {
		return new ConfidentialityInferenceVisitor((ConfidentialityChecker) checker, root);
	}

	/* (non-Javadoc)
	 * @see checkers.inference.InferenceChecker#getSourceLevelQualifiers()
	 */
	@Override
	public Set<AnnotationMirror> getSourceLevelQualifiers() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see checkers.inference.InferenceChecker#adaptField(javax.lang.model.element.AnnotationMirror, javax.lang.model.element.AnnotationMirror)
	 */
	@Override
	public AnnotationMirror adaptField(AnnotationMirror contextAnno,
			AnnotationMirror declAnno) {
		String contextStr = contextAnno.toString();
//		if (contextStr.equals(PPOLY.toString())
//				|| contextStr.equals(PTAINTED.toString())
//				|| contextStr.equals(PSECRET.toString())) {
//			throw new RuntimeException("Adapting from a primitive type!");
//		}
		if (contextStr.equals(BOTTOM.toString()))
			return declAnno;
		String declStr = declAnno.toString();
		if (declStr.equals(PPOLY.toString())) {
			return mapPrimitives.get(contextStr);
		} else if (declStr.equals(RPOLY.toString())) {
			return mapReferences.get(contextStr);
		} else if (declStr.equals(PTAINTED.toString()))
			return PTAINTED;
		else if (declStr.equals(RTAINTED.toString()))
			return RTAINTED;
		else if (declStr.equals(PSECRET.toString()))
			return PSECRET;
		else if (declStr.equals(RSECRET.toString()))
			return RSECRET;
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
		Integer weight = annoWeights.get(anno.toString());
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
			if (ref.getElement() != null 
//					&& !ref.getType().getKind().isPrimitive()
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
			Element elt = ref.getElement();
			String isStatic = (elt != null? (ElementUtils.isStatic(elt)? "STATIC" : "INSTANT") : "UNKOWN");
			if (ref instanceof ExecutableReference) {
				if(isCompilerAddedConstructor((ExecutableElement) ref.getElement()))
					continue;
//				ExecutableReference eRef = (ExecutableReference) ref;
				// output type
				out.println(ref.getFileName() + "\t" + ref.getLineNum() + "\t"
						+ ref.getElement() + "\t\t" + ref.getAnnotatedType()
						+ "\t" + isStatic + "\t" + "METHOD");
			} else {
				// output type
				if (ref.getElement() != null) {
					out.println(ref.getFileName() + "\t" + ref.getLineNum()
							+ "\t" + ref.getElement() + "\t\t"
							+ ref.getAnnotatedType() + "\t" + isStatic + "\t"
							+ elt.getKind().toString());
					tmpType = ref.getAnnotatedType();
				} 
			}
			// statistics
		}	
	}

	@Override
	public boolean needCheckConflict() {
		return true;
	}

	@Override
	public void resolveConflictConstraints(List<Constraint> conflictConstraints) {
		System.out.println("INFO: There are " + conflictConstraints.size() + " conflicts");
		for (Constraint c : conflictConstraints) {
			if (!(c instanceof SubtypeConstraint)) {
				continue;
			}
			Reference left = c.getLeft();
			Reference right = c.getRight();
			Reference declRef, contextRef, outRef;
			if (left instanceof AdaptReference) {
				outRef = right;
				declRef = ((AdaptReference) left).getDeclRef();
				contextRef = ((AdaptReference) left).getContextRef();
			} else if (right instanceof AdaptReference){
				outRef = left;
				declRef = ((AdaptReference) right).getDeclRef();
				contextRef = ((AdaptReference) right).getContextRef();
			} else {
				// No adapt constraint
				continue;
			}
			Element declElt = declRef.getElement();
			if (declElt != null && declElt.getKind() == ElementKind.FIELD
					&& declRef.getAnnotations().size() > 1
					&& declRef.getAnnotations().contains(RTAINTED)) {
				Set<AnnotationMirror> annos = declRef.getAnnotations();
				annos.remove(RTAINTED);
				declRef.setAnnotations(annos);
			}
		}
	}

}
