package lu.kbra.pclib.geo.equation.parametric;

import org.joml.Vector3d;

@FunctionalInterface
public interface ParametricEquation1to3dc {
	Vector3d at(double t, Vector3d dest);
}
