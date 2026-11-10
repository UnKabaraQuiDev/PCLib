package lu.kbra.pclib.geo.two.curve;

import lu.kbra.pclib.geo.Owned;

import java.util.List;
import java.util.Objects;

import org.joml.Vector2f;

import lu.kbra.pclib.geo.Move;
import lu.kbra.pclib.geo.equation.constraint.MathConstraint2fc;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem2fc;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation1to2fc;
import lu.kbra.pclib.geo.math.Axis2f;
import lu.kbra.pclib.geo.math.Vertex2f;
import lu.kbra.pclib.geo.math.Vertex2fc;

public class Segment2f implements Segment2fc, Cloneable {
	@Override
	public @Owned Segment2f clone() {
		return new Segment2f((Vertex2fc) start, (Vertex2fc) end);
	}

	private final Vertex2f start;
	private final Vertex2f end;

	public Segment2f(final Vertex2fc start, final Vertex2fc end) {
		this(new Vertex2f(start), new Vertex2f(end));
	}

	public Segment2f(final @Move Vertex2f start, final @Move Vertex2f end) {
		if (!start.isFinite() || !end.isFinite() || !(new Vector2f(start).distanceSquared(end) > 0)) {
			throw new IllegalArgumentException("Degenerate segment");
		}
		this.start = Objects.requireNonNull(start);
		this.end = Objects.requireNonNull(end);
	}

	@Override
	public Vertex2fc start() {
		return this.start;
	}

	@Override
	public Vertex2fc end() {
		return this.end;
	}

	@Override
	public ParametricEquation1to2fc parametricEquation() {
		final Vector2f delta = new Vector2f(this.end).sub(this.start);
		return (t, dest) -> dest.set(this.start).fma(t, delta);
	}

	@Override
	public ImplicitSystem2fc implicitEquation() {
		return new Line2f((Vertex2fc) this.start, new Axis2f(new Vector2f(this.end).sub(this.start))).implicitEquation();
	}

	@Override
	public List<MathConstraint2fc> constraints() {
		final Vector2f delta = new Vector2f(this.end).sub(this.start);
		final float len = delta.lengthSquared();
		return List.of((point, p) -> {
			final float t = new Vector2f(point).sub(this.start).dot(delta) / len;
			return t >= -p.parameter() && t <= 1 + p.parameter();
		});
	}

}
