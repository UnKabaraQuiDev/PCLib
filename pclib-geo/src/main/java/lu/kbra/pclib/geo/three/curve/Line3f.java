package lu.kbra.pclib.geo.three.curve;

import java.util.Objects;

import org.joml.Matrix4x3fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import lu.kbra.pclib.geo.Move;
import lu.kbra.pclib.geo.Owned;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem3fc;
import lu.kbra.pclib.geo.equation.implicit.LinearEquation3f;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation1to3fc;
import lu.kbra.pclib.geo.equation.solver.AffineSolution;
import lu.kbra.pclib.geo.equation.solver.EquationSystemSolver;
import lu.kbra.pclib.geo.equation.solver.MathSolution;
import lu.kbra.pclib.geo.equation.solver.NoSolution;
import lu.kbra.pclib.geo.math.Axis3f;
import lu.kbra.pclib.geo.math.Axis3fc;
import lu.kbra.pclib.geo.math.LCS3f;
import lu.kbra.pclib.geo.math.Vertex3f;
import lu.kbra.pclib.geo.math.Vertex3fc;
import lu.kbra.pclib.geo.result.Empty;
import lu.kbra.pclib.geo.result.Result2;
import lu.kbra.pclib.geo.result.Result3;
import lu.kbra.pclib.geo.three.surface.Plane3fc;

public class Line3f implements Line3fc, Cloneable {
	@Override
	public @Owned Line3f clone() {
		return new Line3f(this);
	}
	private final Vertex3f origin;
	private final Axis3f direction;

	public Line3f(final Vector3fc origin, final Vector3fc direction) {
		this(new Vertex3f(origin), new Axis3f(direction));
	}

	public Line3f(final Vertex3fc origin, final Axis3fc direction) {
		this(new Vertex3f(origin), new Axis3f(direction));
	}

	public Line3f(final @Move Vertex3f origin, final @Move Axis3f direction) {
		this.origin = Objects.requireNonNull(origin);
		if (!this.origin.isFinite()) {
			throw new IllegalArgumentException("Nonfinite origin");
		}
		this.direction = Objects.requireNonNull(direction);
		this.direction.setUnit(direction);
	}

	public Line3f(final Line3fc source) {
		this(source.origin(), source.direction());
	}

	public Line3f set(final Vector3fc p, final Vector3fc d) {
		if (!p.isFinite()) {
			throw new IllegalArgumentException("Nonfinite origin");
		}
		final Axis3f normalized = new Axis3f(d);
		this.origin.set(p);
		this.direction.set(normalized);
		return this;
	}

	@Override
	public Vertex3fc origin() {
		return this.origin;
	}

	@Override
	public Axis3fc direction() {
		return this.direction;
	}

	@Override
	public ParametricEquation1to3fc parametricEquation() {
		return (t, dest) -> dest.set(this.origin).fma(t, this.direction);
	}

	@Override
	public ImplicitSystem3fc implicitEquation() {
		final LCS3f frame = this.localCoordinateSystem();
		final Axis3fc y = frame.yAxis(), z = frame.zAxis();
		return new ImplicitSystem3fc(
				new LinearEquation3f(y.x(), y.y(), y.z(), -y.x() * this.origin.x - y.y() * this.origin.y - y.z() * this.origin.z),
				new LinearEquation3f(z.x(), z.y(), z.z(), -z.x() * this.origin.x - z.y() * this.origin.y - z.z() * this.origin.z));
	}

	@Override
	public Result3<Line3f, Vertex3f, Empty> intersection(final Plane3fc plane) {
		final MathSolution solution = EquationSystemSolver.intersection(this.implicitEquation(), plane.implicitEquation());
		if (solution instanceof NoSolution) {
			return Result3.third(Empty.INSTANCE);
		}
		if (solution instanceof final AffineSolution affine) {
			final double[] p = affine.origin();
			if (affine.dimension() == 0) {
				return Result3.second(new Vertex3f((float) p[0], (float) p[1], (float) p[2]));
			}
			if (affine.dimension() == 1) {
				return Result3.first(new Line3f(this));
			}
		}
		throw new IllegalStateException("Unexpected line-plane solution: " + solution.getClass().getSimpleName());
	}

	@Override
	public Result2<Line3f, Vertex3f> projectOnto(final Plane3fc plane) {
		return this.projectOnto(plane, plane.normal());
	}

	@Override
	public Result2<Line3f, Vertex3f> projectOnto(final Plane3fc plane, final Axis3fc direction) {
		final float divisor = plane.normal().dot(direction);
		if (Math.abs(divisor) < 1e-6f) {
			throw new IllegalArgumentException("Projection direction parallel to plane");
		}
		final Vector3f projectedOrigin = Line3f.project(this.origin, plane, direction, divisor);
		final Vector3f projectedSecond = Line3f.project(new Vector3f(this.origin).add(this.direction), plane, direction, divisor);
		final Vector3f projectedDir = projectedSecond.sub(projectedOrigin, new Vector3f());
		if (projectedDir.length() < 1e-5f) {
			return Result2.second(new Vertex3f(projectedOrigin));
		}
		return Result2.first(new Line3f(projectedOrigin, projectedDir));
	}

	private static Vector3f project(final Vector3fc p, final Plane3fc plane, final Vector3fc along, final float divisor) {
		final float t = new Vector3f(plane.origin()).sub(p).dot(plane.normal()) / divisor;
		return new Vector3f(p).fma(t, along);
	}

	@Override
	public @Owned Line3f reflectedAround(final Plane3fc plane) {
		final Vector3f n = new Vector3f(plane.normal());
		final Vector3f reflectedOrigin = new Vector3f(this.origin)
				.sub(new Vector3f(n).mul(2 * new Vector3f(this.origin).sub(plane.origin()).dot(n)));
		final Vector3f reflectedDir = new Vector3f(this.direction).sub(new Vector3f(n).mul(2 * this.direction.dot(n)));
		return new Line3f(reflectedOrigin, reflectedDir);
	}

	public Line3f transform(final Matrix4x3fc matrix) {
		final Vector3f p = matrix.transformPosition(this.origin, new Vector3f());
		final Vector3f d = matrix.transformDirection(this.direction, new Vector3f());
		if (!d.isFinite() || d.lengthSquared() == 0) {
			throw new IllegalArgumentException("Degenerate transform");
		}
		return this.set(p, d);
	}

	public @Owned Line3f transformed(final Matrix4x3fc matrix) {
		return new Line3f(this).transform(matrix);
	}
}
