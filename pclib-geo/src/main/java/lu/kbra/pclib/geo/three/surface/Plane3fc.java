package lu.kbra.pclib.geo.three.surface;

import lu.kbra.pclib.geo.Geometry3fc;
import lu.kbra.pclib.geo.GeometryKind;
import lu.kbra.pclib.geo.Owned;
import lu.kbra.pclib.geo.equation.ImplicitRepresentable;
import lu.kbra.pclib.geo.equation.constraint.MathConstraint3fc;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem3fc;
import lu.kbra.pclib.geo.equation.parametric.ParametricSurface3fc;
import lu.kbra.pclib.geo.math.Axis3f;
import lu.kbra.pclib.geo.math.Axis3fc;
import lu.kbra.pclib.geo.math.LCS3f;
import lu.kbra.pclib.geo.math.Vertex3fc;
import lu.kbra.pclib.geo.result.Empty;
import lu.kbra.pclib.geo.result.Result3;
import lu.kbra.pclib.geo.three.curve.Line3f;

public interface Plane3fc extends Geometry3fc, ParametricSurface3fc, ImplicitRepresentable<ImplicitSystem3fc, MathConstraint3fc> {
	Vertex3fc origin();

	Axis3fc normal();

	default @Owned LCS3f localCoordinateSystem() {
		return this.localCoordinateSystem(new LCS3f());
	}

	default LCS3f localCoordinateSystem(final LCS3f dest) {
		return dest.setFromNormal(this.origin(), this.normal());
	}

	@Override
	default LCS3f localCoordinateSystemAt(final float u, final float v, final LCS3f dest) {
		return dest.setFromNormal(this.at(u, v), this.normal());
	}

	@Override
	default @Owned LCS3f localCoordinateSystemAt(final float u, final float v) {
		return this.localCoordinateSystemAt(u, v, new LCS3f());
	}

	@Override
	default @Owned Axis3f normalAt(final float u, final float v) {
		return new Axis3f(this.normal());
	}

	Result3<@Owned Plane3f, @Owned Line3f, Empty> intersection(Plane3fc other);

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
