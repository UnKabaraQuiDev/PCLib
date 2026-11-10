package lu.kbra.pclib.geo;

import lu.kbra.pclib.geo.math.Axis3d;
import lu.kbra.pclib.geo.math.Axis3dc;
import lu.kbra.pclib.geo.math.Vertex3d;
import lu.kbra.pclib.geo.math.Vertex3dc;
import lu.kbra.pclib.geo.three.surface.Plane3d;
import lu.kbra.pclib.geo.three.surface.Plane3dc;

public interface Geometry3dc {

	Axis3dc X_POS_AXIS = new Axis3d(1, 0, 0);
	Axis3dc Y_POS_AXIS = new Axis3d(0, 1, 0);
	Axis3dc Z_POS_AXIS = new Axis3d(0, 0, 1);
	Axis3dc X_NEG_AXIS = new Axis3d(-1, 0, 0);
	Axis3dc Y_NEG_AXIS = new Axis3d(0, -1, 0);
	Axis3dc Z_NEG_AXIS = new Axis3d(0, 0, -1);

	Vertex3dc ORIGIN = new Vertex3d(0, 0, 0);

	Plane3dc XY_PLANE = new Plane3d(ORIGIN, Z_POS_AXIS);
	Plane3dc YZ_PLANE = new Plane3d(ORIGIN, X_POS_AXIS);
	Plane3dc XZ_PLANE = new Plane3d(ORIGIN, Y_POS_AXIS);

	GeometryKind geometryKind();

	int intrinsicDimension();

	default int ambientDimension() {
		return 3;
	}

	boolean isBounded();

	default boolean isFilled() {
		return this.geometryKind() == GeometryKind.SOLID || this.geometryKind() == GeometryKind.REGION;
	}

}
