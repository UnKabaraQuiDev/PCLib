package lu.kbra.pclib.geo.equation.parametric;

import org.joml.Vector3d;

import lu.kbra.pclib.geo.Owned;
import lu.kbra.pclib.geo.equation.ParametricRepresentable;
import lu.kbra.pclib.geo.math.Axis3d;
import lu.kbra.pclib.geo.math.LCS3d;
import lu.kbra.pclib.geo.math.Vertex3d;

public interface ParametricCurve3dc extends ParametricRepresentable<ParametricEquation1to3dc> {
	default ParameterDomain1d parameterDomain() {
		return ParameterDomain1d.ALL;
	}

	default @Owned Vertex3d at(final double t) {
		return this.at(t, new Vertex3d());
	}

	default Vertex3d at(final double t, final Vertex3d dest) {
		this.parametricEquation().at(t, dest);
		return dest;
	}

	/**
	 * Unit tangent, estimated with domain-aware finite differences; implementations may override
	 * analytically.
	 */
	default @Owned Axis3d tangentAt(final double t) {
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
		return new Axis3d(new Vector3d(this.at(after)).sub(this.at(before)));
	}

	default LCS3d localCoordinateSystemAt(final double t, final LCS3d dest) {
		return dest.setFromXAxis(this.at(t), this.tangentAt(t));
	}

	default @Owned LCS3d localCoordinateSystemAt(final double t) {
		return this.localCoordinateSystemAt(t, new LCS3d());
	}
}
