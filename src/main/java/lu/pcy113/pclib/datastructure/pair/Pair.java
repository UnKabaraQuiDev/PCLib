package lu.pcy113.pclib.datastructure.pair;

import java.util.function.Supplier;

import lu.pcy113.pclib.PCUtils;
import lu.pcy113.pclib.datastructure.tuple.Tuple;
import lu.pcy113.pclib.impl.DeepCloneable;

public class Pair<K, V> implements DeepCloneable, Tuple {

	protected K key;
	protected V value;

	public Pair() {
	}

	public Pair(K k, V v) {
		this.key = k;
		this.value = v;
	}

	public K getKey() {
		return key;
	}

	public V getValue() {
		return value;
	}

	public Pair<K, V> setKey(K key) {
		this.key = key;
		return this;
	}

	public Pair<K, V> setValue(V value) {
		this.value = value;
		return this;
	}

	public boolean hasKey() {
		return key != null;
	}

	public boolean hasValue() {
		return value != null;
	}

	@Override
	public int elementCount() {
		return 2;
	}

	@Override
	public <T> T get(int i) {
		if (i < 0 || i > 1) {
			throw new IndexOutOfBoundsException(i + " <> [0..1]");
		}
		return i == 0 ? (T) key : (T) value;
	}

	@Override
	public Object[] asArray() {
		return new Object[] { key, value };
	}

	@Override
	public <T> T[] asArray(T[] arr) {
		return PCUtils.fillArray(arr, key, value);
	}

	@Override
	public <T> T[] asArray(Supplier<T[]> arr) {
		return PCUtils.fillArray(arr.get(), key, value);
	}

	@Override
	public Pair<K, V> clone() {
		try {
			return (Pair<K, V>) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String toString() {
		return String.format("{%s=%s}", key, value);
	}

}
