package lu.kbra.pclib.datastructure.tuple;

import java.util.Objects;
import java.util.function.Function;

import lu.kbra.pclib.datastructure.DeepCloneable;
import lu.kbra.pclib.impl.function.TriFunction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EditableTriplet<A, B, C> implements Triplet<A, B, C>, EditableTuple {

	protected A first;
	protected B second;
	protected C third;

	@Override
	public Object[] asArray() {
		return new Object[] { this.first, this.second, this.third };
	}

	@Override
	public Triplet<A, B, C> clone() {
		return this.cloneEditable();
	}

	@Override
	public EditableTriplet<A, B, C> cloneEditable() {
		return new EditableTriplet<>(this.first, this.second, this.third);
	}

	@Override
	public ReadOnlyTriplet<A, B, C> cloneReadOnly() {
		return new ReadOnlyTriplet<>(this.first, this.second, this.third);
	}

	@Override
	public EditableTriplet<A, B, C> deepClone() {
		return this.deepCloneEditable();
	}

	@Override
	public EditableTriplet<A, B, C> deepCloneEditable() {
		return new EditableTriplet<>(DeepCloneable.deepClone(this.first),
				DeepCloneable.deepClone(this.second),
				DeepCloneable.deepClone(this.third));
	}

	@Override
	public ReadOnlyTriplet<A, B, C> deepCloneReadOnly() {
		return new ReadOnlyTriplet<>(DeepCloneable.deepClone(this.first),
				DeepCloneable.deepClone(this.second),
				DeepCloneable.deepClone(this.third));
	}

	@Override
	public final int elementCount() {
		return 3;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || this.getClass() != obj.getClass()) {
			return false;
		}

		final Triplet<?, ?, ?> other = (Triplet<?, ?, ?>) obj;
		return Objects.equals(this.first, other.getFirst()) && Objects.equals(this.second, other.getSecond())
				&& Objects.equals(this.third, other.getThird());
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
		default:
			throw new IndexOutOfBoundsException("Index: " + index + " out of range: [0, 2]");
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.first, this.second, this.third);
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
	public <T> T map(final TriFunction<A, B, C, T> func) {
		return func.apply(this.first, this.second, this.third);
	}

	@Override
	public <T, U, V> EditableTriplet<T, U, V> map(
			final TriFunction<A, B, C, T> funcFirst,
			final TriFunction<A, B, C, U> funcSecond,
			final TriFunction<A, B, C, V> funcThird) {

		return this.map((a, b, c) -> new EditableTriplet<>(funcFirst.apply(a, b, c), funcSecond.apply(a, b, c), funcThird.apply(a, b, c)));
	}

	@Override
	public <T> EditableTriplet<T, B, C> mapFirst(final TriFunction<A, B, C, T> func) {
		return this.map((a, b, c) -> new EditableTriplet<>(func.apply(a, b, c), b, c));
	}

	@Override
	public <T> EditableTriplet<A, T, C> mapSecond(final TriFunction<A, B, C, T> func) {
		return this.map((a, b, c) -> new EditableTriplet<>(a, func.apply(a, b, c), c));
	}

	@Override
	public <T> EditableTriplet<A, B, T> mapThird(final TriFunction<A, B, C, T> func) {
		return this.map((a, b, c) -> new EditableTriplet<>(a, b, func.apply(a, b, c)));
	}

	@Override
	public <T> EditableTriplet<?, ?, ?> map(final int index, final Function<Object, T> func) {
		switch (index) {
		case 0:
			return new EditableTriplet<>(func.apply(this.first), this.second, this.third);
		case 1:
			return new EditableTriplet<>(this.first, func.apply(this.second), this.third);
		case 2:
			return new EditableTriplet<>(this.first, this.second, func.apply(this.third));
		default:
			throw new IndexOutOfBoundsException("Index: " + index + " out of range: [0, 2]");
		}
	}

	public EditableTriplet<A, B, C> mut(
			final TriFunction<A, B, C, A> funcFirst,
			final TriFunction<A, B, C, B> funcSecond,
			final TriFunction<A, B, C, C> funcThird) {

		this.first = funcFirst.apply(this.first, this.second, this.third);
		this.second = funcSecond.apply(this.first, this.second, this.third);
		this.third = funcThird.apply(this.first, this.second, this.third);

		return this;
	}

	public EditableTriplet<A, B, C> mutFirst(final TriFunction<A, B, C, A> func) {
		this.first = func.apply(this.first, this.second, this.third);
		return this;
	}

	public EditableTriplet<A, B, C> mutSecond(final TriFunction<A, B, C, B> func) {
		this.second = func.apply(this.first, this.second, this.third);
		return this;
	}

	public EditableTriplet<A, B, C> mutThird(final TriFunction<A, B, C, C> func) {
		this.third = func.apply(this.first, this.second, this.third);
		return this;
	}

	@Override
	public <T> EditableTriplet<A, B, C> mut(final int index, final Function<Object, T> func) {
		this.set(index, func.apply(this.get(index)));
		return this;
	}

	public Triplet<A, B, C> setFirst(final A first) {
		this.first = first;
		return this;
	}

	public Triplet<A, B, C> setSecond(final B second) {
		this.second = second;
		return this;
	}

	public Triplet<A, B, C> setThird(final C third) {
		this.third = third;
		return this;
	}

	@Override
	public String toString() {
		return String.format("{%s, %s, %s}", this.first, this.second, this.third);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> EditableTriplet<A, B, C> set(final int index, final T element) {
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
		default:
			throw new IndexOutOfBoundsException("Index: " + index + " out of range: [0, 2]");
		}

		return this;
	}

}
