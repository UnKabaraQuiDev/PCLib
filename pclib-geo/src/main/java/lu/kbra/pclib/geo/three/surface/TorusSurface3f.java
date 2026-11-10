package lu.kbra.pclib.geo.three.surface;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import lu.kbra.pclib.geo.Geometry3fc;
import lu.kbra.pclib.geo.GeometryKind;
import lu.kbra.pclib.geo.Owned;
import lu.kbra.pclib.geo.equation.ImplicitRepresentable;
import lu.kbra.pclib.geo.equation.constraint.MathConstraint3fc;
import lu.kbra.pclib.geo.equation.implicit.ImplicitEquation3fc;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem3fc;
import lu.kbra.pclib.geo.equation.parametric.ParameterDomain1d;
import lu.kbra.pclib.geo.equation.parametric.ParameterDomain2d;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation2to3fc;
import lu.kbra.pclib.geo.equation.parametric.ParametricSurface3fc;
import lu.kbra.pclib.geo.math.Axis3f;
import lu.kbra.pclib.geo.math.LCS3f;
import lu.kbra.pclib.geo.math.Vertex3f;

/** Embedded ring torus. The quartic implicit scalar is not yet solved by PolynomialSystemSolver. */
public final class TorusSurface3f
		implements
			Geometry3fc,
			ParametricSurface3fc,
			ImplicitRepresentable<ImplicitSystem3fc, MathConstraint3fc> {
	private final LCS3f frame;
	private final float majorRadius, minorRadius;

	public TorusSurface3f(final Vector3fc center, final Vector3fc axis, final float majorRadius, final float minorRadius) {
		if (!(majorRadius > minorRadius) || !(minorRadius > 0) || !Float.isFinite(majorRadius) || !Float.isFinite(minorRadius)) {
			throw new IllegalArgumentException("Ring torus requires finite major radius > minor radius > 0");
		}
		this.frame = new LCS3f(center, axis);
		this.majorRadius = majorRadius;
		this.minorRadius = minorRadius;
	}

	public @Owned LCS3f localCoordinateSystem() {
		return this.frame.clone();
	}

	public float majorRadius() {
		return this.majorRadius;
	}

	public float minorRadius() {
		return this.minorRadius;
	}

	@Override
	public ParameterDomain2d parameterDomain() {
		return new ParameterDomain2d(new ParameterDomain1d(0, 2 * Math.PI), new ParameterDomain1d(0, 2 * Math.PI));
	}

	@Override
	public ParametricEquation2to3fc parametricEquation() {
		return (u, v, dest) -> {
			final float radial = this.majorRadius + this.minorRadius * (float) Math.cos(v);
			return dest.set(this.frame.origin())
					.fma(radial * (float) Math.cos(u), this.frame.xAxis())
					.fma(radial * (float) Math.sin(u), this.frame.yAxis())
					.fma(this.minorRadius * (float) Math.sin(v), this.frame.zAxis());
		};
	}

	@Override
	public @Owned Axis3f normalAt(final float u, final float v) {
		final float cv = (float) Math.cos(v), sv = (float) Math.sin(v);
		final Vector3f radial = new Vector3f(this.frame.xAxis()).mul((float) Math.cos(u)).fma((float) Math.sin(u), this.frame.yAxis());
		return new Axis3f(radial.mul(cv).fma(sv, this.frame.zAxis()));
	}

	public ImplicitEquation3fc quarticEquation() {
		return (x, y, z) -> {
			final Vector3f p = this.frame.toLocal(new Vertex3f(x, y, z), new Vertex3f());
			final float q = p.lengthSquared() + this.majorRadius * this.majorRadius - this.minorRadius * this.minorRadius;
			return q * q - 4 * this.majorRadius * this.majorRadius * (p.x * p.x + p.y * p.y);
		};
	}

	@Override
	public ImplicitSystem3fc implicitEquation() {
		return new ImplicitSystem3fc(this.quarticEquation());
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
