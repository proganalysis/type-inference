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
import java.util.Map;
import java.util.Map.Entry;
import soot.SceneTransformer;
import soot.SootMethod;
import soot.Unit;
import vasco.DataFlowSolution;

/**
 * A Soot {@link SceneTransformer} for performing {@link JCryptAnalysis}.
 * 
 * @author Rohan Padhye
 */
public class AETransformer extends SceneTransformer {
	
//	private InferenceTransformer t;
//	
//	public AETransformer(InferenceTransformer t) {
//		super();
//		this.t = t;
//	}

	public AETransformer(String outputDir, String mrExt) {
		super();
		this.outputDir = outputDir;
		this.mrExt = mrExt;
	}

	private String outputDir;
	private String mrExt;
//	private boolean isMapReduce;
	
	@Override
	protected void internalTransform(String arg0, @SuppressWarnings("rawtypes") Map arg1) {
		AEAnalysis analysis = new AEAnalysis();
		analysis.doAnalysis();
		DataFlowSolution<Unit,Map<Object,Byte>> solution = analysis.getMeetOverValidPathsSolution();
		try {
			PrintStream out = new PrintStream(outputDir + File.separator + "analysis-result-" + mrExt + ".txt");
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
	
	public String formatResults(Map<Object, Byte> map) {
		if (map == null) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for (Entry<Object, Byte> entry : map.entrySet()) {
			Object local = entry.getKey();
			byte typeSet = entry.getValue();
			sb.append("(").append(local).append("=[ ");
			if ((0b10000 & typeSet) != 0) sb.append("RND").append(" ");
			if ((0b1000 & typeSet) != 0) sb.append("MH").append(" ");
			if ((0b100 & typeSet) != 0) sb.append("AH").append(" ");
			if ((0b10 & typeSet) != 0) sb.append("DET").append(" ");
			if ((0b1 & typeSet) != 0) sb.append("OPE").append(" ");
			sb.append("]) ");
		}
		return sb.toString();
	}
	
}
