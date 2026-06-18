package lu.kbra.pclib.impl.predicate;

@FunctionalInterface
public interface SextPredicate<A, B, C, D, E, F> {

	boolean test(A a, B b, C c, D d, E e, F f);

	default SextPredicate<A, B, C, D, E, F> and(final SextPredicate<? super A, ? super B, ? super C, ? super D, ? super E, ? super F> other) {
		return (a, b, c, d, e, f) -> this.test(a, b, c, d, e, f) && other.test(a, b, c, d, e, f);
	}

	default SextPredicate<A, B, C, D, E, F> negate() {
		return (a, b, c, d, e, f) -> !this.test(a, b, c, d, e, f);
	}

	default SextPredicate<A, B, C, D, E, F> or(final SextPredicate<? super A, ? super B, ? super C, ? super D, ? super E, ? super F> other) {
		return (a, b, c, d, e, f) -> this.test(a, b, c, d, e, f) || other.test(a, b, c, d, e, f);
	}

}
