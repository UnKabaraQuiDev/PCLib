package lu.kbra.pclib.impl.runnable;

@FunctionalInterface
public interface ThrowingRunnable<T extends Throwable> {

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

	void run() throws T;

}
