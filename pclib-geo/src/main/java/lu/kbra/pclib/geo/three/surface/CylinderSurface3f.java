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

/** Lateral surface of a right circular cylinder (end disks are not part of this shell). */
public final class CylinderSurface3f
		implements
			Geometry3fc,
			ParametricSurface3fc,
			ImplicitRepresentable<ImplicitSystem3fc, MathConstraint3fc> {
	private final LCS3f frame;
	private final float radius, height;

	public CylinderSurface3f(final Vector3fc base, final Vector3fc axis, final float radius, final float height) {
		if (!(radius > 0) || !(height > 0) || !Float.isFinite(radius) || !Float.isFinite(height)) {
			throw new IllegalArgumentException("Cylinder dimensions must be positive and finite");
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
		return (u, v, dest) -> dest.set(this.frame.origin())
				.fma(this.radius * (float) Math.cos(u), this.frame.xAxis())
				.fma(this.radius * (float) Math.sin(u), this.frame.yAxis())
				.fma(v, this.frame.zAxis());
	}

	@Override
	public @Owned Axis3f normalAt(final float u, final float v) {
		return new Axis3f(new Vector3f(this.frame.xAxis()).mul((float) Math.cos(u)).fma((float) Math.sin(u), this.frame.yAxis()));
	}

	@Override
	public ImplicitSystem3fc implicitEquation() {
		return new ImplicitSystem3fc(this.radialEquation());
	}

	public QuadraticEquation3f radialEquation() {
		final Vector3fc n = this.frame.zAxis(), c = this.frame.origin();
		final float xx = 1 - n.x() * n.x(), yy = 1 - n.y() * n.y(), zz = 1 - n.z() * n.z();
		final float xy = -2 * n.x() * n.y(), xz = -2 * n.x() * n.z(), yz = -2 * n.y() * n.z();
		final float bx = -2 * (xx * c.x() + xy * c.y() / 2 + xz * c.z() / 2);
		final float by = -2 * (xy * c.x() / 2 + yy * c.y() + yz * c.z() / 2);
		final float bz = -2 * (xz * c.x() / 2 + yz * c.y() / 2 + zz * c.z());
		final float constant = -(bx * c.x() + by * c.y() + bz * c.z()) / 2 - this.radius * this.radius;
		return new QuadraticEquation3f(xx, yy, zz, xy, xz, yz, bx, by, bz, constant);
	}

	@Override
	public List<MathConstraint3fc> constraints() {
		final Vector3fc n = this.frame.zAxis(), p = this.frame.origin();
		final float dot = n.dot(p);
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
