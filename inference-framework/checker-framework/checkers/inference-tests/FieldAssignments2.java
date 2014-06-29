public class FieldAssignments2 {

    private /*PolyPoly*/ FieldAssignments2 f;

    private /*ReadRead*/ FieldAssignments2 g;

    public void foo() /**/ {
    	/*ReadRead*/ FieldAssignments2 a = new FieldAssignments2();
        /*ReadMut*/ FieldAssignments2 x = new FieldAssignments2();
        x.f = a;
        /*ReadRead*/ FieldAssignments2 b = x.f;
        
        /*MutMut*/ FieldAssignments2 c = new FieldAssignments2();
        /*MutMut*/ FieldAssignments2 y = new FieldAssignments2();
        y.f = c;
        /*MutMut*/ FieldAssignments2 d = y.f;
        d.g = new FieldAssignments2();
    }
}
