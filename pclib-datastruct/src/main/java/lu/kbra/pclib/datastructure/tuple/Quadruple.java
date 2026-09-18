package lu.kbra.pclib.datastructure.tuple;

import java.util.function.Function;

import lu.kbra.pclib.datastructure.DeepCloneable;
import lu.kbra.pclib.impl.function.QuadFunction;

public interface Quadruple<A, B, C, D> extends DeepCloneable, Tuple {

	@Override
	Object[] asArray();

	@Override
	Quadruple<A, B, C, D> clone();

	@Override
	EditableQuadruple<A, B, C, D> cloneEditable();

	@Override
	ReadOnlyQuadruple<A, B, C, D> cloneReadOnly();

	@Override
	default int elementCount() {
		return 4;
	}

	@Override
	<T> T get(int i);

	A getFirst();

	B getSecond();

	C getThird();

	D getFourth();

	boolean hasFirst();

	boolean hasSecond();

	boolean hasThird();

	boolean hasFourth();

	<T> T map(QuadFunction<A, B, C, D, T> func);

	<T, U, V, W> Quadruple<T, U, V, W> map(
			QuadFunction<A, B, C, D, T> funcFirst,
			QuadFunction<A, B, C, D, U> funcSecond,
			QuadFunction<A, B, C, D, V> funcThird,
			QuadFunction<A, B, C, D, W> funcFourth);

	<T> Quadruple<T, B, C, D> mapFirst(QuadFunction<A, B, C, D, T> func);

	<T> Quadruple<A, T, C, D> mapSecond(QuadFunction<A, B, C, D, T> func);

	<T> Quadruple<A, B, T, D> mapThird(QuadFunction<A, B, C, D, T> func);

	<T> Quadruple<A, B, C, T> mapFourth(QuadFunction<A, B, C, D, T> func);

	<T> Quadruple<?, ?, ?, ?> map(int index, Function<Object, T> func);

}
