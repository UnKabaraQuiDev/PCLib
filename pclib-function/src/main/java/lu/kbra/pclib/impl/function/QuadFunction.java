package lu.kbra.pclib.impl.function;

import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
public interface QuadFunction<A, B, C, D, R> {

	default <V> QuadFunction<A, B, C, D, V> andThen(final Function<? super R, ? extends V> after) {
		Objects.requireNonNull(after);
		return (final A a, final B b, final C c, final D d) -> after.apply(this.apply(a, b, c, d));
	}

	R apply(A a, B b, C c, D d);

}
