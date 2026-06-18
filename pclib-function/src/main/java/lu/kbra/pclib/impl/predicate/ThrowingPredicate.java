package lu.kbra.pclib.impl.predicate;

import java.util.function.Predicate;

@FunctionalInterface
public interface ThrowingPredicate<A, T extends Throwable> {

	boolean test(A a) throws T;

	default Predicate<A> asRuntime() throws RuntimeException {
		return a -> {
			try {
				return this.test(a);
			} catch (final RuntimeException re) {
				throw re;
			} catch (final Throwable t) {
				throw new RuntimeException(t);
			}
		};
	}

}
