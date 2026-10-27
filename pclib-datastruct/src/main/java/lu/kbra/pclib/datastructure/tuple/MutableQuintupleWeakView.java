package lu.kbra.pclib.datastructure.tuple;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

import lu.kbra.pclib.impl.function.QuintFunction;
import lu.kbra.pclib.impl.function.ThrowingFunction;
import lu.kbra.pclib.impl.supplier.ThrowingSupplier;

public final class MutableQuintupleWeakView<A, B, C, D, E> implements MutableQuintuple<A, B, C, D, E>, WeakTupleView {

	private final WeakReference<MutableQuintuple<A, B, C, D, E>> parent;

	public MutableQuintupleWeakView(final MutableQuintuple<A, B, C, D, E> parent) {
		this.parent = new WeakReference<>(Objects.requireNonNull(parent, "parent"));
	}

	@Override
	public WeakReference<? extends Tuple> getParentReference() {
		return this.parent;
	}

	@Override
	public MutableQuintuple<A, B, C, D, E> getParent() {
		final MutableQuintuple<A, B, C, D, E> value = this.parent.get();
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
	public MutableQuintuple<A, B, C, D, E> deepClone() {
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
	public <T, U, V, W, X> MutableQuintuple<T, U, V, W, X> map(
			final QuintFunction<A, B, C, D, E, T> funcFirst,
			final QuintFunction<A, B, C, D, E, U> funcSecond,
			final QuintFunction<A, B, C, D, E, V> funcThird,
			final QuintFunction<A, B, C, D, E, W> funcFourth,
			final QuintFunction<A, B, C, D, E, X> funcFifth) {
		return this.getParent().map(funcFirst, funcSecond, funcThird, funcFourth, funcFifth);
	}

	@Override
	public <T> MutableQuintuple<T, B, C, D, E> mapFirst(final QuintFunction<A, B, C, D, E, T> func) {
		return this.getParent().mapFirst(func);
	}

	@Override
	public <T> MutableQuintuple<A, T, C, D, E> mapSecond(final QuintFunction<A, B, C, D, E, T> func) {
		return this.getParent().mapSecond(func);
	}

	@Override
	public <T> MutableQuintuple<A, B, T, D, E> mapThird(final QuintFunction<A, B, C, D, E, T> func) {
		return this.getParent().mapThird(func);
	}

	@Override
	public <T> MutableQuintuple<A, B, C, T, E> mapFourth(final QuintFunction<A, B, C, D, E, T> func) {
		return this.getParent().mapFourth(func);
	}

	@Override
	public <T> MutableQuintuple<A, B, C, D, T> mapFifth(final QuintFunction<A, B, C, D, E, T> func) {
		return this.getParent().mapFifth(func);
	}

	@Override
	public <T> MutableQuintuple<?, ?, ?, ?, ?> map(final int index, final Function<Object, T> func) {
		return this.getParent().map(index, func);
	}

	@Override
	public MutableQuintuple<A, B, C, D, E> mut(
			final QuintFunction<A, B, C, D, E, A> funcFirst,
			final QuintFunction<A, B, C, D, E, B> funcSecond,
			final QuintFunction<A, B, C, D, E, C> funcThird,
			final QuintFunction<A, B, C, D, E, D> funcFourth,
			final QuintFunction<A, B, C, D, E, E> funcFifth) {
		return this.getParent().mut(funcFirst, funcSecond, funcThird, funcFourth, funcFifth);
	}

	@Override
	public MutableQuintuple<A, B, C, D, E> mutFirst(final QuintFunction<A, B, C, D, E, A> func) {
		return this.getParent().mutFirst(func);
	}

	@Override
	public MutableQuintuple<A, B, C, D, E> mutSecond(final QuintFunction<A, B, C, D, E, B> func) {
		return this.getParent().mutSecond(func);
	}

	@Override
	public MutableQuintuple<A, B, C, D, E> mutThird(final QuintFunction<A, B, C, D, E, C> func) {
		return this.getParent().mutThird(func);
	}

	@Override
	public MutableQuintuple<A, B, C, D, E> mutFourth(final QuintFunction<A, B, C, D, E, D> func) {
		return this.getParent().mutFourth(func);
	}

	@Override
	public MutableQuintuple<A, B, C, D, E> mutFifth(final QuintFunction<A, B, C, D, E, E> func) {
		return this.getParent().mutFifth(func);
	}

	@Override
	public <T> MutableQuintuple<A, B, C, D, E> mut(final int index, final Function<Object, T> func) {
		return this.getParent().mut(index, func);
	}

	@Override
	public Quintuple<A, B, C, D, E> setFirst(final A first) {
		return this.getParent().setFirst(first);
	}

	@Override
	public Quintuple<A, B, C, D, E> setSecond(final B second) {
		return this.getParent().setSecond(second);
	}

	@Override
	public Quintuple<A, B, C, D, E> setThird(final C third) {
		return this.getParent().setThird(third);
	}

	@Override
	public Quintuple<A, B, C, D, E> setFourth(final D fourth) {
		return this.getParent().setFourth(fourth);
	}

	@Override
	public Quintuple<A, B, C, D, E> setFifth(final E fifth) {
		return this.getParent().setFifth(fifth);
	}

	@Override
	public <T> MutableQuintuple<A, B, C, D, E> set(final int index, final T element) {
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
	public D getFourth() {
		return this.getParent().getFourth();
	}

	@Override
	public E getFifth() {
		return this.getParent().getFifth();
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
	public MutableQuintuple<A, B, C, D, E> asSynchronized() {
		return this.getParent().asSynchronized();
	}

	@Override
	public Quintuple<A, B, C, D, E> asViewReadOnly() {
		return new ReadOnlyQuintupleView<>(this);
	}

	@Override
	public MutableQuintuple<A, B, C, D, E> asViewMutable() {
		return new MutableQuintupleView<>(this);
	}

	@Override
	public Quintuple<A, B, C, D, E> asWeakViewReadOnly() {
		return new ReadOnlyQuintupleWeakView<>(this);
	}

	@Override
	public MutableQuintuple<A, B, C, D, E> asWeakViewMutable() {
		return new MutableQuintupleWeakView<>(this);
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
