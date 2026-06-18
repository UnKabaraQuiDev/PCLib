package lu.kbra.pclib.impl.function;

import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
public interface OctFunction<A, B, C, D, E, F, G, H, R> {

	default <V> OctFunction<A, B, C, D, E, F, G, H, V> andThen(final Function<? super R, ? extends V> after) {
		Objects.requireNonNull(after);
		return (final A a, final B b, final C c, final D d, final E e, final F f, final G g, final H h) -> after
				.apply(this.apply(a, b, c, d, e, f, g, h));
	}

	R apply(A a, B b, C c, D d, E e, F f, G g, H h);

}
