package lu.kbra.pclib.geo.equation.solver;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3d;

import lu.kbra.pclib.geo.equation.ImplicitRepresentable;
import lu.kbra.pclib.geo.equation.constraint.MathConstraint3dc;
import lu.kbra.pclib.geo.equation.constraint.MathConstraint3fc;
import lu.kbra.pclib.geo.equation.implicit.ImplicitEquation2dc;
import lu.kbra.pclib.geo.equation.implicit.ImplicitEquation3dc;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem2dc;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem2fc;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem3dc;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem3fc;
import lu.kbra.pclib.geo.math.GeometryPrecision;

/** Mathematical system composition. This class imports no PCLib geometry shapes. */
public final class EquationSystemSolver {
	private EquationSystemSolver() {
	}

	public static MathSolution intersection(final ImplicitSystem3dc a, final ImplicitSystem3dc b) {
		return EquationSystemSolver.intersection(a, b, List.of(), GeometryPrecision.DEFAULT);
	}

	/**
	 * Any two implicit-capable shapes may use this mathematical path. This layer sees only equations
	 * and constraints, never a geometry subtype.
	 */
	public static MathSolution intersectionRepresentations(
			final ImplicitRepresentable<ImplicitSystem3dc, MathConstraint3dc> a,
			final ImplicitRepresentable<ImplicitSystem3dc, MathConstraint3dc> b,
			final GeometryPrecision precision) {
		final List<MathConstraint3dc> constraints = new ArrayList<>(a.constraints());
		constraints.addAll(b.constraints());
		return EquationSystemSolver.intersection(a.implicitEquation(), b.implicitEquation(), constraints, precision);
	}

	public static MathSolution intersection(
			final ImplicitSystem3dc a,
			final ImplicitSystem3dc b,
			final List<MathConstraint3dc> constraints,
			final GeometryPrecision precision) {
		final List<ImplicitEquation3dc> equations = new ArrayList<>(a.equations());
		equations.addAll(b.equations());
		final MathSolution solution = PolynomialSystemSolver.solve(new ImplicitSystem3dc(equations), precision);
		if (constraints.isEmpty() || solution instanceof NoSolution || solution instanceof UnresolvedSolution) {
			return solution;
		}
		if (solution instanceof final AffineSolution affine && affine.dimension() == 0) {
			final double[] p = affine.origin();
			final Vector3d point = new Vector3d(p[0], p[1], p[2]);
			for (final MathConstraint3dc constraint : constraints) {
				if (!constraint.accepts(point, precision)) {
					return NoSolution.INSTANCE;
				}
			}
			return solution;
		}
		if (solution instanceof final AffineSolution affine && affine.dimension() == 1) {
			return AffineConstraintClipper.clip(affine, constraints, precision);
		}
		if (solution instanceof final FiniteSolutions finite) {
			final List<double[]> accepted = new ArrayList<>();
			for (final double[] p : finite.points()) {
				final Vector3d point = new Vector3d(p[0], p[1], p[2]);
				if (constraints.stream().allMatch(c -> c.accepts(point, precision))) {
					accepted.add(p);
				}
			}
			return accepted.isEmpty() ? NoSolution.INSTANCE : new FiniteSolutions(accepted);
		}
		// A positive-dimensional solution needs domain clipping, not a sample-point filter.
		return new ConstrainedSolution(solution, constraints);
	}

	public static MathSolution intersection(final ImplicitSystem2dc a, final ImplicitSystem2dc b) {
		final List<ImplicitEquation2dc> equations = new ArrayList<>(a.equations());
		equations.addAll(b.equations());
		return LinearSystemSolver.solve(new ImplicitSystem2dc(equations));
	}

	/** Float equations use the same mathematical result model and double-precision elimination. */
	public static MathSolution intersection(final ImplicitSystem2fc a, final ImplicitSystem2fc b) {
		return intersection(a.asDouble(), b.asDouble());
	}

	public static MathSolution intersection(final ImplicitSystem3fc a, final ImplicitSystem3fc b) {
		return intersection(a.asDouble(), b.asDouble());
	}

	/**
	 * Compose float implicit-capable shapes without introducing shape-dependent logic into the solver.
	 * Structural linear float constraints remain linear after widening, so affine clipping still works.
	 */
	public static MathSolution intersectionFloatRepresentations(
			final ImplicitRepresentable<ImplicitSystem3fc, MathConstraint3fc> a,
			final ImplicitRepresentable<ImplicitSystem3fc, MathConstraint3fc> b,
			final GeometryPrecision precision) {
		final List<MathConstraint3dc> constraints = new ArrayList<>();
		for (final MathConstraint3fc constraint : a.constraints()) {
			constraints.add(constraint.asDouble());
		}
		for (final MathConstraint3fc constraint : b.constraints()) {
			constraints.add(constraint.asDouble());
		}
		return intersection(a.implicitEquation().asDouble(), b.implicitEquation().asDouble(), constraints, precision);
	}
}
