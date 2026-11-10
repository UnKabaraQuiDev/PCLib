package lu.kbra.pclib.geo.collection;

import java.util.List;

import lu.kbra.pclib.geo.Geometry3fc;
import lu.kbra.pclib.geo.GeometryKind;

/** Mixed-dimensional, possibly disconnected 3D geometry without implicit Boolean semantics. */
public class GeometryCollection3f<T extends Geometry3fc> implements Geometry3fc {
	private final List<T> components;

	public GeometryCollection3f(final List<? extends T> components) {
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
		return this.components.stream().mapToInt(Geometry3fc::intrinsicDimension).max().orElse(-1);
	}

	@Override
	public boolean isBounded() {
		return this.components.stream().allMatch(Geometry3fc::isBounded);
	}
}
