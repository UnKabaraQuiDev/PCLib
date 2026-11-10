package lu.kbra.pclib.geo.three.solid;

import java.util.List;
import java.util.Objects;

import org.joml.Vector3d;
import org.joml.Vector3dc;

import lu.kbra.pclib.geo.Move;
import lu.kbra.pclib.geo.equation.constraint.ComparisonConstraint3d;
import lu.kbra.pclib.geo.equation.constraint.MathConstraint3dc;
import lu.kbra.pclib.geo.equation.constraint.Relation;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem3dc;
import lu.kbra.pclib.geo.equation.implicit.QuadraticEquation3d;
import lu.kbra.pclib.geo.equation.parametric.ParameterDomain1d;
import lu.kbra.pclib.geo.equation.parametric.ParameterDomain3d;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation3to3dc;
import lu.kbra.pclib.geo.math.Vertex3d;
import lu.kbra.pclib.geo.math.Vertex3dc;
import lu.kbra.pclib.geo.result.Empty;
import lu.kbra.pclib.geo.result.Result3;
import lu.kbra.pclib.geo.three.face.Disk3d;
import lu.kbra.pclib.geo.three.surface.Plane3dc;

public class Ball3d implements Ball3dc {
	private final Vertex3d center;
	private final double radius;

	public Ball3d(final Vector3dc center, final double radius) {
		this(new Vertex3d(center), radius);
	}

	public Ball3d(final Vertex3dc center, final double radius) {
		this(new Vertex3d(center), radius);
	}

	public Ball3d(final @Move Vertex3d center, final double radius) {
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
	public Vertex3dc center() {
		return this.center;
	}

	@Override
	public double radius() {
		return this.radius;
	}

	@Override
	public boolean contains(final Vector3dc point) {
		return new Vector3d(point).distanceSquared(this.center) <= this.radius * this.radius;
	}

	@Override
	public ImplicitSystem3dc implicitEquation() {
		return new ImplicitSystem3dc();
	}

	@Override
	public List<MathConstraint3dc> constraints() {
		return List.of(new ComparisonConstraint3d(QuadraticEquation3d.sphere(this.center.x, this.center.y, this.center.z, this.radius),
				Relation.LE));
	}

	@Override
	public ParameterDomain3d parameterDomain() {
		return new ParameterDomain3d(new ParameterDomain1d(0, 2 * Math.PI),
				new ParameterDomain1d(-Math.PI / 2, Math.PI / 2),
				new ParameterDomain1d(0, this.radius));
	}

	@Override
	public ParametricEquation3to3dc parametricEquation() {
		return (u, v, r, dest) -> {
			if (r < 0 || r > this.radius) {
				throw new IllegalArgumentException("Outside ball");
			}
			return dest.set(this.center).add(r * Math.cos(v) * Math.cos(u), r * Math.cos(v) * Math.sin(u), r * Math.sin(v));
		};
	}

	@Override
	public Result3<Disk3d, Vertex3d, Empty> intersection(final Plane3dc plane) {
		final double signedDistance = new Vector3d(this.center).sub(plane.origin()).dot(plane.normal());
		if (Math.abs(signedDistance) > this.radius + 1e-9) {
			return Result3.third(Empty.INSTANCE);
		}
		final Vector3d projected = new Vector3d(this.center).fma(-signedDistance, plane.normal());
		final double squared = this.radius * this.radius - signedDistance * signedDistance;
		if (squared <= 1e-9 * this.radius) {
			return Result3.second(new Vertex3d(projected));
		}
		return Result3.first(new Disk3d(projected, plane.normal(), Math.sqrt(squared)));
	}
}
