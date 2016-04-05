/**
 * Copyright (C) 2013 Rohan Padhye
 * 
 * This library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 2.1 of the 
 * License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package vasco.soot;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import soot.PackManager;
import soot.SceneTransformer;
import soot.SootMethod;
import soot.Transform;
import soot.Unit;
import vasco.DataFlowSolution;

/**
 * A Soot {@link SceneTransformer} for performing {@link JCryptAnalysis}.
 * 
 * @author Rohan Padhye
 */
public class JCryptTest extends SceneTransformer {
	
	private JCryptAnalysis analysis;

	
	@Override
	protected void internalTransform(String arg0, @SuppressWarnings("rawtypes") Map arg1) {
		analysis = new JCryptAnalysis();
		analysis.doAnalysis();
		DataFlowSolution<Unit,Map<Object,Set<String>>> solution = analysis.getMeetOverValidPathsSolution();
		System.out.println("----------------------------------------------------------------");
		for (SootMethod sootMethod : analysis.getMethods()) {
			System.out.println(sootMethod);
			for (Unit unit : sootMethod.getActiveBody().getUnits()) {
				System.out.println("----------------------------------------------------------------");
				System.out.println(unit);
				System.out.println("IN:  " + formatResults(solution.getValueBefore(unit)));
				System.out.println("OUT: " + formatResults(solution.getValueAfter(unit)));
			}
			System.out.println("----------------------------------------------------------------");
		}		
	}
	
	public static String formatResults(Map<Object, Set<String>> map) {
		if (map == null) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for (Entry<Object, Set<String>> entry : map.entrySet()) {
			Object local = entry.getKey();
			Set<String> typeSet = entry.getValue();
			if (typeSet != null) {
				sb.append("(").append(local).append("=[");
				for (String type : typeSet) {
					sb.append(type).append(" ");
				}
				sb.append(") ");
			}
		}
		return sb.toString();
	}
	
	public JCryptAnalysis getAnalysis() {
		return analysis;
	}

	public static void main(String args[]) {
		String classPath = ".";		
		String mainClass = null;
		
		/* ------------------- OPTIONS ---------------------- */
		try {
			int i=0;
			while(true){
				if (args[i].equals("-cp")) {
					classPath = args[i+1];
					i += 2;
				} else {
					mainClass = args[i];
					i++;
					break;
				}
			}
			if (i != args.length || mainClass == null)
				throw new Exception();
		} catch (Exception e) {
			System.err.println("Usage: java vasco.soot.examples.JCryptTest [-cp CLASSPATH] MAIN_CLASS");
			System.exit(1);
		}
		
		String[] sootArgs = {
				"-cp", classPath, "-pp", 
				"-w", "-app", 
				"-keep-line-number",
				"-keep-bytecode-offset",
				"-p", "jb", "use-original-names",
				"-p", "cg", "implicit-entry:false",
				"-p", "cg.spark", "enabled",
				"-p", "cg.spark", "simulate-natives",
				"-p", "cg", "safe-forname",
				"-p", "cg", "safe-newinstance",
				"-main-class", mainClass,
				"-f", "none", mainClass 
		};
		JCryptTest cgt = new JCryptTest();
		PackManager.v().getPack("wjtp").add(new Transform("wjtp.ccp", cgt));
		soot.Main.main(sootArgs);
	}
	
}
