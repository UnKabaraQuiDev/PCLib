package lu.kbra.pclib.datastructure.tuple;

import java.util.function.Function;

public interface MutableTuple extends Tuple {

	@Override
	default boolean isMutable() {
		return true;
	}

	default boolean isSynchronized() {
		return false;
	}

	<T> MutableTuple set(int index, T element);

	<T> MutableTuple mut(final int index, final Function<Object, T> func);

	MutableTuple asViewMutable();

	MutableTuple asSynchronized();

	MutableTuple asWeakViewMutable();

}
