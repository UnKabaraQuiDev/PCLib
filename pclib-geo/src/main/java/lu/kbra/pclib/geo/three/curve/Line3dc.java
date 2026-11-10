package lu.kbra.pclib.geo.three.curve;

import lu.kbra.pclib.geo.Geometry3dc;
import lu.kbra.pclib.geo.GeometryKind;
import lu.kbra.pclib.geo.Owned;
import lu.kbra.pclib.geo.equation.ImplicitRepresentable;
import lu.kbra.pclib.geo.equation.constraint.MathConstraint3dc;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem3dc;
import lu.kbra.pclib.geo.equation.parametric.ParametricCurve3dc;
import lu.kbra.pclib.geo.math.Axis3dc;
import lu.kbra.pclib.geo.math.LCS3d;
import lu.kbra.pclib.geo.math.Vertex3d;
import lu.kbra.pclib.geo.math.Vertex3dc;
import lu.kbra.pclib.geo.result.Empty;
import lu.kbra.pclib.geo.result.Result2;
import lu.kbra.pclib.geo.result.Result3;
import lu.kbra.pclib.geo.three.surface.Plane3dc;

public interface Line3dc extends Geometry3dc, ParametricCurve3dc, ImplicitRepresentable<ImplicitSystem3dc, MathConstraint3dc> {
	Vertex3dc origin();

	Axis3dc direction();

	default @Owned LCS3d localCoordinateSystem() {
		return this.localCoordinateSystem(new LCS3d());
	}

	default LCS3d localCoordinateSystem(final LCS3d dest) {
		return dest.setFromXAxis(this.origin(), this.direction());
	}

	Result3<@Owned Line3d, @Owned Vertex3d, Empty> intersection(Plane3dc plane);

	Result2<@Owned Line3d, @Owned Vertex3d> projectOnto(Plane3dc plane);

	Result2<@Owned Line3d, @Owned Vertex3d> projectOnto(Plane3dc plane, Axis3dc direction);

	@Owned
	Line3d reflectedAround(Plane3dc plane);

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
