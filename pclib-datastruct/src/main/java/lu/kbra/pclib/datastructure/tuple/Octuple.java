package lu.kbra.pclib.datastructure.tuple;

import java.util.Objects;

import lu.kbra.pclib.datastructure.DeepCloneable;
import lu.kbra.pclib.impl.function.OctFunction;

public class Octuple<A, B, C, D, E, F, G, H> implements DeepCloneable, Tuple {

	protected A first;
	protected B second;
	protected C third;
	protected D fourth;
	protected E fifth;
	protected F sixth;
	protected G seventh;
	protected H eighth;

	public Octuple() {
		this(null, null, null, null, null, null, null, null);
	}

	public Octuple(
			final A first,
			final B second,
			final C third,
			final D fourth,
			final E fifth,
			final F sixth,
			final G seventh,
			final H eighth) {
		this.first = first;
		this.second = second;
		this.third = third;
		this.fourth = fourth;
		this.fifth = fifth;
		this.sixth = sixth;
		this.seventh = seventh;
		this.eighth = eighth;
	}

	@Override
	public Object[] asArray() {
		return new Object[] { this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh, this.eighth };
	}

	@Override
	public Octuple<A, B, C, D, E, F, G, H> clone() {
		return new Octuple<>(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh, this.eighth);
	}

	@Override
	public int elementCount() {
		return 8;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if ((obj == null) || (this.getClass() != obj.getClass())) {
			return false;
		}
		final Octuple other = (Octuple) obj;
		return Objects.equals(this.eighth, other.eighth) && Objects.equals(this.fifth, other.fifth)
				&& Objects.equals(this.first, other.first) && Objects.equals(this.fourth, other.fourth)
				&& Objects.equals(this.second, other.second) && Objects.equals(this.seventh, other.seventh)
				&& Objects.equals(this.sixth, other.sixth) && Objects.equals(this.third, other.third);
	}

	@Override
	public <T> T get(final int i) {
		if (i < 0 || i > 7) {
			throw new IndexOutOfBoundsException(i + " <> [0..7]");
		}
		return i == 0 ? (T) this.first
				: i == 1 ? (T) this.second
				: i == 2 ? (T) this.third
				: i == 3 ? (T) this.fourth
				: i == 4 ? (T) this.fifth
				: i == 5 ? (T) this.sixth
				: i == 6 ? (T) this.seventh
				: (T) this.eighth;
	}

	public H getEighth() {
		return this.eighth;
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
		return Objects.hash(this.eighth, this.fifth, this.first, this.fourth, this.second, this.seventh, this.sixth, this.third);
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

		return this.map((a, b, c, d, e, f, g, h) -> new Octuple<>(funcFirst.apply(a, b, c, d, e, f, g, h),
				funcSecond.apply(a, b, c, d, e, f, g, h),
				funcThird.apply(a, b, c, d, e, f, g, h),
				funcFourth.apply(a, b, c, d, e, f, g, h),
				funcFifth.apply(a, b, c, d, e, f, g, h),
				funcSixth.apply(a, b, c, d, e, f, g, h),
				funcSeventh.apply(a, b, c, d, e, f, g, h),
				funcEighth.apply(a, b, c, d, e, f, g, h)));
	}

	public <T> T map(final OctFunction<A, B, C, D, E, F, G, H, T> func) {
		return func.apply(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh, this.eighth);
	}

	public <T> Octuple<A, B, C, D, E, F, G, T> mapEighth(final OctFunction<A, B, C, D, E, F, G, H, T> func) {
		return this.map((a, b, c, d, e, f, g, h) -> new Octuple<>(a, b, c, d, e, f, g, func.apply(a, b, c, d, e, f, g, h)));
	}

	public <T> Octuple<A, B, C, D, T, F, G, H> mapFifth(final OctFunction<A, B, C, D, E, F, G, H, T> func) {
		return this.map((a, b, c, d, e, f, g, h) -> new Octuple<>(a, b, c, d, func.apply(a, b, c, d, e, f, g, h), f, g, h));
	}

	public <T> Octuple<T, B, C, D, E, F, G, H> mapFirst(final OctFunction<A, B, C, D, E, F, G, H, T> func) {
		return this.map((a, b, c, d, e, f, g, h) -> new Octuple<>(func.apply(a, b, c, d, e, f, g, h), b, c, d, e, f, g, h));
	}

	public <T> Octuple<A, B, C, T, E, F, G, H> mapFourth(final OctFunction<A, B, C, D, E, F, G, H, T> func) {
		return this.map((a, b, c, d, e, f, g, h) -> new Octuple<>(a, b, c, func.apply(a, b, c, d, e, f, g, h), e, f, g, h));
	}

	public <T> Octuple<A, T, C, D, E, F, G, H> mapSecond(final OctFunction<A, B, C, D, E, F, G, H, T> func) {
		return this.map((a, b, c, d, e, f, g, h) -> new Octuple<>(a, func.apply(a, b, c, d, e, f, g, h), c, d, e, f, g, h));
	}

	public <T> Octuple<A, B, C, D, E, F, T, H> mapSeventh(final OctFunction<A, B, C, D, E, F, G, H, T> func) {
		return this.map((a, b, c, d, e, f, g, h) -> new Octuple<>(a, b, c, d, e, f, func.apply(a, b, c, d, e, f, g, h), h));
	}

	public <T> Octuple<A, B, C, D, E, T, G, H> mapSixth(final OctFunction<A, B, C, D, E, F, G, H, T> func) {
		return this.map((a, b, c, d, e, f, g, h) -> new Octuple<>(a, b, c, d, e, func.apply(a, b, c, d, e, f, g, h), g, h));
	}

	public <T> Octuple<A, B, T, D, E, F, G, H> mapThird(final OctFunction<A, B, C, D, E, F, G, H, T> func) {
		return this.map((a, b, c, d, e, f, g, h) -> new Octuple<>(a, b, func.apply(a, b, c, d, e, f, g, h), d, e, f, g, h));
	}

	public Octuple<A, B, C, D, E, F, G, H> setEighth(final H eighth) {
		this.eighth = eighth;
		return this;
	}

	public Octuple<A, B, C, D, E, F, G, H> setFifth(final E fifth) {
		this.fifth = fifth;
		return this;
	}

	public Octuple<A, B, C, D, E, F, G, H> setFirst(final A first) {
		this.first = first;
		return this;
	}

	public Octuple<A, B, C, D, E, F, G, H> setFourth(final D fourth) {
		this.fourth = fourth;
		return this;
	}

	public Octuple<A, B, C, D, E, F, G, H> setSecond(final B second) {
		this.second = second;
		return this;
	}

	public Octuple<A, B, C, D, E, F, G, H> setSeventh(final G seventh) {
		this.seventh = seventh;
		return this;
	}

	public Octuple<A, B, C, D, E, F, G, H> setSixth(final F sixth) {
		this.sixth = sixth;
		return this;
	}

	public Octuple<A, B, C, D, E, F, G, H> setThird(final C third) {
		this.third = third;
		return this;
	}

	@Override
	public String toString() {
		return String.format("{%s,%s,%s,%s,%s,%s,%s,%s}",
				this.first,
				this.second,
				this.third,
				this.fourth,
				this.fifth,
				this.sixth,
				this.seventh,
				this.eighth);
	}

}
