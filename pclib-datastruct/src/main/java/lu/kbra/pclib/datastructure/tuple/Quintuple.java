package lu.kbra.pclib.datastructure.tuple;

import lu.kbra.pclib.datastructure.DeepCloneable;
import lu.kbra.pclib.impl.function.QuintFunction;

public class Quintuple<A, B, C, D, E> implements DeepCloneable, Tuple {

	protected A first;
	protected B second;
	protected C third;
	protected D fourth;
	protected E fifth;

	public Quintuple() {
		this(null, null, null, null, null);
	}

	public Quintuple(final A first, final B second, final C third, final D fourth, final E fifth) {
		this.first = first;
		this.second = second;
		this.third = third;
		this.fourth = fourth;
		this.fifth = fifth;
	}

	@Override
	public Object[] asArray() {
		return new Object[] { this.first, this.second, this.third, this.fourth, this.fifth };
	}

	@Override
	public Quintuple<A, B, C, D, E> clone() {
		return new Quintuple<>(this.first, this.second, this.third, this.fourth, this.fifth);
	}

	@Override
	public int elementCount() {
		return 5;
	}

	@Override
	public <T> T get(final int i) {
		if (i < 0 || i > 4) {
			throw new IndexOutOfBoundsException(i + " <> [0..4]");
		}
		return i == 0 ? (T) this.first
				: i == 1 ? (T) this.second
				: i == 2 ? (T) this.third
				: i == 3 ? (T) this.fourth
				: (T) this.fifth;
	}

	public E getFifth() {
		return this.fifth;
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

	public <R1, R2, R3, R4, R5> Quintuple<R1, R2, R3, R4, R5> map(
			final QuintFunction<A, B, C, D, E, R1> funcFirst,
			final QuintFunction<A, B, C, D, E, R2> funcSecond,
			final QuintFunction<A, B, C, D, E, R3> funcThird,
			final QuintFunction<A, B, C, D, E, R4> funcFourth,
			final QuintFunction<A, B, C, D, E, R5> funcFifth) {

		return this.map((a, b, c, d, e) -> new Quintuple<>(funcFirst.apply(a, b, c, d, e),
				funcSecond.apply(a, b, c, d, e),
				funcThird.apply(a, b, c, d, e),
				funcFourth.apply(a, b, c, d, e),
				funcFifth.apply(a, b, c, d, e)));
	}

	public <T> T map(final QuintFunction<A, B, C, D, E, T> func) {
		return func.apply(this.first, this.second, this.third, this.fourth, this.fifth);
	}

	public <T> Quintuple<A, B, C, D, T> mapFifth(final QuintFunction<A, B, C, D, E, T> func) {
		return this.map((a, b, c, d, e) -> new Quintuple<>(a, b, c, d, func.apply(a, b, c, d, e)));
	}

	public <T> Quintuple<T, B, C, D, E> mapFirst(final QuintFunction<A, B, C, D, E, T> func) {
		return this.map((a, b, c, d, e) -> new Quintuple<>(func.apply(a, b, c, d, e), b, c, d, e));
	}

	public <T> Quintuple<A, B, C, T, E> mapFourth(final QuintFunction<A, B, C, D, E, T> func) {
		return this.map((a, b, c, d, e) -> new Quintuple<>(a, b, c, func.apply(a, b, c, d, e), e));
	}

	public <T> Quintuple<A, T, C, D, E> mapSecond(final QuintFunction<A, B, C, D, E, T> func) {
		return this.map((a, b, c, d, e) -> new Quintuple<>(a, func.apply(a, b, c, d, e), c, d, e));
	}

	public <T> Quintuple<A, B, T, D, E> mapThird(final QuintFunction<A, B, C, D, E, T> func) {
		return this.map((a, b, c, d, e) -> new Quintuple<>(a, b, func.apply(a, b, c, d, e), d, e));
	}

	public Quintuple<A, B, C, D, E> setFifth(final E fifth) {
		this.fifth = fifth;
		return this;
	}

	public Quintuple<A, B, C, D, E> setFirst(final A first) {
		this.first = first;
		return this;
	}

	public Quintuple<A, B, C, D, E> setFourth(final D fourth) {
		this.fourth = fourth;
		return this;
	}

	public Quintuple<A, B, C, D, E> setSecond(final B second) {
		this.second = second;
		return this;
	}

	public Quintuple<A, B, C, D, E> setThird(final C third) {
		this.third = third;
		return this;
	}

	@Override
	public String toString() {
		return String.format("{%s,%s,%s,%s,%s}", this.first, this.second, this.third, this.fourth, this.fifth);
	}

}
