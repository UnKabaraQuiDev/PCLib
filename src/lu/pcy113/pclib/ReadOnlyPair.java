package lu.pcy113.pclib;

public class ReadOnlyPair<K, V> {
	
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
	
}
