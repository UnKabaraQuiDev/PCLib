package lu.kbra.pclib.geo.equation.parametric;

import org.joml.Vector2d;

import lu.kbra.pclib.geo.Owned;

@FunctionalInterface
public interface ParametricEquation1to2dc {

	Vector2d at(double t, Vector2d dest);

	default @Owned Vector2d at(final double t) {
		return this.at(t, new Vector2d());
	}

}
