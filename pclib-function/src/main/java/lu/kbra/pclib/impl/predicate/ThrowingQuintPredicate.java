package lu.kbra.pclib.impl.predicate;

@FunctionalInterface
public interface ThrowingQuintPredicate<A, B, C, D, E, T extends Throwable> {

	default QuintPredicate<A, B, C, D, E> asRuntime() throws RuntimeException {
		return (a, b, c, d, e) -> {
			try {
				return this.test(a, b, c, d, e);
			} catch (final RuntimeException re) {
				throw re;
			} catch (final Throwable t) {
				throw new RuntimeException(t);
			}
		};
	}

	boolean test(A a, B b, C c, D d, E e) throws T;

}
