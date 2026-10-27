package lu.kbra.pclib.datastructure.tuple;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.function.Function;

import lu.kbra.pclib.impl.function.SextFunction;

public final class ReadOnlySextupleWeakView<A, B, C, D, E, F> implements Sextuple<A, B, C, D, E, F>, WeakTupleView {

	private final WeakReference<Sextuple<A, B, C, D, E, F>> parent;

	public ReadOnlySextupleWeakView(final Sextuple<A, B, C, D, E, F> parent) {
		this.parent = new WeakReference<>(Objects.requireNonNull(parent, "parent"));
	}

	@Override
	public WeakReference<? extends Tuple> getParentReference() {
		return this.parent;
	}

	@Override
	public Sextuple<A, B, C, D, E, F> getParent() {
		final Sextuple<A, B, C, D, E, F> value = this.parent.get();
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
	public Sextuple<A, B, C, D, E, F> deepClone() {
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
	public <T> T map(final SextFunction<A, B, C, D, E, F, T> func) {
		return this.getParent().map(func);
	}

	@Override
	public <T, N, O, P, Q, R> Sextuple<T, N, O, P, Q, R> map(
			final SextFunction<A, B, C, D, E, F, T> funcFirst,
			final SextFunction<A, B, C, D, E, F, N> funcSecond,
			final SextFunction<A, B, C, D, E, F, O> funcThird,
			final SextFunction<A, B, C, D, E, F, P> funcFourth,
			final SextFunction<A, B, C, D, E, F, Q> funcFifth,
			final SextFunction<A, B, C, D, E, F, R> funcSixth) {
		return this.getParent().map(funcFirst, funcSecond, funcThird, funcFourth, funcFifth, funcSixth);
	}

	@Override
	public <T> Sextuple<T, B, C, D, E, F> mapFirst(final SextFunction<A, B, C, D, E, F, T> func) {
		return this.getParent().mapFirst(func);
	}

	@Override
	public <T> Sextuple<A, T, C, D, E, F> mapSecond(final SextFunction<A, B, C, D, E, F, T> func) {
		return this.getParent().mapSecond(func);
	}

	@Override
	public <T> Sextuple<A, B, T, D, E, F> mapThird(final SextFunction<A, B, C, D, E, F, T> func) {
		return this.getParent().mapThird(func);
	}

	@Override
	public <T> Sextuple<A, B, C, T, E, F> mapFourth(final SextFunction<A, B, C, D, E, F, T> func) {
		return this.getParent().mapFourth(func);
	}

	@Override
	public <T> Sextuple<A, B, C, D, T, F> mapFifth(final SextFunction<A, B, C, D, E, F, T> func) {
		return this.getParent().mapFifth(func);
	}

	@Override
	public <T> Sextuple<A, B, C, D, E, T> mapSixth(final SextFunction<A, B, C, D, E, F, T> func) {
		return this.getParent().mapSixth(func);
	}

	@Override
	public <T> Sextuple<?, ?, ?, ?, ?, ?> map(final int i, final Function<Object, T> func) {
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
	public Sextuple<A, B, C, D, E, F> asViewReadOnly() {
		return new ReadOnlySextupleView<>(this);
	}

	@Override
	public Sextuple<A, B, C, D, E, F> asWeakViewReadOnly() {
		return new ReadOnlySextupleWeakView<>(this);
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

}
