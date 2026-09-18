package lu.kbra.pclib.datastructure.tuple;

import java.util.Objects;
import java.util.function.Function;

import lu.kbra.pclib.impl.function.QuadFunction;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReadOnlyQuadruple<A, B, C, D> implements Quadruple<A, B, C, D>, ReadOnlyTuple {

	private final A first;
	private final B second;
	private final C third;
	private final D fourth;

	@Override
	public Object[] asArray() {
		return new Object[] { this.first, this.second, this.third, this.fourth };
	}

	@Override
	public Quadruple<A, B, C, D> clone() {
		return new ReadOnlyQuadruple<>(this.first, this.second, this.third, this.fourth);
	}

	@Override
	public EditableQuadruple<A, B, C, D> cloneEditable() {
		return new EditableQuadruple<>(this.first, this.second, this.third, this.fourth);
	}

	@Override
	public ReadOnlyQuadruple<A, B, C, D> cloneReadOnly() {
		return new ReadOnlyQuadruple<>(this.first, this.second, this.third, this.fourth);
	}

	@Override
	public final int elementCount() {
		return 4;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || this.getClass() != obj.getClass()) {
			return false;
		}
		final Quadruple<?, ?, ?, ?> other = (Quadruple<?, ?, ?, ?>) obj;
		return Objects.equals(this.first, other.getFirst()) && Objects.equals(this.second, other.getSecond())
				&& Objects.equals(this.third, other.getThird()) && Objects.equals(this.fourth, other.getFourth());
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
		default:
			throw new IndexOutOfBoundsException("Index: " + i + " out of range: [0, 3]");
		}
	}

	@Override
	public A getFirst() {
		return this.first;
	}

	@Override
	public B getSecond() {
		return this.second;
	}

	@Override
	public C getThird() {
		return this.third;
	}

	@Override
	public D getFourth() {
		return this.fourth;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.first, this.second, this.third, this.fourth);
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
	public <T> T map(final QuadFunction<A, B, C, D, T> func) {
		return func.apply(this.first, this.second, this.third, this.fourth);
	}

	@Override
	public <T, N, O, P> ReadOnlyQuadruple<T, N, O, P> map(
			final QuadFunction<A, B, C, D, T> funcFirst,
			final QuadFunction<A, B, C, D, N> funcSecond,
			final QuadFunction<A, B, C, D, O> funcThird,
			final QuadFunction<A, B, C, D, P> funcFourth) {

		return this.map((a, b, c, d) -> new ReadOnlyQuadruple<>(funcFirst.apply(a, b, c, d),
				funcSecond.apply(a, b, c, d),
				funcThird.apply(a, b, c, d),
				funcFourth.apply(a, b, c, d)));
	}

	@Override
	public <T> ReadOnlyQuadruple<T, B, C, D> mapFirst(final QuadFunction<A, B, C, D, T> func) {
		return this.map((a, b, c, d) -> new ReadOnlyQuadruple<>(func.apply(a, b, c, d), b, c, d));
	}

	@Override
	public <T> ReadOnlyQuadruple<A, T, C, D> mapSecond(final QuadFunction<A, B, C, D, T> func) {
		return this.map((a, b, c, d) -> new ReadOnlyQuadruple<>(a, func.apply(a, b, c, d), c, d));
	}

	@Override
	public <T> ReadOnlyQuadruple<A, B, T, D> mapThird(final QuadFunction<A, B, C, D, T> func) {
		return this.map((a, b, c, d) -> new ReadOnlyQuadruple<>(a, b, func.apply(a, b, c, d), d));
	}

	@Override
	public <T> ReadOnlyQuadruple<A, B, C, T> mapFourth(final QuadFunction<A, B, C, D, T> func) {
		return this.map((a, b, c, d) -> new ReadOnlyQuadruple<>(a, b, c, func.apply(a, b, c, d)));
	}

	@Override
	public <T> ReadOnlyQuadruple<?, ?, ?, ?> map(final int i, final Function<Object, T> func) {
		switch (i) {
		case 0:
			return new ReadOnlyQuadruple<>(func.apply(this.first), this.second, this.third, this.fourth);
		case 1:
			return new ReadOnlyQuadruple<>(this.first, func.apply(this.second), this.third, this.fourth);
		case 2:
			return new ReadOnlyQuadruple<>(this.first, this.second, func.apply(this.third), this.fourth);
		case 3:
			return new ReadOnlyQuadruple<>(this.first, this.second, this.third, func.apply(this.fourth));
		default:
			throw new IndexOutOfBoundsException("Index: " + i + " out of range: [0, 3]");
		}
	}

	@Override
	public String toString() {
		return String.format("{%s, %s, %s, %s}(readonly)", this.first, this.second, this.third, this.fourth);
	}

}
