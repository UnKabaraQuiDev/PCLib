package lu.kbra.pclib.geo.three.curve;

import java.util.List;
import java.util.Objects;

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
import lu.kbra.pclib.geo.math.Vertex3f;
import lu.kbra.pclib.geo.math.Vertex3fc;
import lu.kbra.pclib.geo.result.Empty;
import lu.kbra.pclib.geo.result.Result3;
import lu.kbra.pclib.geo.three.surface.Plane3fc;

public class Segment3f implements Segment3fc, Cloneable {
	@Override
	public @Owned Segment3f clone() {
		return new Segment3f((Vertex3fc) start, (Vertex3fc) end);
	}

	private final Vertex3f start, end;

	public Segment3f(final Vector3fc start, final Vector3fc end) {
		this(new Vertex3f(start), new Vertex3f(end));
	}

	public Segment3f(final Vertex3fc start, final Vertex3fc end) {
		this(new Vertex3f(start), new Vertex3f(end));
	}

	public Segment3f(final @Move Vertex3f start, final @Move Vertex3f end) {
		if (!start.isFinite() || !end.isFinite() || !(new Vector3f(start).distanceSquared(end) > 0)) {
			throw new IllegalArgumentException("Degenerate segment");
		}
		this.start = Objects.requireNonNull(start);
		this.end = Objects.requireNonNull(end);
	}

	public Segment3f set(final Vector3fc a, final Vector3fc b) {
		if (!a.isFinite() || !b.isFinite() || !(new Vector3f(a).distanceSquared(b) > 0)) {
			throw new IllegalArgumentException("Degenerate segment");
		}
		this.start.set(a);
		this.end.set(b);
		return this;
	}

	@Override
	public Vertex3fc start() {
		return this.start;
	}

	@Override
	public Vertex3fc end() {
		return this.end;
	}

	@Override
	public ParametricEquation1to3fc parametricEquation() {
		final Vector3f delta = new Vector3f(this.end).sub(this.start);
		return (t, dest) -> dest.set(this.start).fma(t, delta);
	}

	@Override
	public ImplicitSystem3fc implicitEquation() {
		return this.supportingLine().implicitEquation();
	}

	@Override
	public List<MathConstraint3fc> constraints() {
		final Vector3f delta = new Vector3f(this.end).sub(this.start);
		final float lengthSquared = delta.lengthSquared();
		final float startDot = delta.dot(this.start);
		return List.of(new LinearConstraint3f(new LinearEquation3f(delta.x, delta.y, delta.z, -startDot), Relation.GE),
				new LinearConstraint3f(new LinearEquation3f(delta.x, delta.y, delta.z, -startDot - lengthSquared), Relation.LE));
	}

	public @Owned Line3f supportingLine() {
		return new Line3f(this.start, new Vector3f(this.end).sub(this.start));
	}

	@Override
	public Result3<Segment3f, Vertex3f, Empty> intersection(final Plane3fc plane) {
		final float a = new Vector3f(this.start).sub(plane.origin()).dot(plane.normal());
		final float b = new Vector3f(this.end).sub(plane.origin()).dot(plane.normal());
		if (Math.abs(a) < 1e-5f && Math.abs(b) < 1e-5f) {
			return Result3.first(new Segment3f((Vertex3fc) this.start, (Vertex3fc) this.end));
		}
		if (a * b > 0) {
			return Result3.third(Empty.INSTANCE);
		}
		final float denom = a - b;
		if (Math.abs(denom) < 1e-6f) {
			return Result3.third(Empty.INSTANCE);
		}
		final float t = a / denom;
		if (!this.parameterDomain().contains(t, 1e-5f)) {
			return Result3.third(Empty.INSTANCE);
		}
		return Result3.second(this.at(t));
	}

	@Override
	public Result3<Segment3f, Vertex3f, Empty> intersection(final Ray3fc ray) {
		return new Ray3f(ray.origin(), ray.direction()).intersection(this);
	}
}
