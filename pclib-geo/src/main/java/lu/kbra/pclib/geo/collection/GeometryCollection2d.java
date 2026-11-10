package lu.kbra.pclib.geo.collection;

import java.util.List;

import lu.kbra.pclib.geo.Geometry2dc;
import lu.kbra.pclib.geo.GeometryKind;

/** Mixed-dimensional, possibly disconnected 2D geometry without implicit Boolean semantics. */
public class GeometryCollection2d<T extends Geometry2dc> implements Geometry2dc {
	private final List<T> components;

	public GeometryCollection2d(final List<? extends T> components) {
		this.components = List.copyOf(components);
	}

	public List<T> components() {
		return this.components;
	}

	@Override
	public GeometryKind geometryKind() {
		return GeometryKind.COMPOSITE;
	}

	/** Empty collections have intrinsic dimension -1. */
	@Override
	public int intrinsicDimension() {
		return this.components.stream().mapToInt(Geometry2dc::intrinsicDimension).max().orElse(-1);
	}

	@Override
	public boolean isBounded() {
		return this.components.stream().allMatch(Geometry2dc::isBounded);
	}
}
