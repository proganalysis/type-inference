/**
 * 
 */
package checkers.inference.aj.quals;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import checkers.quals.SubtypeOf;
import checkers.quals.TypeQualifier;

/**
 * @author Wei Huang
 * @date Feb 17, 2011
 */
@Documented
@TypeQualifier
@Inherited
@SubtypeOf({Aliased.class, NonAliased.class, IntAliased.class, Self.class, IntSelf.class})
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
//@ImplicitFor(
//    treeClasses={LiteralTree.class},
//    typeClasses={AnnotatedPrimitiveType.class})
public @interface AJBottom {

}
