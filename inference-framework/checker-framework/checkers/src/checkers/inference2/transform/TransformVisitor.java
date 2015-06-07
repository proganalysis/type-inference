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
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
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
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCFieldAccess;
import com.sun.tools.javac.tree.JCTree.JCImport;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.tree.JCTree.JCMethodInvocation;
import com.sun.tools.javac.tree.JCTree.JCNewArray;
import com.sun.tools.javac.tree.JCTree.JCParens;
import com.sun.tools.javac.tree.JCTree.JCPrimitiveTypeTree;
import com.sun.tools.javac.tree.JCTree.JCReturn;
import com.sun.tools.javac.tree.TreeCopier;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import com.sun.tools.javac.tree.JCTree.Tag;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Name;

/**
 * 
 *
 */
public class TransformVisitor extends SourceVisitor<Void, Void> {
	
    /** The checker corresponding to this visitor. */
    protected final TransformChecker checker;
    
    /** utilities class for annotated types **/
    protected final AnnotatedTypes annoTypes;

	protected final AnnotatedTypeFactory factory;
	
	private static TreeMaker maker;
	
	private static Map<String, JCMethodInvocation> encryptionMethods = new HashMap<>();
	
	private Map<String, List<Conversion>> convertedReferences;
	
	private static com.sun.tools.javac.util.List<JCTree> imports
		= com.sun.tools.javac.util.List.nil();

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
    	case METHODDEF:
    		eleTypeTree = ((JCMethodDecl) jctree).getReturnType();
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
				case METHODDEF:
					((JCMethodDecl) jctree).restype = jcatt;
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
			fn = encryptionMethods.get("encrypt");
			args = com.sun.tools.javac.util.List.of((JCExpression) arg, toTree);
		} else {
			// Encryption.convert(arg, from, to)
			fn = encryptionMethods.get("convert");
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
			fn = encryptionMethods.get("add");
			break;
		case EQ: // ==
			fn = encryptionMethods.get("equals");
			break;
		case NE: // !=
			fn = encryptionMethods.get("notEquals");
			break;
		case LT: // <
			fn = encryptionMethods.get("lessThan");
			break;
		case GT: // >
			fn = encryptionMethods.get("greaterThan");
			break;
		case LE: // <=
			fn = encryptionMethods.get("lessThanOrEqualTo");
			break;
		case GE: // >=
			fn = encryptionMethods.get("greaterThanOrEqualTo");
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
	public Void visitMethod(MethodTree node, Void p) {
    	String id = checker.getFileName(node) + checker.getLineNumber(node);
    	JCMethodDecl jcmd = (JCMethodDecl) node;
    	// create two versions of a sensitive method
    	if (checker.getNeedCopyMethods().contains(id)) {
			TreeCopier<Void> treeCopier = new TreeCopier<Void>(maker);
			JCTree method = treeCopier.copy(jcmd);
			Tree parent = checker.currentPath.getParentPath().getLeaf();
			JCClassDecl jccd = (JCClassDecl) parent;
			jccd.defs = jccd.defs.append(method);
		}
    	// change the return type: int -> byte[]
		if (jcmd.getReturnType() != null) {
			Reference retRef = checker.getAnnotatedReference(jcmd.getReturnType());
			if (!retRef.getRawAnnotations().contains(checker.CLEAR)) {
				//jcmd.restype = getByteArrayTree(jcmd.getReturnType());
				processVariableTree(jcmd);
			}
		}
		return super.visitMethod(node, p);
    }

	@Override
	public Void visitMethodInvocation(MethodInvocationTree node, Void p) {
    	String methName = node.getMethodSelect().toString();
    	JCMethodInvocation jcmi = (JCMethodInvocation) node;
    	switch (methName) {
    	case "Computation.add":
    		encryptionMethods.put("add", jcmi);
    		break;
    	case "Computation.lessThan":
    		encryptionMethods.put("lessThan", jcmi);
    		break;
    	case "Computation.greaterThan":
    		encryptionMethods.put("greaterThan", jcmi);
    		break;
    	case "Computation.lessThanOrEqualTo":
    		encryptionMethods.put("lessThanOrEqualTo", jcmi);
    		break;
    	case "Computation.greaterThanOrEqualTo":
    		encryptionMethods.put("greaterThanOrEqualTo", jcmi);
    		break;
    	case "Computation.equals":
    		encryptionMethods.put("equals", jcmi);
    		break;
    	case "Computation.notEquals":
    		encryptionMethods.put("notEquals", jcmi);
    		break;
    	case "Conversion.encrypt":
    		encryptionMethods.put("encrypt", jcmi);
    		break;
    	case "Conversion.decrypt":
    		encryptionMethods.put("decrypt", jcmi);
    		break;
    	case "Conversion.convert":
    		encryptionMethods.put("convert", jcmi);
    		break;
    	default:
    		processMethodInvocation(jcmi);
    	}
    	return super.visitMethodInvocation(jcmi, p);
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
		processBinaryTree(node);
		return super.visitBinary(node, p);
	}
	
	private void processBinaryTree(BinaryTree node) {
		ExpressionTree rightOperand = node.getRightOperand();
		ExpressionTree leftOperand = node.getLeftOperand();
		Reference leftRef = checker.getAnnotatedReference(leftOperand);
		Reference rightRef = checker.getAnnotatedReference(rightOperand);
		String nodeId = checker.getFileName(node) + checker.getLineNumber(node);
		// leftOperand -> Encryption.convert(left, from, to)
		if (convertedReferences.containsKey(leftRef.getIdentifier())) {
			for (Conversion con : convertedReferences.get(leftRef.getIdentifier())) {
				if (con.getId().equals(nodeId)) {
					JCBinary jcbt = (JCBinary) node;
					jcbt.lhs = getConvertMethod(node.getLeftOperand(), con);
					break;
				}
			}
		}
		// recursively process rightOperand
		if (rightOperand instanceof JCBinary) {
			processBinaryTree((BinaryTree) rightOperand);
		} else {
			// rightOperand -> Encryption.convert(right, from, to)
			if (convertedReferences.containsKey(rightRef.getIdentifier())) {
				for (Conversion con : convertedReferences.get(rightRef
						.getIdentifier())) {
					if (con.getId().equals(nodeId)) {
						JCBinary jcbt = (JCBinary) node;
						jcbt.rhs = getConvertMethod(rightOperand, con);
						break;
					}
				}
			}
			// leftOperand + rightOperand -> Computation.add(leftOperand, rightOperand)
			if (!leftRef.getRawAnnotations().contains(checker.CLEAR)) {
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
	
	@Override
	public Void visitClass(ClassTree node, Void p) {
		Tree parent = checker.currentPath.getParentPath().getLeaf();
		JCCompilationUnit jccd = (JCCompilationUnit) parent;
		if (node.getSimpleName().toString().equals("EncryptionSample")) {
			for (JCImport imp : jccd.getImports()) {
				imports = imports.append(imp);
			}
		} else {
			jccd.defs = jccd.defs.prependList(imports);
		}
		return super.visitClass(node, p);
	}
	
}

