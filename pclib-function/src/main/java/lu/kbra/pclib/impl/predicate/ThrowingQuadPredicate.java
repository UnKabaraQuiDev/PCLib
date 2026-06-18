package lu.kbra.pclib.impl.predicate;

@FunctionalInterface
public interface ThrowingQuadPredicate<A, B, C, D, T extends Throwable> {

	boolean test(A a, B b, C c, D d) throws T;

	default QuadPredicate<A, B, C, D> asRuntime() throws RuntimeException {
		return (a, b, c, d) -> {
			try {
				return this.test(a, b, c, d);
			} catch (final RuntimeException re) {
				throw re;
			} catch (final Throwable t) {
				throw new RuntimeException(t);
			}
		};
	}

}
