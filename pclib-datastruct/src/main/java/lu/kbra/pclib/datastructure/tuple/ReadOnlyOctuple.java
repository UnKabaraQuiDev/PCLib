package lu.kbra.pclib.datastructure.tuple;

import java.util.function.Function;

import lu.kbra.pclib.impl.function.OctFunction;

public interface ReadOnlyOctuple<A, B, C, D, E, F, G, H> extends Octuple<A, B, C, D, E, F, G, H>, ReadOnlyTuple {

	@Override
	Octuple<A, B, C, D, E, F, G, H> clone();

	@Override
	MutableOctuple<A, B, C, D, E, F, G, H> cloneMutable();

	@Override
	ReadOnlyOctuple<A, B, C, D, E, F, G, H> cloneReadOnly();

	@Override
	ReadOnlyOctuple<A, B, C, D, E, F, G, H> deepClone();

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
	<T, N, O, P, Q, R, S, U> ReadOnlyOctuple<T, N, O, P, Q, R, S, U> map(
			final OctFunction<A, B, C, D, E, F, G, H, T> funcFirst,
			final OctFunction<A, B, C, D, E, F, G, H, N> funcSecond,
			final OctFunction<A, B, C, D, E, F, G, H, O> funcThird,
			final OctFunction<A, B, C, D, E, F, G, H, P> funcFourth,
			final OctFunction<A, B, C, D, E, F, G, H, Q> funcFifth,
			final OctFunction<A, B, C, D, E, F, G, H, R> funcSixth,
			final OctFunction<A, B, C, D, E, F, G, H, S> funcSeventh,
			final OctFunction<A, B, C, D, E, F, G, H, U> funcEighth);

	@Override
	<T> ReadOnlyOctuple<T, B, C, D, E, F, G, H> mapFirst(final OctFunction<A, B, C, D, E, F, G, H, T> func);

	@Override
	<T> ReadOnlyOctuple<A, T, C, D, E, F, G, H> mapSecond(final OctFunction<A, B, C, D, E, F, G, H, T> func);

	@Override
	<T> ReadOnlyOctuple<A, B, T, D, E, F, G, H> mapThird(final OctFunction<A, B, C, D, E, F, G, H, T> func);

	@Override
	<T> ReadOnlyOctuple<A, B, C, T, E, F, G, H> mapFourth(final OctFunction<A, B, C, D, E, F, G, H, T> func);

	@Override
	<T> ReadOnlyOctuple<A, B, C, D, T, F, G, H> mapFifth(final OctFunction<A, B, C, D, E, F, G, H, T> func);

	@Override
	<T> ReadOnlyOctuple<A, B, C, D, E, T, G, H> mapSixth(final OctFunction<A, B, C, D, E, F, G, H, T> func);

	@Override
	<T> ReadOnlyOctuple<A, B, C, D, E, F, T, H> mapSeventh(final OctFunction<A, B, C, D, E, F, G, H, T> func);

	@Override
	<T> ReadOnlyOctuple<A, B, C, D, E, F, G, T> mapEighth(final OctFunction<A, B, C, D, E, F, G, H, T> func);

	@Override
	<T> ReadOnlyOctuple<?, ?, ?, ?, ?, ?, ?, ?> map(final int i, final Function<Object, T> func);

}
