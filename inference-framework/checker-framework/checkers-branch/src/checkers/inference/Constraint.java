/**
 * 
 */
package checkers.inference;

import com.sun.source.tree.Tree;

/**
 * There are three types of constraints currently
 * @author huangw5
 *
 */
public abstract class Constraint {
	
	Reference left; 
	Reference right;
	Reference ref;
	Tree tree;
	
	int id; 
	private static int counter = 0;
	
	public Constraint() {
		id = counter++;
	}

	public Reference getLeft() {
		return left;
	}

	public Reference getRight() {
		return right;
	}

	public Reference getRef() {
		return ref;
	}
	
	public int getID() {
		return id;
	}

	public void setLeft(Reference r) {
		left = r;
	}

	public void setRight(Reference r) {
		right = r;
	}

	public void setRef(Reference r) {
		ref = r;
	}

    @Override
    public int hashCode() {
        int res = 0;
        if (left != null)
            res += left.hashCode() * 17;
        if (right != null)
            res += right.hashCode() * 13;
        if (ref != null)
            res += ref.hashCode() * 11;
        return res;
    }
	
	public abstract boolean isSimilar(Constraint c);

	public static class SubtypeConstraint extends Constraint {
		public SubtypeConstraint(Reference sub, Reference sup) {
			super();
			this.left = sub;
			this.right = sup;
		}
		
		@Override
		public String toString() {
			return "SUB-" + id + ": " + left.toAnnotatedString() + "  <:  " + right.toAnnotatedString();
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
		}
		
		@Override
		public String toString() {
			return "EQU-" + id + ": " + left.toAnnotatedString() + "  ==  " + right.toAnnotatedString();
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
		}
		
		@Override
		public String toString() {
			return "UNE-" + id + ": " + left.toAnnotatedString() + "  !=  " + right.toAnnotatedString();
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
	
	/**
	 * It doesn't enforce any constraints. It is used for variables that are free
	 * of constraints, e.g. a variable defined but never used. 
	 * @author huangw5
	 *
	 */
	public static class EmptyConstraint extends Constraint {
		public EmptyConstraint(Reference ref) {
			super();
			this.ref = ref;
		}
		
		@Override
		public String toString() {
			return "EMP-" + id + ": " + ref.toAnnotatedString();
		}
		@Override
		public boolean isSimilar(Constraint c) {
			if (c instanceof EmptyConstraint) {
				return ref.isSimilar(c.ref);
			}
			return false;
		}
        @Override
        public boolean equals(Object o) {
            Constraint obj = (Constraint) o;
            return (obj instanceof UnequalityConstraint) && 
                this.ref.equals(obj.getRef());
        }
	}
	
	public static class IfConstraint extends Constraint {
		private Constraint condition;
		private Constraint ifConstraint;
		private Constraint elseConstraint;
		public IfConstraint(Constraint condition, Constraint ifConstraint,
				Constraint elseConstraint) {
			super();
			this.condition = condition;
			this.ifConstraint = ifConstraint;
			this.elseConstraint = elseConstraint;
		}
		public Constraint getCondition() {
			return condition;
		}
		public Constraint getIfConstraint() {
			return ifConstraint;
		}
		public Constraint getElseConstraint() {
			return elseConstraint;
		}
		@Override
		public String toString() {
			return "IF-" + id + ": " + condition.toString() + " ? "
					+ ifConstraint + " : " + elseConstraint;
		}
		@Override
		public boolean isSimilar(Constraint c) {
			// TODO Auto-generated method stub
			return false;
		}
	}
	
	/*
	public static class NotConstraint extends Constraint {
		private Constraint nestedConstraint;
		public NotConstraint(Constraint constraint) {
			super();
			this.nestedConstraint = constraint;
		}
		public Constraint getNestedConstraint() {
			return nestedConstraint;
		}
	}
	
	public static abstract class BinaryConstraint extends Constraint {
		private Constraint left; 
		private Constraint right; 
		public BinaryConstraint(Constraint left, Constraint right) {
			super();
			this.left = left;
			this.right = right;
		}
		public Constraint getLeftConstraint() {
			return left;
		}
		public Constraint getRightConstraint() {
			return right;
		}
		
	}
	
	public static class AndConstraint extends BinaryConstraint {
		public AndConstraint(Constraint left, Constraint right) {
			super(left, right);
		}
	}
	
	public static class OrConstraint extends BinaryConstraint {
		public OrConstraint(Constraint left, Constraint right) {
			super(left, right);
		}
	}*/
}

