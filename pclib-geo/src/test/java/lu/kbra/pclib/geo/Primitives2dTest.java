package lu.kbra.pclib.geo;

import java.util.List;

import org.joml.Vector2d;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import lu.kbra.pclib.geo.math.Axis2d;
import lu.kbra.pclib.geo.math.Axis2dc;
import lu.kbra.pclib.geo.math.Vertex2d;
import lu.kbra.pclib.geo.math.Vertex2dc;
import lu.kbra.pclib.geo.two.curve.ArchimedeanSpiral2d;
import lu.kbra.pclib.geo.two.curve.BezierCurve2d;
import lu.kbra.pclib.geo.two.curve.Catenary2d;
import lu.kbra.pclib.geo.two.curve.Ellipse2d;
import lu.kbra.pclib.geo.two.curve.Line2d;
import lu.kbra.pclib.geo.two.curve.LogarithmicSpiral2d;
import lu.kbra.pclib.geo.two.curve.NurbsCurve2d;
import lu.kbra.pclib.geo.two.curve.Parabola2d;
import lu.kbra.pclib.geo.two.curve.Ray2d;
import lu.kbra.pclib.geo.two.curve.Segment2d;
import lu.kbra.pclib.geo.two.region.Disk2d;
import lu.kbra.pclib.geo.two.region.Polygon2d;
import lu.kbra.pclib.geo.two.region.Rectangle2d;

class Primitives2dTest {
	private static final double EPS = 1e-9;

	@Test
	void lineIntersectionHandlesCoincidentParallelAndCrossing() {
		final var x = new Line2d(new Vertex2d(0, 0), new Axis2d(1, 0));
		final var y = new Line2d(new Vertex2d(0, 0), new Axis2d(0, 1));
		Assertions.assertTrue(x.intersection(y).isSecond());
		Assertions.assertEquals(0, x.intersection(y).getSecond().length(), Primitives2dTest.EPS);
		Assertions.assertTrue(x.intersection(new Line2d(new Vertex2d(7, 0), new Axis2d(1, 0))).isFirst());
		Assertions.assertTrue(x.intersection(new Line2d(new Vertex2d(0, 1), new Axis2d(1, 0))).isThird());
	}

	@Test
	void pointReflectionIsAnInvolution() {
		final var axis = new Line2d(new Vertex2d(0, 0), new Axis2d(1, 0));
		final var original = new Vertex2d(4, 3);
		final var reflected = original.reflectedAround(axis);
		Assertions.assertEquals(-3, reflected.y(), Primitives2dTest.EPS);
		Assertions.assertEquals(0, reflected.reflectedAround(axis).distance(original), Primitives2dTest.EPS);
		Assertions.assertEquals(0, original.reflectedAround(new Vertex2d(0, 0)).distance(new Vertex2d(-4, -3)), Primitives2dTest.EPS);
	}

	@Test
	void segmentAndRayHaveCorrectDomainsAndSupportLines() {
		final var a = new Vertex2d(2, 3);
		final var b = new Vertex2d(5, 3);
		final var segment = new Segment2d((Vertex2dc) a, (Vertex2dc) b);
		Assertions.assertEquals(3, segment.at(1).distance(a), Primitives2dTest.EPS);
		Assertions.assertEquals(0, segment.implicitEquation().equations().get(0).value(4, 3), Primitives2dTest.EPS);
		final var ray = new Ray2d((Vertex2dc) a, (Axis2dc) new Axis2d(1, 0));
		Assertions.assertEquals(5, ray.at(3).x, Primitives2dTest.EPS);
		Assertions.assertFalse(ray.parameterDomain().contains(-0.1, 0));
	}

	@Test
	void filledRegionsAndHoles() {
		final var disk = new Disk2d(new Vector2d(), 2);
		Assertions.assertTrue(disk.contains(new Vector2d(1, 1)));
		Assertions.assertFalse(disk.contains(new Vector2d(3, 0)));
		final var polygon = new Polygon2d(List.of(new Vector2d(0, 0), new Vector2d(5, 0), new Vector2d(5, 5), new Vector2d(0, 5)),
				List.of(List.of(new Vector2d(1, 1), new Vector2d(4, 1), new Vector2d(4, 4), new Vector2d(1, 4))));
		Assertions.assertTrue(polygon.contains(new Vector2d(0.5, 0.5)));
		Assertions.assertFalse(polygon.contains(new Vector2d(2, 2)));
		Assertions.assertEquals(0, new Rectangle2d(new Vector2d(0, 0), new Vector2d(2, 2)).at(0, 0).length(), Primitives2dTest.EPS);
	}

