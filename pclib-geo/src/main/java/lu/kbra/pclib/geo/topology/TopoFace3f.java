package lu.kbra.pclib.geo.topology;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lu.kbra.pclib.geo.Owned;
import lu.kbra.pclib.geo.equation.parametric.ParametricSurface3fc;

/** A support surface bounded by a closed outer wire and zero or more closed holes. */
public final class TopoFace3f implements TopoElement3f {
	private final ParametricSurface3fc support;
	private final TopoWire3f outer;
	private final List<TopoWire3f> holes;
	private final boolean reversed;

	public TopoFace3f(final ParametricSurface3fc support, final TopoWire3f outer, final List<TopoWire3f> holes) {
		this(support, outer, holes, false);
	}

	public TopoFace3f(final ParametricSurface3fc support, final TopoWire3f outer, final List<TopoWire3f> holes, final boolean reversed) {
		this.support = Objects.requireNonNull(support);
		this.outer = Objects.requireNonNull(outer);
		this.holes = List.copyOf(holes);
		if (!outer.isClosed() || this.holes.stream().anyMatch(w -> !w.isClosed())) {
			throw new IllegalArgumentException("Face boundary wires must be closed");
		}
		this.reversed = reversed;
	}

	public ParametricSurface3fc support() {
		return this.support;
	}

	public TopoWire3f outer() {
		return this.outer;
	}

	public List<TopoWire3f> holes() {
		return this.holes;
	}

	public boolean reversed() {
		return this.reversed;
	}

	public @Owned List<TopoWire3f> boundaries() {
		final List<TopoWire3f> boundaries = new ArrayList<>(this.holes.size() + 1);
		boundaries.add(this.outer);
		boundaries.addAll(this.holes);
		return List.copyOf(boundaries);
	}

	@Override
	public TopologyKind topologyKind() {
		return TopologyKind.FACE;
	}
}
