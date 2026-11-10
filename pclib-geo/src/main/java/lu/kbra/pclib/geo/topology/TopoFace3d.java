package lu.kbra.pclib.geo.topology;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lu.kbra.pclib.geo.Owned;
import lu.kbra.pclib.geo.equation.parametric.ParametricSurface3dc;

/** A support surface bounded by a closed outer wire and zero or more closed holes. */
public final class TopoFace3d implements TopoElement3d {
	private final ParametricSurface3dc support;
	private final TopoWire3d outer;
	private final List<TopoWire3d> holes;
	private final boolean reversed;

	public TopoFace3d(final ParametricSurface3dc support, final TopoWire3d outer, final List<TopoWire3d> holes) {
		this(support, outer, holes, false);
	}

	public TopoFace3d(final ParametricSurface3dc support, final TopoWire3d outer, final List<TopoWire3d> holes, final boolean reversed) {
		this.support = Objects.requireNonNull(support);
		this.outer = Objects.requireNonNull(outer);
		this.holes = List.copyOf(holes);
		if (!outer.isClosed() || this.holes.stream().anyMatch(w -> !w.isClosed())) {
			throw new IllegalArgumentException("Face boundary wires must be closed");
		}
		this.reversed = reversed;
	}

	public ParametricSurface3dc support() {
		return this.support;
	}

	public TopoWire3d outer() {
		return this.outer;
	}

	public List<TopoWire3d> holes() {
		return this.holes;
	}

	public boolean reversed() {
		return this.reversed;
	}

	public @Owned List<TopoWire3d> boundaries() {
		final List<TopoWire3d> boundaries = new ArrayList<>(this.holes.size() + 1);
		boundaries.add(this.outer);
		boundaries.addAll(this.holes);
		return List.copyOf(boundaries);
	}

	@Override
	public TopologyKind topologyKind() {
		return TopologyKind.FACE;
	}
}
