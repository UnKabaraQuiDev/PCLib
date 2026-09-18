package lu.kbra.pclib.datastructure.tuple;

import java.util.Objects;
import java.util.function.Function;

import lu.kbra.pclib.impl.function.SextFunction;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReadOnlySextuple<A, B, C, D, E, F> implements Sextuple<A, B, C, D, E, F>, ReadOnlyTuple {

	private final A first;
	private final B second;
	private final C third;
	private final D fourth;
	private final E fifth;
	private final F sixth;

	@Override
	public Object[] asArray() {
		return new Object[] { this.first, this.second, this.third, this.fourth, this.fifth, this.sixth };
	}

	@Override
	public Sextuple<A, B, C, D, E, F> clone() {
		return new ReadOnlySextuple<>(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth);
	}

	@Override
	public EditableSextuple<A, B, C, D, E, F> cloneEditable() {
		return new EditableSextuple<>(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth);
	}

	@Override
	public ReadOnlySextuple<A, B, C, D, E, F> cloneReadOnly() {
		return new ReadOnlySextuple<>(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth);
	}

	@Override
	public final int elementCount() {
		return 6;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || this.getClass() != obj.getClass()) {
			return false;
		}
		final Sextuple<?, ?, ?, ?, ?, ?> other = (Sextuple<?, ?, ?, ?, ?, ?>) obj;
		return Objects.equals(this.first, other.getFirst()) && Objects.equals(this.second, other.getSecond())
				&& Objects.equals(this.third, other.getThird()) && Objects.equals(this.fourth, other.getFourth())
				&& Objects.equals(this.fifth, other.getFifth()) && Objects.equals(this.sixth, other.getSixth());
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
		default:
			throw new IndexOutOfBoundsException("Index: " + i + " out of range: [0, 5]");
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth);
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
	public <T> T map(final SextFunction<A, B, C, D, E, F, T> func) {
		return func.apply(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth);
	}

	@Override
	public <T, N, O, P, Q, R> ReadOnlySextuple<T, N, O, P, Q, R> map(
			final SextFunction<A, B, C, D, E, F, T> funcFirst,
			final SextFunction<A, B, C, D, E, F, N> funcSecond,
			final SextFunction<A, B, C, D, E, F, O> funcThird,
			final SextFunction<A, B, C, D, E, F, P> funcFourth,
			final SextFunction<A, B, C, D, E, F, Q> funcFifth,
			final SextFunction<A, B, C, D, E, F, R> funcSixth) {

		return this.map((a, b, c, d, e, f) -> new ReadOnlySextuple<>(funcFirst.apply(a, b, c, d, e, f),
				funcSecond.apply(a, b, c, d, e, f),
				funcThird.apply(a, b, c, d, e, f),
				funcFourth.apply(a, b, c, d, e, f),
				funcFifth.apply(a, b, c, d, e, f),
				funcSixth.apply(a, b, c, d, e, f)));
	}

	@Override
	public <T> ReadOnlySextuple<T, B, C, D, E, F> mapFirst(final SextFunction<A, B, C, D, E, F, T> func) {
		return this.map((a, b, c, d, e, f) -> new ReadOnlySextuple<>(func.apply(a, b, c, d, e, f), b, c, d, e, f));
	}

	@Override
	public <T> ReadOnlySextuple<A, T, C, D, E, F> mapSecond(final SextFunction<A, B, C, D, E, F, T> func) {
		return this.map((a, b, c, d, e, f) -> new ReadOnlySextuple<>(a, func.apply(a, b, c, d, e, f), c, d, e, f));
	}

	@Override
	public <T> ReadOnlySextuple<A, B, T, D, E, F> mapThird(final SextFunction<A, B, C, D, E, F, T> func) {
		return this.map((a, b, c, d, e, f) -> new ReadOnlySextuple<>(a, b, func.apply(a, b, c, d, e, f), d, e, f));
	}

	@Override
	public <T> ReadOnlySextuple<A, B, C, T, E, F> mapFourth(final SextFunction<A, B, C, D, E, F, T> func) {
		return this.map((a, b, c, d, e, f) -> new ReadOnlySextuple<>(a, b, c, func.apply(a, b, c, d, e, f), e, f));
	}

	@Override
	public <T> ReadOnlySextuple<A, B, C, D, T, F> mapFifth(final SextFunction<A, B, C, D, E, F, T> func) {
		return this.map((a, b, c, d, e, f) -> new ReadOnlySextuple<>(a, b, c, d, func.apply(a, b, c, d, e, f), f));
	}

	@Override
	public <T> ReadOnlySextuple<A, B, C, D, E, T> mapSixth(final SextFunction<A, B, C, D, E, F, T> func) {
		return this.map((a, b, c, d, e, f) -> new ReadOnlySextuple<>(a, b, c, d, e, func.apply(a, b, c, d, e, f)));
	}

	@Override
	public <T> ReadOnlySextuple<?, ?, ?, ?, ?, ?> map(final int i, final Function<Object, T> func) {
		switch (i) {
		case 0:
			return new ReadOnlySextuple<>(func.apply(this.first), this.second, this.third, this.fourth, this.fifth, this.sixth);
		case 1:
			return new ReadOnlySextuple<>(this.first, func.apply(this.second), this.third, this.fourth, this.fifth, this.sixth);
		case 2:
			return new ReadOnlySextuple<>(this.first, this.second, func.apply(this.third), this.fourth, this.fifth, this.sixth);
		case 3:
			return new ReadOnlySextuple<>(this.first, this.second, this.third, func.apply(this.fourth), this.fifth, this.sixth);
		case 4:
			return new ReadOnlySextuple<>(this.first, this.second, this.third, this.fourth, func.apply(this.fifth), this.sixth);
		case 5:
			return new ReadOnlySextuple<>(this.first, this.second, this.third, this.fourth, this.fifth, func.apply(this.sixth));
		default:
			throw new IndexOutOfBoundsException("Index: " + i + " out of range: [0, 5]");
		}
	}

	@Override
	public String toString() {
		return String
				.format("{%s, %s, %s, %s, %s, %s}(readonly)", this.first, this.second, this.third, this.fourth, this.fifth, this.sixth);
	}

}
