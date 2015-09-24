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

import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import com.sun.tools.javac.file.JavacFileManager;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCAssign;
import com.sun.tools.javac.tree.JCTree.JCBinary;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCIdent;
import com.sun.tools.javac.tree.JCTree.JCMethodInvocation;
import com.sun.tools.javac.tree.JCTree.JCTypeCast;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;

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
	
	private static boolean inCopiedMethod;
	
	private JCTree stringTree;
	
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
    
    @Override
    public Void visitIdentifier(IdentifierTree node, Void p) {
    	if (stringTree == null && node.getName().toString().equals("String")) {
    		stringTree = (JCTree) node;
    	}
    	return super.visitIdentifier(node, p);
    }
    
    @Override
    public Void visitAssignment(AssignmentTree node, Void p) {
    	if (!inCopiedMethod) return super.visitAssignment(node, p);
    	JCExpression exp = (JCExpression) node.getExpression();
    	String id = checker.getIdentifier(exp);
    	Reference ref = checker.getAnnotatedReferences().get(id); 
    	if (ref != null && checker.getNeedTypeCastRefs().contains(ref)) {
    		JCAssign assign = (JCAssign) node;
    		JCTypeCast typeCast;
			if (assign.type.toString().equals("java.lang.String")) {
				typeCast = maker.TypeCast(stringTree, exp);
    		} else {
    			typeCast = maker.TypeCast(exp.type, exp);
    		}
	    	assign.rhs = typeCast;
    	}
    	return super.visitAssignment(node, p);
    }
    
    @Override
    public Void visitVariable(VariableTree node, Void p) {
    	if (!inCopiedMethod) return super.visitVariable(node, p);
    	JCExpression init = (JCExpression) node.getInitializer();
    	if (init == null) return super.visitVariable(node, p);
    	String id = checker.getIdentifier(init);
    	Reference ref = checker.getAnnotatedReferences().get(id); 
    	if (ref != null && checker.getNeedTypeCastRefs().contains(ref)) {
    		JCVariableDecl variableDecl = (JCVariableDecl) node;
	    	JCTypeCast typeCast = maker.TypeCast(init.type, init);
	    	variableDecl.init = typeCast;
    	}
    	return super.visitVariable(node, p);
    }
    
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

	@Override
	public Void visitMethodInvocation(MethodInvocationTree node, Void p) {
		if (!inCopiedMethod) return super.visitMethodInvocation(node, p);
    	JCExpression[] args = node.getArguments().toArray(new JCExpression[0]);
		for (int i = 0; i < args.length; i++) {
			String id = checker.getIdentifier(args[i]);
	    	Reference ref = checker.getAnnotatedReferences().get(id); 
	    	if (ref != null && checker.getNeedTypeCastRefs().contains(ref)) {
	    		if (args[i].type.toString().equals("java.lang.String")) {
	    			args[i] = maker.TypeCast(stringTree, args[i]);
	    		} else {
	    			args[i] = maker.TypeCast(args[i].type, args[i]);
	    		}
	    	}
		}
		((JCMethodInvocation) node).args = List.from(args);
    	return super.visitMethodInvocation(node, p);
	}
	
	@Override
    public Void visitBinary(BinaryTree node, Void p) {
		if (inCopiedMethod) {
			processBinaryTree(node);
		}
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
			if (!(operand instanceof JCIdent)) return;
			String id = checker.getIdentifier(operand);
			Reference ref = checker.getAnnotatedReferences().get(id);
			if (checker.getNeedTypeCastRefs().contains(ref)) {
				JCIdent ident = (JCIdent) operand;
				JCTypeCast typeCast;
				if (ident.type.toString().equals("java.lang.String")) {
					typeCast = maker.TypeCast(stringTree, ident);
	    		} else {
	    			typeCast = maker.TypeCast(ident.type, ident);
	    		}
		    	JCBinary binary = (JCBinary) node;
		    	if (isLeft) binary.lhs = typeCast;
				else binary.rhs = typeCast;
			}
		}
	}
	
  @Override
	public Void visitMethod(MethodTree node, Void p) {
		for (VariableTree parameter : node.getParameters()) {
			Reference parRef = checker.getAnnotatedReference(parameter);
			if (checker.getNeedCopyMethods().contains(parRef.getIdentifier())) {
				inCopiedMethod = true;
				break;
			} else {
				inCopiedMethod = false;
			}
		}
		return super.visitMethod(node, p);
  }
	
}
