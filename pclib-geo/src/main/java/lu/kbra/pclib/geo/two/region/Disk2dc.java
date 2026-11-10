package lu.kbra.pclib.geo.two.region;

import java.util.List;

import lu.kbra.pclib.geo.Geometry2dc;
import lu.kbra.pclib.geo.GeometryKind;
import lu.kbra.pclib.geo.Owned;
import lu.kbra.pclib.geo.equation.ImplicitRepresentable;
import lu.kbra.pclib.geo.equation.ParametricRepresentable;
import lu.kbra.pclib.geo.equation.constraint.MathConstraint2dc;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem2dc;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation2to2dc;
import lu.kbra.pclib.geo.math.Vertex2d;
import lu.kbra.pclib.geo.math.Vertex2dc;

public interface Disk2dc
		extends
			Geometry2dc,
			ParametricRepresentable<ParametricEquation2to2dc>,
			ImplicitRepresentable<ImplicitSystem2dc, MathConstraint2dc> {

	Vertex2dc center();

	double radius();

	@Owned
	Vertex2d at(double angle, double r, Vertex2d dest);

	default @Owned Vertex2d at(final double angle, final double r) {
		return this.at(angle, r, new Vertex2d());
	}

	@Override
	@Owned
	List<MathConstraint2dc> constraints();

	@Override
	default GeometryKind geometryKind() {
		return GeometryKind.REGION;
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
