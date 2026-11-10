package lu.kbra.pclib.geo.three.curve;

import org.joml.Vector3d;
import org.joml.Vector3dc;

import lu.kbra.pclib.geo.Geometry3dc;
import lu.kbra.pclib.geo.GeometryKind;
import lu.kbra.pclib.geo.equation.parametric.ParametricCurve3dc;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation1to3dc;
import lu.kbra.pclib.geo.math.Axis3d;
import lu.kbra.pclib.geo.math.LCS3d;
import lu.kbra.pclib.geo.math.Vertex3d;

public class Helix3d implements Geometry3dc, ParametricCurve3dc {
	private final Vertex3d center;
	private final Axis3d axis;
	private final double radius, pitch;

	public Helix3d(final Vector3dc center, final Vector3dc axis, final double radius, final double pitch) {
		if (!(radius > 0) || !Double.isFinite(radius) || !Double.isFinite(pitch)) {
			throw new IllegalArgumentException("Invalid helix");
		}
		this.center = new Vertex3d(center);
		this.axis = new Axis3d(axis);
		this.radius = radius;
		this.pitch = pitch;
	}

	@Override
	public ParametricEquation1to3dc parametricEquation() {
		return (angle, dest) -> {
			final LCS3d lcs = new LCS3d(this.center, this.axis);
			dest.set(this.center)
					.fma(this.radius * Math.cos(angle), lcs.xAxis())
					.fma(this.radius * Math.sin(angle), lcs.yAxis())
					.fma(this.pitch * angle / (2 * Math.PI), this.axis);
			return dest;
		};
	}

	public Vector3d derivativeAt(final double angle, final Vector3d dest) {
		final LCS3d lcs = new LCS3d(this.center, this.axis);
		return dest.set(lcs.xAxis())
				.mul(-this.radius * Math.sin(angle))
				.fma(this.radius * Math.cos(angle), lcs.yAxis())
				.fma(this.pitch / (2 * Math.PI), this.axis);
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
