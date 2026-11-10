package lu.kbra.pclib.geo;

import org.joml.Vector3d;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem3dc;
import lu.kbra.pclib.geo.equation.implicit.LinearEquation3d;
import lu.kbra.pclib.geo.equation.solver.EquationSystemSolver;
import lu.kbra.pclib.geo.equation.solver.QuadraticLocusSolution;
import lu.kbra.pclib.geo.three.solid.ConeSolid3d;
import lu.kbra.pclib.geo.three.surface.ConeSurface3d;

class AdvancedSolids3dTest {
	private static final double EPS = 1e-8;

	@Test
	void coneQuadraticMatchesParametricSurface() {
		final var cone = new ConeSurface3d(new Vector3d(1, -2, 3), new Vector3d(0, 0, 1), 2, 4);
		Assertions.assertEquals(0, cone.quadraticEquation().value(cone.at(0, 0)), AdvancedSolids3dTest.EPS);
		Assertions.assertEquals(0, cone.quadraticEquation().value(cone.at(Math.PI, 2)), AdvancedSolids3dTest.EPS);
		Assertions.assertEquals(7, cone.at(0, 4).z, AdvancedSolids3dTest.EPS);
		Assertions.assertThrows(IllegalArgumentException.class, () -> cone.normalAt(0, 4));
		Assertions.assertTrue(cone.normalAt(0, 0).z > 0);
		final var locus = EquationSystemSolver.intersection(cone.implicitEquation(),
				new ImplicitSystem3dc(new LinearEquation3d(0, 0, 1, -5)));
		Assertions.assertInstanceOf(QuadraticLocusSolution.class, locus);
	}

	@Test
	void filledConeEnforcesAxialAndRadialBounds() {
		final var cone = new ConeSolid3d(new Vector3d(0, 0, 0), new Vector3d(0, 0, 1), 2, 4);
		Assertions.assertTrue(cone.contains(new Vector3d(0, 0, 0)));
		Assertions.assertTrue(cone.contains(new Vector3d(0, 0, 4)));
		Assertions.assertTrue(cone.contains(new Vector3d(1, 0, 2)));
		Assertions.assertFalse(cone.contains(new Vector3d(2, 0, 2)));
		Assertions.assertFalse(cone.contains(new Vector3d(0, 0, 5)));
		Assertions.assertEquals(2, cone.at(0, 2, 1).z, AdvancedSolids3dTest.EPS);
		Assertions.assertThrows(IllegalArgumentException.class, () -> cone.at(0, 2, 2));
	}
}
