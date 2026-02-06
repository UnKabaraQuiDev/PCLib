package lu.kbra.pclib.cache;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class CacheList<K, V, X extends CacheEntry<V>> {

	protected final Map<K, X> cache;

	public CacheList() {
		this.cache = new ConcurrentHashMap<>();
	}

	protected abstract boolean isInvalid(X entry);

	public V getOrPut(K a, Supplier<V> supplier) {
		return containsKey(a) ? get(a) : put(a, supplier.get());
	}

	public V getOrPut(K a, Function<K, V> supplier) {
		return containsKey(a) ? get(a) : put(a, supplier.apply(a));
	}

	public V getOrPut(K a, V value) {
		return containsKey(a) ? get(a) : put(a, value);
	}

	public V get(K key) {
		X entry = cache.get(key);
		if (isInvalid(entry)) {
			cache.remove(key);
			return null;
		}
		return entry.getValue();
	}

	public void remove(K key) {
		cache.remove(key);
	}

	public void clear() {
		cache.clear();
	}

	protected abstract X createEntry(V value);

	public CacheEntry<V> compute(K key, BiFunction<? super K, V, V> func) {
		return cache.compute(key, (a, b) -> createEntry(func.apply(a, b.getValue())));
	}

	public CacheEntry<V> computeIfAbsent(K key, Function<? super K, V> func) {
		return cache.computeIfAbsent(key, (a) -> createEntry(func.apply(a)));
	}

	public CacheEntry<V> computeIfPresent(K key, BiFunction<? super K, V, V> func) {
		return cache.computeIfPresent(key, (a, b) -> createEntry(func.apply(a, b.getValue())));
	}

	public boolean containsKey(K key) {
		return cache.containsKey(key);
	}

	public boolean containsValue(X value) {
		return cache.containsValue(value);
	}

	public Set<Entry<K, X>> entrySet() {
		return cache.entrySet();
	}

	public void forEachEntry(BiConsumer<? super K, X> consumer) {
		cache.forEach(consumer);
	}

	public void forEach(BiConsumer<? super K, V> consumer) {
		cache.forEach((k, v) -> consumer.accept(k, v.getValue()));
	}

	public Set<K> keySet() {
		return cache.keySet();
	}

	public void putAll(Map<K, X> other) {
		cache.putAll(other);
	}

	public void putAll(CacheList<K, V, X> other) {
		cache.putAll(other.cache);
	}

	public X putIfAbsent(K key, X entry) {
		return cache.putIfAbsent(key, entry);
	}

	public int size() {
		return cache.size();
	}

	public Collection<X> values() {
		return cache.values();
	}

	public V put(K key, V value) {
		cache.put(key, createEntry(value));
		return value;
	}

}
