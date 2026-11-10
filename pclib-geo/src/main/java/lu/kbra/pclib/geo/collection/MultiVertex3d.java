package lu.kbra.pclib.geo.collection;

import java.util.List;

import lu.kbra.pclib.geo.math.Vertex3dc;

public final class MultiVertex3d extends GeometryCollection3d<Vertex3dc> {
	public MultiVertex3d(final List<? extends Vertex3dc> points) {
		super(points);
	}
}
