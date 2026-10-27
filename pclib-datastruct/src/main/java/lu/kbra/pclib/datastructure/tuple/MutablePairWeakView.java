package lu.kbra.pclib.datastructure.tuple;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

import lu.kbra.pclib.impl.function.ThrowingFunction;
import lu.kbra.pclib.impl.supplier.ThrowingSupplier;

public final class MutablePairWeakView<K, V> implements MutablePair<K, V>, WeakTupleView {

	private final WeakReference<MutablePair<K, V>> parent;

	public MutablePairWeakView(final MutablePair<K, V> parent) {
		this.parent = new WeakReference<>(Objects.requireNonNull(parent, "parent"));
	}

	@Override
	public WeakReference<? extends Tuple> getParentReference() {
		return this.parent;
	}

	@Override
	public MutablePair<K, V> getParent() {
		final MutablePair<K, V> value = this.parent.get();
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
	public MutablePair<K, V> deepClone() {
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
	public <T, N> MutablePair<T, N> map(final BiFunction<K, V, T> funcKey, final BiFunction<K, V, N> funcValue) {
		return this.getParent().map(funcKey, funcValue);
	}

	@Override
	public <T> MutablePair<T, V> mapKey(final BiFunction<K, V, T> func) {
		return this.getParent().mapKey(func);
	}

	@Override
	public <T> MutablePair<K, T> mapValue(final BiFunction<K, V, T> func) {
		return this.getParent().mapValue(func);
	}

	@Override
	public <T> MutablePair<?, ?> map(final int index, final Function<Object, T> func) {
		return this.getParent().map(index, func);
	}

	@Override
	public MutablePair<K, V> mut(final BiFunction<K, V, K> funcKey, final BiFunction<K, V, V> funcValue) {
		return this.getParent().mut(funcKey, funcValue);
	}

	@Override
	public MutablePair<K, V> mutKey(final BiFunction<K, V, K> func) {
		return this.getParent().mutKey(func);
	}

	@Override
	public MutablePair<K, V> mutValue(final BiFunction<K, V, V> func) {
		return this.getParent().mutValue(func);
	}

	@Override
	public <T> MutablePair<K, V> mut(final int index, final Function<Object, T> func) {
		return this.getParent().mut(index, func);
	}

	@Override
	public Pair<K, V> setKey(final K key) {
		return this.getParent().setKey(key);
	}

	@Override
	public Pair<K, V> setValue(final V value) {
		return this.getParent().setValue(value);
	}

	@Override
	public <T> MutablePair<K, V> set(final int index, final T element) {
		return this.getParent().set(index, element);
	}

	@Override
	public K getKey() {
		return this.getParent().getKey();
	}

	@Override
	public V getValue() {
		return this.getParent().getValue();
	}

	@Override
	public <T, P extends Throwable> T[] asArray(final ThrowingFunction<Integer, T[], P> factory) throws P {
		return this.getParent().asArray(factory);
	}

	@Override
	public <T> T[] asArray(final Function<Integer, T[]> factory) {
		return this.getParent().asArray(factory);
	}

	@Override
	public <T> T[] asArray(final IntFunction<T[]> factory) {
		return this.getParent().asArray(factory);
	}

	@Override
	public <T, P extends Throwable> T[] asArray(final ThrowingSupplier<T[], P> factory) throws P {
		return this.getParent().asArray(factory);
	}

	@Override
	public <T> T[] asArray(final Supplier<T[]> factory) {
		return this.getParent().asArray(factory);
	}

	@Override
	public <T> T[] asArray(final T[] destination) {
		return this.getParent().asArray(destination);
	}

	@Override
	public boolean isView() {
		return true;
	}

	@Override
	public boolean isSynchronized() {
		return this.getParent().isSynchronized();
	}

	@Override
	public MutablePair<K, V> asSynchronized() {
		return this.getParent().asSynchronized();
	}

	@Override
	public Pair<K, V> asViewReadOnly() {
		return new ReadOnlyPairView<>(this);
	}

	@Override
	public MutablePair<K, V> asViewMutable() {
		return new MutablePairView<>(this);
	}

	@Override
	public Pair<K, V> asWeakViewReadOnly() {
		return new ReadOnlyPairWeakView<>(this);
	}

	@Override
	public MutablePair<K, V> asWeakViewMutable() {
		return new MutablePairWeakView<>(this);
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

}
