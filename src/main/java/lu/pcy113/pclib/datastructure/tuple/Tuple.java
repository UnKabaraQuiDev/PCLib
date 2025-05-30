package lu.pcy113.pclib.datastructure.tuple;

import java.util.function.Supplier;

public interface Tuple {

	int elementCount();

	<T> T get(int i);

	Object[] asArray();

	<T> T[] asArray(T[] arr);

	<T> T[] asArray(Supplier<T[]> arr);

}
