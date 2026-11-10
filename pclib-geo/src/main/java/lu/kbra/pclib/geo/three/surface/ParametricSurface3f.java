package lu.kbra.pclib.geo.three.surface;

import java.util.Objects;

import lu.kbra.pclib.geo.Geometry3fc;
import lu.kbra.pclib.geo.GeometryKind;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation2to3fc;
import lu.kbra.pclib.geo.equation.parametric.ParametricSurface3fc;

/** A chart of an arbitrary 3D surface; bounds are supplied by its caller. */
public class ParametricSurface3f implements Geometry3fc, ParametricSurface3fc {
	private final ParametricEquation2to3fc equation;

	public ParametricSurface3f(final ParametricEquation2to3fc equation) {
		this.equation = Objects.requireNonNull(equation);
	}

	@Override
	public ParametricEquation2to3fc parametricEquation() {
		return this.equation;
	}

	@Override
	public GeometryKind geometryKind() {
		return GeometryKind.SURFACE;
	}

	@Override
	public int intrinsicDimension() {
		return 2;
	}

	@Override
	public boolean isBounded() {
		return false;
	}
}
