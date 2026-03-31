package lu.kbra.pclib.impl;

import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
public interface ThrowingFunction<I, R, T extends Throwable> {

	R apply(I t) throws T;

	default <V> ThrowingFunction<I, V, T> andThen(final Function<? super R, ? extends V> after) {
		Objects.requireNonNull(after);
		return (final I a) -> after.apply(this.apply(a));
	}

	default Function<I, R> asRuntime() throws RuntimeException {
		return input -> {
			try {
				return this.apply(input);
			} catch (final RuntimeException re) {
				throw re;
			} catch (final Throwable t) {
				throw new RuntimeException(t);
			}
		};
	}

}
