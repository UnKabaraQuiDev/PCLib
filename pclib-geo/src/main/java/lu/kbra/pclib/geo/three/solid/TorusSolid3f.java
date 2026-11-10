package lu.kbra.pclib.geo.three.solid;

import java.util.List;

import org.joml.Vector3fc;

import lu.kbra.pclib.geo.GeometryKind;
import lu.kbra.pclib.geo.equation.ImplicitRepresentable;
import lu.kbra.pclib.geo.equation.constraint.ComparisonConstraint3f;
import lu.kbra.pclib.geo.equation.constraint.MathConstraint3fc;
import lu.kbra.pclib.geo.equation.constraint.Relation;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem3fc;
import lu.kbra.pclib.geo.equation.parametric.ParameterDomain1d;
import lu.kbra.pclib.geo.equation.parametric.ParameterDomain3d;
import lu.kbra.pclib.geo.equation.parametric.ParametricEquation3to3fc;
import lu.kbra.pclib.geo.equation.parametric.ParametricSolid3fc;
import lu.kbra.pclib.geo.math.LCS3f;
import lu.kbra.pclib.geo.math.Vertex3f;
import lu.kbra.pclib.geo.three.surface.TorusSurface3f;

/** Filled ring torus, including its boundary. */
public final class TorusSolid3f implements SolidRegion3fc, ParametricSolid3fc, ImplicitRepresentable<ImplicitSystem3fc, MathConstraint3fc> {
	private final TorusSurface3f boundary;

	public TorusSolid3f(final Vector3fc center, final Vector3fc axis, final float majorRadius, final float minorRadius) {
		this.boundary = new TorusSurface3f(center, axis, majorRadius, minorRadius);
	}

	public TorusSurface3f boundarySurface() {
		return this.boundary;
	}

	@Override
	public ParameterDomain3d parameterDomain() {
		return new ParameterDomain3d(new ParameterDomain1d(0, 2 * Math.PI),
				new ParameterDomain1d(0, 2 * Math.PI),
				new ParameterDomain1d(0, this.boundary.minorRadius()));
	}

	@Override
	public ParametricEquation3to3fc parametricEquation() {
		final LCS3f frame = this.boundary.localCoordinateSystem();
		return (u, v, r, dest) -> {
			if (r < 0 || r > this.boundary.minorRadius()) {
				throw new IllegalArgumentException("Tube parameter outside torus");
			}
			final float radial = this.boundary.majorRadius() + r * (float) Math.cos(v);
			return dest.set(frame.origin())
					.fma(radial * (float) Math.cos(u), frame.xAxis())
					.fma(radial * (float) Math.sin(u), frame.yAxis())
					.fma(r * (float) Math.sin(v), frame.zAxis());
		};
	}

	@Override
	public ImplicitSystem3fc implicitEquation() {
		return new ImplicitSystem3fc();
	}

	@Override
	public List<MathConstraint3fc> constraints() {
		return List.of(new ComparisonConstraint3f(this.boundary.quarticEquation(), Relation.LE));
	}

	@Override
	public boolean contains(final Vector3fc point) {
		final Vertex3f p = this.boundary.localCoordinateSystem().toLocal(new Vertex3f(point), new Vertex3f());
		final float radial = (float) Math.hypot(p.x, p.y) - this.boundary.majorRadius();
		return radial * radial + p.z * p.z <= this.boundary.minorRadius() * this.boundary.minorRadius() + 1e-5f;
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
