package lu.kbra.pclib.geo.equation.parametric;

import org.joml.Vector2d;

import lu.kbra.pclib.geo.Owned;
import lu.kbra.pclib.geo.equation.ParametricRepresentable;
import lu.kbra.pclib.geo.math.Axis2d;
import lu.kbra.pclib.geo.math.LCS2d;
import lu.kbra.pclib.geo.math.Vertex2d;

public interface ParametricCurve2dc extends ParametricRepresentable<ParametricEquation1to2dc> {

	default ParameterDomain1d parameterDomain() {
		return ParameterDomain1d.ALL;
	}

	default @Owned Vertex2d at(final double t) {
		return this.at(t, new Vertex2d());
	}

	default Vertex2d at(final double t, final Vertex2d dest) {
		this.parametricEquation().at(t, dest);
		return dest;
	}

	/** Unit tangent using a domain-aware finite difference. */
	default @Owned Axis2d tangentAt(final double t) {
		final ParameterDomain1d domain = this.parameterDomain();
		if (!domain.contains(t, 0)) {
			throw new IllegalArgumentException("Parameter outside domain");
		}
		final double h = 1e-6 * Math.max(1, Math.abs(t));
		final double before = domain.contains(t - h, 0) ? t - h : t;
		final double after = domain.contains(t + h, 0) ? t + h : t;
		if (before == after) {
			throw new IllegalArgumentException("No parameter neighborhood");
		}
		return new Axis2d(new Vector2d(this.at(after)).sub(this.at(before)));
	}

	default LCS2d localCoordinateSystemAt(final double t, final LCS2d dest) {
		return dest.set(this.at(t), this.tangentAt(t));
	}

	default @Owned LCS2d localCoordinateSystemAt(final double t) {
		return this.localCoordinateSystemAt(t, new LCS2d());
	}

}
