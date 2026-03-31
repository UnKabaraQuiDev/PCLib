package lu.kbra.pclib.datastructure.pair;

public class ReadOnlyPair<K, V> extends Pair<K, V> {

	public ReadOnlyPair() {
	}

	public ReadOnlyPair(final K k, final V v) {
		super(k, v);
	}

	@Override
	@Deprecated
	public ReadOnlyPair<K, V> setKey(final K key) {
		throw new UnsupportedOperationException("Operation not permitted on readonly pair !");
	}

	@Override
	@Deprecated
	public ReadOnlyPair<K, V> setValue(final V value) {
		throw new UnsupportedOperationException("Operation not permitted on readonly pair !");
	}

	@Override
	public ReadOnlyPair<K, V> clone() {
		return (ReadOnlyPair<K, V>) super.clone();
	}

	@Override
	public String toString() {
		return String.format("%s(readonly)", super.toString());
	}

}
