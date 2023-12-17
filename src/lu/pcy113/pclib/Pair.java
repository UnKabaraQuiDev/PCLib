package lu.pcy113.pclib;

public class Pair<K, V> extends ReadOnlyPair<K, V> {

	public Pair() {
		super(null, null);
	}

	public Pair(K key, V value) {
		super(key, value);
	}

	public void setKey(K key) {
		this.key = key;
	}

	public void setValue(V value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return String.format("{%s=%s}", key, value);
	}

	@Override
	protected Pair<K, V> clone() {
		return new Pair<>(key, value);
	}

}
