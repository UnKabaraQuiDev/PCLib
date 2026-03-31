package lu.kbra.pclib.impl;

@FunctionalInterface
public interface ThrowingTriConsumer<A, B, C, T extends Throwable> {

	void accept(A a, B b, C c) throws T;

	default TriConsumer<A, B, C> asRuntime() throws RuntimeException {
		return (a, b, c) -> {
			try {
				this.accept(a, b, c);
			} catch (final RuntimeException re) {
				throw re;
			} catch (final Throwable t) {
				throw new RuntimeException(t);
			}
		};
	}

}
