package lu.kbra.pclib.datastructure.pair;

public final class Pairs {

	public static <K, V> Pair<K, V> empty() {
		return new Pair<>();
	}

	public static <K, V> Pair<K, V> pair(final K key, final V value) {
		return new Pair<>(key, value);
	}

	public static <K, V> ReadOnlyPair<K, V> readOnly(final K key, final V value) {
		return new ReadOnlyPair<>(key, value);
	}

}
