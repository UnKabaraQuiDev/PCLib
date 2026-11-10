package lu.kbra.pclib.geo.equation.parametric;

import org.joml.Vector3d;

@FunctionalInterface
public interface ParametricEquation3to3dc {
	Vector3d at(double u, double v, double w, Vector3d dest);
}
