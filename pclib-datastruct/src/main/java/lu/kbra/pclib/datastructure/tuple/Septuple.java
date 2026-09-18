package lu.kbra.pclib.datastructure.tuple;

import java.util.function.Function;

import lu.kbra.pclib.datastructure.DeepCloneable;
import lu.kbra.pclib.impl.function.SeptFunction;

public interface Septuple<A, B, C, D, E, F, G> extends DeepCloneable, Tuple {

	@Override
	Object[] asArray();

	@Override
	Septuple<A, B, C, D, E, F, G> clone();

	@Override
	EditableSeptuple<A, B, C, D, E, F, G> cloneEditable();

	@Override
	ReadOnlySeptuple<A, B, C, D, E, F, G> cloneReadOnly();

	@Override
	default int elementCount() {
		return 7;
	}

	@Override
	<T> T get(int i);

	A getFirst();

	B getSecond();

	C getThird();

	D getFourth();

	E getFifth();

	F getSixth();

	G getSeventh();

	boolean hasFirst();

	boolean hasSecond();

	boolean hasThird();

	boolean hasFourth();

	boolean hasFifth();

	boolean hasSixth();

	boolean hasSeventh();

	<T> T map(SeptFunction<A, B, C, D, E, F, G, T> func);

	<T, N, O, P, Q, R, S> Septuple<T, N, O, P, Q, R, S> map(
			SeptFunction<A, B, C, D, E, F, G, T> funcFirst,
			SeptFunction<A, B, C, D, E, F, G, N> funcSecond,
			SeptFunction<A, B, C, D, E, F, G, O> funcThird,
			SeptFunction<A, B, C, D, E, F, G, P> funcFourth,
			SeptFunction<A, B, C, D, E, F, G, Q> funcFifth,
			SeptFunction<A, B, C, D, E, F, G, R> funcSixth,
			SeptFunction<A, B, C, D, E, F, G, S> funcSeventh);

	<T> Septuple<T, B, C, D, E, F, G> mapFirst(SeptFunction<A, B, C, D, E, F, G, T> func);

	<T> Septuple<A, T, C, D, E, F, G> mapSecond(SeptFunction<A, B, C, D, E, F, G, T> func);

	<T> Septuple<A, B, T, D, E, F, G> mapThird(SeptFunction<A, B, C, D, E, F, G, T> func);

	<T> Septuple<A, B, C, T, E, F, G> mapFourth(SeptFunction<A, B, C, D, E, F, G, T> func);

	<T> Septuple<A, B, C, D, T, F, G> mapFifth(SeptFunction<A, B, C, D, E, F, G, T> func);

	<T> Septuple<A, B, C, D, E, T, G> mapSixth(SeptFunction<A, B, C, D, E, F, G, T> func);

	<T> Septuple<A, B, C, D, E, F, T> mapSeventh(SeptFunction<A, B, C, D, E, F, G, T> func);

	<T> Septuple<?, ?, ?, ?, ?, ?, ?> map(int i, Function<Object, T> func);

}
