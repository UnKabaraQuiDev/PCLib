package lu.kbra.pclib.geo.three.surface;

import org.joml.Vector3dc;

import lu.kbra.pclib.geo.Geometry3dc;
import lu.kbra.pclib.geo.GeometryKind;
import lu.kbra.pclib.geo.equation.parametric.BSplineBasis;
import lu.kbra.pclib.geo.equation.parametric.ParameterDomain1d;
import lu.kbra.pclib.geo.equation.parametric.ParameterDomain2d;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation2to3dc;
import lu.kbra.pclib.geo.equation.parametric.ParametricSurface3dc;

/** Tensor-product rational B-spline surface; [u][v] control net. */
public class NurbsSurface3d implements Geometry3dc, ParametricSurface3dc {
	private final int degreeU, degreeV;
	private final double[] knotsU, knotsV;
	private final double[][][] weighted;

	public NurbsSurface3d(
			final int degreeU,
			final int degreeV,
			final double[] knotsU,
			final double[] knotsV,
			final Vector3dc[][] controls,
			final double[][] weights) {
		if (controls.length < 2 || controls[0].length < 2) {
			throw new IllegalArgumentException("Control net too small");
		}
		BSplineBasis.validate(degreeU, controls.length, knotsU);
		BSplineBasis.validate(degreeV, controls[0].length, knotsV);
		this.degreeU = degreeU;
		this.degreeV = degreeV;
		this.knotsU = knotsU.clone();
		this.knotsV = knotsV.clone();
		this.weighted = new double[controls.length][controls[0].length][4];
		if (weights.length != controls.length) {
			throw new IllegalArgumentException("Weight grid width differs");
		}
		for (int i = 0; i < controls.length; i++) {
			if (controls[i].length != this.weighted[i].length || weights[i].length != this.weighted[i].length) {
				throw new IllegalArgumentException("Control net and weight grid must be rectangular");
			}
			for (int j = 0; j < controls[i].length; j++) {
				final double w = weights[i][j];
				final Vector3dc p = controls[i][j];
				if (!(w > 0) || !Double.isFinite(w) || !Double.isFinite(p.x()) || !Double.isFinite(p.y()) || !Double.isFinite(p.z())) {
					throw new IllegalArgumentException("Invalid control/weight");
				}
				this.weighted[i][j] = new double[] { p.x() * w, p.y() * w, p.z() * w, w };
			}
		}
	}

	public ParameterDomain1d uDomain() {
		return new ParameterDomain1d(this.knotsU[this.degreeU], this.knotsU[this.weighted.length]);
	}

	public ParameterDomain1d vDomain() {
		return new ParameterDomain1d(this.knotsV[this.degreeV], this.knotsV[this.weighted[0].length]);
	}

	@Override
	public ParameterDomain2d parameterDomain() {
		return new ParameterDomain2d(this.uDomain(), this.vDomain());
	}

	@Override
	public ParametricEquation2to3dc parametricEquation() {
		return (u, v, dest) -> {
			final double[][] columns = new double[this.weighted.length][4];
			for (int i = 0; i < this.weighted.length; i++) {
				columns[i] = BSplineBasis.evaluate(this.degreeV, this.knotsV, this.weighted[i], v);
			}
			final double[] p = BSplineBasis.evaluate(this.degreeU, this.knotsU, columns, u);
			if (!(p[3] > 0)) {
				throw new ArithmeticException("NURBS surface denominator nonpositive");
			}
			return dest.set(p[0] / p[3], p[1] / p[3], p[2] / p[3]);
		};
	}

	@Override
	public GeometryKind geometryKind() {
		return GeometryKind.SURFACE;
	}

	@Override
	public int intrinsicDimension() {
		return 2;
	}

	@Override
	public boolean isBounded() {
		return true;
	}
}
