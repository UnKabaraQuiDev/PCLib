package lu.pcy113.pclib.datastructure.tuple;

import java.lang.reflect.Array;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

public interface Tuple {

	int elementCount();

	<T> T get(int i);

	Object[] asArray();

	default <T> T[] asArray(T[] arr) {
		if (arr.length < elementCount()) {
			arr = (T[]) Array.newInstance(arr.getClass().getComponentType(), elementCount());
		}
		for (int i = 0; i < elementCount(); i++) {
			arr[i] = (T) get(i);
		}
		return arr;
	}

	default <T> T[] asArray(Supplier<T[]> arr) {
		return asArray(arr.get());
	}

	default <T> T[] asArray(Function<Integer, T[]> arr) {
		return asArray(arr.apply(elementCount()));
	}

	default <T> T[] asArray(IntFunction<T[]> arr) {
		return asArray(arr.apply(elementCount()));
	}

}
