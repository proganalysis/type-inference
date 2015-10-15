/**
 * 
 */
package checkers.inference2.transform;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;

import checkers.inference2.InferenceChecker;
import checkers.inference2.Reference;
import checkers.inference2.Reference.ExecutableReference;
import checkers.source.SourceVisitor;
import checkers.types.AnnotatedTypeFactory;
import checkers.types.AnnotatedTypes;
import checkers.util.TreeUtils;

import com.sun.source.tree.ArrayAccessTree;
import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.CompoundAssignmentTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.NewArrayTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TypeCastTree;
import com.sun.source.tree.UnaryTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import com.sun.tools.javac.file.JavacFileManager;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCAnnotatedType;
import com.sun.tools.javac.tree.JCTree.JCArrayAccess;
import com.sun.tools.javac.tree.JCTree.JCArrayTypeTree;
import com.sun.tools.javac.tree.JCTree.JCAssign;
import com.sun.tools.javac.tree.JCTree.JCAssignOp;
import com.sun.tools.javac.tree.JCTree.JCBinary;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCExpressionStatement;
import com.sun.tools.javac.tree.JCTree.JCFieldAccess;
import com.sun.tools.javac.tree.JCTree.JCForLoop;
import com.sun.tools.javac.tree.JCTree.JCIdent;
import com.sun.tools.javac.tree.JCTree.JCImport;
import com.sun.tools.javac.tree.JCTree.JCLiteral;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.tree.JCTree.JCMethodInvocation;
import com.sun.tools.javac.tree.JCTree.JCNewArray;
import com.sun.tools.javac.tree.JCTree.JCNewClass;
import com.sun.tools.javac.tree.JCTree.JCParens;
import com.sun.tools.javac.tree.JCTree.JCPrimitiveTypeTree;
import com.sun.tools.javac.tree.JCTree.JCReturn;
import com.sun.tools.javac.tree.JCTree.JCTypeCast;
import com.sun.tools.javac.tree.JCTree.JCUnary;
import com.sun.tools.javac.tree.TreeCopier;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import com.sun.tools.javac.tree.JCTree.Tag;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
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
	
	private Map<String, Map<Long, String[]>> convertedReferences;
	
	private String returnType;	
	private static boolean inSample;
	
	private static JCExpression objectType;
	private static JCArrayTypeTree byteArray1, byteArray2;
	
	private static List<JCTree> imports	= List.nil();

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
    
    private JCExpression getObjectType(int pos) {
    	JCIdent jci = maker.Ident(((JCIdent) objectType).getName());
    	jci.setPos(pos);
    	return jci;
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

	/**
	 * Modify the type of a sensitive variable.
	 * For a local variable: int -> byte[], String -> byte[][];
	 * For others: int/String -> Object
	 * @param jctree
	 * @param eleTypeTree
	 * @param tag
	 */
	private void modifyTree(JCTree jctree, JCTree eleTypeTree, Tag tag) {
		if (eleTypeTree.getTag() == Tag.TYPEIDENT) {
			JCPrimitiveTypeTree jcptt = (JCPrimitiveTypeTree) eleTypeTree;
			if (jcptt.getPrimitiveTypeKind() == TypeKind.INT) {
				JCExpression jce = getObjectType(jcptt.getStartPosition());
				modifyType(jctree, tag, jce);
			}
		} else if (eleTypeTree.getTag() == Tag.IDENT) {
			JCIdent jci = (JCIdent) eleTypeTree;
			if (jci.getName().contentEquals("String")) {
				JCExpression jce = getObjectType(jci.getStartPosition());
				modifyType(jctree, tag, jce);
			}
		} else {
			processVariableTree(eleTypeTree);
		}
	}

	private void modifyType(JCTree jctree, Tag tag, JCExpression jcatt) {
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
    
	private JCMethodInvocation getConvertMethod(ExpressionTree arg, String[] con, boolean special) {
		JCMethodInvocation fn;
		// from, to
		JCExpression fromTree = maker.Literal(con[0]);
		JCExpression toTree = maker.Literal(con[1]);
		// add type cast: from Object to byte[]
		JCExpression argJ = getTypeCast(arg);
		List<JCExpression> args;
		if (con[0].equals("CLEAR") || con[0].equals("BOT")) {
			// Encryption.encrypt(arg, to)
			fn = special ? encryptionMethods.get("encryptSpe") : encryptionMethods.get("encrypt");
			args = List.of((JCExpression) arg, toTree);
		} else if (con[1].equals("CLEAR")) {
			// Encryption.decrypt(arg, from)
			fn = encryptionMethods.get("decrypt");
			args = List.of(argJ, fromTree);
		} else {
			// Encryption.convert(arg, from, to)
			fn = special ? encryptionMethods.get("convertSpe") : encryptionMethods.get("convert");
			args = List.of(argJ, fromTree, toTree);
		}
		// Encryption -- JCIdent
		JCExpression selected = ((JCFieldAccess) fn.getMethodSelect()).getExpression();
		// convert -- Name
		Name selector = ((JCFieldAccess) fn.getMethodSelect()).getIdentifier();
		// Encryption.convert -- JCFieldAccess
		JCExpression meth = maker.Select(selected, selector);
		return maker.Apply(null, meth, args);
	}

	private JCExpression getTypeCast(ExpressionTree arg) {
		JCExpression argJ = (JCExpression) arg;
		if (arg instanceof JCLiteral) return argJ;
		if (arg instanceof JCTypeCast) {
			argJ = ((JCTypeCast) arg).getExpression();
		}
		if (argJ.type != null && argJ.type.toString().equals("java.lang.String")) {
			argJ = maker.TypeCast(byteArray2, argJ);
		}
		if (argJ.type != null && argJ.type.toString().equals("int")) {
			argJ = maker.TypeCast(byteArray1, argJ);
		}
		return argJ;
	}
	
	private JCMethodInvocation getComputeMethod(ExpressionTree left, ExpressionTree right,
			Tag tag) {
		// a + b -> Computation.add(a, b)
		JCMethodInvocation fn;
		JCExpression leftJ = getTypeCast(left);
		JCExpression rightJ = getTypeCast(right);
		// a, b
		List<JCExpression> args = List.of(leftJ, rightJ);
		// add, compareTo, or equals
		switch (tag) {
		case PLUS: // +
		case PLUS_ASG: // +=
			fn = encryptionMethods.get("add");
			break;
		case MINUS: // -
		case MINUS_ASG: // -=
			fn = encryptionMethods.get("minus");
			break;
		case MUL: // *
		case MUL_ASG: // *=
			fn = encryptionMethods.get("multiply");
			break;
		case DIV: // /
		case DIV_ASG: // /=
			fn = encryptionMethods.get("divide");
			break;
		case MOD: // %
		case MOD_ASG: // %=
			fn = encryptionMethods.get("mod");
			break;
		case SL: // <<
		case SL_ASG: // <<=
			fn = encryptionMethods.get("shiftLeft");
			break;
		case SR: // >>
		case SR_ASG: // >>=
			fn = encryptionMethods.get("shiftRight");
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
		if (annos.isEmpty()) return null;
		String fullname = annos.iterator().next().toString();
		return fullname.substring(fullname.lastIndexOf('.') + 1);
	}
	
    public Void visitVariable(VariableTree node, Void p) {
		VariableElement varElt = TreeUtils.elementFromDeclaration(node);
    	JCVariableDecl jcvd = (JCVariableDecl) node;
    	// get Object type from EncryptionSample
    	if (objectType == null) setObjectType(jcvd);
    	// get byte array type from EncryptionSample;
    	if (byteArray1 == null || byteArray2 == null) setByteArrayType(jcvd);
		Reference varRef = checker.getAnnotatedReference(varElt);
		if (inSample) return super.visitVariable(node, p);
		if (!varRef.getRawAnnotations().contains(checker.CLEAR)
				&& !varRef.getRawAnnotations().contains(checker.BOT)) {
			processVariableTree(jcvd);
			JCExpression init = jcvd.getInitializer();
			if (init != null) {
				Reference initRef = checker.getAnnotatedReference(init);
				// change type: int -> byte[]
				processVariableTree(init);
				// process conversion
				if (shouldConvert(varRef)) {
					processInit(init, jcvd, initRef);
				}
			}
		}
    	return super.visitVariable(node, p);
    }
    
    // check the type of the variable if it is int or String
    private boolean shouldConvert(Reference ref) {
    	String javaType = ref.getType().getUnderlyingType().toString();
    	return javaType.equals("int") || javaType.equals("java.lang.String");
    }

	private void setByteArrayType(JCVariableDecl jcvd) {
		JCTree type = jcvd.getType();
		if (byteArray1 == null && type.toString().equals("byte[]")) {
			byteArray1 = (JCArrayTypeTree) type;
		}
		if (byteArray2 == null && type.toString().equals("byte[][]")) {
			byteArray2 = (JCArrayTypeTree) type;
		}
	}

	private void setObjectType(JCVariableDecl jcvd) {
		JCTree eleTypeTree = jcvd.getType();
    	if (eleTypeTree.getTag() == Tag.IDENT) {
    		JCIdent jci = (JCIdent) eleTypeTree;
    		if (jci.getName().toString().equals("Object")) {
    			objectType = jci;
    		}
    	}
	}
	
	private JCExpression findConvertMethod(JCExpression exp, Reference ref, boolean special) {
		if (!shouldConvert(ref)) return null;
		if (convertedReferences.containsKey(ref.getIdentifier())) {
    		Map<Long, String[]> conversions =
    				convertedReferences.get(ref.getIdentifier());
    		Long pos = checker.getPosition(exp);
    		if (conversions.containsKey(pos)) {
    			String[] con = conversions.get(pos);
    			return getConvertMethod(exp, con, special);
    		}
		}
		return null;
	}
    
    private void processInit(JCExpression init, JCVariableDecl jcvd, Reference initRef) {
    	JCExpression convertMethod = findConvertMethod(init, initRef, false);
    	if (convertMethod != null) {
			jcvd.init = convertMethod;
		}
	}
    
    @Override
    public Void visitNewClass(NewClassTree node, Void p) {
    	Element methElt = TreeUtils.elementFromUse(node);
		JCNewClass newClass = (JCNewClass) node;
		JCExpression[] args = node.getArguments().toArray(
				new JCExpression[0]);
		if (checker.isFromLibrary(methElt)) {
			newClass.args = List.from(decryptParameter(args));
		} else {
			for (int i = 0; i < args.length; i++) {
				Reference argRef = checker.getAnnotatedReference(args[i]);
				JCExpression convertMethod = findConvertMethod((JCExpression) args[i], argRef, false);
				if (convertMethod != null) {
					args[i] = convertMethod;
				}
			}
			newClass.args = List.from(args);
		}
    	return super.visitNewClass(node, p);
    }
    
    @Override
	public Void visitMethod(MethodTree node, Void p) {
    	JCMethodDecl jcmd = (JCMethodDecl) node;
    	// create two versions of a sensitive method
		for (VariableTree parameter : node.getParameters()) {
			Reference parRef = checker.getAnnotatedReference(parameter);
			if (checker.getNeedCopyMethods().contains(parRef.getIdentifier())) {
				TreeCopier<Void> treeCopier = new TreeCopier<Void>(maker);
				JCTree method = treeCopier.copy(jcmd);
				Tree parent = getCurrentPath().getParentPath().getLeaf();
				JCClassDecl jccd = (JCClassDecl) parent;
				jccd.defs = jccd.defs.append(method);
				break;
			}
		}
		// change the return type: int -> byte[]
		returnType = null;
    	ExecutableElement methodElt = (ExecutableElement) TreeUtils.elementFromDeclaration(node);
    	Reference methodRef = checker.getAnnotatedReference(methodElt);
		Reference returnRef = ((ExecutableReference) methodRef).getReturnRef();
		if (!returnRef.getRawAnnotations().contains(checker.CLEAR)) {
			processVariableTree(jcmd);
			returnType = getSimpleEncryptName(returnRef);
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
    	case "Computation.minus":
    		encryptionMethods.put("minus", jcmi);
    		break;
    	case "Computation.multiply":
    		encryptionMethods.put("multiply", jcmi);
    		break;
    	case "Computation.divide":
    		encryptionMethods.put("divide", jcmi);
    		break;
    	case "Computation.mod":
    		encryptionMethods.put("mod", jcmi);
    		break;
    	case "Computation.shiftLeft":
    		encryptionMethods.put("shiftLeft", jcmi);
    		break;
    	case "Computation.shiftRight":
    		encryptionMethods.put("shiftRight", jcmi);
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
    	case "Conversion.encryptSpe":
    		encryptionMethods.put("encryptSpe", jcmi);
    		break;
    	case "Conversion.decrypt":
    		encryptionMethods.put("decrypt", jcmi);
    		break;
    	case "Conversion.convert":
    		encryptionMethods.put("convert", jcmi);
    		break;
    	case "Conversion.convertSpe":
    		encryptionMethods.put("convertSpe", jcmi);
    		break;
    	default:
    		processMethodInvocation(jcmi);
    	}
    	return super.visitMethodInvocation(node, p);
	}
    
    private void processMethodInvocation(MethodInvocationTree node) {
    	Element methElt = TreeUtils.elementFromUse(node);
    	JCExpression[] args = node.getArguments().toArray(new JCExpression[0]);
    	JCMethodInvocation tree = (JCMethodInvocation) node;
    	if (checker.isFromLibrary(methElt)) {
			if (methElt.toString().startsWith("compareTo")
					|| methElt.toString().startsWith("equals")) {
				/*
				 *  a.equals(b)
				 *  a     b
				 *  sen   clear Computation.equals(a, encrypt(b, "DET"))
				 *  sen   sen   Computation.equals(a, b)
				 *  clear sen   Computation.equals(encrypt(a, "DET"), b)
				 *  clear clear do nothing
				 */
				Reference argRef = checker.getAnnotatedReference(args[0]);
				ExpressionTree rcvTree = TreeUtils.getReceiverTree(node);
				Reference rcvRef = checker.getAnnotatedReference(rcvTree);
				Tree parent = getCurrentPath().getParentPath().getLeaf();
				if (!argRef.getRawAnnotations().contains(checker.CLEAR)) {
					if (rcvRef.getRawAnnotations().contains(checker.CLEAR)) {
						JCExpression convertMethod = findConvertMethod((JCExpression) rcvTree, rcvRef, false);
						if (convertMethod != null) {
							tree.meth = convertMethod;
						}
					}
					modifyForComputation(node, rcvTree, args[0], Tag.EQ, parent, false);
				} else {
					if (!rcvRef.getRawAnnotations().contains(checker.CLEAR)) {
						JCExpression newMeth = findConvertMethod((JCExpression) args[0], argRef, false);
						if (newMeth != null) {
							args[0] = newMeth;
						} else {
							args[0] = getConvertMethod(args[0], new String[] { 
									"CLEAR", "DET"}, false);
						}
						tree.args = List.from(args);
						modifyForComputation(node, rcvTree, args[0], Tag.EQ, parent, false);
					}
				}
			} else {
				// process library method call:
				// e.g. System.out.print(decrypt(a, "DET"))
				tree.args = List.from(decryptParameter(args));
			}
		} else { // regular method call
			for (int i = 0; i < args.length; i++) {
				JCExpression arg = (JCExpression) args[i];
				if (arg instanceof JCTypeCast) {
					arg = ((JCTypeCast) arg).getExpression();
				}
				Reference argRef = checker.getAnnotatedReferences()
						.get(checker.getIdentifier(arg));
				JCExpression convertMethod = findConvertMethod((JCExpression) args[i], argRef, false);
				if (convertMethod != null) {
					args[i] = convertMethod;
				}
			}
			tree.args = List.from(args);
		}
    }

	protected JCExpression[] decryptParameter(JCExpression[] args) {
		for (int i = 0; i < args.length; i++) {
			Reference argRef = checker.getAnnotatedReference(args[i]);
			if (!argRef.getRawAnnotations().contains(checker.CLEAR)) {
				String encryptType = getSimpleEncryptName(argRef);
				if (encryptType != null) {
					args[i] = getConvertMethod(args[0], new String[] {
							encryptType, "CLEAR" }, false);
				}
			}
		}
		return args;
	}
    
    public Void visitBinary(BinaryTree node, Void p) {
		processBinaryTreeForConversion(node);
		processBinaryTreeForComputation(node, null, true);
		return super.visitBinary(node, p);
	}
	
	private void processBinaryTreeForComputation(BinaryTree node, BinaryTree outerTree, boolean isLeft) {
		// leftOperand + rightOperand -> Computation.add(leftOperand, rightOperand)
		ExpressionTree rightOperand = node.getRightOperand();
		ExpressionTree leftOperand = node.getLeftOperand();
		if (leftOperand instanceof JCBinary) {
			processBinaryTreeForComputation((BinaryTree) leftOperand, node, true);
		}
		if (rightOperand instanceof JCBinary) {
			processBinaryTreeForComputation((BinaryTree) rightOperand, node, false);
		}
		// skip a == null
		if (shouldSkip(leftOperand, rightOperand)) return;
		Reference leftRef = null;
		// note: here we must call getLeftOperand() again instead of using rightOperand
		// above directly, because it's possible that node has been changed.
		String left = node.getLeftOperand().toString();
		if (!left.startsWith("Conversion") && !left.startsWith("Computation")) {
			leftRef = checker.getAnnotatedReference(leftOperand);
		}
		if (leftRef == null || (shouldConvert(leftRef)
				&& !leftRef.getRawAnnotations().contains(checker.CLEAR))) {
			Tag tag = ((JCBinary) node).getTag();
			Tree parent;
			if (outerTree != null) {
				parent = outerTree;
			} else {
				parent = getCurrentPath().getParentPath().getLeaf();
			}
			modifyForComputation(node, node.getLeftOperand(), node.getRightOperand(),
					tag, parent, isLeft);
		}
	}

	private void modifyForComputation(Tree node, ExpressionTree left,
			ExpressionTree right, Tag tag, Tree parent, boolean isLeft) {
		JCMethodInvocation newCompMethod = getComputeMethod(left, right, tag);
		if (newCompMethod == null) return;
		if (parent instanceof JCParens) {
			JCParens parensTree = (JCParens) parent;
			parensTree.expr = newCompMethod;
		} else if (parent instanceof JCTypeCast) {
			JCTypeCast typeCastTree = (JCTypeCast) parent;
			typeCastTree.expr = newCompMethod;
		} else if (parent instanceof JCForLoop) {
			JCForLoop forLoopTree = (JCForLoop) parent;
			forLoopTree.cond = newCompMethod;
		} else if (parent instanceof JCBinary) {
			JCBinary binaryTree = (JCBinary) parent;
			if (isLeft) binaryTree.lhs = newCompMethod;
			else binaryTree.rhs = newCompMethod;
		} else if (parent instanceof JCMethodInvocation) {
			JCMethodInvocation methInvo = (JCMethodInvocation) parent;
			JCExpression[] args = methInvo.getArguments().toArray(new JCExpression[0]);
			for (int i = 0; i < args.length; i++) {
				if (args[i] == node) {
					args[i] = newCompMethod;
					break;
				}
			}
			methInvo.args = List.from(args);
		} else if (parent instanceof JCAssign) {
			JCAssign assign = (JCAssign) parent;
			assign.rhs = newCompMethod;
		} else if (parent instanceof JCReturn) {
			JCReturn ret = (JCReturn) parent;
			ret.expr = newCompMethod;
		} else if (parent instanceof JCNewClass) {
			JCNewClass newClass = (JCNewClass) parent;
			JCExpression[] args = newClass.getArguments().toArray(new JCExpression[0]);
			for (int i = 0; i < args.length; i++) {
				if (args[i] == node) {
					args[i] = newCompMethod;
					break;
				}
			}
			newClass.args = List.from(args);
		}
	}
	
	private void modifyForConversion(BinaryTree node, ExpressionTree operand,
			boolean isLeft) {
		// recursively process operand
		if (operand instanceof JCBinary) {
			processBinaryTreeForConversion((BinaryTree) operand);
		} else {
			if (operand instanceof JCParens ||
					operand.toString().startsWith("Conversion") ||
					operand.toString().startsWith("Computation")) return;
			JCBinary binary = (JCBinary) node;
			String operator = binary.getOperator().toString();
			// String + int: should use convertSpe() method.
			boolean con = isLeft ? operator.equals("+(int,java.lang.String)") :
				operator.equals("+(java.lang.String,int)");
			ExpressionTree exp = operand;
			if (operand instanceof JCTypeCast) {
				exp = ((JCTypeCast) operand).getExpression();
			}
			String id = checker.getIdentifier(exp);
			Reference ref = checker.getAnnotatedReferences().get(id);
			if (ref == null) ref = checker.getAnnotatedReference(exp);
			JCExpression convertMethod;
			if (con) {
				convertMethod = findConvertMethod((JCExpression) operand, ref, true);
				if (convertMethod == null) {
					if (!ref.getRawAnnotations().contains(checker.CLEAR)) {
						String type = getSimpleEncryptName(ref);
						convertMethod = getConvertMethod(operand, new String[]{type, type}, true);
					}
				}
			} else {
				convertMethod = findConvertMethod((JCExpression) operand, ref, false);
			}
			if (convertMethod != null) {
				if (isLeft) binary.lhs = convertMethod;
				else binary.rhs = convertMethod;
			}
		}
	}

	private void processBinaryTreeForConversion(BinaryTree node) {
		// operand -> Encryption.convert(operand, from, to)
		modifyForConversion(node, node.getRightOperand(), false);
		modifyForConversion(node, node.getLeftOperand(), true);
	}

	// skip (a == null) and (a = true)
	private boolean shouldSkip(ExpressionTree leftOperand, ExpressionTree rightOperand) {
		if (leftOperand instanceof JCLiteral) {
			if (((JCLiteral) leftOperand).getValue() == null) {
				return true;
			}
		}
		if (rightOperand instanceof JCLiteral) {
			if (((JCLiteral) rightOperand).getValue() == null) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public Void visitReturn(ReturnTree node, Void p) {
		ExpressionTree expr = node.getExpression();
		if (expr != null) {
			if (expr instanceof JCParens) {
				expr = ((JCParens) expr).getExpression();
			}
			Reference returnRef = checker.getAnnotatedReference(expr);
			JCExpression convertMethod = findConvertMethod((JCExpression) expr, returnRef, false);
			JCReturn ret = (JCReturn) node;
			if (convertMethod != null) {
				ret.expr = convertMethod;
			} else {
				if (expr instanceof JCLiteral && returnType != null && shouldConvert(returnRef)) {
					ret.expr = getConvertMethod(expr, new String[]{"CLEAR", returnType}, false);
				}
			}
		}
		return super.visitReturn(node, p);
	}
	
	@Override
	public Void visitClass(ClassTree node, Void p) {
		Tree parent = getCurrentPath().getParentPath().getLeaf();
		JCCompilationUnit root = (JCCompilationUnit) parent;
		if (node.getSimpleName().toString().equals("EncryptionSample")) {
			inSample = true;
			for (JCImport imp : root.getImports()) {
				imports = imports.append(imp);
			}
		} else {
			TransformMain.packageName = root.getPackageName().toString().replace('.', '/') + "/";
			File files = new File(TransformMain.outputDirTrans + TransformMain.packageName);
			if (!files.exists()) {
				if (!files.mkdirs()) {
					System.out.println("Failed to create multiple directories!");
				}
			}
			inSample = false;
			root.defs = root.defs.prependList(imports);
		}
		return super.visitClass(node, p);
	}
	
	@Override
	public Void visitAssignment(AssignmentTree node, Void p) {
		ExpressionTree expression = node.getExpression();
		Reference expRef = checker.getAnnotatedReference(expression);
		if (shouldConvert(expRef)) {
			JCExpression convertMethod = findConvertMethod(
					(JCExpression) expression, expRef, false);
			if (convertMethod != null) {
				((JCAssign) node).rhs = convertMethod;
			}
		}
		return super.visitAssignment(node, p);
	}
	
	@Override
	public Void visitCompoundAssignment(CompoundAssignmentTree node, Void p) {
		JCAssignOp assign = (JCAssignOp) node;
		processCompoundAssignment(assign, assign.getTag());
		return super.visitCompoundAssignment(node, p);
	}
	
	// handle x += y;
	private void processCompoundAssignment(JCAssignOp assign, Tag tag) {
		JCExpression exp = assign.getExpression();
		Reference expRef = checker.getAnnotatedReference(exp);
		if (expRef.getRawAnnotations().contains(checker.CLEAR)) return;
		// Encryption.conversion(y, from, to)
		JCExpression convertMethod = findConvertMethod(exp, expRef, false);
		ExpressionTree arg = convertMethod == null ? exp : convertMethod;
		// Computation.add(x, y)
		JCMethodInvocation computeMethod = getComputeMethod(arg, assign.getVariable(), tag);
		// covert back to AH from OPE if the enrypt type of exp is not AH
		String encryptType = getSimpleEncryptName(expRef);
		JCMethodInvocation method;
		if (encryptType.equals("AH")) {
			method = computeMethod;
		} else {
			method = getConvertMethod(computeMethod, new String[]{"AH", encryptType}, false);
		}
		Tree parent = getCurrentPath().getParentPath().getLeaf();
		// For now, consider parent is JCExpressionStatement
		if (parent instanceof JCExpressionStatement) {
			JCExpressionStatement statement = (JCExpressionStatement) parent;
			statement.expr = maker.Assign(assign.getVariable(), method);
		}
	}

	/**
     * A unary operation. ++, --, ...
     */
    @Override
	public Void visitUnary(UnaryTree node, Void p) {
		JCUnary jcu = (JCUnary) node;
		Tag tag = jcu.getTag();
		switch (tag) {
		case PREINC:
		case POSTINC:
			processUnaryTree(jcu, Tag.PLUS);
			break;
		case PREDEC:
		case POSTDEC:
			processUnaryTree(jcu, Tag.MINUS);
		default:
			break;
		}
		return super.visitUnary(node, p);
	}

	private void processUnaryTree(JCUnary jcu, Tag tag) {
		JCExpression exp = jcu.getExpression();
		Reference expRef = checker.getAnnotatedReference(exp);
		if (expRef.getRawAnnotations().contains(checker.CLEAR)) return;
		// conversion: i++ -> Encryption.conversion(i, e1, e2)
		JCExpression convertMethod = findConvertMethod(exp, expRef, false);
		ExpressionTree arg = convertMethod == null ? exp : convertMethod;
		// Encryption.encrypt(1, "AH")
		JCExpression expOne = maker.Literal(1);
		JCMethodInvocation convertOne = getConvertMethod(expOne, new String[]{"CLEAR", "AH"}, false);
		// Computation.add(i, Encryption.encrypt(1, "AH"))
		JCMethodInvocation computeMethod = getComputeMethod(arg, convertOne, tag);
		// covert back to AH from OPE if the enrypt type of exp is not AH
		String encryptType = getSimpleEncryptName(expRef);
		JCMethodInvocation method;
		if (encryptType.equals("AH")) {
			method = computeMethod;
		} else {
			method = getConvertMethod(computeMethod, new String[]{"AH", encryptType}, false);
		}
		// i++ -> i = i + 1;
		Tree parent = getCurrentPath().getParentPath().getLeaf();
		// For now, consider parent is JCExpressionStatement or JCArrayAccess
		if (parent instanceof JCExpressionStatement) {
			JCExpressionStatement jces = (JCExpressionStatement) parent;
			jces.expr = maker.Assign(exp, method);
		} else if (parent instanceof JCArrayAccess) {
			JCArrayAccess jcaa = (JCArrayAccess) parent;
			jcaa.index = maker.Assign(exp, method);
		}
	}
	
	@Override
	public Void visitArrayAccess(ArrayAccessTree node, Void p) {
		ExpressionTree index = node.getIndex();
		Reference indexRef = checker.getAnnotatedReference(index);
		if (index instanceof JCIdent
				&& !indexRef.getRawAnnotations().contains(checker.CLEAR)) {
			JCArrayAccess jcaa = (JCArrayAccess) node;
			jcaa.index = getConvertMethod(index,
					new String[]{getSimpleEncryptName(indexRef), "CLEAR"}, false);
		}
		return super.visitArrayAccess(node, p);
	}
	
	@Override
	public Void visitNewArray(NewArrayTree node, Void p) {
		JCNewArray newArray = (JCNewArray) node;
		Reference ref = checker.getAnnotatedReference(newArray);
		if (!ref.getRawAnnotations().contains(checker.CLEAR)) {
			processVariableTree(newArray);
		}
		List<JCExpression> newDims = List.nil();
		for (JCExpression dim : newArray.getDimensions()) {
			if (dim instanceof JCTypeCast) {
				dim = ((JCTypeCast) dim).getExpression();
			}
			Reference dimRef = checker.getAnnotatedReference(dim);
			if (!dimRef.getRawAnnotations().contains(checker.CLEAR)) {
				newDims = newDims.append(getConvertMethod(dim,
						new String[]{getSimpleEncryptName(dimRef), "CLEAR"}, false));
			} else {
				newDims = newDims.append(dim);
			}
		}
		newArray.dims = newDims;
		return super.visitNewArray(node, p);
	}
	
	@Override
	public Void visitTypeCast(TypeCastTree node, Void p) {
		String type = node.getType().toString();
		if (!type.equals("int") && !type.equals("java.lang.String"))
			return super.visitTypeCast(node, p);
		ExpressionTree exp = node.getExpression();
		Tree parent = getCurrentPath().getParentPath().getLeaf();
		if (parent instanceof JCAssign) {
    		((JCAssign) parent).rhs = (JCExpression) exp;
    	} else if (parent instanceof JCVariableDecl) {
    		((JCVariableDecl) parent).init = (JCExpression) exp;
    	} else if (parent instanceof JCMethodInvocation) {
			JCMethodInvocation methInvo = (JCMethodInvocation) parent;
			JCExpression[] args = methInvo.getArguments().toArray(new JCExpression[0]);
			for (int i = 0; i < args.length; i++) {
				if (args[i] == node) {
					args[i] = (JCExpression) exp;
					break;
				}
			}
			methInvo.args = List.from(args);
    	}
		return super.visitTypeCast(node, p);
	}
	
}
