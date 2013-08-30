/**
 * 
 */
package checkers.inference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Name;
import javax.lang.model.type.TypeKind;

import checkers.inference.Reference.AdaptReference;
import checkers.inference.Reference.ArrayReference;
import checkers.inference.Reference.DeclaredReference;
import checkers.inference.Reference.ExecutableReference;
import checkers.types.AnnotatedTypeMirror;
import checkers.types.AnnotatedTypeMirror.AnnotatedArrayType;
import checkers.types.AnnotatedTypeMirror.AnnotatedDeclaredType;
import checkers.types.AnnotatedTypeMirror.AnnotatedExecutableType;
import checkers.types.AnnotatedTypeMirror.AnnotatedTypeVariable;
import checkers.types.AnnotatedTypeMirror.AnnotatedWildcardType;
import checkers.types.AnnotatedTypes;
import checkers.util.AnnotationUtils;
import checkers.util.TreeUtils;

import com.sun.source.tree.ArrayAccessTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.PrimitiveTypeTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.TypeCastTree;
import com.sun.tools.javac.code.Symbol.ClassSymbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.Type.ClassType;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeInfo;

/**
 * @author Wei Huang
 * @date Apr 29, 2011
 */
public class InferenceUtils {
	
	/**
	 * make deep copy of <code>inType</code>
	 * @param inType The type to be copied
	 * @param copyAnnotations  Indicates if the annotations are copied
	 * @return
	 */
//	public static AnnotatedTypeMirror getDeepCopy(AnnotatedTypeMirror inType, 
//			boolean copyAnnotations) {
//		if (inType == null) {
//			System.err.println("Null type!");
//		}
//		AnnotatedTypeMirror outType = inType.getCopy(copyAnnotations);
//		if (outType.getKind() == TypeKind.ARRAY) {
//			((AnnotatedArrayType) outType).setComponentType(getDeepCopy(
//					((AnnotatedArrayType) inType).getComponentType(),
//					copyAnnotations));
//		}
//		else if (outType.getKind() == TypeKind.EXECUTABLE) {
//			AnnotatedExecutableType aType = (AnnotatedExecutableType) outType;
//			List<AnnotatedTypeMirror> parameterTypes = aType.getParameterTypes();
//			List<AnnotatedTypeMirror> newParameterTypes = new ArrayList<AnnotatedTypeMirror>(
//					parameterTypes.size());
//			for (AnnotatedTypeMirror pt : parameterTypes) {
//				newParameterTypes.add(getDeepCopy(pt, copyAnnotations));
//			} 
//			aType.setParameterTypes(newParameterTypes);
//			AnnotatedTypeMirror newReturnType = getDeepCopy(aType.getReturnType(), copyAnnotations);
//			aType.setReturnType(newReturnType);
//			AnnotatedDeclaredType newRcvType  = (AnnotatedDeclaredType) 
//					getDeepCopy(aType.getReceiverType(), copyAnnotations);
//			aType.setReceiverType(newRcvType);
//		}
//		else if (outType.getKind() == TypeKind.DECLARED) {
//		    List<AnnotatedTypeMirror> oldTypeArgs = ((AnnotatedDeclaredType) inType).getTypeArguments();
//		    List<AnnotatedTypeMirror> newTypeArgs = new ArrayList<AnnotatedTypeMirror>();
//		    for (AnnotatedTypeMirror at : oldTypeArgs) {
//		        newTypeArgs.add(getDeepCopy(at, copyAnnotations));
//		    }
//		    ((AnnotatedDeclaredType) outType).setTypeArguments(newTypeArgs);
//		}
//		else if (outType.getKind() == TypeKind.TYPEVAR) {
//		    AnnotatedTypeVariable aType = (AnnotatedTypeVariable) outType;
//		    AnnotatedTypeMirror upperBound = aType.getUpperBound().getCopy(copyAnnotations);
//		    aType.setUpperBound(upperBound);
//		}
//		else if (outType.getKind() == TypeKind.WILDCARD) {
//		    AnnotatedWildcardType aType = (AnnotatedWildcardType) outType;
//		    AnnotatedTypeMirror upperBound = aType.getExtendsBound().getCopy(copyAnnotations);
//		    aType.setExtendsBound(upperBound);
//		}
//		return outType;
//	}
	

