package lu.kbra.pclib.impl;

import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
public interface ThrowingBiFunction<A, B, R, T extends Throwable> {

	R apply(A a, B b) throws T;

	default <V> ThrowingBiFunction<A, B, V, T> andThen(Function<? super R, ? extends V> after) {
		Objects.requireNonNull(after);
		return (A a, B b) -> after.apply(apply(a, b));
	}

}
