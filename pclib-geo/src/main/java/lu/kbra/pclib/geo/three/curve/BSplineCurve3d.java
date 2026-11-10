package lu.kbra.pclib.geo.three.curve;

import java.util.Arrays;
import java.util.List;

import org.joml.Vector3dc;

import lu.kbra.pclib.geo.Geometry3dc;
import lu.kbra.pclib.geo.GeometryKind;
import lu.kbra.pclib.geo.Owned;
import lu.kbra.pclib.geo.equation.parametric.BSplineBasis;
import lu.kbra.pclib.geo.equation.parametric.ParameterDomain1d;
import lu.kbra.pclib.geo.equation.parametric.ParametricCurve3dc;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation1to3dc;
import lu.kbra.pclib.geo.math.Vertex3d;

/** Nonrational B-spline curve with open or arbitrary valid knot vector. */
public class BSplineCurve3d implements Geometry3dc, ParametricCurve3dc {
	protected final int degree;
	protected final double[] knots;
	protected final double[][] controls;

	public BSplineCurve3d(final int degree, final double[] knots, final List<? extends Vector3dc> points) {
		BSplineBasis.validate(degree, points.size(), knots);
		this.degree = degree;
		this.knots = knots.clone();
		this.controls = new double[points.size()][3];
		for (int i = 0; i < points.size(); i++) {
			final Vector3dc p = points.get(i);
			if (!Double.isFinite(p.x()) || !Double.isFinite(p.y()) || !Double.isFinite(p.z())) {
				throw new IllegalArgumentException("Nonfinite control point");
			}
			this.controls[i] = new double[] { p.x(), p.y(), p.z() };
		}
	}

	public int degree() {
		return this.degree;
	}

	public @Owned double[] knots() {
		return this.knots.clone();
	}

	public @Owned List<Vertex3d> controlPoints() {
		return Arrays.stream(this.controls).map(p -> new Vertex3d(p[0], p[1], p[2])).toList();
	}

	@Override
	public ParameterDomain1d parameterDomain() {
		return new ParameterDomain1d(this.knots[this.degree], this.knots[this.controls.length]);
	}

	@Override
	public ParametricEquation1to3dc parametricEquation() {
		return (t, dest) -> {
			final double[] p = BSplineBasis.evaluate(this.degree, this.knots, this.controls, t);
			return dest.set(p[0], p[1], p[2]);
		};
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
		return true;
	}
}
