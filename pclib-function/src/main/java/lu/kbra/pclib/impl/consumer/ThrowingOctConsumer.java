package lu.kbra.pclib.impl.consumer;

@FunctionalInterface
public interface ThrowingOctConsumer<A, B, C, D, E, F, G, H, T extends Throwable> {

	void accept(A a, B b, C c, D d, E e, F f, G g, H h) throws T;

	default OctConsumer<A, B, C, D, E, F, G, H> asRuntime() throws RuntimeException {
		return (a, b, c, d, e, f, g, h) -> {
			try {
				this.accept(a, b, c, d, e, f, g, h);
			} catch (final RuntimeException re) {
				throw re;
			} catch (final Throwable t) {
				throw new RuntimeException(t);
			}
		};
	}

}
