package lu.kbra.pclib.datastructure.tuple;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

import lu.kbra.pclib.datastructure.DeepCloneable;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReadOnlyPairImpl<K, V> implements ReadOnlyPair<K, V> {

	private final K key;
	private final V value;

	@Override
	public Object[] asArray() {
		return new Object[] { this.key, this.value };
	}

	@Override
	public Pair<K, V> clone() {
		return this.cloneReadOnly();
	}

	@Override
	public MutablePair<K, V> cloneMutable() {
		return new MutablePairImpl<>(this.key, this.value);
	}

	@Override
	public ReadOnlyPair<K, V> cloneReadOnly() {
		return new ReadOnlyPairImpl<>(this.key, this.value);
	}

	@Override
	public ReadOnlyPair<K, V> deepClone() {
		return this.deepCloneReadOnly();
	}

	@Override
	public MutablePair<K, V> deepCloneMutable() {
		return new MutablePairImpl<>(DeepCloneable.deepClone(this.key), DeepCloneable.deepClone(this.value));
	}

	@Override
	public ReadOnlyPair<K, V> deepCloneReadOnly() {
		return new ReadOnlyPairImpl<>(DeepCloneable.deepClone(this.key), DeepCloneable.deepClone(this.value));
	}

	@Override
	public final int elementCount() {
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
		final Pair<?, ?> other = (Pair<?, ?>) obj;
		return Objects.equals(this.key, other.getKey()) && Objects.equals(this.value, other.getValue());
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T get(final int i) {
		if (i < 0 || i > 1) {
			throw new IndexOutOfBoundsException("Index: " + i + " out of range: [0, 1]");
		}
		return i == 0 ? (T) this.key : (T) this.value;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.key, this.value);
	}

	@Override
	public boolean hasKey() {
		return this.key != null;
	}

	@Override
	public boolean hasValue() {
		return this.value != null;
	}

	@Override
	public <T> T map(final BiFunction<K, V, T> func) {
		return func.apply(this.key, this.value);
	}

	@Override
	public <T, N> ReadOnlyPair<T, N> map(final BiFunction<K, V, T> funcKey, final BiFunction<K, V, N> funcValue) {
		return this.map((k, v) -> new ReadOnlyPairImpl<>(funcKey.apply(k, v), funcValue.apply(k, v)));
	}

	@Override
	public <T> ReadOnlyPair<T, V> mapKey(final BiFunction<K, V, T> func) {
		return this.map((k, v) -> new ReadOnlyPairImpl<>(func.apply(k, v), v));
	}

	@Override
	public <T> ReadOnlyPair<K, T> mapValue(final BiFunction<K, V, T> func) {
		return this.map((k, v) -> new ReadOnlyPairImpl<>(k, func.apply(k, v)));
	}

	@Override
	public <T> ReadOnlyPair<?, ?> map(final int i, final Function<Object, T> func) {
		switch (i) {
		case 0:
			return new ReadOnlyPairImpl<>(func.apply(this.key), this.value);
		case 1:
			return new ReadOnlyPairImpl<>(this.key, func.apply(this.value));
		default:
			throw new IndexOutOfBoundsException("Index: " + i + " out of range: [0, 1]");
		}
	}

	@Override
	public String toString() {
		return String.format("{%s, %s}(readonly)", this.key, this.value);
	}

}
