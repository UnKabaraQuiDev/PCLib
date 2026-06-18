package lu.kbra.pclib.impl.consumer;

import java.util.function.BiConsumer;

@FunctionalInterface
public interface ThrowingBiConsumer<A, B, T extends Throwable> {

	void accept(A a, B b) throws T;

	default BiConsumer<A, B> asRuntime() throws RuntimeException {
		return (a, b) -> {
			try {
				this.accept(a, b);
			} catch (final RuntimeException re) {
				throw re;
			} catch (final Throwable t) {
				throw new RuntimeException(t);
			}
		};
	}

}
