package lu.kbra.pclib.geo.three.solid;

import java.util.ArrayList;
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
import lu.kbra.pclib.geo.three.surface.ConeSurface3d;

/** Filled right circular cone, including its base disk and apex. */
public final class ConeSolid3d implements SolidRegion3dc, ParametricSolid3dc, ImplicitRepresentable<ImplicitSystem3dc, MathConstraint3dc> {
	private final ConeSurface3d lateral;

	public ConeSolid3d(final Vector3dc base, final Vector3dc axis, final double radius, final double height) {
		this.lateral = new ConeSurface3d(base, axis, radius, height);
	}

	public ConeSurface3d lateralSurface() {
		return this.lateral;
	}

	@Override
	public ParameterDomain3d parameterDomain() {
		return new ParameterDomain3d(new ParameterDomain1d(0, 2 * Math.PI),
				new ParameterDomain1d(0, this.lateral.height()),
				new ParameterDomain1d(0, this.lateral.radius()));
	}

	@Override
	public ParametricEquation3to3dc parametricEquation() {
		final LCS3d frame = this.lateral.localCoordinateSystem();
		return (u, v, r, dest) -> {
			if (!this.parameterDomain().contains(u, v, r, 0) || r > this.lateral.radius() * (1 - v / this.lateral.height())) {
				throw new IllegalArgumentException("Outside filled cone chart");
			}
			return dest.set(frame.origin()).fma(r * Math.cos(u), frame.xAxis()).fma(r * Math.sin(u), frame.yAxis()).fma(v, frame.zAxis());
		};
	}

	@Override
	public ImplicitSystem3dc implicitEquation() {
		return new ImplicitSystem3dc();
	}

	@Override
	public List<MathConstraint3dc> constraints() {
		final List<MathConstraint3dc> result = new ArrayList<>(this.lateral.constraints());
		result.add(new ComparisonConstraint3d(this.lateral.quadraticEquation(), Relation.LE));
		return List.copyOf(result);
	}

	@Override
	public boolean contains(final Vector3dc point) {
		final Vertex3d local = this.lateral.localCoordinateSystem().toLocal(new Vertex3d(point), new Vertex3d());
		if (local.z < 0 || local.z > this.lateral.height()) {
			return false;
		}
		final double radius = this.lateral.radius() * (1 - local.z / this.lateral.height());
		return local.x * local.x + local.y * local.y <= radius * radius + 1e-10;
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
