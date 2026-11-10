package lu.kbra.pclib.geo.three.surface;

import java.util.Objects;

import lu.kbra.pclib.geo.Geometry3dc;
import lu.kbra.pclib.geo.GeometryKind;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation2to3dc;
import lu.kbra.pclib.geo.equation.parametric.ParametricSurface3dc;

/** A chart of an arbitrary 3D surface; bounds are supplied by its caller. */
public class ParametricSurface3d implements Geometry3dc, ParametricSurface3dc {
	private final ParametricEquation2to3dc equation;

	public ParametricSurface3d(final ParametricEquation2to3dc equation) {
		this.equation = Objects.requireNonNull(equation);
	}

	@Override
	public ParametricEquation2to3dc parametricEquation() {
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
