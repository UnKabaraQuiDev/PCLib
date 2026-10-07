package lu.kbra.pclib.datastructure.tuple;

import java.util.function.Function;

import lu.kbra.pclib.datastructure.DeepCloneable;
import lu.kbra.pclib.impl.function.QuintFunction;

public interface Quintuple<A, B, C, D, E> extends DeepCloneable, Tuple {

	@Override
	Object[] asArray();

	@Override
	Quintuple<A, B, C, D, E> clone();

	@Override
	EditableQuintuple<A, B, C, D, E> cloneEditable();

	@Override
	ReadOnlyQuintuple<A, B, C, D, E> cloneReadOnly();

	@Override
	default int elementCount() {
		return 5;
	}

	@Override
	<T> T get(int i);

	A getFirst();

	B getSecond();

	C getThird();

	D getFourth();

	E getFifth();

	boolean hasFirst();

	boolean hasSecond();

	boolean hasThird();

	boolean hasFourth();

	boolean hasFifth();

	<T> T map(QuintFunction<A, B, C, D, E, T> func);

	<T, U, V, W, X> Quintuple<T, U, V, W, X> map(
			QuintFunction<A, B, C, D, E, T> funcFirst,
			QuintFunction<A, B, C, D, E, U> funcSecond,
			QuintFunction<A, B, C, D, E, V> funcThird,
			QuintFunction<A, B, C, D, E, W> funcFourth,
			QuintFunction<A, B, C, D, E, X> funcFifth);

	<T> Quintuple<T, B, C, D, E> mapFirst(QuintFunction<A, B, C, D, E, T> func);

	<T> Quintuple<A, T, C, D, E> mapSecond(QuintFunction<A, B, C, D, E, T> func);

	<T> Quintuple<A, B, T, D, E> mapThird(QuintFunction<A, B, C, D, E, T> func);

	<T> Quintuple<A, B, C, T, E> mapFourth(QuintFunction<A, B, C, D, E, T> func);

	<T> Quintuple<A, B, C, D, T> mapFifth(QuintFunction<A, B, C, D, E, T> func);

	<T> Quintuple<?, ?, ?, ?, ?> map(int index, Function<Object, T> func);

}
