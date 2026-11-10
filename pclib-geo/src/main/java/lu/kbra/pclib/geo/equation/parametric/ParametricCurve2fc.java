package lu.kbra.pclib.geo.equation.parametric;

import org.joml.Vector2f;

import lu.kbra.pclib.geo.Owned;
import lu.kbra.pclib.geo.equation.ParametricRepresentable;
import lu.kbra.pclib.geo.math.Axis2f;
import lu.kbra.pclib.geo.math.LCS2f;
import lu.kbra.pclib.geo.math.Vertex2f;

public interface ParametricCurve2fc extends ParametricRepresentable<ParametricEquation1to2fc> {

	default ParameterDomain1d parameterDomain() {
		return ParameterDomain1d.ALL;
	}

	default @Owned Vertex2f at(final float t) {
		return this.at(t, new Vertex2f());
	}

	default Vertex2f at(final float t, final Vertex2f dest) {
		this.parametricEquation().at(t, dest);
		return dest;
	}

	/** Unit tangent using a domain-aware finite difference. */
	default @Owned Axis2f tangentAt(final float t) {
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
		return new Axis2f(new Vector2f(this.at(after)).sub(this.at(before)));
	}

	default LCS2f localCoordinateSystemAt(final float t, final LCS2f dest) {
		return dest.set(this.at(t), this.tangentAt(t));
	}

	default @Owned LCS2f localCoordinateSystemAt(final float t) {
		return this.localCoordinateSystemAt(t, new LCS2f());
	}

}
