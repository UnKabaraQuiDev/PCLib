package lu.kbra.pclib.datastructure.tuple;

import java.util.function.Function;

import lu.kbra.pclib.impl.function.SextFunction;

public interface MutableSextuple<A, B, C, D, E, F> extends Sextuple<A, B, C, D, E, F>, MutableTuple {

	@Override
	Sextuple<A, B, C, D, E, F> clone();

	@Override
	MutableSextuple<A, B, C, D, E, F> cloneMutable();

	@Override
	ReadOnlySextuple<A, B, C, D, E, F> cloneReadOnly();

	@Override
	MutableSextuple<A, B, C, D, E, F> deepClone();

	@Override
	MutableSextuple<A, B, C, D, E, F> deepCloneMutable();

	@Override
	ReadOnlySextuple<A, B, C, D, E, F> deepCloneReadOnly();

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
	<T> T map(final SextFunction<A, B, C, D, E, F, T> func);

	@Override
	<T, U, V, W, X, Y> MutableSextuple<T, U, V, W, X, Y> map(
			final SextFunction<A, B, C, D, E, F, T> funcFirst,
			final SextFunction<A, B, C, D, E, F, U> funcSecond,
			final SextFunction<A, B, C, D, E, F, V> funcThird,
			final SextFunction<A, B, C, D, E, F, W> funcFourth,
			final SextFunction<A, B, C, D, E, F, X> funcFifth,
			final SextFunction<A, B, C, D, E, F, Y> funcSixth);

	@Override
	<T> MutableSextuple<T, B, C, D, E, F> mapFirst(final SextFunction<A, B, C, D, E, F, T> func);

	@Override
	<T> MutableSextuple<A, T, C, D, E, F> mapSecond(final SextFunction<A, B, C, D, E, F, T> func);

	@Override
	<T> MutableSextuple<A, B, T, D, E, F> mapThird(final SextFunction<A, B, C, D, E, F, T> func);

	@Override
	<T> MutableSextuple<A, B, C, T, E, F> mapFourth(final SextFunction<A, B, C, D, E, F, T> func);

	@Override
	<T> MutableSextuple<A, B, C, D, T, F> mapFifth(final SextFunction<A, B, C, D, E, F, T> func);

	@Override
	<T> MutableSextuple<A, B, C, D, E, T> mapSixth(final SextFunction<A, B, C, D, E, F, T> func);

	@Override
	<T> MutableSextuple<?, ?, ?, ?, ?, ?> map(final int index, final Function<Object, T> func);

	MutableSextuple<A, B, C, D, E, F> mut(
			final SextFunction<A, B, C, D, E, F, A> funcFirst,
			final SextFunction<A, B, C, D, E, F, B> funcSecond,
			final SextFunction<A, B, C, D, E, F, C> funcThird,
			final SextFunction<A, B, C, D, E, F, D> funcFourth,
			final SextFunction<A, B, C, D, E, F, E> funcFifth,
			final SextFunction<A, B, C, D, E, F, F> funcSixth);

	MutableSextuple<A, B, C, D, E, F> mutFirst(final SextFunction<A, B, C, D, E, F, A> func);

	MutableSextuple<A, B, C, D, E, F> mutSecond(final SextFunction<A, B, C, D, E, F, B> func);

	MutableSextuple<A, B, C, D, E, F> mutThird(final SextFunction<A, B, C, D, E, F, C> func);

	MutableSextuple<A, B, C, D, E, F> mutFourth(final SextFunction<A, B, C, D, E, F, D> func);

	MutableSextuple<A, B, C, D, E, F> mutFifth(final SextFunction<A, B, C, D, E, F, E> func);

	MutableSextuple<A, B, C, D, E, F> mutSixth(final SextFunction<A, B, C, D, E, F, F> func);

	@Override
	<T> MutableSextuple<A, B, C, D, E, F> mut(final int index, final Function<Object, T> func);

	Sextuple<A, B, C, D, E, F> setFirst(final A first);

	Sextuple<A, B, C, D, E, F> setSecond(final B second);

	Sextuple<A, B, C, D, E, F> setThird(final C third);

	Sextuple<A, B, C, D, E, F> setFourth(final D fourth);

	Sextuple<A, B, C, D, E, F> setFifth(final E fifth);

	Sextuple<A, B, C, D, E, F> setSixth(final F sixth);

	@Override
	<T> MutableSextuple<A, B, C, D, E, F> set(final int index, final T element);

	@Override
	MutableSextuple<A, B, C, D, E, F> asSynchronized();

	@Override
	default MutableSextuple<A, B, C, D, E, F> asViewMutable() {
		return new MutableSextupleView<>(this);
	}

	@Override
	default MutableSextuple<A, B, C, D, E, F> asWeakViewMutable() {
		return new MutableSextupleWeakView<>(this);
	}

}
