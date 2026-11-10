package lu.kbra.pclib.geo.three.surface;

import java.util.List;

import org.joml.Vector3d;
import org.joml.Vector3dc;

import lu.kbra.pclib.geo.Geometry3dc;
import lu.kbra.pclib.geo.GeometryKind;
import lu.kbra.pclib.geo.Owned;
import lu.kbra.pclib.geo.equation.ImplicitRepresentable;
import lu.kbra.pclib.geo.equation.constraint.LinearConstraint3d;
import lu.kbra.pclib.geo.equation.constraint.MathConstraint3dc;
import lu.kbra.pclib.geo.equation.constraint.Relation;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem3dc;
import lu.kbra.pclib.geo.equation.implicit.LinearEquation3d;
import lu.kbra.pclib.geo.equation.implicit.QuadraticEquation3d;
import lu.kbra.pclib.geo.equation.parametric.ParameterDomain1d;
import lu.kbra.pclib.geo.equation.parametric.ParameterDomain2d;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation2to3dc;
import lu.kbra.pclib.geo.equation.parametric.ParametricSurface3dc;
import lu.kbra.pclib.geo.math.Axis3d;
import lu.kbra.pclib.geo.math.LCS3d;

/** Lateral shell of a finite right cone: v=0 is the base and v=height is the apex. */
public final class ConeSurface3d implements Geometry3dc, ParametricSurface3dc, ImplicitRepresentable<ImplicitSystem3dc, MathConstraint3dc> {
	private final LCS3d frame;
	private final double radius, height;

	public ConeSurface3d(final Vector3dc base, final Vector3dc axis, final double radius, final double height) {
		if (!(radius > 0) || !(height > 0) || !Double.isFinite(radius) || !Double.isFinite(height)) {
			throw new IllegalArgumentException("Cone dimensions must be positive and finite");
		}
		this.frame = new LCS3d(base, axis);
		this.radius = radius;
		this.height = height;
	}

	public @Owned LCS3d localCoordinateSystem() {
		return this.frame.clone();
	}

	public double radius() {
		return this.radius;
	}

	public double height() {
		return this.height;
	}

	@Override
	public ParameterDomain2d parameterDomain() {
		return new ParameterDomain2d(new ParameterDomain1d(0, 2 * Math.PI), new ParameterDomain1d(0, this.height));
	}

	@Override
	public ParametricEquation2to3dc parametricEquation() {
		return (u, v, dest) -> {
			if (!this.parameterDomain().contains(u, v, 0)) {
				throw new IllegalArgumentException("Outside cone chart");
			}
			final double r = this.radius * (1 - v / this.height);
			return dest.set(this.frame.origin())
					.fma(r * Math.cos(u), this.frame.xAxis())
					.fma(r * Math.sin(u), this.frame.yAxis())
					.fma(v, this.frame.zAxis());
		};
	}

	@Override
	public @Owned Axis3d normalAt(final double u, final double v) {
		if (!this.parameterDomain().contains(u, v, 0) || v == this.height) {
			throw new IllegalArgumentException("Cone apex has no unique normal");
		}
		return new Axis3d(new Vector3d(this.frame.xAxis()).mul(Math.cos(u))
				.fma(Math.sin(u), this.frame.yAxis())
				.fma(this.radius / this.height, this.frame.zAxis()));
	}

	/** A homogeneous quadratic written in world coordinates. */
	public QuadraticEquation3d quadraticEquation() {
		final Vector3dc n = this.frame.zAxis(), b = this.frame.origin();
		final double k = this.radius * this.radius / (this.height * this.height), s = 1 + k;
		final double xx = 1 - s * n.x() * n.x(), yy = 1 - s * n.y() * n.y(), zz = 1 - s * n.z() * n.z();
		final double xy = -2 * s * n.x() * n.y(), xz = -2 * s * n.x() * n.z(), yz = -2 * s * n.y() * n.z();
		final double lx = 2 * k * this.height * n.x(), ly = 2 * k * this.height * n.y(), lz = 2 * k * this.height * n.z();
		final double wx = lx - 2 * xx * b.x() - xy * b.y() - xz * b.z();
		final double wy = ly - xy * b.x() - 2 * yy * b.y() - yz * b.z();
		final double wz = lz - xz * b.x() - yz * b.y() - 2 * zz * b.z();
		final double constant = xx * b.x() * b.x() + yy * b.y() * b.y() + zz * b.z() * b.z() + xy * b.x() * b.y() + xz * b.x() * b.z()
				+ yz * b.y() * b.z() - lx * b.x() - ly * b.y() - lz * b.z() - this.radius * this.radius;
		return new QuadraticEquation3d(xx, yy, zz, xy, xz, yz, wx, wy, wz, constant);
	}

	@Override
	public ImplicitSystem3dc implicitEquation() {
		return new ImplicitSystem3dc(this.quadraticEquation());
	}

	@Override
	public List<MathConstraint3dc> constraints() {
		final Vector3dc n = this.frame.zAxis(), base = this.frame.origin();
		final double dot = n.dot(base);
		return List.of(new LinearConstraint3d(new LinearEquation3d(n.x(), n.y(), n.z(), -dot), Relation.GE),
				new LinearConstraint3d(new LinearEquation3d(n.x(), n.y(), n.z(), -dot - this.height), Relation.LE));
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
