package lu.kbra.pclib.geo.two.region;

import java.util.List;
import java.util.Objects;

import org.joml.Vector2fc;

import lu.kbra.pclib.geo.Move;
import lu.kbra.pclib.geo.Owned;
import lu.kbra.pclib.geo.equation.constraint.MathConstraint2fc;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem2fc;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation2to2fc;
import lu.kbra.pclib.geo.math.Vertex2f;
import lu.kbra.pclib.geo.math.Vertex2fc;

public class Disk2f implements Disk2fc, Cloneable {
	@Override
	public @Owned Disk2f clone() {
		return new Disk2f((Vertex2fc) center, radius);
	}

	private final Vertex2f center;
	private final float radius;

	public Disk2f(final Vector2fc center, final float radius) {
		this(new Vertex2f(center), radius);
	}

	public Disk2f(final Vertex2fc center, final float radius) {
		this(new Vertex2f(center), radius);
	}

	public Disk2f(final @Move Vertex2f center, final float radius) {
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
	public ParametricEquation2to2fc parametricEquation() {
		return (angle, r, dest) -> {
			if (r < 0 || r > this.radius) {
				throw new IllegalArgumentException("Outside disk");
			}
			return dest.set(this.center).add(r * (float) Math.cos(angle), r * (float) Math.sin(angle));
		};
	}

	@Override
	public Vertex2f at(final float angle, final float r, final Vertex2f dest) {
		this.parametricEquation().at(angle, r, dest);
		return dest;
	}

	@Override
	public ImplicitSystem2fc implicitEquation() {
		return new ImplicitSystem2fc();
	}

	@Override
	public List<MathConstraint2fc> constraints() {
		return List.of((point, p) -> point.distanceSquared(this.center) <= this.radius * this.radius + p.distance());
	}

	public boolean contains(final Vector2fc point) {
		return point.distanceSquared(this.center) <= this.radius * this.radius;
	}

}
