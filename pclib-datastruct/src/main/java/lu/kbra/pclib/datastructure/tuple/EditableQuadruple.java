package lu.kbra.pclib.datastructure.tuple;

import java.util.Objects;
import java.util.function.Function;

import lu.kbra.pclib.impl.function.QuadFunction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EditableQuadruple<A, B, C, D> implements Quadruple<A, B, C, D>, EditableTuple {

	protected A first;
	protected B second;
	protected C third;
	protected D fourth;

	@Override
	public Object[] asArray() {
		return new Object[] { this.first, this.second, this.third, this.fourth };
	}

	@Override
	public Quadruple<A, B, C, D> clone() {
		return new EditableQuadruple<>(this.first, this.second, this.third, this.fourth);
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
	public <T> T get(final int index) {
		switch (index) {
		case 0:
			return (T) this.first;
		case 1:
			return (T) this.second;
		case 2:
			return (T) this.third;
		case 3:
			return (T) this.fourth;
		default:
			throw new IndexOutOfBoundsException("Index: " + index + " out of range: [0, 3]");
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
	public <T, U, V, W> EditableQuadruple<T, U, V, W> map(
			final QuadFunction<A, B, C, D, T> funcFirst,
			final QuadFunction<A, B, C, D, U> funcSecond,
			final QuadFunction<A, B, C, D, V> funcThird,
			final QuadFunction<A, B, C, D, W> funcFourth) {

		return this.map((a, b, c, d) -> new EditableQuadruple<>(funcFirst.apply(a, b, c, d),
				funcSecond.apply(a, b, c, d),
				funcThird.apply(a, b, c, d),
				funcFourth.apply(a, b, c, d)));
	}

	@Override
	public <T> EditableQuadruple<T, B, C, D> mapFirst(final QuadFunction<A, B, C, D, T> func) {
		return this.map((a, b, c, d) -> new EditableQuadruple<>(func.apply(a, b, c, d), b, c, d));
	}

	@Override
	public <T> EditableQuadruple<A, T, C, D> mapSecond(final QuadFunction<A, B, C, D, T> func) {
		return this.map((a, b, c, d) -> new EditableQuadruple<>(a, func.apply(a, b, c, d), c, d));
	}

	@Override
	public <T> EditableQuadruple<A, B, T, D> mapThird(final QuadFunction<A, B, C, D, T> func) {
		return this.map((a, b, c, d) -> new EditableQuadruple<>(a, b, func.apply(a, b, c, d), d));
	}

	@Override
	public <T> EditableQuadruple<A, B, C, T> mapFourth(final QuadFunction<A, B, C, D, T> func) {
		return this.map((a, b, c, d) -> new EditableQuadruple<>(a, b, c, func.apply(a, b, c, d)));
	}

	@Override
	public <T> EditableQuadruple<?, ?, ?, ?> map(final int index, final Function<Object, T> func) {
		final EditableQuadruple<?, ?, ?, ?> ret = new EditableQuadruple<>(this.first, this.second, this.third, this.fourth);
		ret.set(index, func.apply(ret.get(index)));
		return ret;
	}

	public Quadruple<A, B, C, D> setFirst(final A first) {
		this.first = first;
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

	public Quadruple<A, B, C, D> setFourth(final D fourth) {
		this.fourth = fourth;
		return this;
	}

	@Override
	public String toString() {
		return String.format("{%s, %s, %s, %s}", this.first, this.second, this.third, this.fourth);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> EditableQuadruple<A, B, C, D> set(final int index, final T element) {
		switch (index) {
		case 0:
			this.first = (A) element;
			break;
		case 1:
			this.second = (B) element;
			break;
		case 2:
			this.third = (C) element;
			break;
		case 3:
			this.fourth = (D) element;
			break;
		default:
			throw new IndexOutOfBoundsException("Index: " + index + " out of range: [0, 3]");
		}

		return this;
	}
}
