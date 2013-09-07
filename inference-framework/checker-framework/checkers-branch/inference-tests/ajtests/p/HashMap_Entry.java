/**
 * 
 */
package p;

import checkers.inference.aj.quals.*;

/*internal*/class HashMap_Entry implements Map_Entry {
    /*atomic(F)*/ private final Object key;
    /*atomic(F)*/ private Object value;
    /*atomic(F)*/ private final int hash;
    /*atomic(F)*/ private HashMap_Entry next/*F=this.F*/;

    /**
     * Create new entry.
     */
    HashMap_Entry(int h, Object k, Object v, HashMap_Entry n/*F=this.F*/) {
        value = v;
        next = n;
        key = k;
        hash = h;
    }

    public Object getKey() { 
        return HashMap.unmaskNull(key);
    }

    public Object getValue() { 
        return value;
    }

    public HashMap_Entry/*F=this.F*/ getNext(){
    	return next;
    }
    
    public void setNext(/*@Aliased*/ HashMap_Entry/*F=this.F*/ n){ 
    	this.next = n;
    }
    
    public int getHash(){ 
    	return hash;
    }
    
    public Object setValue(Object newValue) { 
    Object oldValue = value;
        value = newValue;
        return oldValue;
    }

    public boolean equals(/*unitfor*/ Object o) { 
        if (!(o instanceof Map_Entry))
            return false;
        Map_Entry e/*F=this.F*/ = (Map_Entry/*F=this.F*/)o;
        Object k1 = getKey();
        Object k2 = e.getKey();
        if (k1 == k2 || (k1 != null && k1.equals(k2))) {
            Object v1 = getValue();
            Object v2 = e.getValue();
            if (v1 == v2 || (v1 != null && v1.equals(v2))) 
                return true;
        }
        return false;
    }

    public int hashCode() { 
        return (key==HashMap.NULL_KEY ? 0 : key.hashCode()) ^
               (value==null   ? 0 : value.hashCode());
    }

    public String toString() { 
        return getKey() + "=" + getValue();
    }

    /**
     * This method is invoked whenever the value in an entry is
     * overwritten by an invocation of put(k,v) for a key k that's already
     * in the HashMap.
     */
    void recordAccess(HashMap/*M=this.F*/ m) { 
    }

    /**
     * This method is invoked whenever the entry is
     * removed from the table.
     */
    void recordRemoval(HashMap/*M=this.F*/ m) {
    }
}
