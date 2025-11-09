package lu.pcy113.pclib.impl;

@FunctionalInterface
public interface ThrowingConsumer<I, T extends Throwable> {

	void accept(I input) throws T;

}
