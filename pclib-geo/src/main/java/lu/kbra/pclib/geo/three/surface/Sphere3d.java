package lu.kbra.pclib.geo.three.surface;

import java.util.Objects;

import org.joml.Vector3d;
import org.joml.Vector3dc;

import lu.kbra.pclib.geo.Move;
import lu.kbra.pclib.geo.Owned;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem3dc;
import lu.kbra.pclib.geo.equation.implicit.QuadraticEquation3d;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation2to3dc;
import lu.kbra.pclib.geo.math.LCS3d;
import lu.kbra.pclib.geo.math.Vertex3d;
import lu.kbra.pclib.geo.math.Vertex3dc;
import lu.kbra.pclib.geo.result.Empty;
import lu.kbra.pclib.geo.result.Result3;
import lu.kbra.pclib.geo.three.curve.Circle3d;

public class Sphere3d implements Sphere3dc {
	private final Vertex3d center;
	private final double radius;

	public Sphere3d(final Vector3dc center, final double radius) {
		this(new Vertex3d(center), radius);
	}

	public Sphere3d(final Vertex3dc center, final double radius) {
		this(new Vertex3d(center), radius);
	}

	public Sphere3d(final @Move Vertex3d center, final double radius) {
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
	public @Owned LCS3d localCoordinateSystem() {
		return new LCS3d().setFromNormal(this.center, new Vector3d(0, 0, 1));
	}

	@Override
	public ParametricEquation2to3dc parametricEquation() {
		return (u, v, dest) -> dest.set(this.center)
				.add(this.radius * Math.cos(v) * Math.cos(u), this.radius * Math.cos(v) * Math.sin(u), this.radius * Math.sin(v));
	}

	@Override
	public ImplicitSystem3dc implicitEquation() {
		return new ImplicitSystem3dc(QuadraticEquation3d.sphere(this.center.x, this.center.y, this.center.z, this.radius));
	}

	@Override
	public Result3<Circle3d, Vertex3d, Empty> intersection(final Plane3dc plane) {
		final double signedDistance = new Vector3d(this.center).sub(plane.origin()).dot(plane.normal());
		if (Math.abs(signedDistance) > this.radius + 1e-9) {
			return Result3.third(Empty.INSTANCE);
		}
		final Vector3d projection = new Vector3d(this.center).fma(-signedDistance, plane.normal());
		final double square = this.radius * this.radius - signedDistance * signedDistance;
		if (square <= 1e-9 * this.radius) {
			return Result3.second(new Vertex3d(projection));
		}
		return Result3.first(new Circle3d(projection, plane.normal(), Math.sqrt(square)));
	}
}
