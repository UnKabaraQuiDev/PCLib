package lu.kbra.pclib.geo.three.curve;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import lu.kbra.pclib.geo.Geometry3fc;
import lu.kbra.pclib.geo.GeometryKind;
import lu.kbra.pclib.geo.equation.parametric.ParametricCurve3fc;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation1to3fc;
import lu.kbra.pclib.geo.math.Axis3f;
import lu.kbra.pclib.geo.math.LCS3f;
import lu.kbra.pclib.geo.math.Vertex3f;

public class Helix3f implements Geometry3fc, ParametricCurve3fc {
	private final Vertex3f center;
	private final Axis3f axis;
	private final float radius, pitch;

	public Helix3f(final Vector3fc center, final Vector3fc axis, final float radius, final float pitch) {
		if (!(radius > 0) || !Float.isFinite(radius) || !Float.isFinite(pitch)) {
			throw new IllegalArgumentException("Invalid helix");
		}
		this.center = new Vertex3f(center);
		this.axis = new Axis3f(axis);
		this.radius = radius;
		this.pitch = pitch;
	}

	@Override
	public ParametricEquation1to3fc parametricEquation() {
		return (angle, dest) -> {
			final LCS3f lcs = new LCS3f(this.center, this.axis);
			dest.set(this.center)
					.fma(this.radius * (float) Math.cos(angle), lcs.xAxis())
					.fma(this.radius * (float) Math.sin(angle), lcs.yAxis())
					.fma((float) (this.pitch * angle / (2 * Math.PI)), this.axis);
			return dest;
		};
	}

	public Vector3f derivativeAt(final float angle, final Vector3f dest) {
		final LCS3f lcs = new LCS3f(this.center, this.axis);
		return dest.set(lcs.xAxis())
				.mul(-this.radius * (float) Math.sin(angle))
				.fma(this.radius * (float) Math.cos(angle), lcs.yAxis())
				.fma((float) (this.pitch / (2 * Math.PI)), this.axis);
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
		return this.pitch == 0;
	}
}
