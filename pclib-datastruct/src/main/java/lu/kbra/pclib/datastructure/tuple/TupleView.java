package lu.kbra.pclib.datastructure.tuple;

public interface TupleView extends Tuple {

	@Override
	default boolean isView() {
		return true;
	}

	Tuple getParent();

}
