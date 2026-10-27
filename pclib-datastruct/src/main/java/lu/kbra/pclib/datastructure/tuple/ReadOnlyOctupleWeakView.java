package lu.kbra.pclib.datastructure.tuple;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.function.Function;

import lu.kbra.pclib.impl.function.OctFunction;

public final class ReadOnlyOctupleWeakView<A, B, C, D, E, F, G, H> implements Octuple<A, B, C, D, E, F, G, H>, WeakTupleView {

	private final WeakReference<Octuple<A, B, C, D, E, F, G, H>> parent;

	public ReadOnlyOctupleWeakView(final Octuple<A, B, C, D, E, F, G, H> parent) {
		this.parent = new WeakReference<>(Objects.requireNonNull(parent, "parent"));
	}

	@Override
	public WeakReference<? extends Tuple> getParentReference() {
		return this.parent;
	}

	@Override
	public Octuple<A, B, C, D, E, F, G, H> getParent() {
		final Octuple<A, B, C, D, E, F, G, H> value = this.parent.get();
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
	public Octuple<A, B, C, D, E, F, G, H> deepClone() {
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
	public <T, N, O, P, Q, R, S, U> Octuple<T, N, O, P, Q, R, S, U> map(
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
	public <T> Octuple<T, B, C, D, E, F, G, H> mapFirst(final OctFunction<A, B, C, D, E, F, G, H, T> func) {
		return this.getParent().mapFirst(func);
	}

	@Override
	public <T> Octuple<A, T, C, D, E, F, G, H> mapSecond(final OctFunction<A, B, C, D, E, F, G, H, T> func) {
		return this.getParent().mapSecond(func);
	}

	@Override
	public <T> Octuple<A, B, T, D, E, F, G, H> mapThird(final OctFunction<A, B, C, D, E, F, G, H, T> func) {
		return this.getParent().mapThird(func);
	}

	@Override
	public <T> Octuple<A, B, C, T, E, F, G, H> mapFourth(final OctFunction<A, B, C, D, E, F, G, H, T> func) {
		return this.getParent().mapFourth(func);
	}

	@Override
	public <T> Octuple<A, B, C, D, T, F, G, H> mapFifth(final OctFunction<A, B, C, D, E, F, G, H, T> func) {
		return this.getParent().mapFifth(func);
	}

	@Override
	public <T> Octuple<A, B, C, D, E, T, G, H> mapSixth(final OctFunction<A, B, C, D, E, F, G, H, T> func) {
		return this.getParent().mapSixth(func);
	}

	@Override
	public <T> Octuple<A, B, C, D, E, F, T, H> mapSeventh(final OctFunction<A, B, C, D, E, F, G, H, T> func) {
		return this.getParent().mapSeventh(func);
	}

	@Override
	public <T> Octuple<A, B, C, D, E, F, G, T> mapEighth(final OctFunction<A, B, C, D, E, F, G, H, T> func) {
		return this.getParent().mapEighth(func);
	}

	@Override
	public <T> Octuple<?, ?, ?, ?, ?, ?, ?, ?> map(final int i, final Function<Object, T> func) {
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
	public Octuple<A, B, C, D, E, F, G, H> asViewReadOnly() {
		return new ReadOnlyOctupleView<>(this);
	}

	@Override
	public Octuple<A, B, C, D, E, F, G, H> asWeakViewReadOnly() {
		return new ReadOnlyOctupleWeakView<>(this);
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

	@Override
	public H getEighth() {
		return this.getParent().getEighth();
	}

}
