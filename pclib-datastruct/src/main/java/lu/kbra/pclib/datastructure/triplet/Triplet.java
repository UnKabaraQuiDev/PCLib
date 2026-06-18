package lu.kbra.pclib.datastructure.triplet;

import lu.kbra.pclib.datastructure.DeepCloneable;
import lu.kbra.pclib.datastructure.tuple.Tuple;
import lu.kbra.pclib.impl.TriFunction;

public class Triplet<A, B, C> implements DeepCloneable, Tuple {

	protected A first;
	protected B second;
	protected C third;

	public Triplet() {
		this(null, null, null);
	}

	public Triplet(final A first, final B second, final C third) {
		this.first = first;
		this.second = second;
		this.third = third;
	}

	@Override
	public Object[] asArray() {
		return new Object[] { this.first, this.second, this.third };
	}

	@Override
	public Triplet<A, B, C> clone() {
		try {
			return (Triplet<A, B, C>) super.clone();
		} catch (final CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int elementCount() {
		return 3;
	}

	@Override
	public <T> T get(final int i) {
		if (i < 0 || i > 2) {
			throw new IndexOutOfBoundsException(i + " <> [0..2]");
		}
		return i == 0 ? (T) this.first
				: i == 1 ? (T) this.second
				: (T) this.third;
	}

	public A getFirst() {
		return this.first;
	}

	public B getSecond() {
		return this.second;
	}

	public C getThird() {
		return this.third;
	}

	public <T> T map(final TriFunction<A, B, C, T> tri) {
		return tri.apply(this.first, this.second, this.third);
	}

	public <T> Triplet<T, B, C> mapFirst(final TriFunction<A, B, C, T> func) {
		return map((a, b, c) -> new Triplet<>(func.apply(a, b, c), b, c));
	}

	public <T> Triplet<A, T, C> mapSecond(final TriFunction<A, B, C, T> func) {
		return map((a, b, c) -> new Triplet<>(a, func.apply(a, b, c), c));
	}

	public <T> Triplet<A, B, T> mapThird(final TriFunction<A, B, C, T> func) {
		return map((a, b, c) -> new Triplet<>(a, b, func.apply(a, b, c)));
	}

	public <T, U, V> Triplet<T, U, V> map(
			final TriFunction<A, B, C, T> funcFirst,
			final TriFunction<A, B, C, U> funcSecond,
			final TriFunction<A, B, C, V> funcThird) {

		return map((a, b, c) -> new Triplet<>(funcFirst.apply(a, b, c), funcSecond.apply(a, b, c), funcThird.apply(a, b, c)));
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
		return String.format("{%s,%s,%s}", this.first, this.second, this.third);
	}

}
