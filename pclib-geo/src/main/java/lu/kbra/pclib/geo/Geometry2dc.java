package lu.kbra.pclib.geo;

import lu.kbra.pclib.geo.math.Axis2d;
import lu.kbra.pclib.geo.math.Axis2dc;
import lu.kbra.pclib.geo.math.Vertex2d;
import lu.kbra.pclib.geo.math.Vertex2dc;

public interface Geometry2dc {

	Axis2dc X_POS_AXIS = new Axis2d(1, 0);
	Axis2dc Y_POS_AXIS = new Axis2d(0, 1);
	Axis2dc X_NEG_AXIS = new Axis2d(-1, 0);
	Axis2dc Y_NEG_AXIS = new Axis2d(0, -1);

	Vertex2dc ORIGIN = new Vertex2d(0, 0);

	GeometryKind geometryKind();

	int intrinsicDimension();

	default int ambientDimension() {
		return 2;
	}

	boolean isBounded();

	default boolean isFilled() {
		return this.geometryKind() == GeometryKind.REGION;
	}

}
