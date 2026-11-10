package lu.kbra.pclib.geo.csg;

import java.util.List;
import java.util.Objects;

import org.joml.Vector3dc;

import lu.kbra.pclib.geo.GeometryKind;
import lu.kbra.pclib.geo.Owned;
import lu.kbra.pclib.geo.equation.ImplicitRepresentable;
import lu.kbra.pclib.geo.equation.constraint.MathConstraint3dc;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem3dc;
import lu.kbra.pclib.geo.three.solid.SolidRegion3dc;

/** Lazy set operations; does not attempt B-rep boundary reconstruction. */
public final class CSGSolid3d implements SolidRegion3dc, ImplicitRepresentable<ImplicitSystem3dc, MathConstraint3dc> {

	public enum Operation {
		UNION,
		INTERSECTION,
		DIFFERENCE,
		XOR
	}

	private final SolidRegion3dc left;
	private final SolidRegion3dc right;
	private final Operation operation;

	public CSGSolid3d(final SolidRegion3dc left, final SolidRegion3dc right, final Operation operation) {
		this.left = Objects.requireNonNull(left);
		this.right = Objects.requireNonNull(right);
		this.operation = Objects.requireNonNull(operation);
	}

	public static CSGSolid3d union(final SolidRegion3dc a, final SolidRegion3dc b) {
		return new CSGSolid3d(a, b, Operation.UNION);
	}

	public static CSGSolid3d intersection(final SolidRegion3dc a, final SolidRegion3dc b) {
		return new CSGSolid3d(a, b, Operation.INTERSECTION);
	}

	public static CSGSolid3d difference(final SolidRegion3dc a, final SolidRegion3dc b) {
		return new CSGSolid3d(a, b, Operation.DIFFERENCE);
	}

	@Override
	public boolean contains(final Vector3dc point) {
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
	public ImplicitSystem3dc implicitEquation() {
		return new ImplicitSystem3dc();
	}

	@Override
	public @Owned List<MathConstraint3dc> constraints() {
		return List.of((p, precision) -> this.contains(p));
	}

	public SolidRegion3dc left() {
		return this.left;
	}

	public SolidRegion3dc right() {
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
