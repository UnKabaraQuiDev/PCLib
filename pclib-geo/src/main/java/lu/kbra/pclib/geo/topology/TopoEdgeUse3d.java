package lu.kbra.pclib.geo.topology;

import java.util.Objects;

/** A reference to an edge with orientation in a particular wire. */
public record TopoEdgeUse3d(TopoEdge3d edge, boolean reversed) {
	public TopoEdgeUse3d {
		Objects.requireNonNull(edge);
	}

	public TopoEdgeUse3d(final TopoEdge3d edge) {
		this(edge, false);
	}

	public TopoVertex3d start() {
		return this.reversed ? this.edge.end() : this.edge.start();
	}

	public TopoVertex3d end() {
		return this.reversed ? this.edge.start() : this.edge.end();
	}

	public TopoEdgeUse3d reverse() {
		return new TopoEdgeUse3d(this.edge, !this.reversed);
	}
}
