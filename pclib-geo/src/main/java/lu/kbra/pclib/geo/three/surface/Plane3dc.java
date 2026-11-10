package lu.kbra.pclib.geo.three.surface;

import lu.kbra.pclib.geo.Geometry3dc;
import lu.kbra.pclib.geo.GeometryKind;
import lu.kbra.pclib.geo.Owned;
import lu.kbra.pclib.geo.equation.ImplicitRepresentable;
import lu.kbra.pclib.geo.equation.constraint.MathConstraint3dc;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem3dc;
import lu.kbra.pclib.geo.equation.parametric.ParametricSurface3dc;
import lu.kbra.pclib.geo.math.Axis3d;
import lu.kbra.pclib.geo.math.Axis3dc;
import lu.kbra.pclib.geo.math.LCS3d;
import lu.kbra.pclib.geo.math.Vertex3dc;
import lu.kbra.pclib.geo.result.Empty;
import lu.kbra.pclib.geo.result.Result3;
import lu.kbra.pclib.geo.three.curve.Line3d;

public interface Plane3dc extends Geometry3dc, ParametricSurface3dc, ImplicitRepresentable<ImplicitSystem3dc, MathConstraint3dc> {
	Vertex3dc origin();

	Axis3dc normal();

	default @Owned LCS3d localCoordinateSystem() {
		return this.localCoordinateSystem(new LCS3d());
	}

	default LCS3d localCoordinateSystem(final LCS3d dest) {
		return dest.setFromNormal(this.origin(), this.normal());
	}

	@Override
	default LCS3d localCoordinateSystemAt(final double u, final double v, final LCS3d dest) {
		return dest.setFromNormal(this.at(u, v), this.normal());
	}

	@Override
	default @Owned LCS3d localCoordinateSystemAt(final double u, final double v) {
		return this.localCoordinateSystemAt(u, v, new LCS3d());
	}

	@Override
	default @Owned Axis3d normalAt(final double u, final double v) {
		return new Axis3d(this.normal());
	}

	Result3<@Owned Plane3d, @Owned Line3d, Empty> intersection(Plane3dc other);

	@Override
	default GeometryKind geometryKind() {
		return GeometryKind.SURFACE;
	}

	@Override
	default int intrinsicDimension() {
		return 2;
	}

	@Override
	default boolean isBounded() {
		return false;
	}
}
