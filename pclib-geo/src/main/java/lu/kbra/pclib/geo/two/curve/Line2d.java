package lu.kbra.pclib.geo.two.curve;

import java.util.Objects;

import org.joml.Matrix3x2dc;

import lu.kbra.pclib.geo.Move;
import lu.kbra.pclib.geo.Owned;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem2dc;
import lu.kbra.pclib.geo.equation.implicit.LinearEquation2d;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation1to2dc;
import lu.kbra.pclib.geo.equation.solver.AffineSolution;
import lu.kbra.pclib.geo.equation.solver.EquationSystemSolver;
import lu.kbra.pclib.geo.equation.solver.MathSolution;
import lu.kbra.pclib.geo.equation.solver.NoSolution;
import lu.kbra.pclib.geo.math.Axis2d;
import lu.kbra.pclib.geo.math.Axis2dc;
import lu.kbra.pclib.geo.math.Vertex2d;
import lu.kbra.pclib.geo.math.Vertex2dc;
import lu.kbra.pclib.geo.result.Empty;
import lu.kbra.pclib.geo.result.Result3;

public class Line2d implements Line2dc {

	private final Vertex2d origin;
	private final Axis2d direction;

	public Line2d(final Vertex2dc origin, final Axis2dc direction) {
		this(new Vertex2d(origin), new Axis2d(direction));
	}

	public Line2d(final @Move Vertex2d origin, final @Move Axis2d direction) {
		this.origin = Objects.requireNonNull(origin);
		if (!this.origin.isFinite()) {
			throw new IllegalArgumentException("Nonfinite origin");
		}
		this.direction = Objects.requireNonNull(direction);
		if (!this.direction.isFinite() || !(this.direction.lengthSquared() > 0)) {
			throw new IllegalArgumentException("Invalid direction");
		}
		this.direction.normalize();
	}

	public Line2d(final Line2dc other) {
		this(other.origin(), other.direction());
	}

	public Line2d set(final Vertex2dc origin, final Axis2dc dir) {
		if (!origin.isFinite()) {
			throw new IllegalArgumentException("Nonfinite origin");
		}
		final Axis2d normalized = new Axis2d(dir);
		this.origin.set(origin);
		this.direction.set(normalized);
		return this;
	}

	@Override
	public Vertex2dc origin() {
		return this.origin;
	}

	@Override
	public Axis2dc direction() {
		return this.direction;
	}

	@Override
	public ParametricEquation1to2dc parametricEquation() {
		return (t, dest) -> dest.set(this.origin).fma(t, this.direction);
	}

	@Override
	public ImplicitSystem2dc implicitEquation() {
		final double a = -this.direction.y, b = this.direction.x;
		return new ImplicitSystem2dc(new LinearEquation2d(a, b, -a * this.origin.x - b * this.origin.y));
	}

	@Override
	public Result3<@Owned Line2d, @Owned Vertex2d, Empty> intersection(final Line2dc other) {
		final MathSolution result = EquationSystemSolver.intersection(this.implicitEquation(), other.implicitEquation());
		if (result instanceof NoSolution) {
			return Result3.third(Empty.INSTANCE);
		}
		if (result instanceof final AffineSolution a) {
			if (a.dimension() == 1) {
				return Result3.first(new Line2d(this));
			}
			if (a.dimension() == 0) {
				final double[] p = a.origin();
				return Result3.second(new Vertex2d(p[0], p[1]));
			}
		}
		throw new IllegalStateException("Unexpected line-line solution");
	}

	public Line2d transform(final Matrix3x2dc matrix) {
		final Vertex2d p = new Vertex2d();
		matrix.transformPosition(this.origin, p);
		final Axis2d d = new Axis2d();
		matrix.transformDirection(this.direction, d);
		if (!Double.isFinite(d.lengthSquared()) || d.lengthSquared() == 0) {
			throw new IllegalArgumentException("Degenerate transform");
		}
		return this.set(p, d);
	}

	public @Owned Line2d transformed(final Matrix3x2dc matrix) {
		return new Line2d(this).transform(matrix);
	}

}
