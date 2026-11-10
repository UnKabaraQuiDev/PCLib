package lu.kbra.pclib.geo;

import java.util.List;

import org.joml.Vector2d;
import org.joml.Vector3d;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import lu.kbra.pclib.geo.collection.GeometryCollection3d;
import lu.kbra.pclib.geo.collection.MultiVertex2d;
import lu.kbra.pclib.geo.csg.CSGSolid3d;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem3dc;
import lu.kbra.pclib.geo.equation.implicit.LinearEquation3d;
import lu.kbra.pclib.geo.equation.solver.AffineIntervalSolution;
import lu.kbra.pclib.geo.equation.solver.AffineSolution;
import lu.kbra.pclib.geo.equation.solver.EquationSystemSolver;
import lu.kbra.pclib.geo.equation.solver.FiniteSolutions;
import lu.kbra.pclib.geo.equation.solver.LinearSystemSolver;
import lu.kbra.pclib.geo.equation.solver.NoSolution;
import lu.kbra.pclib.geo.equation.solver.QuadraticLocusSolution;
import lu.kbra.pclib.geo.math.Axis2d;
import lu.kbra.pclib.geo.math.Axis3d;
import lu.kbra.pclib.geo.math.GeometryPrecision;
import lu.kbra.pclib.geo.math.LCS3d;
import lu.kbra.pclib.geo.math.Vertex2d;
import lu.kbra.pclib.geo.math.Vertex3d;
import lu.kbra.pclib.geo.three.curve.BSplineCurve3d;
import lu.kbra.pclib.geo.three.curve.BezierCurve3d;
import lu.kbra.pclib.geo.three.curve.Line3d;
import lu.kbra.pclib.geo.three.curve.Ray3d;
import lu.kbra.pclib.geo.three.curve.Segment3d;
import lu.kbra.pclib.geo.three.solid.Ball3d;
import lu.kbra.pclib.geo.three.solid.Box3d;
import lu.kbra.pclib.geo.three.solid.CylinderSolid3d;
import lu.kbra.pclib.geo.three.solid.TorusSolid3d;
import lu.kbra.pclib.geo.three.surface.BSplineSurface3d;
import lu.kbra.pclib.geo.three.surface.BezierSurface3d;
import lu.kbra.pclib.geo.three.surface.CylinderSurface3d;
import lu.kbra.pclib.geo.three.surface.Plane3d;
import lu.kbra.pclib.geo.three.surface.Sphere3d;
import lu.kbra.pclib.geo.three.surface.TorusSurface3d;
import lu.kbra.pclib.geo.topology.TopoCompound3d;
import lu.kbra.pclib.geo.topology.TopoEdge3d;
import lu.kbra.pclib.geo.topology.TopoEdgeUse3d;
import lu.kbra.pclib.geo.topology.TopoFace3d;
import lu.kbra.pclib.geo.topology.TopoShell3d;
import lu.kbra.pclib.geo.topology.TopoSolid3d;
import lu.kbra.pclib.geo.topology.TopoVertex3d;
import lu.kbra.pclib.geo.topology.TopoWire3d;
import lu.kbra.pclib.geo.topology.TopologyKind;
import lu.kbra.pclib.geo.two.curve.Line2d;
import lu.kbra.pclib.geo.two.curve.NurbsCurve2d;
import lu.kbra.pclib.geo.two.curve.TanhCurve2d;
import lu.kbra.pclib.geo.two.region.Disk2d;
import lu.kbra.pclib.geo.two.region.Polygon2d;

class GeometryCoreTest {
	private static final double EPS = 1e-9;

