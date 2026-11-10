package lu.kbra.pclib.geo.equation.solver;

import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem3dc;

/** A proven implicit solution description, not a reconstructed geometry class. */
public record ImplicitSolution(ImplicitSystem3dc equations) implements MathSolution {
}
