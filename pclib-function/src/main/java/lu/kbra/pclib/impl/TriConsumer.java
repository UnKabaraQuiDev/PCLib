package lu.kbra.pclib.impl;

@FunctionalInterface
public interface TriConsumer<A, B, C> {

	void accept(A a, B b, C c);

}
