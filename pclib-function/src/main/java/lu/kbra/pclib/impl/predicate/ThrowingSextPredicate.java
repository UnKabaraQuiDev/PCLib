package lu.kbra.pclib.impl.predicate;

@FunctionalInterface
public interface ThrowingSextPredicate<A, B, C, D, E, F, T extends Throwable> {

	default SextPredicate<A, B, C, D, E, F> asRuntime() throws RuntimeException {
		return (a, b, c, d, e, f) -> {
			try {
				return this.test(a, b, c, d, e, f);
			} catch (final RuntimeException re) {
				throw re;
			} catch (final Throwable t) {
				throw new RuntimeException(t);
			}
		};
	}

	boolean test(A a, B b, C c, D d, E e, F f) throws T;

}
