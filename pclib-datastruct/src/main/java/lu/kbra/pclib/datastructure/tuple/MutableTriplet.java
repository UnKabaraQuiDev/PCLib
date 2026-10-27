package lu.kbra.pclib.datastructure.tuple;

import java.util.function.Function;

import lu.kbra.pclib.impl.function.TriFunction;

public interface MutableTriplet<A, B, C> extends Triplet<A, B, C>, MutableTuple {

	@Override
	Triplet<A, B, C> clone();

	@Override
	MutableTriplet<A, B, C> cloneMutable();

	@Override
	ReadOnlyTriplet<A, B, C> cloneReadOnly();

	@Override
	MutableTriplet<A, B, C> deepClone();

	@Override
	MutableTriplet<A, B, C> deepCloneMutable();

	@Override
	ReadOnlyTriplet<A, B, C> deepCloneReadOnly();

	@Override
	boolean hasFirst();

	@Override
	boolean hasSecond();

	@Override
	boolean hasThird();

	@Override
	<T> T map(final TriFunction<A, B, C, T> func);

	@Override
	<T, U, V> MutableTriplet<T, U, V>
			map(final TriFunction<A, B, C, T> funcFirst, final TriFunction<A, B, C, U> funcSecond, final TriFunction<A, B, C, V> funcThird);

	@Override
	<T> MutableTriplet<T, B, C> mapFirst(final TriFunction<A, B, C, T> func);

	@Override
	<T> MutableTriplet<A, T, C> mapSecond(final TriFunction<A, B, C, T> func);

	@Override
	<T> MutableTriplet<A, B, T> mapThird(final TriFunction<A, B, C, T> func);

	@Override
	<T> MutableTriplet<?, ?, ?> map(final int index, final Function<Object, T> func);

	MutableTriplet<A, B, C>
			mut(final TriFunction<A, B, C, A> funcFirst, final TriFunction<A, B, C, B> funcSecond, final TriFunction<A, B, C, C> funcThird);

	MutableTriplet<A, B, C> mutFirst(final TriFunction<A, B, C, A> func);

	MutableTriplet<A, B, C> mutSecond(final TriFunction<A, B, C, B> func);

	MutableTriplet<A, B, C> mutThird(final TriFunction<A, B, C, C> func);

	@Override
	<T> MutableTriplet<A, B, C> mut(final int index, final Function<Object, T> func);

	Triplet<A, B, C> setFirst(final A first);

	Triplet<A, B, C> setSecond(final B second);

	Triplet<A, B, C> setThird(final C third);

	@Override
	<T> MutableTriplet<A, B, C> set(final int index, final T element);

	@Override
	default MutableTriplet<A, B, C> viewMutable() {
		return new MutableTripletView<>(this);
	}

}
