package lu.kbra.pclib.geo.three.curve;

import java.util.Arrays;
import java.util.List;

import org.joml.Vector3fc;

import lu.kbra.pclib.geo.Geometry3fc;
import lu.kbra.pclib.geo.GeometryKind;
import lu.kbra.pclib.geo.Owned;
import lu.kbra.pclib.geo.equation.parametric.BSplineBasis;
import lu.kbra.pclib.geo.equation.parametric.ParameterDomain1d;
import lu.kbra.pclib.geo.equation.parametric.ParametricCurve3fc;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation1to3fc;
import lu.kbra.pclib.geo.math.Vertex3f;

/** Nonuniform rational B-spline, evaluated using homogeneous de Boor. */
public class NurbsCurve3f implements Geometry3fc, ParametricCurve3fc {
	private final int degree;
	private final double[] knots;
	private final double[][] weighted;

	public @Owned double[] knots() {
		return this.knots.clone();
	}

	public @Owned double[] weights() {
		final double[] result = new double[this.weighted.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = this.weighted[i][3];
		}
		return result;
	}

	public @Owned List<Vertex3f> controlPoints() {
		return Arrays.stream(this.weighted)
				.map(p -> new Vertex3f((float) (p[0] / p[3]), (float) (p[1] / p[3]), (float) (p[2] / p[3])))
				.toList();
	}

	public NurbsCurve3f(final int degree, final double[] knots, final List<? extends Vector3fc> points, final double[] weights) {
		BSplineBasis.validate(degree, points.size(), knots);
		if (weights.length != points.size()) {
			throw new IllegalArgumentException("Weights and controls differ");
		}
		this.degree = degree;
		this.knots = knots.clone();
		this.weighted = new double[points.size()][4];
		for (int i = 0; i < points.size(); i++) {
			final double w = weights[i];
			final Vector3fc p = points.get(i);
			if (!(w > 0) || !Double.isFinite(w) || !Double.isFinite(p.x()) || !Double.isFinite(p.y()) || !Double.isFinite(p.z())) {
				throw new IllegalArgumentException("Invalid NURBS control or weight");
			}
			this.weighted[i] = new double[] { p.x() * w, p.y() * w, p.z() * w, w };
		}
	}

	@Override
	public ParameterDomain1d parameterDomain() {
		return new ParameterDomain1d(this.knots[this.degree], this.knots[this.weighted.length]);
	}

	@Override
	public ParametricEquation1to3fc parametricEquation() {
		return (t, dest) -> {
			final double[] p = BSplineBasis.evaluate(this.degree, this.knots, this.weighted, t);
			if (!(p[3] > 0)) {
				throw new ArithmeticException("NURBS denominator is nonpositive");
			}
			return dest.set((float) (p[0] / p[3]), (float) (p[1] / p[3]), (float) (p[2] / p[3]));
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
