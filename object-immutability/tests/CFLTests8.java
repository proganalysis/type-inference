import java.util.ArrayList;
import java.util.HashMap;

class MyCrap {
	int i;
}


public class CFLTests8 {
	
	static HashMap<String,MyCrap> hashMap = new HashMap<String,MyCrap>();
	
	HashMap<String,MyCrap> map = new HashMap<String,MyCrap>();
	ArrayList<MyCrap> l = new ArrayList<MyCrap>();
	
	public void m() {
		MyCrap m1 = new MyCrap();
		MyCrap m2 = new MyCrap();
		hashMap.put("Ana",m1);
		
		map.put("Sweety",m2);
	}
	
	public void n() {
		MyCrap m3 = hashMap.get("Ana");
		MyCrap m4 = map.get("Sweety");
		l.add(m4);
	}
	
	public void o() {
		MyCrap m5 = l.get(0);
	}
	
	public static void main(String[] args) {
		CFLTests8 cfl = new CFLTests8();
		cfl.m();
		cfl.n();
		cfl.o();
	}
	
	
}
