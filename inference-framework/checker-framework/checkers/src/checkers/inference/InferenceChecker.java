/**
 * 
 */
package checkers.inference;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

import checkers.basetype.BaseTypeChecker;
import checkers.basetype.BaseTypeVisitor;
import checkers.inference.Reference.ArrayReference;
import checkers.inference.Reference.DeclaredReference;
import checkers.inference.Reference.ExecutableReference;
import checkers.inference.Reference.PrimitiveReference;
import checkers.inference.Reference.VoidReference;
import checkers.types.AnnotatedTypeFactory;
import checkers.types.AnnotatedTypeMirror;
import checkers.types.AnnotatedTypeMirror.AnnotatedArrayType;
import checkers.types.AnnotatedTypeMirror.AnnotatedDeclaredType;
import checkers.types.TypeHierarchy;
import checkers.util.AnnotationUtils;
import checkers.util.ElementUtils;
import checkers.util.InternalUtils;
import checkers.util.MultiGraphQualifierHierarchy;
import checkers.util.TreeUtils;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.PrimitiveTypeTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symbol.TypeSymbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.Type.ClassType;
import com.sun.tools.javac.comp.AttrContext;
import com.sun.tools.javac.comp.Enter;
import com.sun.tools.javac.comp.Env;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeInfo;

/**
 * @author huangw5
 *
 */
@SupportedOptions( { "warn", "checking" } ) 
public abstract class InferenceChecker extends BaseTypeChecker {
	
	private boolean isChecking = false; 
	
	protected Enter enter;

	private SourcePositions positions;
	
	private Comparator<AnnotationMirror> comparator;
	
	private Set<String> libPureMethods = null;
	
	private final String annotatedFolder = "annotated";

	private Types types;
	
	public static boolean DEBUG = false;
	
	public static enum FailureStatus {
		IGNORE,
		WARN,
		ERROR
	}
	
	@Override
	public void initChecker(ProcessingEnvironment processingEnv) {
		super.initChecker(processingEnv);
		isChecking = processingEnv.getOptions().containsKey("checking");
		this.enter = Enter.instance(((JavacProcessingEnvironment) env).getContext());
		this.positions = Trees.instance(getProcessingEnvironment()).getSourcePositions();
		InferenceMain.getInstance().setInferenceChcker(this);
		if (getProcessingEnvironment().getOptions().containsKey(
				"debug")) {
			DEBUG = true;
		}
        types = processingEnv.getTypeUtils();
	}
	
	@Override
	protected BaseTypeVisitor<?> createSourceVisitor(CompilationUnitTree root) {
		if (!isChecking) {
			// create inference visitor
			return getInferenceVisitor(this, root);
		}
		return super.createSourceVisitor(root);
	}
	
	@Override
	protected TypeHierarchy createTypeHierarchy() {
    	return new InferenceTypeHierarchy(this, getQualifierHierarchy());
	}
	
	
    /** Factory method to easily change what Factory is used to
     * create a QualifierHierarchy.
     */
	@Override
    protected MultiGraphQualifierHierarchy.MultiGraphFactory createQualifierHierarchyFactory() {
        return new InferenceGraphQualifierHierarchy.InferenceGraphFactory(this);
    }

	public boolean isChecking() {
		return isChecking;
	}
	
