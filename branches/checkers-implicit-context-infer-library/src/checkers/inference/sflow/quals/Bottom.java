/**
 * 
 */
package checkers.inference.sflow.quals;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Target;

import javax.lang.model.type.TypeKind;

import checkers.inference.reim.quals.Readonly;
import checkers.quals.ImplicitFor;
import checkers.quals.SubtypeOf;
import checkers.quals.TypeQualifier;

@Documented
@TypeQualifier
@Inherited
@SubtypeOf({Top.class, Poly.class, Tainted.class, Safe.class})
//@ImplicitFor(
//	types={TypeKind.NULL}
//    )
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
public @interface Bottom {
    
}
