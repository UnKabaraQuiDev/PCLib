package lu.kbra.pclib.datastructure.tuple;

import java.util.function.BiFunction;

public class ReadOnlyPair<K, V> extends Pair<K, V> {

	public ReadOnlyPair() {
	}

	public ReadOnlyPair(final K k, final V v) {
		super(k, v);
	}

	@Override
	public ReadOnlyPair<K, V> clone() {
		return new ReadOnlyPair<>(key, value);
	}

	public <T> Pair<T, V> mapKey(final BiFunction<K, V, T> func) {
		return super.map((k, v) -> new ReadOnlyPair<>(func.apply(k, v), v));
	}

	public <T> Pair<K, T> mapValue(final BiFunction<K, V, T> func) {
		return super.map((k, v) -> new ReadOnlyPair<>(k, func.apply(k, v)));
	}

	public <T, N> Pair<T, N> map(final BiFunction<K, V, T> funcKey, final BiFunction<K, V, N> funcValue) {
		return super.map((k, v) -> new ReadOnlyPair<>(funcKey.apply(k, v), funcValue.apply(k, v)));
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
	public String toString() {
		return String.format("%s(readonly)", super.toString());
	}

}
