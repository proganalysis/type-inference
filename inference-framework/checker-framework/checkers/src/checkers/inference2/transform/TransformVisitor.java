/**
 * 
 */
package checkers.inference2.transform;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;

import checkers.inference2.Conversion;
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
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.NewArrayTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.UnaryTree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import com.sun.tools.javac.file.JavacFileManager;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCAnnotatedType;
import com.sun.tools.javac.tree.JCTree.JCArrayAccess;
import com.sun.tools.javac.tree.JCTree.JCArrayTypeTree;
import com.sun.tools.javac.tree.JCTree.JCAssign;
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
	
	/** For recording visited method invocation trees or allocation sites */
	protected Set<Tree> visited = new HashSet<Tree>();

	private static boolean hasImported = false, inMainMethod;
	
	private static JCExpression objectType;
	private static JCArrayTypeTree byteArray1, byteArray2;
	
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
    
	private JCMethodInvocation getConvertMethod(ExpressionTree arg, String encryptType, boolean encrypt) {
		Conversion con;
		// encrypt: clear -> RND...
		if (encrypt) con = new Conversion(0, "CLEAR", encryptType);
		// decrypt: RND... -> clear
		else con = new Conversion(0, encryptType, "CLEAR");
		return getConvertMethod(arg, con);
	}
	
	private JCMethodInvocation getConvertMethod(ExpressionTree arg, Conversion con) {
		JCMethodInvocation fn;
		// from, to
		JCExpression fromTree = maker.Literal(con.getFrom());
		JCExpression toTree = maker.Literal(con.getTo());
		// add type cast: from Object to byte[]
		JCExpression argJ = getTypeCast(arg);
		com.sun.tools.javac.util.List<JCExpression> args;
		if (con.getFrom().equals("CLEAR") || con.getFrom().equals("BOT")) {
			// Encryption.encrypt(arg, to)
			fn = encryptionMethods.get("encrypt");
			args = com.sun.tools.javac.util.List.of((JCExpression) arg, toTree);
		} else if (con.getTo().equals("CLEAR")) {
			// Encryption.decrypt(arg, from)
			fn = encryptionMethods.get("decrypt");
			args = com.sun.tools.javac.util.List.of(argJ, fromTree);
		} else {
			// Encryption.convert(arg, from, to)
			fn = encryptionMethods.get("convert");
			if (argJ instanceof JCLiteral) {
				return getConvertMethod(argJ, con.getTo(), true);
			} else {
				args = com.sun.tools.javac.util.List.of(argJ, fromTree, toTree);
			}
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
		com.sun.tools.javac.util.List<JCExpression> args = com.sun.tools.javac.util.List
				.of(leftJ, rightJ);
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
		if (TreeUtils.enclosingClass(getCurrentPath()).getSimpleName().contentEquals("EncryptionSample"))
			return super.visitVariable(node, p);
		if (!varRef.getRawAnnotations().contains(checker.CLEAR)
				&& !varRef.getRawAnnotations().contains(checker.BOT)) {
				//&& !(inMainMethod && varRef.getKind() == RefKind.PARAMETER)) {
			processVariableTree(jcvd);
			JCExpression init = jcvd.getInitializer();
			if (init != null) {
				if (init instanceof JCLiteral && ((JCLiteral) init).getValue() == null)
					return super.visitVariable(node, p);
				Reference initRef = checker.getAnnotatedReference(init);
				// change type: int -> byte[]
				processVariableTree(init);
				// process conversion
				processInit(init, jcvd, initRef, varRef);
			}
		}
    	return super.visitVariable(node, p);
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
    
    private void processInit(JCExpression init, JCVariableDecl jcvd,
    		Reference initRef, Reference varRef) {
    	if (convertedReferences.containsKey(initRef.getIdentifier())) {
			for (Conversion con : convertedReferences.get(initRef.getIdentifier())) {
				if (con.getId() == checker.getPosition(init)) {
					jcvd.init = getConvertMethod(init, con);
					break;
				}
			}
		} else if (!varRef.getRawAnnotations().contains(checker.CLEAR)
				&& init.type.getKind() != TypeKind.BOOLEAN // boolean x = true;
				&& ((init instanceof JCLiteral && !shouldSkip(init, null)) // int x = null;
				|| (init instanceof JCMethodInvocation
						&& checker.isFromLibrary(TreeUtils.elementFromUse(init))))) {
			// int x = 9 -> int x = Conversion.encrypt(9, "AH")
			// int x = o.readLine() -> int x = Conversion.encrypt(o.readLine(), "AH")
			JCMethodInvocation jcmi = getConvertMethod(init, getSimpleEncryptName(varRef), true);
			jcvd.init = jcmi;
		}
	}
    
    @Override
	public Void visitMethod(MethodTree node, Void p) {
    	if (node.getName().contentEquals("main")) inMainMethod = true;
		else inMainMethod = false;
    	String id = checker.getFileName(node) + checker.getLineNumber(node);
    	JCMethodDecl jcmd = (JCMethodDecl) node;
    	// create two versions of a sensitive method
    	if (checker.getNeedCopyMethods().contains(id)) {
			TreeCopier<Void> treeCopier = new TreeCopier<Void>(maker);
			JCTree method = treeCopier.copy(jcmd);
			Tree parent = getCurrentPath().getParentPath().getLeaf();
			JCClassDecl jccd = (JCClassDecl) parent;
			jccd.defs = jccd.defs.append(method);
		}
    	// change the return type: int -> byte[]
    	ExecutableElement methodElt = (ExecutableElement) TreeUtils.elementFromDeclaration(node);
    	Reference methodRef = checker.getAnnotatedReference(methodElt);
		Reference returnRef = ((ExecutableReference) methodRef).getReturnRef();
		if (!returnRef.getRawAnnotations().contains(checker.CLEAR)) {
			processVariableTree(jcmd);
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
    	ExpressionTree method = node.getMethodSelect();
    	String methName = method.toString();
    	JCExpression[] args = node.getArguments().toArray(new JCExpression[0]);
    	int i = 0;
    	for (ExpressionTree arg : node.getArguments()) {
    		Reference argRef = checker.getAnnotatedReference(arg);
    		if (convertedReferences.containsKey(argRef.getIdentifier())) {
    			for (Conversion con : convertedReferences.get(argRef.getIdentifier())) {
    				if (con.getId() == checker.getPosition(arg)) {
    					args[i] = getConvertMethod(arg, con);
    					break;
    				}
    			}
    		} 
    		if (methName.startsWith("System.out.print")) {
    			String encryptType = getSimpleEncryptName(argRef);
    			if (encryptType != null && !(arg instanceof JCLiteral)) {
    				args[i] = getConvertMethod(arg, encryptType, false);
    			}
    		} else if (arg instanceof JCLiteral 
    				&& !argRef.getRawAnnotations().contains(checker.CLEAR)) {
    			args[i] = getConvertMethod(arg, getSimpleEncryptName(argRef), true);
    		}
    		i++;
    	}
    	JCMethodInvocation jcmi = (JCMethodInvocation) node;
    	jcmi.args = com.sun.tools.javac.util.List.from(args);
    	// string.equals(string1) -> Computation.equals(string, string1)
    	if (args.length == 1) processEqualsForString(method, args[0]);
    }
    
	private void processEqualsForString(ExpressionTree method, ExpressionTree arg) {
		// string.equals()
		if (method instanceof JCFieldAccess && method.toString().endsWith(".equals")) {
			// string
			ExpressionTree receiver = ((JCFieldAccess) method).getExpression();
			Reference receiverRef = checker.getAnnotatedReference(receiver);
			if (!receiverRef.getRawAnnotations().contains(checker.CLEAR)
					&& receiverRef.getType().toString().contains("String")) {
				Tree parent = getCurrentPath().getParentPath().getLeaf();
				JCMethodInvocation newCompMethod = getComputeMethod(receiver, arg, Tag.EQ);
				if (parent instanceof JCParens) {
					JCParens jcp = (JCParens) parent;
					if (newCompMethod != null) jcp.expr = newCompMethod;
				}
				if (parent instanceof JCBinary) {
					((JCBinary) parent).rhs = newCompMethod;
				}
			}
		}
	}

	@Override
    public Void visitBinary(BinaryTree node, Void p) {
		processBinaryTreeForConversion(node);
		processBinaryTreeForComputation(node, null);
		return super.visitBinary(node, p);
	}
	
	private void processBinaryTreeForComputation(BinaryTree node, BinaryTree outerTree) {
		// leftOperand + rightOperand -> Computation.add(leftOperand, rightOperand)
		ExpressionTree rightOperand = node.getRightOperand();
		ExpressionTree leftOperand = node.getLeftOperand();
		if (leftOperand instanceof JCBinary) {
			processBinaryTreeForComputation((BinaryTree) leftOperand, node);
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
		if (leftRef == null || !leftRef.getRawAnnotations().contains(checker.CLEAR)) {
			JCBinary jcb = (JCBinary) node;
			if (outerTree != null) {
				JCBinary jcbOuter = (JCBinary) outerTree;
				processJCBinaryForCompuatation(node, jcb, jcbOuter);
			} else {
				Tree parent = getCurrentPath().getParentPath().getLeaf();
				modifyForComputation(node, jcb, parent);
			}
		}
	}

	private void modifyForComputation(BinaryTree node, JCBinary jcb, Tree parent) {
		if (parent instanceof JCParens) {
			JCParens jcp = (JCParens) parent;
			JCMethodInvocation newCompMethod = getComputeMethod(node.getLeftOperand(), node.getRightOperand(),
					jcb.getTag());
			if (newCompMethod != null) jcp.expr = newCompMethod;
		}
		if (parent instanceof JCMethodInvocation) {
			JCMethodInvocation jcmi = (JCMethodInvocation) parent;
			processJCMethodInvocationForComputation(node, jcb, jcmi);
		}
		if (parent instanceof JCTypeCast) {
			JCTypeCast jctc = (JCTypeCast) parent;
			jctc.expr = getComputeMethod(node.getLeftOperand(), node.getRightOperand(), jcb.getTag());
		}
		if (parent instanceof JCForLoop) {
			JCForLoop jcfl = (JCForLoop) parent;
			jcfl.cond = getComputeMethod(node.getLeftOperand(), node.getRightOperand(), jcb.getTag());
		}
	}

	private void processJCBinaryForCompuatation(BinaryTree node, JCBinary jcb,
			JCBinary jcbParent) {
		JCMethodInvocation jcmi = getComputeMethod(node.getLeftOperand(), node.getRightOperand(),
				jcb.getTag());
		if (jcmi != null) jcbParent.lhs = jcmi;
	}

	private void processBinaryTreeForConversion(BinaryTree node) {
		ExpressionTree rightOperand = node.getRightOperand();
		ExpressionTree leftOperand = node.getLeftOperand();
		// skip a == null
		if (shouldSkip(leftOperand, rightOperand)) return;
		Reference leftRef = null, rightRef = null;
		if (!leftOperand.toString().startsWith("Conversion")) {
			leftRef = checker.getAnnotatedReference(leftOperand);
		}
		if (!rightOperand.toString().startsWith("Conversion")) {
			rightRef = checker.getAnnotatedReference(rightOperand);
		}
		// rightOperand -> Encryption.convert(right, from, to)
		modifyForConversion(node, rightRef, checker.getPosition(rightOperand), rightOperand, false);
		// recursively process leftOperand
		if (leftOperand instanceof JCBinary) {
			processBinaryTreeForConversion((BinaryTree) leftOperand);
		} else {
			// leftOperand -> Encryption.convert(left, from, to)
			modifyForConversion(node, leftRef, checker.getPosition(leftOperand), leftOperand, true);
		}
	}

	private void modifyForConversion(BinaryTree node, Reference ref,
			long nodeId, ExpressionTree operand, boolean isLeft) {
		boolean handled = false;
		if (ref != null && convertedReferences.containsKey(ref.getIdentifier())) {
			for (Conversion con : convertedReferences.get(ref.getIdentifier())) {
				if (con.getId() == nodeId) {
					JCBinary jcbt = (JCBinary) node;
					if (isLeft) jcbt.lhs = getConvertMethod(operand, con);
					else jcbt.rhs = getConvertMethod(operand, con);
					handled = true;
					break;
				}
			}
		}
//		if (!handled && !ref.getRawAnnotations().contains(checker.CLEAR)) {
//			JCBinary jcbt = (JCBinary) node;
//			String encryptType = getSimpleEncryptName(ref);
//			if (operand instanceof JCLiteral ||
//					(operand.getKind() == Kind.MEMBER_SELECT
//					&& checker.isFromLibrary(TreeUtils.elementFromUse(operand)))) {
//				JCMethodInvocation newMethod = getConvertMethod(operand, encryptType, true);
//				if (isLeft) jcbt.lhs = newMethod;
//				else jcbt.rhs = newMethod;
//			}
//		}
	}

	private void processJCMethodInvocationForComputation(BinaryTree node, JCBinary jcb,
			JCMethodInvocation jcmi) {
		com.sun.tools.javac.util.List<JCExpression> newArgs = com.sun.tools.javac.util.List.nil();
		for (JCExpression arg : jcmi.getArguments()) {
			if (arg.equals(node)) {
				JCMethodInvocation newCompMethod = getComputeMethod(node.getLeftOperand(), node.getRightOperand(),
						jcb.getTag());
				if (newCompMethod != null) newArgs = newArgs.append(newCompMethod);
				else newArgs = newArgs.append(arg);
			} else {
				newArgs = newArgs.append(arg);
			}
		}
		jcmi.args = newArgs;
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
			Reference returnRef = checker.getAnnotatedReference(expr);
			if (convertedReferences.containsKey(returnRef.getIdentifier())) {
				for (Conversion con : convertedReferences.get(returnRef.getIdentifier())) {
					if (con.getId() == checker.getPosition(expr)) {
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
		Tree parent = getCurrentPath().getParentPath().getLeaf();
		JCCompilationUnit jccd = (JCCompilationUnit) parent;
		if (node.getSimpleName().toString().equals("EncryptionSample")) {
			for (JCImport imp : jccd.getImports()) {
				imports = imports.append(imp);
			}
		} else {
			if (!hasImported) {
				jccd.defs = jccd.defs.prependList(imports);
				hasImported = true;
			}
		}
		return super.visitClass(node, p);
	}
	
	@Override
	public Void visitAssignment(AssignmentTree node, Void p) {
		ExpressionTree expression = node.getExpression();
		Reference expRef = checker.getAnnotatedReference(expression);
		if (convertedReferences.containsKey(expRef.getIdentifier())) {
			for (Conversion con : convertedReferences.get(expRef.getIdentifier())) {
				if (con.getId() == checker.getPosition(expression)) {
					JCAssign jcr = (JCAssign) node;
					jcr.rhs = getConvertMethod(expression, con);
					break;
				}
			}
		} else if (!expRef.getRawAnnotations().contains(checker.CLEAR)
				&& ((JCTree) expression).type.getKind() != TypeKind.BOOLEAN // boolean x = true;
				&& ((expression instanceof JCLiteral && !shouldSkip(expression, null)) // int x = null;
				|| (expression instanceof JCMethodInvocation // x = br.readLine();
						&& checker.isFromLibrary(TreeUtils.elementFromUse(expression))))) {
			JCAssign jcr = (JCAssign) node;
			jcr.rhs = getConvertMethod(expression, getSimpleEncryptName(expRef), true);
		}
		return super.visitAssignment(node, p);
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
			processUnaryTree(jcu);
		default:
			break;
		}
		return super.visitUnary(node, p);
	}

	private void processUnaryTree(JCUnary jcu) {
		JCExpression exp = jcu.getExpression();
		Reference expRef = checker.getAnnotatedReference(exp);
		if (expRef.getRawAnnotations().contains(checker.CLEAR)) return;
		// conversion: i++ -> Encryption.conversion(i, e1, e2)
		JCMethodInvocation convertMethod = null;
		if (convertedReferences.containsKey(expRef.getIdentifier())) {
			for (Conversion con : convertedReferences.get(expRef.getIdentifier())) {
				if (con.getId() == checker.getPosition(exp)) {
					convertMethod = getConvertMethod(exp, con);
					break;
				}
			}
		}
		ExpressionTree arg = convertMethod == null ? exp : convertMethod;
		// Encryption.encrypt(1, "AH")
		JCExpression expOne = maker.Literal(1);
		JCMethodInvocation convertOne = getConvertMethod(expOne, "AH", true);
		// Computation.add(i, Encryption.encrypt(1, "AH"))
		JCMethodInvocation computeMethod = getComputeMethod(arg, convertOne, Tag.PLUS);
		// covert back to AH from OPE if the enrypt type of exp is not AH
		String encryptType = getSimpleEncryptName(expRef);
		JCMethodInvocation method;
		if (encryptType.equals("AH")) {
			method = computeMethod;
		} else {
			Conversion conversion = new Conversion(0, "AH", encryptType);
			method = getConvertMethod(computeMethod, conversion);
		}
		// i++ -> i = i + 1;
		Tree parent = getCurrentPath().getParentPath().getLeaf();
		// For now, consider parent is JCExpressionStatement
		JCExpressionStatement jces = (JCExpressionStatement) parent;
		jces.expr = maker.Assign(exp, method);
	}
	
	@Override
	public Void visitArrayAccess(ArrayAccessTree node, Void p) {
		ExpressionTree index = node.getIndex();
		Reference indexRef = checker.getAnnotatedReference(index);
		if (index instanceof JCIdent && !indexRef.getRawAnnotations().contains(checker.CLEAR)) {
			JCArrayAccess jcaa = (JCArrayAccess) node;
			jcaa.index = getConvertMethod(index, getSimpleEncryptName(indexRef), false);
		}
		return super.visitArrayAccess(node, p);
	}
	
	@Override
	public Void visitNewArray(NewArrayTree node, Void p) {
		JCNewArray newArray = (JCNewArray) node;
		com.sun.tools.javac.util.List<JCExpression> newDims = com.sun.tools.javac.util.List.nil();
		for (JCExpression dim : newArray.getDimensions()) {
			Reference dimRef = checker.getAnnotatedReference(dim);
			if (dim instanceof JCIdent && !dimRef.getRawAnnotations().contains(checker.CLEAR)) {
				newDims = newDims.append(getConvertMethod(dim, getSimpleEncryptName(dimRef), false));
			} else {
				newDims = newDims.append(dim);
			}
		}
		newArray.dims = newDims;
		return super.visitNewArray(node, p);
	}
	
}

