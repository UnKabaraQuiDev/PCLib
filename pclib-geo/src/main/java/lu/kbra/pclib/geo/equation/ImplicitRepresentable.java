package lu.kbra.pclib.geo.equation;

import java.util.List;

/** Capability: defines a locus by implicit equalities plus optional restrictions. */
public interface ImplicitRepresentable<E, C> {
	E implicitEquation();

	default List<C> constraints() {
		return List.of();
	}
}
