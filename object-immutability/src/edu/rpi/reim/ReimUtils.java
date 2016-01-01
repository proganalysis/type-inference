package edu.rpi.reim;

import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import checkers.inference.reim.quals.Readonly;
import checkers.inference.reim.quals.Readonly2;
import checkers.inference.leak.quals.Noleak;
import edu.rpi.AnnotatedValue;
import edu.rpi.AnnotationUtils;
import edu.rpi.Constraint;
import edu.rpi.InferenceTransformer;
import soot.Body;
import soot.SootMethod;
import soot.Unit;
import soot.VoidType;
import soot.jimple.AssignStmt;
import soot.jimple.NewExpr;
import soot.toolkits.graph.Block;
import soot.toolkits.graph.BlockGraph;
import soot.toolkits.graph.BriefBlockGraph;
import soot.Local;
import soot.jimple.*;

public class ReimUtils {
		
	/* All methods with non-Readonly parameters, excluding this */
	private static HashSet<String> nonReadonlyMethods = new HashSet<String>();
	
	private static HashSet<String> additionalInitMethods = new HashSet<String>();
	
	private static HashSet<String> factoryMethods = new HashSet<String>();
	
	private static HashSet<String> externalInitMethods = new HashSet<String>();
	
	// private static HashSet<AnnotatedValue> allocLhss = new HashSet<AnnotatedValue>();
	private static HashSet<String> allocLhss = new HashSet<String>();

	
	/* Public Helper functions */
	
	public static boolean isLocal(AnnotatedValue v) {
		return v.getKind() == AnnotatedValue.Kind.LOCAL;
	}
	
	public static boolean isMethAdapt(AnnotatedValue v) {
		return v.getKind() == AnnotatedValue.Kind.METH_ADAPT;
	}
	
	public static boolean isFieldAdapt(AnnotatedValue v) {
		return v.getKind() == AnnotatedValue.Kind.FIELD_ADAPT;
	}
	
	public static boolean isThis(AnnotatedValue v) {
		return (v.getKind() == AnnotatedValue.Kind.THIS);
	}
	
	public static boolean isR0(AnnotatedValue v) {
		// System.out.println("Trying to figure read through this: "+v.getName());
		return (v.getName().equals("r0") || v.getName().equals("this"));
	}
	
	public static boolean isFakeContext(AnnotatedValue v) {
		return v.toString().contains("fake-"); 
	}
	
	public static boolean isReturn(AnnotatedValue v) {
		return v.getKind() == AnnotatedValue.Kind.RETURN;
	}
	
	public static boolean isAlloc(AnnotatedValue v) {
		return v.getKind() == AnnotatedValue.Kind.ALLOC;
	}
	
	// requires: isAlloc(v)
	private static boolean isNewarrayAlloc(AnnotatedValue v) {
		return v.getName().contains("newarray");
	}
	
	private static boolean isInstanceCall(Constraint c) {
		AnnotatedValue lhs = c.getLeft();
		AnnotatedValue rhs = c.getRight();
		if (!isLocal(lhs) || !isMethAdapt(rhs)) {
    		return false;
    	}
    	AnnotatedValue receiver = ((AnnotatedValue.MethodAdaptValue) rhs).getDeclValue();
    	if (!isThis(receiver))
    		return false;
    	return true;
	}
	
	// requires: c is LOCAL <: METH_ADAPT 
	private static SootMethod getMethod(Constraint c) {
		AnnotatedValue.MethodAdaptValue v = (AnnotatedValue.MethodAdaptValue) c.getRight();
		return v.getDeclValue().getEnclosingMethod();
	}
		
