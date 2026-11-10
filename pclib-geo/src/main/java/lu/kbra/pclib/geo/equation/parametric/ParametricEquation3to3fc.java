package lu.kbra.pclib.geo.equation.parametric;

import org.joml.Vector3f;

@FunctionalInterface
public interface ParametricEquation3to3fc {
	Vector3f at(float u, float v, float w, Vector3f dest);
}