	@Test
	void linearSolverClassifiesAffineDimensions() {
		final var point = LinearSystemSolver.solve(new ImplicitSystem3dc(new LinearEquation3d(1, 0, 0, -1),
				new LinearEquation3d(0, 1, 0, -2),
				new LinearEquation3d(0, 0, 1, -3)));
		Assertions.assertInstanceOf(AffineSolution.class, point);
		Assertions.assertEquals(0, ((AffineSolution) point).dimension());
		Assertions.assertArrayEquals(new double[] { 1, 2, 3 }, ((AffineSolution) point).origin(), GeometryCoreTest.EPS);
		final var line = LinearSystemSolver
				.solve(new ImplicitSystem3dc(new LinearEquation3d(1, 0, 0, -1), new LinearEquation3d(0, 1, 0, -2)));
		Assertions.assertEquals(1, ((AffineSolution) line).dimension());
		final var plane = LinearSystemSolver.solve(new ImplicitSystem3dc(new LinearEquation3d(0, 0, 1, -3)));
		Assertions.assertEquals(2, ((AffineSolution) plane).dimension());
		Assertions.assertSame(NoSolution.INSTANCE,
				LinearSystemSolver.solve(new ImplicitSystem3dc(new LinearEquation3d(0, 0, 1, 0), new LinearEquation3d(0, 0, 1, -1))));
	}

	@Test
	void linePlaneIntersectionAndProjection() {
		final var plane = new Plane3d(new Vector3d(), new Vector3d(0, 0, 1));
		final var line = new Line3d(new Vector3d(1, 2, 3), new Vector3d(0, 0, -1));
		Assertions.assertTrue(line.intersection(plane).isSecond());
		Assertions.assertEquals(0, line.intersection(plane).getSecond().z(), GeometryCoreTest.EPS);
		Assertions.assertTrue(line.projectOnto(plane).isSecond());
		final var coincident = new Line3d(new Vector3d(0, 0, 0), new Vector3d(1, 0, 0));
		Assertions.assertTrue(coincident.intersection(plane).isFirst());
		Assertions.assertTrue(coincident.projectOnto(plane).isFirst());
		Assertions.assertThrows(IllegalArgumentException.class, () -> coincident.projectOnto(plane, new Axis3d(1, 0, 0)));
	}

	@Test
	void planesAndBoundedCurves() {
		final var xy = new Plane3d(new Vector3d(), new Vector3d(0, 0, 1));
		final var yz = new Plane3d(new Vector3d(), new Vector3d(1, 0, 0));
		Assertions.assertTrue(xy.intersection(yz).isSecond());
		Assertions.assertTrue(xy.intersection(new Plane3d(new Vector3d(0, 0, 1), new Vector3d(0, 0, 1))).isThird());
		Assertions.assertTrue(new Segment3d(new Vector3d(0, 0, 1), new Vector3d(0, 0, -1)).intersection(xy).isSecond());
		Assertions.assertTrue(new Ray3d(new Vector3d(0, 0, 1), new Vector3d(0, 0, 1)).intersection(xy).isThird());
	}

	@Test
	void sphereAndBallRespectFill() {
		final var sphere = new Sphere3d(new Vector3d(), 5);
		final var ball = new Ball3d(new Vector3d(), 5);
		final var plane = new Plane3d(new Vector3d(0, 0, 3), new Vector3d(0, 0, 1));
		Assertions.assertEquals(4, sphere.intersection(plane).getFirst().radius(), GeometryCoreTest.EPS);
		Assertions.assertEquals(4, ball.intersection(plane).getFirst().radius(), GeometryCoreTest.EPS);
		Assertions.assertTrue(sphere.intersection(new Plane3d(new Vector3d(0, 0, 5), new Vector3d(0, 0, 1))).isSecond());
		Assertions.assertTrue(ball.contains(new Vector3d(0, 0, 0)));
		Assertions.assertFalse(ball.contains(new Vector3d(6, 0, 0)));
	}

	@Test
	void twoDimensionalPrimitivesAndReflection() {
		final var horizontal = new Line2d(new Vertex2d(), new Axis2d(1, 0));
		final var vertical = new Line2d(new Vertex2d(), new Axis2d(0, 1));
		Assertions.assertTrue(horizontal.intersection(vertical).isSecond());
		Assertions.assertEquals(-3, new Vertex2d(2, 3).reflectedAround(horizontal).y(), GeometryCoreTest.EPS);
		Assertions.assertEquals(2,
				new TanhCurve2d(new Vector2d(), new Vector2d(1, 0), new Vector2d(0, 1), 1, 0).at(2).x(),
				GeometryCoreTest.EPS);
		Assertions.assertTrue(new Disk2d(new Vector2d(), 2).contains(new Vector2d(1, 1)));
	}

