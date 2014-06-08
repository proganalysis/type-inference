/**
 * 
 */
package checkers.inference2.reimN.quals;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.CompoundAssignmentTree;
import com.sun.source.tree.LiteralTree;

import checkers.quals.ImplicitFor;
import checkers.quals.SubtypeOf;
import checkers.quals.TypeQualifier;
import checkers.quals.Unqualified;
import checkers.types.AnnotatedTypeMirror.AnnotatedPrimitiveType;

/**
 * @author dongy6
 *
 */
@Documented
@TypeQualifier
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@SubtypeOf({Unqualified.class})
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@ImplicitFor(
    treeClasses={LiteralTree.class, BinaryTree.class, CompoundAssignmentTree.class},
    typeClasses={AnnotatedPrimitiveType.class})
public @interface ReadRead {

}
