package lu.kbra.pclib.impl.consumer;

@FunctionalInterface
public interface TriConsumer<A, B, C> {

	void accept(A a, B b, C c);

}
