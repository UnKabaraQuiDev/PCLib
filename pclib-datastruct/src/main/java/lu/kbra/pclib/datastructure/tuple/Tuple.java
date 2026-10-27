package lu.kbra.pclib.datastructure.tuple;

import java.lang.reflect.Array;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

import lu.kbra.pclib.datastructure.DeepCloneable;
import lu.kbra.pclib.impl.function.ThrowingFunction;
import lu.kbra.pclib.impl.supplier.ThrowingSupplier;

public interface Tuple extends Cloneable, DeepCloneable {

	Object[] asArray();

	default <T, P extends Throwable> T[] asArray(final ThrowingFunction<Integer, T[], P> arr) throws P {
		return this.asArray(arr.apply(this.elementCount()));
	}

	default <T> T[] asArray(final Function<Integer, T[]> arr) {
		return this.asArray(arr.apply(this.elementCount()));
	}

	default <T> T[] asArray(final IntFunction<T[]> arr) {
		return this.asArray(arr.apply(this.elementCount()));
	}

	default <T, P extends Throwable> T[] asArray(final ThrowingSupplier<T[], P> arr) throws P {
		return this.asArray(arr.get());
	}

	default <T> T[] asArray(final Supplier<T[]> arr) {
		return this.asArray(arr.get());
	}

	@SuppressWarnings("unchecked")
	default <T> T[] asArray(T[] arr) {
		if (arr.length < this.elementCount()) {
			arr = (T[]) Array.newInstance(arr.getClass().getComponentType(), this.elementCount());
		}
		for (int i = 0; i < this.elementCount(); i++) {
			arr[i] = (T) this.get(i);
		}
		return arr;
	}

	int elementCount();

	@Override
	boolean equals(final Object obj);

	<T> T get(int i);

	<T> Tuple map(final int index, final Function<Object, T> func);

	boolean isMutable();

	default boolean isView() {
		return false;
	}

	@Override
	int hashCode();

	@Override
	Tuple clone();

	MutableTuple cloneMutable();

	ReadOnlyTuple cloneReadOnly();

	@Override
	Tuple deepClone();

	MutableTuple deepCloneMutable();

	ReadOnlyTuple deepCloneReadOnly();

	Tuple asViewReadOnly();

	Tuple asWeakViewReadOnly();

}
