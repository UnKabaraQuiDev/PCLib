package lu.pcy113.pclib.impl;

@FunctionalInterface
public interface ExceptionFunction<T, R> {

	R apply(T t) throws Exception;

}
