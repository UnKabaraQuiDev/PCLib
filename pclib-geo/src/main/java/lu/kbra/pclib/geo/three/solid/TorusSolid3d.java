package lu.kbra.pclib.geo.three.solid;

import java.util.List;

import org.joml.Vector3dc;

import lu.kbra.pclib.geo.GeometryKind;
import lu.kbra.pclib.geo.equation.ImplicitRepresentable;
import lu.kbra.pclib.geo.equation.constraint.ComparisonConstraint3d;
import lu.kbra.pclib.geo.equation.constraint.MathConstraint3dc;
import lu.kbra.pclib.geo.equation.constraint.Relation;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem3dc;
import lu.kbra.pclib.geo.equation.parametric.ParameterDomain1d;
import lu.kbra.pclib.geo.equation.parametric.ParameterDomain3d;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation3to3dc;
import lu.kbra.pclib.geo.equation.parametric.ParametricSolid3dc;
import lu.kbra.pclib.geo.math.LCS3d;
import lu.kbra.pclib.geo.math.Vertex3d;
import lu.kbra.pclib.geo.three.surface.TorusSurface3d;

/** Filled ring torus, including its boundary. */
public final class TorusSolid3d implements SolidRegion3dc, ParametricSolid3dc, ImplicitRepresentable<ImplicitSystem3dc, MathConstraint3dc> {
	private final TorusSurface3d boundary;

	public TorusSolid3d(final Vector3dc center, final Vector3dc axis, final double majorRadius, final double minorRadius) {
		this.boundary = new TorusSurface3d(center, axis, majorRadius, minorRadius);
	}

	public TorusSurface3d boundarySurface() {
		return this.boundary;
	}

	@Override
	public ParameterDomain3d parameterDomain() {
		return new ParameterDomain3d(new ParameterDomain1d(0, 2 * Math.PI),
				new ParameterDomain1d(0, 2 * Math.PI),
				new ParameterDomain1d(0, this.boundary.minorRadius()));
	}

	@Override
	public ParametricEquation3to3dc parametricEquation() {
		final LCS3d frame = this.boundary.localCoordinateSystem();
		return (u, v, r, dest) -> {
			if (r < 0 || r > this.boundary.minorRadius()) {
				throw new IllegalArgumentException("Tube parameter outside torus");
			}
			final double radial = this.boundary.majorRadius() + r * Math.cos(v);
			return dest.set(frame.origin())
					.fma(radial * Math.cos(u), frame.xAxis())
					.fma(radial * Math.sin(u), frame.yAxis())
					.fma(r * Math.sin(v), frame.zAxis());
		};
	}

	@Override
	public ImplicitSystem3dc implicitEquation() {
		return new ImplicitSystem3dc();
	}

	@Override
	public List<MathConstraint3dc> constraints() {
		return List.of(new ComparisonConstraint3d(this.boundary.quarticEquation(), Relation.LE));
	}

	@Override
	public boolean contains(final Vector3dc point) {
		final Vertex3d p = this.boundary.localCoordinateSystem().toLocal(new Vertex3d(point), new Vertex3d());
		final double radial = Math.hypot(p.x, p.y) - this.boundary.majorRadius();
		return radial * radial + p.z * p.z <= this.boundary.minorRadius() * this.boundary.minorRadius() + 1e-10;
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
		return true;
	}
}
