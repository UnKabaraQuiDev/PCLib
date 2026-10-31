package lu.kbra.pclib.datastructure.tuple;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

import lu.kbra.pclib.impl.function.ThrowingFunction;
import lu.kbra.pclib.impl.supplier.ThrowingSupplier;

public class MutablePairSyncImpl<K, V> extends MutablePairImpl<K, V> {

	public MutablePairSyncImpl() {
	}

	public MutablePairSyncImpl(final K key, final V value) {
		super(key, value);
	}

	@Override
	public synchronized Object[] asArray() {
		return super.asArray();
	}

	@Override
	public synchronized Pair<K, V> clone() {
		return super.clone();
	}

	@Override
	public synchronized MutablePair<K, V> cloneMutable() {
		return super.cloneMutable();
	}

	@Override
	public synchronized ReadOnlyPair<K, V> cloneReadOnly() {
		return super.cloneReadOnly();
	}

	@Override
	public synchronized MutablePair<K, V> deepClone() {
		return super.deepClone();
	}

	@Override
	public synchronized MutablePair<K, V> deepCloneMutable() {
		return super.deepCloneMutable();
	}

	@Override
	public synchronized ReadOnlyPair<K, V> deepCloneReadOnly() {
		return super.deepCloneReadOnly();
	}

	@Override
	public synchronized int elementCount() {
		return super.elementCount();
	}

	@Override
	public synchronized boolean equals(final Object obj) {
		return super.equals(obj);
	}

	@Override
	public synchronized <T> T get(final int i) {
		return super.get(i);
	}

	@Override
	public synchronized int hashCode() {
		return super.hashCode();
	}

	@Override
	public synchronized boolean hasKey() {
		return super.hasKey();
	}

	@Override
	public synchronized boolean hasValue() {
		return super.hasValue();
	}

	@Override
	public synchronized <T> T map(final BiFunction<K, V, T> func) {
		return super.map(func);
	}

	@Override
	public synchronized <T, N> MutablePair<T, N> map(final BiFunction<K, V, T> funcKey, final BiFunction<K, V, N> funcValue) {
		return super.map(funcKey, funcValue);
	}

	@Override
	public synchronized <T> MutablePair<T, V> mapKey(final BiFunction<K, V, T> func) {
		return super.mapKey(func);
	}

	@Override
	public synchronized <T> MutablePair<K, T> mapValue(final BiFunction<K, V, T> func) {
		return super.mapValue(func);
	}

	@Override
	public synchronized <T> MutablePair<?, ?> map(final int index, final Function<Object, T> func) {
		return super.map(index, func);
	}

	@Override
	public synchronized MutablePair<K, V> mut(final BiFunction<K, V, K> funcKey, final BiFunction<K, V, V> funcValue) {
		return super.mut(funcKey, funcValue);
	}

	@Override
	public synchronized MutablePair<K, V> mutKey(final BiFunction<K, V, K> func) {
		return super.mutKey(func);
	}

	@Override
	public synchronized MutablePair<K, V> mutValue(final BiFunction<K, V, V> func) {
		return super.mutValue(func);
	}

	@Override
	public synchronized <T> MutablePair<K, V> mut(final int index, final Function<Object, T> func) {
		return super.mut(index, func);
	}

	@Override
	public synchronized Pair<K, V> setKey(final K key) {
		return super.setKey(key);
	}

	@Override
	public synchronized Pair<K, V> setValue(final V value) {
		return super.setValue(value);
	}

	@Override
	public synchronized String toString() {
		return super.toString();
	}

	@Override
	public synchronized <T> MutablePair<K, V> set(final int index, final T element) {
		return super.set(index, element);
	}

	@Override
	public synchronized K getKey() {
		return super.getKey();
	}

	@Override
	public synchronized V getValue() {
		return super.getValue();
	}

	@Override
	public synchronized <T, P extends Throwable> T[] asArray(final ThrowingFunction<Integer, T[], P> factory) throws P {
		return super.asArray(factory);
	}

	@Override
	public synchronized <T> T[] asArray(final Function<Integer, T[]> factory) {
		return super.asArray(factory);
	}

	@Override
	public synchronized <T> T[] asArray(final IntFunction<T[]> factory) {
		return super.asArray(factory);
	}

	@Override
	public synchronized <T, P extends Throwable> T[] asArray(final ThrowingSupplier<T[], P> factory) throws P {
		return super.asArray(factory);
	}

	@Override
	public synchronized <T> T[] asArray(final Supplier<T[]> factory) {
		return super.asArray(factory);
	}

	@Override
	public synchronized <T> T[] asArray(final T[] destination) {
		return super.asArray(destination);
	}

	@Override
	public synchronized boolean isMutable() {
		return true;
	}

	@Override
	public synchronized boolean isView() {
		return false;
	}

	@Override
	public synchronized Pair<K, V> asViewReadOnly() {
		return super.asViewReadOnly();
	}

	@Override
	public synchronized MutablePair<K, V> asViewMutable() {
		return super.asViewMutable();
	}

	@Override
	public synchronized MutablePair<K, V> asSynchronized() {
		return this;
	}

	@Override
	public synchronized boolean isSynchronized() {
		return true;
	}
}
