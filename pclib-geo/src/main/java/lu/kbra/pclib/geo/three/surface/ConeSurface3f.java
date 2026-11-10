package lu.kbra.pclib.geo.three.surface;

import java.util.List;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import lu.kbra.pclib.geo.Geometry3fc;
import lu.kbra.pclib.geo.GeometryKind;
import lu.kbra.pclib.geo.Owned;
import lu.kbra.pclib.geo.equation.ImplicitRepresentable;
import lu.kbra.pclib.geo.equation.constraint.LinearConstraint3f;
import lu.kbra.pclib.geo.equation.constraint.MathConstraint3fc;
import lu.kbra.pclib.geo.equation.constraint.Relation;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem3fc;
import lu.kbra.pclib.geo.equation.implicit.LinearEquation3f;
import lu.kbra.pclib.geo.equation.implicit.QuadraticEquation3f;
import lu.kbra.pclib.geo.equation.parametric.ParameterDomain1d;
import lu.kbra.pclib.geo.equation.parametric.ParameterDomain2d;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation2to3fc;
import lu.kbra.pclib.geo.equation.parametric.ParametricSurface3fc;
import lu.kbra.pclib.geo.math.Axis3f;
import lu.kbra.pclib.geo.math.LCS3f;

/** Lateral shell of a finite right cone: v=0 is the base and v=height is the apex. */
public final class ConeSurface3f implements Geometry3fc, ParametricSurface3fc, ImplicitRepresentable<ImplicitSystem3fc, MathConstraint3fc> {
	private final LCS3f frame;
	private final float radius, height;

	public ConeSurface3f(final Vector3fc base, final Vector3fc axis, final float radius, final float height) {
		if (!(radius > 0) || !(height > 0) || !Float.isFinite(radius) || !Float.isFinite(height)) {
			throw new IllegalArgumentException("Cone dimensions must be positive and finite");
		}
		this.frame = new LCS3f(base, axis);
		this.radius = radius;
		this.height = height;
	}

	public @Owned LCS3f localCoordinateSystem() {
		return this.frame.clone();
	}

	public float radius() {
		return this.radius;
	}

	public float height() {
		return this.height;
	}

	@Override
	public ParameterDomain2d parameterDomain() {
		return new ParameterDomain2d(new ParameterDomain1d(0, 2 * Math.PI), new ParameterDomain1d(0, this.height));
	}

	@Override
	public ParametricEquation2to3fc parametricEquation() {
		return (u, v, dest) -> {
			if (!this.parameterDomain().contains(u, v, 0)) {
				throw new IllegalArgumentException("Outside cone chart");
			}
			final float r = this.radius * (1 - v / this.height);
			return dest.set(this.frame.origin())
					.fma(r * (float) Math.cos(u), this.frame.xAxis())
					.fma(r * (float) Math.sin(u), this.frame.yAxis())
					.fma(v, this.frame.zAxis());
		};
	}

	@Override
	public @Owned Axis3f normalAt(final float u, final float v) {
		if (!this.parameterDomain().contains(u, v, 0) || v == this.height) {
			throw new IllegalArgumentException("Cone apex has no unique normal");
		}
		return new Axis3f(new Vector3f(this.frame.xAxis()).mul((float) Math.cos(u))
				.fma((float) Math.sin(u), this.frame.yAxis())
				.fma(this.radius / this.height, this.frame.zAxis()));
	}

	/** A homogeneous quadratic written in world coordinates. */
	public QuadraticEquation3f quadraticEquation() {
		final Vector3fc n = this.frame.zAxis(), b = this.frame.origin();
		final float k = this.radius * this.radius / (this.height * this.height), s = 1 + k;
		final float xx = 1 - s * n.x() * n.x(), yy = 1 - s * n.y() * n.y(), zz = 1 - s * n.z() * n.z();
		final float xy = -2 * s * n.x() * n.y(), xz = -2 * s * n.x() * n.z(), yz = -2 * s * n.y() * n.z();
		final float lx = 2 * k * this.height * n.x(), ly = 2 * k * this.height * n.y(), lz = 2 * k * this.height * n.z();
		final float wx = lx - 2 * xx * b.x() - xy * b.y() - xz * b.z();
		final float wy = ly - xy * b.x() - 2 * yy * b.y() - yz * b.z();
		final float wz = lz - xz * b.x() - yz * b.y() - 2 * zz * b.z();
		final float constant = xx * b.x() * b.x() + yy * b.y() * b.y() + zz * b.z() * b.z() + xy * b.x() * b.y() + xz * b.x() * b.z()
				+ yz * b.y() * b.z() - lx * b.x() - ly * b.y() - lz * b.z() - this.radius * this.radius;
		return new QuadraticEquation3f(xx, yy, zz, xy, xz, yz, wx, wy, wz, constant);
	}

	@Override
	public ImplicitSystem3fc implicitEquation() {
		return new ImplicitSystem3fc(this.quadraticEquation());
	}

	@Override
	public List<MathConstraint3fc> constraints() {
		final Vector3fc n = this.frame.zAxis(), base = this.frame.origin();
		final float dot = n.dot(base);
		return List.of(new LinearConstraint3f(new LinearEquation3f(n.x(), n.y(), n.z(), -dot), Relation.GE),
				new LinearConstraint3f(new LinearEquation3f(n.x(), n.y(), n.z(), -dot - this.height), Relation.LE));
	}

	@Override
	public GeometryKind geometryKind() {
		return GeometryKind.SURFACE;
	}

	@Override
	public int intrinsicDimension() {
		return 2;
	}

	@Override
	public boolean isBounded() {
		return true;
	}
}
