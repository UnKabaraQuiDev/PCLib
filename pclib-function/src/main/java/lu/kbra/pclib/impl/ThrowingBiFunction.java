package lu.kbra.pclib.impl;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

@FunctionalInterface
public interface ThrowingBiFunction<A, B, R, T extends Throwable> {

	R apply(A a, B b) throws T;

	default <V> ThrowingBiFunction<A, B, V, T> andThen(final Function<? super R, ? extends V> after) {
		Objects.requireNonNull(after);
		return (final A a, final B b) -> after.apply(this.apply(a, b));
	}

	default BiFunction<A, B, R> asRuntime() throws RuntimeException {
		return (a, b) -> {
			try {
				return this.apply(a, b);
			} catch (final RuntimeException re) {
				throw re;
			} catch (final Throwable t) {
				throw new RuntimeException(t);
			}
		};
	}

}
