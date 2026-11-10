package lu.kbra.pclib.geo.topology;

import java.util.List;
import java.util.Objects;

/**
 * One exterior closed shell and optional interior closed shells; volumetric validation is separate.
 */
public final class TopoSolid3d implements TopoElement3d {
	private final TopoShell3d exterior;
	private final List<TopoShell3d> cavities;

	public TopoSolid3d(final TopoShell3d exterior, final List<TopoShell3d> cavities) {
		this.exterior = Objects.requireNonNull(exterior);
		this.cavities = List.copyOf(cavities);
		if (!exterior.isClosedTopologically() || this.cavities.stream().anyMatch(s -> !s.isClosedTopologically())) {
			throw new IllegalArgumentException("Solid requires edge-closed shells");
		}
	}

	public TopoShell3d exterior() {
		return this.exterior;
	}

	public List<TopoShell3d> cavities() {
		return this.cavities;
	}

	@Override
	public TopologyKind topologyKind() {
		return TopologyKind.SOLID;
	}
}
