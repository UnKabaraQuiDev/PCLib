package lu.pcy113.pclib;

public class ReadOnlyPair<K, V> extends Pair<K, V> implements Cloneable {

	public ReadOnlyPair() {
		super();
	}

	public ReadOnlyPair(K k, V v) {
		super(k, v);
	}

	@Override
	public void setKey(K key) {
		throw new RuntimeException(new IllegalAccessException("Operation not permitted on readonly pair !"));
	}

	@Override
	public void setValue(V value) {
		throw new RuntimeException(new IllegalAccessException("Operation not permitted on readonly pair !"));
	}

	@Override
	public String toString() {
		return String.format("%s(readonly)", super.toString());
	}

	@Override
	protected ReadOnlyPair<K, V> clone() {
		return new ReadOnlyPair<>(key, value);
	}

}
