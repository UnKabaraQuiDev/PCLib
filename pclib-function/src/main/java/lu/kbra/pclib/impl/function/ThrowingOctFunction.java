package lu.kbra.pclib.impl.function;

import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
public interface ThrowingOctFunction<A, B, C, D, E, F, G, H, R, T extends Throwable> {

	default <V> ThrowingOctFunction<A, B, C, D, E, F, G, H, V, T> andThen(final Function<? super R, ? extends V> after) {
		Objects.requireNonNull(after);
		return (final A a, final B b, final C c, final D d, final E e, final F f, final G g, final H h) -> after.apply(this.apply(a, b, c, d, e, f, g, h));
	}

	R apply(A a, B b, C c, D d, E e, F f, G g, H h) throws T;

	default OctFunction<A, B, C, D, E, F, G, H, R> asRuntime() throws RuntimeException {
		return (a, b, c, d, e, f, g, h) -> {
			try {
				return this.apply(a, b, c, d, e, f, g, h);
			} catch (final RuntimeException re) {
				throw re;
			} catch (final Throwable t) {
				throw new RuntimeException(t);
			}
		};
	}

}
