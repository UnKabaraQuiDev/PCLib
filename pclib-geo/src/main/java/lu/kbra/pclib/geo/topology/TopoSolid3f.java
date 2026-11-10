package lu.kbra.pclib.geo.topology;

import java.util.List;
import java.util.Objects;

/**
 * One exterior closed shell and optional interior closed shells; volumetric validation is separate.
 */
public final class TopoSolid3f implements TopoElement3f {
	private final TopoShell3f exterior;
	private final List<TopoShell3f> cavities;

	public TopoSolid3f(final TopoShell3f exterior, final List<TopoShell3f> cavities) {
		this.exterior = Objects.requireNonNull(exterior);
		this.cavities = List.copyOf(cavities);
		if (!exterior.isClosedTopologically() || this.cavities.stream().anyMatch(s -> !s.isClosedTopologically())) {
			throw new IllegalArgumentException("Solid requires edge-closed shells");
		}
	}

	public TopoShell3f exterior() {
		return this.exterior;
	}

	public List<TopoShell3f> cavities() {
		return this.cavities;
	}

	@Override
	public TopologyKind topologyKind() {
		return TopologyKind.SOLID;
	}
}
