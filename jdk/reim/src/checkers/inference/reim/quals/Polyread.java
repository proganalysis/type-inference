/**
 * 
 */
package checkers.inference.reim.quals;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import checkers.quals.SubtypeOf;
import checkers.quals.TypeQualifier;

/**
 * @author huangw5
 *
 */
@Documented
@TypeQualifier
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@SubtypeOf({Readonly.class})
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
public @interface Polyread {

}
