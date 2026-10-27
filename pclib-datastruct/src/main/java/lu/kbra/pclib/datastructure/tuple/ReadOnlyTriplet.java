package lu.kbra.pclib.datastructure.tuple;

import java.util.function.Function;

import lu.kbra.pclib.impl.function.TriFunction;

public interface ReadOnlyTriplet<A, B, C> extends Triplet<A, B, C>, ReadOnlyTuple {

	@Override
	Triplet<A, B, C> clone();

	@Override
	MutableTriplet<A, B, C> cloneMutable();

	@Override
	ReadOnlyTriplet<A, B, C> cloneReadOnly();

	@Override
	ReadOnlyTriplet<A, B, C> deepClone();

	@Override
	MutableTriplet<A, B, C> deepCloneMutable();

	@Override
	ReadOnlyTriplet<A, B, C> deepCloneReadOnly();

	@Override
	A getFirst();

	@Override
	B getSecond();

	@Override
	C getThird();

	@Override
	boolean hasFirst();

	@Override
	boolean hasSecond();

	@Override
	boolean hasThird();

	@Override
	<T> T map(final TriFunction<A, B, C, T> func);

	@Override
	<T, N, O> ReadOnlyTriplet<T, N, O>
			map(final TriFunction<A, B, C, T> funcFirst, final TriFunction<A, B, C, N> funcSecond, final TriFunction<A, B, C, O> funcThird);

	@Override
	<T> ReadOnlyTriplet<T, B, C> mapFirst(final TriFunction<A, B, C, T> func);

	@Override
	<T> ReadOnlyTriplet<A, T, C> mapSecond(final TriFunction<A, B, C, T> func);

	@Override
	<T> ReadOnlyTriplet<A, B, T> mapThird(final TriFunction<A, B, C, T> func);

	@Override
	<T> ReadOnlyTriplet<?, ?, ?> map(final int i, final Function<Object, T> func);

}
