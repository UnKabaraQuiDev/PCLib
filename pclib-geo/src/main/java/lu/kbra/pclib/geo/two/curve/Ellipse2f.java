package lu.kbra.pclib.geo.two.curve;

import java.util.Objects;

import org.joml.Vector2f;
import org.joml.Vector2fc;

import lu.kbra.pclib.geo.Move;
import lu.kbra.pclib.geo.Owned;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation1to2fc;
import lu.kbra.pclib.geo.math.Vertex2f;
import lu.kbra.pclib.geo.math.Vertex2fc;

/** Nonparallel semi-axis vectors also preserve affine shears. */
public class Ellipse2f implements Ellipse2fc, Cloneable {
	@Override
	public @Owned Ellipse2f clone() {
		return new Ellipse2f((Vertex2fc) center, u, v);
	}

	private final Vertex2f center;
	private final Vector2f u, v;

	public Ellipse2f(final Vertex2fc center, final Vector2fc u, final Vector2fc v) {
		this(new Vertex2f(center), new Vector2f(u), new Vector2f(v));
	}

	public Ellipse2f(final @Move Vertex2f center, final @Move Vector2f u, final @Move Vector2f v) {
		if (!center.isFinite() || !u.isFinite() || !v.isFinite() || !(Math.abs(u.x() * v.y() - u.y() * v.x()) >= 1e-14)) {
			throw new IllegalArgumentException("Degenerate ellipse");
		}
		this.center = Objects.requireNonNull(center);
		this.u = Objects.requireNonNull(u);
		this.v = Objects.requireNonNull(v);
	}

	@Override
	public Vertex2fc center() {
		return this.center;
	}

	@Override
	public Vector2fc semiAxisU() {
		return this.u;
	}

	@Override
	public Vector2fc semiAxisV() {
		return this.v;
	}

	@Override
	public ParametricEquation1to2fc parametricEquation() {
		return (t, dest) -> dest.set(this.center).fma((float) Math.cos(t), this.u).fma((float) Math.sin(t), this.v);
	}

}
