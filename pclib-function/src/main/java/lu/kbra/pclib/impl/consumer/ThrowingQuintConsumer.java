package lu.kbra.pclib.impl.consumer;

@FunctionalInterface
public interface ThrowingQuintConsumer<A, B, C, D, E, T extends Throwable> {

	void accept(A a, B b, C c, D d, E e) throws T;

	default QuintConsumer<A, B, C, D, E> asRuntime() throws RuntimeException {
		return (a, b, c, d, e) -> {
			try {
				this.accept(a, b, c, d, e);
			} catch (final RuntimeException re) {
				throw re;
			} catch (final Throwable t) {
				throw new RuntimeException(t);
			}
		};
	}

}
