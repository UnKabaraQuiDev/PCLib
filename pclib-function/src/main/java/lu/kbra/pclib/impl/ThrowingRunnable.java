package lu.kbra.pclib.impl;

@FunctionalInterface
public interface ThrowingRunnable<T extends Throwable> {

	void run() throws T;

	default Runnable asRuntime() throws RuntimeException {
		return () -> {
			try {
				this.run();
			} catch (final RuntimeException re) {
				throw re;
			} catch (final Throwable t) {
				throw new RuntimeException(t);
			}
		};
	}

}
