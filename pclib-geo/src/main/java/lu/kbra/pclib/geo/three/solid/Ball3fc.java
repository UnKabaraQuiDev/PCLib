package lu.kbra.pclib.geo.three.solid;

import java.util.List;

import lu.kbra.pclib.geo.GeometryKind;
import lu.kbra.pclib.geo.Owned;
import lu.kbra.pclib.geo.equation.ImplicitRepresentable;
import lu.kbra.pclib.geo.equation.constraint.MathConstraint3fc;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem3fc;
import lu.kbra.pclib.geo.equation.parametric.ParametricSolid3fc;
import lu.kbra.pclib.geo.math.Vertex3f;
import lu.kbra.pclib.geo.math.Vertex3fc;
import lu.kbra.pclib.geo.result.Empty;
import lu.kbra.pclib.geo.result.Result3;
import lu.kbra.pclib.geo.three.face.Disk3f;
import lu.kbra.pclib.geo.three.surface.Plane3fc;

public interface Ball3fc extends SolidRegion3fc, ParametricSolid3fc, ImplicitRepresentable<ImplicitSystem3fc, MathConstraint3fc> {
	Vertex3fc center();

	float radius();

	@Override
	List<MathConstraint3fc> constraints();

	Result3<@Owned Disk3f, @Owned Vertex3f, Empty> intersection(Plane3fc plane);

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
