package lu.kbra.pclib.geo.csg;

import java.util.List;
import java.util.Objects;

import org.joml.Vector3fc;

import lu.kbra.pclib.geo.GeometryKind;
import lu.kbra.pclib.geo.Owned;
import lu.kbra.pclib.geo.equation.ImplicitRepresentable;
import lu.kbra.pclib.geo.equation.constraint.MathConstraint3fc;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem3fc;
import lu.kbra.pclib.geo.three.solid.SolidRegion3fc;

/** Lazy set operations; does not attempt B-rep boundary reconstruction. */
public final class CSGSolid3f implements SolidRegion3fc, ImplicitRepresentable<ImplicitSystem3fc, MathConstraint3fc> {

	public enum Operation {
		UNION,
		INTERSECTION,
		DIFFERENCE,
		XOR
	}

	private final SolidRegion3fc left;
	private final SolidRegion3fc right;
	private final Operation operation;

	public CSGSolid3f(final SolidRegion3fc left, final SolidRegion3fc right, final Operation operation) {
		this.left = Objects.requireNonNull(left);
		this.right = Objects.requireNonNull(right);
		this.operation = Objects.requireNonNull(operation);
	}

	public static CSGSolid3f union(final SolidRegion3fc a, final SolidRegion3fc b) {
		return new CSGSolid3f(a, b, Operation.UNION);
	}

	public static CSGSolid3f intersection(final SolidRegion3fc a, final SolidRegion3fc b) {
		return new CSGSolid3f(a, b, Operation.INTERSECTION);
	}

	public static CSGSolid3f difference(final SolidRegion3fc a, final SolidRegion3fc b) {
		return new CSGSolid3f(a, b, Operation.DIFFERENCE);
	}

	@Override
	public boolean contains(final Vector3fc point) {
		final boolean a = this.left.contains(point);
		final boolean b = this.right.contains(point);
		return switch (this.operation) {
		case UNION -> a || b;
		case INTERSECTION -> a && b;
		case DIFFERENCE -> a && !b;
		case XOR -> a ^ b;
		};
	}

	@Override
	public ImplicitSystem3fc implicitEquation() {
		return new ImplicitSystem3fc();
	}

	@Override
	public @Owned List<MathConstraint3fc> constraints() {
		return List.of((p, precision) -> this.contains(p));
	}

	public SolidRegion3fc left() {
		return this.left;
	}

	public SolidRegion3fc right() {
		return this.right;
	}

	public Operation operation() {
		return this.operation;
	}

	@Override
	public GeometryKind geometryKind() {
		return GeometryKind.SOLID;
	}

	@Override
	public int intrinsicDimension() {
		return 3;
	}

	@Override
	public boolean isBounded() {
		return this.left.isBounded() && this.right.isBounded();
	}

}
