package lu.kbra.pclib.geo.equation.implicit;

/**
 * A second-degree polynomial in world coordinates. Cross coefficients multiply xy, xz, yz once
 * (rather than twice).
 */
public record QuadraticEquation3d(
		double xx,
		double yy,
		double zz,
		double xy,
		double xz,
		double yz,
		double x,
		double y,
		double z,
		double constant) implements ImplicitEquation3dc {
	public QuadraticEquation3d {
		for (final double coefficient : new double[] { xx, yy, zz, xy, xz, yz, x, y, z, constant }) {
			if (!Double.isFinite(coefficient)) {
				throw new IllegalArgumentException("Nonfinite polynomial coefficient");
			}
		}
	}

	@Override
	public double value(final double px, final double py, final double pz) {
		return this.xx * px * px + this.yy * py * py + this.zz * pz * pz + this.xy * px * py + this.xz * px * pz + this.yz * py * pz
				+ this.x * px + this.y * py + this.z * pz + this.constant;
	}

	/** Symmetric Q in F(p) = p^T Q p + b^T p + c. */
	public double[][] quadraticMatrix() {
		return new double[][] {
				{ this.xx, this.xy / 2, this.xz / 2 },
				{ this.xy / 2, this.yy, this.yz / 2 },
				{ this.xz / 2, this.yz / 2, this.zz } };
	}

	public double[] linearCoefficients() {
		return new double[] { this.x, this.y, this.z };
	}

	public static QuadraticEquation3d sphere(final double cx, final double cy, final double cz, final double radius) {
		if (!(radius > 0) || !Double.isFinite(radius)) {
			throw new IllegalArgumentException("Invalid radius");
		}
		return new QuadraticEquation3d(1, 1, 1, 0, 0, 0, -2 * cx, -2 * cy, -2 * cz, cx * cx + cy * cy + cz * cz - radius * radius);
	}
}
