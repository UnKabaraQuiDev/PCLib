package lu.kbra.pclib.geo.three.curve;

import java.util.Objects;

import org.joml.Matrix4x3dc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import lu.kbra.pclib.geo.Move;
import lu.kbra.pclib.geo.Owned;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem3dc;
import lu.kbra.pclib.geo.equation.implicit.LinearEquation3d;
import lu.kbra.pclib.geo.equation.implicit.QuadraticEquation3d;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation1to3dc;
import lu.kbra.pclib.geo.math.Axis3d;
import lu.kbra.pclib.geo.math.Axis3dc;
import lu.kbra.pclib.geo.math.LCS3d;
import lu.kbra.pclib.geo.math.Vertex3d;
import lu.kbra.pclib.geo.math.Vertex3dc;

public class Circle3d implements Circle3dc {
	private final Vertex3d center;
	private final Axis3d normal;
	private final double radius;

	public Circle3d(final Vector3dc center, final Vector3dc normal, final double radius) {
		this(new Vertex3d(center), new Axis3d(normal), radius);
	}

	public Circle3d(final Vertex3dc center, final Axis3dc normal, final double radius) {
		this(new Vertex3d(center), new Axis3d(normal), radius);
	}

	public Circle3d(final @Move Vertex3d center, final @Move Axis3d normal, final double radius) {
		if (!Double.isFinite(radius) || !(radius > 0)) {
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
	public Vertex3dc center() {
		return this.center;
	}

	@Override
	public Axis3dc normal() {
		return this.normal;
	}

	@Override
	public double radius() {
		return this.radius;
	}

	@Override
	public ParametricEquation1to3dc parametricEquation() {
		return (t, dest) -> {
			final LCS3d frame = this.localCoordinateSystem();
			dest.set(this.center).fma(this.radius * Math.cos(t), frame.xAxis()).fma(this.radius * Math.sin(t), frame.yAxis());
			return dest;
		};
	}

	@Override
	public ImplicitSystem3dc implicitEquation() {
		return new ImplicitSystem3dc(new LinearEquation3d(this.normal.x, this.normal.y, this.normal.z, -this.normal.dot(this.center)),
				QuadraticEquation3d.sphere(this.center.x, this.center.y, this.center.z, this.radius));
	}

	/** An affine image of a circle is generally an ellipse. */
	public @Owned Ellipse3d transformed(final Matrix4x3dc matrix) {
		final LCS3d frame = this.localCoordinateSystem();
		return new Ellipse3d((Vector3dc) this.center,
				new Vector3d(frame.xAxis()).mul(this.radius),
				new Vector3d(frame.yAxis()).mul(this.radius)).transform(matrix);
	}
}
