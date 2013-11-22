package p;

/**
 * This should be made public as soon as possible.  It greatly simplifies
 * the task of implementing Map.
 */

/*internal*/class AbstractMap_SimpleEntry implements Map_Entry {
/*atomic(F)*/ private Object key;
/*atomic(F)*/ private Object value;

public AbstractMap_SimpleEntry(Object key, Object value) {
    this.key   = key;
        this.value = value;
}

public AbstractMap_SimpleEntry(Map_Entry/*F=this.F*/ e) {
    this.key   = e.getKey();
        this.value = e.getValue();
}

public Object getKey() {
    return key;
}

public Object getValue() {
    return value;
}

public Object setValue(Object value) {
    Object oldValue = this.value;
    this.value = value;
    return oldValue;
}

public boolean equals(/*unitfor*/ Object o) {
    if (!(o instanceof Map_Entry))
	return false;
    Map_Entry e/*F=this.F*/ = (Map_Entry/*F=this.F*/)o;
    return eq(key, e.getKey()) && eq(value, e.getValue());
}

public int hashCode() {
    return ((key   == null)   ? 0 :   key.hashCode()) ^
	   ((value == null)   ? 0 : value.hashCode());
}

public String toString() {
    return key + "=" + value;
}

    private static boolean eq(Object o1, Object o2) {
        return (o1 == null ? o2 == null : o1.equals(o2));
    }
}
