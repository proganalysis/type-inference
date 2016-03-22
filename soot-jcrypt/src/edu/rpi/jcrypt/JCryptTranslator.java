package edu.rpi.jcrypt;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.rpi.AnnotatedValue;
import edu.rpi.Constraint;
import edu.rpi.InferenceTransformer;
import edu.rpi.AnnotatedValue.MethodAdaptValue;
import soot.SootMethod;

public class JCryptTranslator {
	
	private InferenceTransformer checker;

	public JCryptTranslator(InferenceTransformer c) {
		this.checker = c;
	}
	
    public Set<SootMethod> getPolyMethods() {
    	Annotation[] sourceAnnotations = checker.getSourceLevelQualifiers().toArray(new Annotation[0]);
		Arrays.sort(sourceAnnotations, checker.getComparator());
    	Set<SootMethod> res = new HashSet<>();
    	Map<String, boolean[]> map = new HashMap<>();
    	for (Constraint c : checker.getConstraints()) {
    		AnnotatedValue[] annoValues = new AnnotatedValue[]{c.getLeft(), c.getRight()};
			for (AnnotatedValue annoValue : annoValues) {
				if (!(annoValue instanceof MethodAdaptValue)) continue;
				AnnotatedValue decl = ((MethodAdaptValue) annoValue).getDeclValue();
				if (decl.getIdentifier().startsWith(InferenceTransformer.LIB_PREFIX)) continue;
    			String methodName = decl.getEnclosingMethod().toString();
    			if (methodName.contains("<init>()")) continue;
    			boolean[] status = map.get(methodName);
    			AnnotatedValue callsite = ((MethodAdaptValue) annoValue).getContextValue();
				Annotation anno = callsite.getAnnotations(checker).iterator().next();
    			if (status == null) {
    				status = new boolean[3];
    			}
    			for (int i = 0; i < 3; i++) {
    				if (anno == sourceAnnotations[i]) {
    					status[i] = true;
    					break;
    				}
    			}
    			map.put(methodName, status);
    		}
    	}
//    	for (String key : map.keySet()) {
//    		System.out.println(key);
//    		for (boolean b : map.get(key))
//    			System.out.print(b + " ");
//    		System.out.println();
//    	}
    	return res;
    }

}