	@Test
	void localFramesRoundTrip() {
		final var frame = new LCS3d(new Vector3d(3, 2, -1), new Vector3d(1, 1, 1));
		final var point = frame.toWorld(1, 2, 3, new Vertex3d());
		final var local = frame.toLocal(point, new Vertex3d());
		Assertions.assertEquals(1, local.x, GeometryCoreTest.EPS);
		Assertions.assertEquals(2, local.y, GeometryCoreTest.EPS);
		Assertions.assertEquals(3, local.z, GeometryCoreTest.EPS);
	}

	@Test
	void lazyBooleanOperations() {
		final var ball = new Ball3d(new Vector3d(), 2);
		final var box = new Box3d(new Vector3d(-1, -1, -1), new Vector3d(1, 1, 1));
		Assertions.assertTrue(CSGSolid3d.union(ball, box).contains(new Vector3d(1.5, 0, 0)));
		Assertions.assertFalse(CSGSolid3d.difference(ball, box).contains(new Vector3d(0, 0, 0)));
		Assertions.assertTrue(CSGSolid3d.difference(ball, box).contains(new Vector3d(1.5, 0, 0)));
	}

	@Test
	void polygonMembershipAndHoles() {
		final var exterior = List.of(new Vector2d(0, 0), new Vector2d(4, 0), new Vector2d(4, 4), new Vector2d(0, 4));
		final var hole = List.of(new Vector2d(1, 1), new Vector2d(3, 1), new Vector2d(3, 3), new Vector2d(1, 3));
		final var polygon = new Polygon2d(exterior, List.of(hole));
		Assertions.assertTrue(polygon.contains(new Vector2d(0.5, 0.5)));
		Assertions.assertFalse(polygon.contains(new Vector2d(2, 2)));
		Assertions.assertTrue(polygon.contains(new Vector2d(1, 2)));
		Assertions.assertFalse(polygon.contains(new Vector2d(5, 5)));
	}

	@Test
	void polynomialSolverKeepsMathematicsSeparateFromGeometry() {
		final var sphere = new Sphere3d(new Vector3d(), 5);
		final var secant = new ImplicitSystem3dc(new LinearEquation3d(0, 0, 1, -3));
		final var tangent = new ImplicitSystem3dc(new LinearEquation3d(0, 0, 1, -5));
		final var disjoint = new ImplicitSystem3dc(new LinearEquation3d(0, 0, 1, -6));
		final var section = EquationSystemSolver.intersection(sphere.implicitEquation(), secant);
		Assertions.assertInstanceOf(QuadraticLocusSolution.class, section);
		Assertions.assertEquals(2, ((QuadraticLocusSolution) section).parameterDimension());
		final var touching = EquationSystemSolver.intersection(sphere.implicitEquation(), tangent);
		Assertions.assertInstanceOf(FiniteSolutions.class, touching);
		Assertions.assertEquals(1, ((FiniteSolutions) touching).points().size());
		Assertions.assertSame(NoSolution.INSTANCE, EquationSystemSolver.intersection(sphere.implicitEquation(), disjoint));
	}

	@Test
	void quadraticRestrictionOfLineFindsTwoPoints() {
		final var sphere = new Sphere3d(new Vector3d(), 5);
		final var axis = new ImplicitSystem3dc(new LinearEquation3d(1, 0, 0, 0), new LinearEquation3d(0, 1, 0, 0));
		final var answer = EquationSystemSolver.intersection(sphere.implicitEquation(), axis);
		Assertions.assertInstanceOf(FiniteSolutions.class, answer);
		Assertions.assertEquals(2, ((FiniteSolutions) answer).points().size());
		Assertions.assertEquals(-5, ((FiniteSolutions) answer).points().get(0)[2], GeometryCoreTest.EPS);
		Assertions.assertEquals(5, ((FiniteSolutions) answer).points().get(1)[2], GeometryCoreTest.EPS);
	}

