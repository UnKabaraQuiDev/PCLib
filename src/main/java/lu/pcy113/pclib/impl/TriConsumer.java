package lu.pcy113.pclib.impl;

@FunctionalInterface
public interface TriConsumer<A, B, C> {

	void accept(A a, B b, C c);

}
