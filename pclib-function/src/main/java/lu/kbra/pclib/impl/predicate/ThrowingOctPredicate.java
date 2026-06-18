package lu.kbra.pclib.impl.predicate;

@FunctionalInterface
public interface ThrowingOctPredicate<A, B, C, D, E, F, G, H, T extends Throwable> {

	boolean test(A a, B b, C c, D d, E e, F f, G g, H h) throws T;

	default OctPredicate<A, B, C, D, E, F, G, H> asRuntime() throws RuntimeException {
		return (a, b, c, d, e, f, g, h) -> {
			try {
				return this.test(a, b, c, d, e, f, g, h);
			} catch (final RuntimeException re) {
				throw re;
			} catch (final Throwable t) {
				throw new RuntimeException(t);
			}
		};
	}

}
