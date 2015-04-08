/**
 * 
 */
package checkers.inference.reim;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;

import checkers.basetype.BaseTypeVisitor;
import checkers.inference.Constraint;
import checkers.inference.ConstraintManager;
import checkers.inference.DescriptorUtil;
import checkers.inference.InferenceChecker;
import checkers.inference.InferenceMain;
import checkers.inference.InferenceUtils;
import checkers.inference.Reference;
import checkers.inference.Reference.ArrayReference;
import checkers.inference.Reference.ExecutableReference;
import checkers.inference.Reference.PrimitiveReference;
import checkers.inference.Reference.VoidReference;
import checkers.inference.reim.quals.Mutable;
import checkers.inference.reim.quals.Polyread;
import checkers.inference.reim.quals.Readonly;
import checkers.quals.TypeQualifiers;
import checkers.types.AnnotatedTypeMirror;
import checkers.types.AnnotatedTypeMirror.AnnotatedDeclaredType;
import checkers.util.AnnotationUtils;
import checkers.util.ElementUtils;
import checkers.util.TreeUtils;
import checkers.util.TypesUtils;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.MethodTree;
import com.sun.tools.javac.code.Symbol.ClassSymbol;
import com.sun.tools.javac.code.Symbol.MethodSymbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.Type.MethodType;

/**
 * @author huangw5
 *
 */
@SupportedOptions( { "warn", "checking", "libPureMethods", "libMutateStatics", "jaif", "insertAnnos", "debug" } ) 
//@TypeQualifiers({I.class, P.class, M.class})
@TypeQualifiers({Readonly.class, Polyread.class, Mutable.class})
public class ReimChecker extends InferenceChecker {

	public AnnotationMirror READONLY, POLYREAD, MUTABLE;
	
	private Set<AnnotationMirror> sourceLevelQuals; 
	
	/** For default pure method */
	private List<Pattern> defaultPurePatterns = null;
	
	private Map<String, AnnotationMirror> libMutateStatics = null;
	
	protected AnnotationUtils annoFactory;

	private Comparator<AnnotationMirror> comparator;

	@Override
	public void initChecker(ProcessingEnvironment processingEnv) {
		super.initChecker(processingEnv);
		annoFactory = AnnotationUtils.getInstance(env);		
		READONLY = annoFactory.fromClass(Readonly.class);
		POLYREAD = annoFactory.fromClass(Polyread.class);
		MUTABLE = annoFactory.fromClass(Mutable.class);
		
		sourceLevelQuals = AnnotationUtils.createAnnotationSet();
		sourceLevelQuals.add(READONLY);
		sourceLevelQuals.add(POLYREAD);
		sourceLevelQuals.add(MUTABLE);
		
		defaultPurePatterns = new ArrayList<Pattern>(5);
        defaultPurePatterns.add(Pattern.compile(".*\\.equals\\(java\\.lang\\.Object\\)$"));
        defaultPurePatterns.add(Pattern.compile(".*\\.hashCode\\(\\)$"));
        defaultPurePatterns.add(Pattern.compile(".*\\.toString\\(\\)$"));
        defaultPurePatterns.add(Pattern.compile(".*\\.compareTo\\(.*\\)$"));
	}
	
	
	public boolean isDefaultReadonlyType(AnnotatedTypeMirror t) {
		TypeElement elt = null;
		if (t.getKind() == TypeKind.DECLARED) {
			AnnotatedDeclaredType dt = (AnnotatedDeclaredType) t;
	        elt = (TypeElement)dt.getUnderlyingType().asElement();
		}
		return t.getKind().isPrimitive()
				|| TypesUtils.isBoxedPrimitive(t.getUnderlyingType())
				|| (elt != null && elt.getQualifiedName().contentEquals(
						"java.lang.String"));
	}
	
	
	
