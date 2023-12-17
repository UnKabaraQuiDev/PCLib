package lu.pcy113.pclib;

public final class Pairs {

	public static final Pair<Object, Object> empty() {
		return new Pair<Object, Object>();
	}

	public static final <K, V> ReadOnlyPair<K, V> pair(K key, V value) {
		return new Pair<K, V>(key, value);
	}

	public static final <K, V> ReadOnlyPair<K, V> readOnly(K key, V value) {
		return new ReadOnlyPair<K, V>(key, value);
	}

}
