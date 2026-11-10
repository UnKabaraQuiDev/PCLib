package lu.kbra.pclib.geo.three.face;

import java.util.Objects;

import org.joml.Vector3dc;

import lu.kbra.pclib.geo.Move;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation2to3dc;
import lu.kbra.pclib.geo.math.Axis3d;
import lu.kbra.pclib.geo.math.Axis3dc;
import lu.kbra.pclib.geo.math.LCS3d;
import lu.kbra.pclib.geo.math.Vertex3d;
import lu.kbra.pclib.geo.math.Vertex3dc;

/** Surface parameters are polar angle and radius, including the interior. */
public class Disk3d implements Disk3dc {
	private final Vertex3d center;
	private final Axis3d normal;
	private final double radius;

	public Disk3d(final Vector3dc center, final Vector3dc normal, final double radius) {
		this(new Vertex3d(center), new Axis3d(normal), radius);
	}

	public Disk3d(final Vertex3dc center, final Axis3dc normal, final double radius) {
		this(new Vertex3d(center), new Axis3d(normal), radius);
	}

	public Disk3d(final @Move Vertex3d center, final @Move Axis3d normal, final double radius) {
		if (!(radius > 0) || !Double.isFinite(radius)) {
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
	public ParametricEquation2to3dc parametricEquation() {
		return (angle, r, dest) -> {
			if (r < 0 || r > this.radius) {
				throw new IllegalArgumentException("Outside disk radius");
			}
			final LCS3d frame = new LCS3d(this.center, this.normal);
			dest.set(this.center).fma(r * Math.cos(angle), frame.xAxis()).fma(r * Math.sin(angle), frame.yAxis());
			return dest;
		};
	}
}
