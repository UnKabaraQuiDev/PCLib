package lu.kbra.pclib.geo.two.curve;

import lu.kbra.pclib.geo.Geometry2dc;
import lu.kbra.pclib.geo.GeometryKind;
import lu.kbra.pclib.geo.equation.ImplicitRepresentable;
import lu.kbra.pclib.geo.equation.constraint.MathConstraint2dc;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem2dc;
import lu.kbra.pclib.geo.equation.parametric.ParameterDomain1d;
import lu.kbra.pclib.geo.equation.parametric.ParametricCurve2dc;
import lu.kbra.pclib.geo.math.Vertex2dc;

public interface Segment2dc extends Geometry2dc, ParametricCurve2dc, ImplicitRepresentable<ImplicitSystem2dc, MathConstraint2dc> {

	Vertex2dc start();

	Vertex2dc end();

	@Override
	default ParameterDomain1d parameterDomain() {
		return ParameterDomain1d.UNIT;
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
