/**
 * 
 */
package checkers.inference2.jcrypt2.quals;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import checkers.quals.SubtypeOf;
import checkers.quals.TypeQualifier;

@Retention(RetentionPolicy.RUNTIME) 
@Documented
@TypeQualifier
@Inherited
@SubtypeOf({RND.class, AH.class})
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
public @interface DET {
    
}
