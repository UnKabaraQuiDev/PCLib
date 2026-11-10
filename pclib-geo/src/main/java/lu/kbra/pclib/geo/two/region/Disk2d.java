package lu.kbra.pclib.geo.two.region;

import java.util.List;
import java.util.Objects;

import org.joml.Vector2dc;

import lu.kbra.pclib.geo.Move;
import lu.kbra.pclib.geo.equation.constraint.MathConstraint2dc;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem2dc;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation2to2dc;
import lu.kbra.pclib.geo.math.Vertex2d;
import lu.kbra.pclib.geo.math.Vertex2dc;

public class Disk2d implements Disk2dc {

	private final Vertex2d center;
	private final double radius;

	public Disk2d(final Vector2dc center, final double radius) {
		this(new Vertex2d(center), radius);
	}

	public Disk2d(final Vertex2dc center, final double radius) {
		this(new Vertex2d(center), radius);
	}

	public Disk2d(final @Move Vertex2d center, final double radius) {
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
	public ParametricEquation2to2dc parametricEquation() {
		return (angle, r, dest) -> {
			if (r < 0 || r > this.radius) {
				throw new IllegalArgumentException("Outside disk");
			}
			return dest.set(this.center).add(r * Math.cos(angle), r * Math.sin(angle));
		};
	}

	@Override
	public Vertex2d at(final double angle, final double r, final Vertex2d dest) {
		this.parametricEquation().at(angle, r, dest);
		return dest;
	}

	@Override
	public ImplicitSystem2dc implicitEquation() {
		return new ImplicitSystem2dc();
	}

	@Override
	public List<MathConstraint2dc> constraints() {
		return List.of((point, p) -> point.distanceSquared(this.center) <= this.radius * this.radius + p.distance());
	}

	public boolean contains(final Vector2dc point) {
		return point.distanceSquared(this.center) <= this.radius * this.radius;
	}

}
