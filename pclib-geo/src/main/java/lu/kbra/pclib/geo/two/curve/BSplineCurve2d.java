package lu.kbra.pclib.geo.two.curve;

import java.util.Arrays;
import java.util.List;

import org.joml.Vector2dc;

import lu.kbra.pclib.geo.Geometry2dc;
import lu.kbra.pclib.geo.GeometryKind;
import lu.kbra.pclib.geo.Owned;
import lu.kbra.pclib.geo.equation.parametric.BSplineBasis;
import lu.kbra.pclib.geo.equation.parametric.ParameterDomain1d;
import lu.kbra.pclib.geo.equation.parametric.ParametricCurve2dc;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation1to2dc;
import lu.kbra.pclib.geo.math.Vertex2d;

public class BSplineCurve2d implements Geometry2dc, ParametricCurve2dc {

	protected final int degree;
	protected final double[] knots;
	protected final double[][] controls;

	public BSplineCurve2d(final int degree, final double[] knots, final List<? extends Vector2dc> points) {
		BSplineBasis.validate(degree, points.size(), knots);
		this.degree = degree;
		this.knots = knots.clone();
		this.controls = new double[points.size()][2];
		for (int i = 0; i < points.size(); i++) {
			final Vector2dc p = points.get(i);
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

	public @Owned List<Vertex2d> controlPoints() {
		return Arrays.stream(this.controls).map(p -> new Vertex2d(p[0], p[1])).toList();
	}

	@Override
	public ParameterDomain1d parameterDomain() {
		return new ParameterDomain1d(this.knots[this.degree], this.knots[this.controls.length]);
	}

	@Override
	public ParametricEquation1to2dc parametricEquation() {
		return (t, dest) -> {
			final double[] p = BSplineBasis.evaluate(this.degree, this.knots, this.controls, t);
			return dest.set(p[0], p[1]);
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
