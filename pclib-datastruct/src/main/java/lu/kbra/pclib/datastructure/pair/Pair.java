package lu.kbra.pclib.datastructure.pair;

import lu.kbra.pclib.datastructure.DeepCloneable;
import lu.kbra.pclib.datastructure.tuple.Tuple;

public class Pair<K, V> implements DeepCloneable, Tuple {

	protected K key;
	protected V value;

	public Pair() {
	}

	public Pair(final K k, final V v) {
		this.key = k;
		this.value = v;
	}

	public K getKey() {
		return this.key;
	}

	public V getValue() {
		return this.value;
	}

	public Pair<K, V> setKey(final K key) {
		this.key = key;
		return this;
	}

	public Pair<K, V> setValue(final V value) {
		this.value = value;
		return this;
	}

	public boolean hasKey() {
		return this.key != null;
	}

	public boolean hasValue() {
		return this.value != null;
	}

	@Override
	public int elementCount() {
		return 2;
	}

	@Override
	public <T> T get(final int i) {
		if (i < 0 || i > 1) {
			throw new IndexOutOfBoundsException(i + " <> [0..1]");
		}
		return i == 0 ? (T) this.key : (T) this.value;
	}

	@Override
	public Object[] asArray() {
		return new Object[] { this.key, this.value };
	}

	@Override
	public Pair<K, V> clone() {
		try {
			return (Pair<K, V>) super.clone();
		} catch (final CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String toString() {
		return String.format("{%s=%s}", this.key, this.value);
	}

}
