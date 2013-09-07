/**
 * 
 */
package checkers.inference.universe;

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
import javax.lang.model.type.TypeKind;

import checkers.basetype.BaseTypeVisitor;
import checkers.inference.Constraint;
import checkers.inference.InferenceChecker;
import checkers.inference.Reference;
import checkers.inference.Reference.AdaptReference;
import checkers.inference.Reference.ExecutableReference;
import checkers.inference.universe.quals.Any;
import checkers.inference.universe.quals.Lost;
import checkers.inference.universe.quals.Peer;
import checkers.inference.universe.quals.Rep;
import checkers.inference.universe.quals.Self;
import checkers.inference.universe.quals.UTBottom;
import checkers.quals.TypeQualifiers;
import checkers.types.AnnotatedTypeMirror;
import checkers.util.AnnotationUtils;
import checkers.util.TypesUtils;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree.Kind;

/**
 * @author huangw5
 *
 */
@SupportedOptions( { "warn", "checking", "libPureMethods"} ) 
@TypeQualifiers({Rep.class, Peer.class, Any.class, Lost.class, Self.class, UTBottom.class})
public class UniverseChecker extends InferenceChecker {
	
	public AnnotationMirror REP, PEER, ANY, LOST, SELF, BOTTOM;
	
	private Set<AnnotationMirror> sourceLevelQuals; 
	
	private Map<String, AnnotationMirror> adaptMap = new HashMap<String, AnnotationMirror>();
	
	@Override
	public void initChecker(ProcessingEnvironment processingEnv) {
		super.initChecker(processingEnv);
		AnnotationUtils annoFactory = AnnotationUtils.getInstance(processingEnv);
		REP = annoFactory.fromClass(Rep.class);
		PEER = annoFactory.fromClass(Peer.class);
		ANY = annoFactory.fromClass(Any.class);
		LOST = annoFactory.fromClass(Lost.class);
		SELF = annoFactory.fromClass(Self.class);
		BOTTOM = annoFactory.fromClass(UTBottom.class);
		
		sourceLevelQuals = AnnotationUtils.createAnnotationSet();
		sourceLevelQuals.add(REP);
		sourceLevelQuals.add(PEER);
		sourceLevelQuals.add(ANY);
	}
	
	
	public boolean isDefaultAnyType(AnnotatedTypeMirror type) {
        if (TypesUtils.isBoxedPrimitive(type.getUnderlyingType())
        		|| type.getUnderlyingType().toString().equals("java.math.BigInteger")
        		|| type.getUnderlyingType().toString().equals("java.math.BigDecimal")
                || type.getUnderlyingType().toString().equals("java.lang.String")) {
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
		Reference left = c.getLeft();
		Reference right = c.getRight();
		AnnotatedTypeMirror leftType = left.getType();
		AnnotatedTypeMirror rightType = right.getType();
		if (leftType != null && isDefaultAnyType(leftType)
				|| rightType != null && isDefaultAnyType(rightType))
			return FailureStatus.IGNORE;
		
		if (right instanceof AdaptReference) {
			Reference contextRef = ((AdaptReference) right).getContextRef();
			if (isDefaultAnyType(contextRef.getType()))
				return FailureStatus.IGNORE;
		}
		
		return FailureStatus.ERROR;
	}

	/* (non-Javadoc)
	 * @see checkers.inference.InferenceChecker#getInferenceVisitor(checkers.inference.InferenceChecker, com.sun.source.tree.CompilationUnitTree)
	 */
	@Override
	public BaseTypeVisitor<?> getInferenceVisitor(InferenceChecker checker,
			CompilationUnitTree root) {
		return new UniverseInferenceVisitor((UniverseChecker) checker, root);
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
		if (contextAnno.toString().equals(SELF.toString())
				|| contextAnno.toString().equals(BOTTOM.toString()))
			return declAnno;
		else if (declAnno.toString().equals(SELF.toString()))
			return contextAnno;
		
		String key = declAnno.toString() + "|" + contextAnno.toString();
		AnnotationMirror retVal = adaptMap.get(key);
		if (retVal != null)
			return retVal;
		
		if (declAnno.toString().equals(PEER.toString())) {
			if (contextAnno.toString().equals(PEER.toString()))
				retVal = PEER;
			else if (contextAnno.toString().equals(REP.toString()))
				retVal = REP;
			else //if (contextAnno.toString().equals(ANY.toString()))
				retVal = LOST;
		} else if (declAnno.toString().equals(REP.toString()))
			retVal = LOST;
		else if (declAnno.toString().equals(ANY.toString()))
			retVal = ANY;
		
		// Update the map
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
		if (anno.toString().equals(ANY.toString()))
			return 1;
		else if (anno.toString().equals(REP.toString()))
			return 2;
		else if (anno.toString().equals(PEER.toString()))
			return 3;
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
				res = (int) (o1.getLineNum() - o2.getLineNum());
				if (res != 0)
					return res;
				if (o1.getElement() != null && o2.getElement() != null) {
					res = o1.getElement().toString().compareTo(o2.getElement().toString());
				}
				if (res != 0)
					return res;
				if (o1.getElement() == null && o2.getElement() == null) {
					res = o1.getTree().toString().compareTo(o2.getTree().toString());
				}
				if (res != 0)
					return res;
				if (o1.getElement() == null)
					return -1;
				else
					return 1;
			}});
		int repNum = 0, anyNum = 0, peerNum = 0;
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
				if (tmpType.hasAnnotation(ANY))
					anyNum++;
				else if (tmpType.hasAnnotation(REP))
					repNum++;
				else if (tmpType.hasAnnotation(PEER))
					peerNum++;
				else 
					System.err.println("WARN: Unknown type! " + tmpType);
			}
		}	
		String s = "INFO: There are " + anyNum + " ("
				+ (((float) anyNum / totalElementNum) * 100)
				+ "%) @Any, " + repNum + " ("
				+ (((float) repNum / totalElementNum) * 100)
				+ "%) @Rep and " + peerNum + " ("
				+ (((float) peerNum / totalElementNum) * 100) + "%) @Peer"
				+ " references out of " + totalElementNum + " references"
				+ " Total methods: " + methodNum;

		System.out.println(s);
		out.println(s);
	}


	@Override
	public boolean needCheckConflict() {
		return false;
	}

}
