package lu.kbra.pclib.impl.consumer;

@FunctionalInterface
public interface ThrowingSeptConsumer<A, B, C, D, E, F, G, T extends Throwable> {

	void accept(A a, B b, C c, D d, E e, F f, G g) throws T;

	default SeptConsumer<A, B, C, D, E, F, G> asRuntime() throws RuntimeException {
		return (a, b, c, d, e, f, g) -> {
			try {
				this.accept(a, b, c, d, e, f, g);
			} catch (final RuntimeException re) {
				throw re;
			} catch (final Throwable t) {
				throw new RuntimeException(t);
			}
		};
	}

}