	@Test
	void ellipsePreservesSemiaxisLength() {
		final var ellipse = new Ellipse2d(new Vertex2d(2, 3), new Vector2d(4, 0), new Vector2d(0, 2));
		Assertions.assertEquals(6, ellipse.at(0).x, Primitives2dTest.EPS);
		Assertions.assertEquals(5, ellipse.at(Math.PI / 2).y, Primitives2dTest.EPS);
		Assertions.assertThrows(IllegalArgumentException.class,
				() -> new Ellipse2d(new Vertex2d(), new Vector2d(1, 0), new Vector2d(2, 0)));
	}

	@Test
	void parabolaPreservesCoefficientMagnitude() {
		final var curve = new Parabola2d(new Vector2d(), new Vector2d(2, 0), new Vector2d(0, 3));
		Assertions.assertEquals(4, curve.at(2).x, Primitives2dTest.EPS);
		Assertions.assertEquals(12, curve.at(2).y, Primitives2dTest.EPS);
		Assertions.assertEquals(12, curve.derivativeAt(2, new Vector2d()).y, Primitives2dTest.EPS);
	}

	@Test
	void bezierAndNurbsCurvesInterpolateTheirDomains() {
		final var bezier = new BezierCurve2d(List.of(new Vector2d(0, 0), new Vector2d(1, 2), new Vector2d(2, 0)));
		Assertions.assertEquals(1, bezier.at(0.5).x, Primitives2dTest.EPS);
		Assertions.assertEquals(1, bezier.at(0.5).y, Primitives2dTest.EPS);
		Assertions.assertEquals(2, bezier.derivativeAt(0, new Vector2d()).x, Primitives2dTest.EPS);
		final var controls = bezier.controlPoints();
		controls.get(0).set(30, 30);
		Assertions.assertEquals(0, bezier.at(0).x, Primitives2dTest.EPS);
		final var rational = new NurbsCurve2d(2,
				new double[] { 0, 0, 0, 1, 1, 1 },
				List.of(new Vector2d(1, 0), new Vector2d(1, 1), new Vector2d(0, 1)),
				new double[] { 1, 1 / Math.sqrt(2), 1 });
		Assertions.assertEquals(1, rational.at(0.5).length(), Primitives2dTest.EPS);
	}

	@Test
	void catenaryAndSpiralsHaveAnalyticTangents() {
		final Vertex2dc origin = new Vertex2d(0, 0);
		final Axis2dc x = new Axis2d(1, 0);
		final var catenary = new Catenary2d(origin, x, 2);
		Assertions.assertEquals(0, catenary.at(0).length(), Primitives2dTest.EPS);
		Assertions.assertEquals(0, catenary.tangentAt(0).y, Primitives2dTest.EPS);
		Assertions.assertEquals(2 * (Math.cosh(1) - 1), catenary.at(1).y, Primitives2dTest.EPS);
		final var archimedean = new ArchimedeanSpiral2d(origin, x, 1, 0.5);
		Assertions.assertEquals(1, archimedean.at(0).x, Primitives2dTest.EPS);
		Assertions.assertEquals(1 + Math.PI / 2, archimedean.at(Math.PI).length(), Primitives2dTest.EPS);
		Assertions.assertThrows(IllegalArgumentException.class, () -> archimedean.at(-1));
		final var logarithmic = new LogarithmicSpiral2d(origin, x, 2, 0.2);
		Assertions.assertEquals(2, logarithmic.at(0).length(), Primitives2dTest.EPS);
		Assertions.assertEquals(2 * Math.exp(0.2 * Math.PI), logarithmic.at(Math.PI).length(), Primitives2dTest.EPS);
	}
}
