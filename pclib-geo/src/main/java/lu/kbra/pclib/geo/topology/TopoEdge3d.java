package lu.kbra.pclib.geo.topology;

import java.util.Objects;

import lu.kbra.pclib.geo.equation.parametric.ParametricCurve3dc;

/** A trimmed, directed curve with topological endpoints. Reverse traversal uses TopoEdgeUse3d. */
public final class TopoEdge3d implements TopoElement3d {
	private final ParametricCurve3dc curve;
	private final double startParameter, endParameter;
	private final TopoVertex3d start, end;

	public TopoEdge3d(
			final ParametricCurve3dc curve,
			final double startParameter,
			final double endParameter,
			final TopoVertex3d start,
			final TopoVertex3d end) {
		this(curve, startParameter, endParameter, start, end, 1e-7);
	}

	public TopoEdge3d(
			final ParametricCurve3dc curve,
			final double startParameter,
			final double endParameter,
			final TopoVertex3d start,
			final TopoVertex3d end,
			final double tolerance) {
		this.curve = Objects.requireNonNull(curve);
		this.start = Objects.requireNonNull(start);
		this.end = Objects.requireNonNull(end);
		if (!Double.isFinite(startParameter) || !Double.isFinite(endParameter) || startParameter == endParameter || !(tolerance >= 0)
				|| !Double.isFinite(tolerance) || !curve.parameterDomain().contains(startParameter, 0)
				|| !curve.parameterDomain().contains(endParameter, 0)) {
			throw new IllegalArgumentException("Invalid edge parameters");
		}
		if (curve.at(startParameter).distance(start.position()) > tolerance
				|| curve.at(endParameter).distance(end.position()) > tolerance) {
			throw new IllegalArgumentException("Edge endpoints do not lie on the support curve");
		}
		this.startParameter = startParameter;
		this.endParameter = endParameter;
	}

	public ParametricCurve3dc support() {
		return this.curve;
	}

	public double startParameter() {
		return this.startParameter;
	}

	public double endParameter() {
		return this.endParameter;
	}

	public TopoVertex3d start() {
		return this.start;
	}

	public TopoVertex3d end() {
		return this.end;
	}

	@Override
	public TopologyKind topologyKind() {
		return TopologyKind.EDGE;
	}
}
