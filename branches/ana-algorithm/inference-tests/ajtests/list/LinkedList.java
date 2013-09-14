package list; 

import checkers.inference.aj.quals.*;


class Entry {
  Object elem;
  Entry next;
  Entry prev;
  Entry(Object elem, Entry next, Entry prev) {
    this.elem = elem; 
    this.next = next; 
    this.prev = prev;
  }
}

public class LinkedList {
  Entry header;
  int size;

  public LinkedList() {
    header = new Entry(null,null,null);
    header.next = header.prev = header;
  }
  public void add(Object o) {
    Entry newEntry = new Entry(o,header,header.prev);
    newEntry.prev.next = newEntry;
    newEntry.next.prev = newEntry;
    size++;
  }
  public ListIterator iterator() {
    return new /*@Aliased*/ ListIterator(this,this.header,0);
  }

  public int size() { return size; }
}
