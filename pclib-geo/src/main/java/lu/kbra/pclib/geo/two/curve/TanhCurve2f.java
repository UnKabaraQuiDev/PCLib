package lu.kbra.pclib.geo.two.curve;

import org.joml.Vector2f;
import org.joml.Vector2fc;

import lu.kbra.pclib.geo.Geometry2fc;
import lu.kbra.pclib.geo.GeometryKind;
import lu.kbra.pclib.geo.equation.parametric.ParametricCurve2fc;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation1to2fc;
import lu.kbra.pclib.geo.math.Vertex2f;

/** General affine-friendly tanh curve P(t)=O+A*t+B*tanh(k*(t-t0)). */
public class TanhCurve2f implements Geometry2fc, ParametricCurve2fc {

	private final Vertex2f origin;
	private final Vector2f a, b;
	private final float k;
	private final float t0;

	public TanhCurve2f(final Vector2fc origin, final Vector2fc a, final Vector2fc b, final float k, final float t0) {
		if (!Float.isFinite(k) || !Float.isFinite(t0)) {
			throw new IllegalArgumentException("Invalid parameters");
		}
		this.origin = new Vertex2f(origin);
		this.a = new Vector2f(a);
		this.b = new Vector2f(b);
		this.k = k;
		this.t0 = t0;
	}

	@Override
	public ParametricEquation1to2fc parametricEquation() {
		return (t, dest) -> dest.set(this.origin).fma(t, this.a).fma((float) Math.tanh(this.k * (t - this.t0)), this.b);
	}

	public Vector2f derivativeAt(final float t, final Vector2f dest) {
		final float h = (float) Math.tanh(this.k * (t - this.t0));
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
