package lu.kbra.pclib.geo;

import org.joml.Matrix4x3d;
import org.joml.Vector3d;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import lu.kbra.pclib.geo.math.Axis2d;
import lu.kbra.pclib.geo.math.Axis2dc;
import lu.kbra.pclib.geo.math.Axis3d;
import lu.kbra.pclib.geo.math.LCS3d;
import lu.kbra.pclib.geo.math.Vertex2d;
import lu.kbra.pclib.geo.math.Vertex2dc;
import lu.kbra.pclib.geo.math.Vertex3d;
import lu.kbra.pclib.geo.math.Vertex3dc;
import lu.kbra.pclib.geo.result.Empty;
import lu.kbra.pclib.geo.result.Result2;
import lu.kbra.pclib.geo.result.Result3;
import lu.kbra.pclib.geo.three.curve.Circle3d;
import lu.kbra.pclib.geo.three.curve.Ellipse3d;
import lu.kbra.pclib.geo.three.curve.Line3d;
import lu.kbra.pclib.geo.three.curve.Segment3d;
import lu.kbra.pclib.geo.three.surface.Plane3d;
import lu.kbra.pclib.geo.three.surface.Sphere3d;
import lu.kbra.pclib.geo.two.curve.Line2d;

class ValueAndResultTest {
	private static final double EPS = 1e-9;

	@Test
	void fluentJomlOperationsReturnTheTypedReceiver() {
		final Vertex2d point = new Vertex2d(3, 5).sub(1, 2).mul(2).add(1, 1);
		Assertions.assertEquals(5, point.x, ValueAndResultTest.EPS);
		final Vertex3d another = new Vertex3d(1, 2, 3).sub(new Vertex3d(1, 0, 0)).normalize();
		Assertions.assertEquals(1, another.length(), ValueAndResultTest.EPS);
		final Axis3d axis = new Axis3d(1, 1, 0).negate().absolute();
		Assertions.assertEquals(1, axis.length(), ValueAndResultTest.EPS);
	}

	@Test
	void constantInterfaceDestinationOverloadsReturnTheExactDestination() {
		final Vertex3dc point = new Vertex3d(2, 3, 4);
		final Vertex3d destination = new Vertex3d();
		Assertions.assertSame(destination, point.add(new Vertex3d(1, 1, 1), destination));
		Assertions.assertEquals(5, destination.z, ValueAndResultTest.EPS);
		final Axis2dc direction = new Axis2d(0, 1);
		final Axis2d typedDestination = new Axis2d();
		Assertions.assertSame(typedDestination, direction.negate(typedDestination));
		Assertions.assertEquals(-1, typedDestination.y, ValueAndResultTest.EPS);
	}

	@Test
	void copyAndMoveConstructorsAreDistinct() {
		final var point = new Vertex2d(1, 2);
		final var dir = new Axis2d(1, 0);
		final var copied = new Line2d((Vertex2dc) point, (Axis2dc) dir);
		final var moved = new Line2d(point, dir);
		Assertions.assertSame(point, moved.origin());
		Assertions.assertSame(dir, moved.direction());
		Assertions.assertFalse(copied.origin() == point);
		Assertions.assertFalse(copied.direction() == dir);
		final var c = new Line3d((Vertex3dc) new Vertex3d(1, 2, 3), new Axis3d(1, 0, 0));
		Assertions.assertEquals(1, c.origin().x(), ValueAndResultTest.EPS);
	}

	@Test
	void cloneProducesIndependentMutableValues() {
		final var point = new Vertex3d(1, 2, 3);
		final var duplicate = point.clone();
		Assertions.assertFalse(duplicate == point);
		duplicate.set(10, 20, 30);
		Assertions.assertEquals(1, point.x, ValueAndResultTest.EPS);
		final var frame = new LCS3d(new Vertex3d(), new Axis3d(0, 0, 1));
		final var frameCopy = frame.clone();
		Assertions.assertFalse(frameCopy == frame);
		Assertions.assertEquals(0, frameCopy.origin().length(), ValueAndResultTest.EPS);
	}

	@Test
	void resultNGetGetOrElseAndGetOrThrow() {
		final Result3<String, Integer, Empty> selected = Result3.second(42);
		final int value = selected.get(s -> -1, i -> i, e -> -2);
		Assertions.assertEquals(42, value);
		final Result3<String, Integer, Empty> empty = Result3.third(Empty.INSTANCE);
		Assertions.assertEquals(-5, empty.getOrElse(s -> -1, i -> i, e -> -2, -5));
		Assertions.assertThrows(IllegalStateException.class, () -> empty.getOrThrow(s -> -1, i -> i, e -> -2, IllegalStateException::new));
		final Result2<Integer, Empty> simple = Result2.first(7);
		Assertions.assertEquals(7, simple.get(i -> i, e -> -1));
	}

	@Test
	void transformedAndIntersectedShapesDoNotAliasTheirSources() {
		final var ellipse = new Ellipse3d(new Vertex3d(1, 2, 3), new Vector3d(3, 0, 0), new Vector3d(0, 2, 0));
		final var matrix = new Matrix4x3d().translation(10, 0, 0);
		final var transformed = ellipse.transformed(matrix);
		Assertions.assertEquals(1, ellipse.center().x(), ValueAndResultTest.EPS);
		Assertions.assertEquals(11, transformed.center().x(), ValueAndResultTest.EPS);
		final var circle = new Circle3d(new Vertex3d(0, 0, 0), new Axis3d(0, 0, 1), 2);
		circle.transformed(matrix);
		Assertions.assertEquals(0, circle.center().x(), ValueAndResultTest.EPS);
		final var segment = new Segment3d(new Vertex3d(0, 0, 0), new Vertex3d(1, 0, 0));
		final var plane = new Plane3d(new Vertex3d(), new Axis3d(0, 0, 1));
		final var section = segment.intersection(plane).getFirst();
		Assertions.assertFalse(segment.start() == section.start());
	}

	@Test
	void invalidGeometryDoesNotPartiallyMutateExistingShapes() {
		final var line = new Line3d(new Vector3d(1, 2, 3), new Vector3d(1, 0, 0));
		Assertions.assertThrows(IllegalArgumentException.class, () -> line.set(new Vector3d(8, 8, 8), new Vector3d()));
		Assertions.assertEquals(1, line.origin().x(), ValueAndResultTest.EPS);
		Assertions.assertThrows(IllegalArgumentException.class, () -> new Sphere3d(new Vertex3d(Double.NaN, 0, 0), 3));
	}
}
