package lu.kbra.pclib.datastructure.tuple;

import java.util.Objects;
import java.util.function.Function;

import lu.kbra.pclib.impl.function.QuintFunction;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReadOnlyQuintuple<A, B, C, D, E> implements Quintuple<A, B, C, D, E>, ReadOnlyTuple {

	private final A first;
	private final B second;
	private final C third;
	private final D fourth;
	private final E fifth;

	@Override
	public Object[] asArray() {
		return new Object[] { this.first, this.second, this.third, this.fourth, this.fifth };
	}

	@Override
	public Quintuple<A, B, C, D, E> clone() {
		return new ReadOnlyQuintuple<>(this.first, this.second, this.third, this.fourth, this.fifth);
	}

	@Override
	public EditableQuintuple<A, B, C, D, E> cloneEditable() {
		return new EditableQuintuple<>(this.first, this.second, this.third, this.fourth, this.fifth);
	}

	@Override
	public ReadOnlyQuintuple<A, B, C, D, E> cloneReadOnly() {
		return new ReadOnlyQuintuple<>(this.first, this.second, this.third, this.fourth, this.fifth);
	}

	@Override
	public final int elementCount() {
		return 5;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || this.getClass() != obj.getClass()) {
			return false;
		}
		final Quintuple<?, ?, ?, ?, ?> other = (Quintuple<?, ?, ?, ?, ?>) obj;
		return Objects.equals(this.first, other.getFirst()) && Objects.equals(this.second, other.getSecond())
				&& Objects.equals(this.third, other.getThird()) && Objects.equals(this.fourth, other.getFourth())
				&& Objects.equals(this.fifth, other.getFifth());
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
		default:
			throw new IndexOutOfBoundsException("Index: " + i + " out of range: [0, 4]");
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.first, this.second, this.third, this.fourth, this.fifth);
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
	public <T> T map(final QuintFunction<A, B, C, D, E, T> func) {
		return func.apply(this.first, this.second, this.third, this.fourth, this.fifth);
	}

	@Override
	public <T, N, O, P, Q> ReadOnlyQuintuple<T, N, O, P, Q> map(
			final QuintFunction<A, B, C, D, E, T> funcFirst,
			final QuintFunction<A, B, C, D, E, N> funcSecond,
			final QuintFunction<A, B, C, D, E, O> funcThird,
			final QuintFunction<A, B, C, D, E, P> funcFourth,
			final QuintFunction<A, B, C, D, E, Q> funcFifth) {

		return this.map((a, b, c, d, e) -> new ReadOnlyQuintuple<>(funcFirst.apply(a, b, c, d, e),
				funcSecond.apply(a, b, c, d, e),
				funcThird.apply(a, b, c, d, e),
				funcFourth.apply(a, b, c, d, e),
				funcFifth.apply(a, b, c, d, e)));
	}

	@Override
	public <T> ReadOnlyQuintuple<T, B, C, D, E> mapFirst(final QuintFunction<A, B, C, D, E, T> func) {
		return this.map((a, b, c, d, e) -> new ReadOnlyQuintuple<>(func.apply(a, b, c, d, e), b, c, d, e));
	}

	@Override
	public <T> ReadOnlyQuintuple<A, T, C, D, E> mapSecond(final QuintFunction<A, B, C, D, E, T> func) {
		return this.map((a, b, c, d, e) -> new ReadOnlyQuintuple<>(a, func.apply(a, b, c, d, e), c, d, e));
	}

	@Override
	public <T> ReadOnlyQuintuple<A, B, T, D, E> mapThird(final QuintFunction<A, B, C, D, E, T> func) {
		return this.map((a, b, c, d, e) -> new ReadOnlyQuintuple<>(a, b, func.apply(a, b, c, d, e), d, e));
	}

	@Override
	public <T> ReadOnlyQuintuple<A, B, C, T, E> mapFourth(final QuintFunction<A, B, C, D, E, T> func) {
		return this.map((a, b, c, d, e) -> new ReadOnlyQuintuple<>(a, b, c, func.apply(a, b, c, d, e), e));
	}

	@Override
	public <T> ReadOnlyQuintuple<A, B, C, D, T> mapFifth(final QuintFunction<A, B, C, D, E, T> func) {
		return this.map((a, b, c, d, e) -> new ReadOnlyQuintuple<>(a, b, c, d, func.apply(a, b, c, d, e)));
	}

	@Override
	public <T> ReadOnlyQuintuple<?, ?, ?, ?, ?> map(final int i, final Function<Object, T> func) {
		switch (i) {
		case 0:
			return new ReadOnlyQuintuple<>(func.apply(this.first), this.second, this.third, this.fourth, this.fifth);
		case 1:
			return new ReadOnlyQuintuple<>(this.first, func.apply(this.second), this.third, this.fourth, this.fifth);
		case 2:
			return new ReadOnlyQuintuple<>(this.first, this.second, func.apply(this.third), this.fourth, this.fifth);
		case 3:
			return new ReadOnlyQuintuple<>(this.first, this.second, this.third, func.apply(this.fourth), this.fifth);
		case 4:
			return new ReadOnlyQuintuple<>(this.first, this.second, this.third, this.fourth, func.apply(this.fifth));
		default:
			throw new IndexOutOfBoundsException("Index: " + i + " out of range: [0, 4]");
		}
	}

	@Override
	public String toString() {
		return String.format("{%s, %s, %s, %s, %s}(readonly)", this.first, this.second, this.third, this.fourth, this.fifth);
	}

}
