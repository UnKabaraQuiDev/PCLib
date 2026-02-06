package lu.kbra.pclib.cache;

public class TimedCacheList<K, V> extends CacheList<K, V, TimedCacheEntry<V>> {

	protected long expirationTimeMillis;

	public TimedCacheList(long expirationTimeMillis) {
		this.expirationTimeMillis = expirationTimeMillis;
	}

	protected TimedCacheEntry<V> createEntry(V value) {
		return new TimedCacheEntry<V>(value, System.currentTimeMillis());
	}

	public boolean isExpired(TimedCacheEntry<V> entry) {
		return entry.isExpired(expirationTimeMillis);
	}

	@Override
	protected boolean isInvalid(TimedCacheEntry<V> entry) {
		return entry == null || isExpired(entry);
	}

}
