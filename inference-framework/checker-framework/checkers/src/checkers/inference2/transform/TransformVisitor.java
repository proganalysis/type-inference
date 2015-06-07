/**
 * 
 */
package checkers.inference2.transform;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;

import checkers.inference2.Conversion;
import checkers.inference2.InferenceChecker;
import checkers.inference2.Reference;
import checkers.source.SourceVisitor;
import checkers.types.AnnotatedTypeFactory;
import checkers.types.AnnotatedTypes;
import checkers.util.TreeUtils;

import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import com.sun.tools.javac.code.TypeTags;
import com.sun.tools.javac.file.JavacFileManager;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCAnnotatedType;
import com.sun.tools.javac.tree.JCTree.JCArrayTypeTree;
import com.sun.tools.javac.tree.JCTree.JCBinary;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCFieldAccess;
import com.sun.tools.javac.tree.JCTree.JCMethodInvocation;
import com.sun.tools.javac.tree.JCTree.JCNewArray;
import com.sun.tools.javac.tree.JCTree.JCParens;
import com.sun.tools.javac.tree.JCTree.JCPrimitiveTypeTree;
import com.sun.tools.javac.tree.JCTree.JCReturn;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import com.sun.tools.javac.tree.JCTree.Tag;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Name;

/**
 * It has two modes: inference mode and checking mode
 * @author huangw5
 *
 */
public class TransformVisitor extends SourceVisitor<Void, Void> {
	
    /** The checker corresponding to this visitor. */
    //protected final InferenceChecker checker;
    protected final TransformChecker checker;
    
    /** utilities class for annotated types **/
    protected final AnnotatedTypes annoTypes;

	protected final AnnotatedTypeFactory factory;
	
	private static TreeMaker maker;
	
	protected static Map<String, Tree> encryptionMethods = new HashMap<>();
	
	private Map<String, List<Conversion>> convertedReferences;

