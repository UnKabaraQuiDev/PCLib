package lu.kbra.pclib.geo.three.curve;

import org.joml.Vector3dc;

import lu.kbra.pclib.geo.Geometry3dc;
import lu.kbra.pclib.geo.GeometryKind;
import lu.kbra.pclib.geo.equation.parametric.ParameterDomain1d;
import lu.kbra.pclib.geo.equation.parametric.ParametricCurve3dc;
import lu.kbra.pclib.geo.math.Vertex3dc;

public interface Ellipse3dc extends Geometry3dc, ParametricCurve3dc {
	Vertex3dc center();

	Vector3dc semiAxisU();

	Vector3dc semiAxisV();

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
