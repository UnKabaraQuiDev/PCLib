package lu.kbra.pclib.geo.two.curve;

import java.util.Objects;

import org.joml.Vector2f;

import lu.kbra.pclib.geo.Geometry2fc;
import lu.kbra.pclib.geo.GeometryKind;
import lu.kbra.pclib.geo.Move;
import lu.kbra.pclib.geo.equation.parametric.ParametricCurve2fc;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation1to2fc;
import lu.kbra.pclib.geo.math.Axis2f;
import lu.kbra.pclib.geo.math.Axis2fc;
import lu.kbra.pclib.geo.math.Vertex2f;
import lu.kbra.pclib.geo.math.Vertex2fc;

/** Catenary with minimum at t=0: P(t)=origin+a*t*X+a*(cosh(t)-1)*Y. */
public final class Catenary2f implements Geometry2fc, ParametricCurve2fc {
	private final Vertex2f origin;
	private final Axis2f x;
	private final float a;

	public Catenary2f(final Vertex2fc origin, final Axis2fc x, final float a) {
		this(new Vertex2f(origin), new Axis2f(x), a);
	}

	public Catenary2f(final @Move Vertex2f origin, final @Move Axis2f x, final float a) {
		if (!(a > 0) || !Float.isFinite(a)) {
			throw new IllegalArgumentException("Catenary scale must be positive");
		}
		this.origin = Objects.requireNonNull(origin);
		this.x = Objects.requireNonNull(x);
		if (!this.x.isFinite() || !(this.x.lengthSquared() > 0)) {
			throw new IllegalArgumentException("Invalid X direction");
		}
		this.x.normalize();
		this.a = a;
	}

	public float scale() {
		return this.a;
	}

	@Override
	public ParametricEquation1to2fc parametricEquation() {
		return (t, dest) -> {
			if (!Float.isFinite(t)) {
				throw new IllegalArgumentException("Nonfinite parameter");
			}
			final float height = this.a * ((float) Math.cosh(t) - 1);
			return dest.set(this.origin).fma(this.a * t, this.x).add(-height * this.x.y, height * this.x.x);
		};
	}

	public Vector2f derivativeAt(final float t, final Vector2f dest) {
		final float vertical = this.a * (float) Math.sinh(t);
		return dest.set(this.a * this.x.x - vertical * this.x.y, this.a * this.x.y + vertical * this.x.x);
	}

	@Override
	public Axis2f tangentAt(final float t) {
		return new Axis2f(this.derivativeAt(t, new Vector2f()));
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
