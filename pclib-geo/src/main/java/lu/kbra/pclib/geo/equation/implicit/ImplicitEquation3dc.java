package lu.kbra.pclib.geo.equation.implicit;

import org.joml.Vector3dc;

@FunctionalInterface
public interface ImplicitEquation3dc {
	/** Zero describes the locus; sign permits inequality constraints. */
	double value(double x, double y, double z);

	default double value(final Vector3dc p) {
		return this.value(p.x(), p.y(), p.z());
	}
}
