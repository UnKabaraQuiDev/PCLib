package lu.kbra.pclib.geo.equation.solver;

import java.util.ArrayList;
import java.util.List;

import lu.kbra.pclib.geo.equation.constraint.LinearConstraint3d;
import lu.kbra.pclib.geo.equation.constraint.MathConstraint3dc;
import lu.kbra.pclib.geo.equation.constraint.Relation;
import lu.kbra.pclib.geo.equation.implicit.LinearEquation3d;
import lu.kbra.pclib.geo.math.GeometryPrecision;

/**
 * Clips a one-dimensional affine family by linear inequalities. Nonlinear/inequality-not-equal
 * restrictions remain unresolved as constraints.
 */
public final class AffineConstraintClipper {
	private AffineConstraintClipper() {
	}

	public static MathSolution
			clip(final AffineSolution line, final List<MathConstraint3dc> constraints, final GeometryPrecision precision) {
		if (line.dimension() != 1) {
			return new ConstrainedSolution(line, constraints);
		}
		double lower = Double.NEGATIVE_INFINITY, upper = Double.POSITIVE_INFINITY;
		boolean lowerClosed = true, upperClosed = true;
		final List<MathConstraint3dc> remaining = new ArrayList<>();
		final double[] p = line.origin(), d = line.basis()[0];

		for (final MathConstraint3dc constraint : constraints) {
			if (!(constraint instanceof final LinearConstraint3d linear) || linear.relation() == Relation.NE) {
				remaining.add(constraint);
				continue; // may split the interval in two
			}
			final LinearEquation3d e = linear.expression();
			final double slope = e.a() * d[0] + e.b() * d[1] + e.c() * d[2];
			final double offset = e.a() * p[0] + e.b() * p[1] + e.c() * p[2] + e.d();
			final double epsilon = precision.distance();
			if (Math.abs(slope) <= precision.relative() * Math.max(1, Math.abs(offset))) {
				final boolean accepts = switch (linear.relation()) {
				case EQ -> Math.abs(offset) <= epsilon;
				case GE -> offset >= -epsilon;
				case GT -> offset > epsilon;
				case LE -> offset <= epsilon;
				case LT -> offset < -epsilon;
				case NE -> throw new AssertionError();
				};
				if (!accepts) {
					return NoSolution.INSTANCE;
				}
				continue;
			}
			final double bound = -offset / slope;
			final boolean equal = linear.relation() == Relation.EQ;
			final boolean upperBound = linear.relation() == Relation.LE || linear.relation() == Relation.LT;
			final boolean closed = linear.relation() != Relation.LT && linear.relation() != Relation.GT;
			if (equal) {
				if (bound > lower) {
					lower = bound;
					lowerClosed = true;
				}
				if (bound < upper) {
					upper = bound;
					upperClosed = true;
				}
			} else if (upperBound == slope > 0) {
				if (bound < upper) {
					upper = bound;
					upperClosed = closed;
				} else if (bound == upper) {
					upperClosed &= closed;
				}
			} else if (bound > lower) {
				lower = bound;
				lowerClosed = closed;
			} else if (bound == lower) {
				lowerClosed &= closed;
			}
			if (lower > upper || lower == upper && !(lowerClosed && upperClosed)) {
				return NoSolution.INSTANCE;
			}
		}

		final MathSolution clipped = new AffineIntervalSolution(line, lower, upper, lowerClosed, upperClosed);
		if (remaining.isEmpty()) {
			return clipped;
		}
		return new ConstrainedSolution(clipped, remaining);
	}
}
