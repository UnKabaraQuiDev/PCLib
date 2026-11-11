package lu.kbra.pclib.geo.equation.constraint;

import org.joml.Vector3fc;

import lu.kbra.pclib.geo.equation.implicit.LinearEquation3f;
import lu.kbra.pclib.geo.math.GeometryPrecision;

public record LinearConstraint3f(LinearEquation3f expression, Relation relation) implements MathConstraint3fc {
	@Override
	public LinearConstraint3d asDouble() {
		return new LinearConstraint3d(expression.asDouble(), relation);
	}

	@Override
	public boolean accepts(Vector3fc point, GeometryPrecision precision) {
		double v = expression.value(point), e = precision.distance();
		return switch (relation) {
		case EQ -> Math.abs(v) <= e;
		case NE -> Math.abs(v) > e;
		case LT -> v < -e;
		case LE -> v <= e;
		case GT -> v > e;
		case GE -> v >= -e;
		};
	}
}
