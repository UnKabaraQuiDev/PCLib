package lu.kbra.pclib.impl.function;

import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
public interface ThrowingSeptFunction<A, B, C, D, E, F, G, R, T extends Throwable> {

	default <V> ThrowingSeptFunction<A, B, C, D, E, F, G, V, T> andThen(final Function<? super R, ? extends V> after) {
		Objects.requireNonNull(after);
		return (final A a, final B b, final C c, final D d, final E e, final F f, final G g) -> after.apply(this.apply(a, b, c, d, e, f, g));
	}

	R apply(A a, B b, C c, D d, E e, F f, G g) throws T;

	default SeptFunction<A, B, C, D, E, F, G, R> asRuntime() throws RuntimeException {
		return (a, b, c, d, e, f, g) -> {
			try {
				return this.apply(a, b, c, d, e, f, g);
			} catch (final RuntimeException re) {
				throw re;
			} catch (final Throwable t) {
				throw new RuntimeException(t);
			}
		};
	}

}
