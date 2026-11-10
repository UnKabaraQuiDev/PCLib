package lu.kbra.pclib.geo.two.curve;

import java.util.Objects;

import org.joml.Vector2d;
import org.joml.Vector2dc;

import lu.kbra.pclib.geo.Geometry2dc;
import lu.kbra.pclib.geo.GeometryKind;
import lu.kbra.pclib.geo.Move;
import lu.kbra.pclib.geo.equation.parametric.ParametricCurve2dc;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation1to2dc;
import lu.kbra.pclib.geo.math.Vertex2d;

/** General planar parabola P(t) = origin + t*A + t^2*B. */
public class Parabola2d implements Geometry2dc, ParametricCurve2dc {

	private final Vertex2d origin;
	private final Vector2d a;
	private final Vector2d b;

	public Parabola2d(final Vector2dc origin, final Vector2dc linear, final Vector2dc quadratic) {
		this(new Vertex2d(origin), new Vector2d(linear), new Vector2d(quadratic));
	}

	public Parabola2d(final @Move Vertex2d origin, final @Move Vector2d linear, final @Move Vector2d quadratic) {
		this.origin = Objects.requireNonNull(origin);
		this.a = Objects.requireNonNull(linear);
		this.b = Objects.requireNonNull(quadratic);
		if (!this.origin.isFinite() || !this.a.isFinite() || !this.b.isFinite() || !(this.b.lengthSquared() > 0)) {
			throw new IllegalArgumentException("Quadratic vector must be nonzero");
		}
	}

	@Override
	public ParametricEquation1to2dc parametricEquation() {
		return (t, dest) -> dest.set(this.origin).fma(t, this.a).fma(t * t, this.b);
	}

	public Vector2d derivativeAt(final double t, final Vector2d dest) {
		return dest.set(this.a).fma(2 * t, this.b);
	}

	@Override
	public GeometryKind geometryKind() {
		return GeometryKind.CURVE;
	}

	@Override
	public int intrinsicDimension() {
		return 1;
	}

	@Override
	public boolean isBounded() {
		return false;
	}

}
