package lu.kbra.pclib.impl;

@FunctionalInterface
public interface ThrowingRunnable<T extends Throwable> {

	void run() throws T;

	default Runnable asRuntime() throws RuntimeException {
		return () -> {
			try {
				run();
			} catch (RuntimeException re) {
				throw re;
			} catch (Throwable t) {
				throw new RuntimeException(t);
			}
		};
	}

}
