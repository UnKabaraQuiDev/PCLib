package lu.kbra.pclib.impl.function;

import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
public interface QuintFunction<A, B, C, D, E, R> {

	default <V> QuintFunction<A, B, C, D, E, V> andThen(final Function<? super R, ? extends V> after) {
		Objects.requireNonNull(after);
		return (final A a, final B b, final C c, final D d, final E e) -> after.apply(this.apply(a, b, c, d, e));
	}

	R apply(A a, B b, C c, D d, E e);

}
