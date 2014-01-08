package edu.rpi;

import java.util.List;
import java.util.ArrayList;

public abstract class Constraint {

    protected AnnotatedValue left;

    protected AnnotatedValue right;

    protected List<Constraint> causes = new ArrayList<Constraint>(); 

    protected int id;

    private static int counter = 0; 

    public Constraint() {
        id = counter++;
    }

    public AnnotatedValue getLeft() {
        return left;
    }

    public AnnotatedValue getRight() {
        return right;
    }

    public int getId() {
        return id;
    }

    public void addCause(Constraint c) {
        causes.add(c);
    }

    public List<Constraint> getCauses() {
        return causes;
    }

    public String toString(String type, String sep) {
        return type + "-" + id + ": " + left.toString() + "  " 
            + sep + "  " + right.toString() 
            + (causes.size() == 0 ? "" : " caused by " + causeIds());
    }

    public String causeIds() {
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        boolean isFirst = true;
        for (Constraint c : causes) {
            if (isFirst) {
                sb.append(c.getId());
                isFirst = false;
            } else 
                sb.append("," + c.getId());
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return left.hashCode() * 13 + right.hashCode() * 11;
    }

	public static class SubtypeConstraint extends Constraint {
		public SubtypeConstraint(AnnotatedValue sub, AnnotatedValue sup) {
            this.left = sub;
            this.right = sup;
		}

        @Override
		public String toString() {
            return super.toString("SUB", "<:");
		}
        @Override

        public boolean equals(Object o) {
            return (o instanceof SubtypeConstraint) 
                && this.left.equals(((Constraint)o).getLeft()) 
                && this.right.equals(((Constraint)o).getRight());
        }
    }

	public static class EqualityConstraint extends Constraint {
		public EqualityConstraint(AnnotatedValue sub, AnnotatedValue sup) {
            this.left = sub;
            this.right = sup;
		}

        @Override
		public String toString() {
            return super.toString("EQU", "==");
		}
        @Override

        public boolean equals(Object o) {
            return (o instanceof EqualityConstraint) 
                && this.left.equals(((Constraint)o).getLeft()) 
                && this.right.equals(((Constraint)o).getRight());
        }
    }

	public static class UnequalityConstraint extends Constraint {
		public UnequalityConstraint(AnnotatedValue sub, AnnotatedValue sup) {
            this.left = sub;
            this.right = sup;
		}

        @Override
		public String toString() {
            return super.toString("UNE", "!=");
		}
        @Override

        public boolean equals(Object o) {
            return (o instanceof UnequalityConstraint) 
                && this.left.equals(((Constraint)o).getLeft()) 
                && this.right.equals(((Constraint)o).getRight());
        }
    }
}
