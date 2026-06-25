package lu.kbra.pclib.datastructure.tuple;

import java.util.Objects;

import lu.kbra.pclib.datastructure.DeepCloneable;
import lu.kbra.pclib.impl.function.SeptFunction;

public class Septuple<A, B, C, D, E, F, G> implements DeepCloneable, Tuple {

	protected A first;
	protected B second;
	protected C third;
	protected D fourth;
	protected E fifth;
	protected F sixth;
	protected G seventh;

	public Septuple() {
		this(null, null, null, null, null, null, null);
	}

	public Septuple(final A first, final B second, final C third, final D fourth, final E fifth, final F sixth, final G seventh) {
		this.first = first;
		this.second = second;
		this.third = third;
		this.fourth = fourth;
		this.fifth = fifth;
		this.sixth = sixth;
		this.seventh = seventh;
	}

	@Override
	public Object[] asArray() {
		return new Object[] { this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh };
	}

	@Override
	public Septuple<A, B, C, D, E, F, G> clone() {
		return new Septuple<>(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh);
	}

	@Override
	public int elementCount() {
		return 7;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || this.getClass() != obj.getClass()) {
			return false;
		}
		final Septuple other = (Septuple) obj;
		return Objects.equals(this.fifth, other.fifth) && Objects.equals(this.first, other.first)
				&& Objects.equals(this.fourth, other.fourth) && Objects.equals(this.second, other.second)
				&& Objects.equals(this.seventh, other.seventh) && Objects.equals(this.sixth, other.sixth)
				&& Objects.equals(this.third, other.third);
	}

	@Override
	public <T> T get(final int i) {
		if (i < 0 || i > 6) {
			throw new IndexOutOfBoundsException(i + " <> [0..6]");
		}
		return i == 0 ? (T) this.first
				: i == 1 ? (T) this.second
				: i == 2 ? (T) this.third
				: i == 3 ? (T) this.fourth
				: i == 4 ? (T) this.fifth
				: i == 5 ? (T) this.sixth
				: (T) this.seventh;
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

	public G getSeventh() {
		return this.seventh;
	}

	public F getSixth() {
		return this.sixth;
	}

	public C getThird() {
		return this.third;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.fifth, this.first, this.fourth, this.second, this.seventh, this.sixth, this.third);
	}

	public <R1, R2, R3, R4, R5, R6, R7> Septuple<R1, R2, R3, R4, R5, R6, R7> map(
			final SeptFunction<A, B, C, D, E, F, G, R1> funcFirst,
			final SeptFunction<A, B, C, D, E, F, G, R2> funcSecond,
			final SeptFunction<A, B, C, D, E, F, G, R3> funcThird,
			final SeptFunction<A, B, C, D, E, F, G, R4> funcFourth,
			final SeptFunction<A, B, C, D, E, F, G, R5> funcFifth,
			final SeptFunction<A, B, C, D, E, F, G, R6> funcSixth,
			final SeptFunction<A, B, C, D, E, F, G, R7> funcSeventh) {

		return this.map((a, b, c, d, e, f, g) -> new Septuple<>(funcFirst.apply(a, b, c, d, e, f, g),
				funcSecond.apply(a, b, c, d, e, f, g),
				funcThird.apply(a, b, c, d, e, f, g),
				funcFourth.apply(a, b, c, d, e, f, g),
				funcFifth.apply(a, b, c, d, e, f, g),
				funcSixth.apply(a, b, c, d, e, f, g),
				funcSeventh.apply(a, b, c, d, e, f, g)));
	}

	public <T> T map(final SeptFunction<A, B, C, D, E, F, G, T> func) {
		return func.apply(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh);
	}

	public <T> Septuple<A, B, C, D, T, F, G> mapFifth(final SeptFunction<A, B, C, D, E, F, G, T> func) {
		return this.map((a, b, c, d, e, f, g) -> new Septuple<>(a, b, c, d, func.apply(a, b, c, d, e, f, g), f, g));
	}

	public <T> Septuple<T, B, C, D, E, F, G> mapFirst(final SeptFunction<A, B, C, D, E, F, G, T> func) {
		return this.map((a, b, c, d, e, f, g) -> new Septuple<>(func.apply(a, b, c, d, e, f, g), b, c, d, e, f, g));
	}

	public <T> Septuple<A, B, C, T, E, F, G> mapFourth(final SeptFunction<A, B, C, D, E, F, G, T> func) {
		return this.map((a, b, c, d, e, f, g) -> new Septuple<>(a, b, c, func.apply(a, b, c, d, e, f, g), e, f, g));
	}

	public <T> Septuple<A, T, C, D, E, F, G> mapSecond(final SeptFunction<A, B, C, D, E, F, G, T> func) {
		return this.map((a, b, c, d, e, f, g) -> new Septuple<>(a, func.apply(a, b, c, d, e, f, g), c, d, e, f, g));
	}

	public <T> Septuple<A, B, C, D, E, F, T> mapSeventh(final SeptFunction<A, B, C, D, E, F, G, T> func) {
		return this.map((a, b, c, d, e, f, g) -> new Septuple<>(a, b, c, d, e, f, func.apply(a, b, c, d, e, f, g)));
	}

	public <T> Septuple<A, B, C, D, E, T, G> mapSixth(final SeptFunction<A, B, C, D, E, F, G, T> func) {
		return this.map((a, b, c, d, e, f, g) -> new Septuple<>(a, b, c, d, e, func.apply(a, b, c, d, e, f, g), g));
	}

	public <T> Septuple<A, B, T, D, E, F, G> mapThird(final SeptFunction<A, B, C, D, E, F, G, T> func) {
		return this.map((a, b, c, d, e, f, g) -> new Septuple<>(a, b, func.apply(a, b, c, d, e, f, g), d, e, f, g));
	}

	public Septuple<A, B, C, D, E, F, G> setFifth(final E fifth) {
		this.fifth = fifth;
		return this;
	}

	public Septuple<A, B, C, D, E, F, G> setFirst(final A first) {
		this.first = first;
		return this;
	}

	public Septuple<A, B, C, D, E, F, G> setFourth(final D fourth) {
		this.fourth = fourth;
		return this;
	}

	public Septuple<A, B, C, D, E, F, G> setSecond(final B second) {
		this.second = second;
		return this;
	}

	public Septuple<A, B, C, D, E, F, G> setSeventh(final G seventh) {
		this.seventh = seventh;
		return this;
	}

	public Septuple<A, B, C, D, E, F, G> setSixth(final F sixth) {
		this.sixth = sixth;
		return this;
	}

	public Septuple<A, B, C, D, E, F, G> setThird(final C third) {
		this.third = third;
		return this;
	}

	@Override
	public String toString() {
		return String
				.format("{%s,%s,%s,%s,%s,%s,%s}", this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh);
	}

}
