package lu.pcy113.pclib.impl;

@FunctionalInterface
public interface ExceptionRunnable<T> {

	void run() throws Exception;

}
