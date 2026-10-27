package lu.kbra.pclib.datastructure.tuple;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class ReadOnlyPairWeakView<K, V> implements Pair<K, V>, WeakTupleView {

	private final WeakReference<Pair<K, V>> parent;

	public ReadOnlyPairWeakView(final Pair<K, V> parent) {
		this.parent = new WeakReference<>(Objects.requireNonNull(parent, "parent"));
	}

	@Override
	public WeakReference<? extends Tuple> getParentReference() {
		return this.parent;
	}

	@Override
	public Pair<K, V> getParent() {
		final Pair<K, V> value = this.parent.get();
		if (value == null) {
			throw new IllegalStateException("The tuple parent has been garbage-collected");
		}
		return value;
	}

	@Override
	public Object[] asArray() {
		return this.getParent().asArray();
	}

	@Override
	public Pair<K, V> clone() {
		return this.getParent().clone();
	}

	@Override
	public MutablePair<K, V> cloneMutable() {
		return this.getParent().cloneMutable();
	}

	@Override
	public ReadOnlyPair<K, V> cloneReadOnly() {
		return this.getParent().cloneReadOnly();
	}

	@Override
	public Pair<K, V> deepClone() {
		return this.getParent().deepClone();
	}

	@Override
	public MutablePair<K, V> deepCloneMutable() {
		return this.getParent().deepCloneMutable();
	}

	@Override
	public ReadOnlyPair<K, V> deepCloneReadOnly() {
		return this.getParent().deepCloneReadOnly();
	}

	@Override
	public int elementCount() {
		return this.getParent().elementCount();
	}

	@Override
	public <T> T get(final int i) {
		return this.getParent().get(i);
	}

	@Override
	public boolean hasKey() {
		return this.getParent().hasKey();
	}

	@Override
	public boolean hasValue() {
		return this.getParent().hasValue();
	}

	@Override
	public <T> T map(final BiFunction<K, V, T> func) {
		return this.getParent().map(func);
	}

	@Override
	public <T, N> Pair<T, N> map(final BiFunction<K, V, T> funcKey, final BiFunction<K, V, N> funcValue) {
		return this.getParent().map(funcKey, funcValue);
	}

	@Override
	public <T> Pair<T, V> mapKey(final BiFunction<K, V, T> func) {
		return this.getParent().mapKey(func);
	}

	@Override
	public <T> Pair<K, T> mapValue(final BiFunction<K, V, T> func) {
		return this.getParent().mapValue(func);
	}

	@Override
	public <T> Pair<?, ?> map(final int i, final Function<Object, T> func) {
		return this.getParent().map(i, func);
	}

	@Override
	public boolean isView() {
		return true;
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public Pair<K, V> asViewReadOnly() {
		return new ReadOnlyPairView<>(this);
	}

	@Override
	public Pair<K, V> asWeakViewReadOnly() {
		return new ReadOnlyPairWeakView<>(this);
	}

	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		return other instanceof TupleView && this.getParent().equals(((TupleView) other).getParent());
	}

	@Override
	public int hashCode() {
		return this.getParent().hashCode();
	}

	@Override
	public String toString() {
		return this.getParent().toString();
	}

	@Override
	public K getKey() {
		return this.getParent().getKey();
	}

	@Override
	public V getValue() {
		return this.getParent().getValue();
	}

}
