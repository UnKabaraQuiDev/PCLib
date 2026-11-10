package lu.kbra.pclib.geo.topology;

import java.util.ArrayList;
import java.util.List;

import lu.kbra.pclib.geo.Owned;

/** Connected edge uses; open wires are allowed. Uses shared vertex identity, not coincidence. */
public final class TopoWire3d implements TopoElement3d {
	private final List<TopoEdgeUse3d> edges;
	private final boolean closed;

	public TopoWire3d(final List<TopoEdgeUse3d> edges) {
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

	public List<TopoEdgeUse3d> edges() {
		return this.edges;
	}

	public boolean isClosed() {
		return this.closed;
	}

	public TopoVertex3d start() {
		return this.edges.get(0).start();
	}

	public TopoVertex3d end() {
		return this.edges.get(this.edges.size() - 1).end();
	}

	public @Owned TopoWire3d reversed() {
		final List<TopoEdgeUse3d> uses = new ArrayList<>();
		for (int i = this.edges.size() - 1; i >= 0; i--) {
			uses.add(this.edges.get(i).reverse());
		}
		return new TopoWire3d(uses);
	}

	@Override
	public TopologyKind topologyKind() {
		return TopologyKind.WIRE;
	}
}
