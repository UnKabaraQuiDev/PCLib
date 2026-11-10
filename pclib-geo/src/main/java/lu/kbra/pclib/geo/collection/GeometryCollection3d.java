package lu.kbra.pclib.geo.collection;

import java.util.List;

import lu.kbra.pclib.geo.Geometry3dc;
import lu.kbra.pclib.geo.GeometryKind;

/** Mixed-dimensional, possibly disconnected 3D geometry without implicit Boolean semantics. */
public class GeometryCollection3d<T extends Geometry3dc> implements Geometry3dc {
	private final List<T> components;

	public GeometryCollection3d(final List<? extends T> components) {
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
		return this.components.stream().mapToInt(Geometry3dc::intrinsicDimension).max().orElse(-1);
	}

	@Override
	public boolean isBounded() {
		return this.components.stream().allMatch(Geometry3dc::isBounded);
	}
}
