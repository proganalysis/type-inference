/**
 * 
 */
package checkers.inference;

import java.util.List;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.type.TypeKind;

import checkers.basetype.BaseTypeChecker;
import checkers.types.AnnotatedTypeMirror;
import checkers.types.AnnotatedTypeMirror.AnnotatedArrayType;
import checkers.types.AnnotatedTypeMirror.AnnotatedDeclaredType;
import checkers.types.AnnotatedTypeMirror.AnnotatedTypeVariable;
import checkers.types.AnnotatedTypeMirror.AnnotatedWildcardType;
import checkers.types.QualifierHierarchy;
import checkers.types.TypeHierarchy;


/**
 * Implement type hierarchy for ignoring the type arguments. 
 * @author huangw5
 */
public class InferenceTypeHierarchy extends TypeHierarchy {
    
    /** The hierarchy of qualifiers */
    private final QualifierHierarchy qualifierHierarchy;
    
	public InferenceTypeHierarchy(InferenceChecker checker, QualifierHierarchy qualifierHierarchy) {
		super(checker, qualifierHierarchy);
		this.qualifierHierarchy = qualifierHierarchy;
	}

	@Override
	protected boolean isSubtypeAsTypeArgument(AnnotatedTypeMirror rhs, AnnotatedTypeMirror lhs) {
        if (lhs.getKind() == TypeKind.WILDCARD && rhs.getKind() != TypeKind.WILDCARD) {
            if (visited.contains(lhs.getElement()))
                return true;

            visited.add(lhs.getElement());
            lhs = ((AnnotatedWildcardType)lhs).getExtendsBound();
            if (lhs == null) return true;
            return isSubtypeImpl(rhs, lhs);
        }

        if (lhs.getKind() == TypeKind.WILDCARD && rhs.getKind() == TypeKind.WILDCARD) {
        	AnnotatedTypeMirror rhsExtendsBound = ((AnnotatedWildcardType)rhs).getExtendsBound();
        	AnnotatedTypeMirror lhsExtendsBound = ((AnnotatedWildcardType)lhs).getExtendsBound();
        	// FIXME: WEI: Ignored WILDCARD
//        	if (!isSubtype(rhsExtendsBound, lhsExtendsBound)) {
//        		System.err.println("WARN: Ignore wildcard - lhs: " + lhs + " rhs: " + rhs);
//        	}
        	return true;
//        	if (rhsExtendsBound.getAnnotations().size() == lhsExtendsBound.getAnnotations().size())
//	            return isSubtype(rhsExtendsBound, lhsExtendsBound); 
//        	else {
//        		System.err.println("WARN: different annotations on extends bound");
//        		System.err.println("\tlhs = " + lhs + "\t" + rhs);
//        	}
        }

        if (lhs.getKind() == TypeKind.TYPEVAR && rhs.getKind() != TypeKind.TYPEVAR) {
            if (visited.contains(lhs.getElement())) return true;
            visited.add(lhs.getElement());
            // TODO: the following two lines were added to make tests/nullness/MethodTypeVars2 pass.
            // Is this correct or just a quick fix?
            if (visited.contains(((AnnotatedTypeVariable)lhs).getUpperBound().getElement())) return true;
            visited.add(((AnnotatedTypeVariable)lhs).getUpperBound().getElement());
            return isSubtype(rhs, ((AnnotatedTypeVariable)lhs).getUpperBound());
        }

        Set<AnnotationMirror> las = lhs.getAnnotations();
        Set<AnnotationMirror> ras = rhs.getAnnotations();

        // We loose the restriction
        if (!qualifierHierarchy.isSubtype(ras, las)) {
        	if (!ras.isEmpty() && !las.isEmpty()) {
//        		System.err.println("WARN: incompatible qualifiers on type arguments, ignored\n\tlhs: " + lhs + " rhs: " + rhs);
        	}
        }

        if (lhs.getKind() == TypeKind.DECLARED && rhs.getKind() == TypeKind.DECLARED)
            return isSubtypeTypeArguments((AnnotatedDeclaredType)rhs, (AnnotatedDeclaredType)lhs);
        else if (lhs.getKind() == TypeKind.ARRAY && rhs.getKind() == TypeKind.ARRAY) {
            // arrays components within type arguments are invariants too
            // List<String[]> is not a subtype of List<Object[]>
            AnnotatedTypeMirror rhsComponent = ((AnnotatedArrayType)rhs).getComponentType();
            AnnotatedTypeMirror lhsComponent = ((AnnotatedArrayType)lhs).getComponentType();
            return isSubtypeAsTypeArgument(rhsComponent, lhsComponent);
        }
        return true;
	}

	@Override
	protected boolean isSubtypeTypeArguments(AnnotatedDeclaredType rhs, AnnotatedDeclaredType lhs) {
        List<AnnotatedTypeMirror> rhsTypeArgs = rhs.getTypeArguments();
        List<AnnotatedTypeMirror> lhsTypeArgs = lhs.getTypeArguments();

        if (rhsTypeArgs.isEmpty() || lhsTypeArgs.isEmpty())
            return true;

        assert lhsTypeArgs.size() == rhsTypeArgs.size();
        if (lhsTypeArgs.size() != rhsTypeArgs.size())
        	System.out.println();
        for (int i = 0; i < lhsTypeArgs.size(); ++i) {
            if (!isSubtypeAsTypeArgument(rhsTypeArgs.get(i), lhsTypeArgs.get(i)))
                return false;
        }

        return true;
    }

	/**
	 * Do not enforce that array components are co-variant
	 */
	@Override
	protected boolean isSubtypeAsArrayComponent(AnnotatedTypeMirror rhs,
			AnnotatedTypeMirror lhs) {
//		return super.isSubtypeAsArrayComponent(rhs, lhs);
		return isSubtypeImpl(rhs, lhs);
	}
	
	
	
}
