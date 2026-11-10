package lu.kbra.pclib.geo.topology;

import java.util.Objects;

import lu.kbra.pclib.geo.equation.parametric.ParametricCurve3fc;

/** A trimmed, directed curve with topological endpoints. Reverse traversal uses TopoEdgeUse3f. */
public final class TopoEdge3f implements TopoElement3f {
	private final ParametricCurve3fc curve;
	private final float startParameter, endParameter;
	private final TopoVertex3f start, end;

	public TopoEdge3f(
			final ParametricCurve3fc curve,
			final float startParameter,
			final float endParameter,
			final TopoVertex3f start,
			final TopoVertex3f end) {
		this(curve, startParameter, endParameter, start, end, 1e-5f);
	}

	public TopoEdge3f(
			final ParametricCurve3fc curve,
			final float startParameter,
			final float endParameter,
			final TopoVertex3f start,
			final TopoVertex3f end,
			final float tolerance) {
		this.curve = Objects.requireNonNull(curve);
		this.start = Objects.requireNonNull(start);
		this.end = Objects.requireNonNull(end);
		if (!Float.isFinite(startParameter) || !Float.isFinite(endParameter) || startParameter == endParameter || !(tolerance >= 0)
				|| !Float.isFinite(tolerance) || !curve.parameterDomain().contains(startParameter, 0)
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

	public ParametricCurve3fc support() {
		return this.curve;
	}

	public float startParameter() {
		return this.startParameter;
	}

	public float endParameter() {
		return this.endParameter;
	}

	public TopoVertex3f start() {
		return this.start;
	}

	public TopoVertex3f end() {
		return this.end;
	}

	@Override
	public TopologyKind topologyKind() {
		return TopologyKind.EDGE;
	}
}
