package lu.kbra.pclib.geo.equation.implicit;

import org.joml.Vector3fc;

@FunctionalInterface
public interface ImplicitEquation3fc {
	float value(float x, float y, float z);

	default float value(final Vector3fc point) {
		return value(point.x(), point.y(), point.z());
	}

	default ImplicitEquation3dc asDouble() {
		return (x, y, z) -> value((float) x, (float) y, (float) z);
	}
}
