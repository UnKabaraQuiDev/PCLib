package lu.kbra.pclib.datastructure.tuple;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.function.Function;

import lu.kbra.pclib.impl.function.SeptFunction;

public final class ReadOnlySeptupleWeakView<A, B, C, D, E, F, G> implements Septuple<A, B, C, D, E, F, G>, WeakTupleView {

	private final WeakReference<Septuple<A, B, C, D, E, F, G>> parent;

	public ReadOnlySeptupleWeakView(final Septuple<A, B, C, D, E, F, G> parent) {
		this.parent = new WeakReference<>(Objects.requireNonNull(parent, "parent"));
	}

	@Override
	public WeakReference<? extends Tuple> getParentReference() {
		return this.parent;
	}

	@Override
	public Septuple<A, B, C, D, E, F, G> getParent() {
		final Septuple<A, B, C, D, E, F, G> value = this.parent.get();
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
	public Septuple<A, B, C, D, E, F, G> clone() {
		return this.getParent().clone();
	}

	@Override
	public MutableSeptuple<A, B, C, D, E, F, G> cloneMutable() {
		return this.getParent().cloneMutable();
	}

	@Override
	public ReadOnlySeptuple<A, B, C, D, E, F, G> cloneReadOnly() {
		return this.getParent().cloneReadOnly();
	}

	@Override
	public Septuple<A, B, C, D, E, F, G> deepClone() {
		return this.getParent().deepClone();
	}

	@Override
	public MutableSeptuple<A, B, C, D, E, F, G> deepCloneMutable() {
		return this.getParent().deepCloneMutable();
	}

	@Override
	public ReadOnlySeptuple<A, B, C, D, E, F, G> deepCloneReadOnly() {
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
	public <T> T map(final SeptFunction<A, B, C, D, E, F, G, T> func) {
		return this.getParent().map(func);
	}

	@Override
	public <T, N, O, P, Q, R, S> Septuple<T, N, O, P, Q, R, S> map(
			final SeptFunction<A, B, C, D, E, F, G, T> funcFirst,
			final SeptFunction<A, B, C, D, E, F, G, N> funcSecond,
			final SeptFunction<A, B, C, D, E, F, G, O> funcThird,
			final SeptFunction<A, B, C, D, E, F, G, P> funcFourth,
			final SeptFunction<A, B, C, D, E, F, G, Q> funcFifth,
			final SeptFunction<A, B, C, D, E, F, G, R> funcSixth,
			final SeptFunction<A, B, C, D, E, F, G, S> funcSeventh) {
		return this.getParent().map(funcFirst, funcSecond, funcThird, funcFourth, funcFifth, funcSixth, funcSeventh);
	}

	@Override
	public <T> Septuple<T, B, C, D, E, F, G> mapFirst(final SeptFunction<A, B, C, D, E, F, G, T> func) {
		return this.getParent().mapFirst(func);
	}

	@Override
	public <T> Septuple<A, T, C, D, E, F, G> mapSecond(final SeptFunction<A, B, C, D, E, F, G, T> func) {
		return this.getParent().mapSecond(func);
	}

	@Override
	public <T> Septuple<A, B, T, D, E, F, G> mapThird(final SeptFunction<A, B, C, D, E, F, G, T> func) {
		return this.getParent().mapThird(func);
	}

	@Override
	public <T> Septuple<A, B, C, T, E, F, G> mapFourth(final SeptFunction<A, B, C, D, E, F, G, T> func) {
		return this.getParent().mapFourth(func);
	}

	@Override
	public <T> Septuple<A, B, C, D, T, F, G> mapFifth(final SeptFunction<A, B, C, D, E, F, G, T> func) {
		return this.getParent().mapFifth(func);
	}

	@Override
	public <T> Septuple<A, B, C, D, E, T, G> mapSixth(final SeptFunction<A, B, C, D, E, F, G, T> func) {
		return this.getParent().mapSixth(func);
	}

	@Override
	public <T> Septuple<A, B, C, D, E, F, T> mapSeventh(final SeptFunction<A, B, C, D, E, F, G, T> func) {
		return this.getParent().mapSeventh(func);
	}

	@Override
	public <T> Septuple<?, ?, ?, ?, ?, ?, ?> map(final int i, final Function<Object, T> func) {
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
	public Septuple<A, B, C, D, E, F, G> asViewReadOnly() {
		return new ReadOnlySeptupleView<>(this);
	}

	@Override
	public Septuple<A, B, C, D, E, F, G> asWeakViewReadOnly() {
		return new ReadOnlySeptupleWeakView<>(this);
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

	@Override
	public F getSixth() {
		return this.getParent().getSixth();
	}

	@Override
	public G getSeventh() {
		return this.getParent().getSeventh();
	}

}
