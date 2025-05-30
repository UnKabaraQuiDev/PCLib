package lu.pcy113.pclib.impl;

@FunctionalInterface
public interface TriFunction<A, B, C, R> {

	R apply(A a, B b, C c);
	
}
