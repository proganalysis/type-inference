package webil.runtime.client;

/**
 * Our own implementation of client-side queue, because GWT currently does not support java.util.Queue.
 * @author Xin Qi
 */
public class WilQueue {
    protected static class Element {
        protected Object data;
        protected Element next;
        
        public Element(Object data) {
            this.data = data;
        }
    }
    
    Element head = new Element(null);
    Element tail = head;
    
    public boolean isEmpty() {
        return head == tail;
    }
    
    public boolean offer(Object o) {
        tail.next = new Element(o);
        tail = tail.next;
        return true;
    }
    
    public Object peek() {
        if (head.next == null) return null;
        return head.next.data;
    }
    
    public Object poll() {
        if (head.next == null) return null;
        Object o = head.next.data;
        if (head.next == tail) tail = head;
        head.next = head.next.next;
        return o;
    }
    
    public Object peekTail() {
        if (head == tail) return null;
        return tail.data;
    }
}
