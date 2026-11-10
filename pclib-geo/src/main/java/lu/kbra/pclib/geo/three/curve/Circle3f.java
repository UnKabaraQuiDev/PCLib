package lu.kbra.pclib.geo.three.curve;

import java.util.Objects;

import org.joml.Matrix4x3fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import lu.kbra.pclib.geo.Move;
import lu.kbra.pclib.geo.Owned;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem3fc;
import lu.kbra.pclib.geo.equation.implicit.LinearEquation3f;
import lu.kbra.pclib.geo.equation.implicit.QuadraticEquation3f;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation1to3fc;
import lu.kbra.pclib.geo.math.Axis3f;
import lu.kbra.pclib.geo.math.Axis3fc;
import lu.kbra.pclib.geo.math.LCS3f;
import lu.kbra.pclib.geo.math.Vertex3f;
import lu.kbra.pclib.geo.math.Vertex3fc;

public class Circle3f implements Circle3fc, Cloneable {
	@Override
	public @Owned Circle3f clone() {
		return new Circle3f((Vertex3fc) center, (Axis3fc) normal, radius);
	}
	private final Vertex3f center;
	private final Axis3f normal;
	private final float radius;

	public Circle3f(final Vector3fc center, final Vector3fc normal, final float radius) {
		this(new Vertex3f(center), new Axis3f(normal), radius);
	}

	public Circle3f(final Vertex3fc center, final Axis3fc normal, final float radius) {
		this(new Vertex3f(center), new Axis3f(normal), radius);
	}

	public Circle3f(final @Move Vertex3f center, final @Move Axis3f normal, final float radius) {
		if (!Float.isFinite(radius) || !(radius > 0)) {
			throw new IllegalArgumentException("Circle radius must be positive");
		}
		this.center = Objects.requireNonNull(center);
		if (!this.center.isFinite()) {
			throw new IllegalArgumentException("Nonfinite center");
		}
		this.normal = Objects.requireNonNull(normal);
		this.normal.setUnit(normal);
		this.radius = radius;
	}

	@Override
	public Vertex3fc center() {
		return this.center;
	}

	@Override
	public Axis3fc normal() {
		return this.normal;
	}

	@Override
	public float radius() {
		return this.radius;
	}

	@Override
	public ParametricEquation1to3fc parametricEquation() {
		return (t, dest) -> {
			final LCS3f frame = this.localCoordinateSystem();
			dest.set(this.center).fma(this.radius * (float) Math.cos(t), frame.xAxis()).fma(this.radius * (float) Math.sin(t), frame.yAxis());
			return dest;
		};
	}

	@Override
	public ImplicitSystem3fc implicitEquation() {
		return new ImplicitSystem3fc(new LinearEquation3f(this.normal.x, this.normal.y, this.normal.z, -this.normal.dot(this.center)),
				QuadraticEquation3f.sphere(this.center.x, this.center.y, this.center.z, this.radius));
	}

	/** An affine image of a circle is generally an ellipse. */
	public @Owned Ellipse3f transformed(final Matrix4x3fc matrix) {
		final LCS3f frame = this.localCoordinateSystem();
		return new Ellipse3f((Vector3fc) this.center,
				new Vector3f(frame.xAxis()).mul(this.radius),
				new Vector3f(frame.yAxis()).mul(this.radius)).transform(matrix);
	}
}
