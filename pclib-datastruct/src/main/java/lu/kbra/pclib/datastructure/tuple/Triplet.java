package lu.kbra.pclib.datastructure.tuple;

import java.util.function.Function;

import lu.kbra.pclib.datastructure.DeepCloneable;
import lu.kbra.pclib.impl.function.TriFunction;

public interface Triplet<A, B, C> extends DeepCloneable, Tuple {

	@Override
	Object[] asArray();

	@Override
	Triplet<A, B, C> clone();

	@Override
	EditableTriplet<A, B, C> cloneEditable();

	@Override
	ReadOnlyTriplet<A, B, C> cloneReadOnly();

	@Override
	default int elementCount() {
		return 3;
	}

	@Override
	<T> T get(int i);

	A getFirst();

	B getSecond();

	C getThird();

	boolean hasFirst();

	boolean hasSecond();

	boolean hasThird();

	<T> T map(TriFunction<A, B, C, T> func);

	<T, U, V> Triplet<T, U, V>
			map(TriFunction<A, B, C, T> funcFirst, TriFunction<A, B, C, U> funcSecond, TriFunction<A, B, C, V> funcThird);

	<T> Triplet<T, B, C> mapFirst(TriFunction<A, B, C, T> func);

	<T> Triplet<A, T, C> mapSecond(TriFunction<A, B, C, T> func);

	<T> Triplet<A, B, T> mapThird(TriFunction<A, B, C, T> func);

	<T> Triplet<?, ?, ?> map(int index, Function<Object, T> func);

}
