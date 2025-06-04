package lu.pcy113.pclib.datastructure.pair;

import lu.pcy113.pclib.impl.DeepCloneable;

public class ReadOnlyPair<K, V> extends Pair<K, V> {

	public ReadOnlyPair() {
		super();
	}

	public ReadOnlyPair(K k, V v) {
		super(k, v);
	}

	@Override
	@Deprecated
	public ReadOnlyPair<K, V> setKey(K key) {
		throw new UnsupportedOperationException("Operation not permitted on readonly pair !");
	}

	@Override
	@Deprecated
	public ReadOnlyPair<K, V> setValue(V value) {
		throw new UnsupportedOperationException("Operation not permitted on readonly pair !");
	}

	@Override
	public String toString() {
		return String.format("%s(readonly)", super.toString());
	}

	@Override
	public ReadOnlyPair<K, V> clone() {
		return new ReadOnlyPair<>(key, value);
	}

	@Override
	public ReadOnlyPair<K, V> deepClone() {
		return new ReadOnlyPair<>((K) ((DeepCloneable) key).deepClone(), (V) ((DeepCloneable) value).deepClone());
	}

}
