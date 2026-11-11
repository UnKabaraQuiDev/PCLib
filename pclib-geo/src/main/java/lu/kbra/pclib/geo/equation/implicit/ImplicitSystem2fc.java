package lu.kbra.pclib.geo.equation.implicit;

import java.util.List;

public record ImplicitSystem2fc(List<ImplicitEquation2fc> equations) {
	public ImplicitSystem2fc {
		equations = List.copyOf(equations);
	}

	public ImplicitSystem2fc(final ImplicitEquation2fc... equations) {
		this(List.of(equations));
	}

	public ImplicitSystem2dc asDouble() {
		return new ImplicitSystem2dc(equations.stream().map(ImplicitEquation2fc::asDouble).toList());
	}
}
