package edu.rpi.reimutils;

import java.util.HashMap;

import edu.rpi.AnnotatedValue;

public abstract class CfgSymbol {

	static int CALLS = 1;
	static int FIELDS = 2;
	
	protected static OpenParen OPENPAREN = new OpenParen();
	protected static CloseParen CLOSEPAREN = new CloseParen();
	protected static Local LOCAL = new Local();
	protected static HashMap<AnnotatedValue,AtomicOpenParen> atomicOpens = new HashMap<AnnotatedValue, AtomicOpenParen>();
	protected static HashMap<AnnotatedValue,AtomicCloseParen> atomicCloses = new HashMap<AnnotatedValue, AtomicCloseParen>();
		
	//protected static AtomicOpenParen ARRAYWRITE = new AtomicOpenParen(new AnnotatedValue("Array write: [",null,AnnotatedValue.Kind.COMPONENT,null));
	//protected static AtomicCloseParen ARRAYREAD = new AtomicCloseParen(ARRAYWRITE.getInfo());
	
	public static AtomicOpenParen getAtomicOpen(AnnotatedValue info) {
		if (atomicOpens.get(info) == null) 
			atomicOpens.put(info,new AtomicOpenParen(info));
		return atomicOpens.get(info);
	}
	
	public static AtomicCloseParen getAtomicClose(AnnotatedValue info) {
		if (atomicCloses.get(info) == null) 
			atomicCloses.put(info,new AtomicCloseParen(info));
		return atomicCloses.get(info);
	}
	
	// Concat during dynamic closure
	public abstract CfgSymbol concat(CfgSymbol other); 
	
	public abstract boolean match(CfgSymbol other);
	
	// Concat during final closure computation
	public abstract CfgSymbol finalConcat(CfgSymbol other);
	
}

class AtomicOpenParen extends CfgSymbol {

	public AtomicOpenParen(AnnotatedValue info) {
		this.info = info;
	}

	private AnnotatedValue info;
	
	public AnnotatedValue getInfo() {
		return info;
	}
	
	@Override
	public CfgSymbol concat(CfgSymbol other) {
		/*
		if (other instanceof AtomicCloseParen) {
			if (getInfo().equals(((AtomicCloseParen) other).getInfo())) {
				return LOCAL;
			}
			else 
			    return null;			
		}
		else if (other instanceof Local)
			return this;
		*/	
		return null;
	}	
	@Override
	public String toString() { 
		return "(_"+info.toString();
	}

	@Override
	public boolean match(CfgSymbol other) {
		if (other instanceof AtomicCloseParen) {
			if (getInfo().equals(((AtomicCloseParen) other).getInfo())) {
				return true;
			}
			else 
			    return false;			
		}
		else 
			return false;
	}

	@Override
	public CfgSymbol finalConcat(CfgSymbol other) {
		return null;
	}
}

class AtomicCloseParen extends CfgSymbol {

	private AnnotatedValue info;
	
	public AtomicCloseParen(AnnotatedValue info) {
		this.info = info;
	}
	public AnnotatedValue getInfo() {
		return info;
	}
	
	@Override
	public CfgSymbol concat(CfgSymbol other) {
		return null;
	}
	
	@Override
	public String toString() { 
		return ")_"+info.toString();
	}
	@Override
	public boolean match(CfgSymbol other) {
		return false;
	}
	@Override
	public CfgSymbol finalConcat(CfgSymbol other) {
		return null;
	}
	
}

class OpenParen extends CfgSymbol {

	@Override
	public CfgSymbol concat(CfgSymbol other) {
		return null;
	}
	
	@Override
	public String toString() { 
		return "(";
	}

	@Override
	public boolean match(CfgSymbol other) {
		return false;
	}

	@Override
	public CfgSymbol finalConcat(CfgSymbol other) {
		if (other == OPENPAREN || other == LOCAL) {
			return this;
		}
		else {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
}

class CloseParen extends CfgSymbol {
	@Override
	public CfgSymbol concat(CfgSymbol other) {
		/*
		if (other instanceof CloseParen)
			return CLOSEPAREN;
		else if (other instanceof Local)
			return CLOSEPAREN;
		else 
			return null;
		*/
		return null;
	}
	
	@Override
	public String toString() { 
		return ")";
	}

	@Override
	public boolean match(CfgSymbol other) {
		return false;
	}

	@Override
	public CfgSymbol finalConcat(CfgSymbol other) {
		if (other == CLOSEPAREN || other == LOCAL) {
			return this;
		}
		else if (other == OPENPAREN) {
			return other;
		}
		else {
			return null;
		}
	}	
}

class Local extends CfgSymbol {
	@Override
	public CfgSymbol concat(CfgSymbol other) {
		if (other == LOCAL)
			return LOCAL;
		/*
		else if (other instanceof CloseParen) {
			return CLOSEPAREN;
		}
		else if (other instanceof OpenParen) {
			return OPENPAREN;
		}
		*/
		else 
			return null;
	}
	
	@Override
	public String toString() {
		return " ";
	}

	@Override
	public boolean match(CfgSymbol other) {
		return false;
	}

	@Override
	public CfgSymbol finalConcat(CfgSymbol other) {
		if (other == OPENPAREN || other == CLOSEPAREN) {
			return other;
		} 
		else if (other == LOCAL) {
			return this;
		}
		else {
			return null;
		}
	}
}