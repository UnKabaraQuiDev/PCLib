package lu.pcy113.pclib.impl;

@FunctionalInterface
public interface ExceptionConsumer<T> {

	void accept(T input) throws Throwable;

}
