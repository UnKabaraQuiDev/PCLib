package lu.kbra.pclib.datastructure.tuple;

import java.util.Objects;

import lu.kbra.pclib.datastructure.DeepCloneable;
import lu.kbra.pclib.impl.function.SextFunction;

public class Sextuple<A, B, C, D, E, F> implements DeepCloneable, Tuple {

	protected A first;
	protected B second;
	protected C third;
	protected D fourth;
	protected E fifth;
	protected F sixth;

	public Sextuple() {
		this(null, null, null, null, null, null);
	}

	public Sextuple(final A first, final B second, final C third, final D fourth, final E fifth, final F sixth) {
		this.first = first;
		this.second = second;
		this.third = third;
		this.fourth = fourth;
		this.fifth = fifth;
		this.sixth = sixth;
	}

	@Override
	public Object[] asArray() {
		return new Object[] { this.first, this.second, this.third, this.fourth, this.fifth, this.sixth };
	}

	@Override
	public Sextuple<A, B, C, D, E, F> clone() {
		return new Sextuple<>(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth);
	}

	@Override
	public int elementCount() {
		return 6;
	}

	@Override
	public <T> T get(final int i) {
		if (i < 0 || i > 5) {
			throw new IndexOutOfBoundsException(i + " <> [0..5]");
		}
		return i == 0 ? (T) this.first
				: i == 1 ? (T) this.second
				: i == 2 ? (T) this.third
				: i == 3 ? (T) this.fourth
				: i == 4 ? (T) this.fifth
				: (T) this.sixth;
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

	public F getSixth() {
		return this.sixth;
	}

	public C getThird() {
		return this.third;
	}

	public <R1, R2, R3, R4, R5, R6> Sextuple<R1, R2, R3, R4, R5, R6> map(
			final SextFunction<A, B, C, D, E, F, R1> funcFirst,
			final SextFunction<A, B, C, D, E, F, R2> funcSecond,
			final SextFunction<A, B, C, D, E, F, R3> funcThird,
			final SextFunction<A, B, C, D, E, F, R4> funcFourth,
			final SextFunction<A, B, C, D, E, F, R5> funcFifth,
			final SextFunction<A, B, C, D, E, F, R6> funcSixth) {

		return this.map((a, b, c, d, e, f) -> new Sextuple<>(funcFirst.apply(a, b, c, d, e, f),
				funcSecond.apply(a, b, c, d, e, f),
				funcThird.apply(a, b, c, d, e, f),
				funcFourth.apply(a, b, c, d, e, f),
				funcFifth.apply(a, b, c, d, e, f),
				funcSixth.apply(a, b, c, d, e, f)));
	}

	public <T> T map(final SextFunction<A, B, C, D, E, F, T> func) {
		return func.apply(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth);
	}

	public <T> Sextuple<A, B, C, D, T, F> mapFifth(final SextFunction<A, B, C, D, E, F, T> func) {
		return this.map((a, b, c, d, e, f) -> new Sextuple<>(a, b, c, d, func.apply(a, b, c, d, e, f), f));
	}

	public <T> Sextuple<T, B, C, D, E, F> mapFirst(final SextFunction<A, B, C, D, E, F, T> func) {
		return this.map((a, b, c, d, e, f) -> new Sextuple<>(func.apply(a, b, c, d, e, f), b, c, d, e, f));
	}

	public <T> Sextuple<A, B, C, T, E, F> mapFourth(final SextFunction<A, B, C, D, E, F, T> func) {
		return this.map((a, b, c, d, e, f) -> new Sextuple<>(a, b, c, func.apply(a, b, c, d, e, f), e, f));
	}

	public <T> Sextuple<A, T, C, D, E, F> mapSecond(final SextFunction<A, B, C, D, E, F, T> func) {
		return this.map((a, b, c, d, e, f) -> new Sextuple<>(a, func.apply(a, b, c, d, e, f), c, d, e, f));
	}

	public <T> Sextuple<A, B, C, D, E, T> mapSixth(final SextFunction<A, B, C, D, E, F, T> func) {
		return this.map((a, b, c, d, e, f) -> new Sextuple<>(a, b, c, d, e, func.apply(a, b, c, d, e, f)));
	}

	public <T> Sextuple<A, B, T, D, E, F> mapThird(final SextFunction<A, B, C, D, E, F, T> func) {
		return this.map((a, b, c, d, e, f) -> new Sextuple<>(a, b, func.apply(a, b, c, d, e, f), d, e, f));
	}

	public Sextuple<A, B, C, D, E, F> setFifth(final E fifth) {
		this.fifth = fifth;
		return this;
	}

	public Sextuple<A, B, C, D, E, F> setFirst(final A first) {
		this.first = first;
		return this;
	}

	public Sextuple<A, B, C, D, E, F> setFourth(final D fourth) {
		this.fourth = fourth;
		return this;
	}

	public Sextuple<A, B, C, D, E, F> setSecond(final B second) {
		this.second = second;
		return this;
	}

	public Sextuple<A, B, C, D, E, F> setSixth(final F sixth) {
		this.sixth = sixth;
		return this;
	}

	public Sextuple<A, B, C, D, E, F> setThird(final C third) {
		this.third = third;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.fifth, this.first, this.fourth, this.second, this.sixth, this.third);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if ((obj == null) || (this.getClass() != obj.getClass())) {
			return false;
		}
		final Sextuple other = (Sextuple) obj;
		return Objects.equals(this.fifth, other.fifth) && Objects.equals(this.first, other.first)
				&& Objects.equals(this.fourth, other.fourth) && Objects.equals(this.second, other.second)
				&& Objects.equals(this.sixth, other.sixth) && Objects.equals(this.third, other.third);
	}

	@Override
	public String toString() {
		return String.format("{%s,%s,%s,%s,%s,%s}", this.first, this.second, this.third, this.fourth, this.fifth, this.sixth);
	}

}
