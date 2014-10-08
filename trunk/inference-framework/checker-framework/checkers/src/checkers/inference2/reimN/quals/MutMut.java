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

import javax.lang.model.type.TypeKind;

import checkers.quals.ImplicitFor;
import checkers.quals.SubtypeOf;
import checkers.quals.TypeQualifier;

/**
 * @author dongy6
 *
 */
@Documented
@TypeQualifier
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@SubtypeOf({PolyPoly.class, ReadRead.class})
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@ImplicitFor(
	types={TypeKind.NULL})
public @interface MutMut {

}
