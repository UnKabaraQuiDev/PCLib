package lu.kbra.pclib.impl.predicate;

@FunctionalInterface
public interface OctPredicate<A, B, C, D, E, F, G, H> {

	boolean test(A a, B b, C c, D d, E e, F f, G g, H h);

	default OctPredicate<A, B, C, D, E, F, G, H> and(final OctPredicate<? super A, ? super B, ? super C, ? super D, ? super E, ? super F, ? super G, ? super H> other) {
		return (a, b, c, d, e, f, g, h) -> this.test(a, b, c, d, e, f, g, h) && other.test(a, b, c, d, e, f, g, h);
	}

	default OctPredicate<A, B, C, D, E, F, G, H> negate() {
		return (a, b, c, d, e, f, g, h) -> !this.test(a, b, c, d, e, f, g, h);
	}

	default OctPredicate<A, B, C, D, E, F, G, H> or(final OctPredicate<? super A, ? super B, ? super C, ? super D, ? super E, ? super F, ? super G, ? super H> other) {
		return (a, b, c, d, e, f, g, h) -> this.test(a, b, c, d, e, f, g, h) || other.test(a, b, c, d, e, f, g, h);
	}

}