	/**
	 * Return a copy of <code>inType</code> with only the "maximal" annotation
	 * remaining. The "maximal" is determined by the <code>comparator</code>
	 * @param inType
	 * @param comparator
	 * @return
	 */
	public static AnnotatedTypeMirror getMaxType(AnnotatedTypeMirror inType, 
			Comparator<AnnotationMirror> comparator) {
//		AnnotatedTypeMirror outType = getDeepCopy(inType, true);
		AnnotatedTypeMirror outType = AnnotatedTypes.deepCopy(inType);
		if (outType.getKind() == TypeKind.ARRAY) {
			((AnnotatedArrayType) outType).setComponentType(getMaxType(
					((AnnotatedArrayType) outType).getComponentType(),
					comparator));
		} 
		else if (outType.getKind() == TypeKind.DECLARED) {
			List<AnnotatedTypeMirror> oldTypeArgs = ((AnnotatedDeclaredType) outType)
					.getTypeArguments();
			List<AnnotatedTypeMirror> newTypeArgs = new ArrayList<AnnotatedTypeMirror>(
					oldTypeArgs.size());
            for (AnnotatedTypeMirror t : oldTypeArgs) {
                newTypeArgs.add(getMaxType(t, comparator));
            }
            ((AnnotatedDeclaredType) outType).setTypeArguments(newTypeArgs);
		}
		else if (outType.getKind() == TypeKind.EXECUTABLE) {
		    AnnotatedExecutableType aType = (AnnotatedExecutableType) outType;
		    
		    AnnotatedDeclaredType receiverType = aType.getReceiverType();
		    AnnotatedTypeMirror t = getMaxType(receiverType, comparator);
		    InferenceUtils.assignAnnotations(receiverType, t);
		    
		    AnnotatedTypeMirror returnType = aType.getReturnType();
		    t = getMaxType(returnType, comparator);
		    InferenceUtils.assignAnnotations(returnType, t);
		    
		    for (AnnotatedTypeMirror tp : aType.getParameterTypes()) {
		        AnnotatedTypeMirror mostSpecificType = getMaxType(tp, comparator);
		        InferenceUtils.assignAnnotations(tp, mostSpecificType);
		    }
		}
		else if (outType.getKind() == TypeKind.TYPEVAR) {
		    AnnotatedTypeVariable aType = (AnnotatedTypeVariable) outType;
		    AnnotatedTypeMirror upperBound = aType.getUpperBound();
    		Set<AnnotationMirror> set = new TreeSet<AnnotationMirror>(comparator);
		    set.addAll(upperBound.getAnnotations());
		    if (!set.isEmpty()) {
    		    upperBound.clearAnnotations();
    		    upperBound.addAnnotation(set.iterator().next());
		    }
		}
		else if (outType.getKind() == TypeKind.WILDCARD) {
		    AnnotatedWildcardType aType = (AnnotatedWildcardType) outType;
		    AnnotatedTypeMirror upperBound = aType.getExtendsBound();
    		Set<AnnotationMirror> set = new TreeSet<AnnotationMirror>(comparator);
		    set.addAll(upperBound.getAnnotations());
		    if (!set.isEmpty()) {
    		    upperBound.clearAnnotations();
    		    upperBound.addAnnotation(set.iterator().next());
		    }
		}
		if (outType.isAnnotated()) {
			AnnotationMirror[] current = outType.getAnnotations().toArray(new AnnotationMirror[0]);
			Arrays.sort(current, comparator);
			outType.clearAnnotations();
			outType.addAnnotation(current[0]);
		}
		return outType;
	}
	
