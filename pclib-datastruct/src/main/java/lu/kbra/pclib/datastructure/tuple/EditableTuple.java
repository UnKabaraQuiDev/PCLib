package lu.kbra.pclib.datastructure.tuple;

import java.util.function.Function;

public interface EditableTuple extends Tuple {

	@Override
	default boolean isEditable() {
		return true;
	}

	<T> EditableTuple set(int index, T element);

	<T> EditableTuple mut(final int index, final Function<Object, T> func);

}
