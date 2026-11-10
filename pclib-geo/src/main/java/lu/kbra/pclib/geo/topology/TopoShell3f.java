package lu.kbra.pclib.geo.topology;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/** Joined faces. Closure checks edge incidence and direction, not geometric self-intersection. */
public final class TopoShell3f implements TopoElement3f {
	private final List<TopoFace3f> faces;

	public TopoShell3f(final List<TopoFace3f> faces) {
		if (faces.isEmpty()) {
			throw new IllegalArgumentException("Empty shell");
		}
		this.faces = List.copyOf(faces);
	}

	public List<TopoFace3f> faces() {
		return this.faces;
	}

	public boolean isClosedTopologically() {
		final Map<TopoEdge3f, int[]> incidence = new IdentityHashMap<>();
		for (final TopoFace3f face : this.faces) {
			for (final TopoWire3f wire : face.boundaries()) {
				for (final TopoEdgeUse3f use : wire.edges()) {
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
