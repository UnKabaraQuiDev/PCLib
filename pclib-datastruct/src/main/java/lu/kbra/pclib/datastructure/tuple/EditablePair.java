package lu.kbra.pclib.datastructure.tuple;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EditablePair<K, V> implements Pair<K, V>, EditableTuple {

	protected K key;
	protected V value;

	@Override
	public Object[] asArray() {
		return new Object[] { this.key, this.value };
	}

	@Override
	public Pair<K, V> clone() {
		return new EditablePair<>(this.key, this.value);
	}

	@Override
	public EditablePair<K, V> cloneEditable() {
		return new EditablePair<>(this.key, this.value);
	}

	@Override
	public ReadOnlyPair<K, V> cloneReadOnly() {
		return new ReadOnlyPair<>(this.key, this.value);
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

	@SuppressWarnings("unchecked")
	@Override
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
	public <T, N> EditablePair<T, N> map(final BiFunction<K, V, T> funcKey, final BiFunction<K, V, N> funcValue) {
		return this.map((k, v) -> new EditablePair<>(funcKey.apply(k, v), funcValue.apply(k, v)));
	}

	@Override
	public <T> EditablePair<T, V> mapKey(final BiFunction<K, V, T> func) {
		return this.map((k, v) -> new EditablePair<>(func.apply(k, v), v));
	}

	@Override
	public <T> EditablePair<K, T> mapValue(final BiFunction<K, V, T> func) {
		return this.map((k, v) -> new EditablePair<>(k, func.apply(k, v)));
	}

	@Override
	public <T> EditablePair<?, ?> map(final int i, final Function<Object, T> func) {
		final EditablePair<?, ?> ret = new EditablePair<>(this.key, this.value);
		ret.set(i, func.apply(ret.get(i)));
		return ret;
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
		return String.format("{%s, %s}", this.key, this.value);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> EditablePair<K, V> set(final int index, final T element) {
		switch (index) {
		case 0:
			this.key = (K) element;
		case 1:
			this.value = (V) element;
		default:
			throw new IndexOutOfBoundsException("Index: " + index + " out of range: [0, 1]");
		}
	}

}
