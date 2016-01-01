package edu.rpi.reimutils;

public class Edge<N,L> {
	N source;
	N target;
	L label;
	public Edge(N source, N target, L label) {
		this.source = source;
		this.target = target;
		this.label = label;
	}
	@Override
	public boolean equals(Object other) {		
		if (!(other instanceof Edge)) return false;
		Edge o = (Edge) other;
		boolean result = o.source.equals(source) && o.target.equals(target) && o.label.equals(label);
		// System.out.println("Objects "+toString()+" and "+other.toString()+" have equals="+result);
		return result;
	}
	@Override
	public int hashCode() {
		return source.hashCode()+target.hashCode()+label.hashCode();
	}
	
	@Override
	public String toString() {
		return "Edge: "+source.toString()+"---- "+label.toString()+" ---->"+target.toString();
	}
	public N getTarget() {
		return target;
	}
	public N getSource() {
		return source;
	}
	public L getLabel() {
		return label;
	}
	
}
