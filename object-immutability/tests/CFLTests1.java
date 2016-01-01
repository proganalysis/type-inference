

class MyA {
	String f;
}

class List{
    Object[] elems;
    int count;
    List() { 
    	Object[] t = new Object[10];
        this.elems = t; 
    }
    void add(Object m) {
       Object[] t = this.elems;
       t[count++] = m;
    }
    Object get(int ind) {
    	Object[] t = this.elems;
    	Object p = t[ind];
    	return p;
    }
}

class ListClient {
	List list;
	ListClient(List l) { 
		this.list = l; 
	}
	Object retrieve() { 
		List t = this.list;
		Object r = t.get(0);
		return r;
	}
}

public class CFLTests1 {
	public static void main(String[] arg) {
		List l1 = new List();
		MyA t = new MyA(); 
		l1.add(t);
		ListClient client = new ListClient(l1);
		List l2 = new List();
		MyA i = new MyA(); i.f = "abc";
		l2.add(i);
		MyA s = (MyA) client.retrieve();
		MyA j = (MyA) l2.get(0);
		String str = s.f;
	}
}
