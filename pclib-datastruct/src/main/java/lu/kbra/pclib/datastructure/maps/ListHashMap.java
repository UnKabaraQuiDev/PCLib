package lu.kbra.pclib.datastructure.maps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

public class ListHashMap<T1, T2> extends HashMap<T1, List<T2>> {

	private Supplier<List<T2>> listSupplier = ArrayList<T2>::new;

	public ListHashMap() {
	}

	public ListHashMap(Supplier<List<T2>> listSupplier) {
		this.listSupplier = listSupplier;
	}

	public boolean add(T1 key, T2 obj) {
		super.putIfAbsent(key, supplyList());

		return super.get(key).add(obj);
	}

	public List<T2> supplyList() {
		return listSupplier.get();
	}

	public void setListSupplier(Supplier<List<T2>> listSupplier) {
		this.listSupplier = listSupplier;
	}

}
