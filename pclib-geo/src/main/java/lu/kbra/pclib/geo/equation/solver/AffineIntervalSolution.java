package lu.kbra.pclib.geo.equation.solver;

import lu.kbra.pclib.geo.Owned;

/** A rank-one affine line restricted to an interval of its free parameter. */
public record AffineIntervalSolution(AffineSolution support, double min, double max, boolean includeMin, boolean includeMax)
		implements
			MathSolution {
	public AffineIntervalSolution {
		if (support.dimension() != 1 || Double.isNaN(min) || Double.isNaN(max) || min > max || min == max && !(includeMin && includeMax)) {
			throw new IllegalArgumentException("Invalid affine interval");
		}
	}

	public boolean isUnbounded() {
		return !Double.isFinite(this.min) || !Double.isFinite(this.max);
	}

	public boolean isPoint() {
		return this.min == this.max;
	}

	public @Owned double[] at(final double t) {
		if (t < this.min || t > this.max || t == this.min && !this.includeMin || t == this.max && !this.includeMax) {
			throw new IllegalArgumentException("Outside interval");
		}
		return this.support.at(t);
	}
}
