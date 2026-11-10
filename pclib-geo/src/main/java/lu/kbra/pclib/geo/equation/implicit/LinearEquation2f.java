package lu.kbra.pclib.geo.equation.implicit;
/** a*x + b*y + c = 0. */
public record LinearEquation2f(float a, float b, float c) implements ImplicitEquation2fc {
    @Override public float value(final float x, final float y) { return a*x + b*y + c; }
    public float[] coefficients() { return new float[]{a,b}; }
    @Override public LinearEquation2d asDouble() { return new LinearEquation2d(a,b,c); }
}
