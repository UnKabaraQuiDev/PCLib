package lu.kbra.pclib.geo.equation.parametric;

/** Bounded or unbounded interval; inclusive ends, suitable for lines and curves. */
public record ParameterDomain1d(double min, double max, boolean minIncluded, boolean maxIncluded) {

	public static final ParameterDomain1d ALL = new ParameterDomain1d(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
	public static final ParameterDomain1d POSITIVE = new ParameterDomain1d(0, Double.POSITIVE_INFINITY);
	public static final ParameterDomain1d NEGATIVE = new ParameterDomain1d(Double.NEGATIVE_INFINITY, 0);
	public static final ParameterDomain1d UNIT = new ParameterDomain1d(0, 1);

	public ParameterDomain1d {
		if (Double.isNaN(min) || Double.isNaN(max) || min > max) {
			throw new IllegalArgumentException("Invalid domain");
		}
	}

	public ParameterDomain1d(final double min, final double max) {
		this(min, max, true, true);
	}

	public boolean contains(final double t, final double tolerance) {
		return (this.minIncluded ? t >= this.min - tolerance : t > this.min - tolerance)
				&& (this.maxIncluded ? t <= this.max + tolerance : t < this.max + tolerance);
	}

}
