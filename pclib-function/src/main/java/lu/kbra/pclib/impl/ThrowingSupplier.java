package lu.kbra.pclib.impl;

import java.util.function.Supplier;

@FunctionalInterface
public interface ThrowingSupplier<R, T extends Throwable> {

	R get() throws T;
	
	default Supplier<R> asRuntime() throws RuntimeException {
		return () -> {
			try {
				return get();
			} catch (RuntimeException re) {
				throw re;
			} catch (Throwable t) {
				throw new RuntimeException(t);
			}
		};
	}

}
