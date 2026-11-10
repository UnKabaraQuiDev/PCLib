package lu.kbra.pclib.geo.equation.constraint;

import java.util.Objects;

import org.joml.Vector3dc;

import lu.kbra.pclib.geo.equation.implicit.LinearEquation3d;
import lu.kbra.pclib.geo.math.GeometryPrecision;

/** A linear comparison with zero, retaining coefficients for exact interval clipping. */
public record LinearConstraint3d(LinearEquation3d expression, Relation relation) implements MathConstraint3dc {
	public LinearConstraint3d {
		Objects.requireNonNull(expression);
		Objects.requireNonNull(relation);
	}

	@Override
	public boolean accepts(final Vector3dc point, final GeometryPrecision precision) {
		final double value = this.expression.value(point), epsilon = precision.distance();
		return switch (this.relation) {
		case EQ -> Math.abs(value) <= epsilon;
		case NE -> Math.abs(value) > epsilon;
		case LT -> value < -epsilon;
		case LE -> value <= epsilon;
		case GT -> value > epsilon;
		case GE -> value >= -epsilon;
		};
	}
}
