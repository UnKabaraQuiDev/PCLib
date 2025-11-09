package lu.pcy113.pclib.impl;

@FunctionalInterface
public interface ThrowingRunnable<T extends Throwable> {

	void run() throws T;

}
