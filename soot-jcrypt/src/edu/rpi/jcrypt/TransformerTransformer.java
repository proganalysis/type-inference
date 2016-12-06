package edu.rpi.jcrypt;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.rpi.AnnotatedValue;
import soot.Body;
import soot.BodyTransformer;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.tagkit.AbstractHost;
import soot.tagkit.SignatureTag;

public class TransformerTransformer extends BodyTransformer {

	private static Set<SootClass> visited = new HashSet<>();
	private JCryptTransformer jt;

	public TransformerTransformer(JCryptTransformer jcryptTransformer) {
		jt = jcryptTransformer;
	}

	@Override
	protected void internalTransform(Body body, String arg1, Map<String, String> arg2) {
		String methodName = body.getMethod().getName();
		if (methodName.equals("map")) {
			modifyClass(true);
			modifyMapMethod(body.getMethod());
		} else if (methodName.equals("reduce")) {
			modifyClass(false);
			modifyReduceMethod(body.getMethod());
		}

	}

	private void modifyMapMethod(SootMethod sm) {
		Set<Integer> index = new HashSet<>();
		if (shouldModify(jt.mapKey)) {
			index.add(0);
		}
		if (shouldModify(jt.mapValue)) {
			index.add(1);
		}
		modifyGenericType(sm, index);
	}

	private void modifyReduceMethod(SootMethod sm) {
		Set<Integer> index = new HashSet<>();
		if (shouldModify(jt.reduceKey)) {
			index.add(0);
		}
		if (shouldModify(jt.reduceValue)) {
			index.add(1);
		}
		modifyGenericType(sm, index);
	}

	private void modifyClass(boolean isMap) {
		for (SootClass sc : Scene.v().getApplicationClasses()) {
			if (visited.contains(sc))
				continue;
			else
				visited.add(sc);
			Set<Integer> mapIndex = new HashSet<>();
			Set<Integer> reduceIndex = new HashSet<>();
			if (shouldModify(jt.mapKey)) {
				mapIndex.add(2);
				reduceIndex.add(0);
			}
			if (shouldModify(jt.mapValue)) {
				mapIndex.add(3);
				reduceIndex.add(1);
			}
			if (shouldModify(jt.reduceKey)) {
				reduceIndex.add(2);
			}
			if (shouldModify(jt.reduceValue)) {
				reduceIndex.add(3);
			}
			if (isMap)
				modifyGenericType(sc, mapIndex);
			else
				modifyGenericType(sc, reduceIndex);
		}
	}

	private boolean shouldModify(AnnotatedValue value) {
		return !value.containsAnno(jt.CLEAR) && !value.getType().toString().equals("org.apache.hadoop.io.Text");
	}

	private void modifyGenericType(AbstractHost sc, Set<Integer> index) {
		SignatureTag sigTag = (SignatureTag) sc.getTag("SignatureTag");
		if (sigTag == null)
			return;
		sc.removeTag("SignatureTag");
		String signature = sigTag.getSignature();
		int start = signature.indexOf('<') + 1;
		int end = signature.indexOf('>');
		String[] genericTypes = signature.substring(start, end).split(";");
		if (genericTypes.length == 1) { // iterator<Text>
			if (shouldModify(jt.mapValue)) {
				signature = signature.substring(0, start) + "Lorg/apache/hadoop/io/Text;" + signature.substring(end);
			}
			start = signature.indexOf('<', start);
			end = signature.indexOf('>', start);
			genericTypes = signature.substring(start, end).split(";");
		}
		for (int i : index) {
			genericTypes[i] = "Lorg/apache/hadoop/io/Text";
		}
		StringBuilder builder = new StringBuilder();
		for (String s : genericTypes) {
			builder.append(s + ";");
		}
		SignatureTag tag = new SignatureTag(
				signature.substring(0, start) + builder.toString() + signature.substring(end));
		sc.addTag(tag);
	}

}
