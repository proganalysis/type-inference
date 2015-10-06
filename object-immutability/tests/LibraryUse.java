import java.util.*;

public class LibraryUse {
	public static void main(String[] arg) {
		ArrayList<String> al = new ArrayList<String>();
		al.add("Boza");
		al.add("Katarina");
		ArrayList<String> al1 = al;
		int i = al1.size();
		System.out.println(i);
	}
}
