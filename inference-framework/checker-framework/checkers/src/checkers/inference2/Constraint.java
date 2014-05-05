/**
 * 
 */
package checkers.inference2;

import java.util.List;
import java.util.ArrayList;

/**
 * @author huangw5
 *
 */
public abstract class Constraint {
	
	protected Reference left; 
	
	protected Reference right;

    protected List<Constraint> causes; // this constraint may be generated due to other constraints
	
	protected int id; 
	
    /**
     * kind = 0: subkind 
     * kind = 1: equality
     * kind = 2: inequality
     */
    protected int kind;
	
	private static int counter = 0;
	
	public Constraint() {
		id = counter++;
        causes = new ArrayList<Constraint>();
	}
	
	public static int maxId() {
		return counter;
	}
	

	public Reference getLeft() {
		return left;
	}

	public Reference getRight() {
		return right;
	}

	public int getId() {
		return id;
	}
	
	public int getKind() {
		return kind;
	}

    public void addCause(Constraint c) {
        causes.add(c); 
    }   

    public List<Constraint> getCauses() {
        return causes;
    }   

    @Override
    public int hashCode() {
        int res = 0;
        if (left != null)
            res += left.hashCode() * 17;
        if (right != null)
            res += right.hashCode() * 13;
        return res;
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
	
	public abstract boolean isSimilar(Constraint c);

	public static class SubtypeConstraint extends Constraint {
		public SubtypeConstraint(Reference sub, Reference sup) {
            super();
            this.left = sub;
            this.right = sup;
            this.kind = 0;
		}
		
		@Override
		public String toString() {
            return super.toString("SUB", "<:");
		}

		@Override
		public boolean isSimilar(Constraint c) {
			if (c instanceof SubtypeConstraint) {
				return (left.isSimilar(c.left) && right.isSimilar(c.right));
			}
			return false;
		}

        @Override
        public boolean equals(Object o) {
            Constraint obj = (Constraint) o;
            return (obj instanceof SubtypeConstraint) && 
                this.left.equals(obj.getLeft()) && this.right.equals(obj.getRight());
        }
	}
	
	public static class EqualityConstraint extends Constraint {
		public EqualityConstraint(Reference left, Reference right) {
			super();
			this.left = left;
			this.right = right;
            this.kind = 1;
		}
		
		@Override
		public String toString() {
            return super.toString("EQU", "==");
		}
		
		@Override
		public boolean isSimilar(Constraint c) {
			if (c instanceof EqualityConstraint) {
				return (left.isSimilar(c.left) && right.isSimilar(c.right));
			}
			return false;
		}
		
        @Override
        public boolean equals(Object o) {
            Constraint obj = (Constraint) o;
            return (obj instanceof EqualityConstraint) && 
                this.left.equals(obj.getLeft()) && this.right.equals(obj.getRight());
        }
	}
	
	public static class UnequalityConstraint extends Constraint {
		public UnequalityConstraint(Reference left, Reference right) {
			super();
			this.left = left;
			this.right = right;
            this.kind = 2;
		}
		
		@Override
		public String toString() {
            return super.toString("UNE", "!=");
		}
		
		@Override
		public boolean isSimilar(Constraint c) {
			if (c instanceof UnequalityConstraint) {
				return (left.isSimilar(c.left) && right.isSimilar(c.right));
			}
			return false;
		}
		
        @Override
        public boolean equals(Object o) {
            Constraint obj = (Constraint) o;
            return (obj instanceof UnequalityConstraint) && 
                this.left.equals(obj.getLeft()) && this.right.equals(obj.getRight());
        }
	}

}

