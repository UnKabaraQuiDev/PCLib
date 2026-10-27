package lu.kbra.pclib.datastructure.tuple;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

import lu.kbra.pclib.impl.function.SextFunction;
import lu.kbra.pclib.impl.function.ThrowingFunction;
import lu.kbra.pclib.impl.supplier.ThrowingSupplier;

public final class MutableSextupleWeakView<A, B, C, D, E, F> implements MutableSextuple<A, B, C, D, E, F>, WeakTupleView {

	private final WeakReference<MutableSextuple<A, B, C, D, E, F>> parent;

	public MutableSextupleWeakView(final MutableSextuple<A, B, C, D, E, F> parent) {
		this.parent = new WeakReference<>(Objects.requireNonNull(parent, "parent"));
	}

	@Override
	public WeakReference<? extends Tuple> getParentReference() {
		return this.parent;
	}

	@Override
	public MutableSextuple<A, B, C, D, E, F> getParent() {
		final MutableSextuple<A, B, C, D, E, F> value = this.parent.get();
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
	public Sextuple<A, B, C, D, E, F> clone() {
		return this.getParent().clone();
	}

	@Override
	public MutableSextuple<A, B, C, D, E, F> cloneMutable() {
		return this.getParent().cloneMutable();
	}

	@Override
	public ReadOnlySextuple<A, B, C, D, E, F> cloneReadOnly() {
		return this.getParent().cloneReadOnly();
	}

	@Override
	public MutableSextuple<A, B, C, D, E, F> deepClone() {
		return this.getParent().deepClone();
	}

	@Override
	public MutableSextuple<A, B, C, D, E, F> deepCloneMutable() {
		return this.getParent().deepCloneMutable();
	}

	@Override
	public ReadOnlySextuple<A, B, C, D, E, F> deepCloneReadOnly() {
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
	public boolean hasSixth() {
		return this.getParent().hasSixth();
	}

	@Override
	public <T> T map(final SextFunction<A, B, C, D, E, F, T> func) {
		return this.getParent().map(func);
	}

	@Override
	public <T, U, V, W, X, Y> MutableSextuple<T, U, V, W, X, Y> map(
			final SextFunction<A, B, C, D, E, F, T> funcFirst,
			final SextFunction<A, B, C, D, E, F, U> funcSecond,
			final SextFunction<A, B, C, D, E, F, V> funcThird,
			final SextFunction<A, B, C, D, E, F, W> funcFourth,
			final SextFunction<A, B, C, D, E, F, X> funcFifth,
			final SextFunction<A, B, C, D, E, F, Y> funcSixth) {
		return this.getParent().map(funcFirst, funcSecond, funcThird, funcFourth, funcFifth, funcSixth);
	}

	@Override
	public <T> MutableSextuple<T, B, C, D, E, F> mapFirst(final SextFunction<A, B, C, D, E, F, T> func) {
		return this.getParent().mapFirst(func);
	}

	@Override
	public <T> MutableSextuple<A, T, C, D, E, F> mapSecond(final SextFunction<A, B, C, D, E, F, T> func) {
		return this.getParent().mapSecond(func);
	}

	@Override
	public <T> MutableSextuple<A, B, T, D, E, F> mapThird(final SextFunction<A, B, C, D, E, F, T> func) {
		return this.getParent().mapThird(func);
	}

	@Override
	public <T> MutableSextuple<A, B, C, T, E, F> mapFourth(final SextFunction<A, B, C, D, E, F, T> func) {
		return this.getParent().mapFourth(func);
	}

	@Override
	public <T> MutableSextuple<A, B, C, D, T, F> mapFifth(final SextFunction<A, B, C, D, E, F, T> func) {
		return this.getParent().mapFifth(func);
	}

	@Override
	public <T> MutableSextuple<A, B, C, D, E, T> mapSixth(final SextFunction<A, B, C, D, E, F, T> func) {
		return this.getParent().mapSixth(func);
	}

	@Override
	public <T> MutableSextuple<?, ?, ?, ?, ?, ?> map(final int index, final Function<Object, T> func) {
		return this.getParent().map(index, func);
	}

	@Override
	public MutableSextuple<A, B, C, D, E, F> mut(
			final SextFunction<A, B, C, D, E, F, A> funcFirst,
			final SextFunction<A, B, C, D, E, F, B> funcSecond,
			final SextFunction<A, B, C, D, E, F, C> funcThird,
			final SextFunction<A, B, C, D, E, F, D> funcFourth,
			final SextFunction<A, B, C, D, E, F, E> funcFifth,
			final SextFunction<A, B, C, D, E, F, F> funcSixth) {
		return this.getParent().mut(funcFirst, funcSecond, funcThird, funcFourth, funcFifth, funcSixth);
	}

	@Override
	public MutableSextuple<A, B, C, D, E, F> mutFirst(final SextFunction<A, B, C, D, E, F, A> func) {
		return this.getParent().mutFirst(func);
	}

	@Override
	public MutableSextuple<A, B, C, D, E, F> mutSecond(final SextFunction<A, B, C, D, E, F, B> func) {
		return this.getParent().mutSecond(func);
	}

	@Override
	public MutableSextuple<A, B, C, D, E, F> mutThird(final SextFunction<A, B, C, D, E, F, C> func) {
		return this.getParent().mutThird(func);
	}

	@Override
	public MutableSextuple<A, B, C, D, E, F> mutFourth(final SextFunction<A, B, C, D, E, F, D> func) {
		return this.getParent().mutFourth(func);
	}

	@Override
	public MutableSextuple<A, B, C, D, E, F> mutFifth(final SextFunction<A, B, C, D, E, F, E> func) {
		return this.getParent().mutFifth(func);
	}

	@Override
	public MutableSextuple<A, B, C, D, E, F> mutSixth(final SextFunction<A, B, C, D, E, F, F> func) {
		return this.getParent().mutSixth(func);
	}

	@Override
	public <T> MutableSextuple<A, B, C, D, E, F> mut(final int index, final Function<Object, T> func) {
		return this.getParent().mut(index, func);
	}

	@Override
	public Sextuple<A, B, C, D, E, F> setFirst(final A first) {
		return this.getParent().setFirst(first);
	}

	@Override
	public Sextuple<A, B, C, D, E, F> setSecond(final B second) {
		return this.getParent().setSecond(second);
	}

	@Override
	public Sextuple<A, B, C, D, E, F> setThird(final C third) {
		return this.getParent().setThird(third);
	}

	@Override
	public Sextuple<A, B, C, D, E, F> setFourth(final D fourth) {
		return this.getParent().setFourth(fourth);
	}

	@Override
	public Sextuple<A, B, C, D, E, F> setFifth(final E fifth) {
		return this.getParent().setFifth(fifth);
	}

	@Override
	public Sextuple<A, B, C, D, E, F> setSixth(final F sixth) {
		return this.getParent().setSixth(sixth);
	}

	@Override
	public <T> MutableSextuple<A, B, C, D, E, F> set(final int index, final T element) {
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
	public F getSixth() {
		return this.getParent().getSixth();
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
	public MutableSextuple<A, B, C, D, E, F> asSynchronized() {
		return this.getParent().asSynchronized();
	}

	@Override
	public Sextuple<A, B, C, D, E, F> asViewReadOnly() {
		return new ReadOnlySextupleView<>(this);
	}

	@Override
	public MutableSextuple<A, B, C, D, E, F> asViewMutable() {
		return new MutableSextupleView<>(this);
	}

	@Override
	public Sextuple<A, B, C, D, E, F> asWeakViewReadOnly() {
		return new ReadOnlySextupleWeakView<>(this);
	}

	@Override
	public MutableSextuple<A, B, C, D, E, F> asWeakViewMutable() {
		return new MutableSextupleWeakView<>(this);
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
