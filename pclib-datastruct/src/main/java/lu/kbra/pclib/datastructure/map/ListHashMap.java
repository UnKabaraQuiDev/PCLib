package lu.kbra.pclib.datastructure.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

public class ListHashMap<K, V> extends HashMap<K, List<V>> {

	private static final long serialVersionUID = -2587722830616394492L;
	private Supplier<List<V>> listSupplier = ArrayList<V>::new;

	public ListHashMap() {
	}

	public ListHashMap(final Supplier<List<V>> listSupplier) {
		this.listSupplier = listSupplier;
	}

	public boolean add(final K key, final V obj) {
		super.computeIfAbsent(key, this::supplyList);

		return super.get(key).add(obj);
	}

	public List<V> supplyList(final K k) {
		return this.listSupplier.get();
	}

	public List<V> supplyList() {
		return this.listSupplier.get();
	}

	public void setListSupplier(final Supplier<List<V>> listSupplier) {
		this.listSupplier = listSupplier;
	}

}
