package lu.kbra.pclib.geo.two.region;

import java.util.List;
import java.util.Objects;

import org.joml.Vector2fc;

import lu.kbra.pclib.geo.Geometry2fc;
import lu.kbra.pclib.geo.GeometryKind;
import lu.kbra.pclib.geo.Move;
import lu.kbra.pclib.geo.Owned;
import lu.kbra.pclib.geo.equation.ImplicitRepresentable;
import lu.kbra.pclib.geo.equation.ParametricRepresentable;
import lu.kbra.pclib.geo.equation.constraint.MathConstraint2fc;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem2fc;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation2to2fc;
import lu.kbra.pclib.geo.math.Vertex2f;
import lu.kbra.pclib.geo.math.Vertex2fc;

public class Rectangle2f
		implements
			Geometry2fc,
			ParametricRepresentable<ParametricEquation2to2fc>,
			ImplicitRepresentable<ImplicitSystem2fc, MathConstraint2fc>,
			Cloneable {

	@Override
	public @Owned Rectangle2f clone() {
		return new Rectangle2f((Vertex2fc) min, (Vertex2fc) max);
	}

	private final Vertex2f min;
	private final Vertex2f max;

	public Rectangle2f(final Vector2fc minimum, final Vector2fc maximum) {
		this(new Vertex2f(minimum), new Vertex2f(maximum));
	}

	public Rectangle2f(final Vertex2fc minimum, final Vertex2fc maximum) {
		this(new Vertex2f(minimum), new Vertex2f(maximum));
	}

	public Rectangle2f(final @Move Vertex2f minimum, final @Move Vertex2f maximum) {
		if (!(minimum.x() < maximum.x() && minimum.y() < maximum.y())) {
			throw new IllegalArgumentException("Invalid rectangle");
		}
		this.min = Objects.requireNonNull(minimum);
		this.max = Objects.requireNonNull(maximum);
	}

	public Vertex2fc minimum() {
		return this.min;
	}

	public Vertex2fc maximum() {
		return this.max;
	}

	public @Owned Vertex2f at(final float u, final float v) {
		if (u < 0 || u > 1 || v < 0 || v > 1) {
			throw new IllegalArgumentException("Outside rectangle");
		}
		return new Vertex2f(this.min.x + u * (this.max.x - this.min.x), this.min.y + v * (this.max.y - this.min.y));
	}

	@Override
	public ParametricEquation2to2fc parametricEquation() {
		return (u, v, dest) -> {
			final Vertex2f p = this.at(u, v);
			return dest.set(p);
		};
	}

	@Override
	public ImplicitSystem2fc implicitEquation() {
		return new ImplicitSystem2fc();
	}

	@Override
	public @Owned List<MathConstraint2fc> constraints() {
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
