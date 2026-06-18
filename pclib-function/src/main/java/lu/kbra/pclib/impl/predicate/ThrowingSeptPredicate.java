package lu.kbra.pclib.impl.predicate;

@FunctionalInterface
public interface ThrowingSeptPredicate<A, B, C, D, E, F, G, T extends Throwable> {

	boolean test(A a, B b, C c, D d, E e, F f, G g) throws T;

	default SeptPredicate<A, B, C, D, E, F, G> asRuntime() throws RuntimeException {
		return (a, b, c, d, e, f, g) -> {
			try {
				return this.test(a, b, c, d, e, f, g);
			} catch (final RuntimeException re) {
				throw re;
			} catch (final Throwable t) {
				throw new RuntimeException(t);
			}
		};
	}

}
