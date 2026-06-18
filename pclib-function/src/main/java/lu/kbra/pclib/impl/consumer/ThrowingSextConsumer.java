package lu.kbra.pclib.impl.consumer;

@FunctionalInterface
public interface ThrowingSextConsumer<A, B, C, D, E, F, T extends Throwable> {

	void accept(A a, B b, C c, D d, E e, F f) throws T;

	default SextConsumer<A, B, C, D, E, F> asRuntime() throws RuntimeException {
		return (a, b, c, d, e, f) -> {
			try {
				this.accept(a, b, c, d, e, f);
			} catch (final RuntimeException re) {
				throw re;
			} catch (final Throwable t) {
				throw new RuntimeException(t);
			}
		};
	}

}
