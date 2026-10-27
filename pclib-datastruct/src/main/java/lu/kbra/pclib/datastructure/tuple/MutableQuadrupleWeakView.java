package lu.kbra.pclib.datastructure.tuple;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

import lu.kbra.pclib.impl.function.QuadFunction;
import lu.kbra.pclib.impl.function.ThrowingFunction;
import lu.kbra.pclib.impl.supplier.ThrowingSupplier;

public final class MutableQuadrupleWeakView<A, B, C, D> implements MutableQuadruple<A, B, C, D>, WeakTupleView {

	private final WeakReference<MutableQuadruple<A, B, C, D>> parent;

	public MutableQuadrupleWeakView(final MutableQuadruple<A, B, C, D> parent) {
		this.parent = new WeakReference<>(Objects.requireNonNull(parent, "parent"));
	}

	@Override
	public WeakReference<? extends Tuple> getParentReference() {
		return this.parent;
	}

	@Override
	public MutableQuadruple<A, B, C, D> getParent() {
		final MutableQuadruple<A, B, C, D> value = this.parent.get();
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
	public MutableQuadruple<A, B, C, D> deepClone() {
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
	public <T> T map(final QuadFunction<A, B, C, D, T> func) {
		return this.getParent().map(func);
	}

	@Override
	public <T, U, V, W> MutableQuadruple<T, U, V, W> map(
			final QuadFunction<A, B, C, D, T> funcFirst,
			final QuadFunction<A, B, C, D, U> funcSecond,
			final QuadFunction<A, B, C, D, V> funcThird,
			final QuadFunction<A, B, C, D, W> funcFourth) {
		return this.getParent().map(funcFirst, funcSecond, funcThird, funcFourth);
	}

	@Override
	public <T> MutableQuadruple<T, B, C, D> mapFirst(final QuadFunction<A, B, C, D, T> func) {
		return this.getParent().mapFirst(func);
	}

	@Override
	public <T> MutableQuadruple<A, T, C, D> mapSecond(final QuadFunction<A, B, C, D, T> func) {
		return this.getParent().mapSecond(func);
	}

	@Override
	public <T> MutableQuadruple<A, B, T, D> mapThird(final QuadFunction<A, B, C, D, T> func) {
		return this.getParent().mapThird(func);
	}

	@Override
	public <T> MutableQuadruple<A, B, C, T> mapFourth(final QuadFunction<A, B, C, D, T> func) {
		return this.getParent().mapFourth(func);
	}

	@Override
	public <T> MutableQuadruple<?, ?, ?, ?> map(final int index, final Function<Object, T> func) {
		return this.getParent().map(index, func);
	}

	@Override
	public MutableQuadruple<A, B, C, D> mut(
			final QuadFunction<A, B, C, D, A> funcFirst,
			final QuadFunction<A, B, C, D, B> funcSecond,
			final QuadFunction<A, B, C, D, C> funcThird,
			final QuadFunction<A, B, C, D, D> funcFourth) {
		return this.getParent().mut(funcFirst, funcSecond, funcThird, funcFourth);
	}

	@Override
	public MutableQuadruple<A, B, C, D> mutFirst(final QuadFunction<A, B, C, D, A> func) {
		return this.getParent().mutFirst(func);
	}

	@Override
	public MutableQuadruple<A, B, C, D> mutSecond(final QuadFunction<A, B, C, D, B> func) {
		return this.getParent().mutSecond(func);
	}

	@Override
	public MutableQuadruple<A, B, C, D> mutThird(final QuadFunction<A, B, C, D, C> func) {
		return this.getParent().mutThird(func);
	}

	@Override
	public MutableQuadruple<A, B, C, D> mutFourth(final QuadFunction<A, B, C, D, D> func) {
		return this.getParent().mutFourth(func);
	}

	@Override
	public <T> MutableQuadruple<A, B, C, D> mut(final int index, final Function<Object, T> func) {
		return this.getParent().mut(index, func);
	}

	@Override
	public Quadruple<A, B, C, D> setFirst(final A first) {
		return this.getParent().setFirst(first);
	}

	@Override
	public Quadruple<A, B, C, D> setSecond(final B second) {
		return this.getParent().setSecond(second);
	}

	@Override
	public Quadruple<A, B, C, D> setThird(final C third) {
		return this.getParent().setThird(third);
	}

	@Override
	public Quadruple<A, B, C, D> setFourth(final D fourth) {
		return this.getParent().setFourth(fourth);
	}

	@Override
	public <T> MutableQuadruple<A, B, C, D> set(final int index, final T element) {
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
	public MutableQuadruple<A, B, C, D> asSynchronized() {
		return this.getParent().asSynchronized();
	}

	@Override
	public Quadruple<A, B, C, D> asViewReadOnly() {
		return new ReadOnlyQuadrupleView<>(this);
	}

	@Override
	public MutableQuadruple<A, B, C, D> asViewMutable() {
		return new MutableQuadrupleView<>(this);
	}

	@Override
	public Quadruple<A, B, C, D> asWeakViewReadOnly() {
		return new ReadOnlyQuadrupleWeakView<>(this);
	}

	@Override
	public MutableQuadruple<A, B, C, D> asWeakViewMutable() {
		return new MutableQuadrupleWeakView<>(this);
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
