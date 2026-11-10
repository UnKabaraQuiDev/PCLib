package lu.kbra.pclib.geo.two.curve;

import java.util.Objects;

import org.joml.Vector2f;
import org.joml.Vector2fc;

import lu.kbra.pclib.geo.Geometry2fc;
import lu.kbra.pclib.geo.GeometryKind;
import lu.kbra.pclib.geo.Move;
import lu.kbra.pclib.geo.equation.parametric.ParametricCurve2fc;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation1to2fc;
import lu.kbra.pclib.geo.math.Vertex2f;

/** General planar parabola P(t) = origin + t*A + t^2*B. */
public class Parabola2f implements Geometry2fc, ParametricCurve2fc {

	private final Vertex2f origin;
	private final Vector2f a;
	private final Vector2f b;

	public Parabola2f(final Vector2fc origin, final Vector2fc linear, final Vector2fc quadratic) {
		this(new Vertex2f(origin), new Vector2f(linear), new Vector2f(quadratic));
	}

	public Parabola2f(final @Move Vertex2f origin, final @Move Vector2f linear, final @Move Vector2f quadratic) {
		this.origin = Objects.requireNonNull(origin);
		this.a = Objects.requireNonNull(linear);
		this.b = Objects.requireNonNull(quadratic);
		if (!this.origin.isFinite() || !this.a.isFinite() || !this.b.isFinite() || !(this.b.lengthSquared() > 0)) {
			throw new IllegalArgumentException("Quadratic vector must be nonzero");
		}
	}

	@Override
	public ParametricEquation1to2fc parametricEquation() {
		return (t, dest) -> dest.set(this.origin).fma(t, this.a).fma(t * t, this.b);
	}

	public Vector2f derivativeAt(final float t, final Vector2f dest) {
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
