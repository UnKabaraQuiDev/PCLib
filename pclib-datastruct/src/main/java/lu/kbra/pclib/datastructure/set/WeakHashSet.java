package lu.kbra.pclib.datastructure.set;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

public class WeakHashSet<E> extends AbstractSet<E> implements WeakSet<E> {

	private static final Object PRESENT = new Object();

	private final Map<E, Object> map = new WeakHashMap<>();

	@Override
	public boolean add(E e) {
		return map.put(e, PRESENT) == null;
	}

	@Override
	public boolean remove(Object o) {
		return map.remove(o) != null;
	}

	@Override
	public boolean contains(Object o) {
		return map.containsKey(o);
	}

	@Override
	public Iterator<E> iterator() {
		return map.keySet().iterator();
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public String toString() {
		return map.keySet().toString();
	}

}