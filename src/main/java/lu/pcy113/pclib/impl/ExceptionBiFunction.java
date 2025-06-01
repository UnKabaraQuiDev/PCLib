package lu.pcy113.pclib.impl;

@FunctionalInterface
public interface ExceptionBiFunction<A, B, R> {

	R apply(A a, B b) throws Throwable;

}
