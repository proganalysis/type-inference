package edu.rpi.jcrypt;

import soot.Local;
import soot.SootField;
import soot.SootMethod;
import soot.Value;
import soot.jimple.FieldRef;

public class TransUtils {

	public static String getIdenfication(Value v, SootMethod sm) {
		String id = "";
		if (v instanceof FieldRef) {
			SootField sf = ((FieldRef) v).getField();
			id = sf.getSignature();
		} else if (v instanceof Local) {
			id = sm.getSignature() + "@" + v.toString();
		}
		return id;
	}

}
