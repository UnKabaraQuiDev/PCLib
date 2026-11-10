package lu.kbra.pclib.geo.equation.solver;

import java.util.List;

import lu.kbra.pclib.geo.equation.constraint.MathConstraint3dc;

/** Restrictions on an entire solution set; do not test just the base point. */
public record ConstrainedSolution(MathSolution underlying, List<MathConstraint3dc> constraints) implements MathSolution {
	public ConstrainedSolution {
		constraints = List.copyOf(constraints);
	}

	@Override
	public boolean complete() {
		return this.underlying.complete();
	}
}
