package lu.kbra.pclib.geo.three.curve;

import lu.kbra.pclib.geo.Geometry3dc;
import lu.kbra.pclib.geo.GeometryKind;
import lu.kbra.pclib.geo.Owned;
import lu.kbra.pclib.geo.equation.ImplicitRepresentable;
import lu.kbra.pclib.geo.equation.constraint.MathConstraint3dc;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem3dc;
import lu.kbra.pclib.geo.equation.parametric.ParameterDomain1d;
import lu.kbra.pclib.geo.equation.parametric.ParametricCurve3dc;
import lu.kbra.pclib.geo.math.Axis3dc;
import lu.kbra.pclib.geo.math.LCS3d;
import lu.kbra.pclib.geo.math.Vertex3dc;

public interface Circle3dc extends Geometry3dc, ParametricCurve3dc, ImplicitRepresentable<ImplicitSystem3dc, MathConstraint3dc> {
	Vertex3dc center();

	Axis3dc normal();

	double radius();

	default @Owned LCS3d localCoordinateSystem() {
		return new LCS3d(this.center(), this.normal());
	}

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
