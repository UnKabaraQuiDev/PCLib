package lu.kbra.pclib.datastructure.tuple;

import java.util.Objects;
import java.util.function.Function;

import lu.kbra.pclib.impl.function.SeptFunction;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReadOnlySeptuple<A, B, C, D, E, F, G> implements Septuple<A, B, C, D, E, F, G>, ReadOnlyTuple {

	private final A first;
	private final B second;
	private final C third;
	private final D fourth;
	private final E fifth;
	private final F sixth;
	private final G seventh;

	@Override
	public Object[] asArray() {
		return new Object[] { this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh };
	}

	@Override
	public Septuple<A, B, C, D, E, F, G> clone() {
		return new ReadOnlySeptuple<>(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh);
	}

	@Override
	public EditableSeptuple<A, B, C, D, E, F, G> cloneEditable() {
		return new EditableSeptuple<>(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh);
	}

	@Override
	public ReadOnlySeptuple<A, B, C, D, E, F, G> cloneReadOnly() {
		return new ReadOnlySeptuple<>(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh);
	}

	@Override
	public final int elementCount() {
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

		final Septuple<?, ?, ?, ?, ?, ?, ?> other = (Septuple<?, ?, ?, ?, ?, ?, ?>) obj;

		return Objects.equals(this.first, other.getFirst()) && Objects.equals(this.second, other.getSecond())
				&& Objects.equals(this.third, other.getThird()) && Objects.equals(this.fourth, other.getFourth())
				&& Objects.equals(this.fifth, other.getFifth()) && Objects.equals(this.sixth, other.getSixth())
				&& Objects.equals(this.seventh, other.getSeventh());
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
		default:
			throw new IndexOutOfBoundsException("Index: " + i + " out of range: [0, 6]");
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh);
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
	public <T> T map(final SeptFunction<A, B, C, D, E, F, G, T> func) {
		return func.apply(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh);
	}

	@Override
	public <T, N, O, P, Q, R, S> ReadOnlySeptuple<T, N, O, P, Q, R, S> map(
			final SeptFunction<A, B, C, D, E, F, G, T> funcFirst,
			final SeptFunction<A, B, C, D, E, F, G, N> funcSecond,
			final SeptFunction<A, B, C, D, E, F, G, O> funcThird,
			final SeptFunction<A, B, C, D, E, F, G, P> funcFourth,
			final SeptFunction<A, B, C, D, E, F, G, Q> funcFifth,
			final SeptFunction<A, B, C, D, E, F, G, R> funcSixth,
			final SeptFunction<A, B, C, D, E, F, G, S> funcSeventh) {

		return new ReadOnlySeptuple<>(
				funcFirst.apply(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh),
				funcSecond.apply(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh),
				funcThird.apply(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh),
				funcFourth.apply(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh),
				funcFifth.apply(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh),
				funcSixth.apply(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh),
				funcSeventh.apply(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh));
	}

	@Override
	public <T> ReadOnlySeptuple<T, B, C, D, E, F, G> mapFirst(final SeptFunction<A, B, C, D, E, F, G, T> func) {
		return new ReadOnlySeptuple<>(func.apply(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh),
				this.second,
				this.third,
				this.fourth,
				this.fifth,
				this.sixth,
				this.seventh);
	}

	@Override
	public <T> ReadOnlySeptuple<A, T, C, D, E, F, G> mapSecond(final SeptFunction<A, B, C, D, E, F, G, T> func) {
		return new ReadOnlySeptuple<>(this.first,
				func.apply(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh),
				this.third,
				this.fourth,
				this.fifth,
				this.sixth,
				this.seventh);
	}

	@Override
	public <T> ReadOnlySeptuple<A, B, T, D, E, F, G> mapThird(final SeptFunction<A, B, C, D, E, F, G, T> func) {
		return new ReadOnlySeptuple<>(this.first,
				this.second,
				func.apply(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh),
				this.fourth,
				this.fifth,
				this.sixth,
				this.seventh);
	}

	@Override
	public <T> ReadOnlySeptuple<A, B, C, T, E, F, G> mapFourth(final SeptFunction<A, B, C, D, E, F, G, T> func) {
		return new ReadOnlySeptuple<>(this.first,
				this.second,
				this.third,
				func.apply(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh),
				this.fifth,
				this.sixth,
				this.seventh);
	}

	@Override
	public <T> ReadOnlySeptuple<A, B, C, D, T, F, G> mapFifth(final SeptFunction<A, B, C, D, E, F, G, T> func) {
		return new ReadOnlySeptuple<>(this.first,
				this.second,
				this.third,
				this.fourth,
				func.apply(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh),
				this.sixth,
				this.seventh);
	}

	@Override
	public <T> ReadOnlySeptuple<A, B, C, D, E, T, G> mapSixth(final SeptFunction<A, B, C, D, E, F, G, T> func) {
		return new ReadOnlySeptuple<>(this.first,
				this.second,
				this.third,
				this.fourth,
				this.fifth,
				func.apply(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh),
				this.seventh);
	}

	@Override
	public <T> ReadOnlySeptuple<A, B, C, D, E, F, T> mapSeventh(final SeptFunction<A, B, C, D, E, F, G, T> func) {
		return new ReadOnlySeptuple<>(this.first,
				this.second,
				this.third,
				this.fourth,
				this.fifth,
				this.sixth,
				func.apply(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh));
	}

	@Override
	public <T> ReadOnlySeptuple<?, ?, ?, ?, ?, ?, ?> map(final int i, final Function<Object, T> func) {
		switch (i) {
		case 0:
			return new ReadOnlySeptuple<>(func
					.apply(this.first), this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh);
		case 1:
			return new ReadOnlySeptuple<>(this.first,
					func.apply(this.second),
					this.third,
					this.fourth,
					this.fifth,
					this.sixth,
					this.seventh);
		case 2:
			return new ReadOnlySeptuple<>(this.first,
					this.second,
					func.apply(this.third),
					this.fourth,
					this.fifth,
					this.sixth,
					this.seventh);
		case 3:
			return new ReadOnlySeptuple<>(this.first,
					this.second,
					this.third,
					func.apply(this.fourth),
					this.fifth,
					this.sixth,
					this.seventh);
		case 4:
			return new ReadOnlySeptuple<>(this.first,
					this.second,
					this.third,
					this.fourth,
					func.apply(this.fifth),
					this.sixth,
					this.seventh);
		case 5:
			return new ReadOnlySeptuple<>(this.first,
					this.second,
					this.third,
					this.fourth,
					this.fifth,
					func.apply(this.sixth),
					this.seventh);
		case 6:
			return new ReadOnlySeptuple<>(this.first,
					this.second,
					this.third,
					this.fourth,
					this.fifth,
					this.sixth,
					func.apply(this.seventh));
		default:
			throw new IndexOutOfBoundsException("Index: " + i + " out of range: [0, 6]");
		}
	}

	@Override
	public String toString() {
		return String.format("{%s, %s, %s, %s, %s, %s, %s}(readonly)",
				this.first,
				this.second,
				this.third,
				this.fourth,
				this.fifth,
				this.sixth,
				this.seventh);
	}

}