	public TransformVisitor(InferenceChecker checker, CompilationUnitTree root) {
		super(checker, root);
		this.checker = (TransformChecker) checker;
		this.checker.setCurrentFactory(atypeFactory);
		this.factory = atypeFactory;
        this.annoTypes =
            new AnnotatedTypes(checker.getProcessingEnvironment(), atypeFactory);
        Context context = new Context();
		JavacFileManager.preRegister(context);
		maker = TreeMaker.instance(context);
		convertedReferences = checker.getConvertedReferences();
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
    
    private JCArrayTypeTree getByteArrayTree(JCTree typeTree) {
    	((JCPrimitiveTypeTree) typeTree).typetag = TypeTags.BYTE;
		JCArrayTypeTree jcptt = maker.TypeArray((JCExpression) typeTree);
		return jcptt;
    }

    private void processVariableTree(JCTree jctree) {
    	JCTree eleTypeTree;
    	Tag tag = jctree.getTag();
    	switch (tag) {
    	case VARDEF:
    		eleTypeTree = ((JCVariableDecl) jctree).getType();
    		break;
    	case TYPEARRAY:
    		eleTypeTree = ((JCArrayTypeTree) jctree).getType();
    		break;
    	case NEWARRAY:
    		eleTypeTree = ((JCNewArray) jctree).getType();
    		break;
    	case ANNOTATED_TYPE:
    		eleTypeTree = ((JCAnnotatedType) jctree).getUnderlyingType();
    		break;
		default:
			eleTypeTree = null;
			break;
    	}
		if (eleTypeTree != null) {
			modifyTree(jctree, eleTypeTree, tag);
		}
    }

	private void modifyTree(JCTree jctree, JCTree eleTypeTree, Tag tag) {
		if (eleTypeTree.getTag() == Tag.TYPEIDENT) {
			JCPrimitiveTypeTree jcptt = (JCPrimitiveTypeTree) eleTypeTree;
			if (jcptt.getPrimitiveTypeKind() == TypeKind.INT) {
				JCArrayTypeTree jcatt = getByteArrayTree(eleTypeTree);
				switch (tag) {
				case VARDEF:
					((JCVariableDecl) jctree).vartype = jcatt;
					break;
				case TYPEARRAY:
					((JCArrayTypeTree) jctree).elemtype = jcatt;
					break;
				case NEWARRAY:
					((JCNewArray) jctree).elemtype = jcatt;
					break;
				case ANNOTATED_TYPE:
					((JCAnnotatedType) jctree).underlyingType = jcatt;
					break;
				default:
					break;
				}
			}
		} else {
			processVariableTree(eleTypeTree);
		}
	}
    
	private JCMethodInvocation getConvertMethod(ExpressionTree arg, String encryptType) {
		Conversion con = new Conversion(null, "CLEAR", encryptType);
		return getConvertMethod(arg, con);
	}
	
	private JCMethodInvocation getConvertMethod(ExpressionTree arg, Conversion con) {
		JCMethodInvocation fn;
		// from, to
		JCExpression fromTree = maker.Literal(con.getFrom());
		JCExpression toTree = maker.Literal(con.getTo());
		com.sun.tools.javac.util.List<JCExpression> args;
		if (con.getFrom().equals("CLEAR")) {
			// Encryption.encrypt(arg, to)
			fn = (JCMethodInvocation) encryptionMethods.get("enc");
			args = com.sun.tools.javac.util.List.of((JCExpression) arg, toTree);
		} else {
			// Encryption.convert(arg, from, to)
			fn = (JCMethodInvocation) encryptionMethods.get("con");
			args = com.sun.tools.javac.util.List.of((JCExpression) arg, fromTree, toTree);
		}
		// Encryption -- JCIdent
		JCExpression selected = ((JCFieldAccess) fn.getMethodSelect()).getExpression();
		// convert -- Name
		Name selector = ((JCFieldAccess) fn.getMethodSelect()).getIdentifier();
		// Encryption.convert -- JCFieldAccess
		JCExpression meth = maker.Select(selected, selector);
		return maker.Apply(null, meth, args);
	}
	
	private JCMethodInvocation getComputeMethod(ExpressionTree left, ExpressionTree right,
			Tag tag) {
		// a + b -> Computation.add(a, b)
		JCMethodInvocation fn;
		// a, b
		com.sun.tools.javac.util.List<JCExpression> args = com.sun.tools.javac.util.List
				.of((JCExpression) left, (JCExpression) right);
		// add, compareTo, or equals
		switch (tag) {
		case PLUS: // +
			fn = (JCMethodInvocation) encryptionMethods.get("add");
			break;
		case EQ: // ==
		case NE: // !=
			fn = (JCMethodInvocation) encryptionMethods.get("equ");
			break;
		case LT: // <
		case GT: // >
		case LE: // <=
		case GE: // >=
			fn = (JCMethodInvocation) encryptionMethods.get("com");
			break;
		default:
			return null;
		}
		// Encryption -- JCIdent
		JCExpression selected = ((JCFieldAccess) fn.getMethodSelect()).getExpression();
		// convert -- Name
		Name selector = ((JCFieldAccess) fn.getMethodSelect()).getIdentifier();
		// Encryption.convert -- JCFieldAccess
		JCExpression meth = maker.Select(selected, selector);
		return maker.Apply(null, meth, args);
	}
	
	private String getSimpleEncryptName(Reference r) {
		Set<AnnotationMirror> annos = r.getAnnotations(checker);
		String fullname = annos.iterator().next().toString();
		return fullname.substring(fullname.lastIndexOf('.') + 1);
	}
	
    public Void visitVariable(VariableTree node, Void p) {
    	VariableElement varElt = TreeUtils.elementFromDeclaration(node);
		Reference varRef = checker.getAnnotatedReference(varElt);
		if (!varRef.getRawAnnotations().contains(checker.CLEAR)) {
			JCVariableDecl jcvd = (JCVariableDecl) node;
			processVariableTree(jcvd);
			JCExpression init = jcvd.getInitializer();
			if (init != null) {
				Reference initRef = checker.getAnnotatedReference(init);
				// change type: int -> byte[]
				processVariableTree(init);
				// int x = 9 -> int x = Conversion.encrypt(x, "AH")
				if (init.getTag() == Tag.LITERAL) {
					 JCMethodInvocation jcmi
					 = getConvertMethod(init, getSimpleEncryptName(varRef));
					 jcvd.init = jcmi;
				} else {
					// process conversion
					processInit(init, jcvd, initRef);
				}
			}
		}
    	return super.visitVariable(node, p);
    }
    
    private void processInit(JCExpression init, JCVariableDecl jcvd, Reference initRef) {
		if (convertedReferences.containsKey(initRef.getIdentifier())) {
			for (Conversion con : convertedReferences.get(initRef.getIdentifier())) {
				if (con.getId().equals(checker.getFileName(init) + checker.getLineNumber(init))) {
					jcvd.init = getConvertMethod(init, con);
				}
			}
		}
	}

	@Override
	public Void visitMethodInvocation(MethodInvocationTree node, Void p) {
    	String methName = node.getMethodSelect().toString();
    	switch (methName) {
    	case "Computation.add":
    		encryptionMethods.put("add", node);
    		break;
    	case "Computation.compareTo":
    		encryptionMethods.put("com", node);
    		break;
    	case "Computation.equals":
    		encryptionMethods.put("equ", node);
    		break;
    	case "Conversion.encrypt":
    		encryptionMethods.put("enc", node);
    		break;
    	case "Conversion.decrypt":
    		encryptionMethods.put("dec", node);
    		break;
    	case "Conversion.convert":
    		encryptionMethods.put("con", node);
    		break;
    	default:
    		processMethodInvocation(node);
    	}
    	return super.visitMethodInvocation(node, p);
	}
    
    private void processMethodInvocation(MethodInvocationTree node) {
    	JCExpression[] args = node.getArguments().toArray(new JCExpression[0]);
    	int i = 0;
    	for (ExpressionTree arg : node.getArguments()) {
    		Reference argRef = checker.getAnnotatedReference(arg);
    		if (convertedReferences.containsKey(argRef.getIdentifier())) {
    			for (Conversion con : convertedReferences.get(argRef.getIdentifier())) {
    				if (con.getId().equals(checker.getFileName(node) + checker.getLineNumber(node))) {
    					args[i] = getConvertMethod(arg, con);
    					break;
    				}
    			}
    		}
    		i++;
    	}
    	JCMethodInvocation jcmi = (JCMethodInvocation) node;
    	jcmi.args = com.sun.tools.javac.util.List.from(args);
    }
    
	@Override
    public Void visitBinary(BinaryTree node, Void p) {
		processComputation(node);
		//processConversion(node);
		return super.visitBinary(node, p);
	}
	
	private void processComputation(BinaryTree node) {
		ExpressionTree rightOperand = node.getRightOperand();
		ExpressionTree leftOperand = node.getLeftOperand();
		Reference leftRef = checker.getAnnotatedReference(leftOperand);
		Reference rightRef = checker.getAnnotatedReference(rightOperand);
		String nodeId = checker.getFileName(node) + checker.getLineNumber(node);
		if (convertedReferences.containsKey(leftRef.getIdentifier())) {
			for (Conversion con : convertedReferences.get(leftRef.getIdentifier())) {
				if (con.getId().equals(nodeId)) {
					JCBinary jcbt = (JCBinary) node;
					jcbt.lhs = getConvertMethod(node.getLeftOperand(), con);
					break;
				}
			}
		}
		if (rightOperand instanceof JCBinary) {
			processComputation((BinaryTree) rightOperand);
		} else {
			if (convertedReferences.containsKey(rightRef.getIdentifier())) {
				for (Conversion con : convertedReferences.get(rightRef
						.getIdentifier())) {
					if (con.getId().equals(nodeId)) {
						JCBinary jcbt = (JCBinary) node;
						jcbt.rhs = getConvertMethod(rightOperand, con);
						break;
					}
				}
			}if (!leftRef.getRawAnnotations().contains(checker.CLEAR)) {
				JCBinary jcb = (JCBinary) node;
				Tree parent = checker.currentPath.getParentPath().getLeaf();
				if (parent instanceof JCParens) {
					JCParens jcp = (JCParens) parent;
					jcp.expr = getComputeMethod(node.getLeftOperand(), node.getRightOperand(),
							jcb.getTag());
				}
				if (parent instanceof JCMethodInvocation) {
					JCMethodInvocation jcmi = (JCMethodInvocation) parent;
					com.sun.tools.javac.util.List<JCExpression> newArgs = com.sun.tools.javac.util.List
							.nil();
					com.sun.tools.javac.util.List<JCExpression> args = jcmi
							.getArguments();
					for (JCExpression arg : args) {
						if (arg.equals(node)) {
							newArgs = newArgs.append(getComputeMethod(node.getLeftOperand(),
									node.getRightOperand(), jcb.getTag()));
						} else {
							newArgs = newArgs.append(arg);
						}
					}
					jcmi.args = newArgs;
				}
			}
		}
	}
	
	private void processConversion(BinaryTree node) {
		String nodeId = checker.getFileName(node) + checker.getLineNumber(node);
		Reference leftRef = checker.getAnnotatedReference(node.getLeftOperand());
		if (convertedReferences.containsKey(leftRef.getIdentifier())) {
			for (Conversion con : convertedReferences.get(leftRef.getIdentifier())) {
				if (con.getId().equals(nodeId)) {
					JCBinary jcbt = (JCBinary) node;
					jcbt.lhs = getConvertMethod(node.getLeftOperand(), con);
					break;
				}
			}
		}
		ExpressionTree rightOperand = node.getRightOperand();
		if (rightOperand instanceof JCBinary) {
			processConversion((BinaryTree) rightOperand);
			return;
		}
		Reference rightRef = checker.getAnnotatedReference(rightOperand);
		if (convertedReferences.containsKey(rightRef.getIdentifier())) {
			for (Conversion con : convertedReferences.get(rightRef.getIdentifier())) {
				if (con.getId().equals(nodeId)) {
					JCBinary jcbt = (JCBinary) node;
					jcbt.rhs = getConvertMethod(rightOperand, con);
					break;
				}
			}
		}
	}
	
	@Override
	public Void visitReturn(ReturnTree node, Void p) {
		ExpressionTree expr = node.getExpression();
		if (expr != null) {
			Reference returnRef = checker.getAnnotatedReference(expr);
			if (convertedReferences.containsKey(returnRef.getIdentifier())) {
				for (Conversion con : convertedReferences.get(returnRef.getIdentifier())) {
					if (con.getId().equals(checker.getFileName(node) + checker.getLineNumber(node))) {
						JCReturn jcr = (JCReturn) node;
						jcr.expr = getConvertMethod(expr, con);
						break;
					}
				}
			}
		}
		return super.visitReturn(node, p);
	}
	
}

