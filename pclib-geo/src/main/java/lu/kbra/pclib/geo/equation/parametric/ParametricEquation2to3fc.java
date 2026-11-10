package lu.kbra.pclib.geo.equation.parametric;

import org.joml.Vector3f;

@FunctionalInterface
public interface ParametricEquation2to3fc {
	Vector3f at(float u, float v, Vector3f dest);
}
