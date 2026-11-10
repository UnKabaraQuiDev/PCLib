package lu.kbra.pclib.geo.equation.parametric;

import org.joml.Vector3f;

import lu.kbra.pclib.geo.Owned;
import lu.kbra.pclib.geo.equation.ParametricRepresentable;
import lu.kbra.pclib.geo.math.Axis3f;
import lu.kbra.pclib.geo.math.LCS3f;
import lu.kbra.pclib.geo.math.Vertex3f;

public interface ParametricSurface3fc extends ParametricRepresentable<ParametricEquation2to3fc> {
	default ParameterDomain2d parameterDomain() {
		return ParameterDomain2d.ALL;
	}

	default @Owned Vertex3f at(final float u, final float v) {
		return this.at(u, v, new Vertex3f());
	}

	default Vertex3f at(final float u, final float v, final Vertex3f dest) {
		this.parametricEquation().at(u, v, dest);
		return dest;
	}

	/** dP/du; fallback finite difference is chart-aware and preserves derivative scale. */
	default Vector3f partialUAt(final float u, final float v, final Vector3f dest) {
		if (!this.parameterDomain().contains(u, v, 0)) {
			throw new IllegalArgumentException("Parameter outside chart");
		}
		final float h = 1e-3f * Math.max(1, Math.abs(u));
		final float low = this.parameterDomain().u().contains(u - h, 0) ? u - h : u;
		final float high = this.parameterDomain().u().contains(u + h, 0) ? u + h : u;
		if (low == high) {
			throw new IllegalArgumentException("Degenerate U domain");
		}
		return dest.set(this.at(high, v)).sub(this.at(low, v)).div(high - low);
	}

	/** dP/dv; fallback finite difference is chart-aware and preserves derivative scale. */
	default Vector3f partialVAt(final float u, final float v, final Vector3f dest) {
		if (!this.parameterDomain().contains(u, v, 0)) {
			throw new IllegalArgumentException("Parameter outside chart");
		}
		final float h = 1e-3f * Math.max(1, Math.abs(v));
		final float low = this.parameterDomain().v().contains(v - h, 0) ? v - h : v;
		final float high = this.parameterDomain().v().contains(v + h, 0) ? v + h : v;
		if (low == high) {
			throw new IllegalArgumentException("Degenerate V domain");
		}
		return dest.set(this.at(u, high)).sub(this.at(u, low)).div(high - low);
	}

	default @Owned Axis3f tangentAt(final float u, final float v) {
		return new Axis3f(this.partialUAt(u, v, new Vector3f()));
	}

	/** Unit V-tangent (not the mathematical dual cotangent covector). */
	default @Owned Axis3f cotangentAt(final float u, final float v) {
		return new Axis3f(this.partialVAt(u, v, new Vector3f()));
	}

	default @Owned Axis3f normalAt(final float u, final float v) {
		return new Axis3f(this.partialUAt(u, v, new Vector3f()).cross(this.partialVAt(u, v, new Vector3f())));
	}

	default @Owned Axis3f binormalAt(final float u, final float v) {
		return this.normalAt(u, v);
	}

	default LCS3f localCoordinateSystemAt(final float u, final float v, final LCS3f dest) {
		return dest.setFromTangents(this.at(u, v), this.partialUAt(u, v, new Vector3f()), this.partialVAt(u, v, new Vector3f()));
	}

	default @Owned LCS3f localCoordinateSystemAt(final float u, final float v) {
		return this.localCoordinateSystemAt(u, v, new LCS3f());
	}
}
