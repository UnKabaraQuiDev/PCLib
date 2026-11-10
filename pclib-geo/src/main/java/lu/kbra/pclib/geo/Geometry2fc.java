package lu.kbra.pclib.geo;

import lu.kbra.pclib.geo.math.Axis2f;
import lu.kbra.pclib.geo.math.Axis2fc;
import lu.kbra.pclib.geo.math.Vertex2f;
import lu.kbra.pclib.geo.math.Vertex2fc;

public interface Geometry2fc {

	Axis2fc X_POS_AXIS = new Axis2f(1, 0);
	Axis2fc Y_POS_AXIS = new Axis2f(0, 1);
	Axis2fc X_NEG_AXIS = new Axis2f(-1, 0);
	Axis2fc Y_NEG_AXIS = new Axis2f(0, -1);

	Vertex2fc ORIGIN = new Vertex2f(0, 0);

	GeometryKind geometryKind();

	int intrinsicDimension();

	default int ambientDimension() {
		return 2;
	}

	boolean isBounded();

}
