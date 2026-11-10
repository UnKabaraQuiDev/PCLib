package lu.kbra.pclib.geo.three.curve;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import lu.kbra.pclib.geo.Geometry3fc;
import lu.kbra.pclib.geo.GeometryKind;
import lu.kbra.pclib.geo.equation.parametric.ParametricCurve3fc;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation1to3fc;
import lu.kbra.pclib.geo.math.Vertex3f;

public class TanhCurve3f implements Geometry3fc, ParametricCurve3fc {
	private final Vertex3f origin;
	private final Vector3f a, b;
	private final float k, t0;

	public TanhCurve3f(final Vector3fc origin, final Vector3fc a, final Vector3fc b, final float k, final float t0) {
		if (!Float.isFinite(k) || !Float.isFinite(t0)) {
			throw new IllegalArgumentException("Invalid parameters");
		}
		this.origin = new Vertex3f(origin);
		this.a = new Vector3f(a);
		this.b = new Vector3f(b);
		this.k = k;
		this.t0 = t0;
	}

	@Override
	public ParametricEquation1to3fc parametricEquation() {
		return (t, dest) -> dest.set(this.origin).fma(t, this.a).fma((float) Math.tanh(this.k * (t - this.t0)), this.b);
	}

	public Vector3f derivativeAt(final float t, final Vector3f dest) {
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
