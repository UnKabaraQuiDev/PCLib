package lu.kbra.pclib.geo.equation.implicit;

/** a*x+b*y+c=0. */
public record LinearEquation2d(double a, double b, double c) implements ImplicitEquation2dc {
	@Override
	public double value(final double x, final double y) {
		return this.a * x + this.b * y + this.c;
	}

	public double[] coefficients() {
		return new double[] { this.a, this.b };
	}
}
