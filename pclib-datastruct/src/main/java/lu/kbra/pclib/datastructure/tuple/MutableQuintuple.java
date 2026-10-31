package lu.kbra.pclib.datastructure.tuple;

import java.util.function.Function;

import lu.kbra.pclib.impl.function.QuintFunction;

public interface MutableQuintuple<A, B, C, D, E> extends Quintuple<A, B, C, D, E>, MutableTuple {

	@Override
	Quintuple<A, B, C, D, E> clone();

	@Override
	MutableQuintuple<A, B, C, D, E> cloneMutable();

	@Override
	ReadOnlyQuintuple<A, B, C, D, E> cloneReadOnly();

	@Override
	MutableQuintuple<A, B, C, D, E> deepClone();

	@Override
	MutableQuintuple<A, B, C, D, E> deepCloneMutable();

	@Override
	ReadOnlyQuintuple<A, B, C, D, E> deepCloneReadOnly();

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
	<T> T map(final QuintFunction<A, B, C, D, E, T> func);

	@Override
	<T, U, V, W, X> MutableQuintuple<T, U, V, W, X> map(
			final QuintFunction<A, B, C, D, E, T> funcFirst,
			final QuintFunction<A, B, C, D, E, U> funcSecond,
			final QuintFunction<A, B, C, D, E, V> funcThird,
			final QuintFunction<A, B, C, D, E, W> funcFourth,
			final QuintFunction<A, B, C, D, E, X> funcFifth);

	@Override
	<T> MutableQuintuple<T, B, C, D, E> mapFirst(final QuintFunction<A, B, C, D, E, T> func);

	@Override
	<T> MutableQuintuple<A, T, C, D, E> mapSecond(final QuintFunction<A, B, C, D, E, T> func);

	@Override
	<T> MutableQuintuple<A, B, T, D, E> mapThird(final QuintFunction<A, B, C, D, E, T> func);

	@Override
	<T> MutableQuintuple<A, B, C, T, E> mapFourth(final QuintFunction<A, B, C, D, E, T> func);

	@Override
	<T> MutableQuintuple<A, B, C, D, T> mapFifth(final QuintFunction<A, B, C, D, E, T> func);

	@Override
	<T> MutableQuintuple<?, ?, ?, ?, ?> map(final int index, final Function<Object, T> func);

	MutableQuintuple<A, B, C, D, E> mut(
			final QuintFunction<A, B, C, D, E, A> funcFirst,
			final QuintFunction<A, B, C, D, E, B> funcSecond,
			final QuintFunction<A, B, C, D, E, C> funcThird,
			final QuintFunction<A, B, C, D, E, D> funcFourth,
			final QuintFunction<A, B, C, D, E, E> funcFifth);

	MutableQuintuple<A, B, C, D, E> mutFirst(final QuintFunction<A, B, C, D, E, A> func);

	MutableQuintuple<A, B, C, D, E> mutSecond(final QuintFunction<A, B, C, D, E, B> func);

	MutableQuintuple<A, B, C, D, E> mutThird(final QuintFunction<A, B, C, D, E, C> func);

	MutableQuintuple<A, B, C, D, E> mutFourth(final QuintFunction<A, B, C, D, E, D> func);

	MutableQuintuple<A, B, C, D, E> mutFifth(final QuintFunction<A, B, C, D, E, E> func);

	@Override
	<T> MutableQuintuple<A, B, C, D, E> mut(final int index, final Function<Object, T> func);

	Quintuple<A, B, C, D, E> setFirst(final A first);

	Quintuple<A, B, C, D, E> setSecond(final B second);

	Quintuple<A, B, C, D, E> setThird(final C third);

	Quintuple<A, B, C, D, E> setFourth(final D fourth);

	Quintuple<A, B, C, D, E> setFifth(final E fifth);

	@Override
	<T> MutableQuintuple<A, B, C, D, E> set(final int index, final T element);

	@Override
	MutableQuintuple<A, B, C, D, E> asSynchronized();

	@Override
	default MutableQuintuple<A, B, C, D, E> asViewMutable() {
		return new MutableQuintupleView<>(this);
	}

	@Override
	default MutableQuintuple<A, B, C, D, E> asWeakViewMutable() {
		return new MutableQuintupleWeakView<>(this);
	}

}
