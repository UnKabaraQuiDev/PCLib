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

	public V getOrPut(final K a, final Supplier<V> supplier) {
		return this.containsKey(a) ? this.get(a) : this.put(a, supplier.get());
	}

	public V getOrPut(final K a, final Function<K, V> supplier) {
		return this.containsKey(a) ? this.get(a) : this.put(a, supplier.apply(a));
	}

	public V getOrPut(final K a, final V value) {
		return this.containsKey(a) ? this.get(a) : this.put(a, value);
	}

	public V get(final K key) {
		final X entry = this.cache.get(key);
		if (this.isInvalid(entry)) {
			this.cache.remove(key);
			return null;
		}
		return entry.getValue();
	}

	public void remove(final K key) {
		this.cache.remove(key);
	}

	public void clear() {
		this.cache.clear();
	}

	protected abstract X createEntry(V value);

	public CacheEntry<V> compute(final K key, final BiFunction<? super K, V, V> func) {
		return this.cache.compute(key, (a, b) -> this.createEntry(func.apply(a, b.getValue())));
	}

	public CacheEntry<V> computeIfAbsent(final K key, final Function<? super K, V> func) {
		return this.cache.computeIfAbsent(key, a -> this.createEntry(func.apply(a)));
	}

	public CacheEntry<V> computeIfPresent(final K key, final BiFunction<? super K, V, V> func) {
		return this.cache.computeIfPresent(key, (a, b) -> this.createEntry(func.apply(a, b.getValue())));
	}

	public boolean containsKey(final K key) {
		return this.cache.containsKey(key);
	}

	public boolean containsValue(final X value) {
		return this.cache.containsValue(value);
	}

	public Set<Entry<K, X>> entrySet() {
		return this.cache.entrySet();
	}

	public void forEachEntry(final BiConsumer<? super K, X> consumer) {
		this.cache.forEach(consumer);
	}

	public void forEach(final BiConsumer<? super K, V> consumer) {
		this.cache.forEach((k, v) -> consumer.accept(k, v.getValue()));
	}

	public Set<K> keySet() {
		return this.cache.keySet();
	}

	public void putAll(final Map<K, X> other) {
		this.cache.putAll(other);
	}

	public void putAll(final CacheList<K, V, X> other) {
		this.cache.putAll(other.cache);
	}

	public X putIfAbsent(final K key, final X entry) {
		return this.cache.putIfAbsent(key, entry);
	}

	public int size() {
		return this.cache.size();
	}

	public Collection<X> values() {
		return this.cache.values();
	}

	public V put(final K key, final V value) {
		this.cache.put(key, this.createEntry(value));
		return value;
	}

}
