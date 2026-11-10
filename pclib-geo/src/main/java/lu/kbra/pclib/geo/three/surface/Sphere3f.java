package lu.kbra.pclib.geo.three.surface;

import java.util.Objects;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import lu.kbra.pclib.geo.Move;
import lu.kbra.pclib.geo.Owned;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem3fc;
import lu.kbra.pclib.geo.equation.implicit.QuadraticEquation3f;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation2to3fc;
import lu.kbra.pclib.geo.math.LCS3f;
import lu.kbra.pclib.geo.math.Vertex3f;
import lu.kbra.pclib.geo.math.Vertex3fc;
import lu.kbra.pclib.geo.result.Empty;
import lu.kbra.pclib.geo.result.Result3;
import lu.kbra.pclib.geo.three.curve.Circle3f;

public class Sphere3f implements Sphere3fc, Cloneable {
	@Override
	public @Owned Sphere3f clone() {
		return new Sphere3f((Vertex3fc) center, radius);
	}
	private final Vertex3f center;
	private final float radius;

	public Sphere3f(final Vector3fc center, final float radius) {
		this(new Vertex3f(center), radius);
	}

	public Sphere3f(final Vertex3fc center, final float radius) {
		this(new Vertex3f(center), radius);
	}

	public Sphere3f(final @Move Vertex3f center, final float radius) {
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
	public @Owned LCS3f localCoordinateSystem() {
		return new LCS3f().setFromNormal(this.center, new Vector3f(0, 0, 1));
	}

	@Override
	public ParametricEquation2to3fc parametricEquation() {
		return (u, v, dest) -> dest.set(this.center)
				.add(this.radius * (float) Math.cos(v) * (float) Math.cos(u), this.radius * (float) Math.cos(v) * (float) Math.sin(u), this.radius * (float) Math.sin(v));
	}

	@Override
	public ImplicitSystem3fc implicitEquation() {
		return new ImplicitSystem3fc(QuadraticEquation3f.sphere(this.center.x, this.center.y, this.center.z, this.radius));
	}

	@Override
	public Result3<Circle3f, Vertex3f, Empty> intersection(final Plane3fc plane) {
		final float signedDistance = new Vector3f(this.center).sub(plane.origin()).dot(plane.normal());
		if (Math.abs(signedDistance) > this.radius + 1e-5f) {
			return Result3.third(Empty.INSTANCE);
		}
		final Vector3f projection = new Vector3f(this.center).fma(-signedDistance, plane.normal());
		final float square = this.radius * this.radius - signedDistance * signedDistance;
		if (square <= 1e-5f * this.radius) {
			return Result3.second(new Vertex3f(projection));
		}
		return Result3.first(new Circle3f(projection, plane.normal(), (float) Math.sqrt(square)));
	}
}
