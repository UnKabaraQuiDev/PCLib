package lu.kbra.pclib.datastructure.tuple;

import java.util.Objects;
import java.util.function.BiFunction;

import lu.kbra.pclib.datastructure.DeepCloneable;

public class Pair<K, V> implements DeepCloneable, Tuple {

	protected K key;
	protected V value;

	public Pair() {
	}

	public Pair(final K k, final V v) {
		this.key = k;
		this.value = v;
	}

	@Override
	public Object[] asArray() {
		return new Object[] { this.key, this.value };
	}

	@Override
	public Pair<K, V> clone() {
		return new Pair<>(this.key, this.value);
	}

	@Override
	public int elementCount() {
		return 2;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || this.getClass() != obj.getClass()) {
			return false;
		}
		final Pair other = (Pair) obj;
		return Objects.equals(this.key, other.key) && Objects.equals(this.value, other.value);
	}

	@Override
	public <T> T get(final int i) {
		if (i < 0 || i > 1) {
			throw new IndexOutOfBoundsException(i + " <> [0..1]");
		}
		return i == 0 ? (T) this.key : (T) this.value;
	}

	public K getKey() {
		return this.key;
	}

	public V getValue() {
		return this.value;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.key, this.value);
	}

	public boolean hasKey() {
		return this.key != null;
	}

	public boolean hasValue() {
		return this.value != null;
	}

	public <T> T map(final BiFunction<K, V, T> func) {
		return func.apply(this.key, this.value);
	}

	public <T, N> Pair<T, N> map(final BiFunction<K, V, T> funcKey, final BiFunction<K, V, N> funcValue) {
		return this.map((k, v) -> new Pair<>(funcKey.apply(k, v), funcValue.apply(k, v)));
	}

	public <T> Pair<T, V> mapKey(final BiFunction<K, V, T> func) {
		return this.map((k, v) -> new Pair<>(func.apply(k, v), v));
	}

	public <T> Pair<K, T> mapValue(final BiFunction<K, V, T> func) {
		return this.map((k, v) -> new Pair<>(k, func.apply(k, v)));
	}

	public Pair<K, V> setKey(final K key) {
		this.key = key;
		return this;
	}

	public Pair<K, V> setValue(final V value) {
		this.value = value;
		return this;
	}

	@Override
	public String toString() {
		return String.format("{%s=%s}", this.key, this.value);
	}

}
