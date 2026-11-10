package lu.kbra.pclib.geo.two.curve;

import lu.kbra.pclib.geo.Geometry2dc;
import lu.kbra.pclib.geo.GeometryKind;
import lu.kbra.pclib.geo.equation.ImplicitRepresentable;
import lu.kbra.pclib.geo.equation.constraint.MathConstraint2dc;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem2dc;
import lu.kbra.pclib.geo.equation.parametric.ParameterDomain1d;
import lu.kbra.pclib.geo.equation.parametric.ParametricCurve2dc;
import lu.kbra.pclib.geo.math.Axis2dc;
import lu.kbra.pclib.geo.math.Vertex2dc;

public interface Ray2dc extends Geometry2dc, ParametricCurve2dc, ImplicitRepresentable<ImplicitSystem2dc, MathConstraint2dc> {

	Vertex2dc origin();

	Axis2dc direction();

	@Override
	default ParameterDomain1d parameterDomain() {
		return ParameterDomain1d.POSITIVE;
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
		return false;
	}

}
