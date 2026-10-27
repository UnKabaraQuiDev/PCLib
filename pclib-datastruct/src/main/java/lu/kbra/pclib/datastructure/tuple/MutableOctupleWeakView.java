package lu.kbra.pclib.datastructure.tuple;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

import lu.kbra.pclib.impl.function.OctFunction;
import lu.kbra.pclib.impl.function.ThrowingFunction;
import lu.kbra.pclib.impl.supplier.ThrowingSupplier;

public final class MutableOctupleWeakView<A, B, C, D, E, F, G, H> implements MutableOctuple<A, B, C, D, E, F, G, H>, WeakTupleView {

	private final WeakReference<MutableOctuple<A, B, C, D, E, F, G, H>> parent;

	public MutableOctupleWeakView(final MutableOctuple<A, B, C, D, E, F, G, H> parent) {
		this.parent = new WeakReference<>(Objects.requireNonNull(parent, "parent"));
	}

	@Override
	public WeakReference<? extends Tuple> getParentReference() {
		return this.parent;
	}

	@Override
	public MutableOctuple<A, B, C, D, E, F, G, H> getParent() {
		final MutableOctuple<A, B, C, D, E, F, G, H> value = this.parent.get();
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
	public Octuple<A, B, C, D, E, F, G, H> clone() {
		return this.getParent().clone();
	}

	@Override
	public MutableOctuple<A, B, C, D, E, F, G, H> cloneMutable() {
		return this.getParent().cloneMutable();
	}

	@Override
	public ReadOnlyOctuple<A, B, C, D, E, F, G, H> cloneReadOnly() {
		return this.getParent().cloneReadOnly();
	}

	@Override
	public MutableOctuple<A, B, C, D, E, F, G, H> deepClone() {
		return this.getParent().deepClone();
	}

	@Override
	public MutableOctuple<A, B, C, D, E, F, G, H> deepCloneMutable() {
		return this.getParent().deepCloneMutable();
	}

	@Override
	public ReadOnlyOctuple<A, B, C, D, E, F, G, H> deepCloneReadOnly() {
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
	public boolean hasSixth() {
		return this.getParent().hasSixth();
	}

	@Override
	public boolean hasSeventh() {
		return this.getParent().hasSeventh();
	}

	@Override
	public boolean hasEighth() {
		return this.getParent().hasEighth();
	}

	@Override
	public <T> T map(final OctFunction<A, B, C, D, E, F, G, H, T> func) {
		return this.getParent().map(func);
	}

	@Override
	public <T, N, O, P, Q, R, S, U> MutableOctuple<T, N, O, P, Q, R, S, U> map(
			final OctFunction<A, B, C, D, E, F, G, H, T> funcFirst,
			final OctFunction<A, B, C, D, E, F, G, H, N> funcSecond,
			final OctFunction<A, B, C, D, E, F, G, H, O> funcThird,
			final OctFunction<A, B, C, D, E, F, G, H, P> funcFourth,
			final OctFunction<A, B, C, D, E, F, G, H, Q> funcFifth,
			final OctFunction<A, B, C, D, E, F, G, H, R> funcSixth,
			final OctFunction<A, B, C, D, E, F, G, H, S> funcSeventh,
			final OctFunction<A, B, C, D, E, F, G, H, U> funcEighth) {
		return this.getParent().map(funcFirst, funcSecond, funcThird, funcFourth, funcFifth, funcSixth, funcSeventh, funcEighth);
	}

	@Override
	public <T> MutableOctuple<T, B, C, D, E, F, G, H> mapFirst(final OctFunction<A, B, C, D, E, F, G, H, T> func) {
		return this.getParent().mapFirst(func);
	}

	@Override
	public <T> MutableOctuple<A, T, C, D, E, F, G, H> mapSecond(final OctFunction<A, B, C, D, E, F, G, H, T> func) {
		return this.getParent().mapSecond(func);
	}

	@Override
	public <T> MutableOctuple<A, B, T, D, E, F, G, H> mapThird(final OctFunction<A, B, C, D, E, F, G, H, T> func) {
		return this.getParent().mapThird(func);
	}

	@Override
	public <T> MutableOctuple<A, B, C, T, E, F, G, H> mapFourth(final OctFunction<A, B, C, D, E, F, G, H, T> func) {
		return this.getParent().mapFourth(func);
	}

	@Override
	public <T> MutableOctuple<A, B, C, D, T, F, G, H> mapFifth(final OctFunction<A, B, C, D, E, F, G, H, T> func) {
		return this.getParent().mapFifth(func);
	}

	@Override
	public <T> MutableOctuple<A, B, C, D, E, T, G, H> mapSixth(final OctFunction<A, B, C, D, E, F, G, H, T> func) {
		return this.getParent().mapSixth(func);
	}

	@Override
	public <T> MutableOctuple<A, B, C, D, E, F, T, H> mapSeventh(final OctFunction<A, B, C, D, E, F, G, H, T> func) {
		return this.getParent().mapSeventh(func);
	}

	@Override
	public <T> MutableOctuple<A, B, C, D, E, F, G, T> mapEighth(final OctFunction<A, B, C, D, E, F, G, H, T> func) {
		return this.getParent().mapEighth(func);
	}

	@Override
	public <T> MutableOctuple<?, ?, ?, ?, ?, ?, ?, ?> map(final int index, final Function<Object, T> func) {
		return this.getParent().map(index, func);
	}

	@Override
	public MutableOctuple<A, B, C, D, E, F, G, H> mut(
			final OctFunction<A, B, C, D, E, F, G, H, A> funcFirst,
			final OctFunction<A, B, C, D, E, F, G, H, B> funcSecond,
			final OctFunction<A, B, C, D, E, F, G, H, C> funcThird,
			final OctFunction<A, B, C, D, E, F, G, H, D> funcFourth,
			final OctFunction<A, B, C, D, E, F, G, H, E> funcFifth,
			final OctFunction<A, B, C, D, E, F, G, H, F> funcSixth,
			final OctFunction<A, B, C, D, E, F, G, H, G> funcSeventh,
			final OctFunction<A, B, C, D, E, F, G, H, H> funcEighth) {
		return this.getParent().mut(funcFirst, funcSecond, funcThird, funcFourth, funcFifth, funcSixth, funcSeventh, funcEighth);
	}

	@Override
	public MutableOctuple<A, B, C, D, E, F, G, H> mutFirst(final OctFunction<A, B, C, D, E, F, G, H, A> func) {
		return this.getParent().mutFirst(func);
	}

	@Override
	public MutableOctuple<A, B, C, D, E, F, G, H> mutSecond(final OctFunction<A, B, C, D, E, F, G, H, B> func) {
		return this.getParent().mutSecond(func);
	}

	@Override
	public MutableOctuple<A, B, C, D, E, F, G, H> mutThird(final OctFunction<A, B, C, D, E, F, G, H, C> func) {
		return this.getParent().mutThird(func);
	}

	@Override
	public MutableOctuple<A, B, C, D, E, F, G, H> mutFourth(final OctFunction<A, B, C, D, E, F, G, H, D> func) {
		return this.getParent().mutFourth(func);
	}

	@Override
	public MutableOctuple<A, B, C, D, E, F, G, H> mutFifth(final OctFunction<A, B, C, D, E, F, G, H, E> func) {
		return this.getParent().mutFifth(func);
	}

	@Override
	public MutableOctuple<A, B, C, D, E, F, G, H> mutSixth(final OctFunction<A, B, C, D, E, F, G, H, F> func) {
		return this.getParent().mutSixth(func);
	}

	@Override
	public MutableOctuple<A, B, C, D, E, F, G, H> mutSeventh(final OctFunction<A, B, C, D, E, F, G, H, G> func) {
		return this.getParent().mutSeventh(func);
	}

	@Override
	public MutableOctuple<A, B, C, D, E, F, G, H> mutEighth(final OctFunction<A, B, C, D, E, F, G, H, H> func) {
		return this.getParent().mutEighth(func);
	}

	@Override
	public <T> MutableOctuple<A, B, C, D, E, F, G, H> mut(final int index, final Function<Object, T> func) {
		return this.getParent().mut(index, func);
	}

	@Override
	public MutableOctuple<A, B, C, D, E, F, G, H> setFirst(final A first) {
		return this.getParent().setFirst(first);
	}

	@Override
	public MutableOctuple<A, B, C, D, E, F, G, H> setSecond(final B second) {
		return this.getParent().setSecond(second);
	}

	@Override
	public MutableOctuple<A, B, C, D, E, F, G, H> setThird(final C third) {
		return this.getParent().setThird(third);
	}

	@Override
	public MutableOctuple<A, B, C, D, E, F, G, H> setFourth(final D fourth) {
		return this.getParent().setFourth(fourth);
	}

	@Override
	public MutableOctuple<A, B, C, D, E, F, G, H> setFifth(final E fifth) {
		return this.getParent().setFifth(fifth);
	}

	@Override
	public MutableOctuple<A, B, C, D, E, F, G, H> setSixth(final F sixth) {
		return this.getParent().setSixth(sixth);
	}

	@Override
	public MutableOctuple<A, B, C, D, E, F, G, H> setSeventh(final G seventh) {
		return this.getParent().setSeventh(seventh);
	}

	@Override
	public MutableOctuple<A, B, C, D, E, F, G, H> setEighth(final H eighth) {
		return this.getParent().setEighth(eighth);
	}

	@Override
	public <T> MutableOctuple<A, B, C, D, E, F, G, H> set(final int i, final T value) {
		return this.getParent().set(i, value);
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
	public G getSeventh() {
		return this.getParent().getSeventh();
	}

	@Override
	public H getEighth() {
		return this.getParent().getEighth();
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
	public MutableOctuple<A, B, C, D, E, F, G, H> asSynchronized() {
		return this.getParent().asSynchronized();
	}

	@Override
	public Octuple<A, B, C, D, E, F, G, H> asViewReadOnly() {
		return new ReadOnlyOctupleView<>(this);
	}

	@Override
	public MutableOctuple<A, B, C, D, E, F, G, H> asViewMutable() {
		return new MutableOctupleView<>(this);
	}

	@Override
	public Octuple<A, B, C, D, E, F, G, H> asWeakViewReadOnly() {
		return new ReadOnlyOctupleWeakView<>(this);
	}

	@Override
	public MutableOctuple<A, B, C, D, E, F, G, H> asWeakViewMutable() {
		return new MutableOctupleWeakView<>(this);
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
