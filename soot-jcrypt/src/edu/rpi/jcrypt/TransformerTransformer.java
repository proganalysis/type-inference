package edu.rpi.jcrypt;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import soot.Body;
import soot.BodyTransformer;
import soot.Scene;
import soot.SootClass;
import soot.tagkit.SignatureTag;

public class TransformerTransformer extends BodyTransformer {

	private static Set<SootClass> visited = new HashSet<>();
	
	@Override
	protected void internalTransform(Body body, String arg1, Map<String, String> arg2) {
		for (SootClass sc : Scene.v().getApplicationClasses()) {
			if (visited.contains(sc)) continue;
			else
				visited.add(sc);
			SignatureTag sigTag = (SignatureTag) sc.getTag("SignatureTag");
			sc.removeTag("SignatureTag");
			String signature = sigTag.getSignature();
			int start = signature.indexOf('<') + 1;
			int end = signature.indexOf('>');
			String[] genericTypes = signature.substring(start, end).split(";");
			genericTypes[2] = "Lorg/apache/hadoop/io/Text";
			StringBuilder builder = new StringBuilder();
			for (String s : genericTypes) {
				builder.append(s + ";");
			}
			SignatureTag tag = new SignatureTag(signature.substring(0, start) + builder.toString() + signature.substring(end));
			sc.addTag(tag);
		}		
	}

}
