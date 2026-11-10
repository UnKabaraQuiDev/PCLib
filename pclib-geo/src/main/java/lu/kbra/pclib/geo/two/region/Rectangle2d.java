package lu.kbra.pclib.geo.two.region;

import java.util.List;
import java.util.Objects;

import org.joml.Vector2dc;

import lu.kbra.pclib.geo.Geometry2dc;
import lu.kbra.pclib.geo.GeometryKind;
import lu.kbra.pclib.geo.Move;
import lu.kbra.pclib.geo.Owned;
import lu.kbra.pclib.geo.equation.ImplicitRepresentable;
import lu.kbra.pclib.geo.equation.ParametricRepresentable;
import lu.kbra.pclib.geo.equation.constraint.MathConstraint2dc;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem2dc;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation2to2dc;
import lu.kbra.pclib.geo.math.Vertex2d;
import lu.kbra.pclib.geo.math.Vertex2dc;

public class Rectangle2d
		implements
			Geometry2dc,
			ParametricRepresentable<ParametricEquation2to2dc>,
			ImplicitRepresentable<ImplicitSystem2dc, MathConstraint2dc> {

	private final Vertex2d min;
	private final Vertex2d max;

	public Rectangle2d(final Vector2dc minimum, final Vector2dc maximum) {
		this(new Vertex2d(minimum), new Vertex2d(maximum));
	}

	public Rectangle2d(final Vertex2dc minimum, final Vertex2dc maximum) {
		this(new Vertex2d(minimum), new Vertex2d(maximum));
	}

	public Rectangle2d(final @Move Vertex2d minimum, final @Move Vertex2d maximum) {
		if (!(minimum.x() < maximum.x() && minimum.y() < maximum.y())) {
			throw new IllegalArgumentException("Invalid rectangle");
		}
		this.min = Objects.requireNonNull(minimum);
		this.max = Objects.requireNonNull(maximum);
	}

	public Vertex2dc minimum() {
		return this.min;
	}

	public Vertex2dc maximum() {
		return this.max;
	}

	public @Owned Vertex2d at(final double u, final double v) {
		if (u < 0 || u > 1 || v < 0 || v > 1) {
			throw new IllegalArgumentException("Outside rectangle");
		}
		return new Vertex2d(this.min.x + u * (this.max.x - this.min.x), this.min.y + v * (this.max.y - this.min.y));
	}

	@Override
	public ParametricEquation2to2dc parametricEquation() {
		return (u, v, dest) -> {
			final Vertex2d p = this.at(u, v);
			return dest.set(p);
		};
	}

	@Override
	public ImplicitSystem2dc implicitEquation() {
		return new ImplicitSystem2dc();
	}

	@Override
	public @Owned List<MathConstraint2dc> constraints() {
		return List.of((p, eps) -> p.x() >= this.min.x - eps.distance() && p.x() <= this.max.x + eps.distance()
				&& p.y() >= this.min.y - eps.distance() && p.y() <= this.max.y + eps.distance());
	}

	@Override
	public GeometryKind geometryKind() {
		return GeometryKind.REGION;
	}

	@Override
	public int intrinsicDimension() {
		return 2;
	}

	@Override
	public boolean isBounded() {
		return true;
	}

}
