/**
 * 
 */
package checkers.inference2;

import java.util.Set;

/**
 * @author huangw5
 *
 */
public interface ConstraintSolver {
    public Set<Constraint> solve();

    public static class SolverException extends Exception {
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
