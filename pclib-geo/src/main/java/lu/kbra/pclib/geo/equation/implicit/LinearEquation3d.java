package lu.kbra.pclib.geo.equation.implicit;

/** a*x+b*y+c*z+d=0. */
public record LinearEquation3d(double a, double b, double c, double d) implements ImplicitEquation3dc {
	@Override
	public double value(final double x, final double y, final double z) {
		return this.a * x + this.b * y + this.c * z + this.d;
	}

	public double[] coefficients() {
		return new double[] { this.a, this.b, this.c };
	}
}
