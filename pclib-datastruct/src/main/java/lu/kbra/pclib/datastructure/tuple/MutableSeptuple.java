package lu.kbra.pclib.datastructure.tuple;

import java.util.function.Function;

import lu.kbra.pclib.impl.function.SeptFunction;

public interface MutableSeptuple<A, B, C, D, E, F, G> extends Septuple<A, B, C, D, E, F, G>, MutableTuple {

	@Override
	Septuple<A, B, C, D, E, F, G> clone();

	@Override
	MutableSeptuple<A, B, C, D, E, F, G> cloneMutable();

	@Override
	ReadOnlySeptuple<A, B, C, D, E, F, G> cloneReadOnly();

	@Override
	MutableSeptuple<A, B, C, D, E, F, G> deepClone();

	@Override
	MutableSeptuple<A, B, C, D, E, F, G> deepCloneMutable();

	@Override
	ReadOnlySeptuple<A, B, C, D, E, F, G> deepCloneReadOnly();

	@Override
	A getFirst();

	@Override
	B getSecond();

	@Override
	C getThird();

	@Override
	D getFourth();

	@Override
	E getFifth();

	@Override
	F getSixth();

	@Override
	G getSeventh();

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
	<T> T map(final SeptFunction<A, B, C, D, E, F, G, T> func);

	@Override
	<T, N, O, P, Q, R, S> MutableSeptuple<T, N, O, P, Q, R, S> map(
			final SeptFunction<A, B, C, D, E, F, G, T> funcFirst,
			final SeptFunction<A, B, C, D, E, F, G, N> funcSecond,
			final SeptFunction<A, B, C, D, E, F, G, O> funcThird,
			final SeptFunction<A, B, C, D, E, F, G, P> funcFourth,
			final SeptFunction<A, B, C, D, E, F, G, Q> funcFifth,
			final SeptFunction<A, B, C, D, E, F, G, R> funcSixth,
			final SeptFunction<A, B, C, D, E, F, G, S> funcSeventh);

	@Override
	<T> MutableSeptuple<T, B, C, D, E, F, G> mapFirst(final SeptFunction<A, B, C, D, E, F, G, T> func);

	@Override
	<T> MutableSeptuple<A, T, C, D, E, F, G> mapSecond(final SeptFunction<A, B, C, D, E, F, G, T> func);

	@Override
	<T> MutableSeptuple<A, B, T, D, E, F, G> mapThird(final SeptFunction<A, B, C, D, E, F, G, T> func);

	@Override
	<T> MutableSeptuple<A, B, C, T, E, F, G> mapFourth(final SeptFunction<A, B, C, D, E, F, G, T> func);

	@Override
	<T> MutableSeptuple<A, B, C, D, T, F, G> mapFifth(final SeptFunction<A, B, C, D, E, F, G, T> func);

	@Override
	<T> MutableSeptuple<A, B, C, D, E, T, G> mapSixth(final SeptFunction<A, B, C, D, E, F, G, T> func);

	@Override
	<T> MutableSeptuple<A, B, C, D, E, F, T> mapSeventh(final SeptFunction<A, B, C, D, E, F, G, T> func);

	@Override
	<T> MutableSeptuple<?, ?, ?, ?, ?, ?, ?> map(final int index, final Function<Object, T> func);

	MutableSeptuple<A, B, C, D, E, F, G> mut(
			final SeptFunction<A, B, C, D, E, F, G, A> funcFirst,
			final SeptFunction<A, B, C, D, E, F, G, B> funcSecond,
			final SeptFunction<A, B, C, D, E, F, G, C> funcThird,
			final SeptFunction<A, B, C, D, E, F, G, D> funcFourth,
			final SeptFunction<A, B, C, D, E, F, G, E> funcFifth,
			final SeptFunction<A, B, C, D, E, F, G, F> funcSixth,
			final SeptFunction<A, B, C, D, E, F, G, G> funcSeventh);

	MutableSeptuple<A, B, C, D, E, F, G> mutFirst(final SeptFunction<A, B, C, D, E, F, G, A> func);

	MutableSeptuple<A, B, C, D, E, F, G> mutSecond(final SeptFunction<A, B, C, D, E, F, G, B> func);

	MutableSeptuple<A, B, C, D, E, F, G> mutThird(final SeptFunction<A, B, C, D, E, F, G, C> func);

	MutableSeptuple<A, B, C, D, E, F, G> mutFourth(final SeptFunction<A, B, C, D, E, F, G, D> func);

	MutableSeptuple<A, B, C, D, E, F, G> mutFifth(final SeptFunction<A, B, C, D, E, F, G, E> func);

	MutableSeptuple<A, B, C, D, E, F, G> mutSixth(final SeptFunction<A, B, C, D, E, F, G, F> func);

	MutableSeptuple<A, B, C, D, E, F, G> mutSeventh(final SeptFunction<A, B, C, D, E, F, G, G> func);

	@Override
	<T> MutableSeptuple<A, B, C, D, E, F, G> mut(final int index, final Function<Object, T> func);

	MutableSeptuple<A, B, C, D, E, F, G> setFirst(final A first);

	MutableSeptuple<A, B, C, D, E, F, G> setSecond(final B second);

	MutableSeptuple<A, B, C, D, E, F, G> setThird(final C third);

	MutableSeptuple<A, B, C, D, E, F, G> setFourth(final D fourth);

	MutableSeptuple<A, B, C, D, E, F, G> setFifth(final E fifth);

	MutableSeptuple<A, B, C, D, E, F, G> setSixth(final F sixth);

	MutableSeptuple<A, B, C, D, E, F, G> setSeventh(final G seventh);

	@Override
	<T> MutableSeptuple<A, B, C, D, E, F, G> set(final int i, final T value);

	@Override
	MutableSeptuple<A, B, C, D, E, F, G> asSynchronized();

	@Override
	default MutableSeptuple<A, B, C, D, E, F, G> asViewMutable() {
		return new MutableSeptupleView<>(this);
	}

	@Override
	default MutableSeptuple<A, B, C, D, E, F, G> asWeakViewMutable() {
		return new MutableSeptupleWeakView<>(this);
	}

}
