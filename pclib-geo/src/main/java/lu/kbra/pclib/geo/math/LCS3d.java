package lu.kbra.pclib.geo.math;

import java.util.Objects;

import org.joml.Vector3d;
import org.joml.Vector3dc;

import lu.kbra.pclib.geo.Move;
import lu.kbra.pclib.geo.Owned;

/** Right-handed orthonormal frame; normal is local Z. */
public class LCS3d implements LCS3dc, Cloneable {
	private final Vertex3d origin;
	private final Axis3d x, y, z;

	public LCS3d() {
		this(new Vertex3d(), new Axis3d(1, 0, 0), new Axis3d(0, 1, 0), new Axis3d(0, 0, 1));
	}

	public LCS3d(final LCS3dc other) {
		this(new Vertex3d(other.origin()), new Axis3d(other.xAxis()), new Axis3d(other.yAxis()), new Axis3d(other.zAxis()));
	}

	public LCS3d(final Vector3dc origin, final Vector3dc normal) {
		this();
		this.setFromNormal(origin, normal);
	}

	public LCS3d(final Vertex3dc origin, final Axis3dc normal) {
		this(new Vertex3d(origin), new Axis3d(normal));
	}

	public LCS3d(final @Move Vertex3d origin, final @Move Axis3d normal) {
		this.origin = Objects.requireNonNull(origin);
		if (!this.origin.isFinite()) {
			throw new IllegalArgumentException("Nonfinite origin");
		}
		this.z = Objects.requireNonNull(normal);
		this.z.setUnit(normal);
		final Vector3d reference = Math.abs(this.z.z) < 0.9 ? new Vector3d(0, 0, 1) : new Vector3d(0, 1, 0);
		reference.cross(this.z).normalize();
		this.x = new Axis3d(reference);
		this.y = new Axis3d(new Vector3d(this.z).cross(this.x));
	}

	public LCS3d(final @Move Vertex3d origin, final @Move Axis3d x, final @Move Axis3d y, final @Move Axis3d z) {
		this.origin = Objects.requireNonNull(origin);
		this.x = Objects.requireNonNull(x);
		this.y = Objects.requireNonNull(y);
		this.z = Objects.requireNonNull(z);
		final Vector3d cross = new Vector3d(x).cross(y);
		if (!x.isFinite() || !y.isFinite() || !z.isFinite() || Math.abs(x.length() - 1) > 1e-8 || Math.abs(y.length() - 1) > 1e-8
				|| Math.abs(z.length() - 1) > 1e-8 || Math.abs(x.dot(y)) > 1e-8 || Math.abs(x.dot(z)) > 1e-8 || Math.abs(y.dot(z)) > 1e-8
				|| cross.distance(z) > 1e-8) {
			throw new IllegalArgumentException("LCS must be right-handed and orthonormal");
		}
	}

	public LCS3d set(final LCS3dc other) {
		final LCS3d checked = new LCS3d(other);
		this.origin.set(checked.origin());
		this.x.set(checked.xAxis());
		this.y.set(checked.yAxis());
		this.z.set(checked.zAxis());
		return this;
	}

	public LCS3d setFromNormal(final Vector3dc center, final Vector3dc normal) {
		if (!center.isFinite()) {
			throw new IllegalArgumentException("Nonfinite frame origin");
		}
		final Axis3d normalized = new Axis3d(normal);
		this.origin.set(center);
		this.z.set(normalized);
		final Vector3d reference = Math.abs(this.z.z) < 0.9 ? new Vector3d(0, 0, 1) : new Vector3d(0, 1, 0);
		reference.cross(this.z).normalize();
		this.x.setUnit(reference);
		this.z.cross(this.x, reference);
		this.y.setUnit(reference);
		return this;
	}

	/** Uses the supplied line direction as local X, with a stable roll choice. */
	public LCS3d setFromXAxis(final Vector3dc center, final Vector3dc direction) {
		if (!center.isFinite()) {
			throw new IllegalArgumentException("Nonfinite frame origin");
		}
		final Axis3d normalized = new Axis3d(direction);
		this.origin.set(center);
		this.x.set(normalized);
		final Vector3d reference = Math.abs(this.x.z) < 0.9 ? new Vector3d(0, 0, 1) : new Vector3d(0, 1, 0);
		reference.cross(this.x).normalize();
		this.y.setUnit(reference);
		this.x.cross(this.y, reference);
		this.z.setUnit(reference);
		return this;
	}

	/** Builds a right-handed local frame from two independent surface tangents. */
	public LCS3d setFromTangents(final Vector3dc center, final Vector3dc tangentU, final Vector3dc tangentV) {
		if (!center.isFinite()) {
			throw new IllegalArgumentException("Nonfinite frame origin");
		}
		final Vector3d newX = new Vector3d(tangentU);
		final Vector3d newY = new Vector3d(tangentV);
		if (!newX.isFinite() || !newY.isFinite() || !(newX.lengthSquared() > 0)) {
			throw new IllegalArgumentException("Degenerate surface tangents");
		}
		newX.normalize();
		newY.fma(-newY.dot(newX), newX);
		if (!(newY.lengthSquared() > 0) || !newY.isFinite()) {
			throw new IllegalArgumentException("Dependent surface tangents");
		}
		newY.normalize();
		final Vector3d newZ = newX.cross(newY, new Vector3d());
		this.origin.set(center);
		this.x.setUnit(newX);
		this.y.setUnit(newY);
		this.z.setUnit(newZ);
		return this;
	}

	@Override
	public Vertex3dc origin() {
		return this.origin;
	}

	@Override
	public Axis3dc xAxis() {
		return this.x;
	}

	@Override
	public Axis3dc yAxis() {
		return this.y;
	}

	@Override
	public Axis3dc zAxis() {
		return this.z;
	}

	@Override
	public Vertex3d toWorld(final double a, final double b, final double c, final Vertex3d dest) {
		dest.set(this.origin).fma(a, this.x).fma(b, this.y).fma(c, this.z);
		return dest;
	}

	@Override
	public Vertex3d toLocal(final Vertex3dc p, final Vertex3d dest) {
		final double a = p.x() - this.origin.x, b = p.y() - this.origin.y, c = p.z() - this.origin.z;
		dest.set(a * this.x.x + b * this.x.y + c * this.x.z,
				a * this.y.x + b * this.y.y + c * this.y.z,
				a * this.z.x + b * this.z.y + c * this.z.z);
		return dest;
	}

	@Override
	public @Owned LCS3d clone() {
		return new LCS3d(this);
	}
}
