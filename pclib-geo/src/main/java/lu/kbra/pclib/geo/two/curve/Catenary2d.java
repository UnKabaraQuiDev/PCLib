package lu.kbra.pclib.geo.two.curve;

import java.util.Objects;

import org.joml.Vector2d;

import lu.kbra.pclib.geo.Geometry2dc;
import lu.kbra.pclib.geo.GeometryKind;
import lu.kbra.pclib.geo.Move;
import lu.kbra.pclib.geo.equation.parametric.ParametricCurve2dc;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation1to2dc;
import lu.kbra.pclib.geo.math.Axis2d;
import lu.kbra.pclib.geo.math.Axis2dc;
import lu.kbra.pclib.geo.math.Vertex2d;
import lu.kbra.pclib.geo.math.Vertex2dc;

/** Catenary with minimum at t=0: P(t)=origin+a*t*X+a*(cosh(t)-1)*Y. */
public final class Catenary2d implements Geometry2dc, ParametricCurve2dc {
	private final Vertex2d origin;
	private final Axis2d x;
	private final double a;

	public Catenary2d(final Vertex2dc origin, final Axis2dc x, final double a) {
		this(new Vertex2d(origin), new Axis2d(x), a);
	}

	public Catenary2d(final @Move Vertex2d origin, final @Move Axis2d x, final double a) {
		if (!(a > 0) || !Double.isFinite(a)) {
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

	public double scale() {
		return this.a;
	}

	@Override
	public ParametricEquation1to2dc parametricEquation() {
		return (t, dest) -> {
			if (!Double.isFinite(t)) {
				throw new IllegalArgumentException("Nonfinite parameter");
			}
			final double height = this.a * (Math.cosh(t) - 1);
			return dest.set(this.origin).fma(this.a * t, this.x).add(-height * this.x.y, height * this.x.x);
		};
	}

	public Vector2d derivativeAt(final double t, final Vector2d dest) {
		final double vertical = this.a * Math.sinh(t);
		return dest.set(this.a * this.x.x - vertical * this.x.y, this.a * this.x.y + vertical * this.x.x);
	}

	@Override
	public Axis2d tangentAt(final double t) {
		return new Axis2d(this.derivativeAt(t, new Vector2d()));
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
