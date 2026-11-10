package lu.kbra.pclib.geo.topology;

import java.util.ArrayList;
import java.util.List;

import lu.kbra.pclib.geo.Owned;

/** Connected edge uses; open wires are allowed. Uses shared vertex identity, not coincidence. */
public final class TopoWire3f implements TopoElement3f {
	private final List<TopoEdgeUse3f> edges;
	private final boolean closed;

	public TopoWire3f(final List<TopoEdgeUse3f> edges) {
		if (edges.isEmpty()) {
			throw new IllegalArgumentException("Empty wire");
		}
		this.edges = List.copyOf(edges);
		for (int i = 1; i < this.edges.size(); i++) {
			if (this.edges.get(i - 1).end() != this.edges.get(i).start()) {
				throw new IllegalArgumentException("Disconnected edge uses at index " + i);
			}
		}
		this.closed = this.edges.get(this.edges.size() - 1).end() == this.edges.get(0).start();
	}

	public List<TopoEdgeUse3f> edges() {
		return this.edges;
	}

	public boolean isClosed() {
		return this.closed;
	}

	public TopoVertex3f start() {
		return this.edges.get(0).start();
	}

	public TopoVertex3f end() {
		return this.edges.get(this.edges.size() - 1).end();
	}

	public @Owned TopoWire3f reversed() {
		final List<TopoEdgeUse3f> uses = new ArrayList<>();
		for (int i = this.edges.size() - 1; i >= 0; i--) {
			uses.add(this.edges.get(i).reverse());
		}
		return new TopoWire3f(uses);
	}

	@Override
	public TopologyKind topologyKind() {
		return TopologyKind.WIRE;
	}
}
