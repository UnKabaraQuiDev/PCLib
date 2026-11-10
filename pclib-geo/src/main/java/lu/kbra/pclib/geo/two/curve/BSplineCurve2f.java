package lu.kbra.pclib.geo.two.curve;

import java.util.Arrays;
import java.util.List;

import org.joml.Vector2fc;

import lu.kbra.pclib.geo.Geometry2fc;
import lu.kbra.pclib.geo.GeometryKind;
import lu.kbra.pclib.geo.Owned;
import lu.kbra.pclib.geo.equation.parametric.BSplineBasis;
import lu.kbra.pclib.geo.equation.parametric.ParameterDomain1d;
import lu.kbra.pclib.geo.equation.parametric.ParametricCurve2fc;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation1to2fc;
import lu.kbra.pclib.geo.math.Vertex2f;

public class BSplineCurve2f implements Geometry2fc, ParametricCurve2fc {

	protected final int degree;
	protected final double[] knots;
	protected final double[][] controls;

	public BSplineCurve2f(final int degree, final double[] knots, final List<? extends Vector2fc> points) {
		BSplineBasis.validate(degree, points.size(), knots);
		this.degree = degree;
		this.knots = knots.clone();
		this.controls = new double[points.size()][2];
		for (int i = 0; i < points.size(); i++) {
			final Vector2fc p = points.get(i);
			if (!Double.isFinite(p.x()) || !Double.isFinite(p.y())) {
				throw new IllegalArgumentException("Nonfinite point");
			}
			this.controls[i] = new double[] { p.x(), p.y() };
		}
	}

	public int degree() {
		return this.degree;
	}

	public @Owned double[] knots() {
		return this.knots.clone();
	}

	public @Owned List<Vertex2f> controlPoints() {
		return Arrays.stream(this.controls).map(p -> new Vertex2f((float) (p[0]), (float) (p[1]))).toList();
	}

	@Override
	public ParameterDomain1d parameterDomain() {
		return new ParameterDomain1d(this.knots[this.degree], this.knots[this.controls.length]);
	}

	@Override
	public ParametricEquation1to2fc parametricEquation() {
		return (t, dest) -> {
			final double[] p = BSplineBasis.evaluate(this.degree, this.knots, this.controls, t);
			return dest.set((float) (p[0]), (float) (p[1]));
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
