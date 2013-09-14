/**
 * 
 */
package checkers.inference.aj;

import java.util.Set;

import javax.lang.model.element.AnnotationMirror;

import checkers.basetype.BaseTypeVisitor;
import checkers.source.Result;
import checkers.types.AnnotatedTypeMirror;
import checkers.types.AnnotatedTypeMirror.AnnotatedExecutableType;
import checkers.util.TreeUtils;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.MethodInvocationTree;

/**
 * @author huangw5
 *
 */
public class AJVisitor extends BaseTypeVisitor<AJChecker> {

	public AJVisitor(AJChecker checker, CompilationUnitTree root) {
		super(checker, root);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected boolean checkMethodInvocability(AnnotatedExecutableType method,
			MethodInvocationTree node) {
        AnnotatedTypeMirror methodReceiver = method.getReceiverType().getErased();
        AnnotatedTypeMirror treeReceiver = methodReceiver.getCopy(false);
        AnnotatedTypeMirror rcv = atypeFactory.getReceiverType(node);
        treeReceiver.addAnnotations(rcv.getEffectiveAnnotations());
        
		Set<AnnotationMirror> adaptedAnnos = checker.adaptMethodSet(
				treeReceiver.getAnnotations(), 
				methodReceiver.getAnnotations());
		if (!adaptedAnnos.isEmpty()) {
			methodReceiver.clearAnnotations();
			methodReceiver.addAnnotations(adaptedAnnos);
		}
        
        if (!checker.isSubtype(treeReceiver, methodReceiver)) {
            checker.report(Result.failure("method.invocation.invalid",
                TreeUtils.elementFromUse(node),
                treeReceiver.toString(), methodReceiver.toString()), node);
            return false;
        }
        return true;	
	}

}
