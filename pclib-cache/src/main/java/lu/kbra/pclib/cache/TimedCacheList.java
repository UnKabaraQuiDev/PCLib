package lu.kbra.pclib.cache;

public class TimedCacheList<K, V> extends CacheList<K, V, TimedCacheEntry<V>> {

	protected long expirationTimeMillis;

	public TimedCacheList(final long expirationTimeMillis) {
		this.expirationTimeMillis = expirationTimeMillis;
	}

	public boolean isExpired(final TimedCacheEntry<V> entry) {
		return entry.isExpired(this.expirationTimeMillis);
	}

	@Override
	protected TimedCacheEntry<V> createEntry(final V value) {
		return new TimedCacheEntry<>(value, System.currentTimeMillis());
	}

	@Override
	protected boolean isInvalid(final TimedCacheEntry<V> entry) {
		return entry == null || this.isExpired(entry);
	}

}