	/**
	 * Get the mutateStatic of library methodElt. If no input file is given, 
	 * then assume it doesn't mutate statics
	 * @param methodElt
	 * @return
	 */
	public AnnotationMirror getLibraryMutateStatic(ExecutableElement methodElt) {
		if (libMutateStatics == null) {
			libMutateStatics = new HashMap<String, AnnotationMirror>();
			String property = getProcessingEnvironment().getOptions().get(
					"libMutateStatics");
			if (property == null) {
				// No library pure methods are given
				System.err.println("WARN: all library methods are assumed not "
						+ "mutating statics.");
				System.err.println("\tLibrary mutateStatics can be given by "
						+ "-AlibMutateStatics=file1:file2 separated by colons");
			} else {
				for (String fileName : property.split(":")) {
					fileName = fileName.trim();
			    	BufferedReader br = null;
			    	try {
			    		br = new BufferedReader(new FileReader(fileName));
			    		String line = null;
			    		while ((line = br.readLine()) != null) {
			    			line = line.trim();
			    			if (!line.startsWith("#")) {
			    				String[] ss = line.split(":");
			    				if (ss.length == 1) {
				    				libMutateStatics.put(ss[0].trim(), 
				    						MUTABLE);
			    				} else if (ss.length == 2) {
			    					String annoName = ss[1].trim();
			    					if (annoName.startsWith("@"))
			    						annoName = annoName.substring(1);
			    					AnnotationMirror anno = annoFactory.fromName(annoName);
			    					if (anno == null) 
										System.out.println("WARN: invalid annotations in libMutateStatics: "
														+ line
														+ " in "
														+ fileName);
			    					else
										libMutateStatics.put(ss[0].trim(), anno);
			    				} else {
			    					System.out.println("WARN: invalid libMutateStatics: "
			    							+ line + " in " + fileName);
			    					continue;
			    				}
			    			}
			    		}
			    	} catch (FileNotFoundException e) {
						System.out.println("WARN: Cannot find mutate statics "
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
		
		AnnotationMirror anno = libMutateStatics.get(
				InferenceUtils.getMethodSignature(methodElt));
		
		return anno != null ? anno : READONLY;
	}
	
	/**
	 * Check if the method is default pure. E.g. We assume 
	 * java.lang.Object.toString() is default pure. 
	 * @param methodElt
	 * @return
	 */
	public boolean isDefaultPureMethod(ExecutableElement methodElt) {
		String key = InferenceUtils.getMethodSignature(methodElt);
		for (Pattern p : defaultPurePatterns) {
			if (p.matcher(key).matches()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Set<AnnotationMirror> getSourceLevelQualifiers() {
		return sourceLevelQuals;
	}
	
	
	@Override
	public AnnotationMirror adaptField(AnnotationMirror contextAnno, 
			AnnotationMirror declAnno) {
		if (declAnno.toString().equals(READONLY.toString()))
			return READONLY;
		else if (declAnno.toString().equals(MUTABLE.toString()))
			return contextAnno;
		else if (declAnno.toString().equals(POLYREAD.toString()))
			return contextAnno;
		else
			return null;
	}
	
	
	@Override
	public AnnotationMirror adaptMethod(AnnotationMirror contextAnno, 
			AnnotationMirror declAnno) {
		if (declAnno.toString().equals(READONLY.toString()))
			return READONLY;
		else if (declAnno.toString().equals(MUTABLE.toString()))
			return MUTABLE;
		else if (declAnno.toString().equals(POLYREAD.toString()))
			return contextAnno;
		else
			return null;
	}

	@Override
	public int getAnnotaionWeight(AnnotationMirror anno) {
		if (anno.toString().equals(READONLY.toString()))
			return 1;
		else if (anno.toString().equals(POLYREAD.toString()))
			return 2;
		else if (anno.toString().equals(MUTABLE.toString()))
			return 3;
		else 
			return Integer.MAX_VALUE;
	}

	@Override
	public BaseTypeVisitor<?> getInferenceVisitor(InferenceChecker checker, 
			CompilationUnitTree root) {
		return new ReimInferenceVisitor((ReimChecker) checker, root);
	}

	@Override
	public boolean isStrictSubtyping() {
		return false;
	}

	@Override
	public FailureStatus getFailureStatus(Constraint c) {
		Reference left = c.getLeft();
		Reference right = c.getRight();
		AnnotatedTypeMirror leftType = left.getType();
		AnnotatedTypeMirror rightType = right.getType();
		if (leftType != null && isDefaultReadonlyType(leftType)
				|| rightType != null && isDefaultReadonlyType(rightType))
			return FailureStatus.IGNORE;
		Element leftElt = left.getElement();
		Element rightElt = right.getElement();
		if (leftElt != null && isFromLibrary(leftElt) 
				|| rightElt != null && isFromLibrary(rightElt))
			return FailureStatus.WARN;
		return FailureStatus.ERROR;
	}


	@Override
	public void printResult(Map<String, Reference> solution, PrintWriter out) {
		Collection<Reference> values = solution.values();
		List<Reference> lst = new ArrayList<Reference>(values.size());
		for (Reference ref : values) {
			if (ref.getElement() != null 
//					&& !ref.getType().getKind().isPrimitive()
					&& !ref.getFileName().startsWith("zLIB")
					&& ref.getLineNum() > 0
							) {
				lst.add(ref);
			}
		}
		Collections.sort(lst, new Comparator<Reference>(){
			@Override
			public int compare(Reference o1, Reference o2) {
				int res = o1.getFileName().compareTo(o2.getFileName());
				if (res != 0)
					return res;
				else 
					res = (int) (o1.getLineNum() - o2.getLineNum());
				if (res != 0)
					return res;
				else if (o1.getElement() != null && o2.getElement() != null) {
					return o1.getElement().toString().compareTo(o2.getElement().toString());
				}
				return 0;
			}});
		
		int readonlyNum = 0, polyNum = 0, mutableNum = 0;
		int totalElementNum = 0, methodNum = 0;
		
		List<ExecutableReference> executableRefs = new LinkedList<ExecutableReference>();
		List<String> pureMethods = new LinkedList<String>();
		List<String> specialPureMethods = new LinkedList<String>();
		
		for (Reference ref : lst) {
			if (ref instanceof ExecutableReference) {
				if(isCompilerAddedConstructor((ExecutableElement) ref.getElement()))
					continue;
				
				ExecutableReference eRef = (ExecutableReference) ref;
				executableRefs.add(eRef);
				
				// output mutateStatics
				Set<AnnotationMirror> annos = ref.getAnnotations();
				out.println(ref.getFileName() + "\t" + ref.getLineNum() + "\t"
						+ ref.getElement() + "-static\t\t" + InferenceUtils
						.formatAnnotationString(annos));
				
				// output type
				out.println(ref.getFileName() + "\t" + ref.getLineNum() + "\t"
						+ ref.getElement() + "\t\t" + ref.getAnnotatedType());
				
				// statistics
				methodNum++;
				// receiver
				if (!ElementUtils.isStatic(eRef.getElement())) {
					totalElementNum++;
					AnnotatedTypeMirror rcvType = eRef.getReceiverRef().getAnnotatedType();
					if (rcvType.hasAnnotation(READONLY))
						readonlyNum++;
					else if (rcvType.hasAnnotation(POLYREAD))
						polyNum++;
					else if (rcvType.hasAnnotation(MUTABLE))
						mutableNum++;
					else 
						System.err.println("WARN: Unknown type! " + rcvType);
				}
				// return
				AnnotatedTypeMirror returnType = eRef.getReturnRef().getAnnotatedType();
				if (returnType != null && !returnType.getKind().isPrimitive()
						&& returnType.getKind() != TypeKind.VOID) {
					totalElementNum++;
					if (returnType.hasAnnotation(READONLY))
						readonlyNum++;
					else if (returnType.hasAnnotation(POLYREAD))
						polyNum++;
					else if (returnType.hasAnnotation(MUTABLE))
						mutableNum++;
					else 
						System.err.println("WARN: Unknown type! " + returnType);
				}
				// check the purity
				String methodSig = InferenceUtils.getMethodSignature(ref.getElement());
				MethodPurity purity = getPurity(eRef).getPurity();
				if (purity != MethodPurity.IMPURE)
					pureMethods.add(methodSig);
				if (purity == MethodPurity.SPECIAL_PURE)
					specialPureMethods.add(methodSig);
				
			} else {
				// output type
				out.println(ref.getFileName() + "\t" + ref.getLineNum() + "\t"
						+ ref.getElement() + "\t\t" + ref.getAnnotatedType());
				
				// statistics
				AnnotatedTypeMirror type = ref.getAnnotatedType();
				if (!type.getKind().isPrimitive()
						&& type.getKind() != TypeKind.VOID) {
					totalElementNum++;
					if (type.hasAnnotation(READONLY))
						readonlyNum++;
					else if (type.hasAnnotation(POLYREAD))
						polyNum++;
					else if (type.hasAnnotation(MUTABLE) && ref.getElement().getKind() != ElementKind.FIELD)
						mutableNum++;
					else if (type.hasAnnotation(MUTABLE) && ref.getElement().getKind() == ElementKind.FIELD)
						polyNum++;
					else 
						System.err.println("WARN: Unknown type! " + type);
				}
			}
		}	
		int pureNum = pureMethods.size();
		String s = "INFO: There are " + readonlyNum + " ("
				+ (((float) readonlyNum / totalElementNum) * 100)
				+ "%) readonly, " + polyNum + " ("
				+ (((float) polyNum / totalElementNum) * 100)
				+ "%) polyread and " + mutableNum + " ("
				+ (((float) mutableNum / totalElementNum) * 100) + "%) mutable"
				+ " references out of " + totalElementNum + " references"
				+ "\nINFO:"
				+ " Total methods: " + methodNum
				+ " pure methods: " + pureNum + "(" + pureNum * 100 / (float) methodNum + "%)";
		
		System.out.println(s);
		out.println(s);
		
        // Output for latex
//        System.out.println(String.format(/*"<benchmark> & <lineNum> */"& %6d & %6d (%2.0f\\%%) & %6d & %6d (%2.0f\\%%) & %6d (%2.0f\\%%) &  %6d (%2.0f\\%%)  & <inferTime> & <checkingTime> & <total>     \\\\\\hline\n", 
//        		methodNum, pureMethods.size(), pureMethods.size() * 100 / (float) methodNum, totalElementNum, readonlyNum, ((float) readonlyNum / totalElementNum) * 100, 
//        		polyNum, ((float) polyNum / totalElementNum) * 100, mutableNum, ((float) mutableNum / totalElementNum) * 100));
		
		
		// output pure methods 
        String fileName = InferenceMain.outputDir + File.separator + "pure-methods.csv";
        String property2 = getProcessingEnvironment().getOptions().get("pureMethod");
        if (property2 != null)
        	fileName = property2;
        Collections.sort(pureMethods);
		System.out.println("INFO: Writing pure methods to " + fileName + "...");
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(fileName);
            for (String key : pureMethods) {
                pw.println(key);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (pw != null)
                pw.close();
        }
		
        System.out.println("INFO: Impure special methods: " + specialPureMethods.size());
        Collections.sort(specialPureMethods);
        for (String ms : specialPureMethods) {
        	System.out.println("\t" + ms);
        }
        
        // output mutate statics
        fileName = InferenceMain.outputDir + File.separator + "mutatestatics.csv";
        String property3 = getProcessingEnvironment().getOptions().get("mutateStatics");
        if (property3 != null)
        	fileName = property3;
		System.out.println("INFO: Writing mutatestatics methods to " + fileName + "...");
        pw = null;
        try {
            pw = new PrintWriter(fileName);
            for (Reference ref : executableRefs) {
        		AnnotationMirror[] annos = ref.getAnnotations()
        				.toArray(new AnnotationMirror[0]);
        		Arrays.sort(annos, getComparator());
        		if (!annos[0].equals(READONLY))
					pw.println(InferenceUtils.getElementSignature(ref.getElement()) 
							+ ":" + annos[0].toString());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (pw != null)
                pw.close();
        }
		// Print the JAIF result
        if (getProcessingEnvironment().getOptions().containsKey(
				"jaif")) {
			pw = null;
			try {
				pw = new PrintWriter(InferenceMain.outputDir + File.separator + "result.jaif");
				printJAIFResult(solution, pw);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			if (pw != null)
				pw.close();
        }
		
        
		if (getProcessingEnvironment().getOptions().containsKey(
				"insertAnnos")) {
			insertInferredAnnotations(lst);
		}
	}
	
	private void printJAIFResult(Map<String, Reference> solution, PrintWriter out) {
		TypeElement[] visitedClasses = InferenceMain.getInstance().getConstraintManager().getVisitedClasses();
		// First sort the visitedClasses 
    	Arrays.sort(visitedClasses, new Comparator<TypeElement>() {
			@Override
			public int compare(TypeElement o1, TypeElement o2) {
				Element elt1 = o1.getEnclosingElement();
				while (elt1.getKind() != ElementKind.PACKAGE) {
					elt1 = elt1.getEnclosingElement();
				}
				PackageElement packageElt1 = (PackageElement) elt1;
				String packageName1 = packageElt1.getQualifiedName().toString();
				packageName1 = packageElt1.isUnnamed() ? "" : packageName1;
				
				Element elt2 = o2.getEnclosingElement();
				while (elt2.getKind() != ElementKind.PACKAGE) {
					elt2 = elt2.getEnclosingElement();
				}
				PackageElement packageElt2 = (PackageElement) elt2;
				String packageName2 = packageElt2.getQualifiedName().toString();
				packageName2 = packageElt2.isUnnamed() ? "" : packageName2;
				
				int res = packageName1.compareTo(packageName2);
				if (res != 0)
					return res;
				
				String className1 = ((ClassSymbol) o1).flatName().toString();
				if (!packageElt1.isUnnamed()) {
					className1 = className1.substring(packageName1.length() + 1);
				}
				
				String className2 = ((ClassSymbol) o2).flatName().toString();
				if (!packageElt2.isUnnamed()) {
					className2 = className2.substring(packageName2.length() + 1);
				}
				res = className1.compareTo(className2);
				return res;
			}
		});
    	
    	// Now write the JAIF result
		for (TypeElement clazz : visitedClasses) {
			// Output package name
			Element pElt = clazz;
			while (pElt.getKind() != ElementKind.PACKAGE) {
				pElt = pElt.getEnclosingElement();
			}
			PackageElement packageElt = (PackageElement) pElt;
			String packageName = packageElt.getQualifiedName().toString();
			out.println("package " + (packageElt.isUnnamed() ? "" : packageName) + ":");
			
			// Output class name
			String className = ((ClassSymbol) clazz).flatName().toString();
			if (!packageElt.isUnnamed()) {
				className = className.substring(packageName.length() + 1);
			}
			out.println("class " + className + ":");
			
			// Output members
			List<? extends Element> enclosedElements = clazz.getEnclosedElements();
			// first output fields
			for (Element elt : enclosedElements) {
				if (elt.getKind() == ElementKind.FIELD) {
					out.println("\n    field " + elt.toString() + ":");
					Reference ref = solution.get(
							InferenceUtils.getElementSignature(elt));
					if (ref != null && !(ref instanceof PrimitiveReference)) {
						out.print("        type: ");
						out.println(mapToJavarifier(ref));
						int i = 0;
						String prefix = "";
						while (ref instanceof ArrayReference) {
							ref = ((ArrayReference) ref).getComponentRef();
							out.print(prefix + "            inner-type " + i + ":");
							if (ref == null 
									|| ref instanceof PrimitiveReference) {
								out.println();
								break;
							}
							out.println(" " + mapToJavarifier(ref));
							i++;
							prefix += "    ";
						}
					}
				}
			}
			// constructors and methods
			for (Element elt : enclosedElements) {
				if (elt.getKind() == ElementKind.CONSTRUCTOR
						|| elt.getKind() == ElementKind.METHOD) {
					ExecutableElement methodElt = (ExecutableElement) elt;
					ExecutableReference methodRef = (ExecutableReference) solution
							.get(InferenceUtils.getElementSignature(methodElt));
					if (methodRef == null) {
						// TODO: It may be null. This happens on the enum types
						continue;
					}
                    Type t = ((MethodSymbol) methodElt).type;
                    if (t instanceof MethodType) {
                        MethodType mt = (MethodType) ((MethodSymbol) methodElt).type;
                        String javadocSig = "(" + mt.argtypes(false) + ")";
                        String javadocReturnType = mt.restype.toString();
                        String jvmSig = DescriptorUtil.convert(javadocSig, javadocReturnType);
                        out.println("\n    method "
                                + ((MethodSymbol) methodElt).name + jvmSig
                                + ":");
                    }
                    else {
                        out.println("\n    method " + methodElt + ":");
                    }
					out.print("        return:");
					Reference returnRef = methodRef.getReturnRef();
					if ((returnRef instanceof PrimitiveReference)
							|| (returnRef instanceof VoidReference))
						out.println();
					else {
						out.println(" " + mapToJavarifier(returnRef));
					}
					if (!ElementUtils.isStatic(methodElt)) {
						out.print("        receiver: ");
						Reference rcvRef = methodRef.getReceiverRef();
						out.println(mapToJavarifier(rcvRef));
					}

					List<? extends VariableElement> parameters = methodElt
							.getParameters();
					int counter = 0;
					for (VariableElement e : parameters) {
						try {
							out.println("        parameter " + /*e.toString()*/ "#" + (counter++) + ":");
							Reference paramRef = solution.get(
									InferenceUtils.getElementSignature(e));
							if (paramRef != null && !(paramRef instanceof PrimitiveReference)) {
								out.print("            type: ");
								out.println(mapToJavarifier(paramRef));
								int i = 0;
								String prefix = "";
								while (paramRef instanceof ArrayReference) {
									paramRef = ((ArrayReference) paramRef).getComponentRef();
									out.print(prefix + "                inner-type " + i + ":");
									if (paramRef == null 
											|| paramRef instanceof PrimitiveReference) {
										out.println();
										break;
									}
									out.println(" " + mapToJavarifier(paramRef));
									i++;
									prefix += "    ";
								}
							}
						} catch (Exception ex) {
						}
					}
				}
			}
			out.println();
		}
    	
	}
	
    private String mapToJavarifier(Reference ref) {
    	String res = "";
    	Set<AnnotationMirror> annotations = ref.getAnnotations();
    	if (annotations.isEmpty()) 
    		return "<unannotated>";
    	AnnotationMirror[] array = annotations.toArray(new AnnotationMirror[0]);
    	Arrays.sort(array, getComparator());
    	AnnotationMirror anno = array[0];
    	res = anno.toString();
//    	if (anno.toString().equals("@checkers.inference.reim.quals.Readonly"))
//    		res = "@checkers.javari.quals.ReadOnly";
//    	else if (anno.toString().equals("@checkers.inference.reim.quals.Polyread"))
//    		res = "@checkers.javari.quals.PolyRead";
//    	else if (anno.toString().equals("@checkers.inference.reim.quals.Mutable"))
//    		res = "@checkers.javari.quals.Mutable";
    	return res;
    }
	
	
	public static enum MethodPurity {
		PURE,
		IMPURE,
		SPECIAL_PURE,
		UNKNOWN;
	}
	
	public static class MethodPurityWithReason {
		private MethodPurity purity;
		private String reason;
		public MethodPurityWithReason(MethodPurity purity, String reason) {
			super();
			this.purity = purity;
			this.reason = reason;
		}
		public MethodPurity getPurity() {
			return purity;
		}
		public String getReason() {
			return reason;
		}
		
	}
	
	public MethodPurityWithReason getPurity(ExecutableReference eRef) {
		MethodPurity ret = MethodPurity.PURE; 
		StringBuffer reason = new StringBuffer();
		Element methodElt = eRef.getElement();
		
		// check receiver if not static or not constructor
		MethodTree mTree = (MethodTree) getDeclaration(methodElt);
		if (!ElementUtils.isStatic(methodElt) && !TreeUtils.isConstructor(mTree)) {
			AnnotatedTypeMirror rcvType = eRef.getReceiverRef().getAnnotatedType();
			if (InferenceUtils.getMaxType(rcvType, getComparator())
					.hasAnnotation(MUTABLE)) {
				ret = MethodPurity.IMPURE;
				reason.append("receiver is mutated; ");
			}
		}
//		if (ret == MethodPurity.PURE) {
			// check parameters
			for (Reference paramRef : eRef.getParamRefs()) {
				AnnotatedTypeMirror paramType = paramRef.getAnnotatedType();
				if (InferenceUtils.getMaxType(paramType, getComparator())
						.hasAnnotation(MUTABLE)) {
					ret = MethodPurity.IMPURE;
					reason.append("parameter \"" + paramRef.getElement() + "\" is mutated; ");
//					break;
				}
			}
//		}
//		if (ret == MethodPurity.PURE) {
			// Check mutate statics
			AnnotationMirror[] annos = eRef.getAnnotations()
					.toArray(new AnnotationMirror[0]);
			Arrays.sort(annos, getComparator());
			if (annos[0].equals(MUTABLE)) {
				ret = MethodPurity.IMPURE;
				reason.append("it mutates static fields; ");
			}
//		}
		if (ret == MethodPurity.IMPURE 
				&& isDefaultPureMethod((ExecutableElement) methodElt)) {
			return new MethodPurityWithReason(MethodPurity.SPECIAL_PURE, reason.toString());
		} else
			return new MethodPurityWithReason(ret, reason.toString());
	}


	@Override
	public boolean needCheckConflict() {
		return false;
	}
	
}
