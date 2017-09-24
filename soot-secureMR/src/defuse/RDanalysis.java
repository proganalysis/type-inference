package defuse;

import java.util.HashSet;

import soot.PackManager;
import soot.Transform;

public class RDanalysis {
	
	

	public static void main(String[] args) {

		//args = new String[] {"-cp", "adjList.jar", "AdjList$MapClass", "-allow-phantom-refs",
		//		"-p", "jb", "use-original-names:true", "-f", "jimple"};
		args = new String[] {"-cp", "adjList.jar", "AdjList$Reduce", "-allow-phantom-refs",
				"-p", "jb", "use-original-names:true", "-f", "jimple"};
		//String[] argsSources = new String[] {"outEdge", "inEdge"};
		String[] argsSources = new String[] {"vertex", "vertex"};
		HashSet<String> sources = new HashSet<>();
		for (String arg : argsSources) sources.add(arg);
		PackManager.v().getPack("jtp").add(new Transform("jtp.rd", new RDTransformer(sources)));
		soot.Main.main(args);
	}

}
