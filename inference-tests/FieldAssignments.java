

/**
 * This class tests non-parameteric, non-static field assignments to
 * and from locals.
 */
public class FieldAssignments {

    private /*this-mutable*/ FieldAssignments f;

    private /*readonly*/ FieldAssignments g;

    public void foo() /*mutable*/ {
        /*readonly*/ FieldAssignments x = this.g;
        this.g = x;

        /*mutable*/ FieldAssignments y = this.f;
        y.f.g = y;
    }
}
