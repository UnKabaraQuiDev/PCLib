package lu.kbra.pclib.impl.predicate;

@FunctionalInterface
public interface QuintPredicate<A, B, C, D, E> {

	boolean test(A a, B b, C c, D d, E e);

	default QuintPredicate<A, B, C, D, E> and(final QuintPredicate<? super A, ? super B, ? super C, ? super D, ? super E> other) {
		return (a, b, c, d, e) -> this.test(a, b, c, d, e) && other.test(a, b, c, d, e);
	}

	default QuintPredicate<A, B, C, D, E> negate() {
		return (a, b, c, d, e) -> !this.test(a, b, c, d, e);
	}

	default QuintPredicate<A, B, C, D, E> or(final QuintPredicate<? super A, ? super B, ? super C, ? super D, ? super E> other) {
		return (a, b, c, d, e) -> this.test(a, b, c, d, e) || other.test(a, b, c, d, e);
	}

}
