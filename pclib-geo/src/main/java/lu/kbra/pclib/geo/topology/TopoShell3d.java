package lu.kbra.pclib.geo.topology;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/** Joined faces. Closure checks edge incidence and direction, not geometric self-intersection. */
public final class TopoShell3d implements TopoElement3d {
	private final List<TopoFace3d> faces;

	public TopoShell3d(final List<TopoFace3d> faces) {
		if (faces.isEmpty()) {
			throw new IllegalArgumentException("Empty shell");
		}
		this.faces = List.copyOf(faces);
	}

	public List<TopoFace3d> faces() {
		return this.faces;
	}

	public boolean isClosedTopologically() {
		final Map<TopoEdge3d, int[]> incidence = new IdentityHashMap<>();
		for (final TopoFace3d face : this.faces) {
			for (final TopoWire3d wire : face.boundaries()) {
				for (final TopoEdgeUse3d use : wire.edges()) {
					final int[] counts = incidence.computeIfAbsent(use.edge(), e -> new int[2]);
					counts[use.reversed() ^ face.reversed() ? 1 : 0]++;
				}
			}
		}
		return incidence.values().stream().allMatch(counts -> counts[0] == 1 && counts[1] == 1);
	}

	@Override
	public TopologyKind topologyKind() {
		return TopologyKind.SHELL;
	}
}
