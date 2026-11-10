package lu.kbra.pclib.geo.math;

/** Numerical tolerances use world-space units except for the angular threshold. */
public record GeometryPrecision(double distance, double relative, double angle, double parameter) {
	public static final GeometryPrecision DEFAULT = new GeometryPrecision(1e-9, 1e-12, 1e-10, 1e-10);

	public GeometryPrecision {
		if (!(distance > 0) || !(relative >= 0) || !(angle > 0) || !(parameter > 0) || !Double.isFinite(distance)
				|| !Double.isFinite(relative) || !Double.isFinite(angle) || !Double.isFinite(parameter)) {
			throw new IllegalArgumentException("Invalid geometric precision");
		}
	}

	public boolean zero(final double value, final double scale) {
		return Math.abs(value) <= this.distance + this.relative * Math.abs(scale);
	}

	public boolean same(final double a, final double b) {
		return this.zero(a - b, Math.max(Math.abs(a), Math.abs(b)));
	}
}
