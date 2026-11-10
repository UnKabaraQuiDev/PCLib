package lu.kbra.pclib.geo.three.curve;

import java.util.Arrays;
import java.util.List;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import lu.kbra.pclib.geo.Geometry3fc;
import lu.kbra.pclib.geo.GeometryKind;
import lu.kbra.pclib.geo.Owned;
import lu.kbra.pclib.geo.equation.parametric.BSplineBasis;
import lu.kbra.pclib.geo.equation.parametric.ParameterDomain1d;
import lu.kbra.pclib.geo.equation.parametric.ParametricCurve3fc;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation1to3fc;
import lu.kbra.pclib.geo.math.Vertex3f;

/** Arbitrary-degree Bézier curve using de Casteljau evaluation. */
public class BezierCurve3f implements Geometry3fc, ParametricCurve3fc {
	private final double[][] points;

	public BezierCurve3f(final List<? extends Vector3fc> controlPoints) {
		if (controlPoints.size() < 2) {
			throw new IllegalArgumentException("Bézier curve needs at least two controls");
		}
		this.points = new double[controlPoints.size()][3];
		for (int i = 0; i < this.points.length; i++) {
			final Vector3fc p = controlPoints.get(i);
			if (!Double.isFinite(p.x()) || !Double.isFinite(p.y()) || !Double.isFinite(p.z())) {
				throw new IllegalArgumentException("Nonfinite control point");
			}
			this.points[i] = new double[] { p.x(), p.y(), p.z() };
		}
	}

	@Override
	public ParameterDomain1d parameterDomain() {
		return ParameterDomain1d.UNIT;
	}

	@Override
	public ParametricEquation1to3fc parametricEquation() {
		return (t, dest) -> {
			final double[] p = BSplineBasis.evaluateBezier(this.points, t);
			return dest.set((float) (p[0]), (float) (p[1]), (float) (p[2]));
		};
	}

	public Vector3f derivativeAt(final float t, final Vector3f dest) {
		final double[][] derivatives = new double[this.points.length - 1][3];
		for (int i = 0; i < derivatives.length; i++) {
			for (int j = 0; j < 3; j++) {
				derivatives[i][j] = (this.points.length - 1) * (this.points[i + 1][j] - this.points[i][j]);
			}
		}
		final double[] d = BSplineBasis.evaluateBezier(derivatives, t);
		return dest.set(d[0], d[1], d[2]);
	}

	public @Owned List<Vertex3f> controlPoints() {
		return Arrays.stream(this.points).map(p -> new Vertex3f((float) (p[0]), (float) (p[1]), (float) (p[2]))).toList();
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
