package lu.kbra.pclib.cache;

public abstract class CacheEntry<V> {

	private final V value;

	public CacheEntry(final V value) {
		this.value = value;
	}

	public V getValue() {
		return this.value;
	}

}
