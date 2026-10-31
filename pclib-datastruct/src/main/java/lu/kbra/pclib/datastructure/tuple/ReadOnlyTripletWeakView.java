package lu.kbra.pclib.datastructure.tuple;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.function.Function;

import lu.kbra.pclib.impl.function.TriFunction;

public final class ReadOnlyTripletWeakView<A, B, C> implements Triplet<A, B, C>, WeakTupleView {

	private final WeakReference<Triplet<A, B, C>> parent;

	public ReadOnlyTripletWeakView(final Triplet<A, B, C> parent) {
		this.parent = new WeakReference<>(Objects.requireNonNull(parent, "parent"));
	}

	@Override
	public WeakReference<? extends Tuple> getParentReference() {
		return this.parent;
	}

	@Override
	public Triplet<A, B, C> getParent() {
		final Triplet<A, B, C> value = this.parent.get();
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
	public Triplet<A, B, C> clone() {
		return this.getParent().clone();
	}

	@Override
	public MutableTriplet<A, B, C> cloneMutable() {
		return this.getParent().cloneMutable();
	}

	@Override
	public ReadOnlyTriplet<A, B, C> cloneReadOnly() {
		return this.getParent().cloneReadOnly();
	}

	@Override
	public Triplet<A, B, C> deepClone() {
		return this.getParent().deepClone();
	}

	@Override
	public MutableTriplet<A, B, C> deepCloneMutable() {
		return this.getParent().deepCloneMutable();
	}

	@Override
	public ReadOnlyTriplet<A, B, C> deepCloneReadOnly() {
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
	public <T> T map(final TriFunction<A, B, C, T> func) {
		return this.getParent().map(func);
	}

	@Override
	public <T, N, O> Triplet<T, N, O> map(
			final TriFunction<A, B, C, T> funcFirst,
			final TriFunction<A, B, C, N> funcSecond,
			final TriFunction<A, B, C, O> funcThird) {
		return this.getParent().map(funcFirst, funcSecond, funcThird);
	}

	@Override
	public <T> Triplet<T, B, C> mapFirst(final TriFunction<A, B, C, T> func) {
		return this.getParent().mapFirst(func);
	}

	@Override
	public <T> Triplet<A, T, C> mapSecond(final TriFunction<A, B, C, T> func) {
		return this.getParent().mapSecond(func);
	}

	@Override
	public <T> Triplet<A, B, T> mapThird(final TriFunction<A, B, C, T> func) {
		return this.getParent().mapThird(func);
	}

	@Override
	public <T> Triplet<?, ?, ?> map(final int i, final Function<Object, T> func) {
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
	public Triplet<A, B, C> asViewReadOnly() {
		return new ReadOnlyTripletView<>(this);
	}

	@Override
	public Triplet<A, B, C> asWeakViewReadOnly() {
		return new ReadOnlyTripletWeakView<>(this);
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

}
