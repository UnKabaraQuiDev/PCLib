package lu.kbra.pclib.geo.equation.implicit;

import java.util.List;

public record ImplicitSystem2dc(List<ImplicitEquation2dc> equations) {
	public ImplicitSystem2dc {
		equations = List.copyOf(equations);
	}

	public ImplicitSystem2dc(final ImplicitEquation2dc... equations) {
		this(List.of(equations));
	}
}
