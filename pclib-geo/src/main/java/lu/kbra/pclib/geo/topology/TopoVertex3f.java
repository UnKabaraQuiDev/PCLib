package lu.kbra.pclib.geo.topology;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import org.joml.Vector3fc;

import lu.kbra.pclib.geo.Owned;
import lu.kbra.pclib.geo.math.Vertex3f;

/** Stable topological identity; the geometric position is a defensive snapshot. */
public final class TopoVertex3f implements TopoElement3f {
	private static final AtomicLong NEXT_ID = new AtomicLong();
	private final long id = TopoVertex3f.NEXT_ID.incrementAndGet();
	private final Vertex3f position;

	public TopoVertex3f(final Vector3fc position) {
		this.position = new Vertex3f(Objects.requireNonNull(position));
		if (!this.position.isFinite()) {
			throw new IllegalArgumentException("Nonfinite vertex");
		}
	}

	public long id() {
		return this.id;
	}

	public @Owned Vertex3f position() {
		return new Vertex3f(this.position);
	}

	@Override
	public TopologyKind topologyKind() {
		return TopologyKind.VERTEX;
	}
}
