package lu.kbra.pclib.geo.equation.solver;

import java.util.ArrayList;
import java.util.List;

import lu.kbra.pclib.geo.equation.implicit.ImplicitEquation3dc;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem3dc;
import lu.kbra.pclib.geo.equation.implicit.LinearEquation3d;
import lu.kbra.pclib.geo.equation.implicit.QuadraticEquation3d;
import lu.kbra.pclib.geo.math.GeometryPrecision;

/**
 * Solves linear equalities plus at most one quadratic equality in R^3. Higher-dimensional quadratic
 * loci are kept as equations in affine coordinates. Other polynomial systems report
 * UnresolvedSolution, not an invented answer.
 */
public final class PolynomialSystemSolver {
	private PolynomialSystemSolver() {
	}

	public static MathSolution solve(final ImplicitSystem3dc system) {
		return PolynomialSystemSolver.solve(system, GeometryPrecision.DEFAULT);
	}

	public static MathSolution solve(final ImplicitSystem3dc system, final GeometryPrecision precision) {
		final List<ImplicitEquation3dc> linear = new ArrayList<>();
		QuadraticEquation3d quadratic = null;
		for (final ImplicitEquation3dc equation : system.equations()) {
			if (equation instanceof LinearEquation3d) {
				linear.add(equation);
			} else if (equation instanceof final QuadraticEquation3d q) {
				if (quadratic != null) {
					return new UnresolvedSolution("Multiple quadratic equations are not yet supported");
				}
				quadratic = q;
			} else {
				return new UnresolvedSolution("Unsupported implicit equation representation");
			}
		}
		final MathSolution linearSolution = LinearSystemSolver.solve(new ImplicitSystem3dc(linear), precision);
		if (quadratic == null || linearSolution instanceof NoSolution || !(linearSolution instanceof final AffineSolution affine)) {
			return linearSolution;
		}
		return PolynomialSystemSolver.reduceQuadratic(quadratic, affine, precision);
	}

	private static MathSolution
			reduceQuadratic(final QuadraticEquation3d equation, final AffineSolution affine, final GeometryPrecision precision) {
		final double[] base = affine.origin();
		final double[][] vectors = affine.basis();
		final int count = vectors.length;
		final double[][] q = equation.quadraticMatrix();
		final double[] l = equation.linearCoefficients();
		final double constant = equation.value(base[0], base[1], base[2]);
		if (count == 0) {
			return Math.abs(constant) <= precision.distance() ? affine : NoSolution.INSTANCE;
		}

		final double[] gradient = l.clone();
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				gradient[i] += 2 * q[i][j] * base[j];
			}
		}

		final double[][] reduced = new double[count][count];
		final double[] reducedLinear = new double[count];
		for (int i = 0; i < count; i++) {
			for (int x = 0; x < 3; x++) {
				reducedLinear[i] += vectors[i][x] * gradient[x];
			}
			for (int j = 0; j < count; j++) {
				for (int x = 0; x < 3; x++) {
					for (int y = 0; y < 3; y++) {
						reduced[i][j] += vectors[i][x] * q[x][y] * vectors[j][y];
					}
				}
			}
		}

		if (count == 1) {
			return PolynomialSystemSolver.solveUnivariate(affine, reduced[0][0], reducedLinear[0], constant, precision);
		}
		if (count == 2) {
			final MathSolution classified = PolynomialSystemSolver.classifyDefinite2d(affine, reduced, reducedLinear, constant, precision);
			if (classified != null) {
				return classified;
			}
		}
		return new QuadraticLocusSolution(affine, reduced, reducedLinear, constant);
	}

	/** Recognizes empty and singleton real loci of definite binary quadratics. */
	private static MathSolution classifyDefinite2d(
			final AffineSolution affine,
			final double[][] q,
			final double[] linear,
			final double constant,
			final GeometryPrecision precision) {
		final double a = q[0][0], b = q[0][1], c = q[1][1];
		final double determinant = a * c - b * b;
		final double scale = Math.max(1, Math.max(Math.abs(a), Math.max(Math.abs(b), Math.abs(c))));
		final double tolerance = precision.relative() * scale * scale + precision.distance() * 1e-4;
		if (determinant <= tolerance || Math.abs(a) <= tolerance) {
			return null;
		}
		final double t0 = (-c * linear[0] + b * linear[1]) / (2 * determinant);
		final double t1 = (b * linear[0] - a * linear[1]) / (2 * determinant);
		final double extremum = a * t0 * t0 + 2 * b * t0 * t1 + c * t1 * t1 + linear[0] * t0 + linear[1] * t1 + constant;
		final double zeroTolerance = precision.distance() * Math.max(1, Math.abs(constant));
		if (a > 0 && extremum > zeroTolerance || a < 0 && extremum < -zeroTolerance) {
			return NoSolution.INSTANCE;
		}
		if (Math.abs(extremum) <= zeroTolerance) {
			return new FiniteSolutions(List.of(affine.at(t0, t1)));
		}
		return null;
	}

	private static MathSolution solveUnivariate(
			final AffineSolution affine,
			final double a,
			final double b,
			final double c,
			final GeometryPrecision precision) {
		final double tolerance = precision.distance();
		if (Math.abs(a) <= tolerance) {
			if (Math.abs(b) <= tolerance) {
				return Math.abs(c) <= tolerance ? affine : NoSolution.INSTANCE;
			}
			return new FiniteSolutions(List.of(affine.at(-c / b)));
		}
		final double discriminant = b * b - 4 * a * c;
		final double discTolerance = tolerance * Math.max(1, Math.abs(b * b) + Math.abs(4 * a * c));
		if (discriminant < -discTolerance) {
			return NoSolution.INSTANCE;
		}
		if (Math.abs(discriminant) <= discTolerance) {
			return new FiniteSolutions(List.of(affine.at(-b / (2 * a))));
		}
		final double root = Math.sqrt(Math.max(0, discriminant));
		final double q = -0.5 * (b + Math.copySign(root, b == 0 ? 1 : b));
		double t1 = q / a, t2 = c / q;
		if (t1 > t2) {
			final double swap = t1;
			t1 = t2;
			t2 = swap;
		}
		return new FiniteSolutions(List.of(affine.at(t1), affine.at(t2)));
	}
}
