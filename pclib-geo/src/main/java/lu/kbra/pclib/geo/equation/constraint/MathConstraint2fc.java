package lu.kbra.pclib.geo.equation.constraint;
import org.joml.Vector2fc;
import lu.kbra.pclib.geo.math.GeometryPrecision;
@FunctionalInterface
public interface MathConstraint2fc { boolean accepts(Vector2fc point, GeometryPrecision precision); }
