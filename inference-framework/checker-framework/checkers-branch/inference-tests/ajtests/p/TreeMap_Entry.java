package p;

import checkers.inference.aj.quals.*;

/**
 * Node in the Tree.  Doubles as a means to pass key-value pairs back to
 * user (see Map.Entry).
 */

/*internal*/class TreeMap_Entry implements Map_Entry {
	/*atomic(F)*/ private Object key;
	/*atomic(F)*/ private Object value;
	/*atomic(F)*/ private TreeMap_Entry left/*F=this.F*/ = null;
	/*atomic(F)*/ private TreeMap_Entry right/*F=this.F*/ = null;
	/*atomic(F)*/ private TreeMap_Entry parent/*F=this.F*/;
	/*atomic(F)*/ private boolean color = TreeMap.BLACK;

	public TreeMap_Entry/*F=this.F*/ getLeft(){ // FT: added
		return left;
	}
	
    public void setLeft(/*@Aliased*/ TreeMap_Entry/*F=this.F*/ l){ // FT: added
		left = l;
	}
	
	public TreeMap_Entry/*F=this.F*/ getRight(){ // FT: added
		return right;
	}
	
    public void setRight(/*@Aliased*/ TreeMap_Entry/*F=this.F*/ r){ // FT: added
		right = r;
	}
	
	public TreeMap_Entry/*F=this.F*/ getParent(){ // FT: added
		return parent;
	}
	
    public void setParent(/*@Aliased*/ TreeMap_Entry/*F=this.F*/ p){ // FT: added
		parent = p;
	}
	
	public boolean getColor(){ // FT: added
		return color;
	}
	
	public void setColor(boolean c){ // FT: added
		color = c;
	}
	
	
    /**
     * Make a new cell with given key, value, and parent, and with
     * <tt>null</tt> child links, and BLACK color.
     */
    TreeMap_Entry(Object key, Object value, TreeMap_Entry parent/*F=this.F*/) {
        this.key = key;
        this.value = value;
        this.parent = parent;
    }

    /**
     * Returns the key.
     *
     * @return the key.
     */
    public Object getKey() {
        return key;
    }
    
    public void setKey(Object k){ // FT: added
    	key = k;
    }

    /**
     * Returns the value associated with the key.
     *
     * @return the value associated with the key.
     */
    public Object getValue() {
        return value;
    }

    /**
     * Replaces the value currently associated with the key with the given
     * value.
     *
     * @return the value associated with the key before this method was
     *           called.
     */
    public Object setValue(Object value) {
        Object oldValue = this.value;
        this.value = value;
        return oldValue;
    }

    public boolean equals(/*unitfor*/ Object o) {
        if (!(o instanceof Map_Entry))
            return false;
        Map_Entry e/*F=this.F*/ = (Map_Entry/*F=this.F*/)o;

        return TreeMap.valEquals(key,e.getKey()) && TreeMap.valEquals(value,e.getValue());
    }

    public int hashCode() {
        int keyHash = (key==null ? 0 : key.hashCode());
        int valueHash = (value==null ? 0 : value.hashCode());
        return keyHash ^ valueHash;
    }

    public String toString() {
        return key + "=" + value;
    }
}
