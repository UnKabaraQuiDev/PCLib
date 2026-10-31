package lu.kbra.pclib.datastructure.tuple;

import java.util.function.Function;

import lu.kbra.pclib.impl.function.OctFunction;

public interface MutableOctuple<A, B, C, D, E, F, G, H> extends Octuple<A, B, C, D, E, F, G, H>, MutableTuple {

	@Override
	Octuple<A, B, C, D, E, F, G, H> clone();

	@Override
	MutableOctuple<A, B, C, D, E, F, G, H> cloneMutable();

	@Override
	ReadOnlyOctuple<A, B, C, D, E, F, G, H> cloneReadOnly();

	@Override
	MutableOctuple<A, B, C, D, E, F, G, H> deepClone();

	@Override
	MutableOctuple<A, B, C, D, E, F, G, H> deepCloneMutable();

	@Override
	ReadOnlyOctuple<A, B, C, D, E, F, G, H> deepCloneReadOnly();

	@Override
	boolean hasFirst();

	@Override
	boolean hasSecond();

	@Override
	boolean hasThird();

	@Override
	boolean hasFourth();

	@Override
	boolean hasFifth();

	@Override
	boolean hasSixth();

	@Override
	boolean hasSeventh();

	@Override
	boolean hasEighth();

	@Override
	<T> T map(final OctFunction<A, B, C, D, E, F, G, H, T> func);

	@Override
	<T, N, O, P, Q, R, S, U> MutableOctuple<T, N, O, P, Q, R, S, U> map(
			final OctFunction<A, B, C, D, E, F, G, H, T> funcFirst,
			final OctFunction<A, B, C, D, E, F, G, H, N> funcSecond,
			final OctFunction<A, B, C, D, E, F, G, H, O> funcThird,
			final OctFunction<A, B, C, D, E, F, G, H, P> funcFourth,
			final OctFunction<A, B, C, D, E, F, G, H, Q> funcFifth,
			final OctFunction<A, B, C, D, E, F, G, H, R> funcSixth,
			final OctFunction<A, B, C, D, E, F, G, H, S> funcSeventh,
			final OctFunction<A, B, C, D, E, F, G, H, U> funcEighth);

	@Override
	<T> MutableOctuple<T, B, C, D, E, F, G, H> mapFirst(final OctFunction<A, B, C, D, E, F, G, H, T> func);

	@Override
	<T> MutableOctuple<A, T, C, D, E, F, G, H> mapSecond(final OctFunction<A, B, C, D, E, F, G, H, T> func);

	@Override
	<T> MutableOctuple<A, B, T, D, E, F, G, H> mapThird(final OctFunction<A, B, C, D, E, F, G, H, T> func);

	@Override
	<T> MutableOctuple<A, B, C, T, E, F, G, H> mapFourth(final OctFunction<A, B, C, D, E, F, G, H, T> func);

	@Override
	<T> MutableOctuple<A, B, C, D, T, F, G, H> mapFifth(final OctFunction<A, B, C, D, E, F, G, H, T> func);

	@Override
	<T> MutableOctuple<A, B, C, D, E, T, G, H> mapSixth(final OctFunction<A, B, C, D, E, F, G, H, T> func);

	@Override
	<T> MutableOctuple<A, B, C, D, E, F, T, H> mapSeventh(final OctFunction<A, B, C, D, E, F, G, H, T> func);

	@Override
	<T> MutableOctuple<A, B, C, D, E, F, G, T> mapEighth(final OctFunction<A, B, C, D, E, F, G, H, T> func);

	@Override
	<T> MutableOctuple<?, ?, ?, ?, ?, ?, ?, ?> map(final int index, final Function<Object, T> func);

	MutableOctuple<A, B, C, D, E, F, G, H> mut(
			final OctFunction<A, B, C, D, E, F, G, H, A> funcFirst,
			final OctFunction<A, B, C, D, E, F, G, H, B> funcSecond,
			final OctFunction<A, B, C, D, E, F, G, H, C> funcThird,
			final OctFunction<A, B, C, D, E, F, G, H, D> funcFourth,
			final OctFunction<A, B, C, D, E, F, G, H, E> funcFifth,
			final OctFunction<A, B, C, D, E, F, G, H, F> funcSixth,
			final OctFunction<A, B, C, D, E, F, G, H, G> funcSeventh,
			final OctFunction<A, B, C, D, E, F, G, H, H> funcEighth);

	MutableOctuple<A, B, C, D, E, F, G, H> mutFirst(final OctFunction<A, B, C, D, E, F, G, H, A> func);

	MutableOctuple<A, B, C, D, E, F, G, H> mutSecond(final OctFunction<A, B, C, D, E, F, G, H, B> func);

	MutableOctuple<A, B, C, D, E, F, G, H> mutThird(final OctFunction<A, B, C, D, E, F, G, H, C> func);

	MutableOctuple<A, B, C, D, E, F, G, H> mutFourth(final OctFunction<A, B, C, D, E, F, G, H, D> func);

	MutableOctuple<A, B, C, D, E, F, G, H> mutFifth(final OctFunction<A, B, C, D, E, F, G, H, E> func);

	MutableOctuple<A, B, C, D, E, F, G, H> mutSixth(final OctFunction<A, B, C, D, E, F, G, H, F> func);

	MutableOctuple<A, B, C, D, E, F, G, H> mutSeventh(final OctFunction<A, B, C, D, E, F, G, H, G> func);

	MutableOctuple<A, B, C, D, E, F, G, H> mutEighth(final OctFunction<A, B, C, D, E, F, G, H, H> func);

	@Override
	<T> MutableOctuple<A, B, C, D, E, F, G, H> mut(final int index, final Function<Object, T> func);

	MutableOctuple<A, B, C, D, E, F, G, H> setFirst(final A first);

	MutableOctuple<A, B, C, D, E, F, G, H> setSecond(final B second);

	MutableOctuple<A, B, C, D, E, F, G, H> setThird(final C third);

	MutableOctuple<A, B, C, D, E, F, G, H> setFourth(final D fourth);

	MutableOctuple<A, B, C, D, E, F, G, H> setFifth(final E fifth);

	MutableOctuple<A, B, C, D, E, F, G, H> setSixth(final F sixth);

	MutableOctuple<A, B, C, D, E, F, G, H> setSeventh(final G seventh);

	MutableOctuple<A, B, C, D, E, F, G, H> setEighth(final H eighth);

	@Override
	<T> MutableOctuple<A, B, C, D, E, F, G, H> set(final int i, final T value);

	@Override
	MutableOctuple<A, B, C, D, E, F, G, H> asSynchronized();

	@Override
	default MutableOctuple<A, B, C, D, E, F, G, H> asViewMutable() {
		return new MutableOctupleView<>(this);
	}

	@Override
	default MutableOctuple<A, B, C, D, E, F, G, H> asWeakViewMutable() {
		return new MutableOctupleWeakView<>(this);
	}

}