	/*
	 * Soot's Alloc structure: temp1 = new X; temp1.<init>(); temp2 = temp1. 
	 * All operations on temp2.
	 * For arrays though, all operations are on temp1.
	 *
	 */
	public static void findAllocLhs(InferenceTransformer plainTransformer) {
		HashSet<AnnotatedValue> allocs = new HashSet<AnnotatedValue>();
		HashMap<AnnotatedValue,HashSet<Constraint>> lhsToConstraints = new HashMap<AnnotatedValue,HashSet<Constraint>>();		
		Set<Constraint> constraints = plainTransformer.getConstraints();
		for (Constraint c : constraints) {
			AnnotatedValue lhs = c.getLeft();
			if (isAlloc(lhs) || isNewarrayAlloc(lhs)) {
				allocs.add(c.getRight());
				// ALWAYS add c.getRight not only if NewarrayAlloc. Collect all receivers, 
				// i.e., receivers in temp1.<init> and receivers temp2.m and temp2.f = x.
				// if (isNewarrayAlloc(c.getLeft()))
			    assert (isLocal(c.getRight()));					 
			    allocLhss.add(formString(c.getRight()));
			    // System.out.println("========Added to allocLhss I: "+c.getRight());
			}
			// if (isFieldAdapt(lhs)) System.out.println("HERE, POTENTIAL CONSTRAINT "+c);
			if (!(isLocal(lhs) || (isFieldAdapt(lhs) && isR0(((AnnotatedValue.AdaptValue) lhs).getContextValue()))))				
				continue;
			// if (!isLocal(lhs)) System.out.println("HERE, ADDING CONSTRAINT "+c);
			// We need all lhs local <: rhs, which will be used to process all constraints with lhs some temp1
			// as well as r0.f <: rhs local
			HashSet<Constraint> constraintSet = lhsToConstraints.get(lhs);
			if (constraintSet == null) {
				constraintSet = new HashSet<Constraint>();
				lhsToConstraints.put(lhs,constraintSet);
			}
			constraintSet.add(c);
		}
		
		// Process all collected constraints, find the temp1's, then find the temp2's.
		for (AnnotatedValue lhs : lhsToConstraints.keySet()) {
			if (!allocs.contains(lhs)) continue; 
			HashSet<Constraint> set = lhsToConstraints.get(lhs);
			for (Constraint c : set) {
				AnnotatedValue rhs = c.getRight();
				// This collects receivers temp2.
				if (isLocal(rhs)) {					
					AnnotatedValue local = rhs;
					assert (isLocal(local));
					allocLhss.add(formString(local));
					// System.out.println("======Added to allocLhss II: "+formString(local));
				}
				else if (isFieldAdapt(rhs) && isR0(((AnnotatedValue.AdaptValue) rhs).getContextValue())) {
					// System.out.println("NON LOCAL RHS: "+rhs);
					// System.out.println("Examining assignments x = r0.f; x's are potential allocs");
					if (lhsToConstraints.get(rhs) == null) {
						// System.out.println("----- Above rhs is not NEW");
					}
					else {
						for (Constraint cc : lhsToConstraints.get(rhs)) {
							// System.out.println("======Adding to allocLhss III: "+cc.getRight());
							if (rhs.getEnclosingMethod().getName().equals("<init>")) allocLhss.add(formString(cc.getRight()));
						}
					}
				}
			}			
		}		
	}
	
	private static String formString(AnnotatedValue local) {
		return local.getName()+":"+local.getEnclosingMethod()+":"+local.getEnclosingClass();
	}
	public static boolean isAllocVar(AnnotatedValue var) {
		return allocLhss.contains(formString(var));
	}
	
	
	private static void collectNonReadonlyMethods(ReimTransformer reimTransformer) {
		HashMap<SootMethod,HashSet<AnnotatedValue>> parameters = reimTransformer.getParameters();
		for (SootMethod m : parameters.keySet()) {
        	HashSet<AnnotatedValue> parameterSet = parameters.get(m);
        	for (AnnotatedValue v : parameterSet) {
        		// System.out.println("Parameter "+v.getName());
        		if (!v.getAnnotations(reimTransformer).contains(AnnotationUtils.fromClass(Readonly.class))) {
        			nonReadonlyMethods.add(m.toString());
        			// System.out.println(m.toString()+" is not readonly because parameter "+v+" is not readonly.");
        		}
        	}
		}
	}
	
