package lu.kbra.pclib.geo;

import java.util.List;

import org.joml.Vector3d;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import lu.kbra.pclib.geo.equation.constraint.LinearConstraint3d;
import lu.kbra.pclib.geo.equation.constraint.Relation;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem3dc;
import lu.kbra.pclib.geo.equation.implicit.LinearEquation3d;
import lu.kbra.pclib.geo.equation.implicit.QuadraticEquation3d;
import lu.kbra.pclib.geo.equation.parametric.ParameterDomain1d;
import lu.kbra.pclib.geo.equation.solver.AffineIntervalSolution;
import lu.kbra.pclib.geo.equation.solver.AffineSolution;
import lu.kbra.pclib.geo.equation.solver.EquationSystemSolver;
import lu.kbra.pclib.geo.equation.solver.FiniteSolutions;
import lu.kbra.pclib.geo.equation.solver.LinearSystemSolver;
import lu.kbra.pclib.geo.equation.solver.NoSolution;
import lu.kbra.pclib.geo.equation.solver.PolynomialSystemSolver;
import lu.kbra.pclib.geo.equation.solver.QuadraticLocusSolution;
import lu.kbra.pclib.geo.equation.solver.UnresolvedSolution;
import lu.kbra.pclib.geo.math.GeometryPrecision;
import lu.kbra.pclib.geo.three.curve.Ray3d;
import lu.kbra.pclib.geo.three.curve.Segment3d;

/** Only mathematical results are produced here; no geometry-type interpretation. */
class MathSolverTest {
	private static final double EPS = 1e-8;

	@Test
	void linearRankAndContradictions() {
		final var point = LinearSystemSolver.solve(new ImplicitSystem3dc(new LinearEquation3d(1, 0, 0, -2),
				new LinearEquation3d(0, 1, 0, -3),
				new LinearEquation3d(0, 0, 1, -4)));
		Assertions.assertInstanceOf(AffineSolution.class, point);
		Assertions.assertArrayEquals(new double[] { 2, 3, 4 }, ((AffineSolution) point).origin(), MathSolverTest.EPS);
		final var all = LinearSystemSolver.solve(new ImplicitSystem3dc());
		Assertions.assertEquals(3, ((AffineSolution) all).dimension());
		Assertions.assertSame(NoSolution.INSTANCE,
				LinearSystemSolver.solve(new ImplicitSystem3dc(new LinearEquation3d(1, 0, 0, -1), new LinearEquation3d(1, 0, 0, -2))));
	}

	@Test
	void secantTangentAndDisjointQuadraticLine() {
		final var sphere = QuadraticEquation3d.sphere(0, 0, 0, 3);
		final var zAxis = new LinearEquation3d[] { new LinearEquation3d(1, 0, 0, 0), new LinearEquation3d(0, 1, 0, 0) };
		final var secant = PolynomialSystemSolver.solve(new ImplicitSystem3dc(List.of(zAxis[0], zAxis[1], sphere)));
		Assertions.assertInstanceOf(FiniteSolutions.class, secant);
		Assertions.assertEquals(2, ((FiniteSolutions) secant).points().size());
		final var tangent = PolynomialSystemSolver
				.solve(new ImplicitSystem3dc(new LinearEquation3d(0, 0, 1, -3), new LinearEquation3d(0, 1, 0, 0), sphere));
		Assertions.assertInstanceOf(FiniteSolutions.class, tangent);
		Assertions.assertEquals(1, ((FiniteSolutions) tangent).points().size());
		Assertions.assertSame(NoSolution.INSTANCE,
				PolynomialSystemSolver
						.solve(new ImplicitSystem3dc(new LinearEquation3d(0, 0, 1, -4), new LinearEquation3d(0, 1, 0, 0), sphere)));
	}

	@Test
	void arbitraryQuadraticLocusIsNotMisclassified() {
		final var surface = PolynomialSystemSolver
				.solve(new ImplicitSystem3dc(new LinearEquation3d(0, 0, 1, -1), QuadraticEquation3d.sphere(0, 0, 0, 4)));
		Assertions.assertInstanceOf(QuadraticLocusSolution.class, surface);
		Assertions.assertEquals(2, ((QuadraticLocusSolution) surface).parameterDimension());
		final var unresolved = PolynomialSystemSolver
				.solve(new ImplicitSystem3dc(QuadraticEquation3d.sphere(0, 0, 0, 3), QuadraticEquation3d.sphere(1, 0, 0, 3)));
		Assertions.assertInstanceOf(UnresolvedSolution.class, unresolved);
	}

	@Test
	void lineIntervalsRespectClosedAndOpenConstraints() {
		final var ray = new Ray3d(new Vector3d(0, 0, 0), new Vector3d(1, 0, 0));
		final var segment = new Segment3d(new Vector3d(-1, 0, 0), new Vector3d(4, 0, 0));
		final var solution = EquationSystemSolver.intersectionRepresentations(ray, segment, GeometryPrecision.DEFAULT);
		Assertions.assertInstanceOf(AffineIntervalSolution.class, solution);
		final var interval = (AffineIntervalSolution) solution;
		Assertions.assertTrue(interval.includeMin());
		Assertions.assertTrue(interval.includeMax());
		Assertions.assertEquals(4, Math.abs(interval.max() - interval.min()), MathSolverTest.EPS);
		final var strict = new LinearConstraint3d(new LinearEquation3d(1, 0, 0, -1), Relation.GT);
		Assertions.assertFalse(strict.accepts(new Vector3d(1, 0, 0), GeometryPrecision.DEFAULT));
		Assertions.assertTrue(strict.accepts(new Vector3d(2, 0, 0), GeometryPrecision.DEFAULT));
	}

	@Test
	void signedParameterDomainsAreDistinct() {
		Assertions.assertTrue(ParameterDomain1d.POSITIVE.contains(2, 0));
		Assertions.assertFalse(ParameterDomain1d.POSITIVE.contains(-2, 0));
		Assertions.assertTrue(ParameterDomain1d.NEGATIVE.contains(-2, 0));
		Assertions.assertFalse(ParameterDomain1d.NEGATIVE.contains(2, 0));
	}
}
