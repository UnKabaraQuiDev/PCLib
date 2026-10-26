package lu.kbra.pclib.datastructure.map;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.function.Supplier;

import lu.kbra.pclib.datastructure.DeepCloneable;

public class MultiHashMap<K, V> extends HashMap<K, Collection<V>> implements Cloneable, DeepCloneable {

	private static final long serialVersionUID = -2587722830616394492L;

	private Supplier<Collection<V>> collectionSupplier = ArrayList<V>::new;

	public MultiHashMap() {
	}

	public MultiHashMap(final Supplier<Collection<V>> collectionSupplier) {
		this.collectionSupplier = collectionSupplier;
	}

	public boolean add(final K key, final V obj) {
		super.computeIfAbsent(key, this::supplyCollection);

		return super.get(key).add(obj);
	}

	@Override
	@SuppressWarnings("unchecked")
	public MultiHashMap<K, V> clone() {
		return (MultiHashMap<K, V>) super.clone();
	}

	@Override
	@SuppressWarnings("unchecked")
	public MultiHashMap<K, V> deepClone() {
		final MultiHashMap<K, V> clone = new MultiHashMap<>(this.collectionSupplier);

		for (final Entry<K, Collection<V>> entry : this.entrySet()) {
			final Collection<V> original = entry.getValue();

			if (original == null) {
				clone.put(entry.getKey(), null);
				continue;
			}

			final Collection<V> copied = clone.supplyCollection();

			for (final V value : original) {
				if (value == null) {
					copied.add(null);
					continue;
				}

				try {
					if (value instanceof DeepCloneable) {
						copied.add((V) ((DeepCloneable) value).deepClone());
					} else if (value instanceof Cloneable) {
						final Method cloneMethod = value.getClass().getMethod("clone");
						copied.add((V) cloneMethod.invoke(value));
					} else {
						copied.add(value);
					}
				} catch (InvocationTargetException | NoSuchMethodException | SecurityException | IllegalAccessException
						| IllegalArgumentException e) {
					throw new RuntimeException("Clone not supported on object: " + value.getClass());
				}
			}

			clone.put(entry.getKey(), copied);
		}

		return clone;
	}

	public void setcollectionSupplier(final Supplier<Collection<V>> collectionSupplier) {
		this.collectionSupplier = collectionSupplier;
	}

	public Collection<V> supplyCollection() {
		return this.collectionSupplier.get();
	}

	public Collection<V> supplyCollection(final K k) {
		return this.collectionSupplier.get();
	}

}
