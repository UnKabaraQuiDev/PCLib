package lu.kbra.pclib.datastructure.tuple;

public interface ReadOnlyTuple extends Tuple {

	@Override
	default boolean isEditable() {
		return false;
	}

}
