package lu.kbra.pclib.geo.three.face;

import lu.kbra.pclib.geo.Geometry3fc;
import lu.kbra.pclib.geo.GeometryKind;
import lu.kbra.pclib.geo.equation.parametric.ParametricSurface3fc;
import lu.kbra.pclib.geo.math.Axis3fc;
import lu.kbra.pclib.geo.math.Vertex3fc;

public interface Disk3fc extends Geometry3fc, ParametricSurface3fc {
	Vertex3fc center();

	Axis3fc normal();

	float radius();

	@Override
	default GeometryKind geometryKind() {
		return GeometryKind.REGION;
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
