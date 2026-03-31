package lu.kbra.pclib.impl;

import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
public interface ThrowingTriFunction<A, B, C, R, T extends Throwable> {

	R apply(A a, B b, C c) throws T;

	default <V> ThrowingTriFunction<A, B, C, V, T> andThen(final Function<? super R, ? extends V> after) {
		Objects.requireNonNull(after);
		return (final A a, final B b, final C c) -> after.apply(this.apply(a, b, c));
	}

	default TriFunction<A, B, C, R> asRuntime() throws RuntimeException {
		return (a, b, c) -> {
			try {
				return this.apply(a, b, c);
			} catch (final RuntimeException re) {
				throw re;
			} catch (final Throwable t) {
				throw new RuntimeException(t);
			}
		};
	}

}
