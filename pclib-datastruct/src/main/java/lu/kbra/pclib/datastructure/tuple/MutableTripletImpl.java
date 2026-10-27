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
public class MutableTripletImpl<A, B, C> implements MutableTriplet<A, B, C> {

	protected A first;
	protected B second;
	protected C third;

	@Override
	public Object[] asArray() {
		return new Object[] { this.first, this.second, this.third };
	}

	@Override
	public Triplet<A, B, C> clone() {
		return this.cloneMutable();
	}

	@Override
	public MutableTriplet<A, B, C> cloneMutable() {
		return new MutableTripletImpl<>(this.first, this.second, this.third);
	}

	@Override
	public ReadOnlyTriplet<A, B, C> cloneReadOnly() {
		return new ReadOnlyTripletImpl<>(this.first, this.second, this.third);
	}

	@Override
	public MutableTriplet<A, B, C> deepClone() {
		return this.deepCloneMutable();
	}

	@Override
	public MutableTriplet<A, B, C> deepCloneMutable() {
		return new MutableTripletImpl<>(DeepCloneable.deepClone(this.first),
				DeepCloneable.deepClone(this.second),
				DeepCloneable.deepClone(this.third));
	}

	@Override
	public ReadOnlyTriplet<A, B, C> deepCloneReadOnly() {
		return new ReadOnlyTripletImpl<>(DeepCloneable.deepClone(this.first),
				DeepCloneable.deepClone(this.second),
				DeepCloneable.deepClone(this.third));
	}

	@Override
	public int elementCount() {
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
	public <T, U, V> MutableTriplet<T, U, V> map(
			final TriFunction<A, B, C, T> funcFirst,
			final TriFunction<A, B, C, U> funcSecond,
			final TriFunction<A, B, C, V> funcThird) {

		return this
				.map((a, b, c) -> new MutableTripletImpl<>(funcFirst.apply(a, b, c), funcSecond.apply(a, b, c), funcThird.apply(a, b, c)));
	}

	@Override
	public <T> MutableTriplet<T, B, C> mapFirst(final TriFunction<A, B, C, T> func) {
		return this.map((a, b, c) -> new MutableTripletImpl<>(func.apply(a, b, c), b, c));
	}

	@Override
	public <T> MutableTriplet<A, T, C> mapSecond(final TriFunction<A, B, C, T> func) {
		return this.map((a, b, c) -> new MutableTripletImpl<>(a, func.apply(a, b, c), c));
	}

	@Override
	public <T> MutableTriplet<A, B, T> mapThird(final TriFunction<A, B, C, T> func) {
		return this.map((a, b, c) -> new MutableTripletImpl<>(a, b, func.apply(a, b, c)));
	}

	@Override
	public <T> MutableTriplet<?, ?, ?> map(final int index, final Function<Object, T> func) {
		switch (index) {
		case 0:
			return new MutableTripletImpl<>(func.apply(this.first), this.second, this.third);
		case 1:
			return new MutableTripletImpl<>(this.first, func.apply(this.second), this.third);
		case 2:
			return new MutableTripletImpl<>(this.first, this.second, func.apply(this.third));
		default:
			throw new IndexOutOfBoundsException("Index: " + index + " out of range: [0, 2]");
		}
	}

	@Override
	public MutableTriplet<A, B, C> mut(
			final TriFunction<A, B, C, A> funcFirst,
			final TriFunction<A, B, C, B> funcSecond,
			final TriFunction<A, B, C, C> funcThird) {

		this.first = funcFirst.apply(this.first, this.second, this.third);
		this.second = funcSecond.apply(this.first, this.second, this.third);
		this.third = funcThird.apply(this.first, this.second, this.third);

		return this;
	}

	@Override
	public MutableTriplet<A, B, C> mutFirst(final TriFunction<A, B, C, A> func) {
		this.first = func.apply(this.first, this.second, this.third);
		return this;
	}

	@Override
	public MutableTriplet<A, B, C> mutSecond(final TriFunction<A, B, C, B> func) {
		this.second = func.apply(this.first, this.second, this.third);
		return this;
	}

	@Override
	public MutableTriplet<A, B, C> mutThird(final TriFunction<A, B, C, C> func) {
		this.third = func.apply(this.first, this.second, this.third);
		return this;
	}

	@Override
	public <T> MutableTriplet<A, B, C> mut(final int index, final Function<Object, T> func) {
		this.set(index, func.apply(this.get(index)));
		return this;
	}

	@Override
	public Triplet<A, B, C> setFirst(final A first) {
		this.first = first;
		return this;
	}

	@Override
	public Triplet<A, B, C> setSecond(final B second) {
		this.second = second;
		return this;
	}

	@Override
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
	public <T> MutableTriplet<A, B, C> set(final int index, final T element) {
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

	@Override
	public MutableTriplet<A, B, C> asSynchronized() {
		return new MutableTripletSyncImpl<>(this.first, this.second, this.third);
	}

}
