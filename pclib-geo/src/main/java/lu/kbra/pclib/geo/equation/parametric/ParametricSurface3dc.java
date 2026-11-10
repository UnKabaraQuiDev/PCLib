package lu.kbra.pclib.geo.equation.parametric;

import org.joml.Vector3d;

import lu.kbra.pclib.geo.Owned;
import lu.kbra.pclib.geo.equation.ParametricRepresentable;
import lu.kbra.pclib.geo.math.Axis3d;
import lu.kbra.pclib.geo.math.LCS3d;
import lu.kbra.pclib.geo.math.Vertex3d;

public interface ParametricSurface3dc extends ParametricRepresentable<ParametricEquation2to3dc> {
	default ParameterDomain2d parameterDomain() {
		return ParameterDomain2d.ALL;
	}

	default @Owned Vertex3d at(final double u, final double v) {
		return this.at(u, v, new Vertex3d());
	}

	default Vertex3d at(final double u, final double v, final Vertex3d dest) {
		this.parametricEquation().at(u, v, dest);
		return dest;
	}

	/** dP/du; fallback finite difference is chart-aware and preserves derivative scale. */
	default Vector3d partialUAt(final double u, final double v, final Vector3d dest) {
		if (!this.parameterDomain().contains(u, v, 0)) {
			throw new IllegalArgumentException("Parameter outside chart");
		}
		final double h = 1e-6 * Math.max(1, Math.abs(u));
		final double low = this.parameterDomain().u().contains(u - h, 0) ? u - h : u;
		final double high = this.parameterDomain().u().contains(u + h, 0) ? u + h : u;
		if (low == high) {
			throw new IllegalArgumentException("Degenerate U domain");
		}
		return dest.set(this.at(high, v)).sub(this.at(low, v)).div(high - low);
	}

	/** dP/dv; fallback finite difference is chart-aware and preserves derivative scale. */
	default Vector3d partialVAt(final double u, final double v, final Vector3d dest) {
		if (!this.parameterDomain().contains(u, v, 0)) {
			throw new IllegalArgumentException("Parameter outside chart");
		}
		final double h = 1e-6 * Math.max(1, Math.abs(v));
		final double low = this.parameterDomain().v().contains(v - h, 0) ? v - h : v;
		final double high = this.parameterDomain().v().contains(v + h, 0) ? v + h : v;
		if (low == high) {
			throw new IllegalArgumentException("Degenerate V domain");
		}
		return dest.set(this.at(u, high)).sub(this.at(u, low)).div(high - low);
	}

	default @Owned Axis3d tangentAt(final double u, final double v) {
		return new Axis3d(this.partialUAt(u, v, new Vector3d()));
	}

	/** Unit V-tangent (not the mathematical dual cotangent covector). */
	default @Owned Axis3d cotangentAt(final double u, final double v) {
		return new Axis3d(this.partialVAt(u, v, new Vector3d()));
	}

	default @Owned Axis3d normalAt(final double u, final double v) {
		return new Axis3d(this.partialUAt(u, v, new Vector3d()).cross(this.partialVAt(u, v, new Vector3d())));
	}

	default @Owned Axis3d binormalAt(final double u, final double v) {
		return this.normalAt(u, v);
	}

	default LCS3d localCoordinateSystemAt(final double u, final double v, final LCS3d dest) {
		return dest.setFromTangents(this.at(u, v), this.partialUAt(u, v, new Vector3d()), this.partialVAt(u, v, new Vector3d()));
	}

	default @Owned LCS3d localCoordinateSystemAt(final double u, final double v) {
		return this.localCoordinateSystemAt(u, v, new LCS3d());
	}
}
