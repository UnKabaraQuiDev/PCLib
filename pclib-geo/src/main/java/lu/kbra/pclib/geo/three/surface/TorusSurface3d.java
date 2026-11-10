package lu.kbra.pclib.geo.three.surface;

import org.joml.Vector3d;
import org.joml.Vector3dc;

import lu.kbra.pclib.geo.Geometry3dc;
import lu.kbra.pclib.geo.GeometryKind;
import lu.kbra.pclib.geo.Owned;
import lu.kbra.pclib.geo.equation.ImplicitRepresentable;
import lu.kbra.pclib.geo.equation.constraint.MathConstraint3dc;
import lu.kbra.pclib.geo.equation.implicit.ImplicitEquation3dc;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem3dc;
import lu.kbra.pclib.geo.equation.parametric.ParameterDomain1d;
import lu.kbra.pclib.geo.equation.parametric.ParameterDomain2d;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation2to3dc;
import lu.kbra.pclib.geo.equation.parametric.ParametricSurface3dc;
import lu.kbra.pclib.geo.math.Axis3d;
import lu.kbra.pclib.geo.math.LCS3d;
import lu.kbra.pclib.geo.math.Vertex3d;

/** Embedded ring torus. The quartic implicit scalar is not yet solved by PolynomialSystemSolver. */
public final class TorusSurface3d
		implements
			Geometry3dc,
			ParametricSurface3dc,
			ImplicitRepresentable<ImplicitSystem3dc, MathConstraint3dc> {
	private final LCS3d frame;
	private final double majorRadius, minorRadius;

	public TorusSurface3d(final Vector3dc center, final Vector3dc axis, final double majorRadius, final double minorRadius) {
		if (!(majorRadius > minorRadius) || !(minorRadius > 0) || !Double.isFinite(majorRadius) || !Double.isFinite(minorRadius)) {
			throw new IllegalArgumentException("Ring torus requires finite major radius > minor radius > 0");
		}
		this.frame = new LCS3d(center, axis);
		this.majorRadius = majorRadius;
		this.minorRadius = minorRadius;
	}

	public @Owned LCS3d localCoordinateSystem() {
		return this.frame.clone();
	}

	public double majorRadius() {
		return this.majorRadius;
	}

	public double minorRadius() {
		return this.minorRadius;
	}

	@Override
	public ParameterDomain2d parameterDomain() {
		return new ParameterDomain2d(new ParameterDomain1d(0, 2 * Math.PI), new ParameterDomain1d(0, 2 * Math.PI));
	}

	@Override
	public ParametricEquation2to3dc parametricEquation() {
		return (u, v, dest) -> {
			final double radial = this.majorRadius + this.minorRadius * Math.cos(v);
			return dest.set(this.frame.origin())
					.fma(radial * Math.cos(u), this.frame.xAxis())
					.fma(radial * Math.sin(u), this.frame.yAxis())
					.fma(this.minorRadius * Math.sin(v), this.frame.zAxis());
		};
	}

	@Override
	public @Owned Axis3d normalAt(final double u, final double v) {
		final double cv = Math.cos(v), sv = Math.sin(v);
		final Vector3d radial = new Vector3d(this.frame.xAxis()).mul(Math.cos(u)).fma(Math.sin(u), this.frame.yAxis());
		return new Axis3d(radial.mul(cv).fma(sv, this.frame.zAxis()));
	}

	public ImplicitEquation3dc quarticEquation() {
		return (x, y, z) -> {
			final Vector3d p = this.frame.toLocal(new Vertex3d(x, y, z), new Vertex3d());
			final double q = p.lengthSquared() + this.majorRadius * this.majorRadius - this.minorRadius * this.minorRadius;
			return q * q - 4 * this.majorRadius * this.majorRadius * (p.x * p.x + p.y * p.y);
		};
	}

	@Override
	public ImplicitSystem3dc implicitEquation() {
		return new ImplicitSystem3dc(this.quarticEquation());
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
