package lu.kbra.pclib.geo.three.surface;

import java.util.Objects;

import org.joml.Matrix4x3dc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import lu.kbra.pclib.geo.Move;
import lu.kbra.pclib.geo.Owned;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem3dc;
import lu.kbra.pclib.geo.equation.implicit.LinearEquation3d;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation2to3dc;
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
import lu.kbra.pclib.geo.result.Result3;
import lu.kbra.pclib.geo.three.curve.Line3d;

public class Plane3d implements Plane3dc {
	private final Vertex3d origin;
	private final Axis3d normal;

	public Plane3d(final Vector3dc origin, final Vector3dc normal) {
		this(new Vertex3d(origin), new Axis3d(normal));
	}

	public Plane3d(final Vertex3dc origin, final Axis3dc normal) {
		this(new Vertex3d(origin), new Axis3d(normal));
	}

	public Plane3d(final @Move Vertex3d origin, final @Move Axis3d normal) {
		this.origin = Objects.requireNonNull(origin);
		if (!this.origin.isFinite()) {
			throw new IllegalArgumentException("Nonfinite origin");
		}
		this.normal = Objects.requireNonNull(normal);
		this.normal.setUnit(normal);
	}

	public Plane3d(final Plane3dc source) {
		this(source.origin(), source.normal());
	}

	public Plane3d set(final Vector3dc p, final Vector3dc n) {
		if (!p.isFinite()) {
			throw new IllegalArgumentException("Nonfinite origin");
		}
		final Axis3d normalized = new Axis3d(n);
		this.origin.set(p);
		this.normal.set(normalized);
		return this;
	}

	@Override
	public Vertex3dc origin() {
		return this.origin;
	}

	@Override
	public Axis3dc normal() {
		return this.normal;
	}

	@Override
	public ParametricEquation2to3dc parametricEquation() {
		return (u, v, dest) -> {
			final LCS3d lcs = this.localCoordinateSystem();
			dest.set(this.origin).fma(u, lcs.xAxis()).fma(v, lcs.yAxis());
			return dest;
		};
	}

	@Override
	public ImplicitSystem3dc implicitEquation() {
		return new ImplicitSystem3dc(new LinearEquation3d(this.normal.x, this.normal.y, this.normal.z, -this.normal.dot(this.origin)));
	}

	@Override
	public Result3<Plane3d, Line3d, Empty> intersection(final Plane3dc other) {
		final MathSolution result = EquationSystemSolver.intersection(this.implicitEquation(), other.implicitEquation());
		if (result instanceof NoSolution) {
			return Result3.third(Empty.INSTANCE);
		}
		if (result instanceof final AffineSolution a) {
			if (a.dimension() == 2) {
				return Result3.first(new Plane3d(this));
			}
			if (a.dimension() == 1) {
				final double[] p = a.origin(), d = a.basis()[0];
				return Result3.second(new Line3d(new Vector3d(p[0], p[1], p[2]), new Vector3d(d[0], d[1], d[2])));
			}
		}
		throw new IllegalStateException("Unexpected plane-plane solution");
	}

	public Plane3d transform(final Matrix4x3dc matrix) {
		final Vector3d p = matrix.transformPosition(this.origin, new Vector3d());
		final Vector3d i = matrix.transformDirection(this.localCoordinateSystem().xAxis(), new Vector3d());
		final Vector3d j = matrix.transformDirection(this.localCoordinateSystem().yAxis(), new Vector3d());
		final Vector3d n = i.cross(j);
		if (!n.isFinite() || n.lengthSquared() == 0) {
			throw new IllegalArgumentException("Degenerate transform");
		}
		return this.set(p, n);
	}

	public @Owned Plane3d transformed(final Matrix4x3dc matrix) {
		return new Plane3d(this).transform(matrix);
	}
}
