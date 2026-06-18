package lu.kbra.pclib.impl.predicate;

@FunctionalInterface
public interface ThrowingTriPredicate<A, B, C, T extends Throwable> {

	default TriPredicate<A, B, C> asRuntime() throws RuntimeException {
		return (a, b, c) -> {
			try {
				return this.test(a, b, c);
			} catch (final RuntimeException re) {
				throw re;
			} catch (final Throwable t) {
				throw new RuntimeException(t);
			}
		};
	}

	boolean test(A a, B b, C c) throws T;

}
