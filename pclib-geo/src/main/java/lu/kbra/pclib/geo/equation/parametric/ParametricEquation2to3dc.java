package lu.kbra.pclib.geo.equation.parametric;

import org.joml.Vector3d;

@FunctionalInterface
public interface ParametricEquation2to3dc {
	Vector3d at(double u, double v, Vector3d dest);
}