	/**
	 * A Helper method to print annotations separated with a space
	 * @param lst
	 * @return
	 */
	public final static String formatAnnotationString(Collection<? extends AnnotationMirror> lst) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (AnnotationMirror obj : lst) {
			if (!obj.getElementValues().isEmpty()) {
				sb.append(obj.toString());
				sb.append(" ");
				continue;
			}
			if (first)
				first = false;
			else
				sb.append(" ");
			sb.append("@");
			sb.append(obj.getAnnotationType().asElement().getSimpleName());
		}
		return sb.toString();
	}

	public static String formatAnntationType(AnnotatedTypeMirror atype, 
			Comparator<AnnotationMirror> c) {
		Set<AnnotationMirror> set = new TreeSet<AnnotationMirror>(c);
		set.addAll(atype.getAnnotations());
		return formatAnnotationString(set) + atype.getCopy(false);
	}
	
	/**
	 * Check if <code>type1</code> and <code>type2</code> have the same annoations
	 * @param type1
	 * @param type2
	 * @return
	 */
	public static boolean equalAnnotations(AnnotatedTypeMirror type1, AnnotatedTypeMirror type2) {
		if (type1 == null || type2 == null)
			System.out.println();
		boolean result = true;
		if (type1.getKind() == TypeKind.ARRAY && type2.getKind() == TypeKind.ARRAY) {
			AnnotatedTypeMirror componentType1 = ((AnnotatedArrayType) type1)
					.getComponentType();
			AnnotatedTypeMirror componentType2 = ((AnnotatedArrayType) type2)
					.getComponentType();
			result = result && equalAnnotations(componentType1, componentType2);
		} 
        else if (type1.getKind() == TypeKind.EXECUTABLE
                && type2.getKind() == TypeKind.EXECUTABLE) {
			AnnotatedExecutableType typeExe1 = (AnnotatedExecutableType) type1;
			AnnotatedExecutableType typeExe2 = (AnnotatedExecutableType) type2;
			List<AnnotatedTypeMirror> parameterTypes1 = typeExe1.getParameterTypes();
			List<AnnotatedTypeMirror> parameterTypes2 = typeExe2.getParameterTypes();
			for (int i = 0; i < parameterTypes1.size(); i++) {
				result = result
						&& equalAnnotations(parameterTypes1.get(i),
								parameterTypes2.get(i));
			}
			result = result && equalAnnotations(typeExe1.getReturnType(),
							typeExe2.getReturnType());
			result = result && equalAnnotations(typeExe1.getReceiverType(),
							typeExe2.getReceiverType());
		} 
        else if (type1.getKind() == TypeKind.DECLARED && type2.getKind() == TypeKind.DECLARED) {
            AnnotatedDeclaredType aType1 = (AnnotatedDeclaredType) type1;
            AnnotatedDeclaredType aType2 = (AnnotatedDeclaredType) type2;
            if (aType1.getTypeArguments().size() != aType2.getTypeArguments().size())
            	return false;
        }
		result = result && type1.getAnnotations().equals(type2.getAnnotations());
		return result;
	}

    /**
     * Assign annotations of exprType to varType
     * 
     * @param varType
     * @param exprType
     */
    public static void assignAnnotations(AnnotatedTypeMirror varType, AnnotatedTypeMirror exprType) {
        if (equalAnnotations(varType, exprType))
            return;
        if (varType.getKind() == TypeKind.ARRAY && exprType.getKind() == TypeKind.ARRAY) {
            AnnotatedTypeMirror varComponentType = ((AnnotatedArrayType) varType)
                    .getComponentType();
            AnnotatedTypeMirror exprComponentType = ((AnnotatedArrayType) exprType)
                    .getComponentType();
            assignAnnotations(varComponentType, exprComponentType);
        } 
        else if (varType.getKind() == TypeKind.EXECUTABLE
                && exprType.getKind() == TypeKind.EXECUTABLE) {
            AnnotatedExecutableType varTypeExe = (AnnotatedExecutableType) varType;
            AnnotatedExecutableType exprTypeExe = (AnnotatedExecutableType) exprType;
            assignAnnotations(varTypeExe.getReturnType(), exprTypeExe.getReturnType());
            assignAnnotations(varTypeExe.getReceiverType(), exprTypeExe.getReceiverType());
            List<AnnotatedTypeMirror> varParameterTypes = varTypeExe.getParameterTypes();
            List<AnnotatedTypeMirror> exprParameterTypes = exprTypeExe.getParameterTypes();
            for (int i = 0; i < varParameterTypes.size(); i++) {
                assignAnnotations(varParameterTypes.get(i), exprParameterTypes.get(i));
            }
        } 
        else if (varType instanceof AnnotatedExecutableType && !(exprType instanceof AnnotatedTypeVariable)) {
            AnnotatedExecutableType varTypeExe = (AnnotatedExecutableType) varType;
            assignAnnotations(varTypeExe.getReturnType(), exprType);
        }
        else if (varType.getKind() == TypeKind.DECLARED
                && exprType.getKind() == TypeKind.DECLARED) {
            List<AnnotatedTypeMirror> varTypeArguments = ((AnnotatedDeclaredType) varType).getTypeArguments();
            List<AnnotatedTypeMirror> exprTypeArguments = ((AnnotatedDeclaredType) exprType).getTypeArguments();
            if (varTypeArguments.size() == exprTypeArguments.size())
	            for (Iterator<AnnotatedTypeMirror> itVar = varTypeArguments.iterator(), 
	                    itExpr = exprTypeArguments.iterator(); itVar.hasNext() && itExpr.hasNext();) {
	                AnnotatedTypeMirror leftVar = itVar.next();
	                AnnotatedTypeMirror rightExpr = itExpr.next();
	                assignAnnotations(leftVar, rightExpr);
	            }
            else if (varTypeArguments.isEmpty()) {
            	((AnnotatedDeclaredType) varType).setTypeArguments(exprTypeArguments);
            }
        }
        else if (varType.getKind() == TypeKind.WILDCARD && exprType.getKind() == TypeKind.WILDCARD) {
		    AnnotatedWildcardType aTypeVar = (AnnotatedWildcardType) varType;
		    AnnotatedWildcardType aTypeExpr = (AnnotatedWildcardType) exprType;
            AnnotatedTypeMirror upperBoundVar = aTypeVar.getExtendsBound();
            AnnotatedTypeMirror upperBoundExpr = aTypeExpr.getExtendsBound();
            upperBoundVar.clearAnnotations();
            upperBoundVar.addAnnotations(upperBoundExpr.getAnnotations());
//		    assignAnnotations(aTypeVar.getExtendsBound(), aTypeExpr.getExtendsBound());
        }
        else if (varType.getKind() == TypeKind.TYPEVAR && exprType.getKind() == TypeKind.TYPEVAR) {
            AnnotatedTypeVariable aTypeVar = (AnnotatedTypeVariable) varType;
            AnnotatedTypeVariable aTypeExpr = (AnnotatedTypeVariable) exprType;
            // be careful of recursion
            AnnotatedTypeMirror upperBoundVar = aTypeVar.getUpperBound();
            AnnotatedTypeMirror upperBoundExpr = aTypeExpr.getUpperBound();
            upperBoundVar.clearAnnotations();
            upperBoundVar.addAnnotations(upperBoundExpr.getAnnotations());
//		    assignAnnotations(aTypeVar.getUpperBound(), aTypeExpr.getUpperBound());
        }
        else if (varType.getKind() == TypeKind.TYPEVAR && exprType.getKind() == TypeKind.WILDCARD) {
            AnnotatedTypeVariable aTypeVar = (AnnotatedTypeVariable) varType;
		    AnnotatedWildcardType aTypeExpr = (AnnotatedWildcardType) exprType;
            AnnotatedTypeMirror upperBoundVar = aTypeVar.getUpperBound();
            AnnotatedTypeMirror upperBoundExpr = aTypeExpr.getExtendsBound();
            upperBoundVar.clearAnnotations();
            upperBoundVar.addAnnotations(upperBoundExpr.getAnnotations());
        }
        else if (varType.getKind() == TypeKind.WILDCARD && exprType.getKind() == TypeKind.TYPEVAR) {
		    AnnotatedWildcardType aTypeVar = (AnnotatedWildcardType) varType;
            AnnotatedTypeVariable aTypeExpr = (AnnotatedTypeVariable) exprType;
            AnnotatedTypeMirror upperBoundVar = aTypeVar.getExtendsBound();
            AnnotatedTypeMirror upperBoundExpr = aTypeExpr.getUpperBound();
            upperBoundVar.clearAnnotations();
            upperBoundVar.addAnnotations(upperBoundExpr.getAnnotations());
        }
        
        if (exprType.isAnnotated()) {
            Set<AnnotationMirror> annotations = exprType.getAnnotations();
            varType.clearAnnotations();
            if (annotations.isEmpty())
                System.out.println("");
            varType.addAnnotations(annotations);
        }
    }
    
    
    /**
     * Merge the annotations of two types
     * @param type1
     * @param type2
     */
