package lu.kbra.pclib.geo.three.surface;

import java.util.Objects;

import org.joml.Matrix4x3fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3d;

import lu.kbra.pclib.geo.Move;
import lu.kbra.pclib.geo.Owned;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem3fc;
import lu.kbra.pclib.geo.equation.implicit.LinearEquation3f;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation2to3fc;
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
import lu.kbra.pclib.geo.result.Result3;
import lu.kbra.pclib.geo.three.curve.Line3f;

public class Plane3f implements Plane3fc, Cloneable {
	@Override
	public @Owned Plane3f clone() {
		return new Plane3f(this);
	}

	public @Owned Plane3d toDouble() {
		return new Plane3d(new Vector3d(origin), new Vector3d(normal));
	}
	private final Vertex3f origin;
	private final Axis3f normal;

	public Plane3f(final Vector3fc origin, final Vector3fc normal) {
		this(new Vertex3f(origin), new Axis3f(normal));
	}

	public Plane3f(final Vertex3fc origin, final Axis3fc normal) {
		this(new Vertex3f(origin), new Axis3f(normal));
	}

	public Plane3f(final @Move Vertex3f origin, final @Move Axis3f normal) {
		this.origin = Objects.requireNonNull(origin);
		if (!this.origin.isFinite()) {
			throw new IllegalArgumentException("Nonfinite origin");
		}
		this.normal = Objects.requireNonNull(normal);
		this.normal.setUnit(normal);
	}

	public Plane3f(final Plane3fc source) {
		this(source.origin(), source.normal());
	}

	public Plane3f set(final Vector3fc p, final Vector3fc n) {
		if (!p.isFinite()) {
			throw new IllegalArgumentException("Nonfinite origin");
		}
		final Axis3f normalized = new Axis3f(n);
		this.origin.set(p);
		this.normal.set(normalized);
		return this;
	}

	@Override
	public Vertex3fc origin() {
		return this.origin;
	}

	@Override
	public Axis3fc normal() {
		return this.normal;
	}

	@Override
	public ParametricEquation2to3fc parametricEquation() {
		return (u, v, dest) -> {
			final LCS3f lcs = this.localCoordinateSystem();
			dest.set(this.origin).fma(u, lcs.xAxis()).fma(v, lcs.yAxis());
			return dest;
		};
	}

	@Override
	public ImplicitSystem3fc implicitEquation() {
		return new ImplicitSystem3fc(new LinearEquation3f(this.normal.x, this.normal.y, this.normal.z, -this.normal.dot(this.origin)));
	}

	@Override
	public Result3<Plane3f, Line3f, Empty> intersection(final Plane3fc other) {
		final MathSolution result = EquationSystemSolver.intersection(this.implicitEquation(), other.implicitEquation());
		if (result instanceof NoSolution) {
			return Result3.third(Empty.INSTANCE);
		}
		if (result instanceof final AffineSolution a) {
			if (a.dimension() == 2) {
				return Result3.first(new Plane3f(this));
			}
			if (a.dimension() == 1) {
				final double[] p = a.origin(), d = a.basis()[0];
				return Result3.second(new Line3f(new Vector3f((float) p[0], (float) p[1], (float) p[2]), new Vector3f((float) d[0], (float) d[1], (float) d[2])));
			}
		}
		throw new IllegalStateException("Unexpected plane-plane solution");
	}

	public Plane3f transform(final Matrix4x3fc matrix) {
		final Vector3f p = matrix.transformPosition(this.origin, new Vector3f());
		final Vector3f i = matrix.transformDirection(this.localCoordinateSystem().xAxis(), new Vector3f());
		final Vector3f j = matrix.transformDirection(this.localCoordinateSystem().yAxis(), new Vector3f());
		final Vector3f n = i.cross(j);
		if (!n.isFinite() || n.lengthSquared() == 0) {
			throw new IllegalArgumentException("Degenerate transform");
		}
		return this.set(p, n);
	}

	public @Owned Plane3f transformed(final Matrix4x3fc matrix) {
		return new Plane3f(this).transform(matrix);
	}
}
