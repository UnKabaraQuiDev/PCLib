package lu.pcy113.pclib.impl;

@FunctionalInterface
public interface ExceptionTriFunction<A, B, C, R> {

	R apply(A a, B b, C c) throws Throwable;

}
