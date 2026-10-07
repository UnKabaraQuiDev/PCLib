package lu.kbra.pclib.datastructure.tuple;

import java.util.function.Function;

import lu.kbra.pclib.datastructure.DeepCloneable;
import lu.kbra.pclib.impl.function.SextFunction;

public interface Sextuple<A, B, C, D, E, F> extends DeepCloneable, Tuple {

	@Override
	Object[] asArray();

	@Override
	Sextuple<A, B, C, D, E, F> clone();

	@Override
	EditableSextuple<A, B, C, D, E, F> cloneEditable();

	@Override
	ReadOnlySextuple<A, B, C, D, E, F> cloneReadOnly();

	@Override
	default int elementCount() {
		return 6;
	}

	@Override
	<T> T get(int i);

	A getFirst();

	B getSecond();

	C getThird();

	D getFourth();

	E getFifth();

	F getSixth();

	boolean hasFirst();

	boolean hasSecond();

	boolean hasThird();

	boolean hasFourth();

	boolean hasFifth();

	boolean hasSixth();

	<T> T map(SextFunction<A, B, C, D, E, F, T> func);

	<T, U, V, W, X, Y> Sextuple<T, U, V, W, X, Y> map(
			SextFunction<A, B, C, D, E, F, T> funcFirst,
			SextFunction<A, B, C, D, E, F, U> funcSecond,
			SextFunction<A, B, C, D, E, F, V> funcThird,
			SextFunction<A, B, C, D, E, F, W> funcFourth,
			SextFunction<A, B, C, D, E, F, X> funcFifth,
			SextFunction<A, B, C, D, E, F, Y> funcSixth);

	<T> Sextuple<T, B, C, D, E, F> mapFirst(SextFunction<A, B, C, D, E, F, T> func);

	<T> Sextuple<A, T, C, D, E, F> mapSecond(SextFunction<A, B, C, D, E, F, T> func);

	<T> Sextuple<A, B, T, D, E, F> mapThird(SextFunction<A, B, C, D, E, F, T> func);

	<T> Sextuple<A, B, C, T, E, F> mapFourth(SextFunction<A, B, C, D, E, F, T> func);

	<T> Sextuple<A, B, C, D, T, F> mapFifth(SextFunction<A, B, C, D, E, F, T> func);

	<T> Sextuple<A, B, C, D, E, T> mapSixth(SextFunction<A, B, C, D, E, F, T> func);

	<T> Sextuple<?, ?, ?, ?, ?, ?> map(int index, Function<Object, T> func);

}
