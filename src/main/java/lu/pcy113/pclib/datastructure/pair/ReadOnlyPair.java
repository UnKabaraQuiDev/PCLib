package lu.pcy113.pclib.datastructure.pair;

public class ReadOnlyPair<K, V> extends Pair<K, V> implements Cloneable {

	public ReadOnlyPair() {
		super();
	}

	public ReadOnlyPair(K k, V v) {
		super(k, v);
	}

	@Override
	@Deprecated
	public void setKey(K key) {
		throw new UnsupportedOperationException("Operation not permitted on readonly pair !");
	}

	@Override
	@Deprecated
	public void setValue(V value) {
		throw new UnsupportedOperationException("Operation not permitted on readonly pair !");
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
