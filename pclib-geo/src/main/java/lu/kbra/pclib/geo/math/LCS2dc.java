package lu.kbra.pclib.geo.math;

public interface LCS2dc {
	Vertex2dc origin();

	Axis2dc xAxis();

	Axis2dc yAxis();

	Vertex2d toWorld(double u, double v, Vertex2d dest);

	Vertex2d toLocal(Vertex2dc point, Vertex2d dest);
}
