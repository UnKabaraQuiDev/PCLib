package lu.kbra.pclib.datastructure.tuple;

import java.util.Objects;
import java.util.function.Function;

import lu.kbra.pclib.datastructure.DeepCloneable;
import lu.kbra.pclib.impl.function.OctFunction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MutableOctupleImpl<A, B, C, D, E, F, G, H> implements MutableOctuple<A, B, C, D, E, F, G, H> {

	protected A first;
	protected B second;
	protected C third;
	protected D fourth;
	protected E fifth;
	protected F sixth;
	protected G seventh;
	protected H eighth;

	@Override
	public Object[] asArray() {
		return new Object[] { this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh, this.eighth };
	}

	@Override
	public MutableOctuple<A, B, C, D, E, F, G, H> clone() {
		return this.cloneMutable();
	}

	@Override
	public MutableOctuple<A, B, C, D, E, F, G, H> cloneMutable() {
		return new MutableOctupleImpl<>(this.first,
				this.second,
				this.third,
				this.fourth,
				this.fifth,
				this.sixth,
				this.seventh,
				this.eighth);
	}

	@Override
	public ReadOnlyOctuple<A, B, C, D, E, F, G, H> cloneReadOnly() {
		return new ReadOnlyOctupleImpl<>(this.first,
				this.second,
				this.third,
				this.fourth,
				this.fifth,
				this.sixth,
				this.seventh,
				this.eighth);
	}

	@Override
	public MutableOctuple<A, B, C, D, E, F, G, H> deepClone() {
		return this.deepCloneMutable();
	}

	@Override
	public MutableOctuple<A, B, C, D, E, F, G, H> deepCloneMutable() {
		return new MutableOctupleImpl<>(DeepCloneable.deepClone(this.first),
				DeepCloneable.deepClone(this.second),
				DeepCloneable.deepClone(this.third),
				DeepCloneable.deepClone(this.fourth),
				DeepCloneable.deepClone(this.fifth),
				DeepCloneable.deepClone(this.sixth),
				DeepCloneable.deepClone(this.seventh),
				DeepCloneable.deepClone(this.eighth));
	}

	@Override
	public ReadOnlyOctuple<A, B, C, D, E, F, G, H> deepCloneReadOnly() {
		return new ReadOnlyOctupleImpl<>(DeepCloneable.deepClone(this.first),
				DeepCloneable.deepClone(this.second),
				DeepCloneable.deepClone(this.third),
				DeepCloneable.deepClone(this.fourth),
				DeepCloneable.deepClone(this.fifth),
				DeepCloneable.deepClone(this.sixth),
				DeepCloneable.deepClone(this.seventh),
				DeepCloneable.deepClone(this.eighth));
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
		if (obj == null || this.getClass() != obj.getClass()) {
			return false;
		}

		final Octuple<?, ?, ?, ?, ?, ?, ?, ?> other = (Octuple<?, ?, ?, ?, ?, ?, ?, ?>) obj;

		return Objects.equals(this.first, other.getFirst()) && Objects.equals(this.second, other.getSecond())
				&& Objects.equals(this.third, other.getThird()) && Objects.equals(this.fourth, other.getFourth())
				&& Objects.equals(this.fifth, other.getFifth()) && Objects.equals(this.sixth, other.getSixth())
				&& Objects.equals(this.seventh, other.getSeventh()) && Objects.equals(this.eighth, other.getEighth());
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T get(final int i) {
		switch (i) {
		case 0:
			return (T) this.first;
		case 1:
			return (T) this.second;
		case 2:
			return (T) this.third;
		case 3:
			return (T) this.fourth;
		case 4:
			return (T) this.fifth;
		case 5:
			return (T) this.sixth;
		case 6:
			return (T) this.seventh;
		case 7:
			return (T) this.eighth;
		default:
			throw new IndexOutOfBoundsException("Index: " + i + " out of range: [0, 7]");
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh, this.eighth);
	}

	@Override
	public boolean hasFirst() {
		return this.first != null;
	}

	@Override
	public boolean hasSecond() {
		return this.second != null;
	}

	@Override
	public boolean hasThird() {
		return this.third != null;
	}

	@Override
	public boolean hasFourth() {
		return this.fourth != null;
	}

	@Override
	public boolean hasFifth() {
		return this.fifth != null;
	}

	@Override
	public boolean hasSixth() {
		return this.sixth != null;
	}

	@Override
	public boolean hasSeventh() {
		return this.seventh != null;
	}

	@Override
	public boolean hasEighth() {
		return this.eighth != null;
	}

	@Override
	public <T> T map(final OctFunction<A, B, C, D, E, F, G, H, T> func) {
		return func.apply(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh, this.eighth);
	}

	@Override
	public <T, N, O, P, Q, R, S, U> MutableOctuple<T, N, O, P, Q, R, S, U> map(
			final OctFunction<A, B, C, D, E, F, G, H, T> funcFirst,
			final OctFunction<A, B, C, D, E, F, G, H, N> funcSecond,
			final OctFunction<A, B, C, D, E, F, G, H, O> funcThird,
			final OctFunction<A, B, C, D, E, F, G, H, P> funcFourth,
			final OctFunction<A, B, C, D, E, F, G, H, Q> funcFifth,
			final OctFunction<A, B, C, D, E, F, G, H, R> funcSixth,
			final OctFunction<A, B, C, D, E, F, G, H, S> funcSeventh,
			final OctFunction<A, B, C, D, E, F, G, H, U> funcEighth) {

		return new MutableOctupleImpl<>(
				funcFirst.apply(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh, this.eighth),
				funcSecond.apply(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh, this.eighth),
				funcThird.apply(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh, this.eighth),
				funcFourth.apply(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh, this.eighth),
				funcFifth.apply(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh, this.eighth),
				funcSixth.apply(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh, this.eighth),
				funcSeventh.apply(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh, this.eighth),
				funcEighth.apply(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh, this.eighth));
	}

	@Override
	public <T> MutableOctuple<T, B, C, D, E, F, G, H> mapFirst(final OctFunction<A, B, C, D, E, F, G, H, T> func) {
		return new MutableOctupleImpl<>(
				func.apply(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh, this.eighth),
				this.second,
				this.third,
				this.fourth,
				this.fifth,
				this.sixth,
				this.seventh,
				this.eighth);
	}

	@Override
	public <T> MutableOctuple<A, T, C, D, E, F, G, H> mapSecond(final OctFunction<A, B, C, D, E, F, G, H, T> func) {
		return new MutableOctupleImpl<>(this.first,
				func.apply(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh, this.eighth),
				this.third,
				this.fourth,
				this.fifth,
				this.sixth,
				this.seventh,
				this.eighth);
	}

	@Override
	public <T> MutableOctuple<A, B, T, D, E, F, G, H> mapThird(final OctFunction<A, B, C, D, E, F, G, H, T> func) {
		return new MutableOctupleImpl<>(this.first,
				this.second,
				func.apply(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh, this.eighth),
				this.fourth,
				this.fifth,
				this.sixth,
				this.seventh,
				this.eighth);
	}

	@Override
	public <T> MutableOctuple<A, B, C, T, E, F, G, H> mapFourth(final OctFunction<A, B, C, D, E, F, G, H, T> func) {
		return new MutableOctupleImpl<>(this.first,
				this.second,
				this.third,
				func.apply(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh, this.eighth),
				this.fifth,
				this.sixth,
				this.seventh,
				this.eighth);
	}

	@Override
	public <T> MutableOctuple<A, B, C, D, T, F, G, H> mapFifth(final OctFunction<A, B, C, D, E, F, G, H, T> func) {
		return new MutableOctupleImpl<>(this.first,
				this.second,
				this.third,
				this.fourth,
				func.apply(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh, this.eighth),
				this.sixth,
				this.seventh,
				this.eighth);
	}

	@Override
	public <T> MutableOctuple<A, B, C, D, E, T, G, H> mapSixth(final OctFunction<A, B, C, D, E, F, G, H, T> func) {
		return new MutableOctupleImpl<>(this.first,
				this.second,
				this.third,
				this.fourth,
				this.fifth,
				func.apply(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh, this.eighth),
				this.seventh,
				this.eighth);
	}

	@Override
	public <T> MutableOctuple<A, B, C, D, E, F, T, H> mapSeventh(final OctFunction<A, B, C, D, E, F, G, H, T> func) {
		return new MutableOctupleImpl<>(this.first,
				this.second,
				this.third,
				this.fourth,
				this.fifth,
				this.sixth,
				func.apply(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh, this.eighth),
				this.eighth);
	}

	@Override
	public <T> MutableOctuple<A, B, C, D, E, F, G, T> mapEighth(final OctFunction<A, B, C, D, E, F, G, H, T> func) {
		return new MutableOctupleImpl<>(this.first,
				this.second,
				this.third,
				this.fourth,
				this.fifth,
				this.sixth,
				this.seventh,
				func.apply(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh, this.eighth));
	}

	@Override
	public <T> MutableOctuple<?, ?, ?, ?, ?, ?, ?, ?> map(final int index, final Function<Object, T> func) {
		switch (index) {
		case 0:
			return new MutableOctupleImpl<>(func
					.apply(this.first), this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh, this.eighth);
		case 1:
			return new MutableOctupleImpl<>(this.first,
					func.apply(this.second),
					this.third,
					this.fourth,
					this.fifth,
					this.sixth,
					this.seventh,
					this.eighth);
		case 2:
			return new MutableOctupleImpl<>(this.first,
					this.second,
					func.apply(this.third),
					this.fourth,
					this.fifth,
					this.sixth,
					this.seventh,
					this.eighth);
		case 3:
			return new MutableOctupleImpl<>(this.first,
					this.second,
					this.third,
					func.apply(this.fourth),
					this.fifth,
					this.sixth,
					this.seventh,
					this.eighth);
		case 4:
			return new MutableOctupleImpl<>(this.first,
					this.second,
					this.third,
					this.fourth,
					func.apply(this.fifth),
					this.sixth,
					this.seventh,
					this.eighth);
		case 5:
			return new MutableOctupleImpl<>(this.first,
					this.second,
					this.third,
					this.fourth,
					this.fifth,
					func.apply(this.sixth),
					this.seventh,
					this.eighth);
		case 6:
			return new MutableOctupleImpl<>(this.first,
					this.second,
					this.third,
					this.fourth,
					this.fifth,
					this.sixth,
					func.apply(this.seventh),
					this.eighth);
		case 7:
			return new MutableOctupleImpl<>(this.first,
					this.second,
					this.third,
					this.fourth,
					this.fifth,
					this.sixth,
					this.seventh,
					func.apply(this.eighth));
		default:
			throw new IndexOutOfBoundsException("Index: " + index + " out of range: [0, 7]");
		}
	}

	@Override
	public MutableOctuple<A, B, C, D, E, F, G, H> mut(
			final OctFunction<A, B, C, D, E, F, G, H, A> funcFirst,
			final OctFunction<A, B, C, D, E, F, G, H, B> funcSecond,
			final OctFunction<A, B, C, D, E, F, G, H, C> funcThird,
			final OctFunction<A, B, C, D, E, F, G, H, D> funcFourth,
			final OctFunction<A, B, C, D, E, F, G, H, E> funcFifth,
			final OctFunction<A, B, C, D, E, F, G, H, F> funcSixth,
			final OctFunction<A, B, C, D, E, F, G, H, G> funcSeventh,
			final OctFunction<A, B, C, D, E, F, G, H, H> funcEighth) {

		this.first = funcFirst.apply(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh, this.eighth);
		this.second = funcSecond.apply(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh, this.eighth);
		this.third = funcThird.apply(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh, this.eighth);
		this.fourth = funcFourth.apply(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh, this.eighth);
		this.fifth = funcFifth.apply(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh, this.eighth);
		this.sixth = funcSixth.apply(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh, this.eighth);
		this.seventh = funcSeventh
				.apply(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh, this.eighth);
		this.eighth = funcEighth.apply(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh, this.eighth);

		return this;
	}

	@Override
	public MutableOctuple<A, B, C, D, E, F, G, H> mutFirst(final OctFunction<A, B, C, D, E, F, G, H, A> func) {
		this.first = func.apply(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh, this.eighth);
		return this;
	}

	@Override
	public MutableOctuple<A, B, C, D, E, F, G, H> mutSecond(final OctFunction<A, B, C, D, E, F, G, H, B> func) {
		this.second = func.apply(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh, this.eighth);
		return this;
	}

	@Override
	public MutableOctuple<A, B, C, D, E, F, G, H> mutThird(final OctFunction<A, B, C, D, E, F, G, H, C> func) {
		this.third = func.apply(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh, this.eighth);
		return this;
	}

	@Override
	public MutableOctuple<A, B, C, D, E, F, G, H> mutFourth(final OctFunction<A, B, C, D, E, F, G, H, D> func) {
		this.fourth = func.apply(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh, this.eighth);
		return this;
	}

	@Override
	public MutableOctuple<A, B, C, D, E, F, G, H> mutFifth(final OctFunction<A, B, C, D, E, F, G, H, E> func) {
		this.fifth = func.apply(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh, this.eighth);
		return this;
	}

	@Override
	public MutableOctuple<A, B, C, D, E, F, G, H> mutSixth(final OctFunction<A, B, C, D, E, F, G, H, F> func) {
		this.sixth = func.apply(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh, this.eighth);
		return this;
	}

	@Override
	public MutableOctuple<A, B, C, D, E, F, G, H> mutSeventh(final OctFunction<A, B, C, D, E, F, G, H, G> func) {
		this.seventh = func.apply(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh, this.eighth);
		return this;
	}

	@Override
	public MutableOctuple<A, B, C, D, E, F, G, H> mutEighth(final OctFunction<A, B, C, D, E, F, G, H, H> func) {
		this.eighth = func.apply(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh, this.eighth);
		return this;
	}

	@Override
	public <T> MutableOctuple<A, B, C, D, E, F, G, H> mut(final int index, final Function<Object, T> func) {
		this.set(index, func.apply(this.get(index)));
		return this;
	}

	@Override
	public MutableOctuple<A, B, C, D, E, F, G, H> setFirst(final A first) {
		this.first = first;
		return this;
	}

	@Override
	public MutableOctuple<A, B, C, D, E, F, G, H> setSecond(final B second) {
		this.second = second;
		return this;
	}

	@Override
	public MutableOctuple<A, B, C, D, E, F, G, H> setThird(final C third) {
		this.third = third;
		return this;
	}

	@Override
	public MutableOctuple<A, B, C, D, E, F, G, H> setFourth(final D fourth) {
		this.fourth = fourth;
		return this;
	}

	@Override
	public MutableOctuple<A, B, C, D, E, F, G, H> setFifth(final E fifth) {
		this.fifth = fifth;
		return this;
	}

	@Override
	public MutableOctuple<A, B, C, D, E, F, G, H> setSixth(final F sixth) {
		this.sixth = sixth;
		return this;
	}

	@Override
	public MutableOctuple<A, B, C, D, E, F, G, H> setSeventh(final G seventh) {
		this.seventh = seventh;
		return this;
	}

	@Override
	public MutableOctuple<A, B, C, D, E, F, G, H> setEighth(final H eighth) {
		this.eighth = eighth;
		return this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> MutableOctuple<A, B, C, D, E, F, G, H> set(final int i, final T value) {
		switch (i) {
		case 0:
			this.first = (A) value;
			break;
		case 1:
			this.second = (B) value;
			break;
		case 2:
			this.third = (C) value;
			break;
		case 3:
			this.fourth = (D) value;
			break;
		case 4:
			this.fifth = (E) value;
			break;
		case 5:
			this.sixth = (F) value;
			break;
		case 6:
			this.seventh = (G) value;
			break;
		case 7:
			this.eighth = (H) value;
			break;
		default:
			throw new IndexOutOfBoundsException("Index: " + i + " out of range: [0, 7]");
		}

		return this;
	}

	@Override
	public String toString() {
		return String.format("{%s, %s, %s, %s, %s, %s, %s, %s}",
				this.first,
				this.second,
				this.third,
				this.fourth,
				this.fifth,
				this.sixth,
				this.seventh,
				this.eighth);
	}

	@Override
	public MutableOctuple<A, B, C, D, E, F, G, H> asSynchronized() {
		return new MutableOctupleSyncImpl<>(this.first,
				this.second,
				this.third,
				this.fourth,
				this.fifth,
				this.sixth,
				this.seventh,
				this.eighth);
	}

	@Override
	public MutableOctuple<A, B, C, D, E, F, G, H> asWeakViewReadOnly() {
		return new MutableOctupleWeakView<>(this);
	}

}
