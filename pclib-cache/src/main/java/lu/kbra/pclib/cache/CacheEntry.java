package lu.kbra.pclib.cache;

public abstract class CacheEntry<V> {

	private final V value;

	public CacheEntry(V value) {
		this.value = value;
	}

	public V getValue() {
		return value;
	}

}
