package lu.kbra.pclib.datastructure.tuple;

import lu.kbra.pclib.impl.function.SextFunction;

public class ReadOnlySextuple<A, B, C, D, E, F> extends Sextuple<A, B, C, D, E, F> {

	public ReadOnlySextuple() {
	}

	public ReadOnlySextuple(final A first, final B second, final C third, final D fourth, final E fifth, final F sixth) {
		super(first, second, third, fourth, fifth, sixth);
	}

	@Override
	public ReadOnlySextuple<A, B, C, D, E, F> clone() {
		return new ReadOnlySextuple<>(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth);
	}

	@Override
	public <R1, R2, R3, R4, R5, R6> Sextuple<R1, R2, R3, R4, R5, R6> map(
			final SextFunction<A, B, C, D, E, F, R1> funcFirst,
			final SextFunction<A, B, C, D, E, F, R2> funcSecond,
			final SextFunction<A, B, C, D, E, F, R3> funcThird,
			final SextFunction<A, B, C, D, E, F, R4> funcFourth,
			final SextFunction<A, B, C, D, E, F, R5> funcFifth,
			final SextFunction<A, B, C, D, E, F, R6> funcSixth) {

		return this.map((a, b, c, d, e, f) -> new ReadOnlySextuple<>(funcFirst.apply(a, b, c, d, e, f),
				funcSecond.apply(a, b, c, d, e, f),
				funcThird.apply(a, b, c, d, e, f),
				funcFourth.apply(a, b, c, d, e, f),
				funcFifth.apply(a, b, c, d, e, f),
				funcSixth.apply(a, b, c, d, e, f)));
	}

	@Override
	public <T> T map(final SextFunction<A, B, C, D, E, F, T> func) {
		return func.apply(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth);
	}

	@Override
	public <T> Sextuple<A, B, C, D, T, F> mapFifth(final SextFunction<A, B, C, D, E, F, T> func) {
		return this.map((a, b, c, d, e, f) -> new ReadOnlySextuple<>(a, b, c, d, func.apply(a, b, c, d, e, f), f));
	}

	@Override
	public <T> Sextuple<T, B, C, D, E, F> mapFirst(final SextFunction<A, B, C, D, E, F, T> func) {
		return this.map((a, b, c, d, e, f) -> new ReadOnlySextuple<>(func.apply(a, b, c, d, e, f), b, c, d, e, f));
	}

	@Override
	public <T> Sextuple<A, B, C, T, E, F> mapFourth(final SextFunction<A, B, C, D, E, F, T> func) {
		return this.map((a, b, c, d, e, f) -> new ReadOnlySextuple<>(a, b, c, func.apply(a, b, c, d, e, f), e, f));
	}

	@Override
	public <T> Sextuple<A, T, C, D, E, F> mapSecond(final SextFunction<A, B, C, D, E, F, T> func) {
		return this.map((a, b, c, d, e, f) -> new ReadOnlySextuple<>(a, func.apply(a, b, c, d, e, f), c, d, e, f));
	}

	@Override
	public <T> Sextuple<A, B, C, D, E, T> mapSixth(final SextFunction<A, B, C, D, E, F, T> func) {
		return this.map((a, b, c, d, e, f) -> new ReadOnlySextuple<>(a, b, c, d, e, func.apply(a, b, c, d, e, f)));
	}

	@Override
	public <T> Sextuple<A, B, T, D, E, F> mapThird(final SextFunction<A, B, C, D, E, F, T> func) {
		return this.map((a, b, c, d, e, f) -> new ReadOnlySextuple<>(a, b, func.apply(a, b, c, d, e, f), d, e, f));
	}

	@Override
	@Deprecated
	public ReadOnlySextuple<A, B, C, D, E, F> setFifth(final E fifth) {
		throw new UnsupportedOperationException("Operation not permitted on readonly sextuple !");
	}

	@Override
	@Deprecated
	public ReadOnlySextuple<A, B, C, D, E, F> setFirst(final A first) {
		throw new UnsupportedOperationException("Operation not permitted on readonly sextuple !");
	}

	@Override
	@Deprecated
	public ReadOnlySextuple<A, B, C, D, E, F> setFourth(final D fourth) {
		throw new UnsupportedOperationException("Operation not permitted on readonly sextuple !");
	}

	@Override
	@Deprecated
	public ReadOnlySextuple<A, B, C, D, E, F> setSecond(final B second) {
		throw new UnsupportedOperationException("Operation not permitted on readonly sextuple !");
	}

	@Override
	@Deprecated
	public ReadOnlySextuple<A, B, C, D, E, F> setSixth(final F sixth) {
		throw new UnsupportedOperationException("Operation not permitted on readonly sextuple !");
	}

	@Override
	@Deprecated
	public ReadOnlySextuple<A, B, C, D, E, F> setThird(final C third) {
		throw new UnsupportedOperationException("Operation not permitted on readonly sextuple !");
	}

	@Override
	public String toString() {
		return String.format("%s(readonly)", super.toString());
	}

}
