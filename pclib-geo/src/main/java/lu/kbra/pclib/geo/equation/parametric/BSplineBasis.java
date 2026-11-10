package lu.kbra.pclib.geo.equation.parametric;

/**
 * Numerical de Boor evaluation for vector-valued and rational B-splines. Control coordinates are
 * arbitrary dimensions; e.g., 4 for homogeneous 3D NURBS.
 */
public final class BSplineBasis {

	private BSplineBasis() {
	}

	public static void validate(final int degree, final int controlCount, final double[] knots) {
		if (degree < 1 || controlCount <= degree || knots == null || knots.length != controlCount + degree + 1) {
			throw new IllegalArgumentException("Incompatible B-spline degree, poles and knot vector");
		}
		for (int i = 0; i < knots.length; i++) {
			if (!Double.isFinite(knots[i]) || i > 0 && knots[i] < knots[i - 1]) {
				throw new IllegalArgumentException("Knots must be finite and nondecreasing");
			}
		}
		if (!(knots[degree] < knots[controlCount])) {
			throw new IllegalArgumentException("Empty B-spline parameter domain");
		}
	}

	public static double minParameter(final int degree, final double[] knots) {
		return knots[degree];
	}

	public static double maxParameter(final int controlCount, final double[] knots) {
		return knots[controlCount];
	}

	public static double[] evaluate(final int degree, final double[] knots, final double[][] controls, final double parameter) {
		BSplineBasis.validate(degree, controls.length, knots);
		final int dimensions = controls[0].length;
		for (final double[] control : controls) {
			if (control.length != dimensions) {
				throw new IllegalArgumentException("Inconsistent pole dimension");
			}
			for (final double value : control) {
				if (!Double.isFinite(value)) {
					throw new IllegalArgumentException("Nonfinite control coordinate");
				}
			}
		}
		final int count = controls.length;
		final double min = knots[degree], max = knots[count];
		if (!Double.isFinite(parameter) || parameter < min || parameter > max) {
			throw new IllegalArgumentException("Parameter outside knot domain");
		}

		final int span = BSplineBasis.findSpan(degree, knots, count, parameter);
		final double[][] work = new double[degree + 1][dimensions];
		for (int j = 0; j <= degree; j++) {
			System.arraycopy(controls[span - degree + j], 0, work[j], 0, dimensions);
		}

		for (int stage = 1; stage <= degree; stage++) {
			for (int j = degree; j >= stage; j--) {
				final int index = span - degree + j;
				final double denominator = knots[index + degree - stage + 1] - knots[index];
				final double alpha = denominator == 0 ? 0 : (parameter - knots[index]) / denominator;
				for (int component = 0; component < dimensions; component++) {
					work[j][component] = (1 - alpha) * work[j - 1][component] + alpha * work[j][component];
				}
			}
		}
		return work[degree];
	}

	private static int findSpan(final int degree, final double[] knots, final int count, final double t) {
		if (t == knots[count]) {
			return count - 1;
		}
		int low = degree, high = count;
		while (low + 1 < high) {
			final int mid = (low + high) / 2;
			if (t < knots[mid]) {
				high = mid;
			} else {
				low = mid;
			}
		}
		return low;
	}

	/** De Casteljau evaluation for a Bézier control polygon of arbitrary dimension. */
	public static double[] evaluateBezier(final double[][] controls, final double t) {
		if (controls == null || controls.length == 0 || !Double.isFinite(t) || t < 0 || t > 1) {
			throw new IllegalArgumentException("Invalid Bézier controls or parameter");
		}
		final int dimensions = controls[0].length;
		final double[][] work = new double[controls.length][dimensions];
		for (int i = 0; i < controls.length; i++) {
			if (controls[i].length != dimensions) {
				throw new IllegalArgumentException("Inconsistent control dimensions");
			}
			System.arraycopy(controls[i], 0, work[i], 0, dimensions);
		}
		for (int stage = 1; stage < controls.length; stage++) {
			for (int i = 0; i < controls.length - stage; i++) {
				for (int component = 0; component < dimensions; component++) {
					work[i][component] = (1 - t) * work[i][component] + t * work[i + 1][component];
				}
			}
		}
		return work[0];
	}

}
