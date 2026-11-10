package lu.kbra.pclib.geo.two.curve;

import java.util.List;
import java.util.Objects;

import org.joml.Vector2d;
import org.joml.Vector2dc;

import lu.kbra.pclib.geo.Move;
import lu.kbra.pclib.geo.Owned;
import lu.kbra.pclib.geo.equation.constraint.MathConstraint2dc;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem2dc;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation1to2dc;
import lu.kbra.pclib.geo.math.Axis2d;
import lu.kbra.pclib.geo.math.Axis2dc;
import lu.kbra.pclib.geo.math.Vertex2d;
import lu.kbra.pclib.geo.math.Vertex2dc;

public class Ray2d implements Ray2dc {

	private final Vertex2d origin;
	private final Axis2d direction;

	public Ray2d(final Vector2dc o, final Vector2dc d) {
		this(new Vertex2d(o), new Axis2d(d));
	}

	public Ray2d(final Vertex2dc origin, final Axis2dc direction) {
		this(new Vertex2d(origin), new Axis2d(direction));
	}

	public Ray2d(final @Move Vertex2d origin, final @Move Axis2d direction) {
		this.origin = Objects.requireNonNull(origin);
		if (!this.origin.isFinite()) {
			throw new IllegalArgumentException("Nonfinite origin");
		}
		this.direction = Objects.requireNonNull(direction);
		if (!this.direction.isFinite() || !(this.direction.lengthSquared() > 0)) {
			throw new IllegalArgumentException("Invalid direction");
		}
		this.direction.normalize();
	}

	@Override
	public Vertex2dc origin() {
		return this.origin;
	}

	@Override
	public Axis2dc direction() {
		return this.direction;
	}

	@Override
	public ParametricEquation1to2dc parametricEquation() {
		return (t, dest) -> dest.set(this.origin).fma(t, this.direction);
	}

	@Override
	public ImplicitSystem2dc implicitEquation() {
		return new Line2d((Vertex2dc) this.origin, (Axis2dc) this.direction).implicitEquation();
	}

	@Override
	public @Owned List<MathConstraint2dc> constraints() {
		return List.of((point, p) -> new Vector2d(point).sub(this.origin).dot(this.direction) >= -p.distance());
	}

}
