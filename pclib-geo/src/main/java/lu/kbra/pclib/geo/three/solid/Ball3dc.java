package lu.kbra.pclib.geo.three.solid;

import java.util.List;

import lu.kbra.pclib.geo.GeometryKind;
import lu.kbra.pclib.geo.Owned;
import lu.kbra.pclib.geo.equation.ImplicitRepresentable;
import lu.kbra.pclib.geo.equation.constraint.MathConstraint3dc;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem3dc;
import lu.kbra.pclib.geo.equation.parametric.ParametricSolid3dc;
import lu.kbra.pclib.geo.math.Vertex3d;
import lu.kbra.pclib.geo.math.Vertex3dc;
import lu.kbra.pclib.geo.result.Empty;
import lu.kbra.pclib.geo.result.Result3;
import lu.kbra.pclib.geo.three.face.Disk3d;
import lu.kbra.pclib.geo.three.surface.Plane3dc;

public interface Ball3dc extends SolidRegion3dc, ParametricSolid3dc, ImplicitRepresentable<ImplicitSystem3dc, MathConstraint3dc> {
	Vertex3dc center();

	double radius();

	@Override
	List<MathConstraint3dc> constraints();

	Result3<@Owned Disk3d, @Owned Vertex3d, Empty> intersection(Plane3dc plane);

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
