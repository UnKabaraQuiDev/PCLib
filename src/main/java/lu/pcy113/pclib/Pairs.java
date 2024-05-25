package lu.pcy113.pclib;

public final class Pairs {

	public static final <K, V> Pair<K, V> empty() {
		return new Pair<K, V>();
	}

	public static final <K, V> Pair<K, V> pair(K key, V value) {
		return new Pair<K, V>(key, value);
	}

	public static final <K, V> ReadOnlyPair<K, V> readOnly(K key, V value) {
		return new ReadOnlyPair<K, V>(key, value);
	}

}
