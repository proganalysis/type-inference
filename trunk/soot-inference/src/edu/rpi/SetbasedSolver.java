package edu.rpi;

import java.util.*;

import edu.rpi.ConstraintSolver.FailureStatus;
import edu.rpi.ConstraintSolver.SolverException;
import edu.rpi.Constraint.SubtypeConstraint;
import edu.rpi.Constraint.EqualityConstraint;
import edu.rpi.Constraint.UnequalityConstraint;

public class SetbasedSolver extends AbstractConstraintSolver {

    public SetbasedSolver(InferenceTransformer t) {
        super(t);
    }

    @Override
    public Set<Constraint> solve() {
        Set<Constraint> constraints = t.getConstraints();
		Set<Constraint> warnConstraints = new HashSet<Constraint>();
		Set<Constraint> conflictConstraints;
		boolean hasUpdate = false;
		do {
			conflictConstraints = new LinkedHashSet<Constraint>();
			hasUpdate = false;
			for (Constraint c : constraints) {
				try {
					hasUpdate = handleConstraint(c) || hasUpdate;
				} catch (SolverException e) {
					FailureStatus fs = t.getFailureStatus(c);
					if (fs == FailureStatus.ERROR) {
						hasUpdate = false;
						conflictConstraints.add(c);
					} else if (fs == FailureStatus.WARN) {
						if (!warnConstraints.contains(c)) {
							System.out.println("WARN: handling constraint " + c + " failed.");
							warnConstraints.add(c);
						}
					}
				}
			}
		} while (hasUpdate);
        return conflictConstraints;
    }
}


