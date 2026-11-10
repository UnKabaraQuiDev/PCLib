package lu.kbra.pclib.geo.three.curve;

import lu.kbra.pclib.geo.Geometry3fc;
import lu.kbra.pclib.geo.GeometryKind;
import lu.kbra.pclib.geo.Owned;
import lu.kbra.pclib.geo.equation.ImplicitRepresentable;
import lu.kbra.pclib.geo.equation.constraint.MathConstraint3fc;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem3fc;
import lu.kbra.pclib.geo.equation.parametric.ParameterDomain1d;
import lu.kbra.pclib.geo.equation.parametric.ParametricCurve3fc;
import lu.kbra.pclib.geo.math.Vertex3f;
import lu.kbra.pclib.geo.math.Vertex3fc;
import lu.kbra.pclib.geo.result.Empty;
import lu.kbra.pclib.geo.result.Result3;
import lu.kbra.pclib.geo.three.surface.Plane3fc;

public interface Segment3fc extends Geometry3fc, ParametricCurve3fc, ImplicitRepresentable<ImplicitSystem3fc, MathConstraint3fc> {
	Vertex3fc start();

	Vertex3fc end();

	@Override
	default ParameterDomain1d parameterDomain() {
		return ParameterDomain1d.UNIT;
	}

	Result3<@Owned Segment3f, @Owned Vertex3f, Empty> intersection(Plane3fc plane);

	Result3<@Owned Segment3f, @Owned Vertex3f, Empty> intersection(Ray3fc ray);

	@Override
	default GeometryKind geometryKind() {
		return GeometryKind.CURVE;
	}

	@Override
	default int intrinsicDimension() {
		return 1;
	}

	@Override
	default boolean isBounded() {
		return true;
	}
}
