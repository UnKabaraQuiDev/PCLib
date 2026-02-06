package lu.kbra.pclib.impl;

@FunctionalInterface
public interface ThrowingTriConsumer<A, B, C, T extends Throwable> {

	void accept(A a, B b, C c) throws T;

}
