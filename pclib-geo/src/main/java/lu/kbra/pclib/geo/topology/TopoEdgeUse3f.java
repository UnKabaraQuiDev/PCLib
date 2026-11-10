package lu.kbra.pclib.geo.topology;

import java.util.Objects;

/** A reference to an edge with orientation in a particular wire. */
public record TopoEdgeUse3f(TopoEdge3f edge, boolean reversed) {
	public TopoEdgeUse3f {
		Objects.requireNonNull(edge);
	}

	public TopoEdgeUse3f(final TopoEdge3f edge) {
		this(edge, false);
	}

	public TopoVertex3f start() {
		return this.reversed ? this.edge.end() : this.edge.start();
	}

	public TopoVertex3f end() {
		return this.reversed ? this.edge.start() : this.edge.end();
	}

	public TopoEdgeUse3f reverse() {
		return new TopoEdgeUse3f(this.edge, !this.reversed);
	}
}
