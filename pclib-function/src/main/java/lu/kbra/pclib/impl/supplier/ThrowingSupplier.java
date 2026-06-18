package lu.kbra.pclib.impl.supplier;

import java.util.function.Supplier;

@FunctionalInterface
public interface ThrowingSupplier<R, T extends Throwable> {

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

	R get() throws T;

}
