/**
 * 
 */
package checkers.inference2.transform;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
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
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.IfTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.NewArrayTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.ParenthesizedTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.file.JavacFileManager;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCArrayAccess;
import com.sun.tools.javac.tree.JCTree.JCArrayTypeTree;
import com.sun.tools.javac.tree.JCTree.JCAssign;
import com.sun.tools.javac.tree.JCTree.JCAssignOp;
import com.sun.tools.javac.tree.JCTree.JCBinary;
import com.sun.tools.javac.tree.JCTree.JCBlock;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCExpressionStatement;
import com.sun.tools.javac.tree.JCTree.JCFieldAccess;
import com.sun.tools.javac.tree.JCTree.JCIdent;
import com.sun.tools.javac.tree.JCTree.JCIf;
import com.sun.tools.javac.tree.JCTree.JCImport;
import com.sun.tools.javac.tree.JCTree.JCLiteral;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.tree.JCTree.JCMethodInvocation;
import com.sun.tools.javac.tree.JCTree.JCNewArray;
import com.sun.tools.javac.tree.JCTree.JCNewClass;
import com.sun.tools.javac.tree.JCTree.JCParens;
import com.sun.tools.javac.tree.JCTree.JCPrimitiveTypeTree;
import com.sun.tools.javac.tree.JCTree.JCReturn;
import com.sun.tools.javac.tree.JCTree.JCStatement;
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
	private Map<String, Map<Integer, String[]>> convertedReferences;

	private String returnType;
	private static boolean inSample;

	private static JCExpression objectType;

	private static List<JCTree> imports = List.nil();

	public TransformVisitor(InferenceChecker checker, CompilationUnitTree root) {
		super(checker, root);
		this.checker = (TransformChecker) checker;
		this.checker.setCurrentFactory(atypeFactory);
		this.factory = atypeFactory;
		this.annoTypes = new AnnotatedTypes(checker.getProcessingEnvironment(), atypeFactory);
		Context context = new Context();
		JavacFileManager.preRegister(context);
		maker = TreeMaker.instance(context);
		convertedReferences = checker.getConvertedReferences();
	}

	/**
	 * Scan a single node. The current path is updated for the duration of the
	 * scan.
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
	 * et the current path for the node, as built up by the currently active set
	 * of scan calls.
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

	private JCExpression removeTypeCast(ExpressionTree arg) {
		if (arg instanceof JCTypeCast)
			return ((JCTypeCast) arg).getExpression();
		else
			return (JCExpression) arg;
	}

	private JCMethodInvocation getConvertMethod(ExpressionTree arg, String[] con, boolean special) {
		JCMethodInvocation fn;
		// from, to
		JCExpression fromTree = maker.Literal(con[0]);
		JCExpression toTree = maker.Literal(con[1]);
		// // add type cast: from Object to byte[]
		// JCExpression argJ = getTypeCast(arg);
		JCExpression argJ = removeTypeCast(arg);
		List<JCExpression> args;
		if (con[0].equals("CLEAR") || con[0].equals("BOT")) {
			// Encryption.encrypt(arg, to)
			fn = special ? encryptionMethods.get("encryptSpe") : encryptionMethods.get("encrypt");
			args = List.of(argJ, toTree);
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

	private JCMethodInvocation getComputeMethod(ExpressionTree left, ExpressionTree right, Tag tag) {
		// a + b -> Computation.add(a, b)
		JCMethodInvocation fn;
		JCExpression leftJ = removeTypeCast(left);
		JCExpression rightJ = removeTypeCast(right);
		// a, b
		List<JCExpression> args = List.of(leftJ, rightJ);
		// add, compareTo, or equals
		switch (tag) {
		case PLUS: // +
		case PREINC: // ++
		case POSTINC:
		case PLUS_ASG: // +=
			fn = encryptionMethods.get("add");
			break;
		case MINUS: // -
		case PREDEC:
		case POSTDEC: // --
		case MINUS_ASG: // -=
			fn = encryptionMethods.get("minus");
			break;
		case MUL: // *
		case MUL_ASG: // *=
			fn = encryptionMethods.get("multiply");
			break;
		// case DIV: // /
		// case DIV_ASG: // /=
		// fn = encryptionMethods.get("divide");
		// break;
		// case MOD: // %
		// case MOD_ASG: // %=
		// fn = encryptionMethods.get("mod");
		// break;
		// case SL: // <<
		// case SL_ASG: // <<=
		// fn = encryptionMethods.get("shiftLeft");
		// break;
		// case SR: // >>
		// case SR_ASG: // >>=
		// fn = encryptionMethods.get("shiftRight");
		// break;
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
		if (annos.isEmpty())
			return null;
		String fullname = annos.iterator().next().toString();
		return fullname.substring(fullname.lastIndexOf('.') + 1);
	}

	public Void visitVariable(VariableTree node, Void p) {
		VariableElement varElt = TreeUtils.elementFromDeclaration(node);
		JCVariableDecl jcvd = (JCVariableDecl) node;
		// get Object type from EncryptionSample
		if (objectType == null)
			setObjectType(jcvd);
		Reference varRef = checker.getAnnotatedReference(varElt);
		if (inSample)
			return super.visitVariable(node, p);
		if (!varRef.getRawAnnotations().contains(checker.CLEAR) && !varRef.getRawAnnotations().contains(checker.BOT)) {
			JCExpression newVartype = getObjectType(jcvd.getType());
			if (newVartype != null)
				jcvd.vartype = newVartype;
			JCExpression init = removeTypeCast(jcvd.getInitializer());
			if (init != null) {
				int pos = init.getStartPosition();
				Reference initRef = checker.getAnnotatedReference(init);
				// change type: int[] -> Object[]; only consider int[] x = new int[];
				if (init instanceof JCNewArray) {
					JCNewArray newArray = (JCNewArray) init;
					JCExpression newInitType = getObjectType(newArray.getType());
					if (newInitType != null)
						newArray.elemtype = newInitType;
				}
				if (init instanceof JCBinary) init = processBinaryTree(init);
				if (init instanceof JCMethodInvocation)	init = processMethodInvocation(init);
				jcvd.init = findConvertMethod(init, pos, initRef, false);
			}
		}
		return super.visitVariable(node, p);
	}

	private JCExpression processBinaryTree(JCExpression tree) {
		JCBinary binaryTree = (JCBinary) tree;
		JCExpression leftOperand = binaryTree.getLeftOperand();
		// operand -> Encryption.convert(operand, from, to)
		if (leftOperand instanceof JCBinary) {
			binaryTree.lhs = processBinaryTree(leftOperand);
		} else {
			binaryTree.lhs = findConvertMethod(leftOperand);
		}
		JCExpression rightOperand = binaryTree.getRightOperand();
		if (rightOperand instanceof JCBinary) {
			binaryTree.rhs = processBinaryTree(rightOperand);
		} else {
			binaryTree.rhs = findConvertMethod(rightOperand);
		}
		// leftOperand + rightOperand -> Computation.add(leftOperand,
		// rightOperand)
		if (shouldSkip(leftOperand, rightOperand))
			return binaryTree;
		if (hasChanged(binaryTree.getLeftOperand()) || hasChanged(binaryTree.getRightOperand())) {
			JCMethodInvocation meth = getComputeMethod(binaryTree.getLeftOperand(), binaryTree.getRightOperand(), binaryTree.getTag());
			return meth == null ? binaryTree : meth;
		}
		Reference leftRef = checker.getAnnotatedReference(leftOperand);
		Reference rightRef = checker.getAnnotatedReference(rightOperand);
		if ((shouldConvert(leftRef) && !leftRef.getRawAnnotations().contains(checker.CLEAR))
				|| (shouldConvert(rightRef) && !rightRef.getRawAnnotations().contains(checker.CLEAR))) {
			JCMethodInvocation meth = getComputeMethod(binaryTree.getLeftOperand(), binaryTree.getRightOperand(), binaryTree.getTag());
			return meth == null ? binaryTree : meth;
		}
		return binaryTree;
	}

	private boolean hasChanged(JCExpression exp) {
		String name = exp.toString();
		return name.startsWith("Conversion") || name.startsWith("Computation");
	}

	private JCExpression findConvertMethod(JCExpression operand) {
		ExpressionTree exp = removeTypeCast(operand);
		String id = checker.getIdentifier(exp);
		Reference ref = checker.getAnnotatedReferences().get(id);
		if (ref == null)
			ref = checker.getAnnotatedReference(exp);
		JCExpression convertMethod = findConvertMethod(operand, operand.getStartPosition(), ref, false);
		return convertMethod;
	}

	// check the type of the variable if it is int or String
	private boolean shouldConvert(Reference ref) {
		String javaType = ref.getType().getUnderlyingType().toString();
		return javaType.equals("int") || javaType.equals("java.lang.String") || javaType.equals("Integer");
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

	private JCExpression findConvertMethod(JCExpression exp, int pos, Reference ref, boolean special) {
		if (!shouldConvert(ref)) return exp;
		if (convertedReferences.containsKey(ref.getIdentifier())) {
			Map<Integer, String[]> conversions = convertedReferences.get(ref.getIdentifier());
			if (conversions.containsKey(pos)) {
				String[] con = conversions.get(pos);
				return getConvertMethod(exp, con, special);
			}
		}
		return exp;
	}

	@Override
	public Void visitNewClass(NewClassTree node, Void p) {
		Element methElt = TreeUtils.elementFromUse(node);
		JCNewClass newClass = (JCNewClass) node;
		JCExpression[] args = node.getArguments().toArray(new JCExpression[0]);
		if (checker.isFromLibrary(methElt)) {
			newClass.args = List.from(decryptParameter(args));
		} else {
			for (int i = 0; i < args.length; i++) {
				Reference argRef = checker.getAnnotatedReference(args[i]);
				args[i] = findConvertMethod(args[i], args[i].getStartPosition(), argRef, false);
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
			// processVariableTree(jcmd, returnRef);
			JCExpression newReturnType = getObjectType(jcmd.getReturnType());
			if (newReturnType != null)
				jcmd.restype = newReturnType;
			returnType = getSimpleEncryptName(returnRef);
		}
		return super.visitMethod(node, p);
	}

	private JCExpression getObjectType(JCTree tree) {
		if (tree == null)
			return null;
		Tag tag = tree.getTag();
		if (tag == Tag.TYPEIDENT) {
			JCPrimitiveTypeTree jcptt = (JCPrimitiveTypeTree) tree;
			if (jcptt.getPrimitiveTypeKind() == TypeKind.INT) {
				return getObjectType(jcptt.getStartPosition());
			}
		} else if (tag == Tag.IDENT) {
			JCIdent jci = (JCIdent) tree;
			if (jci.getName().contentEquals("String") || jci.getName().contentEquals("Integer")) {
				return getObjectType(jci.getStartPosition());
			}
		} else if (tag == Tag.TYPEARRAY) {
			JCArrayTypeTree arrayType = (JCArrayTypeTree) tree;
			JCExpression eleType = getObjectType(arrayType.getType());
			if (eleType != null) {
				arrayType.elemtype = eleType;
				return arrayType;
			}
		}
		return null;
	}

	@Override
	public Void visitMethodInvocation(MethodInvocationTree node, Void p) {
		JCMethodInvocation jcmi = (JCMethodInvocation) node;
		if (inSample) {
			String methName = node.getMethodSelect().toString();
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
			// case "Computation.divide":
			// encryptionMethods.put("divide", jcmi);
			// break;
			// case "Computation.mod":
			// encryptionMethods.put("mod", jcmi);
			// break;
			// case "Computation.shiftLeft":
			// encryptionMethods.put("shiftLeft", jcmi);
			// break;
			// case "Computation.shiftRight":
			// encryptionMethods.put("shiftRight", jcmi);
			// break;
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
			}
		} else processMethodInvocation(jcmi);
		return super.visitMethodInvocation(node, p);
	}

	private JCExpression processMethodInvocation(JCExpression node) {
		if (hasChanged(node)) return node;
		Element methElt = TreeUtils.elementFromUse(node);
		JCMethodInvocation tree = (JCMethodInvocation) node;
		JCExpression[] args = tree.getArguments().toArray(new JCExpression[0]);
		if (checker.isFromLibrary(methElt)) {
			if (methElt.toString().startsWith("compareTo") || methElt.toString().startsWith("equals")) {
				/*
				 * a.equals(b) a b
				 *  sen   clear Computation.equals(a, encrypt(b, "DET"))
				 *  sen   sen   Computation.equals(a, b)
				 *  clear sen   Computation.equals(encrypt(a, "DET"), b)
				 *  clear clear do nothing
				 */
				Reference argRef = checker.getAnnotatedReference(args[0]);
				JCExpression rcvTree = ((JCFieldAccess) tree.getMethodSelect()).getExpression();
				Reference rcvRef = checker.getAnnotatedReference(rcvTree);
				if (!argRef.getRawAnnotations().contains(checker.CLEAR)) {
					args[0] = findConvertMethod(args[0], args[0].getStartPosition(), argRef, false);
					if (rcvRef.getRawAnnotations().contains(checker.CLEAR))
						rcvTree = getConvertMethod(rcvTree, new String[] { "CLEAR", "DET" }, false);
					else rcvTree = findConvertMethod(rcvTree, rcvTree.getStartPosition(), rcvRef, false);
					return getComputeMethod(rcvTree, args[0], Tag.EQ);
				} else {
					if (!rcvRef.getRawAnnotations().contains(checker.CLEAR)) {
						rcvTree = findConvertMethod(rcvTree, rcvTree.getStartPosition(), rcvRef, false);
						args[0] = getConvertMethod(args[0], new String[] { "CLEAR", "DET" }, false);
						return getComputeMethod(rcvTree, args[0], Tag.EQ);
					}
				}
			} else {
				// process library method call: e.g. System.out.print(decrypt(a, "DET"))
				tree.args = List.from(decryptParameter(args));
			}
		} else { // regular method call
			for (int i = 0; i < args.length; i++) {
				if (hasChanged(args[i])) continue;
				JCExpression arg = removeTypeCast(args[i]);
				Reference argRef = checker.getAnnotatedReferences().get(checker.getIdentifier(arg));
				args[i] = findConvertMethod(arg, args[i].getStartPosition(), argRef, false);
			}
			tree.args = List.from(args);
		}
		return node;
	}

	protected JCExpression[] decryptParameter(JCExpression[] args) {
		for (int i = 0; i < args.length; i++) {
			Reference argRef = checker.getAnnotatedReference(args[i]);
			if (!argRef.getRawAnnotations().contains(checker.CLEAR)) {
				String encryptType = getSimpleEncryptName(argRef);
				if (encryptType != null) {
					args[i] = getConvertMethod(args[i], new String[] { encryptType, "CLEAR" }, false);
				}
			}
		}
		return args;
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
		JCReturn ret = (JCReturn) node;
		JCExpression expr = ret.getExpression();
		if (expr != null) {
			if (expr instanceof JCParens) {
				expr = ((JCParens) expr).getExpression();
			}
			Reference returnRef = checker.getAnnotatedReference(expr);
			if (expr instanceof JCLiteral && returnType != null && shouldConvert(returnRef)) {
				ret.expr = getConvertMethod(expr, new String[] { "CLEAR", returnType }, false);
			} else {
				int pos = expr.getStartPosition();
				if (expr instanceof JCBinary) expr = processBinaryTree(expr);
				ret.expr = findConvertMethod(expr, pos, returnRef, false);
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

//	@Override
//	public Void visitAssignment(AssignmentTree node, Void p) {
//		JCAssign assign = (JCAssign) node;
//		JCExpression exp = removeTypeCast(assign.getExpression());
//		if (!hasChanged(exp)) {
//			Reference expRef = checker.getAnnotatedReference(exp);
//			if (exp instanceof JCUnary) {
//				JCExpression var = ((JCUnary) exp).getExpression();
//				Reference varRef = checker.getAnnotatedReference(var);
//				if (!varRef.getRawAnnotations().contains(checker.CLEAR)) {
//					assign.rhs = var;
//					Tree parent = getCurrentPath().getParentPath().getLeaf();
//					Tag tag = exp.getTag();
//					if (tag == Tag.POSTDEC || tag == Tag.POSTINC) {
//						assign.rhs = processUnaryTree(tag, varRef, var);		
//					}
//				}
//				
//				
//			}
//			else assign.rhs = findConvertMethod(exp, exp.getStartPosition(), expRef,	false);
//		}
//		return super.visitAssignment(node, p);
//	}

	@Override
	public Void visitExpressionStatement(ExpressionStatementTree node, Void p) {
		ExpressionTree expression = node.getExpression();
		if (expression instanceof JCAssignOp) {
			JCAssignOp assign = (JCAssignOp) expression;
			JCExpression exp = assign.getExpression();
			Reference expRef = checker.getAnnotatedReference(exp);
			if (!expRef.getRawAnnotations().contains(checker.CLEAR)) {
				int pos = exp.getStartPosition();
				if (exp instanceof JCBinary)
					exp = processBinaryTree(exp);
				// Encryption.conversion(y, from, to)
				JCExpression arg = findConvertMethod(exp, pos, expRef, false);
				// Computation.add(x, y)
				JCMethodInvocation computeMethod = getComputeMethod(arg, assign.getVariable(), assign.getTag());
				((JCExpressionStatement) node).expr = maker.Assign(assign.getVariable(), computeMethod);
			}
		} else if (expression instanceof JCAssign) {
			processAssignment(node, expression);
		}
		return super.visitExpressionStatement(node, p);
	}

	private void processAssignment(ExpressionStatementTree node, ExpressionTree expression) {
		JCAssign assign = (JCAssign) expression;
		JCExpression exp = removeTypeCast(assign.getExpression());
		if (!hasChanged(exp)) {
			Reference expRef = checker.getAnnotatedReference(exp);
			if (exp instanceof JCUnary) {
				JCExpression var = ((JCUnary) exp).getExpression();
				Reference varRef = checker.getAnnotatedReference(var);
				if (!varRef.getRawAnnotations().contains(checker.CLEAR)) {
					assign.rhs = var;
					Tag tag = exp.getTag();
					Symbol v = ((JCIdent) var).sym;
					JCStatement stat = maker.Assignment(v, processUnaryTree(exp, expRef));
					Tree parent = getCurrentPath().getParentPath().getLeaf();
					if (parent instanceof JCBlock) {
						JCBlock block = (JCBlock) parent;
						List<JCStatement> stats = block.getStatements();
						Iterator<JCStatement> iter = stats.iterator();
						List<JCStatement> newStats = null;
						while (iter.hasNext()) {
							JCStatement s = iter.next();
							if (s == node) {
								if (tag == Tag.POSTDEC || tag == Tag.POSTINC) {
									newStats = newStats == null ? List.of(s) : newStats.append(s);
									newStats = newStats.append(stat);
								} else {
									newStats = newStats == null ? List.of(stat) : newStats.append(stat);
									newStats = newStats.append(s);
								}
							} else newStats = newStats == null ? List.of(s) : newStats.append(s);
						}
						block.stats = newStats;
					}
				}
			}
			else assign.rhs = findConvertMethod(exp, exp.getStartPosition(), expRef,	false);
		}
	}

	private JCExpression processUnaryTree(JCExpression unary, Reference unaryRef) {
		JCExpression exp = ((JCUnary) unary).getExpression();
		// conversion: i++ -> Encryption.conversion(i, e1, e2)
		Reference expRef = checker.getAnnotatedReference(exp);
		JCExpression arg = findConvertMethod(exp, exp.getStartPosition(), expRef, false);
		// Encryption.encrypt(1, "AH")
		JCExpression expOne = maker.Literal(1);
		JCMethodInvocation convertOne = getConvertMethod(expOne, new String[] { "CLEAR", "AH" }, false);
		// Computation.add(i, Encryption.encrypt(1, "AH"))
		JCMethodInvocation meth = getComputeMethod(arg, convertOne, unary.getTag());
		return findConvertMethod(meth, unary.getStartPosition(), unaryRef, false);
	}

	@Override
	public Void visitArrayAccess(ArrayAccessTree node, Void p) {
		ExpressionTree index = node.getIndex();
		Reference indexRef = checker.getAnnotatedReference(index);
		if (index instanceof JCIdent && !indexRef.getRawAnnotations().contains(checker.CLEAR)) {
			JCArrayAccess jcaa = (JCArrayAccess) node;
			jcaa.index = getConvertMethod(index, new String[] { getSimpleEncryptName(indexRef), "CLEAR" }, false);
		}
		return super.visitArrayAccess(node, p);
	}

	@Override
	public Void visitNewArray(NewArrayTree node, Void p) {
		JCNewArray newArray = (JCNewArray) node;
		List<JCExpression> newDims = List.nil();
		for (JCExpression dim : newArray.getDimensions()) {
			dim = removeTypeCast(dim);
			Reference dimRef = checker.getAnnotatedReference(dim);
			if (!dimRef.getRawAnnotations().contains(checker.CLEAR)) {
				newDims = newDims
						.append(getConvertMethod(dim, new String[] { getSimpleEncryptName(dimRef), "CLEAR" }, false));
			} else {
				newDims = newDims.append(dim);
			}
		}
		newArray.dims = newDims;
		return super.visitNewArray(node, p);
	}

	@Override
	public Void visitIf(IfTree node, Void p) {
		JCExpression condition = ((JCParens) node.getCondition()).getExpression();
		if (condition instanceof JCBinary) {
			((JCIf) node).cond = processBinaryTree((JCExpression) condition);
		}
		return super.visitIf(node, p);
	}
	
	@Override
	public Void visitParenthesized(ParenthesizedTree node, Void p) {
		JCParens paren = (JCParens) node;
		JCExpression exp = paren.getExpression();
		if (exp instanceof JCBinary) paren.expr = processBinaryTree(exp);
		if (exp instanceof JCAssign) paren.expr = processAssignment(exp);
		return super.visitParenthesized(node, p);
	}
	
	@Override
	public Void visitAssignment(AssignmentTree node, Void p) {
		JCAssign assign = (JCAssign) node;
		JCExpression exp = assign.getExpression();
		if (exp instanceof JCNewArray) {
			JCNewArray newArray = (JCNewArray) exp;
			JCExpression newInitType = getObjectType(newArray.getType());
			if (newInitType != null)
				newArray.elemtype = newInitType;
		}
		return super.visitAssignment(node, p);
	}

	private JCExpression processAssignment(JCExpression exp) {
		JCAssign assign = (JCAssign) exp;
		JCExpression expr = assign.getExpression();
		if (expr instanceof JCMethodInvocation) assign.rhs = processMethodInvocation(expr);
		Reference expRef = checker.getAnnotatedReference(expr);
		assign.rhs = findConvertMethod(assign.rhs, expr.getStartPosition(), expRef, false);
		return assign;
	}

}