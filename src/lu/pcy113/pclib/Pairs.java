package lu.pcy113.pclib;

public final class Pairs {
	
	public static final Pair<Object, Object> EMPTY = new Pair<>();
	
	public static final <K, V> ReadOnlyPair<K, V> readOnly(K key, V value) {
		return new ReadOnlyPair<K, V>(key, value);
	}
	
}
