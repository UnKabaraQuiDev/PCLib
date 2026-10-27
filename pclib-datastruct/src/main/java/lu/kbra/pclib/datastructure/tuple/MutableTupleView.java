package lu.kbra.pclib.datastructure.tuple;

public interface MutableTupleView extends MutableTuple, TupleView {

	@Override
	default boolean isView() {
		return true;
	}

	@Override
	MutableTuple getParent();

	@Override
	default MutableTuple viewMutable() {
		return this.getParent().viewMutable();
	}

	@Override
	default Tuple viewReadOnly() {
		return this.getParent().viewReadOnly();
	}

}
