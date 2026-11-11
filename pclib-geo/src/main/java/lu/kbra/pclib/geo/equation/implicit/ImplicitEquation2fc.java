package lu.kbra.pclib.geo.equation.implicit;

import org.joml.Vector2fc;

@FunctionalInterface
public interface ImplicitEquation2fc {
	float value(float x, float y);

	default float value(final Vector2fc point) {
		return value(point.x(), point.y());
	}

	default ImplicitEquation2dc asDouble() {
		return (x, y) -> value((float) x, (float) y);
	}
}
