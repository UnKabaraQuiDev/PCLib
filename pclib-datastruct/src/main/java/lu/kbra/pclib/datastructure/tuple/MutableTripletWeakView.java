package lu.kbra.pclib.datastructure.tuple;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

import lu.kbra.pclib.impl.function.ThrowingFunction;
import lu.kbra.pclib.impl.function.TriFunction;
import lu.kbra.pclib.impl.supplier.ThrowingSupplier;

public final class MutableTripletWeakView<A, B, C> implements MutableTriplet<A, B, C>, WeakTupleView {

	private final WeakReference<MutableTriplet<A, B, C>> parent;

	public MutableTripletWeakView(final MutableTriplet<A, B, C> parent) {
		this.parent = new WeakReference<>(Objects.requireNonNull(parent, "parent"));
	}

	@Override
	public WeakReference<? extends Tuple> getParentReference() {
		return this.parent;
	}

	@Override
	public MutableTriplet<A, B, C> getParent() {
		final MutableTriplet<A, B, C> value = this.parent.get();
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
	public MutableTriplet<A, B, C> deepClone() {
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
	public <T> T get(final int index) {
		return this.getParent().get(index);
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
	public <T, U, V> MutableTriplet<T, U, V> map(
			final TriFunction<A, B, C, T> funcFirst,
			final TriFunction<A, B, C, U> funcSecond,
			final TriFunction<A, B, C, V> funcThird) {
		return this.getParent().map(funcFirst, funcSecond, funcThird);
	}

	@Override
	public <T> MutableTriplet<T, B, C> mapFirst(final TriFunction<A, B, C, T> func) {
		return this.getParent().mapFirst(func);
	}

	@Override
	public <T> MutableTriplet<A, T, C> mapSecond(final TriFunction<A, B, C, T> func) {
		return this.getParent().mapSecond(func);
	}

	@Override
	public <T> MutableTriplet<A, B, T> mapThird(final TriFunction<A, B, C, T> func) {
		return this.getParent().mapThird(func);
	}

	@Override
	public <T> MutableTriplet<?, ?, ?> map(final int index, final Function<Object, T> func) {
		return this.getParent().map(index, func);
	}

	@Override
	public MutableTriplet<A, B, C> mut(
			final TriFunction<A, B, C, A> funcFirst,
			final TriFunction<A, B, C, B> funcSecond,
			final TriFunction<A, B, C, C> funcThird) {
		return this.getParent().mut(funcFirst, funcSecond, funcThird);
	}

	@Override
	public MutableTriplet<A, B, C> mutFirst(final TriFunction<A, B, C, A> func) {
		return this.getParent().mutFirst(func);
	}

	@Override
	public MutableTriplet<A, B, C> mutSecond(final TriFunction<A, B, C, B> func) {
		return this.getParent().mutSecond(func);
	}

	@Override
	public MutableTriplet<A, B, C> mutThird(final TriFunction<A, B, C, C> func) {
		return this.getParent().mutThird(func);
	}

	@Override
	public <T> MutableTriplet<A, B, C> mut(final int index, final Function<Object, T> func) {
		return this.getParent().mut(index, func);
	}

	@Override
	public Triplet<A, B, C> setFirst(final A first) {
		return this.getParent().setFirst(first);
	}

	@Override
	public Triplet<A, B, C> setSecond(final B second) {
		return this.getParent().setSecond(second);
	}

	@Override
	public Triplet<A, B, C> setThird(final C third) {
		return this.getParent().setThird(third);
	}

	@Override
	public <T> MutableTriplet<A, B, C> set(final int index, final T element) {
		return this.getParent().set(index, element);
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
	public <T, P extends Throwable> T[] asArray(final ThrowingFunction<Integer, T[], P> factory) throws P {
		return this.getParent().asArray(factory);
	}

	@Override
	public <T> T[] asArray(final Function<Integer, T[]> factory) {
		return this.getParent().asArray(factory);
	}

	@Override
	public <T> T[] asArray(final IntFunction<T[]> factory) {
		return this.getParent().asArray(factory);
	}

	@Override
	public <T, P extends Throwable> T[] asArray(final ThrowingSupplier<T[], P> factory) throws P {
		return this.getParent().asArray(factory);
	}

	@Override
	public <T> T[] asArray(final Supplier<T[]> factory) {
		return this.getParent().asArray(factory);
	}

	@Override
	public <T> T[] asArray(final T[] destination) {
		return this.getParent().asArray(destination);
	}

	@Override
	public boolean isView() {
		return true;
	}

	@Override
	public boolean isSynchronized() {
		return this.getParent().isSynchronized();
	}

	@Override
	public MutableTriplet<A, B, C> asSynchronized() {
		return this.getParent().asSynchronized();
	}

	@Override
	public Triplet<A, B, C> asViewReadOnly() {
		return new ReadOnlyTripletView<>(this);
	}

	@Override
	public MutableTriplet<A, B, C> asViewMutable() {
		return new MutableTripletView<>(this);
	}

	@Override
	public Triplet<A, B, C> asWeakViewReadOnly() {
		return new ReadOnlyTripletWeakView<>(this);
	}

	@Override
	public MutableTriplet<A, B, C> asWeakViewMutable() {
		return new MutableTripletWeakView<>(this);
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
