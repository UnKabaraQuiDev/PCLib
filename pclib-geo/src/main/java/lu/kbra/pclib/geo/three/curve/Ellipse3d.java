package lu.kbra.pclib.geo.three.curve;

import java.util.Objects;

import org.joml.Matrix4x3dc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import lu.kbra.pclib.geo.Move;
import lu.kbra.pclib.geo.Owned;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation1to3dc;
import lu.kbra.pclib.geo.math.Vertex3d;
import lu.kbra.pclib.geo.math.Vertex3dc;

/** Semi-axis vectors need not be perpendicular after an affine transformation. */
public class Ellipse3d implements Ellipse3dc {
	private final Vertex3d center;
	private final Vector3d u, v;

	public Ellipse3d(final Vector3dc center, final Vector3dc u, final Vector3dc v) {
		this(new Vertex3d(center), new Vector3d(u), new Vector3d(v));
	}

	public Ellipse3d(final @Move Vertex3d center, final @Move Vector3d u, final @Move Vector3d v) {
		if (!center.isFinite() || !u.isFinite() || !v.isFinite() || !(new Vector3d(u).cross(v).lengthSquared() >= 1e-24)) {
			throw new IllegalArgumentException("Degenerate ellipse");
		}
		this.center = Objects.requireNonNull(center);
		this.u = Objects.requireNonNull(u);
		this.v = Objects.requireNonNull(v);
	}

	public Ellipse3d set(final Vector3dc c, final Vector3dc a, final Vector3dc b) {
		if (!c.isFinite() || !a.isFinite() || !b.isFinite() || !(new Vector3d(a).cross(b).lengthSquared() >= 1e-24)) {
			throw new IllegalArgumentException("Degenerate ellipse");
		}
		this.center.set(c);
		this.u.set(a);
		this.v.set(b);
		return this;
	}

	@Override
	public Vertex3dc center() {
		return this.center;
	}

	@Override
	public Vector3dc semiAxisU() {
		return this.u;
	}

	@Override
	public Vector3dc semiAxisV() {
		return this.v;
	}

	@Override
	public ParametricEquation1to3dc parametricEquation() {
		return (t, dest) -> dest.set(this.center).fma(Math.cos(t), this.u).fma(Math.sin(t), this.v);
	}

	public Ellipse3d transform(final Matrix4x3dc m) {
		final Vector3d c = m.transformPosition(this.center, new Vector3d());
		final Vector3d a = m.transformDirection(this.u, new Vector3d());
		final Vector3d b = m.transformDirection(this.v, new Vector3d());
		return this.set(c, a, b);
	}

	public @Owned Ellipse3d transformed(final Matrix4x3dc m) {
		return new Ellipse3d((Vector3dc) this.center, this.u, this.v).transform(m);
	}
}
