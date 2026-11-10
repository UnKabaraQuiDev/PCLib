package lu.kbra.pclib.geo.equation.parametric;

import org.joml.Vector2d;

@FunctionalInterface
public interface ParametricEquation2to2dc {
	Vector2d at(double u, double v, Vector2d dest);
}
