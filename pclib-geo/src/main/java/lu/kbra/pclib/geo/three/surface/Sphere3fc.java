package lu.kbra.pclib.geo.three.surface;

import lu.kbra.pclib.geo.Geometry3fc;
import lu.kbra.pclib.geo.GeometryKind;
import lu.kbra.pclib.geo.Owned;
import lu.kbra.pclib.geo.equation.ImplicitRepresentable;
import lu.kbra.pclib.geo.equation.constraint.MathConstraint3fc;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem3fc;
import lu.kbra.pclib.geo.equation.parametric.ParametricSurface3fc;
import lu.kbra.pclib.geo.math.Axis3f;
import lu.kbra.pclib.geo.math.LCS3f;
import lu.kbra.pclib.geo.math.Vertex3f;
import lu.kbra.pclib.geo.math.Vertex3fc;
import lu.kbra.pclib.geo.result.Empty;
import lu.kbra.pclib.geo.result.Result3;
import lu.kbra.pclib.geo.three.curve.Circle3f;

public interface Sphere3fc extends Geometry3fc, ParametricSurface3fc, ImplicitRepresentable<ImplicitSystem3fc, MathConstraint3fc> {
	Vertex3fc center();

	float radius();

	default @Owned LCS3f localCoordinateSystem() {
		return new LCS3f();
	}

	@Override
	default @Owned Axis3f normalAt(final float u, final float v) {
		return new Axis3f(this.at(u, v).sub(this.center()));
	}

	Result3<@Owned Circle3f, @Owned Vertex3f, Empty> intersection(Plane3fc plane);

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
		return true;
	}
}
