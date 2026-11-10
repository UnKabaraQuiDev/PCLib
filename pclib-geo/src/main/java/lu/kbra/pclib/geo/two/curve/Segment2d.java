package lu.kbra.pclib.geo.two.curve;

import java.util.List;
import java.util.Objects;

import org.joml.Vector2d;

import lu.kbra.pclib.geo.Move;
import lu.kbra.pclib.geo.equation.constraint.MathConstraint2dc;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem2dc;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation1to2dc;
import lu.kbra.pclib.geo.math.Axis2d;
import lu.kbra.pclib.geo.math.Vertex2d;
import lu.kbra.pclib.geo.math.Vertex2dc;

public class Segment2d implements Segment2dc {

	private final Vertex2d start;
	private final Vertex2d end;

	public Segment2d(final Vertex2dc start, final Vertex2dc end) {
		this(new Vertex2d(start), new Vertex2d(end));
	}

	public Segment2d(final @Move Vertex2d start, final @Move Vertex2d end) {
		if (!start.isFinite() || !end.isFinite() || !(new Vector2d(start).distanceSquared(end) > 0)) {
			throw new IllegalArgumentException("Degenerate segment");
		}
		this.start = Objects.requireNonNull(start);
		this.end = Objects.requireNonNull(end);
	}

	@Override
	public Vertex2dc start() {
		return this.start;
	}

	@Override
	public Vertex2dc end() {
		return this.end;
	}

	@Override
	public ParametricEquation1to2dc parametricEquation() {
		final Vector2d delta = new Vector2d(this.end).sub(this.start);
		return (t, dest) -> dest.set(this.start).fma(t, delta);
	}

	@Override
	public ImplicitSystem2dc implicitEquation() {
		return new Line2d((Vertex2dc) this.start, new Axis2d(new Vector2d(this.end).sub(this.start))).implicitEquation();
	}

	@Override
	public List<MathConstraint2dc> constraints() {
		final Vector2d delta = new Vector2d(this.end).sub(this.start);
		final double len = delta.lengthSquared();
		return List.of((point, p) -> {
			final double t = new Vector2d(point).sub(this.start).dot(delta) / len;
			return t >= -p.parameter() && t <= 1 + p.parameter();
		});
	}

}
