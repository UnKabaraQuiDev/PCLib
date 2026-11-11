package lu.kbra.pclib.geo.equation.implicit;

import java.util.List;

public record ImplicitSystem3fc(List<ImplicitEquation3fc> equations) {
	public ImplicitSystem3fc {
		equations = List.copyOf(equations);
	}

	public ImplicitSystem3fc(final ImplicitEquation3fc... equations) {
		this(List.of(equations));
	}

	public ImplicitSystem3dc asDouble() {
		return new ImplicitSystem3dc(equations.stream().map(ImplicitEquation3fc::asDouble).toList());
	}
}
