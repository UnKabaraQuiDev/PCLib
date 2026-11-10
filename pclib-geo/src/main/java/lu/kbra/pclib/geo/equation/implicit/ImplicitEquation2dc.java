package lu.kbra.pclib.geo.equation.implicit;

import org.joml.Vector2dc;

@FunctionalInterface
public interface ImplicitEquation2dc {

	double value(double x, double y);

	default double value(final Vector2dc p) {
		return this.value(p.x(), p.y());
	}

}
