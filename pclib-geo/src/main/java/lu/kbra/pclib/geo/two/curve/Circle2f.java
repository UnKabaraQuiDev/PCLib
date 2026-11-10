package lu.kbra.pclib.geo.two.curve;

import lu.kbra.pclib.geo.Owned;

import java.util.Objects;

import org.joml.Vector2fc;

import lu.kbra.pclib.geo.Move;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem2fc;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation1to2fc;
import lu.kbra.pclib.geo.math.Vertex2f;
import lu.kbra.pclib.geo.math.Vertex2fc;

public class Circle2f implements Circle2fc, Cloneable {
	@Override
	public @Owned Circle2f clone() {
		return new Circle2f((Vertex2fc) center, radius);
	}

	private final Vertex2f center;
	private final float radius;

	public Circle2f(final Vector2fc center, final float radius) {
		this(new Vertex2f(center), radius);
	}

	public Circle2f(final Vertex2fc center, final float radius) {
		this(new Vertex2f(center), radius);
	}

	public Circle2f(final @Move Vertex2f center, final float radius) {
		if (!(radius > 0) || !Float.isFinite(radius)) {
			throw new IllegalArgumentException("Invalid radius");
		}
		this.center = Objects.requireNonNull(center);
		if (!this.center.isFinite()) {
			throw new IllegalArgumentException("Nonfinite center");
		}
		this.radius = radius;
	}

	@Override
	public Vertex2fc center() {
		return this.center;
	}

	@Override
	public float radius() {
		return this.radius;
	}

	@Override
	public ParametricEquation1to2fc parametricEquation() {
		return (a, dest) -> dest.set(this.center).add(this.radius * (float) Math.cos(a), this.radius * (float) Math.sin(a));
	}

	@Override
	public ImplicitSystem2fc implicitEquation() {
		return new ImplicitSystem2fc((x, y) -> (x - this.center.x) * (x - this.center.x) + (y - this.center.y) * (y - this.center.y)
				- this.radius * this.radius);
	}

}
