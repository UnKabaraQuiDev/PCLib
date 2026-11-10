package lu.kbra.pclib.geo.topology;

import java.util.List;

/** Disconnected or mixed-dimensional topological elements; no implied union or volume. */
public final class TopoCompound3d implements TopoElement3d {

	private final List<TopoElement3d> elements;

	public TopoCompound3d(final List<? extends TopoElement3d> elements) {
		this.elements = List.copyOf(elements);
	}

	public List<TopoElement3d> elements() {
		return this.elements;
	}

	@Override
	public TopologyKind topologyKind() {
		return TopologyKind.COMPOUND;
	}

}
