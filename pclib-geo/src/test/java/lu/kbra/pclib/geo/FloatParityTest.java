package lu.kbra.pclib.geo;

import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import lu.kbra.pclib.geo.csg.CSGSolid3f;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem2fc;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem3fc;
import lu.kbra.pclib.geo.equation.implicit.LinearEquation2f;
import lu.kbra.pclib.geo.equation.implicit.LinearEquation3f;
import lu.kbra.pclib.geo.equation.solver.AffineIntervalSolution;
import lu.kbra.pclib.geo.equation.solver.AffineSolution;
import lu.kbra.pclib.geo.equation.solver.EquationSystemSolver;
import lu.kbra.pclib.geo.math.Axis2f;
import lu.kbra.pclib.geo.math.Axis3f;
import lu.kbra.pclib.geo.math.GeometryPrecision;
import lu.kbra.pclib.geo.math.LCS2f;
import lu.kbra.pclib.geo.math.LCS3f;
import lu.kbra.pclib.geo.math.Vertex2f;
import lu.kbra.pclib.geo.math.Vertex2fc;
import lu.kbra.pclib.geo.math.Vertex3f;
import lu.kbra.pclib.geo.math.Vertex3fc;
import lu.kbra.pclib.geo.three.curve.Helix3f;
import lu.kbra.pclib.geo.three.curve.Line3f;
import lu.kbra.pclib.geo.three.curve.Ray3f;
import lu.kbra.pclib.geo.three.curve.Segment3f;
import lu.kbra.pclib.geo.three.solid.Ball3f;
import lu.kbra.pclib.geo.three.solid.Box3f;
import lu.kbra.pclib.geo.three.solid.ConeSolid3f;
import lu.kbra.pclib.geo.three.solid.CylinderSolid3f;
import lu.kbra.pclib.geo.three.solid.TorusSolid3f;
import lu.kbra.pclib.geo.three.surface.BSplineSurface3f;
import lu.kbra.pclib.geo.three.surface.BezierSurface3f;
import lu.kbra.pclib.geo.three.surface.ConeSurface3f;
import lu.kbra.pclib.geo.three.surface.CylinderSurface3f;
import lu.kbra.pclib.geo.three.surface.NurbsSurface3f;
import lu.kbra.pclib.geo.three.surface.Plane3f;
import lu.kbra.pclib.geo.three.surface.Sphere3f;
import lu.kbra.pclib.geo.three.surface.TorusSurface3f;
import lu.kbra.pclib.geo.topology.TopoEdge3f;
import lu.kbra.pclib.geo.topology.TopoVertex3f;
import lu.kbra.pclib.geo.two.curve.BezierCurve2f;
import lu.kbra.pclib.geo.two.curve.Catenary2f;
import lu.kbra.pclib.geo.two.curve.Circle2f;
import lu.kbra.pclib.geo.two.curve.Line2f;
import lu.kbra.pclib.geo.two.curve.LogarithmicSpiral2f;
import lu.kbra.pclib.geo.two.curve.NurbsCurve2f;
import lu.kbra.pclib.geo.two.curve.Parabola2f;
import lu.kbra.pclib.geo.two.curve.Segment2f;
import lu.kbra.pclib.geo.two.region.Disk2f;
import lu.kbra.pclib.geo.two.region.Polygon2f;

class FloatParityTest {
	private static final float EPS = 1e-4f;

	@Test
	void jomlFluentMethodsRetainFloatGeometryTypes() {
		Vertex2f p = new Vertex2f(1, 2).add(new Vector2f(2, 1)).mul(2).sub(1, 1);
		Assertions.assertEquals(5, p.x, EPS);
		Assertions.assertEquals(5, p.y, EPS);
		Vertex3f q = new Vertex3f(1, 2, 3).mul(2).sub(new Vector3f(1, 1, 1)).add(new Vector3f(1, 0, 0));
		Assertions.assertEquals(2, q.x, EPS);
		Vertex3f destination = ((Vertex3fc) q).sub(new Vector3f(1, 0, 0), new Vertex3f());
		Assertions.assertEquals(1, destination.x, EPS);
		Axis3f a = new Axis3f(1, 0, 0).rotateZ((float) Math.PI / 2);
		Assertions.assertEquals(1, a.y, EPS);
	}

