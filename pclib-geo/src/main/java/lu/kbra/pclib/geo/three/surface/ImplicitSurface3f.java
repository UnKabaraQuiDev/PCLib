package lu.kbra.pclib.geo.three.surface;

import java.util.Objects;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import lu.kbra.pclib.geo.Geometry3fc;
import lu.kbra.pclib.geo.GeometryKind;
import lu.kbra.pclib.geo.Owned;
import lu.kbra.pclib.geo.equation.ImplicitRepresentable;
import lu.kbra.pclib.geo.equation.constraint.MathConstraint3fc;
import lu.kbra.pclib.geo.equation.implicit.ImplicitEquation3fc;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem3fc;
import lu.kbra.pclib.geo.math.Axis3f;
import lu.kbra.pclib.geo.math.GeometryPrecision;

/** General implicit surface F(x,y,z)=0, without a fabricated global parameterization. */
public class ImplicitSurface3f implements Geometry3fc, ImplicitRepresentable<ImplicitSystem3fc, MathConstraint3fc> {
	private final ImplicitEquation3fc function;

	public ImplicitSurface3f(final ImplicitEquation3fc function) {
		this.function = Objects.requireNonNull(function);
	}

	@Override
	public ImplicitSystem3fc implicitEquation() {
		return new ImplicitSystem3fc(this.function);
	}

	public @Owned Axis3f normalAt(final Vector3fc p, final GeometryPrecision precision) {
		final float h = (float) Math.max(precision.distance() * 100, 1e-3);
		final float dx = this.function.value(p.x() + h, p.y(), p.z()) - this.function.value(p.x() - h, p.y(), p.z());
		final float dy = this.function.value(p.x(), p.y() + h, p.z()) - this.function.value(p.x(), p.y() - h, p.z());
		final float dz = this.function.value(p.x(), p.y(), p.z() + h) - this.function.value(p.x(), p.y(), p.z() - h);
		return new Axis3f(new Vector3f(dx, dy, dz));
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
		return false;
	}
}
