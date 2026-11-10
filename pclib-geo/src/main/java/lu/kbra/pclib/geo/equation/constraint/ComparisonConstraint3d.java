package lu.kbra.pclib.geo.equation.constraint;

import org.joml.Vector3dc;

import lu.kbra.pclib.geo.equation.implicit.ImplicitEquation3dc;
import lu.kbra.pclib.geo.math.GeometryPrecision;

/** World-space F(x,y,z) {relation} 0. */
public record ComparisonConstraint3d(ImplicitEquation3dc equation, Relation relation) implements MathConstraint3dc {
	@Override
	public boolean accepts(final Vector3dc point, final GeometryPrecision precision) {
		final double v = this.equation.value(point), e = precision.distance();
		return switch (this.relation) {
		case EQ -> Math.abs(v) <= e;
		case NE -> Math.abs(v) > e;
		case LT -> v < -e;
		case LE -> v <= e;
		case GT -> v > e;
		case GE -> v >= -e;
		};
	}
}
