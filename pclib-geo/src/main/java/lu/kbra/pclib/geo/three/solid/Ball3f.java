package lu.kbra.pclib.geo.three.solid;

import lu.kbra.pclib.geo.Owned;

import java.util.List;
import java.util.Objects;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import lu.kbra.pclib.geo.Move;
import lu.kbra.pclib.geo.equation.constraint.ComparisonConstraint3f;
import lu.kbra.pclib.geo.equation.constraint.MathConstraint3fc;
import lu.kbra.pclib.geo.equation.constraint.Relation;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem3fc;
import lu.kbra.pclib.geo.equation.implicit.QuadraticEquation3f;
import lu.kbra.pclib.geo.equation.parametric.ParameterDomain1d;
import lu.kbra.pclib.geo.equation.parametric.ParameterDomain3d;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation3to3fc;
import lu.kbra.pclib.geo.math.Vertex3f;
import lu.kbra.pclib.geo.math.Vertex3fc;
import lu.kbra.pclib.geo.result.Empty;
import lu.kbra.pclib.geo.result.Result3;
import lu.kbra.pclib.geo.three.face.Disk3f;
import lu.kbra.pclib.geo.three.surface.Plane3fc;

public class Ball3f implements Ball3fc, Cloneable {
	@Override
	public @Owned Ball3f clone() {
		return new Ball3f((Vertex3fc) center, radius);
	}
	private final Vertex3f center;
	private final float radius;

	public Ball3f(final Vector3fc center, final float radius) {
		this(new Vertex3f(center), radius);
	}

	public Ball3f(final Vertex3fc center, final float radius) {
		this(new Vertex3f(center), radius);
	}

	public Ball3f(final @Move Vertex3f center, final float radius) {
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
	public Vertex3fc center() {
		return this.center;
	}

	@Override
	public float radius() {
		return this.radius;
	}

	@Override
	public boolean contains(final Vector3fc point) {
		return new Vector3f(point).distanceSquared(this.center) <= this.radius * this.radius;
	}

	@Override
	public ImplicitSystem3fc implicitEquation() {
		return new ImplicitSystem3fc();
	}

	@Override
	public List<MathConstraint3fc> constraints() {
		return List.of(new ComparisonConstraint3f(QuadraticEquation3f.sphere(this.center.x, this.center.y, this.center.z, this.radius),
				Relation.LE));
	}

	@Override
	public ParameterDomain3d parameterDomain() {
		return new ParameterDomain3d(new ParameterDomain1d(0, 2 * Math.PI),
				new ParameterDomain1d(-Math.PI / 2, Math.PI / 2),
				new ParameterDomain1d(0, this.radius));
	}

	@Override
	public ParametricEquation3to3fc parametricEquation() {
		return (u, v, r, dest) -> {
			if (r < 0 || r > this.radius) {
				throw new IllegalArgumentException("Outside ball");
			}
			return dest.set(this.center).add(r * (float) Math.cos(v) * (float) Math.cos(u), r * (float) Math.cos(v) * (float) Math.sin(u), r * (float) Math.sin(v));
		};
	}

	@Override
	public Result3<Disk3f, Vertex3f, Empty> intersection(final Plane3fc plane) {
		final float signedDistance = new Vector3f(this.center).sub(plane.origin()).dot(plane.normal());
		if (Math.abs(signedDistance) > this.radius + 1e-5f) {
			return Result3.third(Empty.INSTANCE);
		}
		final Vector3f projected = new Vector3f(this.center).fma(-signedDistance, plane.normal());
		final float squared = this.radius * this.radius - signedDistance * signedDistance;
		if (squared <= 1e-5f * this.radius) {
			return Result3.second(new Vertex3f(projected));
		}
		return Result3.first(new Disk3f(projected, plane.normal(), (float) Math.sqrt(squared)));
	}
}
