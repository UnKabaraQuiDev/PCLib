package lu.kbra.pclib.cache;

public class TimedCacheEntry<V> extends CacheEntry<V> {

	private final long timestamp;

	public TimedCacheEntry(V value, long timestamp) {
		super(value);
		this.timestamp = timestamp;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public boolean isExpired(long expirationTimeMillis) {
		return (System.currentTimeMillis() - timestamp) > expirationTimeMillis;
	}

}