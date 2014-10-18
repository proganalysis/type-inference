public class FieldAssignments2 {

	private /*PolyPoly*/ FieldAssignments2 m;
    private /*ReadRead*/ FieldAssignments2 n;
    private /*PolyPoly*/ FieldAssignments2 f;
    private /*ReadRead*/ FieldAssignments2 g;

    public void foo() /**/ {
    	/*ReadRead*/ FieldAssignments2 a = new FieldAssignments2();
        /*ReadMut*/ FieldAssignments2 x = m;
        x.f = a;
        /*ReadRead*/ FieldAssignments2 b = x.f;
        
        /*MutMut*/ FieldAssignments2 c = n;
        /*MutMut*/ FieldAssignments2 y = new FieldAssignments2();
        y.f = c;
        /*MutMut*/ FieldAssignments2 d = y.f;
        d.g = new FieldAssignments2();
    }
}
