package lu.kbra.pclib.impl;

import java.util.function.Supplier;

@FunctionalInterface
public interface ThrowingSupplier<R, T extends Throwable> {

	R get() throws T;

	default Supplier<R> asRuntime() throws RuntimeException {
		return () -> {
			try {
				return this.get();
			} catch (final RuntimeException re) {
				throw re;
			} catch (final Throwable t) {
				throw new RuntimeException(t);
			}
		};
	}

}
