package lu.kbra.pclib.geo.equation.parametric;

import org.joml.Vector2f;

@FunctionalInterface
public interface ParametricEquation1to2fc {
	Vector2f at(float t, Vector2f dest);
}
