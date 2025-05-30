package lu.pcy113.pclib.impl;

@FunctionalInterface
public interface ExceptionSupplier<T> {

	T get() throws Exception;

}
