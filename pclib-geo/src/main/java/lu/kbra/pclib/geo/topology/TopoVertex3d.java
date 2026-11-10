package lu.kbra.pclib.geo.topology;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import org.joml.Vector3dc;

import lu.kbra.pclib.geo.Owned;
import lu.kbra.pclib.geo.math.Vertex3d;

/** Stable topological identity; the geometric position is a defensive snapshot. */
public final class TopoVertex3d implements TopoElement3d {
	private static final AtomicLong NEXT_ID = new AtomicLong();
	private final long id = TopoVertex3d.NEXT_ID.incrementAndGet();
	private final Vertex3d position;

	public TopoVertex3d(final Vector3dc position) {
		this.position = new Vertex3d(Objects.requireNonNull(position));
		if (!this.position.isFinite()) {
			throw new IllegalArgumentException("Nonfinite vertex");
		}
	}

	public long id() {
		return this.id;
	}

	public @Owned Vertex3d position() {
		return new Vertex3d(this.position);
	}

	@Override
	public TopologyKind topologyKind() {
		return TopologyKind.VERTEX;
	}
}
