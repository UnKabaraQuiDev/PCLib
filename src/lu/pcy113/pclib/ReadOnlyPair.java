package lu.pcy113.pclib;

public class ReadOnlyPair<K, V> implements Cloneable {
	
	protected K key;
	protected V value;
	
	public ReadOnlyPair(K key, V value) {
		this.key = key;
		this.value = value;
	}
	
	public K getKey() {
		return key;
	}
	public V getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return String.format("{%s=%s}(readonly)", key, value);
	}
	
	@Override
	protected ReadOnlyPair<K, V> clone() {
		return new ReadOnlyPair<>(key, value);
	}
	
}
