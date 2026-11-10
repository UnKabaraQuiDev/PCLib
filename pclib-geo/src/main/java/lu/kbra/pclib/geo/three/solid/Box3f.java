package lu.kbra.pclib.geo.three.solid;

import lu.kbra.pclib.geo.Owned;

import java.util.List;
import java.util.Objects;

import org.joml.Vector3fc;

import lu.kbra.pclib.geo.Move;
import lu.kbra.pclib.geo.equation.constraint.ComparisonConstraint3f;
import lu.kbra.pclib.geo.equation.constraint.MathConstraint3fc;
import lu.kbra.pclib.geo.equation.constraint.Relation;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem3fc;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation3to3fc;
import lu.kbra.pclib.geo.math.Vertex3f;
import lu.kbra.pclib.geo.math.Vertex3fc;

/** Filled axis-aligned box, parameters in [0,1]^3. */
public class Box3f implements Box3fc, Cloneable {
	@Override
	public @Owned Box3f clone() {
		return new Box3f((Vertex3fc) min, (Vertex3fc) max);
	}
	private final Vertex3f min, max;

	public Box3f(final Vector3fc minimum, final Vector3fc maximum) {
		this(new Vertex3f(minimum), new Vertex3f(maximum));
	}

	public Box3f(final Vertex3fc minimum, final Vertex3fc maximum) {
		this(new Vertex3f(minimum), new Vertex3f(maximum));
	}

	public Box3f(final @Move Vertex3f minimum, final @Move Vertex3f maximum) {
		if (!(minimum.x() < maximum.x() && minimum.y() < maximum.y() && minimum.z() < maximum.z())) {
			throw new IllegalArgumentException("Box requires positive extents");
		}
		this.min = Objects.requireNonNull(minimum);
		this.max = Objects.requireNonNull(maximum);
	}

	@Override
	public Vertex3fc minimum() {
		return this.min;
	}

	@Override
	public Vertex3fc maximum() {
		return this.max;
	}

	@Override
	public ParametricEquation3to3fc parametricEquation() {
		return (u, v, w, dest) -> {
			if (u < 0 || u > 1 || v < 0 || v > 1 || w < 0 || w > 1) {
				throw new IllegalArgumentException("Outside box parameters");
			}
			return dest.set(this.min.x + u * (this.max.x - this.min.x),
					this.min.y + v * (this.max.y - this.min.y),
					this.min.z + w * (this.max.z - this.min.z));
		};
	}

	@Override
	public ImplicitSystem3fc implicitEquation() {
		return new ImplicitSystem3fc();
	}

	@Override
	public List<MathConstraint3fc> constraints() {
		return List.of(new ComparisonConstraint3f((x, y, z) -> x - this.min.x, Relation.GE),
				new ComparisonConstraint3f((x, y, z) -> this.max.x - x, Relation.GE),
				new ComparisonConstraint3f((x, y, z) -> y - this.min.y, Relation.GE),
				new ComparisonConstraint3f((x, y, z) -> this.max.y - y, Relation.GE),
				new ComparisonConstraint3f((x, y, z) -> z - this.min.z, Relation.GE),
				new ComparisonConstraint3f((x, y, z) -> this.max.z - z, Relation.GE));
	}

	@Override
	public boolean contains(final Vector3fc p) {
		return p.x() >= this.min.x && p.x() <= this.max.x && p.y() >= this.min.y && p.y() <= this.max.y && p.z() >= this.min.z
				&& p.z() <= this.max.z;
	}
}
