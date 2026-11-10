package lu.kbra.pclib.geo.three.surface;

import lu.kbra.pclib.geo.Geometry3dc;
import lu.kbra.pclib.geo.GeometryKind;
import lu.kbra.pclib.geo.Owned;
import lu.kbra.pclib.geo.equation.ImplicitRepresentable;
import lu.kbra.pclib.geo.equation.constraint.MathConstraint3dc;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem3dc;
import lu.kbra.pclib.geo.equation.parametric.ParametricSurface3dc;
import lu.kbra.pclib.geo.math.Axis3d;
import lu.kbra.pclib.geo.math.LCS3d;
import lu.kbra.pclib.geo.math.Vertex3d;
import lu.kbra.pclib.geo.math.Vertex3dc;
import lu.kbra.pclib.geo.result.Empty;
import lu.kbra.pclib.geo.result.Result3;
import lu.kbra.pclib.geo.three.curve.Circle3d;

public interface Sphere3dc extends Geometry3dc, ParametricSurface3dc, ImplicitRepresentable<ImplicitSystem3dc, MathConstraint3dc> {
	Vertex3dc center();

	double radius();

	default @Owned LCS3d localCoordinateSystem() {
		return new LCS3d();
	}

	@Override
	default @Owned Axis3d normalAt(final double u, final double v) {
		return new Axis3d(this.at(u, v).sub(this.center()));
	}

	Result3<@Owned Circle3d, @Owned Vertex3d, Empty> intersection(Plane3dc plane);

	@Override
	default GeometryKind geometryKind() {
		return GeometryKind.SURFACE;
	}

	@Override
	default int intrinsicDimension() {
		return 2;
	}

	@Override
	default boolean isBounded() {
		return true;
	}
}
