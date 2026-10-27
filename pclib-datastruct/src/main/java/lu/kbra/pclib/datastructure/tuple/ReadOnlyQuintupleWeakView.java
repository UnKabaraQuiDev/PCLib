package lu.kbra.pclib.datastructure.tuple;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.function.Function;

import lu.kbra.pclib.impl.function.QuintFunction;

public final class ReadOnlyQuintupleWeakView<A, B, C, D, E> implements Quintuple<A, B, C, D, E>, WeakTupleView {

	private final WeakReference<Quintuple<A, B, C, D, E>> parent;

	public ReadOnlyQuintupleWeakView(final Quintuple<A, B, C, D, E> parent) {
		this.parent = new WeakReference<>(Objects.requireNonNull(parent, "parent"));
	}

	@Override
	public WeakReference<? extends Tuple> getParentReference() {
		return this.parent;
	}

	@Override
	public Quintuple<A, B, C, D, E> getParent() {
		final Quintuple<A, B, C, D, E> value = this.parent.get();
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
	public Quintuple<A, B, C, D, E> clone() {
		return this.getParent().clone();
	}

	@Override
	public MutableQuintuple<A, B, C, D, E> cloneMutable() {
		return this.getParent().cloneMutable();
	}

	@Override
	public ReadOnlyQuintuple<A, B, C, D, E> cloneReadOnly() {
		return this.getParent().cloneReadOnly();
	}

	@Override
	public Quintuple<A, B, C, D, E> deepClone() {
		return this.getParent().deepClone();
	}

	@Override
	public MutableQuintuple<A, B, C, D, E> deepCloneMutable() {
		return this.getParent().deepCloneMutable();
	}

	@Override
	public ReadOnlyQuintuple<A, B, C, D, E> deepCloneReadOnly() {
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
	public boolean hasFifth() {
		return this.getParent().hasFifth();
	}

	@Override
	public <T> T map(final QuintFunction<A, B, C, D, E, T> func) {
		return this.getParent().map(func);
	}

	@Override
	public <T, N, O, P, Q> Quintuple<T, N, O, P, Q> map(
			final QuintFunction<A, B, C, D, E, T> funcFirst,
			final QuintFunction<A, B, C, D, E, N> funcSecond,
			final QuintFunction<A, B, C, D, E, O> funcThird,
			final QuintFunction<A, B, C, D, E, P> funcFourth,
			final QuintFunction<A, B, C, D, E, Q> funcFifth) {
		return this.getParent().map(funcFirst, funcSecond, funcThird, funcFourth, funcFifth);
	}

	@Override
	public <T> Quintuple<T, B, C, D, E> mapFirst(final QuintFunction<A, B, C, D, E, T> func) {
		return this.getParent().mapFirst(func);
	}

	@Override
	public <T> Quintuple<A, T, C, D, E> mapSecond(final QuintFunction<A, B, C, D, E, T> func) {
		return this.getParent().mapSecond(func);
	}

	@Override
	public <T> Quintuple<A, B, T, D, E> mapThird(final QuintFunction<A, B, C, D, E, T> func) {
		return this.getParent().mapThird(func);
	}

	@Override
	public <T> Quintuple<A, B, C, T, E> mapFourth(final QuintFunction<A, B, C, D, E, T> func) {
		return this.getParent().mapFourth(func);
	}

	@Override
	public <T> Quintuple<A, B, C, D, T> mapFifth(final QuintFunction<A, B, C, D, E, T> func) {
		return this.getParent().mapFifth(func);
	}

	@Override
	public <T> Quintuple<?, ?, ?, ?, ?> map(final int i, final Function<Object, T> func) {
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
	public Quintuple<A, B, C, D, E> asViewReadOnly() {
		return new ReadOnlyQuintupleView<>(this);
	}

	@Override
	public Quintuple<A, B, C, D, E> asWeakViewReadOnly() {
		return new ReadOnlyQuintupleWeakView<>(this);
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

	@Override
	public E getFifth() {
		return this.getParent().getFifth();
	}

}
