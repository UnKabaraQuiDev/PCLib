package lu.kbra.pclib.impl;

import java.util.function.Consumer;

@FunctionalInterface
public interface ThrowingConsumer<I, T extends Throwable> {

	void accept(I input) throws T;

	default Consumer<I> asRuntime() throws RuntimeException {
		return input -> {
			try {
				this.accept(input);
			} catch (final RuntimeException re) {
				throw re;
			} catch (final Throwable t) {
				throw new RuntimeException(t);
			}
		};
	}

}
