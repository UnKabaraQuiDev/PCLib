package lu.kbra.pclib.geo.math;

import java.util.Objects;

import org.joml.Vector2dc;

import lu.kbra.pclib.geo.Move;
import lu.kbra.pclib.geo.Owned;

public class LCS2d implements LCS2dc, Cloneable {
	private final Vertex2d origin;
	private final Axis2d x, y;

	public LCS2d() {
		this(new Vertex2d(), new Axis2d());
	}

	public LCS2d(final LCS2dc source) {
		this(new Vertex2d(source.origin()), new Axis2d(source.xAxis()));
	}

	public LCS2d(final Vector2dc origin, final Vector2dc xAxis) {
		this(new Vertex2d(origin), new Axis2d(xAxis));
	}

	public LCS2d(final Vertex2dc origin, final Axis2dc xAxis) {
		this(new Vertex2d(origin), new Axis2d(xAxis));
	}

	public LCS2d(final @Move Vertex2d origin, final @Move Axis2d xAxis) {
		this.origin = Objects.requireNonNull(origin);
		if (!this.origin.isFinite()) {
			throw new IllegalArgumentException("Nonfinite origin");
		}
		this.x = Objects.requireNonNull(xAxis);
		if (!this.x.isFinite() || !(this.x.lengthSquared() > 0)) {
			throw new IllegalArgumentException("Invalid X axis");
		}
		this.x.normalize();
		this.y = new Axis2d(-this.x.y, this.x.x);
	}

	public LCS2d set(final LCS2dc other) {
		return this.set(other.origin(), other.xAxis());
	}

	public LCS2d set(final Vector2dc center, final Vector2dc direction) {
		if (!center.isFinite()) {
			throw new IllegalArgumentException("Nonfinite frame origin");
		}
		final Axis2d normalized = new Axis2d(direction);
		this.origin.set(center);
		this.x.set(normalized);
		this.y.set(-this.x.y, this.x.x);
		return this;
	}

	@Override
	public Vertex2dc origin() {
		return this.origin;
	}

	@Override
	public Axis2dc xAxis() {
		return this.x;
	}

	@Override
	public Axis2dc yAxis() {
		return this.y;
	}

	@Override
	public Vertex2d toWorld(final double u, final double v, final Vertex2d dest) {
		dest.set(this.origin).fma(u, this.x).fma(v, this.y);
		return dest;
	}

	@Override
	public Vertex2d toLocal(final Vertex2dc point, final Vertex2d dest) {
		final double dx = point.x() - this.origin.x, dy = point.y() - this.origin.y;
		dest.set(dx * this.x.x + dy * this.x.y, dx * this.y.x + dy * this.y.y);
		return dest;
	}

	@Override
	public @Owned LCS2d clone() {
		return new LCS2d(this);
	}
}
