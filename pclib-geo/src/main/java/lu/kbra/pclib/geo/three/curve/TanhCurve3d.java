package lu.kbra.pclib.geo.three.curve;

import org.joml.Vector3d;
import org.joml.Vector3dc;

import lu.kbra.pclib.geo.Geometry3dc;
import lu.kbra.pclib.geo.GeometryKind;
import lu.kbra.pclib.geo.equation.parametric.ParametricCurve3dc;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation1to3dc;
import lu.kbra.pclib.geo.math.Vertex3d;

public class TanhCurve3d implements Geometry3dc, ParametricCurve3dc {
	private final Vertex3d origin;
	private final Vector3d a, b;
	private final double k, t0;

	public TanhCurve3d(final Vector3dc origin, final Vector3dc a, final Vector3dc b, final double k, final double t0) {
		if (!Double.isFinite(k) || !Double.isFinite(t0)) {
			throw new IllegalArgumentException("Invalid parameters");
		}
		this.origin = new Vertex3d(origin);
		this.a = new Vector3d(a);
		this.b = new Vector3d(b);
		this.k = k;
		this.t0 = t0;
	}

	@Override
	public ParametricEquation1to3dc parametricEquation() {
		return (t, dest) -> dest.set(this.origin).fma(t, this.a).fma(Math.tanh(this.k * (t - this.t0)), this.b);
	}

	public Vector3d derivativeAt(final double t, final Vector3d dest) {
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
