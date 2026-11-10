package lu.kbra.pclib.geo.two.curve;

import java.util.Objects;

import lu.kbra.pclib.geo.Geometry2dc;
import lu.kbra.pclib.geo.GeometryKind;
import lu.kbra.pclib.geo.equation.parametric.ParameterDomain1d;
import lu.kbra.pclib.geo.equation.parametric.ParametricCurve2dc;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation1to2dc;

public class ParametricCurve2d implements Geometry2dc, ParametricCurve2dc {

	private final ParametricEquation1to2dc equation;
	private final ParameterDomain1d domain;

	public ParametricCurve2d(final ParametricEquation1to2dc equation, final ParameterDomain1d domain) {
		this.equation = Objects.requireNonNull(equation);
		this.domain = Objects.requireNonNull(domain);
	}

	@Override
	public ParametricEquation1to2dc parametricEquation() {
		return this.equation;
	}

	@Override
	public ParameterDomain1d parameterDomain() {
		return this.domain;
	}

	@Override
	public GeometryKind geometryKind() {
		return GeometryKind.CURVE;
	}

	@Override
	public int intrinsicDimension() {
		return 1;
	}

	@Override
	public boolean isBounded() {
		return Double.isFinite(this.domain.min()) && Double.isFinite(this.domain.max());
	}

}
