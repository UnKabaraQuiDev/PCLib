package lu.kbra.pclib.geo.two.curve;

import java.util.List;
import java.util.Objects;

import org.joml.Vector2f;
import org.joml.Vector2fc;

import lu.kbra.pclib.geo.Move;
import lu.kbra.pclib.geo.Owned;
import lu.kbra.pclib.geo.equation.constraint.MathConstraint2fc;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem2fc;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation1to2fc;
import lu.kbra.pclib.geo.math.Axis2f;
import lu.kbra.pclib.geo.math.Axis2fc;
import lu.kbra.pclib.geo.math.Vertex2f;
import lu.kbra.pclib.geo.math.Vertex2fc;

public class Ray2f implements Ray2fc, Cloneable {
	@Override
	public @Owned Ray2f clone() {
		return new Ray2f((Vertex2fc) origin, (Axis2fc) direction);
	}

	private final Vertex2f origin;
	private final Axis2f direction;

	public Ray2f(final Vector2fc o, final Vector2fc d) {
		this(new Vertex2f(o), new Axis2f(d));
	}

	public Ray2f(final Vertex2fc origin, final Axis2fc direction) {
		this(new Vertex2f(origin), new Axis2f(direction));
	}

	public Ray2f(final @Move Vertex2f origin, final @Move Axis2f direction) {
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
	public Vertex2fc origin() {
		return this.origin;
	}

	@Override
	public Axis2fc direction() {
		return this.direction;
	}

	@Override
	public ParametricEquation1to2fc parametricEquation() {
		return (t, dest) -> dest.set(this.origin).fma(t, this.direction);
	}

	@Override
	public ImplicitSystem2fc implicitEquation() {
		return new Line2f((Vertex2fc) this.origin, (Axis2fc) this.direction).implicitEquation();
	}

	@Override
	public @Owned List<MathConstraint2fc> constraints() {
		return List.of((point, p) -> new Vector2f(point).sub(this.origin).dot(this.direction) >= -p.distance());
	}

}
