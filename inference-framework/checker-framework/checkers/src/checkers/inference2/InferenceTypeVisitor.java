package checkers.inference2;

import checkers.basetype.BaseTypeVisitor;
import checkers.types.AnnotatedTypeMirror.AnnotatedArrayType;
import checkers.types.AnnotatedTypeMirror.AnnotatedDeclaredType;
import checkers.types.AnnotatedTypeMirror.AnnotatedPrimitiveType;
import checkers.types.AnnotatedTypeMirror.AnnotatedTypeVariable;
import checkers.types.AnnotatedTypeMirror.AnnotatedWildcardType;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree;

public abstract class InferenceTypeVisitor<Checker extends InferenceChecker>
		extends BaseTypeVisitor<InferenceChecker> {

	public InferenceTypeVisitor(Checker checker,
			CompilationUnitTree root) {
		super(checker, root);
	}

	@Override
	protected TypeValidator createTypeValidator() {
		return new InferenceTypeValidator();
	}

	protected class InferenceTypeValidator extends TypeValidator {
		@Override
        public Void visitDeclared(AnnotatedDeclaredType type, Tree tree) {
			return null;
		}	
        @Override
        public Void visitPrimitive(AnnotatedPrimitiveType type, Tree tree) {
        	return null;
        }
        @Override
        public Void visitArray(AnnotatedArrayType type, Tree tree) {
        	return null;
        }
        @Override
        public Void visitTypeVariable(AnnotatedTypeVariable type, Tree tree) {
        	return null;
        }
        @Override
        public Void visitWildcard(AnnotatedWildcardType type, Tree tree) {
        	return null;
        }
	}
	
}
