package lu.kbra.pclib.geo.two.curve;

import org.joml.Vector2d;
import org.joml.Vector2dc;

import lu.kbra.pclib.geo.Geometry2dc;
import lu.kbra.pclib.geo.GeometryKind;
import lu.kbra.pclib.geo.equation.parametric.ParametricCurve2dc;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation1to2dc;
import lu.kbra.pclib.geo.math.Vertex2d;

/** General affine-friendly tanh curve P(t)=O+A*t+B*tanh(k*(t-t0)). */
public class TanhCurve2d implements Geometry2dc, ParametricCurve2dc {

	private final Vertex2d origin;
	private final Vector2d a, b;
	private final double k;
	private final double t0;

	public TanhCurve2d(final Vector2dc origin, final Vector2dc a, final Vector2dc b, final double k, final double t0) {
		if (!Double.isFinite(k) || !Double.isFinite(t0)) {
			throw new IllegalArgumentException("Invalid parameters");
		}
		this.origin = new Vertex2d(origin);
		this.a = new Vector2d(a);
		this.b = new Vector2d(b);
		this.k = k;
		this.t0 = t0;
	}

	@Override
	public ParametricEquation1to2dc parametricEquation() {
		return (t, dest) -> dest.set(this.origin).fma(t, this.a).fma(Math.tanh(this.k * (t - this.t0)), this.b);
	}

	public Vector2d derivativeAt(final double t, final Vector2d dest) {
		final double h = Math.tanh(this.k * (t - this.t0));
		return dest.set(this.a).fma(this.k * (1 - h * h), this.b);
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
