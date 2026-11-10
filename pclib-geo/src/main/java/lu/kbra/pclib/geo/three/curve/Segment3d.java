package lu.kbra.pclib.geo.three.curve;

import java.util.List;
import java.util.Objects;

import org.joml.Vector3d;
import org.joml.Vector3dc;

import lu.kbra.pclib.geo.Move;
import lu.kbra.pclib.geo.Owned;
import lu.kbra.pclib.geo.equation.constraint.LinearConstraint3d;
import lu.kbra.pclib.geo.equation.constraint.MathConstraint3dc;
import lu.kbra.pclib.geo.equation.constraint.Relation;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem3dc;
import lu.kbra.pclib.geo.equation.implicit.LinearEquation3d;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation1to3dc;
import lu.kbra.pclib.geo.math.Vertex3d;
import lu.kbra.pclib.geo.math.Vertex3dc;
import lu.kbra.pclib.geo.result.Empty;
import lu.kbra.pclib.geo.result.Result3;
import lu.kbra.pclib.geo.three.surface.Plane3dc;

public class Segment3d implements Segment3dc {
	private final Vertex3d start, end;

	public Segment3d(final Vector3dc start, final Vector3dc end) {
		this(new Vertex3d(start), new Vertex3d(end));
	}

	public Segment3d(final Vertex3dc start, final Vertex3dc end) {
		this(new Vertex3d(start), new Vertex3d(end));
	}

	public Segment3d(final @Move Vertex3d start, final @Move Vertex3d end) {
		if (!start.isFinite() || !end.isFinite() || !(new Vector3d(start).distanceSquared(end) > 0)) {
			throw new IllegalArgumentException("Degenerate segment");
		}
		this.start = Objects.requireNonNull(start);
		this.end = Objects.requireNonNull(end);
	}

	public Segment3d set(final Vector3dc a, final Vector3dc b) {
		if (!a.isFinite() || !b.isFinite() || !(new Vector3d(a).distanceSquared(b) > 0)) {
			throw new IllegalArgumentException("Degenerate segment");
		}
		this.start.set(a);
		this.end.set(b);
		return this;
	}

	@Override
	public Vertex3dc start() {
		return this.start;
	}

	@Override
	public Vertex3dc end() {
		return this.end;
	}

	@Override
	public ParametricEquation1to3dc parametricEquation() {
		final Vector3d delta = new Vector3d(this.end).sub(this.start);
		return (t, dest) -> dest.set(this.start).fma(t, delta);
	}

	@Override
	public ImplicitSystem3dc implicitEquation() {
		return this.supportingLine().implicitEquation();
	}

	@Override
	public List<MathConstraint3dc> constraints() {
		final Vector3d delta = new Vector3d(this.end).sub(this.start);
		final double lengthSquared = delta.lengthSquared();
		final double startDot = delta.dot(this.start);
		return List.of(new LinearConstraint3d(new LinearEquation3d(delta.x, delta.y, delta.z, -startDot), Relation.GE),
				new LinearConstraint3d(new LinearEquation3d(delta.x, delta.y, delta.z, -startDot - lengthSquared), Relation.LE));
	}

	public @Owned Line3d supportingLine() {
		return new Line3d(this.start, new Vector3d(this.end).sub(this.start));
	}

	@Override
	public Result3<Segment3d, Vertex3d, Empty> intersection(final Plane3dc plane) {
		final double a = new Vector3d(this.start).sub(plane.origin()).dot(plane.normal());
		final double b = new Vector3d(this.end).sub(plane.origin()).dot(plane.normal());
		if (Math.abs(a) < 1e-9 && Math.abs(b) < 1e-9) {
			return Result3.first(new Segment3d((Vertex3dc) this.start, (Vertex3dc) this.end));
		}
		if (a * b > 0) {
			return Result3.third(Empty.INSTANCE);
		}
		final double denom = a - b;
		if (Math.abs(denom) < 1e-12) {
			return Result3.third(Empty.INSTANCE);
		}
		final double t = a / denom;
		if (!this.parameterDomain().contains(t, 1e-10)) {
			return Result3.third(Empty.INSTANCE);
		}
		return Result3.second(this.at(t));
	}

	@Override
	public Result3<Segment3d, Vertex3d, Empty> intersection(final Ray3dc ray) {
		return new Ray3d(ray.origin(), ray.direction()).intersection(this);
	}
}
