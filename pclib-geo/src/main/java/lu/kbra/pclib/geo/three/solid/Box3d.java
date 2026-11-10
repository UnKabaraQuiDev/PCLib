package lu.kbra.pclib.geo.three.solid;

import java.util.List;
import java.util.Objects;

import org.joml.Vector3dc;

import lu.kbra.pclib.geo.Move;
import lu.kbra.pclib.geo.equation.constraint.ComparisonConstraint3d;
import lu.kbra.pclib.geo.equation.constraint.MathConstraint3dc;
import lu.kbra.pclib.geo.equation.constraint.Relation;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem3dc;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation3to3dc;
import lu.kbra.pclib.geo.math.Vertex3d;
import lu.kbra.pclib.geo.math.Vertex3dc;

/** Filled axis-aligned box, parameters in [0,1]^3. */
public class Box3d implements Box3dc {
	private final Vertex3d min, max;

	public Box3d(final Vector3dc minimum, final Vector3dc maximum) {
		this(new Vertex3d(minimum), new Vertex3d(maximum));
	}

	public Box3d(final Vertex3dc minimum, final Vertex3dc maximum) {
		this(new Vertex3d(minimum), new Vertex3d(maximum));
	}

	public Box3d(final @Move Vertex3d minimum, final @Move Vertex3d maximum) {
		if (!(minimum.x() < maximum.x() && minimum.y() < maximum.y() && minimum.z() < maximum.z())) {
			throw new IllegalArgumentException("Box requires positive extents");
		}
		this.min = Objects.requireNonNull(minimum);
		this.max = Objects.requireNonNull(maximum);
	}

	@Override
	public Vertex3dc minimum() {
		return this.min;
	}

	@Override
	public Vertex3dc maximum() {
		return this.max;
	}

	@Override
	public ParametricEquation3to3dc parametricEquation() {
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
	public ImplicitSystem3dc implicitEquation() {
		return new ImplicitSystem3dc();
	}

	@Override
	public List<MathConstraint3dc> constraints() {
		return List.of(new ComparisonConstraint3d((x, y, z) -> x - this.min.x, Relation.GE),
				new ComparisonConstraint3d((x, y, z) -> this.max.x - x, Relation.GE),
				new ComparisonConstraint3d((x, y, z) -> y - this.min.y, Relation.GE),
				new ComparisonConstraint3d((x, y, z) -> this.max.y - y, Relation.GE),
				new ComparisonConstraint3d((x, y, z) -> z - this.min.z, Relation.GE),
				new ComparisonConstraint3d((x, y, z) -> this.max.z - z, Relation.GE));
	}

	@Override
	public boolean contains(final Vector3dc p) {
		return p.x() >= this.min.x && p.x() <= this.max.x && p.y() >= this.min.y && p.y() <= this.max.y && p.z() >= this.min.z
				&& p.z() <= this.max.z;
	}
}
