package lu.kbra.pclib.geo.three.curve;

import lu.kbra.pclib.geo.Geometry3fc;
import lu.kbra.pclib.geo.GeometryKind;
import lu.kbra.pclib.geo.Owned;
import lu.kbra.pclib.geo.equation.ImplicitRepresentable;
import lu.kbra.pclib.geo.equation.constraint.MathConstraint3fc;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem3fc;
import lu.kbra.pclib.geo.equation.parametric.ParametricCurve3fc;
import lu.kbra.pclib.geo.math.Axis3fc;
import lu.kbra.pclib.geo.math.LCS3f;
import lu.kbra.pclib.geo.math.Vertex3f;
import lu.kbra.pclib.geo.math.Vertex3fc;
import lu.kbra.pclib.geo.result.Empty;
import lu.kbra.pclib.geo.result.Result2;
import lu.kbra.pclib.geo.result.Result3;
import lu.kbra.pclib.geo.three.surface.Plane3fc;

public interface Line3fc extends Geometry3fc, ParametricCurve3fc, ImplicitRepresentable<ImplicitSystem3fc, MathConstraint3fc> {
	Vertex3fc origin();

	Axis3fc direction();

	default @Owned LCS3f localCoordinateSystem() {
		return this.localCoordinateSystem(new LCS3f());
	}

	default LCS3f localCoordinateSystem(final LCS3f dest) {
		return dest.setFromXAxis(this.origin(), this.direction());
	}

	Result3<@Owned Line3f, @Owned Vertex3f, Empty> intersection(Plane3fc plane);

	Result2<@Owned Line3f, @Owned Vertex3f> projectOnto(Plane3fc plane);

	Result2<@Owned Line3f, @Owned Vertex3f> projectOnto(Plane3fc plane, Axis3fc direction);

	@Owned
	Line3f reflectedAround(Plane3fc plane);

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
