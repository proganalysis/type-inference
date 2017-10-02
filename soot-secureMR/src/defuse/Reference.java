package defuse;

import java.util.HashSet;
import java.util.Set;

import soot.Value;

public class Reference {
	
	private Value value;
	private Set<String> operations;
	private Set<Value> children;
	private boolean isInMap;
	
	public Reference(Value value) {
		this.setValue(value);
		setOperations(new HashSet<>());
		setChildren(new HashSet<>());
	}

	public Value getValue() {
		return value;
	}

	public void setValue(Value value) {
		this.value = value;
	}

	public Set<String> getOperations() {
		return operations;
	}

	public void setOperations(Set<String> operations) {
		this.operations = operations;
	}
	
	public boolean contains(String ope) {
		return operations.contains(ope);
	}
	
	public boolean addOperation(String ope) {
		return operations.add(ope);
	}
	
	public boolean removeOperation(String ope) {
		return operations.remove(ope);
	}
	
	public boolean clearOperations() {
		if (operations.isEmpty()) return false;
		else {
			operations.clear();
			return true;
		}
	}

	public Set<Value> getChildren() {
		return children;
	}

	public void setChildren(Set<Value> children) {
		this.children = children;
	}
	
	public void addChild(Value child) {
		children.add(child);
	}

	public boolean isInMap() {
		return isInMap;
	}

	public void setInMap(boolean isInMap) {
		this.isInMap = isInMap;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Reference other = (Reference) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return  (isInMap ? "Map " : "Reduce " ) 
				+ " [value=" + value + ", operations=" + operations + ", children=" + children + "]";
	}

}
