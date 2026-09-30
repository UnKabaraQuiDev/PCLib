package lu.kbra.pclib.datastructure.tuple;

import java.util.function.Function;

import lu.kbra.pclib.datastructure.DeepCloneable;
import lu.kbra.pclib.impl.function.OctFunction;

public interface Octuple<A, B, C, D, E, F, G, H> extends DeepCloneable, Tuple {

	@Override
	Object[] asArray();

	@Override
	Octuple<A, B, C, D, E, F, G, H> clone();

	@Override
	EditableOctuple<A, B, C, D, E, F, G, H> cloneEditable();

	@Override
	ReadOnlyOctuple<A, B, C, D, E, F, G, H> cloneReadOnly();

	@Override
	int elementCount();

	@Override
	<T> T get(int i);

	A getFirst();

	B getSecond();

	C getThird();

	D getFourth();

	E getFifth();

	F getSixth();

	G getSeventh();

	H getEighth();

	boolean hasFirst();

	boolean hasSecond();

	boolean hasThird();

	boolean hasFourth();

	boolean hasFifth();

	boolean hasSixth();

	boolean hasSeventh();

	boolean hasEighth();

	<T> T map(OctFunction<A, B, C, D, E, F, G, H, T> func);

	<T, N, O, P, Q, R, S, U> Octuple<T, N, O, P, Q, R, S, U> map(
			OctFunction<A, B, C, D, E, F, G, H, T> funcFirst,
			OctFunction<A, B, C, D, E, F, G, H, N> funcSecond,
			OctFunction<A, B, C, D, E, F, G, H, O> funcThird,
			OctFunction<A, B, C, D, E, F, G, H, P> funcFourth,
			OctFunction<A, B, C, D, E, F, G, H, Q> funcFifth,
			OctFunction<A, B, C, D, E, F, G, H, R> funcSixth,
			OctFunction<A, B, C, D, E, F, G, H, S> funcSeventh,
			OctFunction<A, B, C, D, E, F, G, H, U> funcEighth);

	<T> Octuple<T, B, C, D, E, F, G, H> mapFirst(OctFunction<A, B, C, D, E, F, G, H, T> func);

	<T> Octuple<A, T, C, D, E, F, G, H> mapSecond(OctFunction<A, B, C, D, E, F, G, H, T> func);

	<T> Octuple<A, B, T, D, E, F, G, H> mapThird(OctFunction<A, B, C, D, E, F, G, H, T> func);

	<T> Octuple<A, B, C, T, E, F, G, H> mapFourth(OctFunction<A, B, C, D, E, F, G, H, T> func);

	<T> Octuple<A, B, C, D, T, F, G, H> mapFifth(OctFunction<A, B, C, D, E, F, G, H, T> func);

	<T> Octuple<A, B, C, D, E, T, G, H> mapSixth(OctFunction<A, B, C, D, E, F, G, H, T> func);

	<T> Octuple<A, B, C, D, E, F, T, H> mapSeventh(OctFunction<A, B, C, D, E, F, G, H, T> func);

	<T> Octuple<A, B, C, D, E, F, G, T> mapEighth(OctFunction<A, B, C, D, E, F, G, H, T> func);

	<T> Octuple<?, ?, ?, ?, ?, ?, ?, ?> map(int i, Function<Object, T> func);

}
