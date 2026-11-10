package lu.kbra.pclib.geo.two.region;

import java.util.List;

import lu.kbra.pclib.geo.Geometry2fc;
import lu.kbra.pclib.geo.GeometryKind;
import lu.kbra.pclib.geo.Owned;
import lu.kbra.pclib.geo.equation.ImplicitRepresentable;
import lu.kbra.pclib.geo.equation.ParametricRepresentable;
import lu.kbra.pclib.geo.equation.constraint.MathConstraint2fc;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem2fc;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation2to2fc;
import lu.kbra.pclib.geo.math.Vertex2f;
import lu.kbra.pclib.geo.math.Vertex2fc;

public interface Disk2fc
		extends
			Geometry2fc,
			ParametricRepresentable<ParametricEquation2to2fc>,
			ImplicitRepresentable<ImplicitSystem2fc, MathConstraint2fc> {

	Vertex2fc center();

	float radius();

	@Owned
	Vertex2f at(float angle, float r, Vertex2f dest);

	default @Owned Vertex2f at(final float angle, final float r) {
		return this.at(angle, r, new Vertex2f());
	}

	@Override
	@Owned
	List<MathConstraint2fc> constraints();

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
