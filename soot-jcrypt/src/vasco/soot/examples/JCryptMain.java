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
package vasco.soot.examples;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import soot.PackManager;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.SootMethod;
import soot.SourceLocator;
import soot.Transform;
import soot.Unit;
import soot.options.Options;
import vasco.DataFlowSolution;

/**
 * A Soot {@link SceneTransformer} for performing {@link JCryptAnalysis}.
 * 
 * @author Rohan Padhye
 */
public class JCryptMain extends SceneTransformer {
	
	private JCryptAnalysis analysis;
	private static String outputDir = null;
	private static String mrExt = "";
	private static boolean isMapReduce;
	
	@Override
	protected void internalTransform(String arg0, @SuppressWarnings("rawtypes") Map arg1) {
		analysis = new JCryptAnalysis(outputDir);
		analysis.doAnalysis();
		DataFlowSolution<Unit,Map<Object,Byte>> solution = analysis.getMeetOverValidPathsSolution();
		try {
			PrintStream out = new PrintStream(outputDir + File.separator + "analysis-result" + mrExt + ".txt");
			out.println("================================================================");
			for (SootMethod sootMethod : analysis.getMethods()) {
				if (!sootMethod.hasActiveBody()) continue;
				out.println(sootMethod);
				for (Unit unit : sootMethod.getActiveBody().getUnits()) {
					out.println("----------------------------------------------------------------");
					out.println(unit);
					out.println("IN:  " + formatResults(solution.getValueBefore(unit)));
					out.println("OUT: " + formatResults(solution.getValueAfter(unit)));
				}
				out.println("================================================================");
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String formatResults(Map<Object, Byte> map) {
		if (map == null) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for (Entry<Object, Byte> entry : map.entrySet()) {
			Object local = entry.getKey();
			byte typeSet = entry.getValue();
			sb.append("(").append(local).append("=[ ");
			if ((0b1000 & typeSet) != 0) sb.append("MH").append(" ");
			if ((0b100 & typeSet) != 0) sb.append("AH").append(" ");
			if ((0b10 & typeSet) != 0) sb.append("DET").append(" ");
			if ((0b1 & typeSet) != 0) sb.append("OPE").append(" ");
			sb.append("]) ");
		}
		return sb.toString();
	}
	
	public JCryptAnalysis getAnalysis() {
		return analysis;
	}

	public static void main(String args[]) {
		String classPath = ".";		
		String mainClass = null;
		outputDir = SourceLocator.v().getOutputDir();
		
		/* ------------------- OPTIONS ---------------------- */
		try {
			int i=0;
			while(true){
				if (args[i].equals("-cp")) {
					classPath = args[i+1];
					i += 2;
				} else if (args[i].equals("-d")) {
					outputDir = args[i+1];
					i += 2;
				} else if (args[i].equals("-MR")) {
					isMapReduce = true;
					i++;
				} else {
					mainClass = args[i];
					i++;
					break;
				}
			}
			if (i != args.length || mainClass == null)
				throw new Exception();
		} catch (Exception e) {
			System.err.println("Usage: java vasco.soot.examples.JCryptMain [-cp CLASSPATH] [-d OUTPUTDIR] [-MR] MAIN_CLASS");
			System.exit(1);
		}
		
		String[] sootArgs = {
				"-cp", classPath, "-pp", 
				"-w",
				//"-app", 
				//"-src-prec", "java",
				"-allow-phantom-refs",
				"-keep-line-number",
				"-keep-bytecode-offset",
				//"-p", "jb", "use-original-names",
				"-p", "cg", "implicit-entry:false",
				"-p", "cg.spark", "enabled",
				"-p", "cg.spark", "simulate-natives",
				"-p", "cg", "safe-forname",
				"-p", "cg", "safe-newinstance",
				"-main-class", mainClass,
				"-f", "none",
				mainClass,
				"-d", outputDir
		};
		JCryptMain cgt = new JCryptMain();
		PackManager.v().getPack("wjtp").add(new Transform("wjtp.ccp", cgt));
		if (!isMapReduce)
			soot.Main.main(sootArgs);
		else {
			List<SootMethod> entryPoints = new ArrayList<>();
			Options.v().parse(sootArgs);
			SootClass c = Scene.v().forceResolve(mainClass, SootClass.BODIES);
			c.setApplicationClass();
			Scene.v().loadNecessaryClasses();
			for (SootMethod sm : c.getMethods()) {
				if (sm.getModifiers() == soot.Modifier.VOLATILE)
					continue;
				int beginIndex = mainClass.lastIndexOf('.');
				mrExt = "-" + mainClass.substring(beginIndex+1);
				if (sm.getName().equals("map_Sen")) {
					entryPoints.add(sm);
					break;
				}
				if (sm.getName().equals("reduce_Sen")) {
					entryPoints.add(sm);
					break;
				}
				if (sm.getName().equals("getPartition_Sen")) {
					entryPoints.add(sm);
					break;
				}
			}
	
			if (entryPoints.isEmpty()) {
				System.out.println("No Entry Point");
				System.exit(1);
			}
			Scene.v().setEntryPoints(entryPoints);
			PackManager.v().runPacks();
		}
		
		Set<String> conversions = JCryptAnalysis.conversions;
		for (String s : conversions) {
			System.out.println(s);
		}
		System.out.println("Number of conversions: " + conversions.size());
	}
	
}
