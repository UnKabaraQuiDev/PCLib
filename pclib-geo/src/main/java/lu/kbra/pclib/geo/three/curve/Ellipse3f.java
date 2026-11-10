package lu.kbra.pclib.geo.three.curve;

import java.util.Objects;

import org.joml.Matrix4x3fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import lu.kbra.pclib.geo.Move;
import lu.kbra.pclib.geo.Owned;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation1to3fc;
import lu.kbra.pclib.geo.math.Vertex3f;
import lu.kbra.pclib.geo.math.Vertex3fc;

/** Semi-axis vectors need not be perpendicular after an affine transformation. */
public class Ellipse3f implements Ellipse3fc, Cloneable {
	@Override
	public @Owned Ellipse3f clone() {
		return new Ellipse3f((Vector3fc) center, u, v);
	}
	private final Vertex3f center;
	private final Vector3f u, v;

	public Ellipse3f(final Vector3fc center, final Vector3fc u, final Vector3fc v) {
		this(new Vertex3f(center), new Vector3f(u), new Vector3f(v));
	}

	public Ellipse3f(final @Move Vertex3f center, final @Move Vector3f u, final @Move Vector3f v) {
		if (!center.isFinite() || !u.isFinite() || !v.isFinite() || !(new Vector3f(u).cross(v).lengthSquared() >= 1e-12f)) {
			throw new IllegalArgumentException("Degenerate ellipse");
		}
		this.center = Objects.requireNonNull(center);
		this.u = Objects.requireNonNull(u);
		this.v = Objects.requireNonNull(v);
	}

	public Ellipse3f set(final Vector3fc c, final Vector3fc a, final Vector3fc b) {
		if (!c.isFinite() || !a.isFinite() || !b.isFinite() || !(new Vector3f(a).cross(b).lengthSquared() >= 1e-12f)) {
			throw new IllegalArgumentException("Degenerate ellipse");
		}
		this.center.set(c);
		this.u.set(a);
		this.v.set(b);
		return this;
	}

	@Override
	public Vertex3fc center() {
		return this.center;
	}

	@Override
	public Vector3fc semiAxisU() {
		return this.u;
	}

	@Override
	public Vector3fc semiAxisV() {
		return this.v;
	}

	@Override
	public ParametricEquation1to3fc parametricEquation() {
		return (t, dest) -> dest.set(this.center).fma((float) Math.cos(t), this.u).fma((float) Math.sin(t), this.v);
	}

	public Ellipse3f transform(final Matrix4x3fc m) {
		final Vector3f c = m.transformPosition(this.center, new Vector3f());
		final Vector3f a = m.transformDirection(this.u, new Vector3f());
		final Vector3f b = m.transformDirection(this.v, new Vector3f());
		return this.set(c, a, b);
	}

	public @Owned Ellipse3f transformed(final Matrix4x3fc m) {
		return new Ellipse3f((Vector3fc) this.center, this.u, this.v).transform(m);
	}
}
