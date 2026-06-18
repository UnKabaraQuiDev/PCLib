package lu.kbra.pclib.impl.consumer;

@FunctionalInterface
public interface SextConsumer<A, B, C, D, E, F> {

	void accept(A a, B b, C c, D d, E e, F f);

}
