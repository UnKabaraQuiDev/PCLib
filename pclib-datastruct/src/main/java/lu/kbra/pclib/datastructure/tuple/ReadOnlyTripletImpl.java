package lu.kbra.pclib.datastructure.tuple;

import java.util.Objects;
import java.util.function.Function;

import lu.kbra.pclib.datastructure.DeepCloneable;
import lu.kbra.pclib.impl.function.TriFunction;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReadOnlyTripletImpl<A, B, C> implements ReadOnlyTriplet<A, B, C> {

	private final A first;
	private final B second;
	private final C third;

	@Override
	public Object[] asArray() {
		return new Object[] { this.first, this.second, this.third };
	}

	@Override
	public Triplet<A, B, C> clone() {
		return this.cloneReadOnly();
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
	public ReadOnlyTriplet<A, B, C> deepClone() {
		return this.deepCloneReadOnly();
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
	public <T> T get(final int i) {
		switch (i) {
		case 0:
			return (T) this.first;
		case 1:
			return (T) this.second;
		case 2:
			return (T) this.third;
		default:
			throw new IndexOutOfBoundsException("Index: " + i + " out of range: [0, 2]");
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
	public <T, N, O> ReadOnlyTriplet<T, N, O> map(
			final TriFunction<A, B, C, T> funcFirst,
			final TriFunction<A, B, C, N> funcSecond,
			final TriFunction<A, B, C, O> funcThird) {

		return this
				.map((a, b, c) -> new ReadOnlyTripletImpl<>(funcFirst.apply(a, b, c), funcSecond.apply(a, b, c), funcThird.apply(a, b, c)));
	}

	@Override
	public <T> ReadOnlyTriplet<T, B, C> mapFirst(final TriFunction<A, B, C, T> func) {
		return this.map((a, b, c) -> new ReadOnlyTripletImpl<>(func.apply(a, b, c), b, c));
	}

	@Override
	public <T> ReadOnlyTriplet<A, T, C> mapSecond(final TriFunction<A, B, C, T> func) {
		return this.map((a, b, c) -> new ReadOnlyTripletImpl<>(a, func.apply(a, b, c), c));
	}

	@Override
	public <T> ReadOnlyTriplet<A, B, T> mapThird(final TriFunction<A, B, C, T> func) {
		return this.map((a, b, c) -> new ReadOnlyTripletImpl<>(a, b, func.apply(a, b, c)));
	}

	@Override
	public <T> ReadOnlyTriplet<?, ?, ?> map(final int i, final Function<Object, T> func) {
		switch (i) {
		case 0:
			return new ReadOnlyTripletImpl<>(func.apply(this.first), this.second, this.third);

		case 1:
			return new ReadOnlyTripletImpl<>(this.first, func.apply(this.second), this.third);

		case 2:
			return new ReadOnlyTripletImpl<>(this.first, this.second, func.apply(this.third));

		default:
			throw new IndexOutOfBoundsException("Index: " + i + " out of range: [0, 2]");
		}
	}

	@Override
	public String toString() {
		return String.format("{%s, %s, %s}(readonly)", this.first, this.second, this.third);
	}

}
