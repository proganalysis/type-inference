/**
 * 
 */
package checkers.inference2;

import javax.lang.model.element.AnnotationMirror;

/**
 * @author huangw5
 *
 */
public interface ViewpointAdapter {

    public AnnotationMirror adaptField(AnnotationMirror context, AnnotationMirror decl);

    public AnnotationMirror adaptMethod(AnnotationMirror context, AnnotationMirror decl);

}
