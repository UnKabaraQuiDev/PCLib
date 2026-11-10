package lu.kbra.pclib.geo.equation.constraint;

import org.joml.Vector2dc;

import lu.kbra.pclib.geo.math.GeometryPrecision;

@FunctionalInterface
public interface MathConstraint2dc {
	boolean accepts(Vector2dc point, GeometryPrecision precision);
}
