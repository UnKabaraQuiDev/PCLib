package lu.kbra.pclib.impl.predicate;

@FunctionalInterface
public interface QuadPredicate<A, B, C, D> {

	default QuadPredicate<A, B, C, D> and(final QuadPredicate<? super A, ? super B, ? super C, ? super D> other) {
		return (a, b, c, d) -> this.test(a, b, c, d) && other.test(a, b, c, d);
	}

	default QuadPredicate<A, B, C, D> negate() {
		return (a, b, c, d) -> !this.test(a, b, c, d);
	}

	default QuadPredicate<A, B, C, D> or(final QuadPredicate<? super A, ? super B, ? super C, ? super D> other) {
		return (a, b, c, d) -> this.test(a, b, c, d) || other.test(a, b, c, d);
	}

	boolean test(A a, B b, C c, D d);

}
