package lu.kbra.pclib.datastructure.tuple;

import java.util.function.Function;

import lu.kbra.pclib.impl.function.QuintFunction;

public interface ReadOnlyQuintuple<A, B, C, D, E> extends Quintuple<A, B, C, D, E>, ReadOnlyTuple {

	@Override
	Quintuple<A, B, C, D, E> clone();

	@Override
	MutableQuintuple<A, B, C, D, E> cloneMutable();

	@Override
	ReadOnlyQuintuple<A, B, C, D, E> cloneReadOnly();

	@Override
	ReadOnlyQuintuple<A, B, C, D, E> deepClone();

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
	<T, N, O, P, Q> ReadOnlyQuintuple<T, N, O, P, Q> map(
			final QuintFunction<A, B, C, D, E, T> funcFirst,
			final QuintFunction<A, B, C, D, E, N> funcSecond,
			final QuintFunction<A, B, C, D, E, O> funcThird,
			final QuintFunction<A, B, C, D, E, P> funcFourth,
			final QuintFunction<A, B, C, D, E, Q> funcFifth);

	@Override
	<T> ReadOnlyQuintuple<T, B, C, D, E> mapFirst(final QuintFunction<A, B, C, D, E, T> func);

	@Override
	<T> ReadOnlyQuintuple<A, T, C, D, E> mapSecond(final QuintFunction<A, B, C, D, E, T> func);

	@Override
	<T> ReadOnlyQuintuple<A, B, T, D, E> mapThird(final QuintFunction<A, B, C, D, E, T> func);

	@Override
	<T> ReadOnlyQuintuple<A, B, C, T, E> mapFourth(final QuintFunction<A, B, C, D, E, T> func);

	@Override
	<T> ReadOnlyQuintuple<A, B, C, D, T> mapFifth(final QuintFunction<A, B, C, D, E, T> func);

	@Override
	<T> ReadOnlyQuintuple<?, ?, ?, ?, ?> map(final int i, final Function<Object, T> func);

}
