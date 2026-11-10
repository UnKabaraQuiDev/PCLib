package lu.kbra.pclib.geo.math;

public interface LCS3fc {
	Vertex3fc origin();

	Axis3fc xAxis();

	Axis3fc yAxis();

	Axis3fc zAxis();

	default Axis3fc normal() {
		return zAxis();
	}

	Vertex3f toWorld(float u, float v, float w, Vertex3f dest);

	Vertex3f toLocal(Vertex3fc point, Vertex3f dest);
}
