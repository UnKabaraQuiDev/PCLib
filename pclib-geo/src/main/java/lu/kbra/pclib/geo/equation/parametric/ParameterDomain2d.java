package lu.kbra.pclib.geo.equation.parametric;

import java.util.Objects;

/** Independent coordinate intervals for a rectangular parameter chart. */
public record ParameterDomain2d(ParameterDomain1d u, ParameterDomain1d v) {
	public static final ParameterDomain2d ALL = new ParameterDomain2d(ParameterDomain1d.ALL, ParameterDomain1d.ALL);
	public static final ParameterDomain2d UNIT = new ParameterDomain2d(ParameterDomain1d.UNIT, ParameterDomain1d.UNIT);

	public ParameterDomain2d {
		Objects.requireNonNull(u);
		Objects.requireNonNull(v);
	}

	public boolean contains(final double u, final double v, final double tolerance) {
		return this.u.contains(u, tolerance) && this.v.contains(v, tolerance);
	}
}