	@Test
	void constrainedLinearIntersectionsProduceMathematicalIntervals() {
		final var ray = new Ray3d(new Vector3d(0, 0, 0), new Vector3d(1, 0, 0));
		final var segment = new Segment3d(new Vector3d(-2, 0, 0), new Vector3d(2, 0, 0));
		final var mathematical = EquationSystemSolver.intersectionRepresentations(ray, segment, GeometryPrecision.DEFAULT);
		Assertions.assertInstanceOf(AffineIntervalSolution.class, mathematical);
		final var clipped = (AffineIntervalSolution) mathematical;
		Assertions.assertFalse(clipped.isUnbounded());
		Assertions.assertEquals(0, clipped.at(clipped.min())[1], GeometryCoreTest.EPS);
		Assertions.assertThrows(IllegalArgumentException.class, () -> clipped.at(clipped.min() - 1));
		Assertions.assertTrue(clipped.includeMin());
		Assertions.assertTrue(clipped.includeMax());
		final var typed = ray.intersection(segment);
		Assertions.assertTrue(typed.isFirst());
		Assertions.assertEquals(2, new Vector3d(typed.getFirst().end()).distance(typed.getFirst().start()), GeometryCoreTest.EPS);
		final var touching = new Segment3d(new Vector3d(-1, 0, 0), new Vector3d(0, 0, 0));
		Assertions.assertTrue(ray.intersection(touching).isSecond());
		final var missing = new Segment3d(new Vector3d(-3, 0, 0), new Vector3d(-1, 0, 0));
		Assertions.assertTrue(ray.intersection(missing).isThird());
	}

	@Test
	void bsplineMatchesBezierWithClampedCubicKnots() {
		final double[] knots = { 0, 0, 0, 0, 1, 1, 1, 1 };
		final var poles = List.of(new Vector3d(0, 0, 0), new Vector3d(1, 2, 0), new Vector3d(2, 2, 0), new Vector3d(3, 0, 0));
		final var spline = new BSplineCurve3d(3, knots, poles);
		Assertions.assertEquals(0, spline.at(0).x(), GeometryCoreTest.EPS);
		Assertions.assertEquals(3, spline.at(1).x(), GeometryCoreTest.EPS);
		Assertions.assertEquals(1.5, spline.at(0.5).x(), GeometryCoreTest.EPS);
		Assertions.assertEquals(1.5, spline.at(0.5).y(), GeometryCoreTest.EPS);
		Assertions.assertThrows(IllegalArgumentException.class, () -> spline.at(-0.1));
	}

	@Test
	void nurbsCanRepresentQuarterCircleExactly() {
		final double weight = 1 / Math.sqrt(2);
		final var curve = new NurbsCurve2d(2,
				new double[] { 0, 0, 0, 1, 1, 1 },
				List.of(new Vector2d(1, 0), new Vector2d(1, 1), new Vector2d(0, 1)),
				new double[] { 1, weight, 1 });
		final var middle = curve.at(0.5);
		Assertions.assertEquals(Math.sqrt(0.5), middle.x, GeometryCoreTest.EPS);
		Assertions.assertEquals(Math.sqrt(0.5), middle.y, GeometryCoreTest.EPS);
		Assertions.assertEquals(1, middle.length(), GeometryCoreTest.EPS);
	}

	@Test
	void bezierDerivativeAndSurfaceNormal() {
		final var curve = new BezierCurve3d(List.of(new Vector3d(0, 0, 0), new Vector3d(1, 2, 0), new Vector3d(2, 0, 0)));
		Assertions.assertEquals(1, curve.at(0.5).x(), GeometryCoreTest.EPS);
		Assertions.assertEquals(1, curve.at(0.5).y(), GeometryCoreTest.EPS);
		Assertions.assertEquals(2, curve.derivativeAt(0, new Vector3d()).x, GeometryCoreTest.EPS);

		final Vector3d[][] net = { { new Vector3d(0, 0, 0), new Vector3d(0, 1, 0) }, { new Vector3d(1, 0, 0), new Vector3d(1, 1, 0) } };
		final var surface = new BezierSurface3d(net);
		Assertions.assertEquals(0.25, surface.at(0.25, 0.75).x, GeometryCoreTest.EPS);
		Assertions.assertEquals(0.75, surface.at(0.25, 0.75).y, GeometryCoreTest.EPS);
		Assertions.assertEquals(1, surface.normalAt(0.25, 0.75).z, GeometryCoreTest.EPS);
	}

