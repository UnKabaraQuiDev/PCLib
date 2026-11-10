package lu.kbra.pclib.geo.collection;

import java.util.List;

import lu.kbra.pclib.geo.math.Vertex3fc;

public final class MultiVertex3f extends GeometryCollection3f<Vertex3fc> {
	public MultiVertex3f(final List<? extends Vertex3fc> points) {
		super(points);
	}
}
