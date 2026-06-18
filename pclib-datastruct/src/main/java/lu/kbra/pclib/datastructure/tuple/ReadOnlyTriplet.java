package lu.kbra.pclib.datastructure.tuple;

import lu.kbra.pclib.impl.function.TriFunction;

public class ReadOnlyTriplet<A, B, C> extends Triplet<A, B, C> {

	public ReadOnlyTriplet() {
	}

	public ReadOnlyTriplet(final A first, final B second, final C third) {
		super(first, second, third);
	}

	@Override
	public ReadOnlyTriplet<A, B, C> clone() {
		return new ReadOnlyTriplet<>(first, second, third);
	}

	public <T> T map(final TriFunction<A, B, C, T> func) {
		return func.apply(this.first, this.second, this.third);
	}

	public <T> Triplet<T, B, C> mapFirst(final TriFunction<A, B, C, T> func) {
		return map((a, b, c) -> new ReadOnlyTriplet<>(func.apply(a, b, c), b, c));
	}

	public <T> Triplet<A, T, C> mapSecond(final TriFunction<A, B, C, T> func) {
		return map((a, b, c) -> new ReadOnlyTriplet<>(a, func.apply(a, b, c), c));
	}

	public <T> Triplet<A, B, T> mapThird(final TriFunction<A, B, C, T> func) {
		return map((a, b, c) -> new ReadOnlyTriplet<>(a, b, func.apply(a, b, c)));
	}

	public <T, U, V> Triplet<T, U, V> map(
			final TriFunction<A, B, C, T> funcFirst,
			final TriFunction<A, B, C, U> funcSecond,
			final TriFunction<A, B, C, V> funcThird) {

		return map((a, b, c) -> new ReadOnlyTriplet<>(funcFirst.apply(a, b, c), funcSecond.apply(a, b, c), funcThird.apply(a, b, c)));
	}

	@Override
	@Deprecated
	public ReadOnlyTriplet<A, B, C> setFirst(final A first) {
		throw new UnsupportedOperationException("Operation not permitted on readonly triplet !");
	}

	@Override
	@Deprecated
	public ReadOnlyTriplet<A, B, C> setSecond(final B second) {
		throw new UnsupportedOperationException("Operation not permitted on readonly triplet !");
	}

	@Override
	@Deprecated
	public ReadOnlyTriplet<A, B, C> setThird(final C third) {
		throw new UnsupportedOperationException("Operation not permitted on readonly triplet !");
	}

	@Override
	public String toString() {
		return String.format("%s(readonly)", super.toString());
	}

}
