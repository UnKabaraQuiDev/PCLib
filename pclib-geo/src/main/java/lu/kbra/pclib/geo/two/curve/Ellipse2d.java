package lu.kbra.pclib.geo.two.curve;

import java.util.Objects;

import org.joml.Vector2d;
import org.joml.Vector2dc;

import lu.kbra.pclib.geo.Move;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation1to2dc;
import lu.kbra.pclib.geo.math.Vertex2d;
import lu.kbra.pclib.geo.math.Vertex2dc;

/** Nonparallel semi-axis vectors also preserve affine shears. */
public class Ellipse2d implements Ellipse2dc {

	private final Vertex2d center;
	private final Vector2d u, v;

	public Ellipse2d(final Vertex2dc center, final Vector2dc u, final Vector2dc v) {
		this(new Vertex2d(center), new Vector2d(u), new Vector2d(v));
	}

	public Ellipse2d(final @Move Vertex2d center, final @Move Vector2d u, final @Move Vector2d v) {
		if (!center.isFinite() || !u.isFinite() || !v.isFinite() || !(Math.abs(u.x() * v.y() - u.y() * v.x()) >= 1e-14)) {
			throw new IllegalArgumentException("Degenerate ellipse");
		}
		this.center = Objects.requireNonNull(center);
		this.u = Objects.requireNonNull(u);
		this.v = Objects.requireNonNull(v);
	}

	@Override
	public Vertex2dc center() {
		return this.center;
	}

	@Override
	public Vector2dc semiAxisU() {
		return this.u;
	}

	@Override
	public Vector2dc semiAxisV() {
		return this.v;
	}

	@Override
	public ParametricEquation1to2dc parametricEquation() {
		return (t, dest) -> dest.set(this.center).fma(Math.cos(t), this.u).fma(Math.sin(t), this.v);
	}

}
