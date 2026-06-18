package lu.kbra.pclib.impl.predicate;

@FunctionalInterface
public interface SeptPredicate<A, B, C, D, E, F, G> {

	boolean test(A a, B b, C c, D d, E e, F f, G g);

	default SeptPredicate<A, B, C, D, E, F, G> and(final SeptPredicate<? super A, ? super B, ? super C, ? super D, ? super E, ? super F, ? super G> other) {
		return (a, b, c, d, e, f, g) -> this.test(a, b, c, d, e, f, g) && other.test(a, b, c, d, e, f, g);
	}

	default SeptPredicate<A, B, C, D, E, F, G> negate() {
		return (a, b, c, d, e, f, g) -> !this.test(a, b, c, d, e, f, g);
	}

	default SeptPredicate<A, B, C, D, E, F, G> or(final SeptPredicate<? super A, ? super B, ? super C, ? super D, ? super E, ? super F, ? super G> other) {
		return (a, b, c, d, e, f, g) -> this.test(a, b, c, d, e, f, g) || other.test(a, b, c, d, e, f, g);
	}

}
