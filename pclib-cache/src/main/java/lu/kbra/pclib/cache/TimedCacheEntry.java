package lu.kbra.pclib.cache;

public class TimedCacheEntry<V> extends CacheEntry<V> {

	private final long timestamp;

	public TimedCacheEntry(final V value, final long timestamp) {
		super(value);
		this.timestamp = timestamp;
	}

	public long getTimestamp() {
		return this.timestamp;
	}

	public boolean isExpired(final long expirationTimeMillis) {
		return System.currentTimeMillis() - this.timestamp > expirationTimeMillis;
	}

}
