/**
 * 
 */
package checkers.inference2.sflow;

import java.util.Set;

import checkers.inference2.AbstractConstraintSolver;
import checkers.inference2.Constraint;
import checkers.inference2.InferenceChecker;

/**
 * @author huangw5
 *
 */
public class SFlowConstraintSolver extends AbstractConstraintSolver<SFlowChecker> {

	private SFlowChecker checker;

	public SFlowConstraintSolver(SFlowChecker t) {
		super(t);
		this.checker = t;
	}

	/* (non-Javadoc)
	 * @see checkers.inference2.AbstractConstraintSolver#solveImpl()
	 */
	@Override
	protected Set<Constraint> solveImpl() {
		// TODO Auto-generated method stub
		return null;
	}

}
