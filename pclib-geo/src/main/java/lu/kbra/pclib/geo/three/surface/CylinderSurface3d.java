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

/** Lateral surface of a right circular cylinder (end disks are not part of this shell). */
public final class CylinderSurface3d
		implements
			Geometry3dc,
			ParametricSurface3dc,
			ImplicitRepresentable<ImplicitSystem3dc, MathConstraint3dc> {
	private final LCS3d frame;
	private final double radius, height;

	public CylinderSurface3d(final Vector3dc base, final Vector3dc axis, final double radius, final double height) {
		if (!(radius > 0) || !(height > 0) || !Double.isFinite(radius) || !Double.isFinite(height)) {
			throw new IllegalArgumentException("Cylinder dimensions must be positive and finite");
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
		return (u, v, dest) -> dest.set(this.frame.origin())
				.fma(this.radius * Math.cos(u), this.frame.xAxis())
				.fma(this.radius * Math.sin(u), this.frame.yAxis())
				.fma(v, this.frame.zAxis());
	}

	@Override
	public @Owned Axis3d normalAt(final double u, final double v) {
		return new Axis3d(new Vector3d(this.frame.xAxis()).mul(Math.cos(u)).fma(Math.sin(u), this.frame.yAxis()));
	}

	@Override
	public ImplicitSystem3dc implicitEquation() {
		return new ImplicitSystem3dc(this.radialEquation());
	}

	public QuadraticEquation3d radialEquation() {
		final Vector3dc n = this.frame.zAxis(), c = this.frame.origin();
		final double xx = 1 - n.x() * n.x(), yy = 1 - n.y() * n.y(), zz = 1 - n.z() * n.z();
		final double xy = -2 * n.x() * n.y(), xz = -2 * n.x() * n.z(), yz = -2 * n.y() * n.z();
		final double bx = -2 * (xx * c.x() + xy * c.y() / 2 + xz * c.z() / 2);
		final double by = -2 * (xy * c.x() / 2 + yy * c.y() + yz * c.z() / 2);
		final double bz = -2 * (xz * c.x() / 2 + yz * c.y() / 2 + zz * c.z());
		final double constant = -(bx * c.x() + by * c.y() + bz * c.z()) / 2 - this.radius * this.radius;
		return new QuadraticEquation3d(xx, yy, zz, xy, xz, yz, bx, by, bz, constant);
	}

	@Override
	public List<MathConstraint3dc> constraints() {
		final Vector3dc n = this.frame.zAxis(), p = this.frame.origin();
		final double dot = n.dot(p);
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
