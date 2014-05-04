/**
 * 
 */
package checkers.inference2;

import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;

import checkers.types.AnnotatedTypeFactory;
import checkers.util.AnnotationUtils;
import checkers.util.TreeUtils;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.MethodTree;

/**
 * @author huangw5
 *
 */
public class InferenceAnnotatedTypeFactory extends AnnotatedTypeFactory {
	
	public InferenceAnnotatedTypeFactory(InferenceChecker checker,
			CompilationUnitTree root) {
		super(checker, root);
        postInit();
	}


	protected ExecutableElement getCurrentMethodElt() {
        MethodTree methodTree = this.getVisitorState().getMethodTree();
        if (methodTree != null) {
            ExecutableElement currentMethod = TreeUtils.elementFromDeclaration(methodTree);
            return currentMethod;
        }
        return null;
    }

    /**
     * The implementation in {@link AnnotatedTypeFactory} cannot recognize the 
     * correct annotations when invoking the Main.compile() in the second time
     */
	@Override
	public boolean isSupportedQualifier(AnnotationMirror a) {
		if (super.isSupportedQualifier(a)) 
			return true;
        Name name = AnnotationUtils.annotationName(a);
		if (name != null && qualHierarchy != null) {
			Set<Name> typeQualifiers = qualHierarchy.getTypeQualifiers();
			for (Name tn : typeQualifiers) {
				if (tn != null && name.toString().equals(tn.toString()))
					return true;
			}
		} 
		return false;
	}
}
