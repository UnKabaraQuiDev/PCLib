package lu.kbra.pclib.geo.three.solid;

import java.util.List;

import lu.kbra.pclib.geo.GeometryKind;
import lu.kbra.pclib.geo.equation.ImplicitRepresentable;
import lu.kbra.pclib.geo.equation.constraint.MathConstraint3dc;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem3dc;
import lu.kbra.pclib.geo.equation.parametric.ParametricSolid3dc;
import lu.kbra.pclib.geo.math.Vertex3dc;

public interface Box3dc extends SolidRegion3dc, ParametricSolid3dc, ImplicitRepresentable<ImplicitSystem3dc, MathConstraint3dc> {
	Vertex3dc minimum();

	Vertex3dc maximum();

	@Override
	List<MathConstraint3dc> constraints();

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
