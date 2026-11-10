package lu.kbra.pclib.geo.three.solid;

import java.util.List;

import lu.kbra.pclib.geo.GeometryKind;
import lu.kbra.pclib.geo.equation.ImplicitRepresentable;
import lu.kbra.pclib.geo.equation.constraint.MathConstraint3fc;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem3fc;
import lu.kbra.pclib.geo.equation.parametric.ParametricSolid3fc;
import lu.kbra.pclib.geo.math.Vertex3fc;

public interface Box3fc extends SolidRegion3fc, ParametricSolid3fc, ImplicitRepresentable<ImplicitSystem3fc, MathConstraint3fc> {
	Vertex3fc minimum();

	Vertex3fc maximum();

	@Override
	List<MathConstraint3fc> constraints();

	@Override
	default GeometryKind geometryKind() {
		return GeometryKind.SOLID;
	}

	@Override
	default int intrinsicDimension() {
		return 3;
	}

	@Override
	default boolean isBounded() {
		return true;
	}
}