	// This one's called when inference is DONE. Collects final data.
	public static void getAllocSiteData(InferenceTransformer transformer) {
		
		HashMap<AnnotatedValue,HashSet<Constraint>> constraintMap = new HashMap<AnnotatedValue,HashSet<Constraint>>();
		HashSet<Constraint> allocConstraints = new HashSet<Constraint>();
		
		for (Constraint c : transformer.getConstraints()) {
			AnnotatedValue left = c.getLeft();
			AnnotatedValue right = c.getRight();
			if (isAlloc(left) && !((ReimTransformer2) transformer).isDefaultReadonlyType(left.getType()))  {
				allocConstraints.add(c);
			}
			if (!isLocal(right)) {
				continue;
			}
			HashSet<Constraint> constraintSet = constraintMap.get(right);
			if (constraintSet == null) {
				constraintSet = new HashSet<Constraint>();
				constraintMap.put(right,constraintSet);
			}
			constraintSet.add(c);
		}
		int totalAllocs = 0;
		int readonlyAllocs = 0;
		int stringBuilderAllocs = 0;
		int shortLivedAllocs = 0;
		
		for (Constraint alloc : allocConstraints) {
			// System.out.println("Processing alloc: "+alloc);
			AnnotatedValue lhs = alloc.getLeft();
			AnnotatedValue rhs = alloc.getRight();
			AnnotatedValue.MethodAdaptValue meth = null;
			HashSet<Constraint> lhsSet = constraintMap.get(rhs);
			totalAllocs++;
			if (rhs.containsAnno(AnnotationUtils.fromClass(Readonly.class))) {
				readonlyAllocs++;
				// System.out.println("---- IS READONLY ALLOC");
				if (lhs.getName().contains("new java.lang.String") || lhs.getName().contains("Exception")) {
					stringBuilderAllocs++;
					// Don't print anything.
				}
				else if (isNewarrayAlloc(lhs) &&
					// Very dumb way of doing things!
					(lhs.toString().contains("(java.lang.String)[") ||
							lhs.toString().contains("(int)[") ||
							lhs.toString().contains("(char)[") ||
							lhs.toString().contains("(long)[") ||
							lhs.toString().contains("(float)[") || 
							lhs.toString().contains("(byte)[") ||
							lhs.toString().contains("(boolean)["))) {
						stringBuilderAllocs++;
						// arrays of immutables.								
				}		
				
				else {
					System.out.println("Processing alloc: "+alloc);
					System.out.println("---- IS READONLY ALLOC");
					if (lhs.containsAnno(AnnotationUtils.fromClass(Noleak.class))) {
						shortLivedAllocs++;
						System.out.println("---- IS SHORT-LIVED OBJECT");
					}
				}					
			}
			else {
				// System.out.println("---- IS NOT READONLY ALLOC");
			}
		}
		
		System.out.println(readonlyAllocs+" readonly ALLOCs out of "+totalAllocs);
		System.out.println(stringBuilderAllocs+" are StringBuilder/Exception ALLOCs out of "+readonlyAllocs);
		System.out.println(shortLivedAllocs+" are short-lived ALLOCs out of all non-StringBuilder/non-Exception allocs");
	}
	
	public static void collectLeakedThisData(LeakTransformer leakTransformer) {
		
		int total = 0;
		int leaks = 0;
		
		HashSet<AnnotatedValue> thisSet = leakTransformer.getThisSet();
		for (AnnotatedValue a : thisSet) {
			total++;
			if (a.containsAnno(AnnotationUtils.fromClass(Noleak.class))) {				
				// System.out.println(a +" is: ");
				// System.out.println("---- IS NOLEAK!");								
			}
			else {
				leaks++;
				System.out.println(a +" is: ");
				System.out.println("---- IS LEAK");
				nonReadonlyMethods.add(a.getEnclosingMethod().toString());
			}
		}
		System.out.println(leaks+" leaks out of "+total);
		
	}
	
	
	public static boolean isNotEscaping(SootMethod m) {
		return !nonReadonlyMethods.contains(m.toString());
	}
	
	public static boolean isAdditionalInit(SootMethod m) {
		
		return additionalInitMethods.contains(m.toString());
	}
	
	public static boolean isFactoryMethod(SootMethod m) {
		return factoryMethods.contains(m.toString());
	}
	
	public static boolean isExternalInit(SootMethod m) {
		return externalInitMethods.contains(m.toString());
	}
}
