package lu.kbra.pclib.datastructure.tuple;

import lu.kbra.pclib.impl.function.QuadFunction;

public class ReadOnlyQuadruple<A, B, C, D> extends Quadruple<A, B, C, D> {

	public ReadOnlyQuadruple() {
	}

	public ReadOnlyQuadruple(final A first, final B second, final C third, final D fourth) {
		super(first, second, third, fourth);
	}

	@Override
	public ReadOnlyQuadruple<A, B, C, D> clone() {
		return new ReadOnlyQuadruple<>(this.first, this.second, this.third, this.fourth);
	}

	@Override
	public <R1, R2, R3, R4> Quadruple<R1, R2, R3, R4> map(
			final QuadFunction<A, B, C, D, R1> funcFirst,
			final QuadFunction<A, B, C, D, R2> funcSecond,
			final QuadFunction<A, B, C, D, R3> funcThird,
			final QuadFunction<A, B, C, D, R4> funcFourth) {

		return this.map((a, b, c, d) -> new ReadOnlyQuadruple<>(funcFirst.apply(a, b, c, d),
				funcSecond.apply(a, b, c, d),
				funcThird.apply(a, b, c, d),
				funcFourth.apply(a, b, c, d)));
	}

	@Override
	public <T> T map(final QuadFunction<A, B, C, D, T> func) {
		return func.apply(this.first, this.second, this.third, this.fourth);
	}

	@Override
	public <T> Quadruple<T, B, C, D> mapFirst(final QuadFunction<A, B, C, D, T> func) {
		return this.map((a, b, c, d) -> new ReadOnlyQuadruple<>(func.apply(a, b, c, d), b, c, d));
	}

	@Override
	public <T> Quadruple<A, B, C, T> mapFourth(final QuadFunction<A, B, C, D, T> func) {
		return this.map((a, b, c, d) -> new ReadOnlyQuadruple<>(a, b, c, func.apply(a, b, c, d)));
	}

	@Override
	public <T> Quadruple<A, T, C, D> mapSecond(final QuadFunction<A, B, C, D, T> func) {
		return this.map((a, b, c, d) -> new ReadOnlyQuadruple<>(a, func.apply(a, b, c, d), c, d));
	}

	@Override
	public <T> Quadruple<A, B, T, D> mapThird(final QuadFunction<A, B, C, D, T> func) {
		return this.map((a, b, c, d) -> new ReadOnlyQuadruple<>(a, b, func.apply(a, b, c, d), d));
	}

	@Override
	@Deprecated
	public ReadOnlyQuadruple<A, B, C, D> setFirst(final A first) {
		throw new UnsupportedOperationException("Operation not permitted on readonly quadruple !");
	}

	@Override
	@Deprecated
	public ReadOnlyQuadruple<A, B, C, D> setFourth(final D fourth) {
		throw new UnsupportedOperationException("Operation not permitted on readonly quadruple !");
	}

	@Override
	@Deprecated
	public ReadOnlyQuadruple<A, B, C, D> setSecond(final B second) {
		throw new UnsupportedOperationException("Operation not permitted on readonly quadruple !");
	}

	@Override
	@Deprecated
	public ReadOnlyQuadruple<A, B, C, D> setThird(final C third) {
		throw new UnsupportedOperationException("Operation not permitted on readonly quadruple !");
	}

	@Override
	public String toString() {
		return String.format("%s(readonly)", super.toString());
	}

}
