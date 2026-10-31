package lu.kbra.pclib.datastructure.tuple;

import java.util.function.Function;

import lu.kbra.pclib.impl.function.QuadFunction;

public interface ReadOnlyQuadruple<A, B, C, D> extends Quadruple<A, B, C, D>, ReadOnlyTuple {

	@Override
	Quadruple<A, B, C, D> clone();

	@Override
	MutableQuadruple<A, B, C, D> cloneMutable();

	@Override
	ReadOnlyQuadruple<A, B, C, D> cloneReadOnly();

	@Override
	ReadOnlyQuadruple<A, B, C, D> deepClone();

	@Override
	MutableQuadruple<A, B, C, D> deepCloneMutable();

	@Override
	ReadOnlyQuadruple<A, B, C, D> deepCloneReadOnly();

	@Override
	A getFirst();

	@Override
	B getSecond();

	@Override
	C getThird();

	@Override
	D getFourth();

	@Override
	boolean hasFirst();

	@Override
	boolean hasSecond();

	@Override
	boolean hasThird();

	@Override
	boolean hasFourth();

	@Override
	<T> T map(final QuadFunction<A, B, C, D, T> func);

	@Override
	<T, N, O, P> ReadOnlyQuadruple<T, N, O, P> map(
			final QuadFunction<A, B, C, D, T> funcFirst,
			final QuadFunction<A, B, C, D, N> funcSecond,
			final QuadFunction<A, B, C, D, O> funcThird,
			final QuadFunction<A, B, C, D, P> funcFourth);

	@Override
	<T> ReadOnlyQuadruple<T, B, C, D> mapFirst(final QuadFunction<A, B, C, D, T> func);

	@Override
	<T> ReadOnlyQuadruple<A, T, C, D> mapSecond(final QuadFunction<A, B, C, D, T> func);

	@Override
	<T> ReadOnlyQuadruple<A, B, T, D> mapThird(final QuadFunction<A, B, C, D, T> func);

	@Override
	<T> ReadOnlyQuadruple<A, B, C, T> mapFourth(final QuadFunction<A, B, C, D, T> func);

	@Override
	<T> ReadOnlyQuadruple<?, ?, ?, ?> map(final int i, final Function<Object, T> func);

}
