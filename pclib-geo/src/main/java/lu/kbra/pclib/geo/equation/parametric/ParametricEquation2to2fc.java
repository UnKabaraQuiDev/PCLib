package lu.kbra.pclib.geo.equation.parametric;

import org.joml.Vector2f;

@FunctionalInterface
public interface ParametricEquation2to2fc {
	Vector2f at(float u, float v, Vector2f dest);
}
