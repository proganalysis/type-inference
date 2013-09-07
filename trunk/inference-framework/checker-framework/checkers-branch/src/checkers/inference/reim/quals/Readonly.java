/**
 * 
 */
package checkers.inference.reim.quals;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Target;

import checkers.quals.ImplicitFor;
import checkers.quals.SubtypeOf;
import checkers.quals.TypeQualifier;
import checkers.quals.Unqualified;
import checkers.types.AnnotatedTypeMirror.AnnotatedPrimitiveType;

import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.CompoundAssignmentTree;
import com.sun.source.tree.LiteralTree;

/**
 * @author Wei Huang
 */
@Documented
@TypeQualifier
@Inherited
@SubtypeOf({Unqualified.class})
//@SubtypeOf({DefaultAnnotation.class})
//@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@ImplicitFor(
    treeClasses={LiteralTree.class, BinaryTree.class, CompoundAssignmentTree.class},
    typeClasses={AnnotatedPrimitiveType.class})
public @interface Readonly {
    
}
