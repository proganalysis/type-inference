/**
 * 
 */
package checkers.inference2;

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
	
}
