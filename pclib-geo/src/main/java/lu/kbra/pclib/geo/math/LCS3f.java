package lu.kbra.pclib.geo.math;

import java.util.Objects;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import lu.kbra.pclib.geo.Move;
import lu.kbra.pclib.geo.Owned;

/** Right-handed orthonormal frame; normal is local Z. */
public class LCS3f implements LCS3fc, Cloneable {
	private final Vertex3f origin;
	private final Axis3f x, y, z;

	public LCS3f() {
		this(new Vertex3f(), new Axis3f(1, 0, 0), new Axis3f(0, 1, 0), new Axis3f(0, 0, 1));
	}

	public LCS3f(final LCS3fc other) {
		this(new Vertex3f(other.origin()), new Axis3f(other.xAxis()), new Axis3f(other.yAxis()), new Axis3f(other.zAxis()));
	}

	public LCS3f(final Vector3fc origin, final Vector3fc normal) {
		this();
		this.setFromNormal(origin, normal);
	}

	public LCS3f(final Vertex3fc origin, final Axis3fc normal) {
		this(new Vertex3f(origin), new Axis3f(normal));
	}

	public LCS3f(final @Move Vertex3f origin, final @Move Axis3f normal) {
		this.origin = Objects.requireNonNull(origin);
		if (!this.origin.isFinite()) {
			throw new IllegalArgumentException("Nonfinite origin");
		}
		this.z = Objects.requireNonNull(normal);
		this.z.setUnit(normal);
		final Vector3f reference = Math.abs(this.z.z) < 0.9 ? new Vector3f(0, 0, 1) : new Vector3f(0, 1, 0);
		reference.cross(this.z).normalize();
		this.x = new Axis3f(reference);
		this.y = new Axis3f(new Vector3f(this.z).cross(this.x));
	}

	public LCS3f(final @Move Vertex3f origin, final @Move Axis3f x, final @Move Axis3f y, final @Move Axis3f z) {
		this.origin = Objects.requireNonNull(origin);
		this.x = Objects.requireNonNull(x);
		this.y = Objects.requireNonNull(y);
		this.z = Objects.requireNonNull(z);
		final Vector3f cross = new Vector3f(x).cross(y);
		if (!x.isFinite() || !y.isFinite() || !z.isFinite() || Math.abs(x.length() - 1) > 1e-4f || Math.abs(y.length() - 1) > 1e-4f
				|| Math.abs(z.length() - 1) > 1e-4f || Math.abs(x.dot(y)) > 1e-4f || Math.abs(x.dot(z)) > 1e-4f
				|| Math.abs(y.dot(z)) > 1e-4f || cross.distance(z) > 1e-4f) {
			throw new IllegalArgumentException("LCS must be right-handed and orthonormal");
		}
	}

	public LCS3f set(final LCS3fc other) {
		final LCS3f checked = new LCS3f(other);
		this.origin.set(checked.origin());
		this.x.set(checked.xAxis());
		this.y.set(checked.yAxis());
		this.z.set(checked.zAxis());
		return this;
	}

	public LCS3f setFromNormal(final Vector3fc center, final Vector3fc normal) {
		if (!center.isFinite()) {
			throw new IllegalArgumentException("Nonfinite frame origin");
		}
		final Axis3f normalized = new Axis3f(normal);
		this.origin.set(center);
		this.z.set(normalized);
		final Vector3f reference = Math.abs(this.z.z) < 0.9 ? new Vector3f(0, 0, 1) : new Vector3f(0, 1, 0);
		reference.cross(this.z).normalize();
		this.x.setUnit(reference);
		this.z.cross(this.x, reference);
		this.y.setUnit(reference);
		return this;
	}

	/** Uses the supplied line direction as local X, with a stable roll choice. */
	public LCS3f setFromXAxis(final Vector3fc center, final Vector3fc direction) {
		if (!center.isFinite()) {
			throw new IllegalArgumentException("Nonfinite frame origin");
		}
		final Axis3f normalized = new Axis3f(direction);
		this.origin.set(center);
		this.x.set(normalized);
		final Vector3f reference = Math.abs(this.x.z) < 0.9 ? new Vector3f(0, 0, 1) : new Vector3f(0, 1, 0);
		reference.cross(this.x).normalize();
		this.y.setUnit(reference);
		this.x.cross(this.y, reference);
		this.z.setUnit(reference);
		return this;
	}

	/** Builds a right-handed local frame from two independent surface tangents. */
	public LCS3f setFromTangents(final Vector3fc center, final Vector3fc tangentU, final Vector3fc tangentV) {
		if (!center.isFinite()) {
			throw new IllegalArgumentException("Nonfinite frame origin");
		}
		final Vector3f newX = new Vector3f(tangentU);
		final Vector3f newY = new Vector3f(tangentV);
		if (!newX.isFinite() || !newY.isFinite() || !(newX.lengthSquared() > 0)) {
			throw new IllegalArgumentException("Degenerate surface tangents");
		}
		newX.normalize();
		newY.fma(-newY.dot(newX), newX);
		if (!(newY.lengthSquared() > 0) || !newY.isFinite()) {
			throw new IllegalArgumentException("Dependent surface tangents");
		}
		newY.normalize();
		final Vector3f newZ = newX.cross(newY, new Vector3f());
		this.origin.set(center);
		this.x.setUnit(newX);
		this.y.setUnit(newY);
		this.z.setUnit(newZ);
		return this;
	}

	@Override
	public Vertex3fc origin() {
		return this.origin;
	}

	@Override
	public Axis3fc xAxis() {
		return this.x;
	}

	@Override
	public Axis3fc yAxis() {
		return this.y;
	}

	@Override
	public Axis3fc zAxis() {
		return this.z;
	}

	@Override
	public Vertex3f toWorld(final float a, final float b, final float c, final Vertex3f dest) {
		dest.set(this.origin).fma(a, this.x).fma(b, this.y).fma(c, this.z);
		return dest;
	}

	@Override
	public Vertex3f toLocal(final Vertex3fc p, final Vertex3f dest) {
		final float a = p.x() - this.origin.x, b = p.y() - this.origin.y, c = p.z() - this.origin.z;
		dest.set(a * this.x.x + b * this.x.y + c * this.x.z,
				a * this.y.x + b * this.y.y + c * this.y.z,
				a * this.z.x + b * this.z.y + c * this.z.z);
		return dest;
	}

	@Override
	public @Owned LCS3f clone() {
		return new LCS3f(this);
	}
}
