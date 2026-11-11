package lu.kbra.pclib.geo.three.curve;

import java.util.List;
import java.util.Objects;

import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import lu.kbra.pclib.geo.Move;
import lu.kbra.pclib.geo.Owned;
import lu.kbra.pclib.geo.equation.constraint.LinearConstraint3f;
import lu.kbra.pclib.geo.equation.constraint.MathConstraint3fc;
import lu.kbra.pclib.geo.equation.constraint.Relation;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem3fc;
import lu.kbra.pclib.geo.equation.implicit.LinearEquation3f;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation1to3fc;
import lu.kbra.pclib.geo.math.Axis3f;
import lu.kbra.pclib.geo.math.Axis3fc;
import lu.kbra.pclib.geo.math.Vertex3f;
import lu.kbra.pclib.geo.math.Vertex3fc;
import lu.kbra.pclib.geo.result.Empty;
import lu.kbra.pclib.geo.result.Result3;
import lu.kbra.pclib.geo.three.surface.Plane3fc;

public class Ray3f implements Ray3fc, Cloneable {
	@Override
	public @Owned Ray3f clone() {
		return new Ray3f((Vertex3fc) origin, (Axis3fc) direction);
	}

	private final Vertex3f origin;
	private final Axis3f direction;

	public Ray3f(final Vector3fc origin, final Vector3fc direction) {
		this(new Vertex3f(origin), new Axis3f(direction));
	}

	public Ray3f(final Vertex3fc origin, final Axis3fc direction) {
		this(new Vertex3f(origin), new Axis3f(direction));
	}

	public Ray3f(final @Move Vertex3f origin, final @Move Axis3f direction) {
		this.origin = Objects.requireNonNull(origin);
		if (!this.origin.isFinite()) {
			throw new IllegalArgumentException("Nonfinite origin");
		}
		this.direction = Objects.requireNonNull(direction);
		this.direction.setUnit(direction);
	}

	@Override
	public Vertex3fc origin() {
		return this.origin;
	}

	@Override
	public Axis3fc direction() {
		return this.direction;
	}

	@Override
	public ParametricEquation1to3fc parametricEquation() {
		return (t, dest) -> dest.set(this.origin).fma(t, this.direction);
	}

	@Override
	public ImplicitSystem3fc implicitEquation() {
		return new Line3f((Vertex3fc) this.origin, (Axis3fc) this.direction).implicitEquation();
	}

	@Override
	public List<MathConstraint3fc> constraints() {
		return List.of(new LinearConstraint3f(
				new LinearEquation3f(this.direction.x, this.direction.y, this.direction.z, -this.direction.dot(this.origin)),
				Relation.GE));
	}

	@Override
	public Result3<Ray3f, Vertex3f, Empty> intersection(final Plane3fc plane) {
		final float a = new Vector3f(this.origin).sub(plane.origin()).dot(plane.normal());
		final float b = this.direction.dot(plane.normal());
		if (Math.abs(b) < 1e-6f) {
			return Math.abs(a) < 1e-5f ? Result3.first(new Ray3f((Vertex3fc) this.origin, (Axis3fc) this.direction))
					: Result3.third(Empty.INSTANCE);
		}
		final float t = -a / b;
		return this.parameterDomain().contains(t, 1e-5f) ? Result3.second(this.at(Math.max(0, t))) : Result3.third(Empty.INSTANCE);
	}

	@Override
	public Result3<Segment3f, Vertex3f, Empty> intersection(final Segment3fc segment) {
		final Ray3d preciseRay = new Ray3d(new Vector3d(this.origin), new Vector3d(this.direction));
		final Segment3d preciseSegment = new Segment3d(new Vector3d(segment.start()), new Vector3d(segment.end()));
		final var result = preciseRay.intersection(preciseSegment);
		if (result.isFirst()) {
			final Segment3d overlap = result.getFirst();
			return Result3.first(new Segment3f(new Vector3f(overlap.start()), new Vector3f(overlap.end())));
		}
		if (result.isSecond()) {
			return Result3.second(new Vertex3f(new Vector3f(result.getSecond())));
		}
		return Result3.third(Empty.INSTANCE);
	}
}
