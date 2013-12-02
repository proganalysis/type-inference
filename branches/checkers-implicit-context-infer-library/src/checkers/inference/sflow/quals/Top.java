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
import checkers.quals.Unqualified;

@Documented
@TypeQualifier
@Inherited
@SubtypeOf({Unqualified.class})
//@ImplicitFor(
//	types={TypeKind.NULL}
//    )
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
public @interface Top {
    
}

