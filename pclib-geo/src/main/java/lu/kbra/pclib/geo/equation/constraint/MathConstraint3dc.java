package lu.kbra.pclib.geo.equation.constraint;

import org.joml.Vector3dc;

import lu.kbra.pclib.geo.math.GeometryPrecision;

@FunctionalInterface
public interface MathConstraint3dc {
	boolean accepts(Vector3dc point, GeometryPrecision precision);
}
