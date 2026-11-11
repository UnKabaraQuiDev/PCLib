package lu.kbra.pclib.geo.equation.implicit;

/** a*x + b*y + c*z + d = 0. */
public record LinearEquation3f(float a, float b, float c, float d) implements ImplicitEquation3fc {
	@Override
	public float value(final float x, final float y, final float z) {
		return a * x + b * y + c * z + d;
	}

	public float[] coefficients() {
		return new float[] { a, b, c };
	}

	@Override
	public LinearEquation3d asDouble() {
		return new LinearEquation3d(a, b, c, d);
	}
}
