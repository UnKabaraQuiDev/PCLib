package lu.kbra.pclib.geo.equation.solver;

import lu.kbra.pclib.geo.Owned;

/** x = origin + sum(t[i] * basis[i]); arrays are defensively copied. */
public final class AffineSolution implements MathSolution {
	private final double[] origin;
	private final double[][] basis;

	public AffineSolution(final double[] origin, final double[][] basis) {
		this.origin = origin.clone();
		this.basis = new double[basis.length][];
		for (int i = 0; i < basis.length; i++) {
			this.basis[i] = basis[i].clone();
		}
	}

	public int ambientDimension() {
		return this.origin.length;
	}

	public int dimension() {
		return this.basis.length;
	}

	public @Owned double[] origin() {
		return this.origin.clone();
	}

	public @Owned double[][] basis() {
		final double[][] result = new double[this.basis.length][];
		for (int i = 0; i < this.basis.length; i++) {
			result[i] = this.basis[i].clone();
		}
		return result;
	}

	public @Owned double[] at(final double... parameters) {
		if (parameters.length != this.basis.length) {
			throw new IllegalArgumentException("Expected " + this.basis.length + " parameters");
		}
		final double[] result = this.origin();
		for (int i = 0; i < this.basis.length; i++) {
			for (int j = 0; j < this.origin.length; j++) {
				result[j] += parameters[i] * this.basis[i][j];
			}
		}
		return result;
	}
}