	@Test
	void nurbsSurfaceBilinearPatch() {
		final Vector3d[][] net = { { new Vector3d(0, 0, 0), new Vector3d(0, 1, 0) }, { new Vector3d(1, 0, 0), new Vector3d(1, 1, 1) } };
		final var patch = new BSplineSurface3d(1, 1, new double[] { 0, 0, 1, 1 }, new double[] { 0, 0, 1, 1 }, net);
		final var point = patch.at(0.25, 0.75);
		Assertions.assertEquals(0.25, point.x, GeometryCoreTest.EPS);
		Assertions.assertEquals(0.75, point.y, GeometryCoreTest.EPS);
		Assertions.assertEquals(0.1875, point.z, GeometryCoreTest.EPS);
	}

	@Test
	void topologySupportsSharedOrientedEdgesAndClosedShells() {
		final var a = new TopoVertex3d(new Vector3d(0, 0, 0));
		final var b = new TopoVertex3d(new Vector3d(1, 0, 0));
		final var c = new TopoVertex3d(new Vector3d(0, 1, 0));
		final var d = new TopoVertex3d(new Vector3d(0, 0, 1));
		final var ab = GeometryCoreTest.edge(a, b);
		final var ac = GeometryCoreTest.edge(a, c);
		final var ad = GeometryCoreTest.edge(a, d);
		final var bc = GeometryCoreTest.edge(b, c);
		final var bd = GeometryCoreTest.edge(b, d);
		final var cd = GeometryCoreTest.edge(c, d);
		final var bottom = new TopoFace3d(new Plane3d(a.position(), new Vector3d(0, 0, -1)),
				GeometryCoreTest.wire(new TopoEdgeUse3d(ac), new TopoEdgeUse3d(bc, true), new TopoEdgeUse3d(ab, true)),
				List.of());
		final var sideA = new TopoFace3d(new Plane3d(a.position(), new Vector3d(0, -1, 0)),
				GeometryCoreTest.wire(new TopoEdgeUse3d(ab), new TopoEdgeUse3d(bd), new TopoEdgeUse3d(ad, true)),
				List.of());
		final var sideB = new TopoFace3d(new Plane3d(b.position(), new Vector3d(1, 1, 1)),
				GeometryCoreTest.wire(new TopoEdgeUse3d(bc), new TopoEdgeUse3d(cd), new TopoEdgeUse3d(bd, true)),
				List.of());
		final var sideC = new TopoFace3d(new Plane3d(c.position(), new Vector3d(-1, 0, 0)),
				GeometryCoreTest.wire(new TopoEdgeUse3d(ac, true), new TopoEdgeUse3d(ad), new TopoEdgeUse3d(cd, true)),
				List.of());
		final var shell = new TopoShell3d(List.of(bottom, sideA, sideB, sideC));
		Assertions.assertTrue(shell.isClosedTopologically());
		Assertions.assertFalse(new TopoShell3d(List.of(bottom, sideA, sideB)).isClosedTopologically());
		Assertions.assertEquals(4, new TopoSolid3d(shell, List.of()).exterior().faces().size());
		Assertions.assertTrue(bottom.outer().reversed().isClosed());
		Assertions.assertSame(bottom.outer().end(), bottom.outer().start());
		Assertions.assertThrows(IllegalArgumentException.class, () -> new TopoSolid3d(new TopoShell3d(List.of(bottom)), List.of()));
		Assertions.assertThrows(IllegalArgumentException.class,
				() -> new TopoEdge3d(new Segment3d(a.position(), b.position()), 0, 1, a, c));
	}

	@Test
	void mixedDimensionGeometryCollectionsPreserveComponents() {
		final var line = new Line3d(new Vector3d(), new Vector3d(1, 0, 0));
		final var point = new Vertex3d(1, 2, 3);
		final var collection = new GeometryCollection3d<>(List.of(line, point));
		Assertions.assertEquals(1, collection.intrinsicDimension());
		Assertions.assertFalse(collection.isBounded());
		Assertions.assertEquals(2, collection.components().size());
		Assertions.assertEquals(0, new MultiVertex2d(List.of(new Vertex2d(1, 2))).intrinsicDimension());
		final var compound = new TopoCompound3d(List.of(new TopoVertex3d(point)));
		Assertions.assertSame(TopologyKind.COMPOUND, compound.topologyKind());
	}

