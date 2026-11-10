package lu.kbra.pclib.geo.collection;

import java.util.List;

import lu.kbra.pclib.geo.math.Vertex2fc;

public final class MultiVertex2f extends GeometryCollection2f<Vertex2fc> {
	public MultiVertex2f(final List<? extends Vertex2fc> points) {
		super(points);
	}
}
