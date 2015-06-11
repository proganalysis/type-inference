import java.util.*;

public class ExtendedVertex {
    long id;
    ArrayList<Long> calls;
    ArrayList<Long> fields;
    int depth;
    ExtendedVertex parent;
    String info;


    public boolean equals(Object o) {
	ExtendedVertex other = (ExtendedVertex) o;
	if (id != other.id) return false;

	// if (parent != other.parent) return false;
	
	if ((calls.size() == 0) && (other.calls.size() == 0)) return true;
	if ((calls.size() == 0) && (other.calls.size() != 0)) return false;
	if ((calls.size() != 0) && (other.calls.size() == 0)) return false;
	if (!calls.get(calls.size()-1).equals(other.calls.get(other.calls.size()-1))) return false;
        if ((calls.size() > 1) && (other.calls.size() > 1) &&
            !calls.get(calls.size()-2).equals(other.calls.get(other.calls.size()-2))) return false;

	if ((fields.size() == 0) && (other.fields.size() == 0)) return true;
        if ((fields.size() == 0) && (other.fields.size() != 0)) return false;
        if ((fields.size() != 0) && (other.fields.size() == 0)) return false;
	if (!fields.get(fields.size()-1).equals(other.fields.get(other.fields.size()-1))) return false;
        if ((fields.size() > 1) && (other.fields.size() > 1) &&
            !fields.get(fields.size()-2).equals(other.fields.get(other.fields.size()-2))) return false;


	
	/*
	if (calls.size() != other.calls.size()) return false;
	for (int i=0; i<calls.size(); i++)
	    if (!calls.get(i).equals(other.calls.get(i))) return false;
	*/
        /*
	if (fields.size() != other.fields.size()) return false;
        for (int i=0; i<fields.size(); i++)
            if (!fields.get(i).equals(other.fields.get(i))) return false;
	*/
	return true;
    }

    public int hashCode() { return (int) id; }

    public ExtendedVertex(long id, ArrayList<Long> c, boolean callCopy, ArrayList<Long> f, boolean fieldCopy) {
	this.id = id;
	if (callCopy)
	    this.calls = new ArrayList<Long>(c);
	else
	    this.calls = c;
	if (fieldCopy)
	    this.fields = new ArrayList<Long>(f);
	else
	    this.fields = f;
    }

    public void printVertex(int depth) {
	System.out.println("---- Vertex id: "+id+" ");
	for (int i=0; i<depth; i++) System.out.print("  ");
	System.out.print("---- The calls array: ");
	for (int i=0; i<calls.size(); i++) 
	    System.out.print(calls.get(i)+"    ");
	System.out.println();
	for (int i=0; i<depth; i++) System.out.print("  ");
	System.out.print("---- The fields array: ");
        for (int i=0; i<fields.size(); i++)
            System.out.print(fields.get(i)+"    ");
	System.out.println();
	for (int i=0; i<depth; i++) System.out.print("  ");
	System.out.print("---- The edge info: ");
	System.out.println(info);
    }
    

    ExtendedVertex newExtendedVertex(Edge edge) {

	ExtendedVertex result = null;
	long id = edge.target;

	if (edge.info.equals("call") || edge.info.equals("call-super")) {
	    if ((info != null) && (info.equals("subtype-plus") || info.equals("subtype-minus"))) return null; 
	    if (calls.contains(new Long(Math.abs(edge.call)))) 
		result = null;
	    else {
		result = new ExtendedVertex(id,calls,true,fields,false);
		result.calls.add(new Long(Math.abs(edge.call)));
	    }
	}
	else if (edge.info.equals("write")) { 
	    if (fields.contains(new Long(Math.abs(edge.field))))
                result = null;
            else {
		result = new ExtendedVertex(id,calls,false,fields,true);
		result.fields.add(new Long(Math.abs(edge.field)));
	    }
	}
	else if (edge.info.equals("return") || edge.info.equals("return-super")) {
	    if ((info != null) && info.equals("subtype-minus")) return null;
	    if (edge.info.equals("return-super") && info.equals("subtype-plus")) return null;
	    if (calls.size()==0) {
		result = new ExtendedVertex(id,calls,false,fields,false);
	    }
	    else if (calls.get(calls.size()-1).longValue() == edge.call) {
		result = new ExtendedVertex(id,calls,true,fields,false);
		result.calls.remove(calls.size()-1);
	    }
	    else result = null;
	}
	else if (edge.info.equals("read")) {
	    if (fields.size()==0) {
		result = new ExtendedVertex(id,calls,false,fields,false);
	    }
            else if (fields.get(fields.size()-1).longValue() == edge.field) {
                // TODO: pop last call off stack, return stack  

		    result = new ExtendedVertex(id,calls,false,fields,true);
		    result.fields.remove(fields.size()-1);

            }
	    else {
		result = null;
	    }
	}
	else if (edge.info.equals("toClass")) {
	    result = new ExtendedVertex(id,new ArrayList<Long>(),true,fields,false);
	}
	else if (edge.info.equals("fromClass") || (edge.info.equals("fromStatic")) || (edge.info.indexOf("local")>-1)) {
	    result = new ExtendedVertex(id,calls,false,fields,false);
	}
	else if (edge.info.equals("toStatic")) {
	    result = new ExtendedVertex(id,new ArrayList<Long>(),false,fields,false);
	}
	else if (edge.info.equals("subtype-minus")) {
	    // TODO: NEED TO FIGURE OUT WHAT TO DO! DOUBLE CHECK
	    // result = new ExtendedVertex(id,calls,false,fields,false);
	    if (info.equals("subtype-minus") || info.equals("call")) 
		result = new ExtendedVertex(id,calls,false,fields,false);
	    else return null;

	}
	else if (edge.info.equals("subtype-plus")) {
            // TODO: NEED TO FIGURE OUT WHAT TO DO! DOUBLE CHECK                                                                        
            // result = new ExtendedVertex(id,calls,false,fields,false);                                                                    
            if (info.equals("subtype-plus") || (info.indexOf("local")>-1))
		result = new ExtendedVertex(id,calls,false,fields,false);
            else return null;
	}


	if (result != null) { result.depth = depth+1; result.parent = this; result.info = edge.info; }
	return result;
    }

}