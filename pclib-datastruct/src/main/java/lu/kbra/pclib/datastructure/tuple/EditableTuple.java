package lu.kbra.pclib.datastructure.tuple;

public interface EditableTuple extends Tuple {

	@Override
	default boolean isEditable() {
		return true;
	}

	<T> EditableTuple set(int index, T element);

}
