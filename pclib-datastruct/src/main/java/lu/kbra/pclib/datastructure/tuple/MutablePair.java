package lu.kbra.pclib.datastructure.tuple;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface MutablePair<K, V> extends Pair<K, V>, MutableTuple {

	@Override
	Pair<K, V> clone();

	@Override
	MutablePair<K, V> cloneMutable();

	@Override
	ReadOnlyPair<K, V> cloneReadOnly();

	@Override
	MutablePair<K, V> deepClone();

	@Override
	MutablePair<K, V> deepCloneMutable();

	@Override
	ReadOnlyPair<K, V> deepCloneReadOnly();

	@Override
	boolean hasKey();

	@Override
	boolean hasValue();

	@Override
	<T> T map(final BiFunction<K, V, T> func);

	@Override
	<T, N> MutablePair<T, N> map(final BiFunction<K, V, T> funcKey, final BiFunction<K, V, N> funcValue);

	@Override
	<T> MutablePair<T, V> mapKey(final BiFunction<K, V, T> func);

	@Override
	<T> MutablePair<K, T> mapValue(final BiFunction<K, V, T> func);

	@Override
	<T> MutablePair<?, ?> map(final int index, final Function<Object, T> func);

	MutablePair<K, V> mut(final BiFunction<K, V, K> funcKey, final BiFunction<K, V, V> funcValue);

	MutablePair<K, V> mutKey(final BiFunction<K, V, K> func);

	MutablePair<K, V> mutValue(final BiFunction<K, V, V> func);

	@Override
	<T> MutablePair<K, V> mut(final int index, final Function<Object, T> func);

	Pair<K, V> setKey(final K key);

	Pair<K, V> setValue(final V value);

	@Override
	<T> MutablePair<K, V> set(final int index, final T element);

	@Override
	default MutablePair<K, V> viewMutable() {
		return new MutablePairView<>(this);
	}

}
