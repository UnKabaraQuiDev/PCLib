package lu.kbra.pclib.geo.equation.parametric;

import org.joml.Vector3f;

import lu.kbra.pclib.geo.Owned;
import lu.kbra.pclib.geo.equation.ParametricRepresentable;
import lu.kbra.pclib.geo.math.Axis3f;
import lu.kbra.pclib.geo.math.LCS3f;
import lu.kbra.pclib.geo.math.Vertex3f;

public interface ParametricCurve3fc extends ParametricRepresentable<ParametricEquation1to3fc> {
	default ParameterDomain1d parameterDomain() {
		return ParameterDomain1d.ALL;
	}

	default @Owned Vertex3f at(final float t) {
		return this.at(t, new Vertex3f());
	}

	default Vertex3f at(final float t, final Vertex3f dest) {
		this.parametricEquation().at(t, dest);
		return dest;
	}

	/**
	 * Unit tangent, estimated with domain-aware finite differences; implementations may override
	 * analytically.
	 */
	default @Owned Axis3f tangentAt(final float t) {
		final ParameterDomain1d domain = this.parameterDomain();
		if (!domain.contains(t, 0)) {
			throw new IllegalArgumentException("Parameter outside domain");
		}
		final float h = 1e-3f * Math.max(1, Math.abs(t));
		final float before = domain.contains(t - h, 0) ? t - h : t;
		final float after = domain.contains(t + h, 0) ? t + h : t;
		if (before == after) {
			throw new IllegalArgumentException("No parameter neighborhood");
		}
		return new Axis3f(new Vector3f(this.at(after)).sub(this.at(before)));
	}

	default LCS3f localCoordinateSystemAt(final float t, final LCS3f dest) {
		return dest.setFromXAxis(this.at(t), this.tangentAt(t));
	}

	default @Owned LCS3f localCoordinateSystemAt(final float t) {
		return this.localCoordinateSystemAt(t, new LCS3f());
	}
}
