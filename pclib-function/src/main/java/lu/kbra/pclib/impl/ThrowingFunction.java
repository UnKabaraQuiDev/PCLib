package lu.kbra.pclib.impl;

import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
public interface ThrowingFunction<I, R, T extends Throwable> {

	R apply(I t) throws T;

	default <V> ThrowingFunction<I, V, T> andThen(Function<? super R, ? extends V> after) {
		Objects.requireNonNull(after);
		return (I a) -> after.apply(apply(a));
	}
	
	default Function<I, R> asRuntime() throws RuntimeException {
		return (input) -> {
			try {
				return apply(input);
			} catch (RuntimeException re) {
				throw re;
			} catch (Throwable t) {
				throw new RuntimeException(t);
			}
		};
	}

}
