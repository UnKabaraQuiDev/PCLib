package lu.kbra.pclib.geo.two.curve;

import java.util.Arrays;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector2fc;

import lu.kbra.pclib.geo.Geometry2fc;
import lu.kbra.pclib.geo.GeometryKind;
import lu.kbra.pclib.geo.Owned;
import lu.kbra.pclib.geo.equation.parametric.BSplineBasis;
import lu.kbra.pclib.geo.equation.parametric.ParameterDomain1d;
import lu.kbra.pclib.geo.equation.parametric.ParametricCurve2fc;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation1to2fc;
import lu.kbra.pclib.geo.math.Vertex2f;

public class BezierCurve2f implements Geometry2fc, ParametricCurve2fc {

	private final double[][] points;

	public BezierCurve2f(final List<? extends Vector2fc> controls) {
		if (controls.size() < 2) {
			throw new IllegalArgumentException("At least two controls required");
		}
		this.points = new double[controls.size()][2];
		for (int i = 0; i < this.points.length; i++) {
			final Vector2fc p = controls.get(i);
			if (!Double.isFinite(p.x()) || !Double.isFinite(p.y())) {
				throw new IllegalArgumentException("Nonfinite control point");
			}
			this.points[i] = new double[] { p.x(), p.y() };
		}
	}

	@Override
	public ParameterDomain1d parameterDomain() {
		return ParameterDomain1d.UNIT;
	}

	@Override
	public ParametricEquation1to2fc parametricEquation() {
		return (t, dest) -> {
			final double[] p = BSplineBasis.evaluateBezier(this.points, t);
			return dest.set((float) (p[0]), (float) (p[1]));
		};
	}

	public Vector2f derivativeAt(final float t, final Vector2f dest) {
		final double[][] derivatives = new double[this.points.length - 1][2];
		for (int i = 0; i < derivatives.length; i++) {
			for (int j = 0; j < 2; j++) {
				derivatives[i][j] = (this.points.length - 1) * (this.points[i + 1][j] - this.points[i][j]);
			}
		}
		final double[] d = BSplineBasis.evaluateBezier(derivatives, t);
		return dest.set(d[0], d[1]);
	}

	public @Owned List<Vertex2f> controlPoints() {
		return Arrays.stream(this.points).map(p -> new Vertex2f((float) (p[0]), (float) (p[1]))).toList();
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
