package lu.kbra.pclib.datastructure.tuple;

import java.util.function.BiFunction;
import java.util.function.Function;

import lu.kbra.pclib.datastructure.DeepCloneable;

public interface Pair<K, V> extends DeepCloneable, Tuple {

	@Override
	Object[] asArray();

	@Override
	Pair<K, V> clone();

	@Override
	MutablePair<K, V> cloneMutable();

	@Override
	ReadOnlyPair<K, V> cloneReadOnly();

	@Override
	Pair<K, V> deepClone();

	@Override
	MutablePair<K, V> deepCloneMutable();

	@Override
	ReadOnlyPair<K, V> deepCloneReadOnly();

	@Override
	default int elementCount() {
		return 2;
	}

	@Override
	<T> T get(int i);

	K getKey();

	V getValue();

	boolean hasKey();

	boolean hasValue();

	<T> T map(BiFunction<K, V, T> func);

	<T, N> Pair<T, N> map(BiFunction<K, V, T> funcKey, BiFunction<K, V, N> funcValue);

	<T> Pair<T, V> mapKey(BiFunction<K, V, T> func);

	<T> Pair<K, T> mapValue(BiFunction<K, V, T> func);

	@Override
	<T> Pair<?, ?> map(int index, final Function<Object, T> func);

	@Override
	default Pair<K, V> viewReadOnly() {
		return new ReadOnlyPairView<>(this);
	}

}
