package lu.kbra.pclib.geo.equation.solver;

/** No dependency on geometric shape classes. */
public sealed interface MathSolution
		permits
			NoSolution,
			AffineSolution,
			FiniteSolutions,
			UnresolvedSolution,
			ConstrainedSolution,
			CompositeSolution,
			ImplicitSolution,
			QuadraticLocusSolution,
			AffineIntervalSolution {

	default boolean complete() {
		return true;
	}
}
