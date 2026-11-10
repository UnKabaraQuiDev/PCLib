package lu.kbra.pclib.geo.two.curve;

import lu.kbra.pclib.geo.Geometry2fc;
import lu.kbra.pclib.geo.GeometryKind;
import lu.kbra.pclib.geo.Owned;
import lu.kbra.pclib.geo.equation.ImplicitRepresentable;
import lu.kbra.pclib.geo.equation.constraint.MathConstraint2fc;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem2fc;
import lu.kbra.pclib.geo.equation.parametric.ParametricCurve2fc;
import lu.kbra.pclib.geo.math.Axis2fc;
import lu.kbra.pclib.geo.math.Vertex2f;
import lu.kbra.pclib.geo.math.Vertex2fc;
import lu.kbra.pclib.geo.result.Empty;
import lu.kbra.pclib.geo.result.Result3;

public interface Line2fc extends Geometry2fc, ParametricCurve2fc, ImplicitRepresentable<ImplicitSystem2fc, MathConstraint2fc> {

	Vertex2fc origin();

	Axis2fc direction();

	Result3<@Owned Line2f, @Owned Vertex2f, Empty> intersection(Line2fc other);

	@Override
	default GeometryKind geometryKind() {
		return GeometryKind.CURVE;
	}

	@Override
	default int intrinsicDimension() {
		return 1;
	}

	@Override
	default boolean isBounded() {
		return false;
	}

}
