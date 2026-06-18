package lu.kbra.pclib.impl.function;

import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
public interface ThrowingQuadFunction<A, B, C, D, R, T extends Throwable> {

	default <V> ThrowingQuadFunction<A, B, C, D, V, T> andThen(final Function<? super R, ? extends V> after) {
		Objects.requireNonNull(after);
		return (final A a, final B b, final C c, final D d) -> after.apply(this.apply(a, b, c, d));
	}

	R apply(A a, B b, C c, D d) throws T;

	default QuadFunction<A, B, C, D, R> asRuntime() throws RuntimeException {
		return (a, b, c, d) -> {
			try {
				return this.apply(a, b, c, d);
			} catch (final RuntimeException re) {
				throw re;
			} catch (final Throwable t) {
				throw new RuntimeException(t);
			}
		};
	}

}
