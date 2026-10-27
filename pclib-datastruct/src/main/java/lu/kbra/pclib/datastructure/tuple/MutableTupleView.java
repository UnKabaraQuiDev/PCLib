package lu.kbra.pclib.datastructure.tuple;

public interface MutableTupleView extends MutableTuple, TupleView {

	@Override
	default boolean isView() {
		return true;
	}

	@Override
	MutableTuple getParent();

	@Override
	default MutableTuple asViewMutable() {
		return this.getParent().asViewMutable();
	}

	@Override
	default Tuple asViewReadOnly() {
		return this.getParent().asViewReadOnly();
	}

}
