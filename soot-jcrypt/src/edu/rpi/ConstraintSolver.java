package edu.rpi;

import java.lang.annotation.Annotation;
import java.util.*;

public interface ConstraintSolver {
    public Set<Constraint> solve();
    
    public Set<Annotation> getAnnotations(AnnotatedValue av);

    public static class SolverException extends Exception {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public SolverException(String string) {
			super(string);
		}
    }

	public static enum FailureStatus {
		IGNORE,
		WARN,
		ERROR
	}
}
