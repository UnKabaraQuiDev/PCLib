package lu.kbra.pclib.geo.math;

public interface LCS2fc {
	Vertex2fc origin();

	Axis2fc xAxis();

	Axis2fc yAxis();

	Vertex2f toWorld(float u, float v, Vertex2f dest);

	Vertex2f toLocal(Vertex2fc point, Vertex2f dest);
}
