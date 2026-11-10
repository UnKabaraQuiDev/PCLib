package lu.kbra.pclib.geo.three.surface;

import java.util.Objects;

import org.joml.Vector3d;
import org.joml.Vector3dc;

import lu.kbra.pclib.geo.Geometry3dc;
import lu.kbra.pclib.geo.GeometryKind;
import lu.kbra.pclib.geo.Owned;
import lu.kbra.pclib.geo.equation.ImplicitRepresentable;
import lu.kbra.pclib.geo.equation.constraint.MathConstraint3dc;
import lu.kbra.pclib.geo.equation.implicit.ImplicitEquation3dc;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem3dc;
import lu.kbra.pclib.geo.math.Axis3d;
import lu.kbra.pclib.geo.math.GeometryPrecision;

/** General implicit surface F(x,y,z)=0, without a fabricated global parameterization. */
public class ImplicitSurface3d implements Geometry3dc, ImplicitRepresentable<ImplicitSystem3dc, MathConstraint3dc> {
	private final ImplicitEquation3dc function;

	public ImplicitSurface3d(final ImplicitEquation3dc function) {
		this.function = Objects.requireNonNull(function);
	}

	@Override
	public ImplicitSystem3dc implicitEquation() {
		return new ImplicitSystem3dc(this.function);
	}

	public @Owned Axis3d normalAt(final Vector3dc p, final GeometryPrecision precision) {
		final double h = precision.distance() * 100;
		final double dx = this.function.value(p.x() + h, p.y(), p.z()) - this.function.value(p.x() - h, p.y(), p.z());
		final double dy = this.function.value(p.x(), p.y() + h, p.z()) - this.function.value(p.x(), p.y() - h, p.z());
		final double dz = this.function.value(p.x(), p.y(), p.z() + h) - this.function.value(p.x(), p.y(), p.z() - h);
		return new Axis3d(new Vector3d(dx, dy, dz));
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
