package lu.kbra.pclib.geo.topology;

import java.util.List;

/** Disconnected or mixed-dimensional topological elements; no implied union or volume. */
public final class TopoCompound3f implements TopoElement3f {

	private final List<TopoElement3f> elements;

	public TopoCompound3f(final List<? extends TopoElement3f> elements) {
		this.elements = List.copyOf(elements);
	}

	public List<TopoElement3f> elements() {
		return this.elements;
	}

	@Override
	public TopologyKind topologyKind() {
		return TopologyKind.COMPOUND;
	}

}
