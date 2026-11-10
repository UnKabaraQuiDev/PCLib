package lu.kbra.pclib.geo.two.curve;

import java.util.Objects;

import org.joml.Vector2f;

import lu.kbra.pclib.geo.Geometry2fc;
import lu.kbra.pclib.geo.GeometryKind;
import lu.kbra.pclib.geo.Move;
import lu.kbra.pclib.geo.equation.parametric.ParametricCurve2fc;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation1to2fc;
import lu.kbra.pclib.geo.math.Axis2f;
import lu.kbra.pclib.geo.math.Axis2fc;
import lu.kbra.pclib.geo.math.Vertex2f;
import lu.kbra.pclib.geo.math.Vertex2fc;

/** Equiangular spiral with r(t)=r0*exp(growth*t), positive r0. */
public final class LogarithmicSpiral2f implements Geometry2fc, ParametricCurve2fc {
	private final Vertex2f center;
	private final Axis2f x;
	private final float r0, growth;

	public LogarithmicSpiral2f(final Vertex2fc center, final Axis2fc x, final float r0, final float growth) {
		this(new Vertex2f(center), new Axis2f(x), r0, growth);
	}

	public LogarithmicSpiral2f(final @Move Vertex2f center, final @Move Axis2f x, final float r0, final float growth) {
		if (!(r0 > 0) || !Float.isFinite(r0) || !Float.isFinite(growth)) {
			throw new IllegalArgumentException("Invalid spiral parameters");
		}
		this.center = Objects.requireNonNull(center);
		this.x = Objects.requireNonNull(x);
		if (!this.x.isFinite() || !(this.x.lengthSquared() > 0)) {
			throw new IllegalArgumentException("Invalid direction");
		}
		this.x.normalize();
		this.r0 = r0;
		this.growth = growth;
	}

	@Override
	public ParametricEquation1to2fc parametricEquation() {
		return (t, dest) -> {
			if (!Float.isFinite(t)) {
				throw new IllegalArgumentException("Nonfinite parameter");
			}
			final float r = this.r0 * (float) Math.exp(this.growth * t), c = (float) Math.cos(t), s = (float) Math.sin(t);
			if (!Float.isFinite(r)) {
				throw new ArithmeticException("Spiral overflows");
			}
			return dest.set(this.center).add(r * (c * this.x.x - s * this.x.y), r * (c * this.x.y + s * this.x.x));
		};
	}

	public Vector2f derivativeAt(final float t, final Vector2f dest) {
		final float r = this.r0 * (float) Math.exp(this.growth * t), c = (float) Math.cos(t), s = (float) Math.sin(t);
		if (!Float.isFinite(r)) {
			throw new ArithmeticException("Spiral derivative overflows");
		}
		final float dx = r * (this.growth * c - s), dy = r * (this.growth * s + c);
		return dest.set(dx * this.x.x - dy * this.x.y, dx * this.x.y + dy * this.x.x);
	}

	@Override
	public Axis2f tangentAt(final float t) {
		return new Axis2f(this.derivativeAt(t, new Vector2f()));
	}

	@Override
	public GeometryKind geometryKind() {
		return GeometryKind.CURVE;
	}

	@Override
	public int intrinsicDimension() {
		return 1;
	}

	@Override
	public boolean isBounded() {
		return false;
	}
}
