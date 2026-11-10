package lu.kbra.pclib.geo.three.surface;

import org.joml.Vector3d;
import org.joml.Vector3dc;

import lu.kbra.pclib.geo.Geometry3dc;
import lu.kbra.pclib.geo.GeometryKind;
import lu.kbra.pclib.geo.Owned;
import lu.kbra.pclib.geo.equation.parametric.BSplineBasis;
import lu.kbra.pclib.geo.equation.parametric.ParameterDomain2d;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation2to3dc;
import lu.kbra.pclib.geo.equation.parametric.ParametricSurface3dc;
import lu.kbra.pclib.geo.math.Axis3d;

/** Rectangular Bézier surface control net indexed [u][v]. */
public class BezierSurface3d implements Geometry3dc, ParametricSurface3dc {
	private final double[][][] controls;

	public BezierSurface3d(final Vector3dc[][] controlNet) {
		if (controlNet.length < 2 || controlNet[0].length < 2) {
			throw new IllegalArgumentException("Need at least 2x2 controls");
		}
		final int vCount = controlNet[0].length;
		this.controls = new double[controlNet.length][vCount][3];
		for (int i = 0; i < this.controls.length; i++) {
			if (controlNet[i].length != vCount) {
				throw new IllegalArgumentException("Nonrectangular control net");
			}
			for (int j = 0; j < vCount; j++) {
				final Vector3dc p = controlNet[i][j];
				if (!Double.isFinite(p.x()) || !Double.isFinite(p.y()) || !Double.isFinite(p.z())) {
					throw new IllegalArgumentException("Nonfinite surface point");
				}
				this.controls[i][j] = new double[] { p.x(), p.y(), p.z() };
			}
		}
	}

	private static double[] evaluate(final double[][][] net, final double u, final double v) {
		final double[][] alongU = new double[net.length][3];
		for (int i = 0; i < net.length; i++) {
			alongU[i] = BSplineBasis.evaluateBezier(net[i], v);
		}
		return BSplineBasis.evaluateBezier(alongU, u);
	}

	@Override
	public ParametricEquation2to3dc parametricEquation() {
		return (u, v, dest) -> {
			final double[] p = BezierSurface3d.evaluate(this.controls, u, v);
			return dest.set(p[0], p[1], p[2]);
		};
	}

	@Override
	public ParameterDomain2d parameterDomain() {
		return ParameterDomain2d.UNIT;
	}

	@Override
	public Vector3d partialUAt(final double u, final double v, final Vector3d dest) {
		return this.tangentUAt(u, v, dest);
	}

	@Override
	public Vector3d partialVAt(final double u, final double v, final Vector3d dest) {
		return this.tangentVAt(u, v, dest);
	}

	public Vector3d tangentUAt(final double u, final double v, final Vector3d dest) {
		final double[][][] derivatives = new double[this.controls.length - 1][this.controls[0].length][3];
		for (int i = 0; i < derivatives.length; i++) {
			for (int j = 0; j < derivatives[i].length; j++) {
				for (int k = 0; k < 3; k++) {
					derivatives[i][j][k] = (this.controls.length - 1) * (this.controls[i + 1][j][k] - this.controls[i][j][k]);
				}
			}
		}
		final double[] p = BezierSurface3d.evaluate(derivatives, u, v);
		return dest.set(p[0], p[1], p[2]);
	}

	public Vector3d tangentVAt(final double u, final double v, final Vector3d dest) {
		final double[][][] derivatives = new double[this.controls.length][this.controls[0].length - 1][3];
		for (int i = 0; i < derivatives.length; i++) {
			for (int j = 0; j < derivatives[i].length; j++) {
				for (int k = 0; k < 3; k++) {
					derivatives[i][j][k] = (this.controls[i].length - 1) * (this.controls[i][j + 1][k] - this.controls[i][j][k]);
				}
			}
		}
		final double[] p = BezierSurface3d.evaluate(derivatives, u, v);
		return dest.set(p[0], p[1], p[2]);
	}

	@Override
	public @Owned Axis3d normalAt(final double u, final double v) {
		final Vector3d a = this.tangentUAt(u, v, new Vector3d()), b = this.tangentVAt(u, v, new Vector3d());
		return new Axis3d(a.cross(b));
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
