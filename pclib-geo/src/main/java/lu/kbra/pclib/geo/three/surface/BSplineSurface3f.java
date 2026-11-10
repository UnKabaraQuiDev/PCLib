package lu.kbra.pclib.geo.three.surface;

import java.util.Arrays;

import org.joml.Vector3fc;

/** Nonrational surface as the uniform-weight specialization of NURBS. */
public class BSplineSurface3f extends NurbsSurface3f {
	public BSplineSurface3f(
			final int degreeU,
			final int degreeV,
			final double[] knotsU,
			final double[] knotsV,
			final Vector3fc[][] controls) {
		super(degreeU, degreeV, knotsU, knotsV, controls, BSplineSurface3f.unitWeights(controls));
	}

	private static double[][] unitWeights(final Vector3fc[][] controls) {
		final double[][] weights = new double[controls.length][];
		for (int i = 0; i < controls.length; i++) {
			weights[i] = new double[controls[i].length];
			Arrays.fill(weights[i], 1);
		}
		return weights;
	}
}