	public void insertInferredAnnotations(List<Reference> refs) {
		// Build a map from filename to insertions
		Map<String, List<Reference>> map = new HashMap<String, List<Reference>>();
		for (Reference ref : refs) {
			String fileName = ref.getFileName();
			List<Reference> list = map.get(fileName);
			if (list == null) {
				list = new ArrayList<Reference>();
				map.put(fileName, list);
			}
			list.add(ref);
		}
		
		File folder = new File(annotatedFolder);
		if (!folder.exists())
			folder.mkdirs();
		

		
		for (Entry<String, List<Reference>> entry : map.entrySet()) {
			String fileName = entry.getKey();
			// Read the source file into a buffer.
			StringBuffer source = new StringBuffer();
			
	        FileInputStream in = null;
	        try {
		        in = new FileInputStream(fileName);
		        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		        int c;
		        while ((c = in.read()) != -1)
		            bytes.write(c);
		        source.append(bytes.toString());
	        } catch (IOException e) {
	        	e.printStackTrace();
	        } finally {
	        	try {
					if (in != null)
						in.close();
				} catch (IOException e) {
				}
	        }
			
			List<Reference> list = entry.getValue();
			// Sort inserts in reverse order. We insert the annotations from 
			// back to front
			Collections.sort(list, new Comparator<Reference>() {
				@Override
				public int compare(Reference o1, Reference o2) {
					return o2.getOffset() - o1.getOffset();
				}
			});
			
			// TODO: new array, and array
		    Pattern varPattern = Pattern.compile("((public|protected|private|static|final|transient)\\s+)?((/\\*@\\w+\\*/\\s*)*[\\w\\.\\<\\>]+(\\s*(/\\*@\\w+\\*/\\s*)*\\[\\])*(\\s*/\\*[^/\\*]*\\*/)?)\\s+(\\w+)\\s*(/\\*[^\\*/]*\\*/)?\\s*(;|=|,|\\)|:)");
		    Pattern newclassPattern = Pattern.compile("(^|\\s)new\\s+([\\w\\.\\<\\>@\\*/ ]+)(\\s*/\\*[^/\\*]*\\*/)?\\s*\\(([^\\)]*)\\)");
		    Pattern methodPattern = Pattern.compile("((public|protected|private|static|final)\\s+)?(((/\\*@\\w+\\*/\\s*)*[\\w\\.\\<\\>]+(\\s*(/\\*@\\w+\\*/\\s*)*\\[\\])*)(\\s*/\\*[^/\\*]*\\*/)?\\s+)?(\\w+)\\s*\\(([^\\);]*)\\)");

			
			// Now we insert annotations
			for (Reference ref : list) {
				int offset = ref.getOffset();
				Element elt = ref.getElement();
				Tree tree = ref.getTree();
				int x = source.indexOf(";", offset) ;
				if (x < 0) x = Integer.MAX_VALUE;
				int y = source.indexOf("{", offset) ;
				if (y < 0) y = Integer.MAX_VALUE;
				int z = source.indexOf("}", offset) ;
				if (z < 0) z = Integer.MAX_VALUE;
				int end = (x < y ? x : y);
				end = (end < z ? end : z) + 1;
				String s = source.substring(offset, end);
				String typeStr = null, annoStr = null;
				// FIXME: WEI: Comment out for AtomicSet
//				if (ref.getType().getKind().isPrimitive())
//					continue;
				if (ref instanceof ExecutableReference) {
					if (isCompilerAddedConstructor((ExecutableElement) elt))
						continue;
					
					int returnStart = -1, returnEnd = -1, thisOffset = -1;
					boolean hasParams = false;
					if (ref.getElement().getSimpleName().contentEquals("<init>")) {
						// it is a constructor
						Matcher matcher = methodPattern.matcher(s);
						if (matcher.find()) {
							thisOffset = matcher.start(10) + offset;
							if (!matcher.group(10).trim().equals(""))
								hasParams = true;
						}
					} else {
						// a method
						Matcher matcher = methodPattern.matcher(s);
						if (matcher.find()) {
							returnStart = matcher.start(4) + offset;
							returnEnd = matcher.end(4) + offset;
							thisOffset = matcher.start(10) + offset;
							if (!matcher.group(10).trim().equals(""))
								hasParams = true;
						}
					}
				
					if (returnStart < 0 && thisOffset < 0) {
						System.err.println("WARN: insertion failed: " + ref);
					}
					// Insert this
					if (thisOffset > 0
							&& (elt == null || !ElementUtils.isStatic(elt))) {
						Reference rcvRef = ((ExecutableReference) ref)
								.getReceiverRef();
						String annos = ""
								+ InferenceUtils.formatAnnotationString(rcvRef
										.getAnnotations()) + " "
								+ ((AnnotatedDeclaredType) rcvRef.getType())
										.getUnderlyingType().asElement()
										.getSimpleName()
								+ " this" + (hasParams ? ", " : "");
						if (!s.startsWith("/*>>>"))
							source.insert(thisOffset, annos);
					}
					// Insert return
					Reference returnRef = ((ExecutableReference) ref).getReturnRef();
					if (returnStart > 0
							&& !(returnRef instanceof VoidReference)
							&& !(returnRef instanceof PrimitiveReference)) {
						insertAnnotations(returnRef, source, returnStart,
								returnEnd);
					}
				} else if (tree != null && tree.getKind() == Kind.NEW_CLASS) {
					Matcher matcher = newclassPattern.matcher(s);
					if (matcher.find()) {
						insertAnnotations(ref, source,
								offset + matcher.start(2),
								offset + matcher.end(2));
					} else 
						System.err.println("WARN: cannot insert annotations "
								+ ref.getFileName() + ":" + ref.getLineNum()
								+ " " + ref.getTree());
				} else if (tree != null && tree.getKind() == Kind.NEW_ARRAY) {
				} else {
					Matcher matcher = varPattern.matcher(s);
					if (matcher.find()) {
						insertAnnotations(ref, source,
								offset + matcher.start(3),
								offset + matcher.end(3));
					} else 
						System.err.println("WARN: cannot insert annotations "
								+ ref.getFileName() + ":" + ref.getLineNum()
								+ " " + ref.getElement());
				}
			}
//			System.out.println(source);
			File outFile = new File(annotatedFolder, fileName);
			outFile.getParentFile().mkdirs();
	        OutputStream output = null;
	        try {
				output = new FileOutputStream(outFile);
				output.write(source.toString().getBytes());
				output.flush();
				output.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("INFO: annotated source files are written into " + annotatedFolder + "/");
	}
	
	/**
	 * 
	 * @param ref
	 * @param source
	 * @param typeStr
	 * @param annoStr is the existing annotations. It can be null.
	 * @param start
	 * @param end
	 */
	private void insertAnnotations(Reference ref, StringBuffer source,
			int start, int end) {
		String annotated = ref.getAnnotatedType().toString() + "";
		String annoStr = InferenceUtils.formatAnnotationString(ref
				.getAnnotations());
		if (!source.substring(start, end).contains(annoStr))
			source.replace(start, end, annotated);
	}
	
	private String getFormattedAnnotations(Set<AnnotationMirror> set) {
		String annos = "/*" + InferenceUtils.formatAnnotationString(set)
				+ "*/ ";
		return annos;
	}
	
	protected Reference getMaximal(Reference ref) {
		Reference copy = ref;
//		Reference copy = ref.getCopy(); // FIXME WEI: add ".getCopy()" on Nov 30, 201
		AnnotationMirror[] annos = copy.getAnnotations().toArray(
				new AnnotationMirror[0]);
		if (annos.length == 0)
			return copy;
		// sort
		Arrays.sort(annos, getComparator());
		// get the maximal annotation
		Set<AnnotationMirror> maxAnnos = AnnotationUtils.createAnnotationSet();
		maxAnnos.add(annos[0]);
		copy.setAnnotations(maxAnnos);
		
		// Check the type of ref
		if (copy instanceof DeclaredReference) {
			List<? extends Reference> typeArgs = 
					((DeclaredReference) copy).getTypeArguments();
			if (typeArgs != null) {
				List<Reference> ts = new ArrayList<Reference>(typeArgs.size());
				for (Reference typeArgRef : typeArgs)
					ts.add(getMaximal(typeArgRef));
				((DeclaredReference) copy).setTypeArguments(ts);
			}
		} else if (copy instanceof ArrayReference) {
			Reference componentRef = ((ArrayReference) copy).getComponentRef();
			((ArrayReference) copy).setComponentRef(getMaximal(componentRef));
		} else if (copy instanceof ExecutableReference) {
			ExecutableReference executableRef = (ExecutableReference) copy;
			executableRef.setReceiverRef(getMaximal(executableRef.getReceiverRef()));
			executableRef.setReturnRef(getMaximal(executableRef.getReturnRef()));
		} 
		return copy;
	}
	
	/**
	 * Fill all possible annotations for unqualified references
	 * @param refs
	 */
	public void fillAllPossibleAnnos(List<Reference> refs) {
		for (Reference ref : refs) {
			if (ref.getAnnotations().isEmpty()) {
				ref.setAnnotations(getSourceLevelQualifiers());
			}
		}
	}
	
	/**
	 * Get the root tree of element elt
	 * @param elt
	 * @return
	 */
	public CompilationUnitTree getRootByElement(Element elt) {
		Symbol symbol = (Symbol) elt;
		TypeSymbol enclosing = symbol.enclClass();
		Env<AttrContext> env = enter.getEnv(enclosing);
		if (env == null)
			return null;
		return env.toplevel;
	}
	
	public Tree getDeclaration(Element elt) {
        Tree fromElt;
        CompilationUnitTree newRoot = getRootByElement(elt);
        if (newRoot == null)
        	return null;
        switch (elt.getKind()) {
        case CLASS:
        case ENUM:
        case INTERFACE:
        case ANNOTATION_TYPE:
        case FIELD:
        case ENUM_CONSTANT:
        case METHOD:
        case CONSTRUCTOR:
            fromElt = trees.getTree(elt);
            break;
        default:
            fromElt = TreeInfo.declarationFor((Symbol)elt, (JCTree)newRoot);
            break;
        }
        return fromElt;
	}
	
	/**
	 * Check whether fieldElt is a field in typeElt
	 * @param typeElt
	 * @param fieldElt
	 * @return
	 */
	protected boolean isFieldElt(AnnotatedTypeMirror type, Element fieldElt) {
		if (fieldElt.getKind() != ElementKind.FIELD)
			return false;
		if (ElementUtils.isStatic(fieldElt) 
				&& fieldElt.getSimpleName().contentEquals("class"))
			return true;
		if (type instanceof AnnotatedArrayType && fieldElt.getSimpleName().contentEquals("length"))
			return true;
		TypeElement typeElt = (type instanceof AnnotatedDeclaredType)? 
            		(TypeElement) ((AnnotatedDeclaredType) type).getUnderlyingType().asElement() 
            		: null;
        if (typeElt == null)
        	return false;
		VariableElement findFieldInType = ElementUtils.findFieldInType(typeElt, fieldElt.toString());
		TypeMirror superclass = typeElt.getSuperclass();
		while (findFieldInType == null && superclass.getKind() != TypeKind.NONE) {
			typeElt = (TypeElement) ((ClassType) superclass).asElement();
			superclass = typeElt.getSuperclass();
			findFieldInType = ElementUtils.findFieldInType(typeElt, fieldElt.toString());
		}
		return findFieldInType != null;
	}
	
	/**
	 * Check whether fieldElt is a field in typeElt
	 * @param expr
	 * @param fieldElt
	 * @return
	 */
//	protected boolean isFieldElt(ExpressionTree expr, Element fieldElt) {
//		if (fieldElt.getKind() != ElementKind.FIELD)
//			return false;
////		AnnotatedTypeMirror exprType = factory.getAnnotatedType(expr);
////		if (exprType.getKind() == TypeKind.DECLARED) {
////			TypeElement t = (TypeElement) ((AnnotatedDeclaredType) exprType)
////					.getUnderlyingType().asElement();
////			return isFieldElt(t, fieldElt);
////		}
//		return false;
//	}
	
    
	protected Element getOuterThisElement(MemberSelectTree mTree,
			ExecutableElement currentMethodElt) {
    	if (!isAccessOuterThis(mTree))
    		return null;
        if (currentMethodElt == null) {
        	return null;
        }
    	Element enclosingElt = currentMethodElt.getEnclosingElement();
        TypeElement element = (TypeElement)InternalUtils.symbol(mTree.getExpression());
        while (enclosingElt != null) {
            if (enclosingElt instanceof ExecutableElement) {
                ExecutableElement method = (ExecutableElement) enclosingElt;
                if (method.asType() != null
                        && isSubtype(
                    		(TypeElement) method.getEnclosingElement(), element))
                    if (ElementUtils.isStatic(method)) {
                        enclosingElt = null;
                        break;
                    }
                    else
                        break;
            }
            else if (enclosingElt instanceof TypeElement) {
                if (isSubtype((TypeElement) enclosingElt, element))
                    break;
            }
            enclosingElt = enclosingElt.getEnclosingElement();
        }
        return enclosingElt;
    }
	
    /**
     * Check if this mTree is accessing expr like "Body.this"
     * Consider replace it with {@link AnnotatedTypeFactory#isAnyEnclosingThisDeref(ExpressionTree)}
     * @param mTree
     * @return
     */
    protected boolean isAccessOuterThis(MemberSelectTree mTree) {
		if (!(mTree.getExpression() instanceof PrimitiveTypeTree)) {
			if (mTree.getIdentifier().contentEquals("this")) {
				return true;
			}
		}
		return false;
    }
    
	
	public boolean isExceptionClass(AnnotatedTypeMirror type) {
		boolean result = false;
		TypeMirror underlyingType = type.getUnderlyingType();
		if (underlyingType instanceof ClassType) {
			Type supertype_field = ((ClassType) underlyingType).supertype_field;
			while (supertype_field != null) {
				if (supertype_field.toString().equals("java.lang.Throwable")) {
					result = true;
					break;
				} else if (supertype_field instanceof ClassType) {
					supertype_field = ((ClassType) supertype_field).supertype_field;
				} else
					break;
			}
		}
		return result;
	}


	public boolean isAnnotated(AnnotatedTypeMirror type) {
		return type.isAnnotated();
	}

	
	/**
	 * Decide if {@code methodElt} is a pure library method. It reads pure
	 * library methods from a text file, which includes all pure library 
	 * methods.
	 * @param methodElt
	 * @return
	 */
	public boolean isPureLibraryMethod(ExecutableElement methodElt) {
		if (libPureMethods == null) {
			libPureMethods = new HashSet<String>();
			String property = getProcessingEnvironment().getOptions().get(
					"libPureMethods");
			if (property == null) {
				// No library pure methods are given
				System.err.println("WARN: all library methods are assumed as impure.");
				System.err.println("\tPure libary methods can be given by "
						+ "-AlibPureMethods=file1:file2 separated by colons");
			} else {
				for (String fileName : property.split(":")) {
					fileName = fileName.trim();
			    	BufferedReader br = null;
			    	try {
			    		br = new BufferedReader(new FileReader(fileName));
			    		String line = null;
			    		while ((line = br.readLine()) != null) {
			    			line = line.trim();
			    			if (!line.startsWith("#"))
				    			libPureMethods.add(line);
			    		}
			    	} catch (FileNotFoundException e) {
						System.out.println("WARN: Cannot find pure library "
								+ "method file " + fileName);
			    	} catch (Exception e) {e.printStackTrace(); }
			    	finally {
						try {
							if (br != null)
								br.close();
						} catch (IOException e) {}
			    	}
				}
			}
		}
		String methodSignature = InferenceUtils.getMethodSignature(methodElt);
		return libPureMethods.contains(methodSignature);
	}
	
	
	/**
	 * Check if the {@code elt} is from library
	 * @param elt
	 * @return
	 */
	public boolean isFromLibrary(Element elt) {
		return this.getRootByElement(elt) == null;
	}
	
	
	public boolean isCompilerAddedConstructor(ExecutableElement methodElt) {
		MethodTree node = (MethodTree) getDeclaration(methodElt);
		Element enclosingElement = methodElt.getEnclosingElement();
		if (enclosingElement instanceof TypeElement && TreeUtils.isConstructor(node)) {
			// Check if it is a default constructor
			if (node.getParameters().isEmpty()) {
				List<? extends StatementTree> statements = node.getBody().getStatements();
				if (statements.size() == 1 && statements.get(0).toString().contains("super")) {
					CompilationUnitTree root = getRootByElement(methodElt);
					long constructorLineNum = positions.getStartPosition(root, node);
					constructorLineNum = root.getLineMap().getLineNumber(constructorLineNum);
					ClassTree enclosingClassDecl = (ClassTree) getDeclaration(enclosingElement);
					long classLineNum = positions.getStartPosition(root, enclosingClassDecl);
					classLineNum = root.getLineMap().getLineNumber(classLineNum);
					if (constructorLineNum == classLineNum) {
						// a default constructor, skip it
						return true;
					}
				}
			}
		}
        return false;
	}
	
	/**
	 * Adapt the declared type of a field from the point of view the receiver
	 * @param contextSet The set of annotations of the receiver type
	 * @param declSet The set of annotations of the declared type
	 * @return
	 */
	public Set<AnnotationMirror> adaptFieldSet(Set<AnnotationMirror> contextSet,
			Set<AnnotationMirror> declSet) {
		Set<AnnotationMirror> outSet = AnnotationUtils.createAnnotationSet();
		for (AnnotationMirror declAnno : declSet) {
			for (AnnotationMirror rcvAnno : contextSet) {
				AnnotationMirror anno = adaptField(rcvAnno, declAnno);
				if (anno != null)
					outSet.add(anno);
			}
		}
		return outSet;
	}
	
	/**
	 * Adapt the declared type of a parameter/return from the point of view the
	 * receiver
	 * 
	 * @param contextSet
	 *            The set of annotations of the receiver type
	 * @param declSet
	 *            The set of annotations of the declared type
	 * @return
	 */
	public Set<AnnotationMirror> adaptMethodSet(Set<AnnotationMirror> contextSet,
			Set<AnnotationMirror> declSet) {
		Set<AnnotationMirror> outSet = AnnotationUtils.createAnnotationSet();
		for (AnnotationMirror declAnno : declSet) {
			for (AnnotationMirror rcvAnno : contextSet) {
				AnnotationMirror anno = adaptMethod(rcvAnno, declAnno);
				if (anno != null)
					outSet.add(anno);
			}
		}
		return outSet;
	}
	
    /**
	 * Create the comparator
	 * @return
	 */
	public Comparator<AnnotationMirror> getComparator() {
		if (comparator == null) {
			comparator = new Comparator<AnnotationMirror>() {
				@Override
				public int compare(AnnotationMirror o1, AnnotationMirror o2) {
					int ow1 = getAnnotaionWeight(o1);
					int ow2 = getAnnotaionWeight(o2);
					return ow1 - ow2;
				}
			};
		}
		return comparator;
	}
	
	/**
	 * Resolve conflicted constraints. It is necessary only when 
	 * {@link #needCheckConflict()} is true and the subclass has 
	 * to override it
	 * @param conflictConstraints
	 */
	public void resolveConflictConstraints(List<Constraint> conflictConstraints) {
		if (needCheckConflict())
			throw new RuntimeException("Need to override " +
					"resolveConflictConstraints(List<Constraint> conflictConstraints)!");
	}
	
	/**
	 * Indicate if it needs to force all elements in sup to be the super type
	 * of all elements in sub;
	 * @return
	 */
	public abstract boolean isStrictSubtyping();
				
	/**
	 * Indicate if the error on this constraint can be ignored
	 * @param c
	 * @return
	 */
	public abstract FailureStatus getFailureStatus(Constraint c);
	
	public abstract BaseTypeVisitor<?> getInferenceVisitor(InferenceChecker checker, 
			CompilationUnitTree root);
	
	public abstract Set<AnnotationMirror> getSourceLevelQualifiers();

	public abstract AnnotationMirror adaptField(AnnotationMirror contextAnno, 
			AnnotationMirror declAnno);
	
	public abstract AnnotationMirror adaptMethod(AnnotationMirror contextAnno, 
			AnnotationMirror declAnno);
	
	public abstract int getAnnotaionWeight(AnnotationMirror anno);
	
	public abstract void printResult(Map<String, Reference> solution, 
			PrintWriter out);
	
	public abstract boolean needCheckConflict();

	public boolean isSubtype(TypeElement a1, TypeElement a2) {
	    return (a1.equals(a2)
	            || types.isSubtype(types.erasure(a1.asType()),
	                    types.erasure(a2.asType())));
	}

}