	@Test
	void localFramesUseCopyAndDoNotAliasSources() {
		Vertex2f o = new Vertex2f(2, 3);
		LCS2f frame = new LCS2f((Vertex2fc) o, new Axis2f(0, 1));
		o.set(100, 100);
		Assertions.assertEquals(2, frame.origin().x(), EPS);
		Assertions.assertEquals(0, frame.toLocal(new Vertex2f(2, 3), new Vertex2f()).length(), EPS);
		LCS3f three = new LCS3f().setFromTangents(new Vector3f(3, 2, 1), new Vector3f(1, 0, 0), new Vector3f(0, 1, 0));
		Assertions.assertEquals(1, three.zAxis().z(), EPS);
		Assertions.assertEquals(0,
				three.toLocal(three.toWorld(0.5f, 1.5f, 2, new Vertex3f()), new Vertex3f()).distance(new Vertex3f(0.5f, 1.5f, 2)),
				EPS);
	}

	@Test
	void floatLinearSolverUsesPreciseMathModel() {
		var x = new ImplicitSystem2fc(new LinearEquation2f(1, 0, -2));
		var y = new ImplicitSystem2fc(new LinearEquation2f(0, 1, -3));
		var solution = (AffineSolution) EquationSystemSolver.intersection(x, y);
		Assertions.assertEquals(0, solution.dimension());
		Assertions.assertEquals(2, solution.origin()[0], EPS);
		Assertions.assertEquals(3, solution.origin()[1], EPS);
		var planeX = new ImplicitSystem3fc(new LinearEquation3f(1, 0, 0, -1));
		var planeY = new ImplicitSystem3fc(new LinearEquation3f(0, 1, 0, -2));
		Assertions.assertEquals(1, ((AffineSolution) EquationSystemSolver.intersection(planeX, planeY)).dimension());
	}

	@Test
	void floatEquationConstraintsRetainAffineClipping() {
		var line = new Line3f(new Vertex3f(0, 0, 0), new Axis3f(1, 0, 0));
		var ray = new Ray3f(new Vertex3f(2, 0, 0), new Axis3f(1, 0, 0));
		var result = EquationSystemSolver.intersectionFloatRepresentations(line, ray, GeometryPrecision.DEFAULT);
		Assertions.assertTrue(result instanceof AffineIntervalSolution);
	}

	@Test
	void lineAndPlaneIntersectionsCoverDegeneracies() {
		var x = new Line2f(new Vertex2f(0, 0), new Axis2f(1, 0));
		var y = new Line2f(new Vertex2f(0, 0), new Axis2f(0, 1));
		Assertions.assertEquals(0, x.intersection(y).getOrThrow(l -> 10f, v -> v.length(), e -> -1f, IllegalStateException::new), EPS);
		Assertions.assertTrue(x.intersection(new Line2f(new Vertex2f(0, 1), new Axis2f(1, 0))).isThird());
		var plane = new Plane3f(new Vertex3f(0, 0, 0), new Axis3f(0, 0, 1));
		var normalLine = new Line3f(new Vertex3f(0, 0, 3), new Axis3f(0, 0, 1));
		Assertions.assertEquals(0, normalLine.intersection(plane).getOrElse(l -> 7f, v -> v.z, e -> 9f, -1f), EPS);
		Assertions.assertTrue(normalLine.projectOnto(plane).isSecond());
		var offsetPlane = new Plane3f(new Vertex3f(0, 0, 2), new Axis3f(0, 1, 0));
		Assertions.assertTrue(plane.intersection(offsetPlane).isSecond());
	}

	@Test
	void raysAndSegmentsClipToTheirDomains() {
		var ray = new Ray3f(new Vertex3f(0, 0, 0), new Axis3f(1, 0, 0));
		var segment = new Segment3f(new Vertex3f(2, -1, 0), new Vertex3f(2, 1, 0));
		Assertions.assertTrue(ray.intersection(segment).isSecond());
		Assertions.assertEquals(2, ray.intersection(segment).getSecond().x, EPS);
		var clipped = new Segment2f(new Vertex2f(0, 0), new Vertex2f(4, 0));
		Assertions.assertEquals(4, clipped.at(1).x, EPS);
		Assertions.assertFalse(clipped.parameterDomain().contains(2, 0));
	}

	@Test
	void circlesAndDisksKeepBoundaryAndInteriorApart() {
		var circle = new Circle2f(new Vertex2f(1, 2), 3);
		Assertions.assertEquals(4, circle.at(0).x, EPS);
		Assertions.assertEquals(0, circle.implicitEquation().equations().get(0).value(4, 2), EPS);
		var disk = new Disk2f(new Vertex2f(1, 2), 3);
		Assertions.assertTrue(disk.contains(new Vertex2f(2, 3)));
		Assertions.assertFalse(disk.contains(new Vertex2f(9, 2)));
		Assertions.assertEquals(1, disk.at(0, 0).x, EPS);
		Assertions.assertThrows(IllegalArgumentException.class, () -> disk.at(0, 4));
	}

