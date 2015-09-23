/**
 * 
 */
package checkers.inference2.typeCast;

import java.io.File;

import checkers.inference2.Reference;
import checkers.inference2.InferenceChecker;
import checkers.source.SourceVisitor;
import checkers.types.AnnotatedTypeFactory;
import checkers.types.AnnotatedTypes;

import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import com.sun.tools.javac.file.JavacFileManager;
import com.sun.tools.javac.tree.JCTree.JCBinary;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.javac.tree.JCTree.JCIdent;
import com.sun.tools.javac.tree.JCTree.JCTypeCast;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;

/**
 * 
 *
 */
public class TypeCastVisitor extends SourceVisitor<Void, Void> {

	/** The checker corresponding to this visitor. */
    protected final TypeCastChecker checker;
    
    /** utilities class for annotated types **/
    protected final AnnotatedTypes annoTypes;

	protected final AnnotatedTypeFactory factory;
	
	private static TreeMaker maker;
	
	/** For recording visited method invocation trees or allocation sites */
	//protected Set<Tree> visited = new HashSet<Tree>();

	//private static boolean hasImported = false, inSample;
	
	//private static boolean inCopiedMethod;
	
	public TypeCastVisitor(InferenceChecker checker, CompilationUnitTree root) {
		super(checker, root);
		this.checker = (TypeCastChecker) checker;
		this.checker.setCurrentFactory(atypeFactory);
		this.factory = atypeFactory;
        this.annoTypes =
            new AnnotatedTypes(checker.getProcessingEnvironment(), atypeFactory);
        Context context = new Context();
		JavacFileManager.preRegister(context);
		maker = TreeMaker.instance(context);
	}
	
    /**
     * Scan a single node.
     * The current path is updated for the duration of the scan.
     */
    @Override
    public Void scan(Tree tree, Void p) {
        if (tree == null)
            return null;

        TreePath prev = checker.currentPath;
        checker.currentPath = new TreePath(checker.currentPath, tree);
        try {
            return tree.accept(this, p);
        } finally {
            checker.currentPath = prev;
        }
    }

    /**
     * et the current path for the node, as built up by the
     * currently active set of scan calls.
     */
    @Override
    public TreePath getCurrentPath() {
        return checker.currentPath;
    }
    
//    @Override
//    public Void visitIdentifier(IdentifierTree node, Void p) {
//    	String id = checker.getIdentifier(node);
//    	Reference ref = checker.getAnnotatedReferences().get(id); 
//    	if (ref != null && checker.getNeedTypeCastRefs().contains(ref)) {
//    		
//    	}
//    	return super.visitIdentifier(node, p);
//    }
    
//    @Override
//	public Void visitMethod(MethodTree node, Void p) {
//		for (VariableTree parameter : node.getParameters()) {
//			Reference parRef = checker.getAnnotatedReference(parameter);
//			if (checker.getNeedCopyMethods().contains(parRef.getIdentifier())) {
//				inCopiedMethod = true;
//				break;
//			} else {
//				inCopiedMethod = false;
//			}
//		}
//		return super.visitMethod(node, p);
//    }
    
	@Override
	public Void visitClass(ClassTree node, Void p) {
		Tree parent = getCurrentPath().getParentPath().getLeaf();
		JCCompilationUnit root = (JCCompilationUnit) parent;
		TypeCastMain.packageName = root.getPackageName().toString()
				.replace('.', '/') + "/";
		File files = new File(TypeCastMain.outputDirTrans + TypeCastMain.packageName);
		if (!files.exists()) {
			if (!files.mkdirs()) {
				System.out.println("Failed to create multiple directories!");
			}
		}
		return super.visitClass(node, p);
	}

//	@Override
//	public Void visitMethodInvocation(MethodInvocationTree node, Void p) {
//		if (inCopiedMethod) {
//			String methodName = node.getMethodSelect().toString();
//			int index = methodName.lastIndexOf('.') + 1;
//			if (index > 0) {
//				methodName = methodName.substring(index);
//			}
//			if (checker.getNeedTypeCastMethods().contains(methodName)) {
//				JCExpression exp = (JCExpression) node;
//				Tree parent = getCurrentPath().getParentPath().getLeaf();
//				if (parent instanceof JCExpressionStatement) {
//					JCExpressionStatement statement = (JCExpressionStatement) parent;
//					statement.expr = maker.TypeCast(exp.type, exp);
//				} else if (parent instanceof JCVariableDecl) {
//					JCVariableDecl variableDecl = (JCVariableDecl) parent;
//					variableDecl.init = maker.TypeCast(exp.type, exp);
//				}
//			}
//		}
//    	return super.visitMethodInvocation(node, p);
//	}
	
//	@Override
//	public Void visitNewArray(NewArrayTree node, Void p) {
//		JCNewArray newArray = (JCNewArray) node;
//		List<JCExpression> newDims = List.nil();
//		for (JCExpression dim : newArray.getDimensions()) {
//			newDims = newDims.append(maker.TypeCast(dim.type, dim));
//		}
//		newArray.dims = newDims;
//		return super.visitNewArray(node, p);
//	}
	
	@Override
    public Void visitBinary(BinaryTree node, Void p) {
		processBinaryTree(node);
		return super.visitBinary(node, p);
	}
	
	private void processBinaryTree(BinaryTree node) {
		modifiyForTypeCast(node, node.getRightOperand(), false);
		modifiyForTypeCast(node, node.getLeftOperand(), true);
	}
    
	private void modifiyForTypeCast(BinaryTree node, ExpressionTree operand,
			boolean isLeft) {
		// recursively process operand
		if (operand instanceof BinaryTree) {
			processBinaryTree((BinaryTree) operand);
		} else {
			String id = checker.getIdentifier(operand);
			Reference ref = checker.getAnnotatedReferences().get(id);
			if (checker.getNeedTypeCastRefs().contains(ref)) {
				JCIdent ident = (JCIdent) operand;
		    	JCTypeCast typeCast = maker.TypeCast(ident.type, ident);
		    	JCBinary binary = (JCBinary) node;
		    	if (isLeft) binary.lhs = typeCast;
				else binary.rhs = typeCast;
			}
		}
	}
	
}
