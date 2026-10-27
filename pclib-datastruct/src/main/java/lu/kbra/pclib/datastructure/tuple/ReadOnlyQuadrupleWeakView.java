package lu.kbra.pclib.datastructure.tuple;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.function.Function;

import lu.kbra.pclib.impl.function.QuadFunction;

public final class ReadOnlyQuadrupleWeakView<A, B, C, D> implements Quadruple<A, B, C, D>, WeakTupleView {

	private final WeakReference<Quadruple<A, B, C, D>> parent;

	public ReadOnlyQuadrupleWeakView(final Quadruple<A, B, C, D> parent) {
		this.parent = new WeakReference<>(Objects.requireNonNull(parent, "parent"));
	}

	@Override
	public WeakReference<? extends Tuple> getParentReference() {
		return this.parent;
	}

	@Override
	public Quadruple<A, B, C, D> getParent() {
		final Quadruple<A, B, C, D> value = this.parent.get();
		if (value == null) {
			throw new IllegalStateException("The tuple parent has been garbage-collected");
		}
		return value;
	}

	@Override
	public Object[] asArray() {
		return this.getParent().asArray();
	}

	@Override
	public Quadruple<A, B, C, D> clone() {
		return this.getParent().clone();
	}

	@Override
	public MutableQuadruple<A, B, C, D> cloneMutable() {
		return this.getParent().cloneMutable();
	}

	@Override
	public ReadOnlyQuadruple<A, B, C, D> cloneReadOnly() {
		return this.getParent().cloneReadOnly();
	}

	@Override
	public Quadruple<A, B, C, D> deepClone() {
		return this.getParent().deepClone();
	}

	@Override
	public MutableQuadruple<A, B, C, D> deepCloneMutable() {
		return this.getParent().deepCloneMutable();
	}

	@Override
	public ReadOnlyQuadruple<A, B, C, D> deepCloneReadOnly() {
		return this.getParent().deepCloneReadOnly();
	}

	@Override
	public int elementCount() {
		return this.getParent().elementCount();
	}

	@Override
	public <T> T get(final int i) {
		return this.getParent().get(i);
	}

	@Override
	public boolean hasFirst() {
		return this.getParent().hasFirst();
	}

	@Override
	public boolean hasSecond() {
		return this.getParent().hasSecond();
	}

	@Override
	public boolean hasThird() {
		return this.getParent().hasThird();
	}

	@Override
	public boolean hasFourth() {
		return this.getParent().hasFourth();
	}

	@Override
	public <T> T map(final QuadFunction<A, B, C, D, T> func) {
		return this.getParent().map(func);
	}

	@Override
	public <T, N, O, P> Quadruple<T, N, O, P> map(
			final QuadFunction<A, B, C, D, T> funcFirst,
			final QuadFunction<A, B, C, D, N> funcSecond,
			final QuadFunction<A, B, C, D, O> funcThird,
			final QuadFunction<A, B, C, D, P> funcFourth) {
		return this.getParent().map(funcFirst, funcSecond, funcThird, funcFourth);
	}

	@Override
	public <T> Quadruple<T, B, C, D> mapFirst(final QuadFunction<A, B, C, D, T> func) {
		return this.getParent().mapFirst(func);
	}

	@Override
	public <T> Quadruple<A, T, C, D> mapSecond(final QuadFunction<A, B, C, D, T> func) {
		return this.getParent().mapSecond(func);
	}

	@Override
	public <T> Quadruple<A, B, T, D> mapThird(final QuadFunction<A, B, C, D, T> func) {
		return this.getParent().mapThird(func);
	}

	@Override
	public <T> Quadruple<A, B, C, T> mapFourth(final QuadFunction<A, B, C, D, T> func) {
		return this.getParent().mapFourth(func);
	}

	@Override
	public <T> Quadruple<?, ?, ?, ?> map(final int i, final Function<Object, T> func) {
		return this.getParent().map(i, func);
	}

	@Override
	public boolean isView() {
		return true;
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public Quadruple<A, B, C, D> asViewReadOnly() {
		return new ReadOnlyQuadrupleView<>(this);
	}

	@Override
	public Quadruple<A, B, C, D> asWeakViewReadOnly() {
		return new ReadOnlyQuadrupleWeakView<>(this);
	}

	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		return other instanceof TupleView && this.getParent().equals(((TupleView) other).getParent());
	}

	@Override
	public int hashCode() {
		return this.getParent().hashCode();
	}

	@Override
	public String toString() {
		return this.getParent().toString();
	}

	@Override
	public A getFirst() {
		return this.getParent().getFirst();
	}

	@Override
	public B getSecond() {
		return this.getParent().getSecond();
	}

	@Override
	public C getThird() {
		return this.getParent().getThird();
	}

	@Override
	public D getFourth() {
		return this.getParent().getFourth();
	}

}