	@Test
	void parametricTangentsAndLocalFramesRespectChartDomains() {
		final var curve = new BezierCurve3d(List.of(new Vector3d(0, 0, 0), new Vector3d(3, 0, 0)));
		Assertions.assertEquals(1, curve.tangentAt(0).x, GeometryCoreTest.EPS);
		Assertions.assertEquals(3, curve.localCoordinateSystemAt(1).origin().x(), GeometryCoreTest.EPS);
		final Vector3d[][] net = { { new Vector3d(0, 0, 0), new Vector3d(0, 1, 0) }, { new Vector3d(1, 0, 0), new Vector3d(1, 1, 0) } };
		final var surface = new BezierSurface3d(net);
		Assertions.assertEquals(1, surface.tangentAt(0.5, 0.5).x, GeometryCoreTest.EPS);
		Assertions.assertEquals(1, surface.cotangentAt(0.5, 0.5).y, GeometryCoreTest.EPS);
		Assertions.assertEquals(1, surface.localCoordinateSystemAt(0.5, 0.5).zAxis().z(), GeometryCoreTest.EPS);
		Assertions.assertThrows(IllegalArgumentException.class, () -> surface.partialUAt(-0.1, 0, new Vector3d()));
	}

	@Test
	void finiteCylinderHasQuadraticShellAndBoundedSolid() {
		final var shell = new CylinderSurface3d(new Vector3d(1, 2, 3), new Vector3d(0, 0, 1), 2, 4);
		final var p = shell.at(0, 1);
		Assertions.assertEquals(3, p.x(), GeometryCoreTest.EPS);
		Assertions.assertEquals(4, p.z(), GeometryCoreTest.EPS);
		Assertions.assertEquals(0, shell.radialEquation().value(p), GeometryCoreTest.EPS);
		Assertions.assertEquals(1, shell.normalAt(0, 1).x(), GeometryCoreTest.EPS);
		final var solid = new CylinderSolid3d(new Vector3d(1, 2, 3), new Vector3d(0, 0, 1), 2, 4);
		Assertions.assertTrue(solid.contains(new Vector3d(1, 2, 3)));
		Assertions.assertTrue(solid.contains(new Vector3d(3, 2, 4)));
		Assertions.assertFalse(solid.contains(new Vector3d(1, 2, 8)));
		Assertions.assertFalse(solid.contains(new Vector3d(4, 2, 4)));
		Assertions.assertEquals(0, solid.at(0, 1, 0).distance(new Vector3d(1, 2, 4)), GeometryCoreTest.EPS);
		Assertions.assertThrows(IllegalArgumentException.class, () -> solid.at(0, 1, 3));
	}

	@Test
	void torusQuarticMatchesParametricShellAndSolidMembership() {
		final var torus = new TorusSurface3d(new Vector3d(), new Vector3d(0, 0, 1), 3, 1);
		final var p = torus.at(0, 0);
		Assertions.assertEquals(4, p.x(), GeometryCoreTest.EPS);
		Assertions.assertEquals(0, torus.quarticEquation().value(p), GeometryCoreTest.EPS);
		Assertions.assertEquals(1, torus.normalAt(0, 0).x, GeometryCoreTest.EPS);
		Assertions.assertEquals(0, torus.quarticEquation().value(torus.at(1.1, 2.5)), 1e-7);
		final var solid = new TorusSolid3d(new Vector3d(), new Vector3d(0, 0, 1), 3, 1);
		Assertions.assertTrue(solid.contains(new Vector3d(3, 0, 0)));
		Assertions.assertTrue(solid.contains(new Vector3d(4, 0, 0)));
		Assertions.assertFalse(solid.contains(new Vector3d(0, 0, 0)));
		Assertions.assertFalse(solid.contains(new Vector3d(5, 0, 0)));
		Assertions.assertEquals(3, solid.at(0, 0, 0).x(), GeometryCoreTest.EPS);
	}

	private static TopoEdge3d edge(final TopoVertex3d start, final TopoVertex3d end) {
		return new TopoEdge3d(new Segment3d(start.position(), end.position()), 0, 1, start, end);
	}

	private static TopoWire3d wire(final TopoEdgeUse3d... uses) {
		return new TopoWire3d(List.of(uses));
	}
}
