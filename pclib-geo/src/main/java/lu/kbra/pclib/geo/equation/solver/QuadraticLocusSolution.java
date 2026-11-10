package lu.kbra.pclib.geo.equation.solver;

import lu.kbra.pclib.geo.Owned;

/**
 * Exact implicit quadratic locus in affine parameter coordinates. A t-parameter maps to world x =
 * base + basis*t; the reduced equation is t^T Q t + linear^T t + constant = 0. No geometric shape
 * is constructed.
 */
public final class QuadraticLocusSolution implements MathSolution {
	private final AffineSolution space;
	private final double[][] quadratic;
	private final double[] linear;
	private final double constant;

	public QuadraticLocusSolution(final AffineSolution space, final double[][] quadratic, final double[] linear, final double constant) {
		if (space.dimension() < 1 || quadratic.length != space.dimension() || linear.length != space.dimension()) {
			throw new IllegalArgumentException("Wrong parameter dimension");
		}
		this.space = space;
		this.quadratic = new double[quadratic.length][];
		for (int i = 0; i < quadratic.length; i++) {
			if (quadratic[i].length != quadratic.length) {
				throw new IllegalArgumentException("Non-square quadratic form");
			}
			this.quadratic[i] = quadratic[i].clone();
		}
		this.linear = linear.clone();
		this.constant = constant;
	}

	public AffineSolution ambientSubspace() {
		return this.space;
	}

	public int parameterDimension() {
		return this.space.dimension();
	}

	public @Owned double[][] quadratic() {
		final double[][] result = new double[this.quadratic.length][];
		for (int i = 0; i < this.quadratic.length; i++) {
			result[i] = this.quadratic[i].clone();
		}
		return result;
	}

	public @Owned double[] linear() {
		return this.linear.clone();
	}

	public double constant() {
		return this.constant;
	}

	public double evaluate(final double... t) {
		if (t.length != this.linear.length) {
			throw new IllegalArgumentException("Wrong parameter count");
		}
		double result = this.constant;
		for (int i = 0; i < t.length; i++) {
			result += this.linear[i] * t[i];
			for (int j = 0; j < t.length; j++) {
				result += t[i] * this.quadratic[i][j] * t[j];
			}
		}
		return result;
	}
}
