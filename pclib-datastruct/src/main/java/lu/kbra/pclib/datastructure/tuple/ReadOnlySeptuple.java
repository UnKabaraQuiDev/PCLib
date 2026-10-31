package lu.kbra.pclib.datastructure.tuple;

import java.util.function.Function;

import lu.kbra.pclib.impl.function.SeptFunction;

public interface ReadOnlySeptuple<A, B, C, D, E, F, G> extends Septuple<A, B, C, D, E, F, G>, ReadOnlyTuple {

	@Override
	Septuple<A, B, C, D, E, F, G> clone();

	@Override
	MutableSeptuple<A, B, C, D, E, F, G> cloneMutable();

	@Override
	ReadOnlySeptuple<A, B, C, D, E, F, G> cloneReadOnly();

	@Override
	ReadOnlySeptuple<A, B, C, D, E, F, G> deepClone();

	@Override
	MutableSeptuple<A, B, C, D, E, F, G> deepCloneMutable();

	@Override
	ReadOnlySeptuple<A, B, C, D, E, F, G> deepCloneReadOnly();

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
	<T, N, O, P, Q, R, S> ReadOnlySeptuple<T, N, O, P, Q, R, S> map(
			final SeptFunction<A, B, C, D, E, F, G, T> funcFirst,
			final SeptFunction<A, B, C, D, E, F, G, N> funcSecond,
			final SeptFunction<A, B, C, D, E, F, G, O> funcThird,
			final SeptFunction<A, B, C, D, E, F, G, P> funcFourth,
			final SeptFunction<A, B, C, D, E, F, G, Q> funcFifth,
			final SeptFunction<A, B, C, D, E, F, G, R> funcSixth,
			final SeptFunction<A, B, C, D, E, F, G, S> funcSeventh);

	@Override
	<T> ReadOnlySeptuple<T, B, C, D, E, F, G> mapFirst(final SeptFunction<A, B, C, D, E, F, G, T> func);

	@Override
	<T> ReadOnlySeptuple<A, T, C, D, E, F, G> mapSecond(final SeptFunction<A, B, C, D, E, F, G, T> func);

	@Override
	<T> ReadOnlySeptuple<A, B, T, D, E, F, G> mapThird(final SeptFunction<A, B, C, D, E, F, G, T> func);

	@Override
	<T> ReadOnlySeptuple<A, B, C, T, E, F, G> mapFourth(final SeptFunction<A, B, C, D, E, F, G, T> func);

	@Override
	<T> ReadOnlySeptuple<A, B, C, D, T, F, G> mapFifth(final SeptFunction<A, B, C, D, E, F, G, T> func);

	@Override
	<T> ReadOnlySeptuple<A, B, C, D, E, T, G> mapSixth(final SeptFunction<A, B, C, D, E, F, G, T> func);

	@Override
	<T> ReadOnlySeptuple<A, B, C, D, E, F, T> mapSeventh(final SeptFunction<A, B, C, D, E, F, G, T> func);

	@Override
	<T> ReadOnlySeptuple<?, ?, ?, ?, ?, ?, ?> map(final int i, final Function<Object, T> func);

}
