package lu.kbra.pclib.impl;

import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
public interface ThrowingTriFunction<A, B, C, R, T extends Throwable> {

	R apply(A a, B b, C c) throws T;

	default <V> ThrowingTriFunction<A, B, C, V, T> andThen(Function<? super R, ? extends V> after) {
		Objects.requireNonNull(after);
		return (A a, B b, C c) -> after.apply(apply(a, b, c));
	}

	default TriFunction<A, B, C, R> asRuntime() throws RuntimeException {
		return (a, b, c) -> {
			try {
				return apply(a, b, c);
			} catch (RuntimeException re) {
				throw re;
			} catch (Throwable t) {
				throw new RuntimeException(t);
			}
		};
	}

}
