package lu.kbra.pclib.impl.function;

import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
public interface ThrowingSextFunction<A, B, C, D, E, F, R, T extends Throwable> {

	default <V> ThrowingSextFunction<A, B, C, D, E, F, V, T> andThen(final Function<? super R, ? extends V> after) {
		Objects.requireNonNull(after);
		return (final A a, final B b, final C c, final D d, final E e, final F f) -> after.apply(this.apply(a, b, c, d, e, f));
	}

	R apply(A a, B b, C c, D d, E e, F f) throws T;

	default SextFunction<A, B, C, D, E, F, R> asRuntime() throws RuntimeException {
		return (a, b, c, d, e, f) -> {
			try {
				return this.apply(a, b, c, d, e, f);
			} catch (final RuntimeException re) {
				throw re;
			} catch (final Throwable t) {
				throw new RuntimeException(t);
			}
		};
	}

}
