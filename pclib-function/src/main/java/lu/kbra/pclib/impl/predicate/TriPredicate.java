package lu.kbra.pclib.impl.predicate;

@FunctionalInterface
public interface TriPredicate<A, B, C> {

	default TriPredicate<A, B, C> and(final TriPredicate<? super A, ? super B, ? super C> other) {
		return (a, b, c) -> this.test(a, b, c) && other.test(a, b, c);
	}

	default TriPredicate<A, B, C> negate() {
		return (a, b, c) -> !this.test(a, b, c);
	}

	default TriPredicate<A, B, C> or(final TriPredicate<? super A, ? super B, ? super C> other) {
		return (a, b, c) -> this.test(a, b, c) || other.test(a, b, c);
	}

	boolean test(A a, B b, C c);

}
