package lu.kbra.pclib.geo.three.curve;

import java.util.Objects;

import org.joml.Matrix4x3dc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import lu.kbra.pclib.geo.Move;
import lu.kbra.pclib.geo.Owned;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem3dc;
import lu.kbra.pclib.geo.equation.implicit.LinearEquation3d;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation1to3dc;
import lu.kbra.pclib.geo.equation.solver.AffineSolution;
import lu.kbra.pclib.geo.equation.solver.EquationSystemSolver;
import lu.kbra.pclib.geo.equation.solver.MathSolution;
import lu.kbra.pclib.geo.equation.solver.NoSolution;
import lu.kbra.pclib.geo.math.Axis3d;
import lu.kbra.pclib.geo.math.Axis3dc;
import lu.kbra.pclib.geo.math.LCS3d;
import lu.kbra.pclib.geo.math.Vertex3d;
import lu.kbra.pclib.geo.math.Vertex3dc;
import lu.kbra.pclib.geo.result.Empty;
import lu.kbra.pclib.geo.result.Result2;
import lu.kbra.pclib.geo.result.Result3;
import lu.kbra.pclib.geo.three.surface.Plane3dc;

public class Line3d implements Line3dc {
	private final Vertex3d origin;
	private final Axis3d direction;

	public Line3d(final Vector3dc origin, final Vector3dc direction) {
		this(new Vertex3d(origin), new Axis3d(direction));
	}

	public Line3d(final Vertex3dc origin, final Axis3dc direction) {
		this(new Vertex3d(origin), new Axis3d(direction));
	}

	public Line3d(final @Move Vertex3d origin, final @Move Axis3d direction) {
		this.origin = Objects.requireNonNull(origin);
		if (!this.origin.isFinite()) {
			throw new IllegalArgumentException("Nonfinite origin");
		}
		this.direction = Objects.requireNonNull(direction);
		this.direction.setUnit(direction);
	}

	public Line3d(final Line3dc source) {
		this(source.origin(), source.direction());
	}

	public Line3d set(final Vector3dc p, final Vector3dc d) {
		if (!p.isFinite()) {
			throw new IllegalArgumentException("Nonfinite origin");
		}
		final Axis3d normalized = new Axis3d(d);
		this.origin.set(p);
		this.direction.set(normalized);
		return this;
	}

	@Override
	public Vertex3dc origin() {
		return this.origin;
	}

	@Override
	public Axis3dc direction() {
		return this.direction;
	}

	@Override
	public ParametricEquation1to3dc parametricEquation() {
		return (t, dest) -> dest.set(this.origin).fma(t, this.direction);
	}

	@Override
	public ImplicitSystem3dc implicitEquation() {
		final LCS3d frame = this.localCoordinateSystem();
		final Axis3dc y = frame.yAxis(), z = frame.zAxis();
		return new ImplicitSystem3dc(
				new LinearEquation3d(y.x(), y.y(), y.z(), -y.x() * this.origin.x - y.y() * this.origin.y - y.z() * this.origin.z),
				new LinearEquation3d(z.x(), z.y(), z.z(), -z.x() * this.origin.x - z.y() * this.origin.y - z.z() * this.origin.z));
	}

	@Override
	public Result3<Line3d, Vertex3d, Empty> intersection(final Plane3dc plane) {
		final MathSolution solution = EquationSystemSolver.intersection(this.implicitEquation(), plane.implicitEquation());
		if (solution instanceof NoSolution) {
			return Result3.third(Empty.INSTANCE);
		}
		if (solution instanceof final AffineSolution affine) {
			final double[] p = affine.origin();
			if (affine.dimension() == 0) {
				return Result3.second(new Vertex3d(p[0], p[1], p[2]));
			}
			if (affine.dimension() == 1) {
				return Result3.first(new Line3d(this));
			}
		}
		throw new IllegalStateException("Unexpected line-plane solution: " + solution.getClass().getSimpleName());
	}

	@Override
	public Result2<Line3d, Vertex3d> projectOnto(final Plane3dc plane) {
		return this.projectOnto(plane, plane.normal());
	}

	@Override
	public Result2<Line3d, Vertex3d> projectOnto(final Plane3dc plane, final Axis3dc direction) {
		final double divisor = plane.normal().dot(direction);
		if (Math.abs(divisor) < 1e-12) {
			throw new IllegalArgumentException("Projection direction parallel to plane");
		}
		final Vector3d projectedOrigin = Line3d.project(this.origin, plane, direction, divisor);
		final Vector3d projectedSecond = Line3d.project(new Vector3d(this.origin).add(this.direction), plane, direction, divisor);
		final Vector3d projectedDir = projectedSecond.sub(projectedOrigin, new Vector3d());
		if (projectedDir.length() < 1e-10) {
			return Result2.second(new Vertex3d(projectedOrigin));
		}
		return Result2.first(new Line3d(projectedOrigin, projectedDir));
	}

	private static Vector3d project(final Vector3dc p, final Plane3dc plane, final Vector3dc along, final double divisor) {
		final double t = new Vector3d(plane.origin()).sub(p).dot(plane.normal()) / divisor;
		return new Vector3d(p).fma(t, along);
	}

	@Override
	public @Owned Line3d reflectedAround(final Plane3dc plane) {
		final Vector3d n = new Vector3d(plane.normal());
		final Vector3d reflectedOrigin = new Vector3d(this.origin)
				.sub(new Vector3d(n).mul(2 * new Vector3d(this.origin).sub(plane.origin()).dot(n)));
		final Vector3d reflectedDir = new Vector3d(this.direction).sub(new Vector3d(n).mul(2 * this.direction.dot(n)));
		return new Line3d(reflectedOrigin, reflectedDir);
	}

	public Line3d transform(final Matrix4x3dc matrix) {
		final Vector3d p = matrix.transformPosition(this.origin, new Vector3d());
		final Vector3d d = matrix.transformDirection(this.direction, new Vector3d());
		if (!d.isFinite() || d.lengthSquared() == 0) {
			throw new IllegalArgumentException("Degenerate transform");
		}
		return this.set(p, d);
	}

	public @Owned Line3d transformed(final Matrix4x3dc matrix) {
		return new Line3d(this).transform(matrix);
	}
}
