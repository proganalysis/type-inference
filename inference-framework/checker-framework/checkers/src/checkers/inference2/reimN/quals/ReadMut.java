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
@SubtypeOf({ReadRead.class, ReadPoly.class})
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
public @interface ReadMut {

}
