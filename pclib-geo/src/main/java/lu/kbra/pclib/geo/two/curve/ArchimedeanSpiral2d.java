package lu.kbra.pclib.geo.two.curve;

import java.util.Objects;

import org.joml.Vector2d;

import lu.kbra.pclib.geo.Geometry2dc;
import lu.kbra.pclib.geo.GeometryKind;
import lu.kbra.pclib.geo.Move;
import lu.kbra.pclib.geo.equation.parametric.ParameterDomain1d;
import lu.kbra.pclib.geo.equation.parametric.ParametricCurve2dc;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation1to2dc;
import lu.kbra.pclib.geo.math.Axis2d;
import lu.kbra.pclib.geo.math.Axis2dc;
import lu.kbra.pclib.geo.math.Vertex2d;
import lu.kbra.pclib.geo.math.Vertex2dc;

/** Archimedean spiral r(t)=r0+pitch*t for t>=0; pitch is radius gained per radian. */
public final class ArchimedeanSpiral2d implements Geometry2dc, ParametricCurve2dc {
	private final Vertex2d center;
	private final Axis2d x;
	private final double r0, pitch;

	public ArchimedeanSpiral2d(final Vertex2dc center, final Axis2dc x, final double r0, final double pitch) {
		this(new Vertex2d(center), new Axis2d(x), r0, pitch);
	}

	public ArchimedeanSpiral2d(final @Move Vertex2d center, final @Move Axis2d x, final double r0, final double pitch) {
		if (r0 < 0 || !(pitch > 0) || !Double.isFinite(r0) || !Double.isFinite(pitch)) {
			throw new IllegalArgumentException("Invalid spiral parameters");
		}
		this.center = Objects.requireNonNull(center);
		this.x = Objects.requireNonNull(x);
		if (!this.x.isFinite() || !(this.x.lengthSquared() > 0)) {
			throw new IllegalArgumentException("Invalid direction");
		}
		this.x.normalize();
		this.r0 = r0;
		this.pitch = pitch;
	}

	@Override
	public ParameterDomain1d parameterDomain() {
		return ParameterDomain1d.POSITIVE;
	}

	@Override
	public ParametricEquation1to2dc parametricEquation() {
		return (t, dest) -> {
			if (!this.parameterDomain().contains(t, 0)) {
				throw new IllegalArgumentException("Negative spiral parameter");
			}
			final double r = this.r0 + this.pitch * t, c = Math.cos(t), s = Math.sin(t);
			return dest.set(this.center).add(r * (c * this.x.x - s * this.x.y), r * (c * this.x.y + s * this.x.x));
		};
	}

	public Vector2d derivativeAt(final double t, final Vector2d dest) {
		if (!this.parameterDomain().contains(t, 0)) {
			throw new IllegalArgumentException("Negative spiral parameter");
		}
		final double r = this.r0 + this.pitch * t, c = Math.cos(t), s = Math.sin(t);
		final double dx = this.pitch * c - r * s, dy = this.pitch * s + r * c;
		return dest.set(dx * this.x.x - dy * this.x.y, dx * this.x.y + dy * this.x.x);
	}

	@Override
	public Axis2d tangentAt(final double t) {
		return new Axis2d(this.derivativeAt(t, new Vector2d()));
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
