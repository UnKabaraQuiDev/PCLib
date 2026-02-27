package lu.kbra.pclib.datastructure.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

public class ListHashMap<K, V> extends HashMap<K, List<V>> {

	private Supplier<List<V>> listSupplier = ArrayList<V>::new;

	public ListHashMap() {
	}

	public ListHashMap(Supplier<List<V>> listSupplier) {
		this.listSupplier = listSupplier;
	}

	public boolean add(K key, V obj) {
		super.computeIfAbsent(key, this::supplyList);

		return super.get(key).add(obj);
	}

	public List<V> supplyList(K k) {
		return listSupplier.get();
	}

	public List<V> supplyList() {
		return listSupplier.get();
	}

	public void setListSupplier(Supplier<List<V>> listSupplier) {
		this.listSupplier = listSupplier;
	}

}
