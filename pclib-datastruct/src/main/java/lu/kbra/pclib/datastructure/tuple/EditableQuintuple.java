package lu.kbra.pclib.datastructure.tuple;

import java.util.Objects;
import java.util.function.Function;

import lu.kbra.pclib.datastructure.DeepCloneable;
import lu.kbra.pclib.impl.function.QuintFunction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EditableQuintuple<A, B, C, D, E> implements Quintuple<A, B, C, D, E>, EditableTuple {

	protected A first;
	protected B second;
	protected C third;
	protected D fourth;
	protected E fifth;

	@Override
	public Object[] asArray() {
		return new Object[] { this.first, this.second, this.third, this.fourth, this.fifth };
	}

	@Override
	public Quintuple<A, B, C, D, E> clone() {
		return this.cloneEditable();
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
	public EditableQuintuple<A, B, C, D, E> deepClone() {
		return this.deepCloneEditable();
	}

	@Override
	public EditableQuintuple<A, B, C, D, E> deepCloneEditable() {
		return new EditableQuintuple<>(DeepCloneable.deepClone(this.first),
				DeepCloneable.deepClone(this.second),
				DeepCloneable.deepClone(this.third),
				DeepCloneable.deepClone(this.fourth),
				DeepCloneable.deepClone(this.fifth));
	}

	@Override
	public ReadOnlyQuintuple<A, B, C, D, E> deepCloneReadOnly() {
		return new ReadOnlyQuintuple<>(DeepCloneable.deepClone(this.first),
				DeepCloneable.deepClone(this.second),
				DeepCloneable.deepClone(this.third),
				DeepCloneable.deepClone(this.fourth),
				DeepCloneable.deepClone(this.fifth));
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
		case 4:
			return (T) this.fifth;
		default:
			throw new IndexOutOfBoundsException("Index: " + index + " out of range: [0, 4]");
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
	public <T, U, V, W, X> EditableQuintuple<T, U, V, W, X> map(
			final QuintFunction<A, B, C, D, E, T> funcFirst,
			final QuintFunction<A, B, C, D, E, U> funcSecond,
			final QuintFunction<A, B, C, D, E, V> funcThird,
			final QuintFunction<A, B, C, D, E, W> funcFourth,
			final QuintFunction<A, B, C, D, E, X> funcFifth) {

		return this.map((a, b, c, d, e) -> new EditableQuintuple<>(funcFirst.apply(a, b, c, d, e),
				funcSecond.apply(a, b, c, d, e),
				funcThird.apply(a, b, c, d, e),
				funcFourth.apply(a, b, c, d, e),
				funcFifth.apply(a, b, c, d, e)));
	}

	@Override
	public <T> EditableQuintuple<T, B, C, D, E> mapFirst(final QuintFunction<A, B, C, D, E, T> func) {
		return this.map((a, b, c, d, e) -> new EditableQuintuple<>(func.apply(a, b, c, d, e), b, c, d, e));
	}

	@Override
	public <T> EditableQuintuple<A, T, C, D, E> mapSecond(final QuintFunction<A, B, C, D, E, T> func) {
		return this.map((a, b, c, d, e) -> new EditableQuintuple<>(a, func.apply(a, b, c, d, e), c, d, e));
	}

	@Override
	public <T> EditableQuintuple<A, B, T, D, E> mapThird(final QuintFunction<A, B, C, D, E, T> func) {
		return this.map((a, b, c, d, e) -> new EditableQuintuple<>(a, b, func.apply(a, b, c, d, e), d, e));
	}

	@Override
	public <T> EditableQuintuple<A, B, C, T, E> mapFourth(final QuintFunction<A, B, C, D, E, T> func) {
		return this.map((a, b, c, d, e) -> new EditableQuintuple<>(a, b, c, func.apply(a, b, c, d, e), e));
	}

	@Override
	public <T> EditableQuintuple<A, B, C, D, T> mapFifth(final QuintFunction<A, B, C, D, E, T> func) {
		return this.map((a, b, c, d, e) -> new EditableQuintuple<>(a, b, c, d, func.apply(a, b, c, d, e)));
	}

	@Override
	public <T> EditableQuintuple<?, ?, ?, ?, ?> map(final int index, final Function<Object, T> func) {
		switch (index) {
		case 0:
			return new EditableQuintuple<>(func.apply(this.first), this.second, this.third, this.fourth, this.fifth);
		case 1:
			return new EditableQuintuple<>(this.first, func.apply(this.second), this.third, this.fourth, this.fifth);
		case 2:
			return new EditableQuintuple<>(this.first, this.second, func.apply(this.third), this.fourth, this.fifth);
		case 3:
			return new EditableQuintuple<>(this.first, this.second, this.third, func.apply(this.fourth), this.fifth);
		case 4:
			return new EditableQuintuple<>(this.first, this.second, this.third, this.fourth, func.apply(this.fifth));
		default:
			throw new IndexOutOfBoundsException("Index: " + index + " out of range: [0, 4]");
		}
	}

	public EditableQuintuple<A, B, C, D, E> mut(
			final QuintFunction<A, B, C, D, E, A> funcFirst,
			final QuintFunction<A, B, C, D, E, B> funcSecond,
			final QuintFunction<A, B, C, D, E, C> funcThird,
			final QuintFunction<A, B, C, D, E, D> funcFourth,
			final QuintFunction<A, B, C, D, E, E> funcFifth) {

		this.first = funcFirst.apply(this.first, this.second, this.third, this.fourth, this.fifth);
		this.second = funcSecond.apply(this.first, this.second, this.third, this.fourth, this.fifth);
		this.third = funcThird.apply(this.first, this.second, this.third, this.fourth, this.fifth);
		this.fourth = funcFourth.apply(this.first, this.second, this.third, this.fourth, this.fifth);
		this.fifth = funcFifth.apply(this.first, this.second, this.third, this.fourth, this.fifth);

		return this;
	}

	public EditableQuintuple<A, B, C, D, E> mutFirst(final QuintFunction<A, B, C, D, E, A> func) {
		this.first = func.apply(this.first, this.second, this.third, this.fourth, this.fifth);
		return this;
	}

	public EditableQuintuple<A, B, C, D, E> mutSecond(final QuintFunction<A, B, C, D, E, B> func) {
		this.second = func.apply(this.first, this.second, this.third, this.fourth, this.fifth);
		return this;
	}

	public EditableQuintuple<A, B, C, D, E> mutThird(final QuintFunction<A, B, C, D, E, C> func) {
		this.third = func.apply(this.first, this.second, this.third, this.fourth, this.fifth);
		return this;
	}

	public EditableQuintuple<A, B, C, D, E> mutFourth(final QuintFunction<A, B, C, D, E, D> func) {
		this.fourth = func.apply(this.first, this.second, this.third, this.fourth, this.fifth);
		return this;
	}

	public EditableQuintuple<A, B, C, D, E> mutFifth(final QuintFunction<A, B, C, D, E, E> func) {
		this.fifth = func.apply(this.first, this.second, this.third, this.fourth, this.fifth);
		return this;
	}

	@Override
	public <T> EditableQuintuple<A, B, C, D, E> mut(final int index, final Function<Object, T> func) {
		this.set(index, func.apply(this.get(index)));
		return this;
	}

	public Quintuple<A, B, C, D, E> setFirst(final A first) {
		this.first = first;
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

	public Quintuple<A, B, C, D, E> setFourth(final D fourth) {
		this.fourth = fourth;
		return this;
	}

	public Quintuple<A, B, C, D, E> setFifth(final E fifth) {
		this.fifth = fifth;
		return this;
	}

	@Override
	public String toString() {
		return String.format("{%s, %s, %s, %s, %s}", this.first, this.second, this.third, this.fourth, this.fifth);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> EditableQuintuple<A, B, C, D, E> set(final int index, final T element) {
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
		case 4:
			this.fifth = (E) element;
			break;
		default:
			throw new IndexOutOfBoundsException("Index: " + index + " out of range: [0, 4]");
		}

		return this;
	}
}
