package lu.kbra.pclib.geo.math;

import java.util.Objects;

import org.joml.Vector2fc;

import lu.kbra.pclib.geo.Move;
import lu.kbra.pclib.geo.Owned;

public class LCS2f implements LCS2fc, Cloneable {
	private final Vertex2f origin;
	private final Axis2f x, y;

	public LCS2f() {
		this(new Vertex2f(), new Axis2f());
	}

	public LCS2f(final LCS2fc source) {
		this(new Vertex2f(source.origin()), new Axis2f(source.xAxis()));
	}

	public LCS2f(final Vector2fc origin, final Vector2fc xAxis) {
		this(new Vertex2f(origin), new Axis2f(xAxis));
	}

	public LCS2f(final Vertex2fc origin, final Axis2fc xAxis) {
		this(new Vertex2f(origin), new Axis2f(xAxis));
	}

	public LCS2f(final @Move Vertex2f origin, final @Move Axis2f xAxis) {
		this.origin = Objects.requireNonNull(origin);
		if (!this.origin.isFinite()) {
			throw new IllegalArgumentException("Nonfinite origin");
		}
		this.x = Objects.requireNonNull(xAxis);
		if (!this.x.isFinite() || !(this.x.lengthSquared() > 0)) {
			throw new IllegalArgumentException("Invalid X axis");
		}
		this.x.normalize();
		this.y = new Axis2f(-this.x.y, this.x.x);
	}

	public LCS2f set(final LCS2fc other) {
		return this.set(other.origin(), other.xAxis());
	}

	public LCS2f set(final Vector2fc center, final Vector2fc direction) {
		if (!center.isFinite()) {
			throw new IllegalArgumentException("Nonfinite frame origin");
		}
		final Axis2f normalized = new Axis2f(direction);
		this.origin.set(center);
		this.x.set(normalized);
		this.y.set(-this.x.y, this.x.x);
		return this;
	}

	@Override
	public Vertex2fc origin() {
		return this.origin;
	}

	@Override
	public Axis2fc xAxis() {
		return this.x;
	}

	@Override
	public Axis2fc yAxis() {
		return this.y;
	}

	@Override
	public Vertex2f toWorld(final float u, final float v, final Vertex2f dest) {
		dest.set(this.origin).fma(u, this.x).fma(v, this.y);
		return dest;
	}

	@Override
	public Vertex2f toLocal(final Vertex2fc point, final Vertex2f dest) {
		final float dx = point.x() - this.origin.x, dy = point.y() - this.origin.y;
		dest.set(dx * this.x.x + dy * this.x.y, dx * this.y.x + dy * this.y.y);
		return dest;
	}

	@Override
	public @Owned LCS2f clone() {
		return new LCS2f(this);
	}
}
