package lu.kbra.pclib.datastructure.tuple;

import lu.kbra.pclib.impl.function.OctFunction;

public class ReadOnlyOctuple<A, B, C, D, E, F, G, H> extends Octuple<A, B, C, D, E, F, G, H> {

	public ReadOnlyOctuple() {
	}

	public ReadOnlyOctuple(
			final A first,
			final B second,
			final C third,
			final D fourth,
			final E fifth,
			final F sixth,
			final G seventh,
			final H eighth) {
		super(first, second, third, fourth, fifth, sixth, seventh, eighth);
	}

	@Override
	public ReadOnlyOctuple<A, B, C, D, E, F, G, H> clone() {
		return new ReadOnlyOctuple<>(first, second, third, fourth, fifth, sixth, seventh, eighth);
	}

	public <T> T map(final OctFunction<A, B, C, D, E, F, G, H, T> func) {
		return func.apply(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh, this.eighth);
	}

	public <T> Octuple<T, B, C, D, E, F, G, H> mapFirst(final OctFunction<A, B, C, D, E, F, G, H, T> func) {
		return map((a, b, c, d, e, f, g, h) -> new ReadOnlyOctuple<>(func.apply(a, b, c, d, e, f, g, h), b, c, d, e, f, g, h));
	}

	public <T> Octuple<A, T, C, D, E, F, G, H> mapSecond(final OctFunction<A, B, C, D, E, F, G, H, T> func) {
		return map((a, b, c, d, e, f, g, h) -> new ReadOnlyOctuple<>(a, func.apply(a, b, c, d, e, f, g, h), c, d, e, f, g, h));
	}

	public <T> Octuple<A, B, T, D, E, F, G, H> mapThird(final OctFunction<A, B, C, D, E, F, G, H, T> func) {
		return map((a, b, c, d, e, f, g, h) -> new ReadOnlyOctuple<>(a, b, func.apply(a, b, c, d, e, f, g, h), d, e, f, g, h));
	}

	public <T> Octuple<A, B, C, T, E, F, G, H> mapFourth(final OctFunction<A, B, C, D, E, F, G, H, T> func) {
		return map((a, b, c, d, e, f, g, h) -> new ReadOnlyOctuple<>(a, b, c, func.apply(a, b, c, d, e, f, g, h), e, f, g, h));
	}

	public <T> Octuple<A, B, C, D, T, F, G, H> mapFifth(final OctFunction<A, B, C, D, E, F, G, H, T> func) {
		return map((a, b, c, d, e, f, g, h) -> new ReadOnlyOctuple<>(a, b, c, d, func.apply(a, b, c, d, e, f, g, h), f, g, h));
	}

	public <T> Octuple<A, B, C, D, E, T, G, H> mapSixth(final OctFunction<A, B, C, D, E, F, G, H, T> func) {
		return map((a, b, c, d, e, f, g, h) -> new ReadOnlyOctuple<>(a, b, c, d, e, func.apply(a, b, c, d, e, f, g, h), g, h));
	}

	public <T> Octuple<A, B, C, D, E, F, T, H> mapSeventh(final OctFunction<A, B, C, D, E, F, G, H, T> func) {
		return map((a, b, c, d, e, f, g, h) -> new ReadOnlyOctuple<>(a, b, c, d, e, f, func.apply(a, b, c, d, e, f, g, h), h));
	}

	public <T> Octuple<A, B, C, D, E, F, G, T> mapEighth(final OctFunction<A, B, C, D, E, F, G, H, T> func) {
		return map((a, b, c, d, e, f, g, h) -> new ReadOnlyOctuple<>(a, b, c, d, e, f, g, func.apply(a, b, c, d, e, f, g, h)));
	}

	public <R1, R2, R3, R4, R5, R6, R7, R8> Octuple<R1, R2, R3, R4, R5, R6, R7, R8> map(
			final OctFunction<A, B, C, D, E, F, G, H, R1> funcFirst,
			final OctFunction<A, B, C, D, E, F, G, H, R2> funcSecond,
			final OctFunction<A, B, C, D, E, F, G, H, R3> funcThird,
			final OctFunction<A, B, C, D, E, F, G, H, R4> funcFourth,
			final OctFunction<A, B, C, D, E, F, G, H, R5> funcFifth,
			final OctFunction<A, B, C, D, E, F, G, H, R6> funcSixth,
			final OctFunction<A, B, C, D, E, F, G, H, R7> funcSeventh,
			final OctFunction<A, B, C, D, E, F, G, H, R8> funcEighth) {

		return map((a, b, c, d, e, f, g, h) -> new ReadOnlyOctuple<>(funcFirst.apply(a, b, c, d, e, f, g, h),
				funcSecond.apply(a, b, c, d, e, f, g, h),
				funcThird.apply(a, b, c, d, e, f, g, h),
				funcFourth.apply(a, b, c, d, e, f, g, h),
				funcFifth.apply(a, b, c, d, e, f, g, h),
				funcSixth.apply(a, b, c, d, e, f, g, h),
				funcSeventh.apply(a, b, c, d, e, f, g, h),
				funcEighth.apply(a, b, c, d, e, f, g, h)));
	}

	@Override
	@Deprecated
	public ReadOnlyOctuple<A, B, C, D, E, F, G, H> setFirst(final A first) {
		throw new UnsupportedOperationException("Operation not permitted on readonly octuple !");
	}

	@Override
	@Deprecated
	public ReadOnlyOctuple<A, B, C, D, E, F, G, H> setSecond(final B second) {
		throw new UnsupportedOperationException("Operation not permitted on readonly octuple !");
	}

	@Override
	@Deprecated
	public ReadOnlyOctuple<A, B, C, D, E, F, G, H> setThird(final C third) {
		throw new UnsupportedOperationException("Operation not permitted on readonly octuple !");
	}

	@Override
	@Deprecated
	public ReadOnlyOctuple<A, B, C, D, E, F, G, H> setFourth(final D fourth) {
		throw new UnsupportedOperationException("Operation not permitted on readonly octuple !");
	}

	@Override
	@Deprecated
	public ReadOnlyOctuple<A, B, C, D, E, F, G, H> setFifth(final E fifth) {
		throw new UnsupportedOperationException("Operation not permitted on readonly octuple !");
	}

	@Override
	@Deprecated
	public ReadOnlyOctuple<A, B, C, D, E, F, G, H> setSixth(final F sixth) {
		throw new UnsupportedOperationException("Operation not permitted on readonly octuple !");
	}

	@Override
	@Deprecated
	public ReadOnlyOctuple<A, B, C, D, E, F, G, H> setSeventh(final G seventh) {
		throw new UnsupportedOperationException("Operation not permitted on readonly octuple !");
	}

	@Override
	@Deprecated
	public ReadOnlyOctuple<A, B, C, D, E, F, G, H> setEighth(final H eighth) {
		throw new UnsupportedOperationException("Operation not permitted on readonly octuple !");
	}

	@Override
	public String toString() {
		return String.format("%s(readonly)", super.toString());
	}

}
