import java.io.*;

public class Parser {

    Graph g;

    static public long FROM_CLASS_EDGE = 5000000;
    /* this = CLASS are equality constraints with identifier id;
       We create this <: CLASS edge with identifier id 
       and CLASS <: this inverse edge with identifier FROM_CLASS_EDGE+id
    */

    static public long ARRAY_ACCESS = 10000000;
    /* special array access field */

    static public long FAKE_CALL = 7000000;
    /* calls to access$xyz methods */

    Parser(Graph g) { this.g = g; }

    void loadGraph(String file) {

	String fileName = "/projects/proganalysis/TaintAnalysis/soot-inference/android-tests/GooglePlay/checked/"+file+"/sflow-constraints.log";

        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line = null;

            while ((line = reader.readLine()) != null) {

		// System.out.println(line);
                // System.out.println();                                                                               
		parseLine(line);
		// System.out.println();
            }
        } catch (IOException e) { System.out.println("Can't read file "+fileName); }

    }

    void parseLine(String line) {
        // parses a constraint lhs <: rhs or lhs == rhs                                                                                                         

	boolean equ = false;
	int i = line.indexOf("<:");
        if (i < 0) { i = line.indexOf("=="); equ = true; }
        long edgeId = Long.parseLong(getSubstring(line,'-',':'));
        Side lhs = parseSide(line.substring(0,i));
        Side rhs = parseSide(line.substring(i+2));
        // System.out.println("lhs: "+lhs.call+","+lhs.field+","+lhs.var);                                                                                         
        // System.out.println("rhs: "+rhs.call+","+rhs.field+","+rhs.var);                                                                                         
        long call=0, field=0;
        if (lhs.call != 0) call = lhs.call;
        else if (rhs.call != 0) call = -rhs.call;
        else if (lhs.field != 0) field = lhs.field;
        else if (rhs.field != 0) field = -rhs.field;

        int c = line.indexOf("caused by");
        if (c > -1)
            g.registerCausedByEdge(edgeId,lhs.var,rhs.var,call,field,line.substring(line.indexOf('[',c)));
        else
            g.registerEdge(edgeId,lhs.var,rhs.var,call,field,"");

        if (equ == true) g.registerEdge(FROM_CLASS_EDGE+edgeId,rhs.var,lhs.var,call,field,"");
    }

    Side parseSide(String side) {
        Side result = new Side();
        if (side.indexOf("=m=>") > -1) parseCall(side,result);
        else if (side.indexOf("=f=>") > -1) parseField(side,result);
        else parseVar(side,result);

        return result;
    }

    void parseCall(String side, Side result) {
        // ((callsite-id))callsite- .... =m=> varinfo                                                                                                           
        int i = side.indexOf("((")+2; int j = side.indexOf(")callsite");
        result.call = Integer.parseInt(side.substring(i,j));
        result.field = 0;
        result.var = g.registerNode(side.substring(side.indexOf("=m=>")+5),false);
    }

    void parseField(String side, Side result) {

        int i = side.indexOf("=f=> (")+6; int j = side.indexOf(')',i);
        result.call = 0;
        // System.out.println(side+" and i: "+i + " and j: "+j);                 
                                                                                  
        result.field = Integer.parseInt(side.substring(i,j));
        result.var = g.registerNode(side.substring(side.indexOf('(')+1,side.indexOf(" =f=>")),false);
        long field = g.registerNode(side.substring(i-1),true); // registering the field itself 
                                                                      
        if ((g.getNode(field)).kind.equals("array access")) result.field = ARRAY_ACCESS;
        // if the field is array access, we send default 1000000...                                                                                      
          
    }
    void parseVar(String side, Side result) {
        int i = side.indexOf("(");
        result.call = 0; result.field = 0;
        result.var = g.registerNode(side.substring(i),false);
    }

    String getSubstring(String str, char from, char to) {
        int i, j;
        if (from == '>') i = str.lastIndexOf(from)+1;
        else i = str.indexOf(from)+1;
        if (to == '>') j = str.lastIndexOf(to);
	else j = str.indexOf(to,i);
        return str.substring(i,j);
    }


    void loadReachableMethods(String file) {

	String fileName = "/projects/proganalysis/TaintAnalysis/soot-inference/android-tests/GooglePlay/checked/"+file+"/"+file+".apk.cg.txt";

        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line = null;

            while ((line = reader.readLine()) != null) {

                // System.out.println(line);
                                                                                                                             
                // System.out.println(); 
                                                                                                                                
 		String enclClass = line.substring(1,line.indexOf(';')).replace('/','.');
		int pos = line.indexOf(';')+2;
		// System.out.println("Enclosing class: "+enclClass);
		// System.out.println("pos is: "+pos+" and end is "+line.indexOf('('));
		// System.out.println();
		String methodName = line.substring(pos,line.indexOf('(',pos));
		pos = line.indexOf('(',pos)+1;
		String parameters = "";
		while (line.charAt(pos) != ')') {
		    if (parameters.length() == 0) parameters = parseParameter(line,pos);
		    else parameters = parameters + "," + parseParameter(line,pos);
		    pos = getPosition(line,pos);
		}
		

		String ret = parseParameter(line,pos+1);

		// System.out.println(enclClass+": "+ret+" "+methodName+"("+parameters+")");
		// System.out.println();
                g.registerReachableMethod(enclClass+": "+ret+" "+methodName+"("+parameters+")");   
            }

        } catch (IOException e) { System.out.println("Can't read file "+fileName); }


    }

    int getPosition(String line, int pos) {
	if (line.charAt(pos) == 'L')
	    return line.indexOf(';',pos)+1;
	else if (line.charAt(pos) == '[')
	    return getPosition(line,pos+1);
	else
	    return pos+1;
    }

    String parseParameter(String line, int pos) {
	if (line.charAt(pos) == 'L')
	    return line.substring(pos+1,line.indexOf(';',pos)).replace('/','.');
	else if (line.charAt(pos) == 'I')
	    return "int";
	else if (line.charAt(pos) == 'V')
	    return "void";
	else if (line.charAt(pos) == 'Z')
	    return "boolean";
	else if (line.charAt(pos) == '[')
	    return parseParameter(line,pos+1)+"[]";
        else if (line.charAt(pos) == 'B')
	    return "byte";
	else if (line.charAt(pos) == 'J')
            return "long";
	else if(line.charAt(pos) == 'F')
            return "float";
	else if(line.charAt(pos) == 'C')
            return "char";
	else if(line.charAt(pos) == 'D')
            return "double";
	else if(line.charAt(pos) == 'S')
            return "short";
	else {
	    System.out.println("UNKNONWN TYPE!!!");
	    throw new RuntimeException("UNKNOWN TYPE: "+line.charAt(pos));
	}		
    }

}