package javax.lang.model.element;

import java.util.List;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;

import checkers.inference.reim.quals.*;

public interface TypeParameterElement extends Element {
    @PolyreadThis @Polyread Element getGenericElement() ;
    @PolyreadThis @Polyread List<? extends TypeMirror> getBounds() ;
}
