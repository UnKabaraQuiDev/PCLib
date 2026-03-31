package lu.kbra.pclib.datastructure.set;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

public class WeakHashSet<E> extends AbstractSet<E> implements WeakSet<E> {

	private static final Object PRESENT = new Object();

	private final Map<E, Object> map = new WeakHashMap<>();

	@Override
	public boolean add(final E e) {
		return this.map.put(e, WeakHashSet.PRESENT) == null;
	}

	@Override
	public boolean remove(final Object o) {
		return this.map.remove(o) != null;
	}

	@Override
	public boolean contains(final Object o) {
		return this.map.containsKey(o);
	}

	@Override
	public Iterator<E> iterator() {
		return this.map.keySet().iterator();
	}

	@Override
	public int size() {
		return this.map.size();
	}

	@Override
	public void clear() {
		this.map.clear();
	}

	@Override
	public String toString() {
		return this.map.keySet().toString();
	}

}
