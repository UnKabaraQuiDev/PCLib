package lu.kbra.pclib.geo.equation.constraint;
import org.joml.Vector3fc;
import lu.kbra.pclib.geo.equation.implicit.ImplicitEquation3fc;
import lu.kbra.pclib.geo.math.GeometryPrecision;
public record ComparisonConstraint3f(ImplicitEquation3fc equation, Relation relation) implements MathConstraint3fc {
    @Override public ComparisonConstraint3d asDouble() {
        return new ComparisonConstraint3d(equation.asDouble(), relation);
    }
    @Override public boolean accepts(Vector3fc point, GeometryPrecision precision) {
        double v = equation.value(point), e = precision.distance();
        return switch(relation) {
            case EQ -> Math.abs(v) <= e;
            case NE -> Math.abs(v) > e;
            case LT -> v < -e;
            case LE -> v <= e;
            case GT -> v > e;
            case GE -> v >= -e;
        };
    }
}
