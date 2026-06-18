package lu.kbra.pclib.datastructure.tuple;

import lu.kbra.pclib.impl.function.QuintFunction;

public class ReadOnlyQuintuple<A, B, C, D, E> extends Quintuple<A, B, C, D, E> {

	public ReadOnlyQuintuple() {
	}

	public ReadOnlyQuintuple(final A first, final B second, final C third, final D fourth, final E fifth) {
		super(first, second, third, fourth, fifth);
	}

	@Override
	public ReadOnlyQuintuple<A, B, C, D, E> clone() {
		return new ReadOnlyQuintuple<>(first, second, third, fourth, fifth);
	}

	public <T> T map(final QuintFunction<A, B, C, D, E, T> func) {
		return func.apply(this.first, this.second, this.third, this.fourth, this.fifth);
	}

	public <T> Quintuple<T, B, C, D, E> mapFirst(final QuintFunction<A, B, C, D, E, T> func) {
		return map((a, b, c, d, e) -> new ReadOnlyQuintuple<>(func.apply(a, b, c, d, e), b, c, d, e));
	}

	public <T> Quintuple<A, T, C, D, E> mapSecond(final QuintFunction<A, B, C, D, E, T> func) {
		return map((a, b, c, d, e) -> new ReadOnlyQuintuple<>(a, func.apply(a, b, c, d, e), c, d, e));
	}

	public <T> Quintuple<A, B, T, D, E> mapThird(final QuintFunction<A, B, C, D, E, T> func) {
		return map((a, b, c, d, e) -> new ReadOnlyQuintuple<>(a, b, func.apply(a, b, c, d, e), d, e));
	}

	public <T> Quintuple<A, B, C, T, E> mapFourth(final QuintFunction<A, B, C, D, E, T> func) {
		return map((a, b, c, d, e) -> new ReadOnlyQuintuple<>(a, b, c, func.apply(a, b, c, d, e), e));
	}

	public <T> Quintuple<A, B, C, D, T> mapFifth(final QuintFunction<A, B, C, D, E, T> func) {
		return map((a, b, c, d, e) -> new ReadOnlyQuintuple<>(a, b, c, d, func.apply(a, b, c, d, e)));
	}

	public <R1, R2, R3, R4, R5> Quintuple<R1, R2, R3, R4, R5> map(
			final QuintFunction<A, B, C, D, E, R1> funcFirst,
			final QuintFunction<A, B, C, D, E, R2> funcSecond,
			final QuintFunction<A, B, C, D, E, R3> funcThird,
			final QuintFunction<A, B, C, D, E, R4> funcFourth,
			final QuintFunction<A, B, C, D, E, R5> funcFifth) {

		return map((a, b, c, d, e) -> new ReadOnlyQuintuple<>(funcFirst.apply(a, b, c, d, e),
				funcSecond.apply(a, b, c, d, e),
				funcThird.apply(a, b, c, d, e),
				funcFourth.apply(a, b, c, d, e),
				funcFifth.apply(a, b, c, d, e)));
	}

	@Override
	@Deprecated
	public ReadOnlyQuintuple<A, B, C, D, E> setFirst(final A first) {
		throw new UnsupportedOperationException("Operation not permitted on readonly quintuple !");
	}

	@Override
	@Deprecated
	public ReadOnlyQuintuple<A, B, C, D, E> setSecond(final B second) {
		throw new UnsupportedOperationException("Operation not permitted on readonly quintuple !");
	}

	@Override
	@Deprecated
	public ReadOnlyQuintuple<A, B, C, D, E> setThird(final C third) {
		throw new UnsupportedOperationException("Operation not permitted on readonly quintuple !");
	}

	@Override
	@Deprecated
	public ReadOnlyQuintuple<A, B, C, D, E> setFourth(final D fourth) {
		throw new UnsupportedOperationException("Operation not permitted on readonly quintuple !");
	}

	@Override
	@Deprecated
	public ReadOnlyQuintuple<A, B, C, D, E> setFifth(final E fifth) {
		throw new UnsupportedOperationException("Operation not permitted on readonly quintuple !");
	}

	@Override
	public String toString() {
		return String.format("%s(readonly)", super.toString());
	}

}
