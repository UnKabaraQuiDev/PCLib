package lu.kbra.pclib.geo.equation.parametric;

import java.util.Objects;

/** Independent coordinate intervals for a three-parameter volume chart. */
public record ParameterDomain3d(ParameterDomain1d u, ParameterDomain1d v, ParameterDomain1d w) {
	public static final ParameterDomain3d ALL = new ParameterDomain3d(ParameterDomain1d.ALL, ParameterDomain1d.ALL, ParameterDomain1d.ALL);

	public ParameterDomain3d {
		Objects.requireNonNull(u);
		Objects.requireNonNull(v);
		Objects.requireNonNull(w);
	}

	public boolean contains(final double u, final double v, final double w, final double tolerance) {
		return this.u.contains(u, tolerance) && this.v.contains(v, tolerance) && this.w.contains(w, tolerance);
	}
}
