package lu.kbra.pclib.impl.function;

import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
public interface ThrowingQuintFunction<A, B, C, D, E, R, T extends Throwable> {

	default <V> ThrowingQuintFunction<A, B, C, D, E, V, T> andThen(final Function<? super R, ? extends V> after) {
		Objects.requireNonNull(after);
		return (final A a, final B b, final C c, final D d, final E e) -> after.apply(this.apply(a, b, c, d, e));
	}

	R apply(A a, B b, C c, D d, E e) throws T;

	default QuintFunction<A, B, C, D, E, R> asRuntime() throws RuntimeException {
		return (a, b, c, d, e) -> {
			try {
				return this.apply(a, b, c, d, e);
			} catch (final RuntimeException re) {
				throw re;
			} catch (final Throwable t) {
				throw new RuntimeException(t);
			}
		};
	}

}
