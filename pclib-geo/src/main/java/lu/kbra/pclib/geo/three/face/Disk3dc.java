package lu.kbra.pclib.geo.three.face;

import lu.kbra.pclib.geo.Geometry3dc;
import lu.kbra.pclib.geo.GeometryKind;
import lu.kbra.pclib.geo.equation.parametric.ParametricSurface3dc;
import lu.kbra.pclib.geo.math.Axis3dc;
import lu.kbra.pclib.geo.math.Vertex3dc;

public interface Disk3dc extends Geometry3dc, ParametricSurface3dc {
	Vertex3dc center();

	Axis3dc normal();

	double radius();

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
