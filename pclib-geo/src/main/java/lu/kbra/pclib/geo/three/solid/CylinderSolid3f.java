package lu.kbra.pclib.geo.three.solid;

import java.util.ArrayList;
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
import lu.kbra.pclib.geo.three.surface.CylinderSurface3f;

/** Filled cylinder with two end disks; its implicit representation is a set of inequalities. */
public final class CylinderSolid3f
		implements
			SolidRegion3fc,
			ParametricSolid3fc,
			ImplicitRepresentable<ImplicitSystem3fc, MathConstraint3fc> {
	private final CylinderSurface3f lateral;

	public CylinderSolid3f(final Vector3fc base, final Vector3fc axis, final float radius, final float height) {
		this.lateral = new CylinderSurface3f(base, axis, radius, height);
	}

	public CylinderSurface3f lateralSurface() {
		return this.lateral;
	}

	@Override
	public ParameterDomain3d parameterDomain() {
		return new ParameterDomain3d(new ParameterDomain1d(0, 2 * Math.PI),
				new ParameterDomain1d(0, this.lateral.height()),
				new ParameterDomain1d(0, this.lateral.radius()));
	}

	@Override
	public ParametricEquation3to3fc parametricEquation() {
		final LCS3f frame = this.lateral.localCoordinateSystem();
		return (u, v, r, dest) -> {
			if (r < 0 || r > this.lateral.radius()) {
				throw new IllegalArgumentException("Radial parameter outside cylinder");
			}
			return dest.set(frame.origin()).fma(r * (float) Math.cos(u), frame.xAxis()).fma(r * (float) Math.sin(u), frame.yAxis()).fma(v, frame.zAxis());
		};
	}

	@Override
	public ImplicitSystem3fc implicitEquation() {
		return new ImplicitSystem3fc();
	}

	@Override
	public List<MathConstraint3fc> constraints() {
		final List<MathConstraint3fc> restrictions = new ArrayList<>();
		restrictions.add(new ComparisonConstraint3f(this.lateral.radialEquation(), Relation.LE));
		restrictions.addAll(this.lateral.constraints());
		return List.copyOf(restrictions);
	}

	@Override
	public boolean contains(final Vector3fc point) {
		final Vertex3f local = this.lateral.localCoordinateSystem().toLocal(new Vertex3f(point), new Vertex3f());
		return local.z >= -1e-5f && local.z <= this.lateral.height() + 1e-5f
				&& local.x * local.x + local.y * local.y <= this.lateral.radius() * this.lateral.radius() + 1e-5f;
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
