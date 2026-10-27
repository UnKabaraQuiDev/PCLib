package lu.kbra.pclib.datastructure.tuple;

import java.lang.ref.WeakReference;

public interface WeakTupleView extends TupleView {

	WeakReference<? extends Tuple> getParentReference();

	default boolean isParentAvailable() {
		return this.getParentReference() != null && this.getParentReference().get() != null;
	}

}
