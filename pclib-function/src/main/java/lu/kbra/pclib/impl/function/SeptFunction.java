package lu.kbra.pclib.impl.function;

import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
public interface SeptFunction<A, B, C, D, E, F, G, R> {

	default <V> SeptFunction<A, B, C, D, E, F, G, V> andThen(final Function<? super R, ? extends V> after) {
		Objects.requireNonNull(after);
		return (final A a, final B b, final C c, final D d, final E e, final F f, final G g) -> after.apply(this.apply(a, b, c, d, e, f, g));
	}

	R apply(A a, B b, C c, D d, E e, F f, G g);

}
