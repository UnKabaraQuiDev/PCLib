package lu.kbra.pclib.impl;

@FunctionalInterface
public interface ThrowingRunnable<T extends Throwable> {

	void run() throws T;

}
