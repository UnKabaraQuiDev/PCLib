package lu.kbra.pclib.geo;

import lu.kbra.pclib.geo.math.Axis3f;
import lu.kbra.pclib.geo.math.Axis3fc;
import lu.kbra.pclib.geo.math.Vertex3f;
import lu.kbra.pclib.geo.math.Vertex3fc;
import lu.kbra.pclib.geo.three.surface.Plane3f;
import lu.kbra.pclib.geo.three.surface.Plane3fc;

public interface Geometry3fc {

	Axis3fc X_POS_AXIS = new Axis3f(1, 0, 0);
	Axis3fc Y_POS_AXIS = new Axis3f(0, 1, 0);
	Axis3fc Z_POS_AXIS = new Axis3f(0, 0, 1);
	Axis3fc X_NEG_AXIS = new Axis3f(-1, 0, 0);
	Axis3fc Y_NEG_AXIS = new Axis3f(0, -1, 0);
	Axis3fc Z_NEG_AXIS = new Axis3f(0, 0, -1);

	Vertex3fc ORIGIN = new Vertex3f(0, 0, 0);

	Plane3fc XY_PLANE = new Plane3f(ORIGIN, Z_POS_AXIS);
	Plane3fc YZ_PLANE = new Plane3f(ORIGIN, X_POS_AXIS);
	Plane3fc XZ_PLANE = new Plane3f(ORIGIN, Y_POS_AXIS);

	GeometryKind geometryKind();

	int intrinsicDimension();

	default int ambientDimension() {
		return 3;
	}

	boolean isBounded();

}
