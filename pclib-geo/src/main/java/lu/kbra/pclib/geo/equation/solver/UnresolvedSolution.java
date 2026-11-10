package lu.kbra.pclib.geo.equation.solver;

/** Solver did not prove any complete answer. Never treat this as empty. */
public record UnresolvedSolution(String reason) implements MathSolution {
	@Override
	public boolean complete() {
		return false;
	}
}
