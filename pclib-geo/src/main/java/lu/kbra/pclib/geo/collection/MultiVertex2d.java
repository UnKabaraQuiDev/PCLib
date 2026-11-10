package lu.kbra.pclib.geo.collection;

import java.util.List;

import lu.kbra.pclib.geo.math.Vertex2dc;

public final class MultiVertex2d extends GeometryCollection2d<Vertex2dc> {
	public MultiVertex2d(final List<? extends Vertex2dc> points) {
		super(points);
	}
}