//    public static void mergeAnnotations(AnnotatedTypeMirror type1, AnnotatedTypeMirror type2) {
//    	AnnotatedTypeMirror res = type1.getCopy(true);
//        if (equalAnnotations(res, type2))
//            return;
//        if (res.getKind() == TypeKind.ARRAY && type2.getKind() == TypeKind.ARRAY) {
//            AnnotatedTypeMirror varComponentType = ((AnnotatedArrayType) res)
//                    .getComponentType();
//            AnnotatedTypeMirror exprComponentType = ((AnnotatedArrayType) type2)
//                    .getComponentType();
//            mergeAnnotations(varComponentType, exprComponentType);
//        } 
//        else if (res.getKind() == TypeKind.EXECUTABLE
//                && type2.getKind() == TypeKind.EXECUTABLE) {
//            AnnotatedExecutableType varTypeExe = (AnnotatedExecutableType) res;
//            AnnotatedExecutableType exprTypeExe = (AnnotatedExecutableType) type2;
//            mergeAnnotations(varTypeExe.getReturnType(), exprTypeExe.getReturnType());
//            mergeAnnotations(varTypeExe.getReceiverType(), exprTypeExe.getReceiverType());
//            List<AnnotatedTypeMirror> varParameterTypes = varTypeExe.getParameterTypes();
//            List<AnnotatedTypeMirror> exprParameterTypes = exprTypeExe.getParameterTypes();
//            for (int i = 0; i < varParameterTypes.size(); i++) {
//                mergeAnnotations(varParameterTypes.get(i), exprParameterTypes.get(i));
//            }
//        } 
//        else if (res instanceof AnnotatedExecutableType && !(type2 instanceof AnnotatedTypeVariable)) {
//            AnnotatedExecutableType varTypeExe = (AnnotatedExecutableType) res;
//            mergeAnnotations(varTypeExe.getReturnType(), type2);
//        }
//        else if (res.getKind() == TypeKind.DECLARED
//                && type2.getKind() == TypeKind.DECLARED) {
//            List<AnnotatedTypeMirror> varTypeArguments = ((AnnotatedDeclaredType) res).getTypeArguments();
//            List<AnnotatedTypeMirror> exprTypeArguments = ((AnnotatedDeclaredType) type2).getTypeArguments();
//            if (varTypeArguments.size() == exprTypeArguments.size())
//	            for (Iterator<AnnotatedTypeMirror> itVar = varTypeArguments.iterator(), 
//	                    itExpr = exprTypeArguments.iterator(); itVar.hasNext() && itExpr.hasNext();) {
//	                AnnotatedTypeMirror leftVar = itVar.next();
//	                AnnotatedTypeMirror rightExpr = itExpr.next();
//	                mergeAnnotations(leftVar, rightExpr);
//	            }
//            else if (varTypeArguments.isEmpty()) {
//            	((AnnotatedDeclaredType) res).setTypeArguments(exprTypeArguments);
//            }
//        }
//        else if (res.getKind() == TypeKind.WILDCARD && type2.getKind() == TypeKind.WILDCARD) {
//		    AnnotatedWildcardType aTypeVar = (AnnotatedWildcardType) res;
//		    AnnotatedWildcardType aTypeExpr = (AnnotatedWildcardType) type2;
//            AnnotatedTypeMirror upperBoundVar = aTypeVar.getExtendsBound();
//            AnnotatedTypeMirror upperBoundExpr = aTypeExpr.getExtendsBound();
//            upperBoundVar.clearAnnotations();
//            upperBoundVar.addAnnotations(upperBoundExpr.getAnnotations());
////		    assignAnnotations(aTypeVar.getExtendsBound(), aTypeExpr.getExtendsBound());
//        }
//        else if (res.getKind() == TypeKind.TYPEVAR && type2.getKind() == TypeKind.TYPEVAR) {
//            AnnotatedTypeVariable aTypeVar = (AnnotatedTypeVariable) res;
//            AnnotatedTypeVariable aTypeExpr = (AnnotatedTypeVariable) type2;
//            // be careful of recursion
//            AnnotatedTypeMirror upperBoundVar = aTypeVar.getUpperBound();
//            AnnotatedTypeMirror upperBoundExpr = aTypeExpr.getUpperBound();
//            upperBoundVar.clearAnnotations();
//            upperBoundVar.addAnnotations(upperBoundExpr.getAnnotations());
//        }
//        else if (res.getKind() == TypeKind.TYPEVAR && type2.getKind() == TypeKind.WILDCARD) {
//            AnnotatedTypeVariable aTypeVar = (AnnotatedTypeVariable) res;
//		    AnnotatedWildcardType aTypeExpr = (AnnotatedWildcardType) type2;
//            AnnotatedTypeMirror upperBoundVar = aTypeVar.getUpperBound();
//            AnnotatedTypeMirror upperBoundExpr = aTypeExpr.getExtendsBound();
//            upperBoundVar.clearAnnotations();
//            upperBoundVar.addAnnotations(upperBoundExpr.getAnnotations());
//        }
//        else if (res.getKind() == TypeKind.WILDCARD && type2.getKind() == TypeKind.TYPEVAR) {
//		    AnnotatedWildcardType aTypeVar = (AnnotatedWildcardType) res;
//            AnnotatedTypeVariable aTypeExpr = (AnnotatedTypeVariable) type2;
//            AnnotatedTypeMirror upperBoundVar = aTypeVar.getExtendsBound();
//            AnnotatedTypeMirror upperBoundExpr = aTypeExpr.getUpperBound();
//            upperBoundVar.clearAnnotations();
//            upperBoundVar.addAnnotations(upperBoundExpr.getAnnotations());
//        }
//        
//        if (type2.isAnnotated()) {
//            Set<AnnotationMirror> annotations = type2.getAnnotations();
//            res.clearAnnotations();
//            if (annotations.isEmpty())
//                System.out.println("");
//            res.addAnnotations(annotations);
//        }
//    }
    
    /**
     * Intersect two annotation sets by comparing the toString() of each annotations
     * The the equals() method is implemented differently in {@link AnnotationMirror}
     * @param set1
     * @param set2
     * @return
     */
	public static Set<AnnotationMirror> intersectAnnotations(
			Set<AnnotationMirror> set1, Set<AnnotationMirror> set2) {
		Set<AnnotationMirror> res = AnnotationUtils.createAnnotationSet();
		for (AnnotationMirror anno1 : set1) {
			for (AnnotationMirror anno2 : set2) {
				if (anno1.toString().equals(anno2.toString())) {
					res.add(anno1);
				}
			}
		}
		return res;
	}
	
	/**
	 * Remove from {@code set1} the elements appeared in {@code set2}
	 * @param set1
	 * @param set2
	 * @return
	 */
	public static Set<AnnotationMirror> differAnnotations(
			Set<AnnotationMirror> set1, Set<AnnotationMirror> set2) {
		Set<AnnotationMirror> res = AnnotationUtils.createAnnotationSet();
		res.addAll(set1);
		for (Iterator<AnnotationMirror> it = res.iterator();
				it.hasNext();) {
			AnnotationMirror anno1 = it.next();
			for (AnnotationMirror anno2 : set2) {
				if (anno1 != null && anno2 != null 
						&& anno1.toString().equals(anno2.toString())) {
					it.remove();
					break;
				}
//				else if (anno1 == null || anno2 == null)
//					System.out.println("WARN: null annotations. " + set1 + "\t" + set2);
			}
		}
		return res;
	}
	
	/**
	 * Check if <code>subType</code> is the subtype of <code>supType</code>
	 * @param subType
	 * @param supType
	 * @return
	 */
	public static boolean isSubtype(ClassType subType, ClassType supType) {
		boolean result = false;
		Type supertype_field = ((ClassType) subType).supertype_field;
		while (supertype_field != null) {
			if (supertype_field.toString().equals(supType.toString())) {
				result = true;
				break;
			} else if (supertype_field instanceof ClassType) {
				supertype_field = ((ClassType) supertype_field).supertype_field;
			} else
				break;
		}
		return result; 
	}
	
	/**
	 * Extract the method signature of <code>methodElt</code>
	 * @param elt
	 * @return
	 */
	public static String getMethodSignature(Element elt) {
		Element enclosingElement = elt.getEnclosingElement();
		while (enclosingElement != null
				&& enclosingElement.getKind() != ElementKind.CLASS
				&& enclosingElement.getKind() != ElementKind.INTERFACE
				&& enclosingElement.getKind() != ElementKind.ENUM
				&& enclosingElement.getKind() != ElementKind.ANNOTATION_TYPE)
			enclosingElement = enclosingElement.getEnclosingElement();
		if (enclosingElement == null)
			System.out.println();
		return ((ClassSymbol) enclosingElement).flatName() + "." + elt.toString();
	}
	
	public static String getElementSignature(Element elt) {
		Element enclosingElement = elt;
		if (elt.getKind() == ElementKind.LOCAL_VARIABLE) {
			throw new RuntimeException("Cannot get signature for local variables: " + elt.toString());
		}
		if (elt.getKind() == ElementKind.CLASS || elt.getKind() == ElementKind.INTERFACE) 
			return ((ClassSymbol) elt).flatName().toString();
		if (enclosingElement.getKind() == ElementKind.METHOD
				|| enclosingElement.getKind() == ElementKind.FIELD
				|| enclosingElement.getKind() == ElementKind.ENUM_CONSTANT
				|| enclosingElement.getKind() == ElementKind.TYPE_PARAMETER
				|| enclosingElement.getKind() == ElementKind.CONSTRUCTOR) {
			return getMethodSignature(elt);
		} 
		while (enclosingElement != null
				&& enclosingElement.getKind() != ElementKind.METHOD
				&& enclosingElement.getKind() != ElementKind.CONSTRUCTOR)
			enclosingElement = enclosingElement.getEnclosingElement();
		if (enclosingElement == null) {
//			System.err.println("WARN: Get element's signature failed: " + elt 
//					+ " type: " + elt.getKind());
			return null;
		}
		return getMethodSignature(enclosingElement) + "." + elt.toString();
	}
	
	public static String getTreeSignature(ExpressionTree tree, 
			ClassSymbol enclosingClass) {
		return enclosingClass.flatName() + "." + tree.toString() + ":"
				+ TreeInfo.getStartPos((JCTree) tree);
	}
	
	/**
	 * Assign the annotations of a Reference to an AnnotatedTypeMirror
	 * @param type
	 * @param ref
	 */
	public static void annotateReferenceType(AnnotatedTypeMirror type, 
			Reference ref) {
		annotateReferenceType(type, ref, true);
	}
	
	public static void  printMergedTypeErrors(List<Constraint> constraints) {
		Map<Reference, Set<Reference>> map = new LinkedHashMap<Reference, Set<Reference>>();
		for (Constraint c : constraints) {
			Reference left = c.getLeft();
			Set<Reference> set = map.get(left);
			if (set == null) {
				set = new HashSet<Reference>(3);
				map.put(left, set);
			}
			set.add(c.getRight());
		}
		// TODO: Now for the right side
		System.out.println("INFO: There are " + map.size() + " type errors:");
		for (Entry<Reference, Set<Reference>> entry : map.entrySet()) {
			System.out.print(entry.getKey().toAnnotatedString() + " =!= ");
			Set<Reference> value = entry.getValue();
			if (value.size() > 1) 
				System.out.print("[");
			for (Iterator<Reference> it = value.iterator(); it.hasNext();) {
				System.out.print(it.next().toAnnotatedString());
				if (it.hasNext())
					System.out.print(", ");
			}
			
			if (value.size() > 1) 
				System.out.print("]");
			System.out.println();
		}
		
//		Map<Reference, List<Reference>> map = new LinkedHashMap<Reference, List<Reference>>();
//		for (Constraint c : constraints) {
//			Reference left = c.getLeft();
//			List<Reference> set = map.get(left);
//			if (set == null) {
//				set = new ArrayList<Reference>(3);
//				map.put(left, set);
//			}
//			set.add(c.getRight());
//			System.out.println("processing " + c);
//			System.out.println(left.getClass());
//			if (left instanceof AdaptReference)
//				System.out.println("set = " + set);
//		}
//		// TODO: Now for the right side
//		System.out.println("INFO: There are " + map.size() + " type errors:");
//		for (Entry<Reference, List<Reference>> entry : map.entrySet()) {
//			System.out.println(entry.getKey() + " =!= " + entry.getValue());
//		}
	}
	
	/**
	 * Assign the annotations of a Reference to an AnnotatedTypeMirror
	 * @param type
	 * @param ref
	 */
	public static void annotateReferenceType(AnnotatedTypeMirror type, 
			Reference ref, boolean clearOldAnnos) {
		// Add annotations if unannotated???
		if (!ref.getAnnotations().isEmpty()) {
			if (clearOldAnnos)
				type.clearAnnotations();
			type.addAnnotations(ref.getAnnotations());
		} 
		if (type instanceof AnnotatedArrayType 
				&& ref instanceof ArrayReference) {
			annotateReferenceType(((AnnotatedArrayType) type).getComponentType(),
					((ArrayReference) ref).getComponentRef(), clearOldAnnos);
		} else if (type instanceof AnnotatedDeclaredType 
				&& ref instanceof DeclaredReference) {
			List<AnnotatedTypeMirror> typeArgs = ((AnnotatedDeclaredType) type)
					.getTypeArguments();
			List<? extends Reference> refArgs = ((DeclaredReference) ref)
					.getTypeArguments();
			if (typeArgs.size() != refArgs.size()) {
			} else {
				for (Iterator<AnnotatedTypeMirror> typeIt = typeArgs.iterator();
						typeIt.hasNext();) {
					for (Iterator<? extends Reference> refIt = refArgs.iterator(); 
							refIt.hasNext();) {
						annotateReferenceType(typeIt.next(), refIt.next(), clearOldAnnos);
					}
				}
			}
		} else if (type instanceof AnnotatedExecutableType
				&& ref instanceof ExecutableReference) {
			AnnotatedExecutableType aType = (AnnotatedExecutableType) type;
			ExecutableReference aRef = (ExecutableReference) ref;
			annotateReferenceType(aType.getReceiverType(), aRef.getReceiverRef(), clearOldAnnos);
			annotateReferenceType(aType.getReturnType(), aRef.getReturnRef(), clearOldAnnos);
			List<AnnotatedTypeMirror> paramTypes = aType.getParameterTypes();
			List<Reference> paramRefs = aRef.getParamRefs();
			if (paramRefs.size() != paramTypes.size())
				throw new RuntimeException("Incompatible Types!" + type + " vs " + ref); 
			for (Iterator<AnnotatedTypeMirror> typeIt = paramTypes.iterator(); typeIt
					.hasNext();) {
				for (Iterator<Reference> refIt = paramRefs.iterator(); refIt
						.hasNext();) {
					annotateReferenceType(typeIt.next(), refIt.next(), clearOldAnnos);
				}
			}
		} else if (type instanceof AnnotatedWildcardType) {
			AnnotatedTypeMirror superBound = ((AnnotatedWildcardType) type)
					.getSuperBound();
			if (superBound != null)
				superBound.addAnnotations(ref.getAnnotations());
			AnnotatedTypeMirror extendsBound = ((AnnotatedWildcardType) type)
					.getExtendsBound();
			if (extendsBound != null)
				extendsBound.addAnnotations(ref.getAnnotations());
		} else if (type instanceof AnnotatedTypeVariable) {
			AnnotatedTypeMirror lowerBound = ((AnnotatedTypeVariable) type)
					.getLowerBound();
			if (lowerBound != null)
				lowerBound.addAnnotations(ref.getAnnotations());
			AnnotatedTypeMirror upperBound = ((AnnotatedTypeVariable) type)
					.getUpperBound();
			if (upperBound != null)
				upperBound.addAnnotations(ref.getAnnotations());
		}
	}
    
    private static long id = 1;
    public static synchronized long getUniqueID() {
    	return id++;
    }
    
    
    /**
     * Returns the receiver tree of a field access or a method invocation
     */
    public static ExpressionTree getReceiverTree(ExpressionTree expression) {
        if (!(expression.getKind() == Tree.Kind.METHOD_INVOCATION
                || expression.getKind() == Tree.Kind.MEMBER_SELECT
                || expression.getKind() == Tree.Kind.IDENTIFIER
                || expression.getKind() == Tree.Kind.ARRAY_ACCESS))
            // No receiver type for those
            return null;

        if (expression.getKind() == Tree.Kind.IDENTIFIER
            && "this".equals(expression.toString()))
            return null;

        ExpressionTree receiver = TreeUtils.skipParens(expression);
        if (receiver.getKind() == Tree.Kind.ARRAY_ACCESS)
            return ((ArrayAccessTree)receiver).getExpression();

        // Avoid int.class
        if (expression.getKind() == Tree.Kind.MEMBER_SELECT &&
                ((MemberSelectTree)expression).getExpression() instanceof PrimitiveTypeTree)
            return null;

        if (isSelfAccess(expression)) {
            return null;
        }

        //
        // Trying to handle receiver calls to trees of the form
        // ((m).getArray())
        // returns the type of 'm' in this case

        if (receiver.getKind() == Tree.Kind.METHOD_INVOCATION)
            receiver = ((MethodInvocationTree)receiver).getMethodSelect();
        receiver = TreeUtils.skipParens(receiver);
        assert receiver.getKind() == Tree.Kind.MEMBER_SELECT;
        if (receiver.getKind() == Tree.Kind.MEMBER_SELECT)
            receiver = ((MemberSelectTree)receiver).getExpression();

        return receiver;
    }
    
    /**
	 * Check if <code>exprTree</code> is a self access tree This improves the
	 * <code>isSelfAccess</code> in {@link checkers.util.TreeUtils}, where
	 * accessing a field on a casted "this" is recognized as self-access
	 * 
	 * TODO: WEI: But accessing a field on a casted "this" should also return
     * true, e.g. ((@mutable X) this).field
	 * 
	 * @param exprTree
	 * @return
	 */
    public static boolean isSelfAccess(final ExpressionTree exprTree) {
        ExpressionTree tr = TreeUtils.skipParens(exprTree);
        // If method invocation check the method select
        if (tr.getKind() == Tree.Kind.ARRAY_ACCESS)
            return false;

        if (exprTree.getKind() == Tree.Kind.METHOD_INVOCATION) {
            tr = ((MethodInvocationTree)exprTree).getMethodSelect();
        }
        tr = TreeUtils.skipParens(tr);
        if (tr.getKind() == Tree.Kind.TYPE_CAST)
            tr = ((TypeCastTree)tr).getExpression();
        tr = TreeUtils.skipParens(tr);

        if (tr.getKind() == Tree.Kind.IDENTIFIER)
            return true;

        if (tr.getKind() == Tree.Kind.MEMBER_SELECT) {
            tr = ((MemberSelectTree)tr).getExpression();

            // WEI:
//            while (true) {
//                if (tr.getKind() == Tree.Kind.PARENTHESIZED)
//                    tr = TreeUtils.skipParens(tr);
//                else if (tr.getKind() == Tree.Kind.TYPE_CAST)
//                    tr = ((TypeCastTree)tr).getExpression();
//                else break;
//            }

            if (tr.getKind() == Tree.Kind.IDENTIFIER) {
                Name ident = ((IdentifierTree)tr).getName();
                return ident.contentEquals("this") ||
                        ident.contentEquals("super");
            }
        }

        return false;
    }
    
   

	public static boolean isThisTree(Tree tree) {
		if (tree instanceof ExpressionTree)
			tree = TreeUtils.skipParens((ExpressionTree) tree);
		while (tree.getKind() == Kind.TYPE_CAST) {
			tree = ((TypeCastTree) tree).getExpression();
			if (tree instanceof ExpressionTree)
				tree = TreeUtils.skipParens((ExpressionTree) tree);
		}
		if (tree.toString().equals("this"))
			return true;
		return false;
	}

}
