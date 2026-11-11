package lu.kbra.pclib.geo.equation.implicit;

/** Second-order polynomial in three world coordinates; cross terms appear once. */
public record QuadraticEquation3f(float xx, float yy, float zz, float xy, float xz, float yz, float x, float y, float z, float constant)
		implements
			ImplicitEquation3fc {
	public QuadraticEquation3f {
		for (float coefficient : new float[] { xx, yy, zz, xy, xz, yz, x, y, z, constant }) {
			if (!Float.isFinite(coefficient))
				throw new IllegalArgumentException("Nonfinite coefficient");
		}
	}

	@Override
	public float value(float px, float py, float pz) {
		return xx * px * px + yy * py * py + zz * pz * pz + xy * px * py + xz * px * pz + yz * py * pz + x * px + y * py + z * pz
				+ constant;
	}

	@Override
	public QuadraticEquation3d asDouble() {
		return new QuadraticEquation3d(xx, yy, zz, xy, xz, yz, x, y, z, constant);
	}

	public static QuadraticEquation3f sphere(float cx, float cy, float cz, float radius) {
		if (!(radius > 0) || !Float.isFinite(radius))
			throw new IllegalArgumentException("Invalid radius");
		return new QuadraticEquation3f(1, 1, 1, 0, 0, 0, -2 * cx, -2 * cy, -2 * cz, cx * cx + cy * cy + cz * cz - radius * radius);
	}
}
