package lu.kbra.pclib.geo.three.face;

import java.util.Objects;

import org.joml.Vector3fc;

import lu.kbra.pclib.geo.Move;
import lu.kbra.pclib.geo.Owned;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation2to3fc;
import lu.kbra.pclib.geo.math.Axis3f;
import lu.kbra.pclib.geo.math.Axis3fc;
import lu.kbra.pclib.geo.math.LCS3f;
import lu.kbra.pclib.geo.math.Vertex3f;
import lu.kbra.pclib.geo.math.Vertex3fc;

/** Surface parameters are polar angle and radius, including the interior. */
public class Disk3f implements Disk3fc, Cloneable {
	@Override
	public @Owned Disk3f clone() {
		return new Disk3f((Vertex3fc) center, (Axis3fc) normal, radius);
	}

	private final Vertex3f center;
	private final Axis3f normal;
	private final float radius;

	public Disk3f(final Vector3fc center, final Vector3fc normal, final float radius) {
		this(new Vertex3f(center), new Axis3f(normal), radius);
	}

	public Disk3f(final Vertex3fc center, final Axis3fc normal, final float radius) {
		this(new Vertex3f(center), new Axis3f(normal), radius);
	}

	public Disk3f(final @Move Vertex3f center, final @Move Axis3f normal, final float radius) {
		if (!(radius > 0) || !Float.isFinite(radius)) {
			throw new IllegalArgumentException("Invalid radius");
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
	public ParametricEquation2to3fc parametricEquation() {
		return (angle, r, dest) -> {
			if (r < 0 || r > this.radius) {
				throw new IllegalArgumentException("Outside disk radius");
			}
			final LCS3f frame = new LCS3f(this.center, this.normal);
			dest.set(this.center).fma(r * (float) Math.cos(angle), frame.xAxis()).fma(r * (float) Math.sin(angle), frame.yAxis());
			return dest;
		};
	}
}
