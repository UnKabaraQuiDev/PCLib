package lu.kbra.pclib.geo.three.curve;

import java.util.List;
import java.util.Objects;

import org.joml.Vector3d;
import org.joml.Vector3dc;

import lu.kbra.pclib.geo.Move;
import lu.kbra.pclib.geo.equation.constraint.LinearConstraint3d;
import lu.kbra.pclib.geo.equation.constraint.MathConstraint3dc;
import lu.kbra.pclib.geo.equation.constraint.Relation;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem3dc;
import lu.kbra.pclib.geo.equation.implicit.LinearEquation3d;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation1to3dc;
import lu.kbra.pclib.geo.equation.solver.AffineIntervalSolution;
import lu.kbra.pclib.geo.equation.solver.AffineSolution;
import lu.kbra.pclib.geo.equation.solver.EquationSystemSolver;
import lu.kbra.pclib.geo.equation.solver.MathSolution;
import lu.kbra.pclib.geo.equation.solver.NoSolution;
import lu.kbra.pclib.geo.math.Axis3d;
import lu.kbra.pclib.geo.math.Axis3dc;
import lu.kbra.pclib.geo.math.GeometryPrecision;
import lu.kbra.pclib.geo.math.Vertex3d;
import lu.kbra.pclib.geo.math.Vertex3dc;
import lu.kbra.pclib.geo.result.Empty;
import lu.kbra.pclib.geo.result.Result3;
import lu.kbra.pclib.geo.three.surface.Plane3dc;

public class Ray3d implements Ray3dc {
	private final Vertex3d origin;
	private final Axis3d direction;

	public Ray3d(final Vector3dc origin, final Vector3dc direction) {
		this(new Vertex3d(origin), new Axis3d(direction));
	}

	public Ray3d(final Vertex3dc origin, final Axis3dc direction) {
		this(new Vertex3d(origin), new Axis3d(direction));
	}

	public Ray3d(final @Move Vertex3d origin, final @Move Axis3d direction) {
		this.origin = Objects.requireNonNull(origin);
		if (!this.origin.isFinite()) {
			throw new IllegalArgumentException("Nonfinite origin");
		}
		this.direction = Objects.requireNonNull(direction);
		this.direction.setUnit(direction);
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
		return new Line3d((Vertex3dc) this.origin, (Axis3dc) this.direction).implicitEquation();
	}

	@Override
	public List<MathConstraint3dc> constraints() {
		return List.of(new LinearConstraint3d(
				new LinearEquation3d(this.direction.x, this.direction.y, this.direction.z, -this.direction.dot(this.origin)),
				Relation.GE));
	}

	@Override
	public Result3<Ray3d, Vertex3d, Empty> intersection(final Plane3dc plane) {
		final double a = new Vector3d(this.origin).sub(plane.origin()).dot(plane.normal());
		final double b = this.direction.dot(plane.normal());
		if (Math.abs(b) < 1e-12) {
			return Math.abs(a) < 1e-9 ? Result3.first(new Ray3d((Vertex3dc) this.origin, (Axis3dc) this.direction))
					: Result3.third(Empty.INSTANCE);
		}
		final double t = -a / b;
		return this.parameterDomain().contains(t, 1e-10) ? Result3.second(this.at(Math.max(0, t))) : Result3.third(Empty.INSTANCE);
	}

	@Override
	public Result3<Segment3d, Vertex3d, Empty> intersection(final Segment3dc segment) {
		final MathSolution mathematical = EquationSystemSolver.intersectionRepresentations(this, segment, GeometryPrecision.DEFAULT);
		if (mathematical instanceof NoSolution) {
			return Result3.third(Empty.INSTANCE);
		}
		if (mathematical instanceof final AffineSolution a && a.dimension() == 0) {
			final double[] p = a.origin();
			return Result3.second(new Vertex3d(p[0], p[1], p[2]));
		}
		if (mathematical instanceof final AffineIntervalSolution interval) {
			if (!Double.isFinite(interval.min()) || !Double.isFinite(interval.max())) {
				throw new IllegalStateException("Finite segment intersection expected");
			}
			final double[] lo = interval.at(interval.min()), hi = interval.at(interval.max());
			final Vector3d a = new Vector3d(lo[0], lo[1], lo[2]), b = new Vector3d(hi[0], hi[1], hi[2]);
			if (a.distanceSquared(b) <= GeometryPrecision.DEFAULT.distance() * GeometryPrecision.DEFAULT.distance()) {
				return Result3.second(new Vertex3d(a));
			}
			return Result3.first(new Segment3d(a, b));
		}
		throw new IllegalStateException("Ray-segment solver did not produce a complete supported result: " + mathematical);
	}
}
