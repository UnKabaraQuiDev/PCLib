package lu.kbra.pclib.geo.equation.implicit;

import java.util.List;

/** Conjunction of zero-equalities in world coordinates. */
public record ImplicitSystem3dc(List<ImplicitEquation3dc> equations) {
	public ImplicitSystem3dc {
		equations = List.copyOf(equations);
	}

	public ImplicitSystem3dc(final ImplicitEquation3dc... equations) {
		this(List.of(equations));
	}
}