	@Test
	void sphereBallAndBoxHaveConsistentIntersections() {
		var sphere = new Sphere3f(new Vertex3f(0, 0, 0), 2);
		var plane = new Plane3f(new Vertex3f(0, 0, 1), new Axis3f(0, 0, 1));
		var section = sphere.intersection(plane);
		Assertions.assertTrue(section.isFirst());
		Assertions.assertEquals((float) Math.sqrt(3), section.getFirst().radius(), EPS);
		var ball = new Ball3f(new Vertex3f(0, 0, 0), 2);
		Assertions.assertTrue(ball.intersection(plane).isFirst());
		Assertions.assertTrue(ball.contains(new Vertex3f(0, 0, 1)));
		Assertions.assertEquals(2, ball.at(0, 0, 2).x, EPS);
		var box = new Box3f(new Vertex3f(0, 0, 0), new Vertex3f(2, 3, 4));
		Assertions.assertTrue(box.contains(new Vertex3f(1, 2, 3)));
		Assertions.assertEquals(4, box.at(1, 1, 1).z, EPS);
	}

	@Test
	void cylinderTorusAndHelixMatchAnalyticGeometry() {
		var base = new Vertex3f(0, 0, 0);
		var axis = new Axis3f(0, 0, 1);
		var shell = new CylinderSurface3f(base, axis, 2, 3);
		Assertions.assertEquals(0, shell.radialEquation().value(shell.at(0, 1)), EPS);
		var solid = new CylinderSolid3f(base, axis, 2, 3);
		Assertions.assertTrue(solid.contains(new Vertex3f(1, 0, 1)));
		Assertions.assertFalse(solid.contains(new Vertex3f(3, 0, 1)));
		var torus = new TorusSurface3f(base, axis, 3, 1);
		Assertions.assertEquals(0, torus.quarticEquation().value(torus.at(0, 0)), EPS);
		Assertions.assertTrue(new TorusSolid3f(base, axis, 3, 1).contains(torus.at(0, 0)));
		Assertions.assertEquals(2, new Helix3f(base, axis, 2, 1).at(0).length(), EPS);
	}

	@Test
	void floatBezierAndNurbsRetainControlPoints() {
		var curve = new BezierCurve2f(List.of(new Vector2f(0, 0), new Vector2f(1, 2), new Vector2f(2, 0)));
		Assertions.assertEquals(1, curve.at(0.5f).y, EPS);
		curve.controlPoints().get(0).set(100, 100);
		Assertions.assertEquals(0, curve.at(0).x, EPS);
		var nurbs = new NurbsCurve2f(2,
				new double[] { 0, 0, 0, 1, 1, 1 },
				List.of(new Vector2f(1, 0), new Vector2f(1, 1), new Vector2f(0, 1)),
				new double[] { 1, 1 / Math.sqrt(2), 1 });
		Assertions.assertEquals(1, nurbs.at(0.5f).length(), EPS);
	}

	@Test
	void cloneFrameAndVectorReturnIndependentValues() {
		var vector = new Vertex3f(1, 2, 3);
		var copied = vector.clone();
		copied.set(4, 5, 6);
		Assertions.assertEquals(1, vector.x, EPS);
		var frame = new LCS3f(new Vertex3f(0, 0, 0), new Axis3f(0, 0, 1));
		var copiedFrame = frame.clone();
		Assertions.assertEquals(0, copiedFrame.origin().distance(frame.origin()), EPS);
	}

	@Test
	void polygonHolesStayExcluded() {
		var polygon = new Polygon2f(List.of(new Vector2f(0, 0), new Vector2f(4, 0), new Vector2f(4, 4), new Vector2f(0, 4)),
				List.of(List.of(new Vector2f(1, 1), new Vector2f(3, 1), new Vector2f(3, 3), new Vector2f(1, 3))));
		Assertions.assertTrue(polygon.contains(new Vector2f(0.5f, 0.5f)));
		Assertions.assertFalse(polygon.contains(new Vector2f(2, 2)));
	}

	@Test
	void floatReflectionsAndProjectionsMatchDoubleSemantics() {
		var axis = new Line2f(new Vertex2f(0, 0), new Axis2f(1, 0));
		var source = new Vertex2f(2, 3);
		var reflected = source.reflectedAround(axis);
		Assertions.assertEquals(-3, reflected.y, EPS);
		Assertions.assertEquals(3, source.y, EPS);
		Assertions.assertEquals(0, reflected.reflectedAround(axis).distance(source), EPS);
		var plane = new Plane3f(new Vertex3f(0, 0, 2), new Axis3f(0, 0, 1));
		var p = new Vertex3f(2, 3, 5);
		Assertions.assertEquals(-1, p.reflectedAround(plane).z, EPS);
		Assertions.assertEquals(2, p.projectedOnto(plane).z, EPS);
		Assertions.assertEquals(5, p.z, EPS);
	}

