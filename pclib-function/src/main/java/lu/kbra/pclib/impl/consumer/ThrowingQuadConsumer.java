package lu.kbra.pclib.impl.consumer;

@FunctionalInterface
public interface ThrowingQuadConsumer<A, B, C, D, T extends Throwable> {

	void accept(A a, B b, C c, D d) throws T;

	default QuadConsumer<A, B, C, D> asRuntime() throws RuntimeException {
		return (a, b, c, d) -> {
			try {
				this.accept(a, b, c, d);
			} catch (final RuntimeException re) {
				throw re;
			} catch (final Throwable t) {
				throw new RuntimeException(t);
			}
		};
	}

}
