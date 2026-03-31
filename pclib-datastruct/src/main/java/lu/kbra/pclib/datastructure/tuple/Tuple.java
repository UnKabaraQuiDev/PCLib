package lu.kbra.pclib.datastructure.tuple;

import java.lang.reflect.Array;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

public interface Tuple {

	int elementCount();

	<T> T get(int i);

	Object[] asArray();

	default <T> T[] asArray(T[] arr) {
		if (arr.length < this.elementCount()) {
			arr = (T[]) Array.newInstance(arr.getClass().getComponentType(), this.elementCount());
		}
		for (int i = 0; i < this.elementCount(); i++) {
			arr[i] = (T) this.get(i);
		}
		return arr;
	}

	default <T> T[] asArray(final Supplier<T[]> arr) {
		return this.asArray(arr.get());
	}

	default <T> T[] asArray(final Function<Integer, T[]> arr) {
		return this.asArray(arr.apply(this.elementCount()));
	}

	default <T> T[] asArray(final IntFunction<T[]> arr) {
		return this.asArray(arr.apply(this.elementCount()));
	}

}
