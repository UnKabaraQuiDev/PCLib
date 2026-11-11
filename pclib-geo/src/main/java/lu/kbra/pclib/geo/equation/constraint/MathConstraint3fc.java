package lu.kbra.pclib.geo.equation.constraint;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import lu.kbra.pclib.geo.math.GeometryPrecision;

@FunctionalInterface
public interface MathConstraint3fc {
	boolean accepts(Vector3fc point, GeometryPrecision precision);

	default MathConstraint3dc asDouble() {
		return (point, precision) -> accepts(new Vector3f(point), precision);
	}
}
