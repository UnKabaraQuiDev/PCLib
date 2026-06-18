package lu.kbra.pclib.datastructure.tuple;

import lu.kbra.pclib.datastructure.DeepCloneable;
import lu.kbra.pclib.impl.function.QuadFunction;

public class Quadruple<A, B, C, D> implements DeepCloneable, Tuple {

	protected A first;
	protected B second;
	protected C third;
	protected D fourth;

	public Quadruple() {
		this(null, null, null, null);
	}

	public Quadruple(final A first, final B second, final C third, final D fourth) {
		this.first = first;
		this.second = second;
		this.third = third;
		this.fourth = fourth;
	}

	@Override
	public Object[] asArray() {
		return new Object[] { this.first, this.second, this.third, this.fourth };
	}

	@Override
	public Quadruple<A, B, C, D> clone() {
		return new Quadruple<>(this.first, this.second, this.third, this.fourth);
	}

	@Override
	public int elementCount() {
		return 4;
	}

	@Override
	public <T> T get(final int i) {
		if (i < 0 || i > 3) {
			throw new IndexOutOfBoundsException(i + " <> [0..3]");
		}
		return i == 0 ? (T) this.first
				: i == 1 ? (T) this.second
				: i == 2 ? (T) this.third
				: (T) this.fourth;
	}

	public A getFirst() {
		return this.first;
	}

	public D getFourth() {
		return this.fourth;
	}

	public B getSecond() {
		return this.second;
	}

	public C getThird() {
		return this.third;
	}

	public <R1, R2, R3, R4> Quadruple<R1, R2, R3, R4> map(
			final QuadFunction<A, B, C, D, R1> funcFirst,
			final QuadFunction<A, B, C, D, R2> funcSecond,
			final QuadFunction<A, B, C, D, R3> funcThird,
			final QuadFunction<A, B, C, D, R4> funcFourth) {

		return this.map((a, b, c, d) -> new Quadruple<>(funcFirst.apply(a, b, c, d),
				funcSecond.apply(a, b, c, d),
				funcThird.apply(a, b, c, d),
				funcFourth.apply(a, b, c, d)));
	}

	public <T> T map(final QuadFunction<A, B, C, D, T> func) {
		return func.apply(this.first, this.second, this.third, this.fourth);
	}

	public <T> Quadruple<T, B, C, D> mapFirst(final QuadFunction<A, B, C, D, T> func) {
		return this.map((a, b, c, d) -> new Quadruple<>(func.apply(a, b, c, d), b, c, d));
	}

	public <T> Quadruple<A, B, C, T> mapFourth(final QuadFunction<A, B, C, D, T> func) {
		return this.map((a, b, c, d) -> new Quadruple<>(a, b, c, func.apply(a, b, c, d)));
	}

	public <T> Quadruple<A, T, C, D> mapSecond(final QuadFunction<A, B, C, D, T> func) {
		return this.map((a, b, c, d) -> new Quadruple<>(a, func.apply(a, b, c, d), c, d));
	}

	public <T> Quadruple<A, B, T, D> mapThird(final QuadFunction<A, B, C, D, T> func) {
		return this.map((a, b, c, d) -> new Quadruple<>(a, b, func.apply(a, b, c, d), d));
	}

	public Quadruple<A, B, C, D> setFirst(final A first) {
		this.first = first;
		return this;
	}

	public Quadruple<A, B, C, D> setFourth(final D fourth) {
		this.fourth = fourth;
		return this;
	}

	public Quadruple<A, B, C, D> setSecond(final B second) {
		this.second = second;
		return this;
	}

	public Quadruple<A, B, C, D> setThird(final C third) {
		this.third = third;
		return this;
	}

	@Override
	public String toString() {
		return String.format("{%s,%s,%s,%s}", this.first, this.second, this.third, this.fourth);
	}

}
