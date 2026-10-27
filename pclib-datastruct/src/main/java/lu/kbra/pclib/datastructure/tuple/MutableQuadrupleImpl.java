package lu.kbra.pclib.datastructure.tuple;

import java.util.Objects;
import java.util.function.Function;

import lu.kbra.pclib.datastructure.DeepCloneable;
import lu.kbra.pclib.impl.function.QuadFunction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MutableQuadrupleImpl<A, B, C, D> implements MutableQuadruple<A, B, C, D> {

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
		return this.cloneMutable();
	}

	@Override
	public MutableQuadruple<A, B, C, D> cloneMutable() {
		return new MutableQuadrupleImpl<>(this.first, this.second, this.third, this.fourth);
	}

	@Override
	public ReadOnlyQuadruple<A, B, C, D> cloneReadOnly() {
		return new ReadOnlyQuadrupleImpl<>(this.first, this.second, this.third, this.fourth);
	}

	@Override
	public MutableQuadruple<A, B, C, D> deepClone() {
		return this.deepCloneMutable();
	}

	@Override
	public MutableQuadruple<A, B, C, D> deepCloneMutable() {
		return new MutableQuadrupleImpl<>(DeepCloneable.deepClone(this.first),
				DeepCloneable.deepClone(this.second),
				DeepCloneable.deepClone(this.third),
				DeepCloneable.deepClone(this.fourth));
	}

	@Override
	public ReadOnlyQuadruple<A, B, C, D> deepCloneReadOnly() {
		return new ReadOnlyQuadrupleImpl<>(DeepCloneable.deepClone(this.first),
				DeepCloneable.deepClone(this.second),
				DeepCloneable.deepClone(this.third),
				DeepCloneable.deepClone(this.fourth));
	}

	@Override
	public int elementCount() {
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
	public <T, U, V, W> MutableQuadruple<T, U, V, W> map(
			final QuadFunction<A, B, C, D, T> funcFirst,
			final QuadFunction<A, B, C, D, U> funcSecond,
			final QuadFunction<A, B, C, D, V> funcThird,
			final QuadFunction<A, B, C, D, W> funcFourth) {

		return this.map((a, b, c, d) -> new MutableQuadrupleImpl<>(funcFirst.apply(a, b, c, d),
				funcSecond.apply(a, b, c, d),
				funcThird.apply(a, b, c, d),
				funcFourth.apply(a, b, c, d)));
	}

	@Override
	public <T> MutableQuadruple<T, B, C, D> mapFirst(final QuadFunction<A, B, C, D, T> func) {
		return this.map((a, b, c, d) -> new MutableQuadrupleImpl<>(func.apply(a, b, c, d), b, c, d));
	}

	@Override
	public <T> MutableQuadruple<A, T, C, D> mapSecond(final QuadFunction<A, B, C, D, T> func) {
		return this.map((a, b, c, d) -> new MutableQuadrupleImpl<>(a, func.apply(a, b, c, d), c, d));
	}

	@Override
	public <T> MutableQuadruple<A, B, T, D> mapThird(final QuadFunction<A, B, C, D, T> func) {
		return this.map((a, b, c, d) -> new MutableQuadrupleImpl<>(a, b, func.apply(a, b, c, d), d));
	}

	@Override
	public <T> MutableQuadruple<A, B, C, T> mapFourth(final QuadFunction<A, B, C, D, T> func) {
		return this.map((a, b, c, d) -> new MutableQuadrupleImpl<>(a, b, c, func.apply(a, b, c, d)));
	}

	@Override
	public <T> MutableQuadruple<?, ?, ?, ?> map(final int index, final Function<Object, T> func) {
		switch (index) {
		case 0:
			return new MutableQuadrupleImpl<>(func.apply(this.first), this.second, this.third, this.fourth);
		case 1:
			return new MutableQuadrupleImpl<>(this.first, func.apply(this.second), this.third, this.fourth);
		case 2:
			return new MutableQuadrupleImpl<>(this.first, this.second, func.apply(this.third), this.fourth);
		case 3:
			return new MutableQuadrupleImpl<>(this.first, this.second, this.third, func.apply(this.fourth));
		default:
			throw new IndexOutOfBoundsException("Index: " + index + " out of range: [0, 3]");
		}
	}

	@Override
	public MutableQuadruple<A, B, C, D> mut(
			final QuadFunction<A, B, C, D, A> funcFirst,
			final QuadFunction<A, B, C, D, B> funcSecond,
			final QuadFunction<A, B, C, D, C> funcThird,
			final QuadFunction<A, B, C, D, D> funcFourth) {

		this.first = funcFirst.apply(this.first, this.second, this.third, this.fourth);
		this.second = funcSecond.apply(this.first, this.second, this.third, this.fourth);
		this.third = funcThird.apply(this.first, this.second, this.third, this.fourth);
		this.fourth = funcFourth.apply(this.first, this.second, this.third, this.fourth);

		return this;
	}

	@Override
	public MutableQuadruple<A, B, C, D> mutFirst(final QuadFunction<A, B, C, D, A> func) {
		this.first = func.apply(this.first, this.second, this.third, this.fourth);
		return this;
	}

	@Override
	public MutableQuadruple<A, B, C, D> mutSecond(final QuadFunction<A, B, C, D, B> func) {
		this.second = func.apply(this.first, this.second, this.third, this.fourth);
		return this;
	}

	@Override
	public MutableQuadruple<A, B, C, D> mutThird(final QuadFunction<A, B, C, D, C> func) {
		this.third = func.apply(this.first, this.second, this.third, this.fourth);
		return this;
	}

	@Override
	public MutableQuadruple<A, B, C, D> mutFourth(final QuadFunction<A, B, C, D, D> func) {
		this.fourth = func.apply(this.first, this.second, this.third, this.fourth);
		return this;
	}

	@Override
	public <T> MutableQuadruple<A, B, C, D> mut(final int index, final Function<Object, T> func) {
		this.set(index, func.apply(this.get(index)));
		return this;
	}

	@Override
	public Quadruple<A, B, C, D> setFirst(final A first) {
		this.first = first;
		return this;
	}

	@Override
	public Quadruple<A, B, C, D> setSecond(final B second) {
		this.second = second;
		return this;
	}

	@Override
	public Quadruple<A, B, C, D> setThird(final C third) {
		this.third = third;
		return this;
	}

	@Override
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
	public <T> MutableQuadruple<A, B, C, D> set(final int index, final T element) {
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

	@Override
	public MutableQuadruple<A, B, C, D> asSynchronized() {
		return new MutableQuadrupleSyncImpl<>(this.first, this.second, this.third, this.fourth);
	}

}
