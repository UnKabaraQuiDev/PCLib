package lu.kbra.pclib.geo.two.curve;

import lu.kbra.pclib.geo.Geometry2fc;
import lu.kbra.pclib.geo.GeometryKind;
import lu.kbra.pclib.geo.equation.ImplicitRepresentable;
import lu.kbra.pclib.geo.equation.constraint.MathConstraint2fc;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem2fc;
import lu.kbra.pclib.geo.equation.parametric.ParameterDomain1d;
import lu.kbra.pclib.geo.equation.parametric.ParametricCurve2fc;
import lu.kbra.pclib.geo.math.Vertex2fc;

public interface Circle2fc extends Geometry2fc, ParametricCurve2fc, ImplicitRepresentable<ImplicitSystem2fc, MathConstraint2fc> {

	Vertex2fc center();

	float radius();

	@Override
	default ParameterDomain1d parameterDomain() {
		return new ParameterDomain1d(0, 2 * Math.PI);
	}

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
