/**
 * 
 */
package checkers.inference.reim.quals;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

import javax.lang.model.type.TypeKind;

import checkers.quals.DefaultQualifier;
import checkers.quals.DefaultQualifierInHierarchy;
import checkers.quals.ImplicitFor;
import checkers.quals.SubtypeOf;
import checkers.quals.TypeQualifier;
import checkers.quals.Unqualified;

/**
 * @author huangw5
 */
@Documented
@TypeQualifier
@Inherited
@SubtypeOf({Readonly.class, Polyread.class})
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@ImplicitFor(
	types={TypeKind.NULL}
//    treeClasses={LiteralTree.class},
//    typeClasses={AnnotatedPrimitiveType.class}
    )
public @interface Mutable {
    
}