	@Test
	void cloneDoesNotMoveNestedGeometryValues() {
		var center = new Vertex3f(2, 3, 4);
		var sphere = new Sphere3f((Vertex3fc) center, 2);
		var second = sphere.clone();
		center.set(9, 9, 9);
		Assertions.assertEquals(2, second.center().x(), EPS);
		var line = new Line3f(new Vertex3f(0, 0, 0), new Axis3f(1, 0, 0));
		var other = line.clone();
		Assertions.assertEquals(0, other.origin().distance(line.origin()), EPS);
		Assertions.assertEquals(0, other.at(2).distance(line.at(2)), EPS);
	}

	@Test
	void coneAndBooleanCompositionUseNativeFloatMembership() {
		var axis = new Axis3f(0, 0, 1);
		var origin = new Vertex3f();
		var shell = new ConeSurface3f(origin, axis, 2, 4);
		Assertions.assertEquals(0, shell.quadraticEquation().value(shell.at(0, 1)), EPS);
		var cone = new ConeSolid3f(origin, axis, 2, 4);
		Assertions.assertTrue(cone.contains(new Vertex3f(0, 0, 2)));
		Assertions.assertFalse(cone.contains(new Vertex3f(3, 0, 2)));
		var ball = new Ball3f(new Vertex3f(0, 0, 2), 1);
		var union = CSGSolid3f.union(ball, cone);
		Assertions.assertTrue(union.contains(new Vertex3f(0, 0, 0.1f)));
		Assertions.assertFalse(
				CSGSolid3f.intersection(ball, new Box3f(new Vertex3f(5, 5, 5), new Vertex3f(6, 6, 6))).contains(new Vertex3f(0, 0, 2)));
	}

	@Test
	void parametricFloatCurvesAndSurfacesMaintainDifferentials() {
		var parabola = new Parabola2f(new Vertex2f(), new Vector2f(1, 0), new Vector2f(0, 2));
		Assertions.assertEquals(8, parabola.at(2).y, EPS);
		Assertions.assertEquals(8, parabola.derivativeAt(2, new Vector2f()).y, EPS);
		var catenary = new Catenary2f(new Vertex2f(), new Axis2f(1, 0), 2);
		Assertions.assertEquals(0, catenary.tangentAt(0).y, EPS);
		var net = new Vector3f[][] { { new Vector3f(0, 0, 0), new Vector3f(0, 1, 0) }, { new Vector3f(1, 0, 0), new Vector3f(1, 1, 0) } };
		var patch = new BezierSurface3f(net);
		Assertions.assertEquals(0, patch.at(0.25f, 0.75f).z, EPS);
		Assertions.assertEquals(1, patch.normalAt(0.25f, 0.75f).z, EPS);
	}

	@Test
	void floatBSplineAndNurbsSurfacesMatchBilinearPatch() {
		Vector3f[][] net = { { new Vector3f(0, 0, 0), new Vector3f(0, 1, 0) }, { new Vector3f(1, 0, 0), new Vector3f(1, 1, 0) } };
		double[] knots = { 0, 0, 1, 1 };
		var spline = new BSplineSurface3f(1, 1, knots, knots, net);
		var nurbs = new NurbsSurface3f(1, 1, knots, knots, net, new double[][] { { 1, 1 }, { 1, 1 } });
		Assertions.assertEquals(0, spline.at(0.3f, 0.7f).distance(nurbs.at(0.3f, 0.7f)), EPS);
		Assertions.assertEquals(0, spline.at(0.3f, 0.7f).z, EPS);
		var spiral = new LogarithmicSpiral2f(new Vertex2f(0, 0), new Axis2f(1, 0), 2, 0.2f);
		Assertions.assertEquals(2, spiral.at(0).length(), EPS);
	}

	@Test
	void floatTopologySharesGeometricEndpointsByIdentity() {
		var first = new TopoVertex3f(new Vertex3f(0, 0, 0));
		var second = new TopoVertex3f(new Vertex3f(1, 0, 0));
		var line = new Line3f(new Vertex3f(0, 0, 0), new Axis3f(1, 0, 0));
		var edge = new TopoEdge3f(line, 0, 1, first, second);
		Assertions.assertTrue(edge.start() == first);
		Assertions.assertTrue(edge.end() == second);
		Assertions.assertThrows(IllegalArgumentException.class, () -> new TopoEdge3f(line, 0, 1, second, first));
	}
}
