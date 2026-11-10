package lu.kbra.pclib.geo.equation.parametric;

import org.joml.Vector3f;

@FunctionalInterface
public interface ParametricEquation1to3fc {
	Vector3f at(float t, Vector3f dest);
}
