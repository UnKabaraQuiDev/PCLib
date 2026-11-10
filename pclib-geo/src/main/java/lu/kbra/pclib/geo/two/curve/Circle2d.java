package lu.kbra.pclib.geo.two.curve;

import java.util.Objects;

import org.joml.Vector2dc;

import lu.kbra.pclib.geo.Move;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem2dc;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation1to2dc;
import lu.kbra.pclib.geo.math.Vertex2d;
import lu.kbra.pclib.geo.math.Vertex2dc;

public class Circle2d implements Circle2dc {

	private final Vertex2d center;
	private final double radius;

	public Circle2d(final Vector2dc center, final double radius) {
		this(new Vertex2d(center), radius);
	}

	public Circle2d(final Vertex2dc center, final double radius) {
		this(new Vertex2d(center), radius);
	}

	public Circle2d(final @Move Vertex2d center, final double radius) {
		if (!(radius > 0) || !Double.isFinite(radius)) {
			throw new IllegalArgumentException("Invalid radius");
		}
		this.center = Objects.requireNonNull(center);
		if (!this.center.isFinite()) {
			throw new IllegalArgumentException("Nonfinite center");
		}
		this.radius = radius;
	}

	@Override
	public Vertex2dc center() {
		return this.center;
	}

	@Override
	public double radius() {
		return this.radius;
	}

	@Override
	public ParametricEquation1to2dc parametricEquation() {
		return (a, dest) -> dest.set(this.center).add(this.radius * Math.cos(a), this.radius * Math.sin(a));
	}

	@Override
	public ImplicitSystem2dc implicitEquation() {
		return new ImplicitSystem2dc((x, y) -> (x - this.center.x) * (x - this.center.x) + (y - this.center.y) * (y - this.center.y)
				- this.radius * this.radius);
	}

}
