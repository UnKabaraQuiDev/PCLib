package lu.kbra.pclib.datastructure.tuple;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface ReadOnlyPair<K, V> extends Pair<K, V>, ReadOnlyTuple {

	@Override
	Pair<K, V> clone();

	@Override
	MutablePair<K, V> cloneMutable();

	@Override
	ReadOnlyPair<K, V> cloneReadOnly();

	@Override
	ReadOnlyPair<K, V> deepClone();

	@Override
	MutablePair<K, V> deepCloneMutable();

	@Override
	ReadOnlyPair<K, V> deepCloneReadOnly();

	@Override
	K getKey();

	@Override
	V getValue();

	@Override
	boolean hasKey();

	@Override
	boolean hasValue();

	@Override
	<T> T map(final BiFunction<K, V, T> func);

	@Override
	<T, N> ReadOnlyPair<T, N> map(final BiFunction<K, V, T> funcKey, final BiFunction<K, V, N> funcValue);

	@Override
	<T> ReadOnlyPair<T, V> mapKey(final BiFunction<K, V, T> func);

	@Override
	<T> ReadOnlyPair<K, T> mapValue(final BiFunction<K, V, T> func);

	@Override
	<T> ReadOnlyPair<?, ?> map(final int i, final Function<Object, T> func);

}
