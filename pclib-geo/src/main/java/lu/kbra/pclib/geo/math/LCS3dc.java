package lu.kbra.pclib.geo.math;

public interface LCS3dc {
	Vertex3dc origin();

	Axis3dc xAxis();

	Axis3dc yAxis();

	Axis3dc zAxis();

	default Axis3dc normal() {
		return this.zAxis();
	}

	Vertex3d toWorld(double x, double y, double z, Vertex3d dest);

	Vertex3d toLocal(Vertex3dc point, Vertex3d dest);
}
