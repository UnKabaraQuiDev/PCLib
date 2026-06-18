package lu.kbra.pclib.impl.predicate;

import java.util.function.BiPredicate;

@FunctionalInterface
public interface ThrowingBiPredicate<A, B, T extends Throwable> {

	default BiPredicate<A, B> asRuntime() throws RuntimeException {
		return (a, b) -> {
			try {
				return this.test(a, b);
			} catch (final RuntimeException re) {
				throw re;
			} catch (final Throwable t) {
				throw new RuntimeException(t);
			}
		};
	}

	boolean test(A a, B b) throws T;

}
