package lu.kbra.pclib.datastructure.tuple;

import java.util.function.Function;

import lu.kbra.pclib.impl.function.QuadFunction;

public interface MutableQuadruple<A, B, C, D> extends Quadruple<A, B, C, D>, MutableTuple {

	@Override
	Quadruple<A, B, C, D> clone();

	@Override
	MutableQuadruple<A, B, C, D> cloneMutable();

	@Override
	ReadOnlyQuadruple<A, B, C, D> cloneReadOnly();

	@Override
	MutableQuadruple<A, B, C, D> deepClone();

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
	<T, U, V, W> MutableQuadruple<T, U, V, W> map(
			final QuadFunction<A, B, C, D, T> funcFirst,
			final QuadFunction<A, B, C, D, U> funcSecond,
			final QuadFunction<A, B, C, D, V> funcThird,
			final QuadFunction<A, B, C, D, W> funcFourth);

	@Override
	<T> MutableQuadruple<T, B, C, D> mapFirst(final QuadFunction<A, B, C, D, T> func);

	@Override
	<T> MutableQuadruple<A, T, C, D> mapSecond(final QuadFunction<A, B, C, D, T> func);

	@Override
	<T> MutableQuadruple<A, B, T, D> mapThird(final QuadFunction<A, B, C, D, T> func);

	@Override
	<T> MutableQuadruple<A, B, C, T> mapFourth(final QuadFunction<A, B, C, D, T> func);

	@Override
	<T> MutableQuadruple<?, ?, ?, ?> map(final int index, final Function<Object, T> func);

	MutableQuadruple<A, B, C, D> mut(
			final QuadFunction<A, B, C, D, A> funcFirst,
			final QuadFunction<A, B, C, D, B> funcSecond,
			final QuadFunction<A, B, C, D, C> funcThird,
			final QuadFunction<A, B, C, D, D> funcFourth);

	MutableQuadruple<A, B, C, D> mutFirst(final QuadFunction<A, B, C, D, A> func);

	MutableQuadruple<A, B, C, D> mutSecond(final QuadFunction<A, B, C, D, B> func);

	MutableQuadruple<A, B, C, D> mutThird(final QuadFunction<A, B, C, D, C> func);

	MutableQuadruple<A, B, C, D> mutFourth(final QuadFunction<A, B, C, D, D> func);

	@Override
	<T> MutableQuadruple<A, B, C, D> mut(final int index, final Function<Object, T> func);

	Quadruple<A, B, C, D> setFirst(final A first);

	Quadruple<A, B, C, D> setSecond(final B second);

	Quadruple<A, B, C, D> setThird(final C third);

	Quadruple<A, B, C, D> setFourth(final D fourth);

	@Override
	<T> MutableQuadruple<A, B, C, D> set(final int index, final T element);

	@Override
	MutableQuadruple<A, B, C, D> asSynchronized();

	@Override
	default MutableQuadruple<A, B, C, D> asViewMutable() {
		return new MutableQuadrupleView<>(this);
	}

	@Override
	default MutableQuadruple<A, B, C, D> asWeakViewMutable() {
		return new MutableQuadrupleWeakView<>(this);
	}

}
