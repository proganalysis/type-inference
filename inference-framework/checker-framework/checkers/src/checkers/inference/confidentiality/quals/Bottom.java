/**
 * 
 */
package checkers.inference.confidentiality.quals;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Target;

import checkers.quals.SubtypeOf;
import checkers.quals.TypeQualifier;

@Documented
@TypeQualifier
@Inherited
@SubtypeOf({PPoly.class, PSecret.class, PTainted.class, RPoly.class, RSecret.class, RTainted.class})
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
public @interface Bottom {
    
}
