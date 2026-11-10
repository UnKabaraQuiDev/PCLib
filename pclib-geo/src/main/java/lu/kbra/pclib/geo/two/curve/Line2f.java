package lu.kbra.pclib.geo.two.curve;

import java.util.Objects;

import org.joml.Matrix3x2fc;

import lu.kbra.pclib.geo.Move;
import lu.kbra.pclib.geo.Owned;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem2fc;
import lu.kbra.pclib.geo.equation.implicit.LinearEquation2f;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation1to2fc;
import lu.kbra.pclib.geo.equation.solver.AffineSolution;
import lu.kbra.pclib.geo.equation.solver.EquationSystemSolver;
import lu.kbra.pclib.geo.equation.solver.MathSolution;
import lu.kbra.pclib.geo.equation.solver.NoSolution;
import lu.kbra.pclib.geo.math.Axis2f;
import lu.kbra.pclib.geo.math.Axis2fc;
import lu.kbra.pclib.geo.math.Vertex2f;
import lu.kbra.pclib.geo.math.Vertex2fc;
import lu.kbra.pclib.geo.result.Empty;
import lu.kbra.pclib.geo.result.Result3;

public class Line2f implements Line2fc, Cloneable {
	@Override
	public @Owned Line2f clone() {
		return new Line2f(this);
	}

	private final Vertex2f origin;
	private final Axis2f direction;

	public Line2f(final Vertex2fc origin, final Axis2fc direction) {
		this(new Vertex2f(origin), new Axis2f(direction));
	}

	public Line2f(final @Move Vertex2f origin, final @Move Axis2f direction) {
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

	public Line2f(final Line2fc other) {
		this(other.origin(), other.direction());
	}

	public Line2f set(final Vertex2fc origin, final Axis2fc dir) {
		if (!origin.isFinite()) {
			throw new IllegalArgumentException("Nonfinite origin");
		}
		final Axis2f normalized = new Axis2f(dir);
		this.origin.set(origin);
		this.direction.set(normalized);
		return this;
	}

	@Override
	public Vertex2fc origin() {
		return this.origin;
	}

	@Override
	public Axis2fc direction() {
		return this.direction;
	}

	@Override
	public ParametricEquation1to2fc parametricEquation() {
		return (t, dest) -> dest.set(this.origin).fma(t, this.direction);
	}

	@Override
	public ImplicitSystem2fc implicitEquation() {
		final float a = -this.direction.y, b = this.direction.x;
		return new ImplicitSystem2fc(new LinearEquation2f(a, b, -a * this.origin.x - b * this.origin.y));
	}

	@Override
	public Result3<@Owned Line2f, @Owned Vertex2f, Empty> intersection(final Line2fc other) {
		final MathSolution result = EquationSystemSolver.intersection(this.implicitEquation(), other.implicitEquation());
		if (result instanceof NoSolution) {
			return Result3.third(Empty.INSTANCE);
		}
		if (result instanceof final AffineSolution a) {
			if (a.dimension() == 1) {
				return Result3.first(new Line2f(this));
			}
			if (a.dimension() == 0) {
				final double[] p = a.origin();
				return Result3.second(new Vertex2f((float) p[0], (float) p[1]));
			}
		}
		throw new IllegalStateException("Unexpected line-line solution");
	}

	public Line2f transform(final Matrix3x2fc matrix) {
		final Vertex2f p = new Vertex2f();
		matrix.transformPosition(this.origin, p);
		final Axis2f d = new Axis2f();
		matrix.transformDirection(this.direction, d);
		if (!Float.isFinite(d.lengthSquared()) || d.lengthSquared() == 0) {
			throw new IllegalArgumentException("Degenerate transform");
		}
		return this.set(p, d);
	}

	public @Owned Line2f transformed(final Matrix3x2fc matrix) {
		return new Line2f(this).transform(matrix);
	}

}
