
package list;

import checkers.inference.aj.quals.*;

class ListIterator {
  private /*@Aliased*/ Entry lastReturned = null;
  private Entry next;
  private int nextIndex;
  final LinkedList list;
  final Entry header;

  ListIterator(LinkedList l, Entry h, int index) {
    list = l; 
    header = h; 
    lastReturned = header;
    next = header.next;
    for (nextIndex = 0; nextIndex < index; nextIndex++) 
      next = next.next;
  }

  public Object next() {
    lastReturned = next; 
    next = next.next; 
    nextIndex++;
    return lastReturned.elem;
  }

}
