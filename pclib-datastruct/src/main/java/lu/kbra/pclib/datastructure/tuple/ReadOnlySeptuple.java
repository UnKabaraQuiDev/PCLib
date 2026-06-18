package lu.kbra.pclib.datastructure.tuple;

import lu.kbra.pclib.impl.function.SeptFunction;

public class ReadOnlySeptuple<A, B, C, D, E, F, G> extends Septuple<A, B, C, D, E, F, G> {

	public ReadOnlySeptuple() {
	}

	public ReadOnlySeptuple(final A first, final B second, final C third, final D fourth, final E fifth, final F sixth, final G seventh) {
		super(first, second, third, fourth, fifth, sixth, seventh);
	}

	@Override
	public ReadOnlySeptuple<A, B, C, D, E, F, G> clone() {
		return new ReadOnlySeptuple<>(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh);
	}

	@Override
	public <R1, R2, R3, R4, R5, R6, R7> Septuple<R1, R2, R3, R4, R5, R6, R7> map(
			final SeptFunction<A, B, C, D, E, F, G, R1> funcFirst,
			final SeptFunction<A, B, C, D, E, F, G, R2> funcSecond,
			final SeptFunction<A, B, C, D, E, F, G, R3> funcThird,
			final SeptFunction<A, B, C, D, E, F, G, R4> funcFourth,
			final SeptFunction<A, B, C, D, E, F, G, R5> funcFifth,
			final SeptFunction<A, B, C, D, E, F, G, R6> funcSixth,
			final SeptFunction<A, B, C, D, E, F, G, R7> funcSeventh) {

		return this.map((a, b, c, d, e, f, g) -> new ReadOnlySeptuple<>(funcFirst.apply(a, b, c, d, e, f, g),
				funcSecond.apply(a, b, c, d, e, f, g),
				funcThird.apply(a, b, c, d, e, f, g),
				funcFourth.apply(a, b, c, d, e, f, g),
				funcFifth.apply(a, b, c, d, e, f, g),
				funcSixth.apply(a, b, c, d, e, f, g),
				funcSeventh.apply(a, b, c, d, e, f, g)));
	}

	@Override
	public <T> T map(final SeptFunction<A, B, C, D, E, F, G, T> func) {
		return func.apply(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh);
	}

	@Override
	public <T> Septuple<A, B, C, D, T, F, G> mapFifth(final SeptFunction<A, B, C, D, E, F, G, T> func) {
		return this.map((a, b, c, d, e, f, g) -> new ReadOnlySeptuple<>(a, b, c, d, func.apply(a, b, c, d, e, f, g), f, g));
	}

	@Override
	public <T> Septuple<T, B, C, D, E, F, G> mapFirst(final SeptFunction<A, B, C, D, E, F, G, T> func) {
		return this.map((a, b, c, d, e, f, g) -> new ReadOnlySeptuple<>(func.apply(a, b, c, d, e, f, g), b, c, d, e, f, g));
	}

	@Override
	public <T> Septuple<A, B, C, T, E, F, G> mapFourth(final SeptFunction<A, B, C, D, E, F, G, T> func) {
		return this.map((a, b, c, d, e, f, g) -> new ReadOnlySeptuple<>(a, b, c, func.apply(a, b, c, d, e, f, g), e, f, g));
	}

	@Override
	public <T> Septuple<A, T, C, D, E, F, G> mapSecond(final SeptFunction<A, B, C, D, E, F, G, T> func) {
		return this.map((a, b, c, d, e, f, g) -> new ReadOnlySeptuple<>(a, func.apply(a, b, c, d, e, f, g), c, d, e, f, g));
	}

	@Override
	public <T> Septuple<A, B, C, D, E, F, T> mapSeventh(final SeptFunction<A, B, C, D, E, F, G, T> func) {
		return this.map((a, b, c, d, e, f, g) -> new ReadOnlySeptuple<>(a, b, c, d, e, f, func.apply(a, b, c, d, e, f, g)));
	}

	@Override
	public <T> Septuple<A, B, C, D, E, T, G> mapSixth(final SeptFunction<A, B, C, D, E, F, G, T> func) {
		return this.map((a, b, c, d, e, f, g) -> new ReadOnlySeptuple<>(a, b, c, d, e, func.apply(a, b, c, d, e, f, g), g));
	}

	@Override
	public <T> Septuple<A, B, T, D, E, F, G> mapThird(final SeptFunction<A, B, C, D, E, F, G, T> func) {
		return this.map((a, b, c, d, e, f, g) -> new ReadOnlySeptuple<>(a, b, func.apply(a, b, c, d, e, f, g), d, e, f, g));
	}

	@Override
	@Deprecated
	public ReadOnlySeptuple<A, B, C, D, E, F, G> setFifth(final E fifth) {
		throw new UnsupportedOperationException("Operation not permitted on readonly septuple !");
	}

	@Override
	@Deprecated
	public ReadOnlySeptuple<A, B, C, D, E, F, G> setFirst(final A first) {
		throw new UnsupportedOperationException("Operation not permitted on readonly septuple !");
	}

	@Override
	@Deprecated
	public ReadOnlySeptuple<A, B, C, D, E, F, G> setFourth(final D fourth) {
		throw new UnsupportedOperationException("Operation not permitted on readonly septuple !");
	}

	@Override
	@Deprecated
	public ReadOnlySeptuple<A, B, C, D, E, F, G> setSecond(final B second) {
		throw new UnsupportedOperationException("Operation not permitted on readonly septuple !");
	}

	@Override
	@Deprecated
	public ReadOnlySeptuple<A, B, C, D, E, F, G> setSeventh(final G seventh) {
		throw new UnsupportedOperationException("Operation not permitted on readonly septuple !");
	}

	@Override
	@Deprecated
	public ReadOnlySeptuple<A, B, C, D, E, F, G> setSixth(final F sixth) {
		throw new UnsupportedOperationException("Operation not permitted on readonly septuple !");
	}

	@Override
	@Deprecated
	public ReadOnlySeptuple<A, B, C, D, E, F, G> setThird(final C third) {
		throw new UnsupportedOperationException("Operation not permitted on readonly septuple !");
	}

	@Override
	public String toString() {
		return String.format("%s(readonly)", super.toString());
	}

}
