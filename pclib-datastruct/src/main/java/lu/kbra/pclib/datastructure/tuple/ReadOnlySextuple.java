package lu.kbra.pclib.datastructure.tuple;

import java.util.function.Function;

import lu.kbra.pclib.impl.function.SextFunction;

public interface ReadOnlySextuple<A, B, C, D, E, F> extends Sextuple<A, B, C, D, E, F>, ReadOnlyTuple {

	@Override
	Sextuple<A, B, C, D, E, F> clone();

	@Override
	MutableSextuple<A, B, C, D, E, F> cloneMutable();

	@Override
	ReadOnlySextuple<A, B, C, D, E, F> cloneReadOnly();

	@Override
	ReadOnlySextuple<A, B, C, D, E, F> deepClone();

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
	<T, N, O, P, Q, R> ReadOnlySextuple<T, N, O, P, Q, R> map(
			final SextFunction<A, B, C, D, E, F, T> funcFirst,
			final SextFunction<A, B, C, D, E, F, N> funcSecond,
			final SextFunction<A, B, C, D, E, F, O> funcThird,
			final SextFunction<A, B, C, D, E, F, P> funcFourth,
			final SextFunction<A, B, C, D, E, F, Q> funcFifth,
			final SextFunction<A, B, C, D, E, F, R> funcSixth);

	@Override
	<T> ReadOnlySextuple<T, B, C, D, E, F> mapFirst(final SextFunction<A, B, C, D, E, F, T> func);

	@Override
	<T> ReadOnlySextuple<A, T, C, D, E, F> mapSecond(final SextFunction<A, B, C, D, E, F, T> func);

	@Override
	<T> ReadOnlySextuple<A, B, T, D, E, F> mapThird(final SextFunction<A, B, C, D, E, F, T> func);

	@Override
	<T> ReadOnlySextuple<A, B, C, T, E, F> mapFourth(final SextFunction<A, B, C, D, E, F, T> func);

	@Override
	<T> ReadOnlySextuple<A, B, C, D, T, F> mapFifth(final SextFunction<A, B, C, D, E, F, T> func);

	@Override
	<T> ReadOnlySextuple<A, B, C, D, E, T> mapSixth(final SextFunction<A, B, C, D, E, F, T> func);

	@Override
	<T> ReadOnlySextuple<?, ?, ?, ?, ?, ?> map(final int i, final Function<Object, T> func);

}
